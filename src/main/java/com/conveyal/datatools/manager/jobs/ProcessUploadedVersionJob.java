package com.conveyal.datatools.manager.jobs;

import com.conveyal.datatools.common.status.MonitorableJob;
import com.conveyal.datatools.editor.jobs.ProcessGtfsSnapshotMerge;
import com.conveyal.datatools.editor.models.Snapshot;
import com.conveyal.datatools.manager.DataManager;
import com.conveyal.datatools.manager.models.FeedVersion;
import com.conveyal.datatools.manager.persistence.Persistence;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Process (load, validate, take snapshot and build network) an uploaded GTFS version
 * @author 5t
 *
 */
public class ProcessUploadedVersionJob extends MonitorableJob {
    private FeedVersion feedVersion;
    private String owner;
    private static final Logger LOG = LoggerFactory.getLogger(ProcessUploadedVersionJob.class);

    /**
     * Create a job for the given feed version.
     */
    public ProcessUploadedVersionJob (FeedVersion feedVersion, String owner) {
        super(owner, "Processing GTFS for " + feedVersion.parentFeedSource().name, JobType.PROCESS_FEED);
        this.feedVersion = feedVersion;
        this.owner = owner;
        status.update(false,  "Processing...", 0);
        status.uploading = true;
    }

    /**
     * Getter that allows a client to know the ID of the feed version that will be created as soon as the upload is
     * initiated; however, we will not store the FeedVersion in the mongo application database until the upload and
     * processing is completed. This prevents clients from manipulating GTFS data before it is entirely imported.
     */
    @JsonProperty
    public String getFeedVersionId () {
        return feedVersion.id;
    }

    @JsonProperty
    public String getFeedSourceId () {
        return feedVersion.parentFeedSource().id;
    }

    @Override
    public void jobLogic () {
        LOG.info("Processing feed for {}", feedVersion.id);

        // First, load the feed into database.
        addNextJob(new LoadFeedJob(feedVersion, owner));

        // Next, validate the feed.
        addNextJob(new ValidateFeedJob(feedVersion, owner));

        // Use this FeedVersion to seed Editor DB .
        if(DataManager.isModuleEnabled("editor")) {
            // always create a snapshot to be linked to this version
            addNextJob(new ProcessGtfsSnapshotMerge(feedVersion, owner));
        }

        // chain on a network builder job, if applicable
        if(DataManager.isModuleEnabled("r5_network")) {
            addNextJob(new BuildTransportNetworkJob(feedVersion, owner));
        }
    }

    @Override
    public void jobFinished () {
        if (!status.error) {
            // Note: storing a new feed version in database is handled at completion of the ValidateFeedJob subtask.
            status.update(false, "New version saved.", 100, true);
        } else {
            // Processing did not complete. Depending on which sub-task this occurred in,
            // there may or may not have been a successful load/validation of the feed. For instance,
            // if an error occurred while building a TransportNetwork, the only impact is that there will
            // be no r5 network to use for generating isochrones.
            LOG.warn("Error processing version {} because error due to {}.", feedVersion.id, status.exceptionType);
        }
    }

}
