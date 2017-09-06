package com.mhr.demoapp.data;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mhr.demoapp.R;
import com.mhr.demoapp.dashboard.model.UserHistory;
import com.mhr.demoapp.login.model.User;

import javax.inject.Inject;

/**
 * Created by Mihir on 26/05/2017.
 */

public class DatabaseService {

    public DatabaseReference mDatabase;

    @Inject
    public DatabaseService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void writeNewUser(Activity mActivity, String userId, String name, String email) {
        User user = new User(userId, name, email);
        mDatabase.child(mActivity.getString(R.string.table_users)).child(userId).setValue(user);
    }

    public void logDateAndTime(Activity mActivity, String userID, String email, String loginDateTime, String loginlatitude, String loginlongitude) {
        UserHistory userHistory = new UserHistory(userID, email, loginDateTime, loginlatitude, loginlongitude);
        String key = mDatabase.child(mActivity.getString(R.string.table_users_history)).child(userID).push().getKey();
        mDatabase.child(mActivity.getString(R.string.table_users_history)).child(userID).child(key).setValue(userHistory);
    }
}
