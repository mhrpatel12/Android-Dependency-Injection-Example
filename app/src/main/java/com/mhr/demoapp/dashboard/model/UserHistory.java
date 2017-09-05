package com.mhr.demoapp.dashboard.model;

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class UserHistory {

    public String userID;
    public String email;
    public String loginDateTime;
    public String loginlatitude;
    public String loginlongitude;

    public UserHistory() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserHistory(String userID, String email, String loginDateTime, String loginlatitude, String loginlongitude) {
        this.userID = userID;
        this.email = email;
        this.loginDateTime = loginDateTime;
        this.loginlatitude = loginlatitude;
        this.loginlongitude = loginlongitude;
    }

}
// [END user_class]
