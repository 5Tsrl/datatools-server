package com.conveyal.datatools.manager.auth;

import com.conveyal.datatools.manager.DataManager;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by demory on 1/18/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth0UserProfile {
    String email;
    String user_id;
    AppMetadata app_metadata;

    public Auth0UserProfile() {
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setApp_metadata(AppMetadata app_metadata) {
        this.app_metadata = app_metadata;
    }

    public AppMetadata getApp_metadata() { return app_metadata; }

    @JsonIgnore
    public void setDatatoolsInfo(DatatoolsInfo datatoolsInfo) {
        this.app_metadata.getDatatoolsInfo().setClientId(datatoolsInfo.clientId);
        this.app_metadata.getDatatoolsInfo().setPermissions(datatoolsInfo.permissions);
        this.app_metadata.getDatatoolsInfo().setProjects(datatoolsInfo.projects);
        this.app_metadata.getDatatoolsInfo().setSubscriptions(datatoolsInfo.subscriptions);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AppMetadata {
        ObjectMapper mapper = new ObjectMapper();
        @JsonProperty("datatools")
        List<DatatoolsInfo> datatools;

        public AppMetadata() {
        }

        @JsonIgnore
        public void setDatatoolsInfo(DatatoolsInfo datatools) {
            for(int i = 0; i < this.datatools.size(); i++) {
                if (this.datatools.get(i).clientId.equals(DataManager.getConfigPropertyAsText("AUTH0_CLIENT_ID"))) {
                    this.datatools.set(i, datatools);
                }
            }
        }
        @JsonIgnore
        public DatatoolsInfo getDatatoolsInfo() {
            for(int i = 0; i < this.datatools.size(); i++) {
                DatatoolsInfo dt = this.datatools.get(i);
                if (dt.clientId.equals(DataManager.getConfigPropertyAsText("AUTH0_CLIENT_ID"))) {
                    return dt;
                }
            }
            return null;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DatatoolsInfo {
        @JsonProperty("client_id")
        String clientId;
        Organization[] organizations;
        Project[] projects;
        Permission[] permissions;
        Subscription[] subscriptions;

        public DatatoolsInfo() {
        }

        public DatatoolsInfo(String clientId, Project[] projects, Permission[] permissions, Organization[] organizations, Subscription[] subscriptions) {
            this.clientId = clientId;
            this.projects = projects;
            this.permissions = permissions;
            this.organizations = organizations;
            this.subscriptions = subscriptions;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public void setProjects(Project[] projects) {
            this.projects = projects;
        }
        public void setOrganizations(Organization[] organizations) {
            this.organizations = organizations;
        }
        public void setPermissions(Permission[] permissions) {
            this.permissions = permissions;
        }

        public void setSubscriptions(Subscription[] subscriptions) {
            this.subscriptions = subscriptions;
        }

        public Subscription[] getSubscriptions() { return subscriptions == null ? new Subscription[0] : subscriptions; }

    }


    public static class Project {

        String project_id;
        Permission[] permissions;
        String[] defaultFeeds;

        public Project() {
        }

        public Project(String project_id, Permission[] permissions, String[] defaultFeeds) {
            this.project_id = project_id;
            this.permissions = permissions;
            this.defaultFeeds = defaultFeeds;
        }

        public void setProject_id(String project_id) {
            this.project_id = project_id;
        }

        public void setPermissions(Permission[] permissions) { this.permissions = permissions; }

        public void setDefaultFeeds(String[] defaultFeeds) {
            this.defaultFeeds = defaultFeeds;
        }

    }

    public static class Permission {

        String type;
        String[] feeds;

        public Permission() {
        }

        public Permission(String type, String[] feeds) {
            this.type = type;
            this.feeds = feeds;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setFeeds(String[] feeds) {
            this.feeds = feeds;
        }
    }
    public static class Organization {
        @JsonProperty("organization_id")
        String organizationId;
        Permission[] permissions;
//        String name;
//        UsageTier usageTier;
//        Extension[] extensions;
//        Date subscriptionDate;
//        String logoUrl;

        public Organization() {
        }

        public Organization(String organizationId, Permission[] permissions) {
            this.organizationId = organizationId;
            this.permissions = permissions;
        }

        public void setOrganizationId(String organizationId) {
            this.organizationId = organizationId;
        }

        public void setPermissions(Permission[] permissions) {
            this.permissions = permissions;
        }
    }
    public static class Subscription {

        String type;
        String[] target;

        public Subscription() {
        }

        public Subscription(String type, String[] target) {
            this.type = type;
            this.target = target;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() { return type; }

        public void setTarget(String[] target) {
            this.target = target;
        }

        public String[] getTarget() { return target; }
    }

    public int getProjectCount() {
        return app_metadata.getDatatoolsInfo().projects.length;
    }

    public boolean hasProject(String projectID, String organizationId) {
        if (canAdministerApplication()) return true;
        if (canAdministerOrganization(organizationId)) return true;
        if(app_metadata.getDatatoolsInfo() == null || app_metadata.getDatatoolsInfo().projects == null) return false;
        for(Project project : app_metadata.getDatatoolsInfo().projects) {
            if (project.project_id.equals(projectID)) return true;
        }
        return false;
    }

    public boolean canAdministerApplication() {
        if(app_metadata.getDatatoolsInfo() != null && app_metadata.getDatatoolsInfo().permissions != null) {
            for(Permission permission : app_metadata.getDatatoolsInfo().permissions) {
                if(permission.type.equals("administer-application")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canAdministerOrganization() {
        if(app_metadata.getDatatoolsInfo() != null && app_metadata.getDatatoolsInfo().organizations != null) {
            Organization org = app_metadata.getDatatoolsInfo().organizations[0];
            for(Permission permission : org.permissions) {
                if(permission.type.equals("administer-organization")) {
                    return true;
                }
            }
        }
        return false;
    }

    public Organization getAuth0Organization() {
        if(app_metadata.getDatatoolsInfo() != null && app_metadata.getDatatoolsInfo().organizations != null && app_metadata.getDatatoolsInfo().organizations.length != 0) {
            return app_metadata.getDatatoolsInfo().organizations[0];
        }
        return null;
    }

    public String getOrganizationId() {
        Organization org = getAuth0Organization();
        if (org != null) {
            return org.organizationId;
        }
        return null;
    }

    public boolean canAdministerOrganization(String organizationId) {
//      TODO: adapt for specific org
        Organization org = getAuth0Organization();
        if (org != null && org.organizationId.equals(organizationId)) {
            for(Permission permission : org.permissions) {
                if(permission.type.equals("administer-organization")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canAdministerProject(String projectID, String organizationId) {
        if(canAdministerApplication()) return true;
        if(canAdministerOrganization(organizationId)) return true;
        for(Project project : app_metadata.getDatatoolsInfo().projects) {
            if (project.project_id.equals(projectID)) {
                for(Permission permission : project.permissions) {
                    if(permission.type.equals("administer-project")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canViewFeed(String organizationId, String projectID, String feedID) {
        if (canAdministerApplication() || canAdministerProject(projectID, organizationId)) {
            return true;
        }
        for(Project project : app_metadata.getDatatoolsInfo().projects) {
            if (project.project_id.equals(projectID)) {
                return checkFeedPermission(project, feedID, "view-feed");
            }
        }
        return false;
    }

    public boolean canManageFeed(String organizationId, String projectID, String feedID) {
        if (canAdministerApplication() || canAdministerProject(projectID, organizationId)) {
            return true;
        }
        Project[] projectList = app_metadata.getDatatoolsInfo().projects;
        for(Project project : projectList) {
            System.out.println("project_id: " + project.project_id);
            if (project.project_id.equals(projectID)) {
                return checkFeedPermission(project, feedID, "manage-feed");
            }
        }
        return false;
    }

    public boolean canEditGTFS(String organizationId, String projectID, String feedID) {
        if (canAdministerApplication() || canAdministerProject(projectID, organizationId)) {
            return true;
        }
        Project[] projectList = app_metadata.getDatatoolsInfo().projects;
        for(Project project : projectList) {
            System.out.println("project_id: " + project.project_id);
            if (project.project_id.equals(projectID)) {
                return checkFeedPermission(project, feedID, "edit-gtfs");
            }
        }
        return false;
    }

    public boolean canApproveGTFS(String organizationId, String projectID, String feedID) {
        if (canAdministerApplication() || canAdministerProject(projectID, organizationId)) {
            return true;
        }
        Project[] projectList = app_metadata.getDatatoolsInfo().projects;
        for(Project project : projectList) {
            System.out.println("project_id: " + project.project_id);
            if (project.project_id.equals(projectID)) {
                return checkFeedPermission(project, feedID, "approve-gtfs");
            }
        }
        return false;
    }

    public boolean checkFeedPermission(Project project, String feedID, String permissionType) {
        String feeds[] = project.defaultFeeds;

        // check for permission-specific feeds
        for (Permission permission : project.permissions) {
            if(permission.type.equals(permissionType)) {
                if(permission.feeds != null) {
                    feeds = permission.feeds;
                }
            }
        }

        for(String thisFeedID : feeds) {
            if (thisFeedID.equals(feedID) || thisFeedID.equals("*")) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public com.conveyal.datatools.manager.models.Organization getOrganization () {
        Organization[] orgs = getApp_metadata().getDatatoolsInfo().organizations;
        if (orgs != null && orgs.length != 0) {
            return orgs[0] != null ? com.conveyal.datatools.manager.models.Organization.get(orgs[0].organizationId) : null;
        }
        return null;
    }
}
