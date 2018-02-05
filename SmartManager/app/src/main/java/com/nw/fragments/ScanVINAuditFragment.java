package com.nw.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.LogClient;
import com.nw.model.Parameter;
import com.nw.model.ScanVIN;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.activity.VehicleActivity;
import com.smartmanager.android.R;
import com.sonyericsson.util.AlbumStorageDirFactory;
import com.sonyericsson.util.BaseAlbumDirFactory;
import com.sonyericsson.util.FroyoAlbumDirFactory;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScanVINAuditFragment extends BaseFragement
{
    Location location;
    CustomTextViewLight tvVinMake, tvVinModel, tvVinColor, tvVinRegistration, tvVinEngine, tvVinNumber, tvVinLicense,
            tvVinExpires, tvVinGeoLocation, tvCountDown;
    ScanVIN scanVIN;
    CustomButton btnSubmit;
    private long timeElapsed;
    ImageView ivAddImage, ivFirstImage, ivSecondImage;
    private final long startTime = 3 * 60 * 1000; // countdown for 3 mins
    private final long interval = 1000;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    int imageCount = 0;
    private static final int PICK_FROM_CAMERA = 1888;
    public static final int PICK_MULTIPLE_FILE_CROP = 2002;
    Uri fileUri, mImageCaptureUri;
    private String mCurrentPhotoPath, address, base64Vehicle, base64License;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    ImageLoader loader;
    private DisplayImageOptions options;
    boolean isFirstSelceted = false;
    Location locationUser;
    ImageCountDownTimer countDownTimer;
    LogClient logClient;
    boolean gps_enabled = false, network_enabled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_scanvin_audit, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        turnGPSOn();
        locationUser = getBestLastKnownLocation();
        try
        {
            if (locationUser != null)
            {
                address = Helper.getAddress(getActivity(), locationUser.getLatitude(), locationUser.getLongitude());
            } else
            {
                LocationManager lm = null;
                boolean gps_enabled = false, network_enabled = false;
                if (lm == null)
                    lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                try
                {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex)
                {
                }
                try
                {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex)
                {
                }

                if (!gps_enabled && !network_enabled)
                {
                    CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.location_service_disable_message), new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            if (type == Dialog.BUTTON_POSITIVE)
                            {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                            } else
                            {
                                tvVinGeoLocation.setVisibility(View.VISIBLE);
                                tvVinGeoLocation.setText(getString(R.string.location_not_found));
                            }
                        }
                    });
                } else
                {
                    Helper.showToast("Getting location..", getActivity());
                    if (locationUser != null)
                    {
                        address = Helper.getAddress(getActivity(), locationUser.getLatitude(), locationUser.getLongitude());
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (getArguments() != null)
        {
            scanVIN = getArguments().getParcelable("scannedVIN");
        }
        loader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)
                .showImageOnFail(R.drawable.noimage)
                .showImageForEmptyUri(R.drawable.noimage)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        createDirectory();
        setHasOptionsMenu(true);
        initialise(view);

        setDetails(scanVIN);
        return view;
    }

    private void initialise(View view)
    {
        tvVinMake = (CustomTextViewLight) view.findViewById(R.id.tvVinMake);
        tvVinColor = (CustomTextViewLight) view.findViewById(R.id.tvVinColor);
        tvVinEngine = (CustomTextViewLight) view.findViewById(R.id.tvVinEngine);
        tvVinExpires = (CustomTextViewLight) view.findViewById(R.id.tvVinExpires);
        tvVinNumber = (CustomTextViewLight) view.findViewById(R.id.tvVinNumber);
        tvVinLicense = (CustomTextViewLight) view.findViewById(R.id.tvVinLicense);
        tvVinGeoLocation = (CustomTextViewLight) view.findViewById(R.id.tvVinGeoLocation);
        tvVinRegistration = (CustomTextViewLight) view.findViewById(R.id.tvVinRegistration);
        tvVinModel = (CustomTextViewLight) view.findViewById(R.id.tvVinModel);
        tvCountDown = (CustomTextViewLight) view.findViewById(R.id.tvCountDown);
        ivAddImage = (ImageView) view.findViewById(R.id.ivAddImage);
        ivFirstImage = (ImageView) view.findViewById(R.id.ivFirstImage);
        ivSecondImage = (ImageView) view.findViewById(R.id.ivSecondImage);
        ivAddImage.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                File f;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try
                {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    fileUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                } catch (IOException e)
                {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                } catch (Exception e)
                {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
            }
        });
        btnSubmit = (CustomButton) view.findViewById(R.id.btnVINSubmit);
        btnSubmit.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (base64License != null && base64Vehicle != null)
                {
                    countDownTimer.cancel();
                    saveVINAudit();
                } else
                {
                    CustomDialogManager.showOkDialog(getContext(), "Please add 2 images, 1 of the front of the " + "vehicle and 1 of the license disc to save this audit");
                }

            }
        });
    }

    private void setDetails(ScanVIN scanVIN)
    {
        tvVinMake.setText(scanVIN.getMake());
        tvVinColor.setText(scanVIN.getColour());
        tvVinModel.setText(scanVIN.getModel());
        tvVinExpires.setText(scanVIN.getDate());
        tvVinLicense.setText(scanVIN.getLicence());
        tvVinRegistration.setText(scanVIN.getRegistration());
        tvVinEngine.setText(scanVIN.getEngineNumber());
        tvVinNumber.setText(scanVIN.getVIN());
        tvVinGeoLocation.setText(address);
        countDownTimer = new ImageCountDownTimer(startTime, interval);
        countDownTimer.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                hideKeyboard();
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Vehicle Audit");
        //getActivity().getActionBar().setSubtitle(null);
        location = ((VehicleActivity) getActivity()).getUserLocation();
        if (location != null)
            doGeoTag();
        else
        {
            LocationManager lm = null;

            if (lm == null)
                lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            try
            {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex)
            {
            }
            try
            {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex)
            {
            }

            if (!gps_enabled && !network_enabled)
            {
                CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.location_service_disable_message), new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        if (type == Dialog.BUTTON_POSITIVE)
                        {
                            Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        } else
                        {
                            tvVinGeoLocation.setVisibility(View.VISIBLE);
                            tvVinGeoLocation.setText(getString(R.string.location_not_found));
                        }
                    }
                });
            } else
            {
                Helper.showToast("Getting location..", getActivity());
                location = ((VehicleActivity) getActivity()).getUserLocation();
                if (location != null)
                    doGeoTag();
            }
        }
    }

    public class ImageCountDownTimer extends CountDownTimer
    {
        public ImageCountDownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }

        @Override
        public void onFinish()
        {
            if (getActivity() != null)
            {
                // CustomDialogManager.cancleDialog();
                getActivity().getFragmentManager().popBackStack();
            }
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            DecimalFormat decimalFormat = new DecimalFormat("00");
            Helper.Log("Time remain", "Time remain:" + millisUntilFinished);
            Helper.Log("Time Elapsed: ", "Time Elapsed: " + String.valueOf(timeElapsed));
            int days = (int) ((millisUntilFinished / 1000) / 86400);
            int hours = (int) (((millisUntilFinished / 1000) - (days * 86400)) / 3600);
            int minutes = (int) (((millisUntilFinished / 1000) - ((days * 86400) + (hours * 3600))) / 60);
            int seconds = (int) ((millisUntilFinished / 1000) % 60);
            tvCountDown.setText("Time remaining: " + decimalFormat.format(minutes) + ":" + decimalFormat.format(seconds));
            timeElapsed = startTime - millisUntilFinished;
        }
    }

    private File setUpPhotoFile() throws IOException
    {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "" + JPEG_FILE_SUFFIX;
        File albumF = getAlbumDir();
        //File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);

        File imageF = new File(albumF, imageFileName);
        return imageF;
    }


    private File getAlbumDir()
    {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            DataManager.getInstance().albumDirectory = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName()).getPath();
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName() + "/Camera");

            if (storageDir != null)
            {
                //DataManager.getInstance().albumDirectory=storageDir.getPath();
                if (!storageDir.mkdirs())
                {
                    if (!storageDir.exists())
                    {
                        Helper.Log("AlbumDir", "Failed to create Album directory");
                        Helper.showToast("Failed to create directory", getActivity());
                        return null;
                    }
                }
            }
        } else
        {
            Log.v(getActivity().getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
            Helper.showToast("External storage is not mounted READ/WRITE.", getActivity());
        }
        return storageDir;
    }

    private void createDirectory()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
        {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else
        {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    /* Photo album for this application */
    private String getAlbumName()
    {
        return getActivity().getString(R.string.app_name);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }

        switch (requestCode)
        {
            case PICK_FROM_CAMERA:

                if (!isFirstSelceted)
                {
                    loadImage(ivFirstImage, "file:///" + mCurrentPhotoPath);
                    isFirstSelceted = true;
                    base64Vehicle = Helper.convertResizedBitmapToBase64(mCurrentPhotoPath);
                } else
                {
                    loader.displayImage("file:///" + mCurrentPhotoPath, ivSecondImage, options, new ImageLoadingListener()
                    {

                        @Override
                        public void onLoadingStarted(String arg0, View arg1)
                        {

                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
                        {


                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2)
                        {
                            ivAddImage.setOnClickListener(null);

                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1)
                        {

                        }
                    });
                    base64License = Helper.convertResizedBitmapToBase64(mCurrentPhotoPath);
                }
                break;

        }

    }

    private void loadImage(ImageView imageView, String path)
    {
        loader.displayImage(path, imageView, options);
    }

    @SuppressWarnings("unused")
    private Location getBestLastKnownLocation()
    {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation_byGps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location lastKnownLocation_byNetwork = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // Network Last Location not available
        if (lastKnownLocation_byGps == null && lastKnownLocation_byNetwork != null)
        {
            return lastKnownLocation_byNetwork;
        }// GPS Last Location not available
        else if (lastKnownLocation_byGps != null && lastKnownLocation_byNetwork == null)
        {
            return lastKnownLocation_byGps;
        } // Both Location provider have last location
        else if (lastKnownLocation_byGps != null && lastKnownLocation_byNetwork != null)
        {

            if (lastKnownLocation_byGps.getAccuracy() <= lastKnownLocation_byNetwork.getAccuracy())
            {
                return lastKnownLocation_byGps;
            } else
            {
                return lastKnownLocation_byNetwork;
            }
        } else
        {
            Criteria c = new Criteria();
            c.setAccuracy(Criteria.ACCURACY_FINE);
            Location l = lm.getLastKnownLocation(lm.getBestProvider(c, false));
            if (l != null)
            {
                return l;
            }
            return null;
        }
    }

    @SuppressWarnings("unused")
    private void turnGPSOn()
    {
        @SuppressWarnings("deprecation")
        String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps"))
        { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getActivity().sendBroadcast(poke);
        }
    }

    private void doGeoTag()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            final StringBuilder soapBuilder = new StringBuilder();
            soapBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\"><Body><DoGeoTag xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapBuilder.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            /*soapBuilder.append("<googleLAtitude>-29.8369444</googleLAtitude>");
            soapBuilder.append("<googleLongitude>30.914722</googleLongitude>");*/
            soapBuilder.append("<googleLAtitude>" + location.getLatitude() + "</googleLAtitude>");
            soapBuilder.append("<googleLongitude>" + location.getLongitude() + "</googleLongitude>");
            soapBuilder.append("</DoGeoTag></Body></Envelope>");
            Helper.Log("doGeoTag request:", soapBuilder.toString());


            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    Helper.Log("Error: ", error.getMessage());
                }

                @Override
                public void onResponse(String response)
                {

                    if (response != null)
                    {
                        Helper.Log("Response:", "" + response);
                        logClient = ParserManager.parsedoGet(response);

                        if (!TextUtils.isEmpty(logClient.getAddress()))
                        {
                            tvVinGeoLocation.setVisibility(View.VISIBLE);
                            tvVinGeoLocation.setText("" + logClient.getAddress());
                        /*	logClient.setLongitude("-29.8369444");
                            logClient.setLatetude("30.914722");*/
                            logClient.setLongitude("" + location.getLongitude());
                            logClient.setLatetude("" + location.getLatitude());
                        }

                    }
                    hideProgressDialog();
                }
            };


            VollyCustomRequest request = new VollyCustomRequest(Constants.PLANNER_WEBSERVICE_URL, soapBuilder.toString(), Constants.TEMP_URI_NAMESPACE + "IPlannerService/DoGeoTag", vollyResponseListener);
            try
            {
                request.init("getBlogType");
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void saveVINAudit()
    {

        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), String.class));
            parameterList.add(new Parameter("vin", scanVIN.getVIN(), String.class));
            parameterList.add(new Parameter("registration", scanVIN.getRegistration(), String.class));
            parameterList.add(new Parameter("make", scanVIN.getMake(), String.class));
            parameterList.add(new Parameter("model", scanVIN.getModel(), String.class));
            parameterList.add(new Parameter("colour", scanVIN.getColour(), String.class));
            parameterList.add(new Parameter("engineNo", scanVIN.getEngineNumber(), String.class));
            parameterList.add(new Parameter("googleLatitude", locationUser.getLatitude(), Double.class));
            parameterList.add(new Parameter("googleLongitude", locationUser.getLongitude(), Double.class));
            parameterList.add(new Parameter("base64License", base64License, String.class));
            parameterList.add(new Parameter("base64Vehicle", base64Vehicle, String.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("SaveAudit");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/SaveAudit");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    Helper.Log("Response", result.toString());
                    if (!result.equals(""))
                    {
                        CustomDialogManager.showOkDialog(getActivity(), "Audit saved successfully", new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        });
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }

    }
}
