package com.smartmanager.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.greysonparrelli.permiso.Permiso;
import com.nw.interfaces.DialogListener;
import com.nw.widget.CustomDialogManager;
import com.utils.Helper;

public class LoacationBaseActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener
{
	private LocationRequest mLocationRequest;
	private GoogleApiClient mGoogleApiClient;

	Location location;
	// Global constants
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 10 * 60;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 5 * 60;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	private int PERMISSION_CALL = 100;

	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		getLocation();

	}

	private void getLocation()
	{
		// create request object
		mLocationRequest = new LocationRequest();
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
	}

	public Location getUserLocation()
	{
		return this.location;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//    Permiso.getInstance().setActivity(this);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
       /* if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                @Override
                public void onPermissionResult(Permiso.ResultSet resultSet) {
                    if (resultSet.areAllPermissionsGranted()) {
                        getLocation();
                    }else {
                        CustomDialogManager.showOkCancelDialog(LoacationBaseActivity.this, "Please accept all permissions" +
                                " for proper functioning of app", "Ok", "Cancel", new DialogListener() {
                            @Override
                            public void onButtonClicked(int type) {
                                switch (type){
                                    case Dialog.BUTTON_NEGATIVE:
                                        LoacationBaseActivity.this.finish();
                                        break;
                                }
                            }
                        });
                    }
                }

                @Override
                public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                    Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                }
            }, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION);
        }else {
            if (mGoogleApiClient!=null){
            mGoogleApiClient.connect();
            }
        }*/
		//  getLocation();
		if (mGoogleApiClient != null)
		{
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		if (mGoogleApiClient != null)
		{
			if (mGoogleApiClient.isConnected())
			{
				mGoogleApiClient.disconnect();
			}
		}
	}


	@Override
	public void onConnected(Bundle bundle)
	{
		/*if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                @Override
                public void onPermissionResult(Permiso.ResultSet resultSet) {
                    if (resultSet.areAllPermissionsGranted()) {
                        getLocation();
                    }else {
                        CustomDialogManager.showOkCancelDialog(LoacationBaseActivity.this, "Please accept all permissions" +
                                " for proper functioning of app", "Ok", "Cancel", new DialogListener() {
                            @Override
                            public void onButtonClicked(int type) {
                                switch (type){
                                    case Dialog.BUTTON_NEGATIVE:
                                        LoacationBaseActivity.this.finish();
                                        break;
                                }
                            }
                        });
                    }
                }

                @Override
                public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                    Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                }
            }, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION);
        }else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }*/
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Helper.Log("Location activity", "GoogleApiClient connection has been suspend");
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null)
			this.location=location;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Helper.Log("Test", ""+connectionResult.toString());
	}
}

