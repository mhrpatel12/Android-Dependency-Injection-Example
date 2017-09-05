package com.mhr.demoapp.login.model;

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class User {

    public String userID;
    public String userName;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userID, String userName, String email) {
        this.userID = userID;
        this.userName = userName;
        this.email = email;
    }

}
// [END user_class]
