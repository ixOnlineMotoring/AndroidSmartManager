package com.nw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.adapters.BidReceivedAdapter;
import com.nw.adapters.HorizontalListViewAdapter;
import com.nw.adapters.MessageListingAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.MessageListing;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomTextView;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.serialization.SoapObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ActiveTradeFragment extends BaseFragement implements OnClickListener, OnItemClickListener, BidReceivedAdapter.ListClickListener
{
    ImageView ivArrowIcon, ivArrowIcon1;
    LinearLayout llBidReceived, llMessagesReceived;
    RelativeLayout rlBidReceived, rlMessageReceived;
    StaticListView lvBidReceived, lvMessageReceived;
    BidReceivedAdapter bidReceivedAdapter;
    ArrayList<VehicleDetails> vehicleDetails;
    ArrayList<MessageListing> messageList;
    TextView tvHeaderCount1, tvHeaderCount2;
    CustomTextView tvVehicleYear, tvVehicleName, tvRegNumber, tvColor, tvStock,
            tvDepartment, tvMileage, tvRemainingDays, tvRetailPrice,
            tvTradePrice, tvBidPrice, tvBidExpiry, tvHighestBid;
    HorizontalListView hlvCarImages;
    HorizontalListViewAdapter adapter;
    ArrayList<MyImage> list;
    ListDetailsFragment listDetailsFragment;
    CustomButton bnExtend, bnEdit, bRejectedBid, bAccept, bnDeactivate;
    MessageListingAdapter messageListingAdapter;
    Vehicle vehicle;
    int vehicleID;
    int BidsResivedId_postion = 10000;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_active_trade, container,
                false);
        setHasOptionsMenu(true);
        vehicleID = getArguments().getInt("vehicleId");
        // listBids();
        init(view);
        loadVehicle();
        return view;
    }

    private void init(View view)
    {
        tvVehicleYear = (CustomTextView) view.findViewById(R.id.tvVehicleYear);
        tvVehicleName = (CustomTextView) view.findViewById(R.id.tvVehicleName);
        tvRegNumber = (CustomTextView) view.findViewById(R.id.tvRegNumber);
        tvColor = (CustomTextView) view.findViewById(R.id.tvColor);
        tvStock = (CustomTextView) view.findViewById(R.id.tvStock);
        tvDepartment = (CustomTextView) view.findViewById(R.id.tvDepartment);
        tvMileage = (CustomTextView) view.findViewById(R.id.tvMileage);
        tvRemainingDays = (CustomTextView) view.findViewById(R.id.tvRemainingDays);
        tvRetailPrice = (CustomTextView) view.findViewById(R.id.tvRetailPrice);
        tvTradePrice = (CustomTextView) view.findViewById(R.id.tvTradePrice);
        tvBidPrice = (CustomTextView) view.findViewById(R.id.tvBidPrice);
        tvBidExpiry = (CustomTextView) view.findViewById(R.id.tvBidExpiry);
        tvHighestBid = (CustomTextView) view.findViewById(R.id.tvHighestBid);
        hlvCarImages = (HorizontalListView) view.findViewById(R.id.hlvCarImages);
        list = new ArrayList<MyImage>();
        adapter = new HorizontalListViewAdapter(getActivity(), list);
        hlvCarImages.setAdapter(adapter);
        hlvCarImages.setOnItemClickListener(this);

        bnExtend = (CustomButton) view.findViewById(R.id.bnExtend);
        bnEdit = (CustomButton) view.findViewById(R.id.bnEdit);
        bRejectedBid = (CustomButton) view.findViewById(R.id.btnRejectedBid);
        bAccept = (CustomButton) view.findViewById(R.id.btnAccept);
        bnDeactivate = (CustomButton) view.findViewById(R.id.bnDeactivate);

        bnExtend.setOnClickListener(this);
        bnEdit.setOnClickListener(this);
        bRejectedBid.setOnClickListener(this);
        bAccept.setOnClickListener(this);
        bnDeactivate.setOnClickListener(this);

        lvBidReceived = (StaticListView) view.findViewById(R.id.lvBidReceived);
        lvMessageReceived = (StaticListView) view
                .findViewById(R.id.lvMessageReceived);
        ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
        ivArrowIcon1 = (ImageView) view.findViewById(R.id.ivArrowIcon1);
        llBidReceived = (LinearLayout) view.findViewById(R.id.llBidReceived);
        tvHeaderCount1 = (TextView) view.findViewById(R.id.tvHeaderCount1);
        tvHeaderCount2 = (TextView) view.findViewById(R.id.tvHeaderCount2);
        llMessagesReceived = (LinearLayout) view
                .findViewById(R.id.llMessagesReceived);
        rlBidReceived = (RelativeLayout) view.findViewById(R.id.rlBidReceived);
        rlMessageReceived = (RelativeLayout) view
                .findViewById(R.id.rlMessageReceived);
        rlBidReceived.setOnClickListener(this);
        rlMessageReceived.setOnClickListener(this);

        tvRetailPrice.setText(Helper.formatPrice(new BigDecimal(getArguments()
                .getDouble("RetailPrice")) + ""));
        tvTradePrice.setText(Helper.formatPrice(new BigDecimal(getArguments()
                .getDouble("TradePrice")) + ""));
        tvBidPrice.setText(Helper.formatPrice(new BigDecimal(getArguments()
                .getDouble("BidPrice")) + ""));
        tvHighestBid.setText(Helper.formatPrice(new BigDecimal(getArguments()
                .getDouble("BidPrice")) + ""));
        tvDepartment.setText(getArguments().getString("Department"));
        if (getArguments().getString("Screentype").equalsIgnoreCase("trade"))
        {
            tvRemainingDays.setText(Html.fromHtml("<font color=" + getContext().getResources().getColor(R.color.dark_blue) + ">" + getArguments().getString("Days") + " Days" + "</font>"));
        } else
        {
            tvRemainingDays.setText(Html.fromHtml("<font color=" + getContext().getResources().getColor(R.color.dark_blue) + ">" + getArguments().getString("Days") + "</font>"));
        }
    }

    protected void putValues()
    {
        tvVehicleYear.setText(vehicle.getYear() + "");
        tvVehicleName.setText(vehicle.getFriendlyName());
        tvStock.setText(vehicle.getStockNumber());
        tvColor.setText(vehicle.getColour());
        // tvDepartment.setText(vehicle.getDepartment());

        tvRegNumber.setText(vehicle.getRegNumber());
        tvMileage.setText(Helper.getFormattedDistance(vehicle.getMileage() + "") + " Km");
        tvBidExpiry.setText(Helper.convertDateToNormal(vehicle.getExpires()));
        // tvBidPrice.setText(Helper.formatPrice(new
        // BigDecimal(vehicle.getHightestBid()) + ""));
        // tvHighestBid.setText(Helper.formatPrice(new
        // BigDecimal(vehicle.getHightestBid()) + ""));
        // tvRetailPrice.setText(Helper.formatPrice(new
        // BigDecimal(getArguments().getDouble("RetailPrice")) + ""));
        // tvRetailPrice.setText("R?");
        // tvTradePrice.setText(Helper.formatPrice(new
        // BigDecimal(vehicle.getTradePrice()) + ""));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rlBidReceived:
                if (llBidReceived.getVisibility() == View.GONE)
                {
                    if (!tvHeaderCount1.getText().toString().equalsIgnoreCase("0"))
                    {
                        ivArrowIcon.setRotation(90);
                        llBidReceived.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    llBidReceived.setVisibility(View.GONE);
                    ivArrowIcon.setRotation(0);
                }
                break;

            case R.id.rlMessageReceived:
                if (llMessagesReceived.getVisibility() == View.GONE)
                {
                    if (!tvHeaderCount2.getText().toString().equalsIgnoreCase("0"))
                    {
                        llMessagesReceived.setVisibility(View.VISIBLE);
                        ivArrowIcon1.setRotation(90);
                    }
                } else
                {
                    ivArrowIcon1.setRotation(0);
                    llMessagesReceived.setVisibility(View.GONE);
                }
                break;
            case R.id.bnExtend:
                extendBidding();
                break;

            case R.id.bnEdit:

                listDetailsFragment = new ListDetailsFragment();
                Bundle args = new Bundle();
                args.putString("vehicleName", vehicle.getFriendlyName());
                args.putInt("stockID", vehicle.getID());
                listDetailsFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.Container, listDetailsFragment)
                        .addToBackStack(null).commit();
                break;

            case R.id.btnRejectedBid:
                if (!vehicleDetails.isEmpty())
                {
                    if (BidsResivedId_postion != 10000)
                    {
                        rejectBid();
                    } else
                    {
                        Helper.showToast("Please select bids", getActivity());
                    }
                }

                break;

            case R.id.btnAccept:
                if (!vehicleDetails.isEmpty())
                {
                    if (BidsResivedId_postion != 10000)
                    {
                        acceptBid();
                    } else
                    {
                        Helper.showToast("Please select bids", getActivity());
                    }
                }
                break;

            case R.id.bnDeactivate:
                deActivateVehicle(0);
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getArguments().getString("Screentype").equalsIgnoreCase("BiddingEnded"))
        {
            showActionBar("Action Bidding Ended");
        } else if (getArguments().getString("Screentype").equalsIgnoreCase("BidsRecevied"))
        {
            showActionBar("Active Bids Received");
        } else
        {
            showActionBar("Trade Vehicles");
        }
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3)
    {
        if (!list.isEmpty())
        {
            navigateToLargeImage(position, list);
        }
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

        getFragmentManager().beginTransaction().replace(this.getId(), f)
                .addToBackStack(null).commit();
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
            parameterList.add(new Parameter("userHash", DataManager
                    .getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager
                    .getInstance().user.getDefaultClient().getId(),
                    Integer.class));
            if (getArguments() != null)
                parameterList.add(new Parameter("vehicle", vehicleID,
                        Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListBids");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE
                    + "/ITradeService/ListBids");
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
                        vehicleDetails = new ArrayList<VehicleDetails>();
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject vehicleObj = (SoapObject) inner.getProperty(i);

                            vehicle = new VehicleDetails();
                            vehicle.setOfferID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("id", "0")));
                            vehicle.setOfferClientId(Integer.parseInt(vehicleObj.getPropertySafelyAsString("clientID", "0")));
                            vehicle.setOfferClient(vehicleObj.getPropertySafelyAsString("clientName", ""));
                            vehicle.setOfferDate(vehicleObj.getPropertySafelyAsString("Date", ""));
                            vehicle.setOfferAmt(Float.parseFloat(vehicleObj.getPropertySafelyAsString("amount", "0.0")));
                            vehicle.setOfferMember(vehicleObj.getPropertySafelyAsString("user", ""));
                            vehicle.setBidChecked(false);
                            vehicleDetails.add(vehicle);
                        }
                        bidReceivedAdapter = new BidReceivedAdapter(getActivity(), 0, vehicleDetails);
                        bidReceivedAdapter.setListClickListener(ActiveTradeFragment.this);
                        lvBidReceived.setAdapter(bidReceivedAdapter);
                        tvHeaderCount1.setText("" + vehicleDetails.size());
                        // Helper.setListViewHeightBasedOnChildren(lvBidReceived);
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

    private void ListMessagesForVehicleList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", vehicleID, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListMessagesForVehicle");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ListMessagesForVehicle");
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
                        messageList = new ArrayList<MessageListing>();
                        Helper.Log("Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer
                                .getPropertySafely("Messages");
                        MessageListing messageListing;
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            if (inner.getProperty(i) instanceof SoapObject)
                            {
                                SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
                                messageListing = new MessageListing();
                                messageListing.setName(vehicleObj.getPropertySafelyAsString("Name", ""));
                                messageListing.setDate(vehicleObj.getPropertySafelyAsString("Date", ""));
                                messageListing.setMessage(vehicleObj.getPropertySafelyAsString("Message", ""));
                                messageList.add(messageListing);
                            }
                        }
                        if (messageListingAdapter == null)
                        {
                            tvHeaderCount2.setText("" + messageList.size());
                            messageListingAdapter = new MessageListingAdapter(getActivity(), R.layout.single_item_seller_message,
                                    messageList, "Message");
                            lvMessageReceived.setAdapter(messageListingAdapter);
                        } else
                        {
                            messageListingAdapter.notifyDataSetChanged();
                        }
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

    // function loads vehicle
    private void loadVehicle()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("Vehicle", vehicleID, Integer.class));

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
                        vehicle = new Vehicle();
                        SoapObject obj = (SoapObject) result;
                        SoapObject vehicleObj = (SoapObject) obj.getPropertySafely("Vehicle", "default");
                        vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("ID", "")));
                        vehicle.setOwnerID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("OwnerID", "")));
                        vehicle.setOwnerName(vehicleObj.getPropertySafelyAsString("OwnerName", ""));
                        vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Year", "")));
                        vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
                        vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "")));
                        vehicle.setMileageType(vehicleObj.getPropertySafelyAsString("MileageType", ""));
                        vehicle.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
                        vehicle.setLocation(vehicleObj.getPropertySafelyAsString("Location", ""));
                        if (vehicleObj.getPropertyAsString("TradePrice") != null)
                        {
                            vehicle.setTradePrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
                            vehicle.setBuyNow(true);
                        } else
                        {
                            vehicle.setTradePrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
                            vehicle.setBuyNow(false);
                        }
                        vehicle.setCount(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Count", "")));
                        vehicle.setExpires(vehicleObj.getPropertySafelyAsString("Expires", ""));
                        vehicle.setTimeLeft(vehicleObj.getPropertySafelyAsString("TimeLeft", ""));
                        vehicle.setBuyNow(Float.parseFloat(vehicleObj.getPropertySafelyAsString("BuyNow", "")));
                        vehicle.setMinBid(Float.parseFloat(vehicleObj.getPropertySafelyAsString("MinBid", "")));
                        vehicle.setMyHighestBid(Float.parseFloat(vehicleObj.getPropertySafelyAsString("MyHighestBid", "")));
                        vehicle.setHightestBid(Float.parseFloat(vehicleObj.getPropertySafelyAsString("HightestBid", "")));
                        vehicle.setIncrement(Float.parseFloat(vehicleObj.getPropertySafelyAsString("Increment", "")));
                        vehicle.setStockNumber(vehicleObj.getPropertySafelyAsString("StockNumber", ""));
                        vehicle.setRegNumber(vehicleObj.getPropertySafelyAsString("RegNumber", ""));
                        vehicle.setVIN(vehicleObj.getPropertySafelyAsString("VIN", ""));
                        vehicle.setComments(vehicleObj.getPropertySafelyAsString("Comments", ""));
                        vehicle.setExtras(vehicleObj.getPropertySafelyAsString("Extras", ""));
                        Helper.Log("TAG", "" + vehicleObj.getPropertySafelyAsString("AutobidCap", ""), false);
                        SoapObject imageObj = (SoapObject) vehicleObj.getPropertySafely("Images");
                        MyImage image;
                        ArrayList<MyImage> tImageList = new ArrayList<MyImage>();
                        for (int j = 0; j < imageObj.getPropertyCount(); j++)
                        {
                            if (imageObj.getProperty(j) instanceof SoapObject)
                            {
                                image = new MyImage();
                                SoapObject object = (SoapObject) imageObj.getProperty(j);
                                image.setFull(object.getPropertySafelyAsString("Full", ""));
                                image.setThumb(object.getPropertySafelyAsString("Thumb", ""));
                                tImageList.add(j, image);
                            }
                        }
                        if (tImageList.isEmpty())
                        {
                            hlvCarImages.setVisibility(View.GONE);
                        } else
                        {
                            hlvCarImages.setVisibility(View.VISIBLE);
                        }
                        list.addAll(tImageList);
                        adapter.notifyDataSetChanged();
                        vehicle.setImageList(tImageList);
                        putValues();
                        listBids();
                        ListMessagesForVehicleList();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(),
                    getString(R.string.no_internet_connection));
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
            parameterList.add(new Parameter("vehicle", vehicle.getID(), Integer.class));

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
                        String output = StringUtils.substringBetween(msg, "{", "}");
                        String message = StringUtils.substringBetween(output, "=", ";");
                        String date = StringUtils.substringAfterLast(output, "=");
                        tvBidExpiry.setText(Helper.convertDateToNormal(date));
                        if (message.equalsIgnoreCase("trade period extended"))
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
            parameterList.add(new Parameter("bid", vehicleDetails.get(BidsResivedId_postion).getOfferID(), Integer.class));

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
                        {
                            msg = obj.getPropertySafelyAsString("Error", "");
                        } else
                        {
                            vehicleDetails.remove(BidsResivedId_postion);
                            bidReceivedAdapter.notifyDataSetChanged();
                            if (vehicleDetails.size() == 0)
                            {
                                llBidReceived.setVisibility(View.GONE);
                                ivArrowIcon.setRotation(0);
                                tvHeaderCount1.setText("0");
                            }
                        }
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
            parameterList.add(new Parameter("bid", vehicleDetails.get(BidsResivedId_postion).getOfferID(), Integer.class));

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
                        String msg = obj.getPropertySafelyAsString("Success",
                                "");
                        if (TextUtils.isEmpty(msg))
                        {
                            msg = obj.getPropertySafelyAsString("Error", "");
                        } else
                        {
                            vehicleDetails.remove(BidsResivedId_postion);
                            bidReceivedAdapter.notifyDataSetChanged();
                            if (vehicleDetails.size() == 0)
                            {
                                llBidReceived.setVisibility(View.GONE);
                                ivArrowIcon.setRotation(0);
                                tvHeaderCount1.setText("0");
                            }
                        }
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

    @Override
    public void ListClick(int postion)
    {
        BidsResivedId_postion = postion;
        for (int i = 0; i < vehicleDetails.size(); i++)
        {
            if (postion == i)
            {
                vehicleDetails.get(i).setBidChecked(true);
            } else
            {
                vehicleDetails.get(i).setBidChecked(false);
            }
        }
        bidReceivedAdapter.notifyDataSetChanged();
    }

    protected void deActivateVehicle(int TradePrice)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("UsedvehicleStockID", vehicleID, Integer.class));
            parameterList.add(new Parameter("IsTrade", false, Boolean.class));
            parameterList.add(new Parameter("TradePrice", (int) vehicle.getTradePrice(), Integer.class));
            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ActivateVehicle");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/ActivateVehicle");
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
                        Helper.Log("De-ActivateVehicle response", result.toString());
                        hideProgressDialog();
                        SoapObject obj = (SoapObject) result;
                        SoapObject response = (SoapObject) obj.getPropertySafely("Result", "");
                        // final String status =
                        // response.getPrimitivePropertySafelyAsString("Status");
                        CustomDialogManager.showOkDialog(getActivity(), response.getPrimitivePropertySafelyAsString("Message"),
                                new DialogListener()
                                {
                                    @Override
                                    public void onButtonClicked(int type)
                                    {
                                        getActivity().finish();
                                    }
                                });
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_occured_try_later), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                getActivity().finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (listDetailsFragment != null)
            listDetailsFragment.onActivityResult(requestCode, resultCode, data);
    }

}
