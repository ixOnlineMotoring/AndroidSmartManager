package com.nw.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nw.interfaces.DialogListener;
import com.nw.model.Audit;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.Person;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import net.photopay.barcode.BarcodeDetailedData;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;


public class SellerFragment extends BaseFragement implements OnClickListener
{
    CustomButton btnScanLicense, btnSend;
    private static final int MY_REQUEST_CODE = 1337;
    String encoded;
    Person person;
    EditText edSellerName, edSellerSurname, edCompany, edSellerID, edPhone, edEmail, etStreetAddress;
    TextView tvAge, tvGender, tvDob, tvRestrictions;
    NetworkImageView ivScanLicense;
    ImageLoader imageLoader;
    VehicleDetails vehicleDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_seller, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            vehicleDetails = getArguments().getParcelable("vehicleDetails");
        }
        initialise(view);
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        return view;
    }

    private void initialise(View view)
    {
        btnScanLicense = (CustomButton) view.findViewById(R.id.btnScanLicense);
        btnSend = (CustomButton) view.findViewById(R.id.btnSend);
        btnScanLicense.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        edSellerName = (EditText) view.findViewById(R.id.edSellerName);
        edSellerSurname = (EditText) view.findViewById(R.id.edSellerSurname);
        edCompany = (EditText) view.findViewById(R.id.edCompany);
        edSellerID = (EditText) view.findViewById(R.id.edSellerID);
        edPhone = (EditText) view.findViewById(R.id.etMobile);
        edEmail = (EditText) view.findViewById(R.id.etEmail);
        etStreetAddress = (EditText) view.findViewById(R.id.etStreetAddress);

        tvDob = (TextView) view.findViewById(R.id.tvDob);
        tvAge = (TextView) view.findViewById(R.id.tvAge);
        tvGender = (TextView) view.findViewById(R.id.tvGender);
        tvRestrictions = (TextView) view.findViewById(R.id.tvRestrictions);

        ivScanLicense = (NetworkImageView) view.findViewById(R.id.ivUserPhoto);

        getSellerData();
    }

    private void putValues(Person person)
    {
        edSellerName.setText(person.getInitials());
        edSellerSurname.setText(person.getSurname());
        edCompany.setText(person.getCompany());
        edSellerID.setText(person.getIdentity_Number());
        edEmail.setText(person.getEmail_id());
        edPhone.setText(person.getTelephone());
        etStreetAddress.setText(person.getStreetAddress());

        if (!person.getDateOfBirth().equalsIgnoreCase("0"))
            tvAge.setText(Helper.getAge(person.getDateOfBirth()));
        else
            tvAge.setText("date?");
        tvGender.setText(person.getGender());
        tvDob.setText(person.getDateOfBirth());

        tvRestrictions.setText(person.getlicenceNumber());

      /*  String base64 = (person.getPhoto()).replace("\\.", "");
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ivScanLicense.setImageBitmap(decodedByte);*/

        ivScanLicense.setImageUrl(person.getPhoto(), imageLoader);
        ivScanLicense.setErrorImageResId(R.drawable.noimage);
        ivScanLicense.setDefaultImageResId(R.drawable.noimage);

        // btnScanLicense.setVisibility(View.GONE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Seller");
        //getActivity().getActionBar().setSubtitle(null);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.btnScanLicense:
                btnScan_click();
                break;
            case R.id.btnSend:
                saveSeller();
                break;
        }
    }

    public void btnScan_click()
    {
        // Intent for ScanActivity
        Intent intent = new Intent(getActivity(), Pdf417ScanActivity.class);
        // If you want sound to be played after the scanning process ends,
        // put here the resource ID of your sound file. (optional)
        intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
        // set EXTRAS_ALWAYS_USE_HIGH_RES to true if you want to always use highest
        // possible camera resolution (enabled by default for all devices that support
        // at least 720p camera preview frame size)
        // intent.putExtra(Pdf417ScanActivity.EXTRAS_ALWAYS_USE_HIGH_RES, true);
        // set the license key (for commercial versions only) - obtain your key at
        // http://pdf417.mobi
        intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, Constants.SCAN_LICENSE_KEY);
        // demo license key for package mobi.pdf417.demo
        // If you want to open front facing camera, uncomment the following line.
        // Note that front facing cameras do not have autofocus support, so it will not
        // be possible to scan denser and smaller codes.
        //intent.putExtra(Pdf417ScanActivity.EXTRAS_CAMERA_TYPE, (Parcelable)CameraType.CAMERA_FRONTFACE);
        // You can use Pdf417MobiSettings object to tweak additional scanning parameters.
        // This is entirely optional. If you don't send this object via intent, default
        // scanning parameters will be used - this means both QR and PDF417 codes will
        // be scanned and default camera overlay will be shown.
        Pdf417MobiSettings sett = new Pdf417MobiSettings();
        // set this to true to enable PDF417 scanning
        sett.setPdf417Enabled(true);
        // Set this to true to scan even barcode not compliant with standards
        // For example, malformed PDF417 barcodes which were incorrectly encoded
        // Use only if necessary because it slows down the recognition process
        //		sett.setUncertainScanning(true);
        // Set this to true to scan barcodes which don't have quiet zone (white area) around it
        // Use only if necessary because it drastically slows down the recognition process
        sett.setNullQuietZoneAllowed(true);
        // Set this to true to enable parsing of data from US Driver's License barcodes
        // This feature is available only if license key permits it.
        sett.setDecodeUSDriverLicenseData(true);
        // set this to true to enable QR code scanning
        sett.setQrCodeEnabled(true);
        // set this to true to prevent showing dialog after successful scan
        sett.setDontShowDialog(true);
        // if license permits this, remove Pdf417.mobi logo overlay on scan activity
        // if license forbids this, this option has no effect
        sett.setRemoveOverlayEnabled(true);
        // set this to false if you want to receive at most one scan result
        //        sett.setAllowMultipleScanResultsOnSingleImage(false);
        // put settings as intent extra
        intent.putExtra(Pdf417ScanActivity.EXTRAS_SETTINGS, sett);
        // Start Activity
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_REQUEST_CODE && resultCode == Pdf417ScanActivity.RESULT_OK)
        {
            // obtain scan results
            // ArrayList<Pdf417MobiScanData> scanDataList =
            //data.getParcelableArrayListExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_MODE);

            Pdf417MobiScanData scanData = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RESULT);

            // NOTE: if you are interested in only single scan result, you can obtain the first element of the array list
            //       or you can use the old key EXTRAS_RESULT
            // If you have set allowing of multiple scan results on single image to false
            //(Pdf417MobiSettings.setAllowMultipleScanResultsOnSingleImage method)
            // scanDataList will contain at most one element.
            // Pdf417MobiScanData scanData = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RESULT);

            // read scanned barcode type (PDF417 or QR code)
            //String barcodeType = scanData.getBarcodeType();
            // read the data contained in barcode
            //String barcodeData = scanData.getBarcodeData();
            // read raw barcode data
            if (scanData.getBarcodeType().equals("PDF417"))
            {
                BarcodeDetailedData rawData = scanData.getBarcodeRawData();
                byte[] rawArray = rawData.getAllData();
                encoded = Base64.encodeToString(rawArray, Base64.DEFAULT);
                Log.v("BASE 64 STRING", encoded);
                getLicenseDetails(encoded);
            }

        }

    }

    private void getLicenseDetails(String encodedData)
    {
        try
        {
            showProgressDialog();
            final StringBuilder soapBuilder = new StringBuilder();
            soapBuilder.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body>");
            soapBuilder.append("<DecodeToXML xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\"><userHash>" +
                    DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapBuilder.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</clientID>");
            soapBuilder.append("<base64LicenseData>" + encodedData + "</base64LicenseData>");
            soapBuilder.append("</DecodeToXML></s:Body></s:Envelope>");
            Helper.Log("DecodeToXML request", soapBuilder.toString());

            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    VolleyLog.e("DecodeToXML Error: ", error.getMessage());
                    CustomDialogManager.showOkDialog(getActivity(), "No Details found, please try again");
                }

                @Override
                public void onResponse(String response)
                {
                    if (!TextUtils.isEmpty(response))
                    {
                        person = ParserManager.parseDrivingLicenseRespose(response);
                        hideProgressDialog();
                        if (person != null)
                        {
                            //navigateToLicenseDetailsScreen(person,ON_SCAN_BUTTON_CLICK);
                            putValues(person);
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), "This is not a valid Barcode");
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), "No Details found");
                    }
                }
            };
            VollyCustomRequest request = new VollyCustomRequest(Constants.LICENSE_WEBSERVICE_URL, soapBuilder.toString(),
                    Constants.TEMP_URI_NAMESPACE + "ILicense/DecodeToXML", vollyResponseListener);
            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }

        } catch (NumberFormatException e1)
        {
            e1.printStackTrace();
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    private void getSellerData()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
            //    parameterList.add(new Parameter("ClientID", 1, Integer.class));
            parameterList.add(new Parameter("AppraisalId", vehicleDetails.getStr_AppraisalId(), Integer.class));
            parameterList.add(new Parameter("VinNumber", vehicleDetails.getVin(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadSellerInformation");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadSellerInformation");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("soap response", result.toString());
                        hideProgressDialog();
                        Helper.Log("Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject response = (SoapObject) outer.getPropertySafely("SellerInfo");
                        person = new Person();
                        person.setInitials(response.getPropertySafelyAsString("Name", ""));
                        person.setSurname(response.getPropertySafelyAsString("Surname", ""));
                        person.setCompany(response.getPropertySafelyAsString("Company", ""));
                        person.setIdentity_Number(response.getPropertySafelyAsString("IDNumber", ""));
                        person.setAge(response.getPropertySafelyAsString("Age", ""));
                        person.setGender(response.getPropertySafelyAsString("GenderType", ""));
                        person.setDateOfBirth(response.getPropertySafelyAsString("DOB", ""));
                        person.setSellerId(response.getPropertySafelyAsString("SellerId", ""));
                        //    person.setlicenceNumber(response.getPropertySafelyAsString("licenceNumber", ""));
                        person.setEmail_id(response.getPropertySafelyAsString("Email", ""));
                        person.setTelephone(response.getPropertySafelyAsString("Mobile", ""));
                        person.setPhoto(response.getPropertySafelyAsString("Image", ""));
                        person.setStreetAddress(response.getPropertySafelyAsString("StreetAddress", ""));

                        SoapObject inner = (SoapObject) response.getPropertySafely("DriversLicences");

                        StringBuffer LicenceType = new StringBuffer();
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject leadObj = (SoapObject) inner.getProperty(i);
                            LicenceType.append(leadObj.getPropertySafelyAsString("DriversLicenceName", ""));
                            if ((inner.getPropertyCount() - 1) != i)
                                LicenceType.append(", ");
                        }
                        person.setlicenceNumber("" + LicenceType);
                        putValues(person);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                // getActivity().getFragmentManager().popBackStack();
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

    private void saveSeller()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SaveSellerInformation xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");

            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<SellerInformationXML>");
            soapMessage.append("<SellerInfo>");

            soapMessage.append("<AppraisalId>" + vehicleDetails.getStr_AppraisalId() + "</AppraisalId>");
            soapMessage.append("<Company>" + edCompany.getText().toString().trim() + "</Company>");
            soapMessage.append("<Email>" + edEmail.getText().toString().trim() + "</Email>");
            soapMessage.append("<IDNumber>" + edSellerID.getText().toString().trim() + "</IDNumber>");
            soapMessage.append("<Mobile>" + edPhone.getText().toString().trim() + "</Mobile>");
            soapMessage.append("<Name>" + edSellerName.getText().toString().trim() + "</Name>");
            soapMessage.append("<Surname>" + edSellerSurname.getText().toString().trim() + "</Surname>");
            if (person != null)
            {
                if (person.getSellerId() != null)
                    soapMessage.append("<SellerId>" + person.getSellerId() + "</SellerId>");
                else
                    soapMessage.append("<SellerId>" + "" + "</SellerId>");
            } else
            {
                soapMessage.append("<SellerId>" + "" + "</SellerId>");
            }

            // soapMessage.append("<ClientId>" + 1 + "</ClientId>");
            soapMessage.append("<ClientId>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientId>");
            soapMessage.append("<VinNumber>" + vehicleDetails.getVin() + "</VinNumber>");
            soapMessage.append("<StreetAddress>" + etStreetAddress.getText().toString().trim() + "</StreetAddress>")
            ;
            soapMessage.append("</SellerInfo>");
            soapMessage.append("</SellerInformationXML>");
            soapMessage.append("</SaveSellerInformation>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            Helper.Log("SaveVehicle request", "" + soapMessage);

            VollyResponseListener listener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    VolleyLog.e("Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    hideProgressDialog();
                    if (response == null)
                    {
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_seller_data), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                //getFragmentManager().popBackStack();
                            }
                        });
                        return;
                    } else
                    {
                        String PassOrFailed = ParserManager.parsetokenChecker(response, "PassOrFailed");
                        String message = "";
                        if (PassOrFailed.equalsIgnoreCase("true"))
                        {
                            message = "Seller save successfully";
                        } else
                        {
                            message = "Error while saving Seller";
                        }

                        CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {

                            }
                        });
                    }
                }
            };
            request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SaveSellerInformation", listener);
            try
            {
                request.init();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }


}