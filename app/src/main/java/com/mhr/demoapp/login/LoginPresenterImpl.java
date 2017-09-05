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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
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

import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by Mihir on 05/09/2017.
 */

public class LoginPresenterImpl implements LoginPresenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Facebook/Google Login";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
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
    private TextInputLayout layoutEmailLogin;
    private TextInputLayout layoutPasswordLogin;

    private EditText edtEmailRegistration;
    private EditText edtPasswordRegistration;
    private EditText edtConfirmPasswordRegistration;
    private EditText edtName;
    private TextInputLayout layoutEmailRegistration;
    private TextInputLayout layoutPasswordRegistration;
    private TextInputLayout layoutConfirmPasswordRegistration;
    private TextInputLayout layoutName;

    @Inject
    public LoginPresenterImpl(final Activity activity, LoginView loginView, DatabaseService databaseService) {
        this.mainView = loginView;
        this.databaseService = databaseService;
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
    }

    @Override
    public void loadLogin() {
        layoutLogin = (LinearLayout) activity.findViewById(R.id.email_login_form);
        layoutRegistration = (LinearLayout) activity.findViewById(R.id.email_registration_form);

        edtEmailLogin = (EditText) activity.findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = (EditText) activity.findViewById(R.id.edtPasswordLogin);
        layoutEmailLogin = (TextInputLayout) activity.findViewById(R.id.layoutEmailLogin);
        layoutPasswordLogin = (TextInputLayout) activity.findViewById(R.id.layoutPasswordLogin);

        edtEmailRegistration = (EditText) activity.findViewById(R.id.edtEmailRegistration);
        edtPasswordRegistration = (EditText) activity.findViewById(R.id.edtPasswordRegistration);
        edtConfirmPasswordRegistration = (EditText) activity.findViewById(R.id.edtConfirmPasswordRegistration);
        edtName = (EditText) activity.findViewById(R.id.edtNameRegistration);
        layoutEmailRegistration = (TextInputLayout) activity.findViewById(R.id.layoutEmailRegistration);
        layoutPasswordRegistration = (TextInputLayout) activity.findViewById(R.id.layoutPasswordRegistration);
        layoutConfirmPasswordRegistration = (TextInputLayout) activity.findViewById(R.id.layoutConfirmPasswordRegistration);
        layoutName = (TextInputLayout) activity.findViewById(R.id.layoutNameRegistration);

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
        boolean isRegistrationComplete = true;

        layoutEmailRegistration.setError(null);
        layoutPasswordRegistration.setError(null);
        layoutConfirmPasswordRegistration.setError(null);
        layoutName.setError(null);

        if ((edtEmailRegistration.getText().toString().trim()).equals("")) {
            isRegistrationComplete = false;
            layoutEmailRegistration.setError(activity.getString(R.string.error_mandatory));
        }
        if ((edtPasswordRegistration.getText().toString().trim()).equals("")) {
            isRegistrationComplete = false;
            layoutPasswordRegistration.setError(activity.getString(R.string.error_mandatory));
        }
        if ((edtConfirmPasswordRegistration.getText().toString().trim()).equals("")) {
            isRegistrationComplete = false;
            layoutConfirmPasswordRegistration.setError(activity.getString(R.string.error_mandatory));
        }
        if ((edtName.getText().toString().trim()).equals("")) {
            isRegistrationComplete = false;
            layoutName.setError(activity.getString(R.string.error_mandatory));
        }
        if (!(edtEmailRegistration.getText().toString().trim()).equals("") && !VALID_EMAIL_ADDRESS_REGEX.matcher(edtEmailRegistration.getText().toString().trim()).find()) {
            isRegistrationComplete = false;
            layoutEmailRegistration.setError(activity.getString(R.string.error_invalid_email));
        }

        return isRegistrationComplete;
    }

    private boolean isLoginFormComplete() {
        boolean isLoginComplete = true;
        layoutEmailLogin.setError(null);
        layoutPasswordLogin.setError(null);
        if ((edtEmailLogin.getText().toString().trim()).equals("")) {
            isLoginComplete = false;
            layoutEmailLogin.setError(activity.getString(R.string.error_mandatory));
        }
        if ((edtPasswordLogin.getText().toString().trim()).equals("")) {
            isLoginComplete = false;
            layoutPasswordLogin.setError(activity.getString(R.string.error_mandatory));
        }
        if (!(edtEmailLogin.getText().toString().trim()).equals("") && !VALID_EMAIL_ADDRESS_REGEX.matcher(edtEmailLogin.getText().toString().trim()).find()) {
            isLoginComplete = false;
            layoutEmailLogin.setError(activity.getString(R.string.error_invalid_email));
        }

        return isLoginComplete;
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
    public boolean isGPSEnables() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void initiateLogin() {
        if (isLoginFormComplete()) {
            showProgress(activity.getString(R.string.message_dialog_signing_in));
            attemptLoginByEmailPassword(edtEmailLogin.getText().toString(), edtPasswordLogin.getText().toString());
        }
    }

    @Override
    public void initiateRegistration() {
        if (isRegistrationFormComplete()) {
            if (verifyPassword()) {
                showProgress(activity.getString(R.string.message_dialog_registering));
                attemptRegistrationByEmailPassword(edtEmailRegistration.getText().toString(), edtPasswordRegistration.getText().toString());
            } else {
                Snackbar.make(layoutLogin, activity.getString(R.string.error_password), Snackbar.LENGTH_LONG).show();
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
                            startDashboardActivity(edtName.getText().toString());
                        } else {
                            // If sign in fails, display a message to the user.
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar.make(layoutLogin, task.getException() + "", Snackbar.LENGTH_LONG).show();
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
                            onEmailSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(layoutLogin, task.getException() + "", Snackbar.LENGTH_LONG).show();
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
                            Snackbar snackbar = Snackbar.make(layoutLogin, task.getException() + "", Snackbar.LENGTH_INDEFINITE);
                            ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setSingleLine(false);
                            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            snackbar.show();
                            LoginManager.getInstance().logOut();
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
                            Snackbar snackbar = Snackbar.make(layoutLogin, task.getException() + "", Snackbar.LENGTH_INDEFINITE);
                            ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setSingleLine(false);
                            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            snackbar.show();
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
    public void hideKeyboard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
