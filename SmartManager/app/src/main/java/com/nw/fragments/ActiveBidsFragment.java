package com.nw.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.adapters.ActiveBidsAdapter;
import com.nw.adapters.HorizontalListViewAdapter;
import com.nw.model.DataInObject;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.activity.VehicleActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ActiveBidsFragment extends BaseFragement implements OnClickListener, OnItemClickListener
{

    HorizontalListView hlvVehicleImages;
    Button bRejectedBid, bAccept, bExtendBid, bEditVehicle;
    ListView lvBids;
    TextView tvStockNumber, tvAge, tvName, tvRetail, tvType, tvPeriod, tvBidsReceived;
    HorizontalListViewAdapter adapter;
    ActiveBidsAdapter bidsAdapter;
    ArrayList<MyImage> list;
    ArrayList<VehicleDetails> listBids;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_active_bids_received, container, false);
        setHasOptionsMenu(true);
        if (listBids == null)
        {
            listBids = new ArrayList<VehicleDetails>();
        }
        initialise(view);
        putValues();
        listBids();
        loadVehicle();
        return view;
    }

    // Function to initialise views
    private void initialise(View view)
    {

        hlvVehicleImages = (HorizontalListView) view.findViewById(R.id.hlvCarImages);

        bRejectedBid = (Button) view.findViewById(R.id.btnRejectedBid);
        bAccept = (Button) view.findViewById(R.id.btnAccept);
        bExtendBid = (Button) view.findViewById(R.id.btnExtendedBid);
        bEditVehicle = (Button) view.findViewById(R.id.btnEditVehicle);

        bRejectedBid.setOnClickListener(this);
        bAccept.setOnClickListener(this);
        bExtendBid.setOnClickListener(this);
        bEditVehicle.setOnClickListener(this);

        tvStockNumber = (TextView) view.findViewById(R.id.tvStockNo);
        tvAge = (TextView) view.findViewById(R.id.tvAge);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvRetail = (TextView) view.findViewById(R.id.tvRetail);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvPeriod = (TextView) view.findViewById(R.id.tvPeriod);
        tvBidsReceived = (TextView) view.findViewById(R.id.tvBidsRecieved);

        lvBids = (ListView) view.findViewById(R.id.lvBids);
        bidsAdapter = new ActiveBidsAdapter(getActivity(), listBids);
        lvBids.setAdapter(bidsAdapter);

        Helper.setListViewHeightBasedOnChildren(lvBids);
        list = new ArrayList<MyImage>();
        adapter = new HorizontalListViewAdapter(getActivity(), list);
        hlvVehicleImages.setAdapter(adapter);
        hlvVehicleImages.setOnItemClickListener(this);
    }

    /*
     * Function to add values to views
     */
    private void putValues()
    {
        tvStockNumber.setText(getArguments().getString("stock_code"));
        tvName.setText(getArguments().getString("offer_client"));
        tvRetail.setText("Retail: " + Helper.formatPrice(new BigDecimal(getArguments().getDouble("price")) + ""));
        tvType.setText("Type: " + getArguments().getString("type"));
        if (!TextUtils.isEmpty(getArguments().getString("offerEnd")))
            tvPeriod.setText(getArguments().getString("offerStart") + " To " + getArguments().getString("offerEnd"));
        else
            tvPeriod.setText(getArguments().getString("offerStart"));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnRejectedBid:
                if (!listBids.isEmpty())
                {
                    rejectBid();
                }

                break;

            case R.id.btnAccept:
                if (!listBids.isEmpty())
                {
                    acceptBid();
                }
                break;

            case R.id.btnEditVehicle:
                navigateToEdit();
                break;

            case R.id.btnExtendedBid:
                extendBidding();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        navigateToLargeImage(position, list);
    }

    /*
     * Function to display Image in Gallery view on clicking on image Parameters
     * position clicked, Imagelist, titleName
     */
    private void navigateToLargeImage(int position, ArrayList<MyImage> list)
    {
        Fragment f = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("imagelist", list);
        args.putInt("index", position);
        args.putString("vehicleName", "Sell: Active Bids Received");
        f.setArguments(args);

        getFragmentManager().beginTransaction().replace(this.getId(), f).addToBackStack("active_bids").commit();
    }

    /*
     * Function to navigate to ListDetails Fragment ListDetailFrament is in
     * VehicleActivity
     */
    Intent iToBuyActivity;

    private void navigateToEdit()
    {
        try
        {
            iToBuyActivity = new Intent(getActivity(), VehicleActivity.class);
            iToBuyActivity.putExtra("ActiveBidsFragment", true);
            iToBuyActivity.putExtra("stockID", getArguments().getInt("vehicleId"));
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Active Bids Received");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Function to accept bid */
    private void acceptBid()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("vehicle", getArguments().getInt("vehicleId"), Integer.class));
            parameterList.add(new Parameter("bid", bidsAdapter.getSelectedVehicle().getOfferID(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("AcceptBid");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/AcceptBid");
            inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {

                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("Response", result.toString());
                        SoapObject obj = (SoapObject) result;
                        String msg = obj.getPropertySafelyAsString("Success", "");
                        if (TextUtils.isEmpty(msg))
                            msg = obj.getPropertySafelyAsString("Error", "");
                        CustomDialogManager.showOkDialog(getActivity(), msg);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    /*
     * Function to reject bid
     */
    private void rejectBid()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("vehicle", getArguments().getInt("vehicleId"), Integer.class));
            parameterList.add(new Parameter("bid", bidsAdapter.getSelectedVehicle().getOfferID(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("RejectBid");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/RejectBid");
            inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {

                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("Response", result.toString());
                        SoapObject obj = (SoapObject) result;
                        String msg = obj.getPropertySafelyAsString("Success", "");
                        if (TextUtils.isEmpty(msg))
                            msg = obj.getPropertySafelyAsString("Error", "");
                        CustomDialogManager.showOkDialog(getActivity(), msg);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    /*
     * Function to extending bid on vehicle
     */
    private void extendBidding()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("vehicle", getArguments().getInt("vehicleId"), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ExtendBidding");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ExtendBidding");
            inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {

                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("Response", result.toString());
                        SoapObject obj = (SoapObject) result;
                        String msg = obj.getPropertySafelyAsString("Success", "");
                        if (msg.equalsIgnoreCase("trade period extended"))
                            CustomDialogManager.showOkDialog(getActivity(), "Trade period extended");
                        else
                            CustomDialogManager.showOkDialog(getActivity(), msg);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    /*
     * Funtion to fetch all bids received on vehicle
     */
    private void listBids()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            if (getArguments() != null)
                parameterList.add(new Parameter("vehicle", getArguments().getInt("vehicleId"), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListBids");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListBids");
            inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {

                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject bidsOuter = (SoapObject) outer.getPropertySafely("Bids");
                        SoapObject inner = (SoapObject) bidsOuter.getProperty("Offers");
                        VehicleDetails vehicle;
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject vehicleObj = (SoapObject) inner.getProperty(i);

                            vehicle = new VehicleDetails();
                            vehicle.setOfferID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("id", "0")));
                            vehicle.setOfferClientId(Integer.parseInt(vehicleObj.getPropertySafelyAsString("clientID", "0")));
                            vehicle.setOfferClient(vehicleObj.getPropertySafelyAsString("clientName", ""));
                            vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("amount", "0.0")));
                            vehicle.setOfferMember(vehicleObj.getPropertySafelyAsString("user", ""));
                            vehicle.setOfferDate(vehicleObj.getPropertySafelyAsString("Date", ""));
                            listBids.add(vehicle);
                        }
                        if (inner.getPropertyCount() == 0)
                        {
                            bRejectedBid.setVisibility(View.GONE);
                            bAccept.setVisibility(View.GONE);
                            tvBidsReceived.setVisibility(View.GONE);
                        }
                        bidsAdapter.notifyDataSetChanged();
                        Helper.setListViewHeightBasedOnChildren(lvBids);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    // function loads vehicle
    private void loadVehicle()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("Vehicle", getArguments().getInt("vehicleId"), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadVehicle");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/LoadVehicle");
            inObj.setUrl(Constants.TRADER_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("soap Response", result.toString());
                        SoapObject obj = (SoapObject) result;
                        SoapObject vehicleObj = (SoapObject) obj.getPropertySafely("Vehicle", "default");
                        tvAge.setText(vehicleObj.getPropertySafelyAsString("Age", "") + " days");
                        SoapObject imageObj = (SoapObject) vehicleObj.getPropertySafely("Images");
                        MyImage image;
                        for (int j = 0; j < imageObj.getPropertyCount(); j++)
                        {

                            if (imageObj.getProperty(j) instanceof SoapObject)
                            {
                                image = new MyImage();
                                SoapObject object = (SoapObject) imageObj.getProperty(j);
                                image.setFull(object.getPropertySafelyAsString("Full", ""));
                                image.setThumb(object.getPropertySafelyAsString("Thumb", ""));
                                list.add(j, image);
                            }
                        }
                        if (list.isEmpty())
                        {
                            MyImage image1 = new MyImage();
                            image1.setFull("");
                            list.add(image1);
                        }
                        adapter.notifyDataSetChanged();

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (BuyActivity.vehicleActivity != null)
            BuyActivity.vehicleActivity.activityResult(requestCode, resultCode, data);
    }
}
