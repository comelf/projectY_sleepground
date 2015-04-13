package com.projecty.sleepgroundbox.model;

import com.projecty.sleepgroundbox.task.SandboxLoginAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by byungwoo on 15. 4. 9..
 */
public class UserProfile {
    private static UserProfile userProfile = new UserProfile();
    private String userName;
    private String userPhotoUrl;
    private String userGooglePlusProfile;
    private String userEmail;

    private static int userId =-1;
    private boolean isLogin=false;

    private UserProfile(){

    }

    public static UserProfile getUser(){
        return userProfile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserGooglePlusProfile() {
        return userGooglePlusProfile;
    }

    public void setUserGooglePlusProfile(String userGooglePlusProfile) {
        this.userGooglePlusProfile = userGooglePlusProfile;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void login() {

        new SandboxLoginAsyncTask() {
            @Override
            public void onPostExecute(JSONObject result) {
                handleResult(result);
            }
        }.execute(userName,userEmail, null);

    }

    public static int getStaticUserId(){
        return userId;
    }

    private void handleResult(JSONObject result) {
        if(result==null){
            return;
        }

        try {
            this.userId =  Integer.parseInt(result.getString("user_id"));
            this.isLogin = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
