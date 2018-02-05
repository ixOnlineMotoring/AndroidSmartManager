package com.nw.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nw.adapters.ScanListAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.Person;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;

import net.photopay.barcode.BarcodeDetailedData;

import java.util.ArrayList;

import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;

public class ScanLicenseFragment extends BaseFragement
{
    Button btnScanLicense;
    SwipeMenuListView lvScanLicenses;
    private static final int MY_REQUEST_CODE = 1337;
    int selectedPageNumber = 0;
    boolean isLoadMore = false;
    String encoded;
    Person person;
    ScanListAdapter scanListAdapter;
    ArrayList<Person> scannedLicenseList, tempList;
    int ON_ITEM_CLICKED = 1, ON_SCAN_BUTTON_CLICK = 2;
    LicenseDetailsFragment licenseDetailsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_scan_license, container, false);
        if (scannedLicenseList == null)
            scannedLicenseList = new ArrayList<Person>();
        if (scannedLicenseList == null)
            tempList = new ArrayList<Person>();
        setHasOptionsMenu(true);
        initialise(view);
        getScannedLicense(selectedPageNumber);

        return view;
    }

    private void initialise(View view)
    {
        btnScanLicense = (Button) view.findViewById(R.id.btnScanLicenses);
        SwipeMenuCreator creator = new SwipeMenuCreator()
        {

            @Override
            public void create(SwipeMenu menu)
            {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(Helper.dpToPx(getActivity(), 90));
                // set a icon
                deleteItem.setIcon(R.drawable.cancel_icon);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        lvScanLicenses = (SwipeMenuListView) view.findViewById(R.id.lvScanLicenses);
        lvScanLicenses.setMenuCreator(creator);
        lvScanLicenses.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        lvScanLicenses.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
            {
                switch (index)
                {
                    case 0:
                        // to delete list item
                        if (!(scannedLicenseList.size() == 0))
                        {
                            deleteLicense(scannedLicenseList.get(position).getScanID(), position);
                        }
                        break;
                }
                return false;
            }
        });
        scanListAdapter = new ScanListAdapter(getActivity(), R.layout.list_item_scan_license, scannedLicenseList);
        lvScanLicenses.setOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int lastSeenItem = firstVisibleItem + visibleItemCount;
                if (!scannedLicenseList.isEmpty())
                {
                    if (lastSeenItem == totalItemCount && !isLoadMore)
                    {
                        if (scannedLicenseList.size() < DataManager.getInstance().getTotalLicenseCount())
                        {
                            selectedPageNumber++;
                            getScannedLicense(selectedPageNumber);
                        }
                    }
                }
            }
        });
        setSwipeListListener();
        lvScanLicenses.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // to open license from list
                if (!(scannedLicenseList.size() == 0))
                {
                    navigateToLicenseDetailsScreen(scannedLicenseList.get(position), ON_ITEM_CLICKED);
                }
            }
        });
        btnScanLicense.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btnScan_click();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Scan License");
    }

    private void setSwipeListListener()
    {
        if (Build.VERSION.SDK_INT >= 11)
        {
            lvScanLicenses.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            lvScanLicenses.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
            {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
                {
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item)
                {
                    return false;
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu)
                {
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode)
                {
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu)
                {
                    return false;
                }
            });
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
//	        sett.setAllowMultipleScanResultsOnSingleImage(false);
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

    private void navigateToLicenseDetailsScreen(Person person, int type)
    {
        licenseDetailsFragment = new LicenseDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("person_details", person);
        bundle.putInt("FromFragment", type);
        licenseDetailsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(this.getId(), licenseDetailsFragment).addToBackStack(null).commit();
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
                    CustomDialogManager.showOkDialog(getActivity(), "No details found, please try again.");
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
                            navigateToLicenseDetailsScreen(person, ON_SCAN_BUTTON_CLICK);
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), "Could not find the details");
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), "No details found, please try again.");
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

    private void getScannedLicense(final int pageNumber)
    {
        try
        {
            showProgressDialog();
            final StringBuilder soapBuilder = new StringBuilder();
            soapBuilder.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body>");
            soapBuilder.append("<ListLicenses xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapBuilder.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapBuilder.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</clientID>");
            soapBuilder.append("<Page>" + pageNumber + "</Page>");
            soapBuilder.append("<RecordCount>" + "10" + "</RecordCount>");
            soapBuilder.append("</ListLicenses></s:Body></s:Envelope>");
            Helper.Log("ListLicenses request", soapBuilder.toString());

            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    VolleyLog.e("ListLicenses Error: ", error.getMessage());
                    CustomDialogManager.showOkDialog(getActivity(), "No details found, please try again.");
                }

                @Override
                public void onResponse(String response)
                {
                    if (!TextUtils.isEmpty(response))
                    {
                        if (pageNumber == 0)
                            scannedLicenseList.clear();

                        hideProgressDialog();
                        tempList = ParserManager.parseDrivingLicenseList(response);
                        if (tempList != null)
                        {
                            scannedLicenseList.addAll(tempList);
                            if (pageNumber == 0)
                            {

                                lvScanLicenses.setAdapter(scanListAdapter);
                            } else
                            {
                                scanListAdapter.notifyDataSetChanged();

                            }
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        });
                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.LICENSE_WEBSERVICE_URL, soapBuilder.toString(),
                    Constants.TEMP_URI_NAMESPACE + "ILicense/ListLicenses", vollyResponseListener);
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

    protected void deleteLicense(String userScanID, final int position)
    {
        try
        {
            showProgressDialog();
            final StringBuilder soapBuilder = new StringBuilder();
            soapBuilder.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body>");
            soapBuilder.append("<RemoveLicense xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapBuilder.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapBuilder.append("<clientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</clientID>");
            soapBuilder.append("<ScanID>" + userScanID + "</ScanID>");
            soapBuilder.append("</RemoveLicense></s:Body></s:Envelope>");
            Helper.Log("RemoveLicense request", soapBuilder.toString());

            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    VolleyLog.e("RemoveLicense Error: ", error.getMessage());
                    CustomDialogManager.showOkDialog(getActivity(), "No Details found, please try again");
                }

                @Override
                public void onResponse(String response)
                {
                    if (!TextUtils.isEmpty(response))
                    {
                        hideProgressDialog();
                        if (response.contains("Success"))
                        {
                            scannedLicenseList.remove(position);
                            scanListAdapter.notifyDataSetChanged();
                            DataManager.getInstance().setTotalLicenseCount(DataManager.getInstance().getTotalLicenseCount() - 1);
                            CustomDialogManager.showOkDialog(getActivity(), "License information removed", new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    getActivity().getFragmentManager().popBackStack();
                                }
                            });
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), "Please try again");
                    }
                }
            };
            VollyCustomRequest request = new VollyCustomRequest(Constants.LICENSE_WEBSERVICE_URL, soapBuilder.toString(),
                    Constants.TEMP_URI_NAMESPACE + "ILicense/RemoveLicense", vollyResponseListener);
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

    public void onBackPressed()
    {
        if (licenseDetailsFragment != null)
        {
            licenseDetailsFragment.onBackPressed();
            licenseDetailsFragment = null;
        } else
        {
            getFragmentManager().popBackStack();
        }
    }
}
