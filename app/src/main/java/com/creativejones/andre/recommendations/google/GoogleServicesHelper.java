package com.creativejones.andre.recommendations.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class GoogleServicesHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_AVAILABILITY = -100;
    private static final int REQUEST_CODE_RESOLUTION = -101;


    GoogleServicesListener mListener;
    Activity mActivity;
    GoogleApiClient mClient;

    public GoogleServicesHelper(Activity activity, GoogleServicesListener listener){
        mListener = listener;
        mActivity = activity;

        mClient = new GoogleApiClient.Builder(activity)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Plus.API, Plus.PlusOptions.builder()
                                            .setServerClientId("798656238650-r40fl6esh1uj21niehckfn85jtg6cgib.apps.googleusercontent.com")
                                            .build())
                        .build();
    }

    public void connect(){
        if(isGooglePlayServicesAvailable()) {
            mClient.connect();
        } else {
            mListener.onDisconnected();
        }
    }

    public void disconnect(){
        if(isGooglePlayServicesAvailable()) {
            mClient.disconnect();
        } else {
            mListener.onDisconnected();
        }
    }

    private boolean isGooglePlayServicesAvailable(){
       int availability = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);

        switch (availability){
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
                GooglePlayServicesUtil.getErrorDialog(availability, mActivity, REQUEST_CODE_AVAILABILITY).show();
                return false;
            default:
                return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mListener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mListener.onDisconnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult(mActivity, REQUEST_CODE_RESOLUTION);
            }catch (IntentSender.SendIntentException e){
                connect();
            }
        } else {
            mListener.onDisconnected();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE_RESOLUTION || requestCode == REQUEST_CODE_AVAILABILITY){
            if(resultCode == Activity.RESULT_OK){
                connect();
            } else {
                mListener.onDisconnected();
            }
        }
    }

    public interface GoogleServicesListener {
        void onConnected();
        void onDisconnected();
    }
}
