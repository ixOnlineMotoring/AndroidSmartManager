package com.nw.fragments;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nw.adapters.ActiveStockFeedsAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.ActiveStockFeeds;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.AppSession;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class StockSummaryFragment extends BaseFragement
{
    TextView tvDealersName, tvLastStockImport, tvManualUpdate, tvNetTotal, tvIgnoreStockImport;

    TextView tvUsedActiveVeh, tvUsedActivePics, tvUsedActiveVideos, tvUsedActiveMan,
            tvUsedExcludedVeh, tvUsedExcludedPics, tvUsedExcludedVideos, tvUsedExcludedMan,
            tvUsedInvalidVeh, tvUsedInvalidPics, tvUsedInvalidVideos, tvUsedInvalidMan,
            tvUsedVehTotal, tvUsedPicsTotal, tvUsedVideosTotal, tvUsedManTotal;

    TextView tvNewActiveVeh, tvNewActivePics, tvNewActiveVideos, tvNewActiveMan,
            tvNewExcludedVeh, tvNewExcludedPics, tvNewExcludedVideos, tvNewExcludedMan,
            tvNewInvalidVeh, tvNewInvalidPics, tvNewInvalidVideos, tvNewInvalidMan,
            tvNewVehTotal, tvNewPicsTotal, tvNewVideosTotal, tvNewManTotal;
    EditText etTest, etEmailList;

    StaticListView lvActiveStockFeeds;
    ActiveStockFeedsAdapter activeStockFeedsAdapter;
    ArrayList<ActiveStockFeeds> activeStockFeedsList;

    CheckBox cbUsedExcluded, cbUsedInvalid, cbNewExcluded, cbNewInvalid;

    Button btnSendEmailList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_stock_summary, container, false);
        setHasOptionsMenu(true);
        initialise(view);
        return view;
    }

    private void initialise(View view)
    {

        tvDealersName = (TextView) view.findViewById(R.id.tvVehicleName);
        tvLastStockImport = (TextView) view.findViewById(R.id.tvLastStockImport);
        tvManualUpdate = (TextView) view.findViewById(R.id.tvManualUpdate);

        //Views for the Used Active Vehicles
        tvUsedActiveVeh = (TextView) view.findViewById(R.id.tvUsedActiveVeh);
        tvUsedActivePics = (TextView) view.findViewById(R.id.tvUsedActivePics);
        tvUsedActiveVideos = (TextView) view.findViewById(R.id.tvUsedActiveVideos);
        tvUsedActiveMan = (TextView) view.findViewById(R.id.tvUsedActiveMan);

        //Views for the Used Excluded Vehicles
        tvUsedExcludedVeh = (TextView) view.findViewById(R.id.tvUsedExcludedVeh);
        tvUsedExcludedPics = (TextView) view.findViewById(R.id.tvUsedExcludedPics);
        tvUsedExcludedVideos = (TextView) view.findViewById(R.id.tvUsedExcludedVideos);
        tvUsedExcludedMan = (TextView) view.findViewById(R.id.tvUsedExcludedMan);

        //Views for the Used Invalid Vehicles
        tvUsedInvalidVeh = (TextView) view.findViewById(R.id.tvUsedInvalidVeh);
        tvUsedInvalidPics = (TextView) view.findViewById(R.id.tvUsedInvalidPics);
        tvUsedInvalidVideos = (TextView) view.findViewById(R.id.tvUsedInvalidVideos);
        tvUsedInvalidMan = (TextView) view.findViewById(R.id.tvUsedInvalidMan);

        //Views for the Used Total Records
        tvUsedVehTotal = (TextView) view.findViewById(R.id.tvUsedVehTotal);
        tvUsedPicsTotal = (TextView) view.findViewById(R.id.tvUsedPicsTotal);
        tvUsedVideosTotal = (TextView) view.findViewById(R.id.tvUsedVideosTotal);
        tvUsedManTotal = (TextView) view.findViewById(R.id.tvUsedManTotal);


        //Views for the New Active Vehicles
        tvNewActiveVeh = (TextView) view.findViewById(R.id.tvNewActiveVeh);
        tvNewActivePics = (TextView) view.findViewById(R.id.tvNewActivePics);
        tvNewActiveVideos = (TextView) view.findViewById(R.id.tvNewActiveVideos);
        tvNewActiveMan = (TextView) view.findViewById(R.id.tvNewActiveMan);

        //Views for the New Excluded Vehicles
        tvNewExcludedVeh = (TextView) view.findViewById(R.id.tvNewExcludedVeh);
        tvNewExcludedPics = (TextView) view.findViewById(R.id.tvNewExcludedPics);
        tvNewExcludedVideos = (TextView) view.findViewById(R.id.tvNewExcludedVideos);
        tvNewExcludedMan = (TextView) view.findViewById(R.id.tvNewExcludedMan);

        //Views for the Invalid Vehicles
        tvNewInvalidVeh = (TextView) view.findViewById(R.id.tvNewInvalidVeh);
        tvNewInvalidPics = (TextView) view.findViewById(R.id.tvNewInvalidPics);
        tvNewInvalidVideos = (TextView) view.findViewById(R.id.tvNewInvalidVideos);
        tvNewInvalidMan = (TextView) view.findViewById(R.id.tvNewInvalidMan);

        //Views for the New Total Records
        tvNewVehTotal = (TextView) view.findViewById(R.id.tvNewVehTotal);
        tvNewPicsTotal = (TextView) view.findViewById(R.id.tvNewPicsTotal);
        tvNewVideosTotal = (TextView) view.findViewById(R.id.tvNewVideosTotal);
        tvNewManTotal = (TextView) view.findViewById(R.id.tvNewManTotal);

        //View for Net total of both Used & New
        tvNetTotal = (TextView) view.findViewById(R.id.tvNetTotal);
        lvActiveStockFeeds = (StaticListView) view.findViewById(R.id.lvActiveStockFeeds);
        etTest = (EditText) view.findViewById(R.id.etTest);
        etEmailList = (EditText) view.findViewById(R.id.etEmailList);
        tvIgnoreStockImport = (TextView) view.findViewById(R.id.tvIgnoreStockImport);
        tvIgnoreStockImport.setTypeface(tvIgnoreStockImport.getTypeface(), Typeface.BOLD_ITALIC);

        //View for Used Excluded & Invalid Vehicles
        cbUsedExcluded = (CheckBox) view.findViewById(R.id.cbUsedExcluded);
        cbUsedInvalid = (CheckBox) view.findViewById(R.id.cbUsedInvalid);

        //View for New Excluded & Invalid Vehicles
        cbNewExcluded = (CheckBox) view.findViewById(R.id.cbNewExcluded);
        cbNewInvalid = (CheckBox) view.findViewById(R.id.cbNewInvalid);

        btnSendEmailList = (Button) view.findViewById(R.id.btnSendEmailList);

        if (activeStockFeedsList == null)
        {
            activeStockFeedsList = new ArrayList<>();
        }
        initialiseActiveFeedsAdapter();
        getStockSummary();
        btnSendEmailList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String getEmailLists = etEmailList.getText().toString();
                validateEmailList(getEmailLists);
            }
        });
    }

    private void validateEmailList(String emails)
    {

        if (!cbUsedExcluded.isChecked()
                && !cbUsedInvalid.isChecked()
                && !cbNewExcluded.isChecked()
                && !cbNewInvalid.isChecked())
        {

            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.please_select_one_option));
            return;

        }

        if (emails.equalsIgnoreCase(""))
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.please_enter_atleast_one_email));

        } else
        {
            if (emails.contains(";"))
            {
                String[] emailArray = emails.split(";");
                for(int i = 0; i < emailArray.length; i++)
                {
                    if (!Helper.validMail(emailArray[i].trim()))
                    {
                        CustomDialogManager.showOkDialog(getActivity(), "Email Address " + emailArray[i] + " is incorrect");
                        return;
                    }
                }
            } else
            {
                if (!Helper.validMail(emails))
                {
                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.please_enter_valid_email));

                    return;
                }
            }

            sendEmailList(cbUsedExcluded.isChecked(), cbUsedInvalid.isChecked(),
                    cbNewExcluded.isChecked(), cbNewInvalid.isChecked(), etEmailList.getText().toString());
        }

    }

    private void initialiseActiveFeedsAdapter()
    {
        activeStockFeedsAdapter = new ActiveStockFeedsAdapter(getActivity(), R.layout.active_stock_feeds_items, activeStockFeedsList);
        lvActiveStockFeeds.setAdapter(activeStockFeedsAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar(getString(R.string.stock_summary));
    }

    //Webservice implementation for Stock Summary
    private void getStockSummary()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("StockSummary");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/StockSummary");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    etTest.requestFocus();
                    activeStockFeedsList = new ArrayList<>();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Stock_Summary");
                        String dealerName = inner.getPropertySafelyAsString("DealerName", "0");
                        tvDealersName.setText(dealerName);
                        tvDealersName.setTextAppearance(getActivity(), R.style.WhiteText);

                        //Created an Object for Stock Import
                        SoapObject stockImports =
                                (SoapObject) inner.getPropertySafely("Stock_Import", "default");

                        //Fetched lastStockImport
                        String lastStockImport =
                                stockImports.getPropertySafelyAsString("Last_Stock_Import", "0");
                        tvLastStockImport.setText(lastStockImport);
                        tvLastStockImport.setTextAppearance(getActivity(), R.style.WhiteText);

                        //Fetched lastManuaLUpdate
                        String lastManualUpdate =
                                stockImports.getPropertySafelyAsString("Last_Manual_Upadte", "0");
                        tvManualUpdate.setText(lastManualUpdate);
                        tvManualUpdate.setTextAppearance(getActivity(), R.style.WhiteText);

                        //Fetch all Used Active Stock
                        //Create an object for Stock Totals
                        SoapObject stockTotals =
                                (SoapObject) inner.getPropertySafely("Stock_Totals", "0");

                        //Create an inner object for Used
                        SoapObject used = (SoapObject) stockTotals.getPropertySafely("Used", "0");

                        //Create an inner object for Active
                        SoapObject active = (SoapObject) used.getPropertySafely("Active", "0");

                        String usedActiveVehs = active.getPropertySafelyAsString("vehs", "0");
                        tvUsedActiveVeh.setText(usedActiveVehs);

                        String usedActivePics = active.getPropertySafelyAsString("Pics", "0");
                        tvUsedActivePics.setText(usedActivePics);

                        String usedActiveVideos = active.getPropertySafelyAsString("Videos", "0");
                        tvUsedActiveVideos.setText(usedActiveVideos);

                        String usedActiveMan = active.getPropertySafelyAsString("Man", "0");
                        tvUsedActiveMan.setText(usedActiveMan);


                        //Fetch all Used Excluded Stock
                        //Create an inner object for Excluded
                        SoapObject excluded = (SoapObject) used.getPropertySafely("Excluded", "0");

                        String usedExcludedVehs = excluded.getPropertySafelyAsString("vehs", "0");
                        tvUsedExcludedVeh.setText(usedExcludedVehs);

                        String usedExcludedPics = excluded.getPropertySafelyAsString("Pics", "0");
                        tvUsedExcludedPics.setText(usedExcludedPics);

                        String usedExcludedVideos =
                                excluded.getPropertySafelyAsString("Videos", "0");
                        tvUsedExcludedVideos.setText(usedExcludedVideos);

                        String usedExcludedMan = excluded.getPropertySafelyAsString("Man", "0");
                        tvUsedExcludedMan.setText(usedExcludedMan);


                        //Fetch all Used Invalid Stock
                        //Create an inner object for Invalid
                        SoapObject invalid = (SoapObject) used.getPropertySafely("Invalid", "0");

                        String usedInvalidVehs = invalid.getPropertySafelyAsString("vehs", "0");
                        tvUsedInvalidVeh.setText(usedInvalidVehs);

                        String usedInvalidPics = invalid.getPropertySafelyAsString("Pics", "0");
                        tvUsedInvalidPics.setText(usedInvalidPics);

                        String usedInvalidVideos =
                                invalid.getPropertySafelyAsString("Videos", "0");
                        tvUsedInvalidVideos.setText(usedInvalidVideos);

                        String usedInvalidMan = invalid.getPropertySafelyAsString("Man", "0");
                        tvUsedInvalidMan.setText(usedInvalidMan);


                        //Fetch all Used Totals Stock
                        //Create an inner object for Totals
                        SoapObject totals = (SoapObject) used.getPropertySafely("Totals", "0");
                        String usedVehsTotal = totals.getPropertySafelyAsString("Used_Vehs", "0");
                        tvUsedVehTotal.setText(usedVehsTotal);

                        String usedPicsTotal = totals.getPropertySafelyAsString("Used_Pics", "0");
                        tvUsedPicsTotal.setText(usedPicsTotal);

                        String usedVideosTotal =
                                totals.getPropertySafelyAsString("Used_Videos", "0");
                        tvUsedVideosTotal.setText(usedVideosTotal);

                        String usedManTotal = totals.getPropertySafelyAsString("Used_Man", "0");
                        tvUsedManTotal.setText(usedManTotal);

                        //Fetch all New Active Stock
                        //Create an inner object for Used
                        SoapObject newVehicle =
                                (SoapObject) stockTotals.getPropertySafely("New", "0");

                        //Create an inner object for New Active
                        SoapObject new_active =
                                (SoapObject) newVehicle.getPropertySafely("Active", "0");

                        String newActiveVehs = new_active.getPropertySafelyAsString("vehs", "0");
                        tvNewActiveVeh.setText(newActiveVehs);

                        String newActivePics = new_active.getPropertySafelyAsString("Pics", "0");
                        tvNewActivePics.setText(newActivePics);

                        String newActiveVideos =
                                new_active.getPropertySafelyAsString("Videos", "0");
                        tvNewActiveVideos.setText(newActiveVideos);

                        String newActiveMan = new_active.getPropertySafelyAsString("Man", "0");
                        tvNewActiveMan.setText(newActiveMan);

                        //Create an inner object for New Excluded
                        SoapObject new_excluded = (SoapObject) newVehicle.getPropertySafely("Excluded", "0");

                        String newExcludedVehs = new_excluded.getPropertySafelyAsString("vehs", "0");
                        tvNewExcludedVeh.setText(newExcludedVehs);

                        String newExcludedPics = new_excluded.getPropertySafelyAsString("Pics", "0");
                        tvNewExcludedPics.setText(newExcludedPics);

                        String newExcludedVideos = new_excluded.getPropertySafelyAsString("Videos", "0");
                        tvNewExcludedVideos.setText(newExcludedVideos);

                        String newExcludedMan = new_excluded.getPropertySafelyAsString("Man", "0");
                        tvNewExcludedMan.setText(newExcludedMan);

                        //Create an inner object for New Invalid
                        SoapObject new_invalid = (SoapObject) newVehicle.getPropertySafely("Invalid", "0");

                        String newInvalidVehs = new_invalid.getPropertySafelyAsString("vehs", "0");
                        tvNewInvalidVeh.setText(newInvalidVehs);

                        String newInvalidPics = new_invalid.getPropertySafelyAsString("Pics", "0");
                        tvNewInvalidPics.setText(newInvalidPics);

                        String newInvalidVideos = new_invalid.getPropertySafelyAsString("Videos", "0");
                        tvNewInvalidVideos.setText(newInvalidVideos);

                        String newInvalidMan = new_invalid.getPropertySafelyAsString("Man", "0");
                        tvNewInvalidMan.setText(newInvalidMan);

                        //Create an inner object for New Vehicle Total
                        SoapObject new_vehicle_total = (SoapObject) newVehicle.getPropertySafely("Totals", "0");

                        String newVehTotal = new_vehicle_total.getPropertySafelyAsString("New_Vehs", "0");
                        tvNewVehTotal.setText(newVehTotal);

                        String newPicsTotal = new_vehicle_total.getPropertySafelyAsString("New_Pics", "0");
                        tvNewPicsTotal.setText(newPicsTotal);

                        String newVideosTotal = new_vehicle_total.getPropertySafelyAsString("New_Videos", "0");
                        tvNewVideosTotal.setText(newVideosTotal);

                        String newManTotal = new_vehicle_total.getPropertySafelyAsString("New_Man", "0");
                        tvNewManTotal.setText(newManTotal);

                        //Create an inner object for NeT Total
                        String netTotal = stockTotals.getPropertySafelyAsString("Total", "0");
                        tvNetTotal.setText(netTotal);

                        //Created object for Active Stock Feeds
                        String activeFeedsName = inner.getPropertySafelyAsString("Active_Stock_Feeds", "0");
                        String feedsArray[] = activeFeedsName.split(",");
                        ArrayList activeFeedsList = new ArrayList();
                        for (int i = 0; i < feedsArray.length; i++)
                        {
                            ActiveStockFeeds activeStockFeeds = new ActiveStockFeeds();
                            activeFeedsList.add(feedsArray[i]);
                            activeStockFeeds.setActiveStocksName(activeFeedsList.get(i).toString());
                            activeStockFeedsList.add(activeStockFeeds);
                        }
                        initialiseActiveFeedsAdapter();
                        // activeStockFeedsAdapter.notifyDataSetChanged();

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();

        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    //Webservice implementation to send email
    private void sendEmailList(boolean isUsedExcluded, boolean isUsedInvalid, boolean isNewExcluded, boolean isNewInvalid, String emailList)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            String usedExcluded, usedInvalid, newExcluded, newInvalid;

            if (isUsedExcluded)
            {
                usedExcluded = "1";
            } else
            {
                usedExcluded = "0";
            }

            if (isUsedInvalid)
            {
                usedInvalid = "1";
            } else
            {
                usedInvalid = "0";
            }

            if (isNewExcluded)
            {
                newExcluded = "1";
            } else
            {
                newExcluded = "0";
            }
            if (isNewInvalid)
            {
                newInvalid = "1";
            } else
            {
                newInvalid = "0";
            }


            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("usedExcluded", usedExcluded, Integer.class));
            parameterList.add(new Parameter("usedInvalid", usedInvalid, Integer.class));
            parameterList.add(new Parameter("newExcluded", newExcluded, Integer.class));
            parameterList.add(new Parameter("newInvailid", newInvalid, Integer.class));
            parameterList.add(new Parameter("recipientslist", emailList, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("StockSummaryEmailList");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/StockSummaryEmailList");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    try
                    {
                        SoapObject outer = (SoapObject) result;

                        String response = outer.getPropertyAsString("Success");
                        String successResponse = "";
                        if (response.equalsIgnoreCase("False"))
                        {
                            successResponse = getString(R.string.failure_message);
                        } else
                        {
                            successResponse = getString(R.string.success_message);
                        }

                        CustomDialogManager.showOkDialog(getActivity(), successResponse, new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                etEmailList.setText("");
                                cbUsedExcluded.setChecked(false);
                                cbUsedInvalid.setChecked(false);
                                cbNewExcluded.setChecked(false);
                                cbNewInvalid.setChecked(false);
                                etTest.requestFocus();
                            }
                        });

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();


        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));

        }
    }

}
