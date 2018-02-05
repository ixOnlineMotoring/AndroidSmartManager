package com.smartmanager.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.greysonparrelli.permiso.Permiso;
import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nw.broadcast.NetworkUtil;
import com.nw.database.SMDatabase;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.service.ServiceImage;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.onesignal.OneSignal;
import com.smartmanager.android.R;
import com.utils.AppSession;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends FragmentActivity
{

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    @SuppressWarnings("unused")
    private View mLoginStatusView;
    private View mJoinsView;
    private TextView mLoginStatusMessageView, mContactView, mContactPhoneView;
    Dialog dialog;
    Button btnJoinNow, btnInfoJoinView;
    ImageView ivCompanyLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Permiso.getInstance().setActivity(this);
        overridePendingTransition(0, 0);
        getActionBar().hide();
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        mLoginFormView = findViewById(R.id.login_form);
        if (AppSession.isLoginAvailabe(this) && getIntent().hasExtra("fromsplash"))
        {
            mLoginStatusMessageView.setText(R.string.authenticating);
            if (!isFinishing())
            {
                dialog = CustomDialogManager.showProgressDialog(LoginActivity.this, getString(R.string.authenticating));
            }
            Helper.hidekeybord(mLoginFormView);
            String user[] = AppSession.getLogin(this);
            mLoginFormView.setVisibility(View.INVISIBLE);
            validateUser(user[0], user[1]);
        } else
        {
            findViews();
            initImageLoader();

            if (!TextUtils.isEmpty(AppSession.getClientImage(this)))
                if (AppSession.getClientID(this) != 1)
                {
                    ivCompanyLogo.setVisibility(View.VISIBLE);
                    loader.displayImage("" + AppSession.getClientImage(this), ivCompanyLogo, company_options);
                } else
                    loader.displayImage("", ivCompanyLogo, company_options);
        }
    }

    public void findViews()
    {
        // Set up the login form.
        mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, IconValue.fa_user, 30), null, null, null);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, IconValue.fa_lock, 30), null, null, null);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        mContactView = (TextView) findViewById(R.id.tvContact);
        mContactPhoneView = (TextView) findViewById(R.id.tvPhone);
        mJoinsView = findViewById(R.id.lv_join_now);
        ivCompanyLogo = (ImageView) findViewById(R.id.ivCompanyLogo);
        btnJoinNow = (Button) findViewById(R.id.btnJoinNow);
        btnJoinNow.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, IconValue.fa_info_circle, 30), null, null, null);
        btnJoinNow.setText("  Help");
        btnInfoJoinView = (Button) findViewById(R.id.btnInfoJoinView);
        btnInfoJoinView.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, IconValue.fa_info_circle, 30), null, null, null);
        btnInfoJoinView.setText("  Help");
        mContactView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {

                mContactView.setTextColor(getResources().getColor(R.color.gray));
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:support@ix.co.za"));
                emailIntent.setType("message/rfc822");
                String aEmailList[] = {"support@ix.co.za"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Smart Manager");

                try
                {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        mContactPhoneView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                mContactPhoneView.setTextColor(getResources().getColor(R.color.gray));
                String posted_by = "0861 292 999";
                final String uri = "tel:" + posted_by.trim();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
                        return;
                    }
                }
                PackageManager pm = LoginActivity.this.getPackageManager();
                int hasPerm = pm.checkPermission(Manifest.permission.CALL_PHONE, LoginActivity.this.getPackageName());
                if (hasPerm == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                } else
                {
                    Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult()
                    {
                        @Override
                        public void onPermissionResult(Permiso.ResultSet resultSet)
                        {
                            if (resultSet.areAllPermissionsGranted())
                            {
                                if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                                {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse(uri));
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions)
                        {
                        }
                    }, Manifest.permission.CALL_PHONE);
                }
            }
        });
        findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });
        findViewById(R.id.btnJoinNow).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Helper.hidekeybord(mPasswordView);
                mJoinsView.setVisibility(View.VISIBLE);
                mLoginFormView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Permiso.getInstance().setActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    ImageLoader loader;
    private DisplayImageOptions company_options;

    private void initImageLoader()
    {
        loader = ImageLoader.getInstance();
        initCompanyImageLoader();
    }

    private void initCompanyImageLoader()
    {
        company_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)
                .showImageOnFail(Color.TRANSPARENT)
                .showImageForEmptyUri(Color.TRANSPARENT)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (dialog != null)
            dialog.dismiss();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin()
    {
        if (mAuthTask != null)
        {
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString().trim();
        mPassword = mPasswordView.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword))
        {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else
        {
            Helper.hidekeybord(mPasswordView);

            if (HelperHttp.isNetworkAvailable(this))
            {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                mLoginStatusMessageView.setText(R.string.authenticating);
                if (!isFinishing())
                {
                    dialog = CustomDialogManager.showProgressDialog(LoginActivity.this, getString(R.string.authenticating));
                    dialog.show();
                }
                validateUser(mEmail, mPassword);
            } else
                HelperHttp.showNoInternetDialog(this);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params)
        {
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;
            if (dialog != null)
                dialog.dismiss();
            if (success)
            {
            } else
            {

                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled()
        {
            mAuthTask = null;
            if (dialog != null)
                dialog.dismiss();
        }
    }


    public void validateUser(final String username, final String password)
    {
        StringBuilder soapMessage = new StringBuilder();
        soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapMessage.append("<Body>");
        soapMessage.append("<AuthenticateUser xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
        soapMessage.append("<userName>" + Helper.getCDATAString(username) + "</userName>");
        soapMessage.append("<password>" + Helper.getCDATAString(Helper.md5(password).toUpperCase(Locale.getDefault())) + "</password>");
        soapMessage.append("</AuthenticateUser>");
        soapMessage.append("</Body>");
        soapMessage.append("</Envelope>");

        VollyResponseListener vollyResponseListener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                handleResponse(volleyError);
            }

            @Override
            public void onResponse(String response)
            {
                if (dialog != null)
                {
                    dialog.dismiss();
                    dialog = null;
                }
                Helper.Log("Response:%n %s", response);
                DataManager.getInstance().user = ParserManager.parseLoginRespose(response);
                if (DataManager.getInstance().user.isAuthenticated())
                {
                    if (TextUtils.isEmpty(DataManager.getInstance().user.getFailureReason()))
                    {
                        AppSession.saveLogin(LoginActivity.this, username, password);
                        /*JSONObject tagObject = new JSONObject();
                        try
						{
							tagObject.put("key", DataManager.getInstance().user.getNotificationIdentifier());
						} catch (JSONException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                		OneSignal.sendTags(tagObject);
                    	finish();
                    	// start home screen
    					Intent intent=new Intent(LoginActivity.this, HomeScreenActivity.class);
    					startActivity(intent);*/
                        sendDeviceID();
                    } else
                    {
                        AppSession.saveLoginStatus(LoginActivity.this, false);
                        findViews();
                        mPasswordView.setText("");
                        mLoginFormView.setVisibility(View.VISIBLE);
                        mEmailView.requestFocus();
                        if (!isFinishing())
                        {
                            CustomDialogManager.showErrorDialogEmail(LoginActivity.this,
                                    DataManager.getInstance().user.getFailureReason() + ". " + getString(R.string.please_contact_on));
                        }
                    }
                } else
                {
                    findViews();
                    AppSession.saveLoginStatus(LoginActivity.this, false);
                    mPasswordView.setText("");
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mEmailView.requestFocus();
                    if (!isFinishing())
                    {
                        CustomDialogManager.showErrorDialogEmail(LoginActivity.this,
                                DataManager.getInstance().user.getFailureReason() + ". " + getString(R.string.please_contact_on));
                    }
                }
            }
        };
        try
        {
            VollyCustomRequest vollyCustomRequest = new VollyCustomRequest(Constants.WEBSERVICE_URL,
                    soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IAuthenticate/AuthenticateUser", vollyResponseListener);
            vollyCustomRequest.init("validateUser");
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public void sendDeviceID()
    {
        if (HelperHttp.isNetworkAvailable(LoginActivity.this))
        {
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("CodeType", "1", String.class));
            parameterList.add(new Parameter("DeviceCode", AppSession.getDeviceID(LoginActivity.this), String.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("SaveDeviceCode");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IAuthenticate/SaveDeviceCode");
            inObj.setUrl(Constants.WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            new WebServiceTask(LoginActivity.this, inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    callNextActivity();
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(LoginActivity.this, getString(R.string.no_internet_connection));
        }

    }

    private void callNextActivity()
    {
        JSONObject tagObject = new JSONObject();
        try
        {
            tagObject.put("key", DataManager.getInstance().user.getNotificationIdentifier());
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Check is on wifi or mobile network // Start Background service
        if (NetworkUtil.getConnectivityStatusString(LoginActivity.this) == ConnectivityManager.TYPE_WIFI)
            startService(new Intent(LoginActivity.this, ServiceImage.class));

        OneSignal.sendTags(tagObject);
        callIntent();
       /* if (AppSession.getImageDetails(LoginActivity.this).equalsIgnoreCase(""))
        {
            CustomDialogManager.showOkCancelDialog(LoginActivity.this, getString(R.string.do_want_to_subscribe_for_notification_services), new DialogListener()
            {
                @Override
                public void onButtonClicked(int type)
                {
                    if (type == Dialog.BUTTON_POSITIVE)
                    {
                        OneSignal.setSubscription(true);
                    } else
                    {
                        OneSignal.setSubscription(false);
                    }
                    callIntent();
                }
            });
        } else
        {
            callIntent();
        }*/


    }

    private void callIntent()
    {
        AppSession.saveImageDetails(LoginActivity.this, "true");
        Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleResponse(VolleyError volleyError)
    {
        Helper.Log("Login error response", "" + volleyError.toString());
        if (dialog != null)
            dialog.dismiss();

        findViews();
        AppSession.saveLoginStatus(LoginActivity.this, false);
        mPasswordView.setText("");
        mLoginFormView.setVisibility(View.VISIBLE);
        mEmailView.requestFocus();
        if (volleyError instanceof NoConnectionError || volleyError instanceof NetworkError)
            if (!isFinishing())
            {
                CustomDialogManager.showOkDialog(LoginActivity.this, "Please check your internet connection or try again later");
            } else if (!isFinishing())
            {
                CustomDialogManager.showErrorDialogEmail(LoginActivity.this, "Your username and or password is invalid. Please contact: ");
            }
    }


    @Override
    public void onBackPressed()
    {
        if (mJoinsView.getVisibility() == View.VISIBLE)
        {
            // hide join view
            mJoinsView.setVisibility(View.GONE);
            mLoginFormView.setVisibility(View.VISIBLE);
            return;
        } else
            super.onBackPressed();
    }
}
