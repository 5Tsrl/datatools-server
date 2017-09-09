package com.conveyal.datatools.manager.persistence;

import com.conveyal.datatools.manager.models.Model;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * This provides some abstraction over the Mongo Java driver for storing a particular kind of POJO.
 *
 * When performing an update (in our case with findOneAndUpdate) the Document of updates
 * may contain extra fields beyond those in the Java model class, or values of a type that
 * do not match the Java model class. The update will nonetheless add these extra fields
 * and wrong-typed values to MongoDB, which is not shocking considering its schemaless
 * nature. Of course a retrieved Java object will not contain these extra values
 * because it simply doesn't have a field to hold the values. If a value of the wrong
 * type has been stored in the database, deserialization will just fail with
 * "org.bson.codecs.configuration.CodecConfigurationException: Failed to decode X."
 *
 * This means clients have the potential to stuff any amount of garbage in our MongoDB
 * and trigger deserialization errors during application execution unless we perform
 * type checking and clean the incoming documents. There is probably a configuration
 * option to force schema adherence, which would prevent long-term compatibility but
 * would give us more safety in the short term.
 *
 * PojoCodecImpl does not seem to have any hooks to throw errors when unexpected fields
 * are encountered (see else clause of
 * org.bson.codecs.pojo.PojoCodecImpl#decodePropertyModel). We could make our own
 * function to imitate the PropertyModel checking and fail early when unexpected fields
 * are present in a document.
 */
public class TypedPersistence<T extends Model> {

    private static final Logger LOG = LoggerFactory.getLogger(TypedPersistence.class);

    MongoCollection<T> mongoCollection;
    private Constructor<T> noArgConstructor;

    public TypedPersistence(MongoDatabase mongoDatabase, Class<T> clazz) {
        mongoCollection = mongoDatabase.getCollection(clazz.getSimpleName(), clazz);
        try {
            noArgConstructor = clazz.getConstructor(new Class<?>[0]);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Could not get no-arg constructor for class " + clazz.getName(), ex);
        }
    }

    public T create (String updateJson) {
        T item = null;
        try {
            // Keeping our own reference to the constructor here is a little shady.
            // FIXME: We should try to use some Mongo codec method for this, e.g. inserting an empty document.
            item = noArgConstructor.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Could not use no-arg constructor to instantiate class.", ex);
        }
        mongoCollection.insertOne(item);
        return update(item.id, updateJson);
    }

    public T update (String id, String updateJson) {
        Document updateDocument = Document.parse(updateJson);
        return mongoCollection.findOneAndUpdate(eq(id), new Document("$set", updateDocument));
    }

    public T getById (String id) {
        return mongoCollection.find(eq(id)).first();
    }

    /**
     * This is not memory efficient.
     * TODO: Always use iterators / streams, always perform selection of subsets on the Mongo server side ("where clause").
     */
    public List<T> getAll () {
        return mongoCollection.find().into(new ArrayList<>());
    }

    public boolean removeById (String id) {
        DeleteResult result = mongoCollection.deleteOne(eq(id));
        if (result.getDeletedCount() == 1) {
            LOG.info("Deleted object id={} type={}", id, mongoCollection.getDocumentClass().getSimpleName());
            return true;
        } else if (result.getDeletedCount() > 1) {
            LOG.error("Deleted more than one object for ID {}", id);
        } else {
            LOG.error("Could not delete project: {}", id);
        }
        return false;
    }

}