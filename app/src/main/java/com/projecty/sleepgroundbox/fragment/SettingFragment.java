package com.projecty.sleepgroundbox.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.activity.GoogleLogin;
import com.projecty.sleepgroundbox.model.UserProfile;


/**
 * Created by byungwoo on 15. 4. 5..
 */
public class SettingFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    TextView user_id;
    UserProfile user;
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        user_id = (TextView) view.findViewById(R.id.user_sandbox_id);
        TextView user_email = (TextView) view.findViewById(R.id.user_google_email);
        Button logout = (Button) view.findViewById(R.id.user_logout_button);
        user = UserProfile.getUser();

        user_id.setText(user.getUserName());
        user_email.setText(user.getUserEmail());

        logout.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope("https://www.googleapis.com/auth/youtube"))
                .addScope(new Scope("https://www.googleapis.com/auth/youtubepartner"))
                .addScope(Plus.SCOPE_PLUS_PROFILE).build();
        mGoogleApiClient.connect();
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mSignInClicked = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_logout_button:
                if(mSignInClicked){
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    alert_confirm.setMessage("로그아웃 하시겠습니까? 프로그램이 종료됩니다.").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    signOutFromGplus();
                                }
                            }).setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 'No'
                                    return;
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();

                }else{
                    Toast.makeText(getActivity(),
                            "구글서버에 연결할 수 없습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                }
                break;

        }

    }

    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
//            mGoogleApiClient.connect();
        }
        Intent intent = new Intent(getActivity(), GoogleLogin.class);
                            startActivity(intent);
                            getActivity().finish();

//        System.exit(0);
    }

//    private void revokeGplusAccess() {
//        if (mGoogleApiClient.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
//                    .setResultCallback(new ResultCallback<Status>() {
//                        @Override
//                        public void onResult(Status arg0) {
//                            mGoogleApiClient.connect();
//                            Intent intent = new Intent(getActivity(), GoogleLogin.class);
//                            startActivity(intent);
//                            getActivity().finish();
//                        }
//
//                    });
//        }
//    }
}
