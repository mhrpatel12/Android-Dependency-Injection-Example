package com.mhr.demoapp.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mhr.demoapp.R;
import com.mhr.demoapp.dashboard.DashboardActivity;
import com.mhr.demoapp.data.DatabaseService;

import javax.inject.Inject;

/**
 * Created by mertsimsek on 25/05/2017.
 */

public class LoginPresenterImpl implements LoginPresenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Facebook/Google Login";

    private LoginView mainView;
    private DatabaseService databaseService;
    private GoogleApiClient googleApiClient;
    private ProgressDialog progressDialog;
    private Activity activity;
    private CallbackManager mCallbackManager;
    private LocationManager manager;

    // [END declare_auth]
    private FirebaseAuth mAuth;
    // [START declare_auth]

    private LinearLayout layoutLogin;
    private LinearLayout layoutRegistration;

    private EditText edtEmailLogin;
    private EditText edtPasswordLogin;

    private EditText edtEmailRegistration;
    private EditText edtPasswordRegistration;
    private EditText edtConfirmPasswordRegistration;
    private EditText edtName;

    @Inject
    public LoginPresenterImpl(final Activity activity, LoginView loginView, DatabaseService databaseService) {
        this.mainView = loginView;
        this.databaseService = databaseService;
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    public void loadLogin() {
        layoutLogin = (LinearLayout) activity.findViewById(R.id.email_login_form);
        layoutRegistration = (LinearLayout) activity.findViewById(R.id.email_registration_form);

        edtEmailLogin = (EditText) activity.findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = (EditText) activity.findViewById(R.id.edtPasswordLogin);

        edtEmailRegistration = (EditText) activity.findViewById(R.id.edtEmailRegistration);
        edtPasswordRegistration = (EditText) activity.findViewById(R.id.edtPasswordRegistration);
        edtConfirmPasswordRegistration = (EditText) activity.findViewById(R.id.edtConfirmPasswordRegistration);
        edtName = (EditText) activity.findViewById(R.id.edtNameRegistration);

        mCallbackManager = CallbackManager.Factory.create();
        manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage((FragmentActivity) activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        googleApiClient.connect();
        mainView.onLoginLoaded(googleApiClient, mCallbackManager, mAuth);
    }

    private boolean isRegistrationFormComplete() {
        return (!(edtEmailRegistration.getText().toString().trim()).equals(""))
                && (!((edtName.getText().toString().trim()).equals("")))
                && (!((edtPasswordRegistration.getText().toString().trim()).equals("")))
                && (!(edtConfirmPasswordRegistration.getText().toString().trim()).equals(""));
    }

    private boolean isLoginFormComplete() {
        return (!(edtEmailLogin.getText().toString().trim()).equals(""))
                && (!((edtPasswordLogin.getText().toString().trim()).equals("")));
    }

    private boolean verifyPassword() {
        return ((edtPasswordRegistration.getText().toString().trim()).equals((edtConfirmPasswordRegistration.getText().toString().trim())));
    }

    @Override
    public void toggleViews() {
        if (layoutRegistration.getVisibility() == View.VISIBLE) {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutRegistration.setVisibility(View.GONE);
        } else if (layoutLogin.getVisibility() == View.VISIBLE) {
            layoutLogin.setVisibility(View.GONE);
            layoutRegistration.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initiateLogin() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            if (isLoginFormComplete()) {
                showProgress(activity.getString(R.string.message_dialog_signing_in));
                attemptLoginByEmailPassword(edtEmailLogin.getText().toString(), edtPasswordLogin.getText().toString());
            } else {
                Toast.makeText(activity, activity.getString(R.string.error_registration), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void initiateRegistration() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            if (isRegistrationFormComplete()) {
                if (verifyPassword()) {
                    showProgress(activity.getString(R.string.message_dialog_registering));
                    attemptRegistrationByEmailPassword(edtEmailRegistration.getText().toString(), edtPasswordRegistration.getText().toString());
                } else {
                    Toast.makeText(activity, activity.getString(R.string.error_password), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(activity, activity.getString(R.string.error_registration), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void attemptLoginByEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(activity, "Authentication success.",
                                    Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            startDashboardActivity(edtName.getText().toString());
                        } else {
                            // If sign in fails, display a message to the user.
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void attemptRegistrationByEmailPassword(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(activity, "Registration success.",
                                    Toast.LENGTH_LONG).show();
                            onEmailSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity, task.getException() + "",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // [START auth_with_facebook]
    public void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgress(activity.getString(R.string.authenticating));
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        }

                        // [START_EXCLUDE]
                        hideProgress();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    // [START auth_with_google]
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgress(activity.getString(R.string.authenticating));
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        }
                        // [START_EXCLUDE]
                        hideProgress();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    private void onAuthSuccess(FirebaseUser user) {
        databaseService.writeNewUser(activity, user.getUid(), user.getDisplayName(), user.getEmail());
        startDashboardActivity(user.getDisplayName());
    }

    private void onEmailSuccess(FirebaseUser user) {
        databaseService.writeNewUser(activity, user.getUid(), edtName.getText().toString().trim(), user.getEmail());
        startDashboardActivity(edtName.getText().toString().trim());
    }

    private void startDashboardActivity(String nameOfUser) {
        Intent intent = new Intent(activity, DashboardActivity.class);
        intent.putExtra(activity.getString(R.string.name_of_user), nameOfUser);
        activity.startActivity(intent);
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

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.prompt_location_on))
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
