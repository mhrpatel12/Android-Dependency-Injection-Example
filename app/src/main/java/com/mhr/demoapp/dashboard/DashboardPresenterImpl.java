package com.mhr.demoapp.dashboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mhr.demoapp.R;
import com.mhr.demoapp.dashboard.adapter.UserHistoryAdapter;
import com.mhr.demoapp.dashboard.model.UserHistory;
import com.mhr.demoapp.data.DatabaseService;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Mihir on 05/09/2017.
 */

public class DashboardPresenterImpl implements DashboardPresenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Facebook/Google Login";

    private DashboardView mainView;
    private DatabaseService databaseService;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private Activity activity;
    private LocationManager manager;

    // [END declare_auth]
    public FirebaseAuth mAuth;
    // [START declare_auth]

    private RecyclerView recyclerViewCommits;
    private ArrayList<UserHistory> userHistoryArrayList = new ArrayList<>();
    private UserHistoryAdapter userHistoryAdapter;

    @Inject
    public DashboardPresenterImpl(final Activity activity, DashboardView loginView, DatabaseService databaseService) {
        this.mainView = loginView;
        this.databaseService = databaseService;
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
    }

    @Override
    public void loadDashboard() {
        showProgress(activity.getString(R.string.fetching_history));
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        recyclerViewCommits = (RecyclerView) activity.findViewById(R.id.recycler_view_login_history);
        recyclerViewCommits.setLayoutManager(new LinearLayoutManager(activity));

        userHistoryAdapter = new UserHistoryAdapter(userHistoryArrayList, R.layout.list_item_user_history, activity);
        recyclerViewCommits.setAdapter(userHistoryAdapter);

        if (mAuth.getCurrentUser() != null) {
            databaseService.mDatabase.child(activity.getString(R.string.table_users_history)).
                    child(mAuth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                userHistoryArrayList.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    UserHistory userHistory = ds.getValue(UserHistory.class);
                                    userHistoryArrayList.add(userHistory);
                                }
                                userHistoryAdapter.notifyDataSetChanged();
                            }
                            hideProgress();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage((FragmentActivity) activity, this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
        mainView.onDashboardLoaded();
    }

    @Override
    public void logTimeAndLocation(String dateTime, LatLng latLng) {
        databaseService.logDateAndTime(activity,
                mAuth.getCurrentUser().getUid(),
                mAuth.getCurrentUser().getEmail(),
                dateTime,
                latLng.longitude + "",
                latLng.longitude + "");
    }

    @Override
    public void showProgress(String dialogMessage) {
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(dialogMessage);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void logout() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        mainView.onSignedOut();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
