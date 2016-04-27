package com.conveyal.datatools.editor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import com.conveyal.datatools.editor.utils.Auth0UserProfile;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;

import static spark.Spark.*;

public class Auth0Controller {
//    public static void auth0Login(String token) {
//        System.out.println("auth0Login token="+token);
//
//        // get user profile from Auth0 tokeninfo API
//
//        session.put("token", token);
//
//        try {
//            Auth0UserProfile profile = getUserInfo(token);
//            System.out.println("got userinfo for " + profile.getEmail());
//            session.put("username", profile.getEmail());
//
//            String projectID = Play.configuration.getProperty("application.projectId");
//
//            String editableFeeds = StringUtils.join(profile.getEditableFeeds(projectID), ",");
//            session.put("editableFeeds", editableFeeds);
//
//            String manageableFeeds = StringUtils.join(profile.getManageableFeeds(projectID), ",");
//            System.out.println("manageableFeeds = " + manageableFeeds);
//            session.put("manageableFeeds", manageableFeeds);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Error in user auth, redirecting to /auth0logout");
//            redirect("/auth0logout");
//        }
//
//        return true; // ok();
//    }
//
//    public static void auth0Logout(String token) {
//        System.out.println("logging out");
//        session.clear();
//        redirect("/");
//    }
//
//    protected static String getToken() {
//        String token = null;
//        final String authorizationHeader = request.params.get("authorization");
//        if (authorizationHeader == null) return null;
//
//        // check format (Authorization: Bearer [token])
//        String[] parts = authorizationHeader.split(" ");
//        if (parts.length != 2) return null;
//
//        String scheme = parts[0];
//        String credentials = parts[1];
//
//        Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
//        if (pattern.matcher(scheme).matches()) {
//            token = credentials;
//        }
//        return token;
//    }
//
//    protected static Auth0UserProfile getUserInfo(String token) throws Exception {
//
//        URL url = new URL("https://" + Play.configuration.getProperty("application.auth0Domain") + "/tokeninfo");
//        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
//
//        //add request header
//        con.setRequestMethod("POST");
//        con.setRequestProperty("User-Agent", "USER_AGENT");
//        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//
//        String urlParameters = "id_token=" + token;
//
//        // Send post request
//        con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(urlParameters);
//        wr.flush();
//        wr.close();
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        String userInfo = response.toString();
//
//        ObjectMapper m = new ObjectMapper();
//        return m.readValue(userInfo, Auth0UserProfile.class);
//
//
//    }
}
