package com.projecty.sleepgroundbox.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.UserProfile;

public class GoogleLogin extends Activity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "GoogleLogin";
    StrictMode.ThreadPolicy policy
            = new StrictMode.ThreadPolicy.Builder().permitAll().build();



    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private Button btnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btnSignIn = (Button)findViewById(R.id.btn_sing_in);
        btnSignIn.setOnClickListener(this);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "NotoSans.otf");
        btnSignIn.setTypeface(custom_font);


    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope("https://www.googleapis.com/auth/youtube"))
                .addScope(new Scope("https://www.googleapis.com/auth/youtubepartner"))
                .addScope(Plus.SCOPE_PLUS_PROFILE).build();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
        if (!mIntentInProgress) {
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
        // Get user's information
        getProfileInformation();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {


            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                UserProfile user = UserProfile.getUser();
                user.setUserName(personName);
                user.setUserPhotoUrl(personPhotoUrl);
                user.setUserGooglePlusProfile(personGooglePlusProfile);
                user.setUserEmail(email);
                user.login();

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sing_in:
                signInWithGplus();
                break;
        }
    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }


    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {

    }
}