package com.conveyal.datatools.manager.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.mapdb.Fun.Tuple2;

import com.conveyal.datatools.editor.utils.JacksonSerializers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by raf
 */
public class Tuple2Codec implements Codec<Tuple2> {
    @Override
    public void encode(final BsonWriter writer, final Tuple2 value, final EncoderContext encoderContext) {
        //writer.writeString(value.toString());
    	writer.writeString(JacksonSerializers.Tuple2IntSerializer.serialize(value));
    }

    @Override
    public Tuple2 decode(final BsonReader reader, final DecoderContext decoderContext) {
    	try {
        	return JacksonSerializers.Tuple2IntDeserializer.deserialize(reader.readString());
            //return new Tuple2(reader.readString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<Tuple2> getEncoderClass() {
        return Tuple2.class;
    }
}
