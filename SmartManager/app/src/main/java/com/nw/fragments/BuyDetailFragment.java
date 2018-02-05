package com.nw.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.adapters.HorizontalListViewAdapter;
import com.nw.adapters.MessageListingAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.MessageListing;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

public class BuyDetailFragment extends BaseFragement implements OnClickListener, OnItemClickListener
{

    HorizontalListView hlvCarImages;
    HorizontalListViewAdapter adapter;
    NetworkImageView ivCar;
    ImageView ivRibbon;
    TextView tvTitle, tvMileage, tvColor, tvLocation, tvTradePrice, tvTimeLeft, tvAutoBidding, tvtitleMinBid;
    TextView tvhighest_bid_so_far, tvBestOffer, tvMinBid, tvIncrement, tvBuyNowPrice, tvBiddingClosingDate;
    TextView tvOwnerName, tvStockNo, tvRegNo, tvVinNo, tvComments, tvExtras, tvMyBid, tvMyBidStatus;
    TextView tvPriceChange, tvVehicleDescribed, tvVehicleDispatched, tvReviewCount;
    EditText edNextBid, edAutoBid, edMessageInput;
    Button btnPlaceBid, btnAutoBid, btnBuyNow, btnAutoCancel, btnAutoActivate, bnSubmitMessage;
    ImageLoader imageLoader;
    Vehicle vehicle;
    ArrayList<MyImage> tempList;
    int bidLimit, vehicleID;
    LinearLayout llAutoBid;
    private boolean isAutoBiddingSet = false;
    IntrUpdateList intrUpdateList;
    TableRow trAutoBid, trBuyNow;
    ImageView ivArrowIcon, ivArrowIconRating;
    RelativeLayout rlMessage, rlNoRating, rlRating;
    LinearLayout llMessage, llRating;
    StaticListView slvMessages;
    TextView tvHeaderCount1;
    MessageListingAdapter messageListingAdapter;
    ArrayList<MessageListing> messageList, questionsList, reviewlist;
    RatingBar ratingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_buy_details, container, false);

        // get arguments from previous screen
        vehicleID = getArguments().getInt("selectedVehicleid");
        setHasOptionsMenu(true);
        intrUpdateList = (IntrUpdateList) getActivity();
        imageLoader = VolleySingleton.getInstance().getImageLoader();

        initialise(view);
        try
        {
            if (vehicle == null)
            {
                loadVehicle();
            } else
            {
                tempList.addAll(vehicle.getImageList());
                Collections.reverse(tempList); // clients requirement to display
                // images in reverse order
                putValues(); // if vehicle is already loaded no need to call web
                // service again
                getSellerRating();
                ListMessagesForVehicleList();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        hideKeyboard(view);
        return view;
    }

    // function initialises all views in screen
    private void initialise(View view)
    {
        // Views initialisation
        slvMessages = (StaticListView) view.findViewById(R.id.lvMessage);
        tvHeaderCount1 = (TextView) view.findViewById(R.id.tvHeaderCount1);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ivCar = (NetworkImageView) view.findViewById(R.id.ivItemBuyList);
        ivRibbon = (ImageView) view.findViewById(R.id.ivRibbon);
        tvTitle = (TextView) view.findViewById(R.id.tvTitleItemBuyList);
        tvTitle.setTextColor(getResources().getColor(R.color.dark_blue));
        tvMileage = (TextView) view.findViewById(R.id.tvDistanceItemBuyList);
        tvColor = (TextView) view.findViewById(R.id.tvColorItemBuyList);
        tvLocation = (TextView) view.findViewById(R.id.tvLocationItemBuyList);
        tvTradePrice = (TextView) view.findViewById(R.id.tvPriceItemBuyList);
        // tvTradePrice.setTextColor(getResources().getColor(R.color.green));
        tvTimeLeft = (TextView) view.findViewById(R.id.tvTimeLeftItemBuyList);
        tvTimeLeft.setTextColor(getResources().getColor(R.color.red));
        tvhighest_bid_so_far = (TextView) view.findViewById(R.id.tvhighest_bid_so_far);
        tvBestOffer = (TextView) view.findViewById(R.id.tvBestOffer);
        tvMinBid = (TextView) view.findViewById(R.id.tvMinBid);
        tvIncrement = (TextView) view.findViewById(R.id.tvIncrement);
        tvBuyNowPrice = (TextView) view.findViewById(R.id.tvBuyNowPrice);
        tvBiddingClosingDate = (TextView) view.findViewById(R.id.tvBiddingClosingDate);
        tvOwnerName = (TextView) view.findViewById(R.id.tvOwnerName);
        tvStockNo = (TextView) view.findViewById(R.id.tvStockNumber);
        tvRegNo = (TextView) view.findViewById(R.id.tvRegNumber);
        tvVinNo = (TextView) view.findViewById(R.id.tvVinNumber);
        tvPriceChange = (TextView) view.findViewById(R.id.tvPriceChange);
        tvVehicleDescribed = (TextView) view.findViewById(R.id.tvVehicleDescribed);
        tvVehicleDispatched = (TextView) view.findViewById(R.id.tvVehicleDispatched);
        tvReviewCount = (TextView) view.findViewById(R.id.tvReviewCount);
        tvComments = (TextView) view.findViewById(R.id.tvComments);
        tvExtras = (TextView) view.findViewById(R.id.tvExtras);
        edNextBid = (EditText) view.findViewById(R.id.edNextBidPrice);
        edMessageInput = (EditText) view.findViewById(R.id.edMessageInput);
        btnPlaceBid = (Button) view.findViewById(R.id.btnPlaceBid);
        btnAutoBid = (Button) view.findViewById(R.id.btnAutoBid);
        btnBuyNow = (Button) view.findViewById(R.id.btnBuyNow);
        hlvCarImages = (HorizontalListView) view.findViewById(R.id.hlvCarImages);
        hlvCarImages.setOnItemClickListener(this);
        rlMessage = (RelativeLayout) view.findViewById(R.id.rlMessage);
        llMessage = (LinearLayout) view.findViewById(R.id.llMessage);
        rlNoRating = (RelativeLayout) view.findViewById(R.id.rlNoRating);
        rlRating = (RelativeLayout) view.findViewById(R.id.rlRating);
        llRating = (LinearLayout) view.findViewById(R.id.llRating);
        rlMessage.setOnClickListener(this);
        rlRating.setOnClickListener(this);
        trAutoBid = (TableRow) view.findViewById(R.id.trAutoBid);
        trBuyNow = (TableRow) view.findViewById(R.id.trBuyNow);
        trBuyNow.setVisibility(View.GONE);
        tvMyBid = (TextView) view.findViewById(R.id.tvMyBidItemBuyList);
        tvMyBidStatus = (TextView) view.findViewById(R.id.tvBidStatusItemBuyList);
        ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
        ivArrowIconRating = (ImageView) view.findViewById(R.id.ivArrowIconRating);

        llAutoBid = (LinearLayout) view.findViewById(R.id.llAutoBid);
        tvAutoBidding = (TextView) view.findViewById(R.id.tvAutoBid);
        tvtitleMinBid = (TextView) view.findViewById(R.id.tvtitleMinBid);
        edAutoBid = (EditText) view.findViewById(R.id.edAutoBidLimit);
        btnAutoCancel = (Button) view.findViewById(R.id.btnAutoCancel);
        btnAutoActivate = (Button) view.findViewById(R.id.btnAutoActivate);
        bnSubmitMessage = (Button) view.findViewById(R.id.bnSubmitMessage);
        bnSubmitMessage.setOnClickListener(this);
        btnAutoCancel.setOnClickListener(this);
        btnAutoActivate.setOnClickListener(this);
        btnPlaceBid.setOnClickListener(this);
        btnAutoBid.setOnClickListener(this);
        btnBuyNow.setOnClickListener(this);
        ivCar.setOnClickListener(this);
        intrUpdateList.updateVehicleList(false);
    }

    // function puts values from vehicle object in views
    // function set price values in Rand format Helper methods are used
    private void putValues()
    {
        showActionBar(vehicle.getFriendlyName());
        if (vehicle.getImageList() != null)
        {
            if (vehicle.getImageList() != null && !vehicle.getImageList().isEmpty())
            {

                if (vehicle.getImageList().get(0).getThumb() != null)
                {
                    ivCar.setImageUrl(vehicle.getImageList().get(0).getThumb() + "width=200", imageLoader);
                }
            } else
            {
                ivCar.setImageResource(R.drawable.noimage);
            }
            if (vehicle.getImageList().size() == 1 || vehicle.getImageList().size() == 0)
            {
                hlvCarImages.setVisibility(View.GONE);
            }
        }
        ivCar.setDefaultImageResId(R.drawable.noimage);
        ivCar.setErrorImageResId(R.drawable.noimage);

        tempList = new ArrayList<MyImage>();
        tempList.addAll(vehicle.getImageList());
        if (!tempList.isEmpty())
            tempList.remove(0);
        adapter = new HorizontalListViewAdapter(getActivity(), tempList);
        hlvCarImages.setAdapter(adapter);
        tvTitle.setText(Html.fromHtml("<font color=#ffffff>" + vehicle.getYear() + "</font> <font color=" + getResources().getColor(R.color.dark_blue) + ">" + vehicle.getFriendlyName() + "</font>"));

        tvMileage.setText(Helper.getFormattedDistance(vehicle.getMileage() + "") + "Km");
        tvMileage.setText(vehicle.getColour());
        tvLocation.setText(vehicle.getLocation());
        /*
         * if(vehicle.getHightestBid()>vehicle.getRetailPrice())
		 * tvTradePrice.setText(Helper.formatPrice(new
		 * BigDecimal(vehicle.getHightestBid())+"")); else
		 * tvTradePrice.setText(Helper.formatPrice(new
		 * BigDecimal(vehicle.getRetailPrice())+""));
		 */

        if ((int) vehicle.getBuyNow() > 0)
            ivRibbon.setVisibility(View.VISIBLE);
        tvTimeLeft.setText(Helper.formatDate(vehicle.getTimeLeft()));
        //	tvhighest_bid_so_far.setText(Helper.formatPrice(new BigDecimal(vehicle.getMyHighestBid()) + ""));
        if (vehicle.getHightestBid() != 0)
            tvhighest_bid_so_far.setText(Helper.formatPrice(new BigDecimal(vehicle.getHightestBid()) + ""));
        else
            tvhighest_bid_so_far.setText("None");

        if (vehicle.getMyHighestBid() == 0)
        {
            tvMyBid.setText("My Bid: None Yet");
            tvMyBidStatus.setVisibility(View.INVISIBLE);
        } else if (vehicle.getMyHighestBid() >= vehicle.getHightestBid())
        {
            tvMyBidStatus.setVisibility(View.VISIBLE);
            tvMyBid.setText("My Bid: " + Helper.formatPrice(new BigDecimal(vehicle.getMyHighestBid()) + ""));
            tvMyBid.setTextColor(getResources().getColor(R.color.violet));
            tvMyBidStatus.setText("Winning");
            tvMyBidStatus.setTextColor(getResources().getColor(R.color.green));
            tvtitleMinBid.setVisibility(View.INVISIBLE);
            tvTradePrice.setVisibility(View.INVISIBLE);
        } else
        {
            tvMyBidStatus.setVisibility(View.VISIBLE);
            tvMyBid.setText("My Bid: " + Helper.formatPrice(new BigDecimal(vehicle.getMyHighestBid()) + ""));
            tvMyBidStatus.setText("Beaten");
            tvMyBid.setTextColor(getResources().getColor(R.color.violet));
            tvMyBidStatus.setTextColor(getResources().getColor(R.color.red));
        }
        // tvMinBid.setText(Helper.formatPrice(new
        // BigDecimal(vehicle.getMinBid())+""));
        tvIncrement.setText(Helper.formatPrice(new BigDecimal(vehicle.getIncrement()) + ""));
        tvBuyNowPrice.setText(Helper.formatPrice(new BigDecimal(vehicle.getBuyNow()) + ""));
        tvBiddingClosingDate.setText(vehicle.getExpires());
        tvOwnerName.setText("Seller: " + vehicle.getOwnerName() + ", " + vehicle.getLocation());
        tvComments.setText(vehicle.getComments());
        tvExtras.setText(vehicle.getExtras());
        tvStockNo.setText(vehicle.getStockNumber());
        tvRegNo.setText(vehicle.getRegNumber());
        tvVinNo.setText(vehicle.getVIN());
        if (vehicle.getMinBid() == 0)
        {
            edNextBid.setText(Helper.formatPrice(new BigDecimal(vehicle.getTradePrice()) + ""));
            tvMinBid.setText(Helper.formatPrice(new BigDecimal(vehicle.getTradePrice()) + ""));
            tvTradePrice.setText(Helper.formatPrice(new BigDecimal(vehicle.getTradePrice()) + ""));
        } else
        {
            edNextBid.setText(Helper.formatPrice(new BigDecimal(vehicle.getMinBid()) + ""));
            tvMinBid.setText(Helper.formatPrice(new BigDecimal(vehicle.getMinBid()) + ""));
            tvTradePrice.setText(Helper.formatPrice(new BigDecimal(vehicle.getMinBid()) + ""));
        }
        // edNextBid.setText(Helper.formatPrice(new
        // BigDecimal(vehicle.getMinBid())+""));
        btnBuyNow.setText("Buy Now For " + Helper.formatPrice(new BigDecimal(vehicle.getBuyNow()) + ""));

        if ((int) vehicle.getBuyNow() == 0)
        {
            btnBuyNow.setVisibility(View.GONE);
            // trBuyNow.setVisibility(View.GONE);
        } else
        {
            btnBuyNow.setVisibility(View.VISIBLE);
            // trBuyNow.setVisibility(View.VISIBLE);
        }
        if (vehicle.getMinBid() == 0)
        {
            edNextBid.setVisibility(View.GONE);
            btnPlaceBid.setVisibility(View.GONE);
            tvMinBid.setVisibility(View.GONE);
        } else
        {
            edNextBid.setVisibility(View.VISIBLE);
            btnPlaceBid.setVisibility(View.VISIBLE);
            tvMinBid.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(vehicle.getExpires()))
        {
            tvBiddingClosingDate.setVisibility(View.GONE);
        } else
        {
            tvBiddingClosingDate.setVisibility(View.VISIBLE);
        }
        if (isAutoBiddingSet)
        {
            btnAutoActivate.setText("Amend");
            btnAutoCancel.setText(Html.fromHtml("<font color=" + getResources().getColor(R.color.red) + ">Disable</font>"));
            putAmendText();
            trAutoBid.setVisibility(View.GONE);
        } else
        {
            trAutoBid.setVisibility(View.VISIBLE);
            putAutobidText();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnPlaceBid:
                if (HelperHttp.isNetworkAvailable(getActivity()))
                    try
                    {
                        CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.doyouwanttoplacebid), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (type == DialogInterface.BUTTON_POSITIVE)
                                {
                                    if (!edNextBid.getText().toString().trim().equals("")
                                            && Integer.parseInt(Helper.formatPriceToDefault(edNextBid.getText().toString().trim()).replace(".0", "")) != 0)
                                        placeBid();
                                }
                            }
                        });

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                else
                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                break;

            case R.id.btnAutoBid:

                if (llAutoBid.getVisibility() == View.GONE)
                    llAutoBid.setVisibility(View.VISIBLE);
                else
                    llAutoBid.setVisibility(View.GONE);

                if (isAutoBiddingSet)
                {
                    btnAutoActivate.setText("Amend");
                    btnAutoCancel.setText(Html.fromHtml("<font color=" + getResources().getColor(R.color.red) + ">Disable</font>"));
                    try
                    {
                        putAmendText();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    trAutoBid.setVisibility(View.GONE);
                } else
                {
                    trAutoBid.setVisibility(View.VISIBLE);
                    try
                    {
                        putAutobidText();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.btnBuyNow:
                CustomDialogManager.showOkCancelDialog(getActivity(), "Do you want to purchase the vehicle at the buy now price of " + Helper.formatPrice(new BigDecimal(vehicle.getBuyNow()) + "")
                        + "?", new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        if (type == DialogInterface.BUTTON_POSITIVE)
                        {
                            buyNow();
                        }
                    }
                });

                break;

            case R.id.ivItemBuyList:
                navigateToLargeImage(0, "image");
                break;

            case R.id.bnSubmitMessage:
                if (TextUtils.isEmpty(edMessageInput.getText().toString().trim()))
                {
                    Helper.showToast("Please add message for vehicle", getActivity());
                    return;
                } else
                {
                    addMessageForVehicle();

                }
                break;

            case R.id.rlMessage:

                if (llRating.getVisibility() == View.VISIBLE)
                {
                    llRating.setVisibility(View.GONE);
                    ivArrowIconRating.setRotation(0);
                }

                if (llMessage.getVisibility() != View.GONE)
                {
                    llMessage.setVisibility(View.GONE);
                    ivArrowIcon.setRotation(0);
                } else
                {
                    llMessage.setVisibility(View.VISIBLE);
                    ivArrowIcon.setRotation(90);
                }
                break;

            case R.id.rlRating:

                if (llMessage.getVisibility() == View.VISIBLE)
                {
                    llMessage.setVisibility(View.GONE);
                    ivArrowIcon.setRotation(0);
                }

                if (llRating.getVisibility() != View.GONE)
                {
                    llRating.setVisibility(View.GONE);
                    ivArrowIconRating.setRotation(0);
                } else
                {
                    llRating.setVisibility(View.VISIBLE);
                    ivArrowIconRating.setRotation(90);
                }
                break;

            case R.id.btnAutoActivate:
                try
                {
                    if (btnAutoActivate.getText().toString().trim().equals(getString(R.string.activate)))
                    {
                        // auto bid logic
                        if (!edAutoBid.getText().toString().trim().equals("") && Integer.parseInt(Helper.formatPriceToDefault(edAutoBid.getText().toString().trim()).replace(".0", "")) != 0)
                        {
                            CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.doyouwanttosetautobid), new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    if (type == DialogInterface.BUTTON_POSITIVE)
                                    {
                                        setAutomatedBidding(Integer.parseInt(Helper.formatPriceToDefault(edAutoBid.getText().toString().trim())));
                                    }
                                }
                            });
                        } else
                            Helper.showToast(getString(R.string.enter_valid_bid_limit), getActivity());

                    } else
                    {
                        // Amend logic
                        if (trAutoBid.getVisibility() == View.GONE)
                        {
                            putAutobidText();
                            trAutoBid.setVisibility(View.VISIBLE);
                        } else if (Integer.parseInt(Helper.formatPriceToDefault(edAutoBid.getText().toString().trim()).replace(".0", "")) != 0 && !edAutoBid.getText().toString().equals(""))
                            setAutomatedBidding(Integer.parseInt(Helper.formatPriceToDefault(edAutoBid.getText().toString().trim()).replace("R", "").replace(".0", "")));
                        else
                            Helper.showToast(getString(R.string.entervalidamount), getActivity());
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case R.id.btnAutoCancel:
                if (btnAutoCancel.getText().toString().equals(getString(R.string.cancel)))
                    llAutoBid.setVisibility(View.GONE);
                else
                {
                    // Disable logic
                    disableAutoBid();
                    isAutoBiddingSet = false;
                    if (trAutoBid.getVisibility() == View.GONE)
                        trAutoBid.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void addMessageForVehicle()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", vehicle.getID(), Integer.class));
            parameterList.add(new Parameter("message", edMessageInput.getText().toString().trim(), String.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("AddMessageToVehicle");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/AddMessageToVehicle");
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
                        questionsList = new ArrayList<MessageListing>();
                        Helper.Log("AddMessageToVehicle Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        // SoapObject outerRating = (SoapObject)
                        // outer.getPropertySafely("SUCCESS");
                        if (outer.toString().contains("SUCCESS"))
                        {
                            messageList.add(new MessageListing(DataManager.getInstance().user.getName() + " " + DataManager.getInstance().user.getSurName(), "Just Now", edMessageInput.getText()
                                    .toString().trim()));
                            messageListingAdapter.notifyDataSetChanged();
                            edMessageInput.setText("");
                            tvHeaderCount1.setText("" + messageList.size());
                        }
                        CustomDialogManager.showOkDialog(getActivity(), "Message submitted successfully");
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        navigateToLargeImage(position + 1, "list");
    }

    // function starts new ImageDetailFragment and set required arguments to it.
    @SuppressWarnings("unchecked")
    private void navigateToLargeImage(int position, String from)
    {
        Fragment f = new ImageDetailFragment();
        Bundle args = new Bundle();
        ArrayList<MyImage> list = (ArrayList<MyImage>) vehicle.getImageList().clone();
        args.putParcelableArrayList("imagelist", list);
        args.putInt("index", position);
        args.putString("vehicleName", vehicle.getFriendlyName());
        args.putString("from", from);
        f.setArguments(args);
        getFragmentManager().beginTransaction().replace(this.getId(), f, "imageDetailFragment").addToBackStack("detail").commit();
    }

    private void getSellerRating()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("sellerClientID", vehicle.getOwnerID(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetRatingForSeller");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetRatingForSeller");
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
                        reviewlist = new ArrayList<MessageListing>();
                        Helper.Log("GetRatingForSeller Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject outerRating = (SoapObject) outer.getPropertySafely("Rating");
                        float ratingValue = Float.parseFloat(outerRating.getPropertySafelyAsString("Value", ""));
                        String Reviews = outerRating.getPropertySafelyAsString("Reviews", "");
                        tvReviewCount.setText(Reviews + " Reviews");
                        if (ratingValue == -1)
                        {
                            rlNoRating.setVisibility(View.VISIBLE);
                            rlRating.setVisibility(View.GONE);
                            return;
                        } else
                        {
                            rlNoRating.setVisibility(View.GONE);
                            rlRating.setVisibility(View.VISIBLE);
                        }
                        ratingBar.setRating(ratingValue);
                        SoapObject inner = (SoapObject) outerRating.getPropertySafely("Questions");
                        MessageListing messageListing;
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            if (inner.getProperty(i) instanceof SoapObject)
                            {
                                SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
                                messageListing = new MessageListing();
                                messageListing.setName(vehicleObj.getPropertySafelyAsString("Name", ""));
                                if (i == 0)
                                {
                                    tvPriceChange.setText(vehicleObj.getPropertySafelyAsString("Value", "") + "/12");
                                } else if (i == 1)
                                {
                                    tvVehicleDescribed.setText(vehicleObj.getPropertySafelyAsString("Value", "") + "/12");
                                } else if (i == 2)
                                {
                                    tvVehicleDispatched.setText(vehicleObj.getPropertySafelyAsString("Value", "") + "/12");
                                }
                                reviewlist.add(messageListing);
                            }
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        rlNoRating.setVisibility(View.VISIBLE);
                        rlRating.setVisibility(View.GONE);
                    }

                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void ListMessagesForVehicleList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", vehicle.getID(), Integer.class));

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
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Messages");
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
						/*
						 * if (messageListingAdapter == null) {
						 */
                        tvHeaderCount1.setText("" + messageList.size());
                        messageListingAdapter = new MessageListingAdapter(getActivity(), R.layout.single_item_seller_message, messageList, "Message");
                        slvMessages.setAdapter(messageListingAdapter);
                        // }
						/*
						 * else { messageListingAdapter.notifyDataSetChanged();
						 * }
						 */
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

    // function contains web service call to place bid to a vehicle
    private void placeBid()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("UserID", DataManager.getInstance().user.getIdenttity(), Integer.class));
            parameterList.add(new Parameter("Vehicle", vehicle.getID(), Integer.class));
            parameterList.add(new Parameter("Amount", (int) Double.parseDouble(Helper.formatPriceToDefault(edNextBid.getText().toString().trim())), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("Bid");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/Bid");
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
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getProperty(0);
                        String msg = inner.getPropertyAsString("Status");
                        String fullMessage = inner.getPropertySafelyAsString("Reason", "");
                        // String Reason= inner.getPropertyAsString("Reason");
                        Helper.Log("status", msg);
                        intrUpdateList.updateVehicleList(true);
                        if (msg.equalsIgnoreCase("Ok"))
                        {
                            // btnAutoBid.setEnabled(false);
                            btnPlaceBid.setEnabled(false);
                            // btnBuyNow.setEnabled(false);

                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.placebidmessage), new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    loadVehicle();
                                }
                            });
                        } else if (msg.contains("You are already the highest bidder"))
                        {
                            CustomDialogManager.showOkDialog(getActivity(), msg);
                        } else if (fullMessage.equalsIgnoreCase("You are not authorised to access this."))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "You are not authorised to access this.");
                        } else if (fullMessage.equalsIgnoreCase("You are not authorized to bid on vehicles"))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "You are not authorized to bid on vehicles");
                        } else if (fullMessage.equalsIgnoreCase("Unknown Error"))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "Your offer did not go through. Please try again. If this issue persists \n please contact");
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), fullMessage);
                            // CustomDialogManager.showOkDialog(getActivity(),
                            // msg);
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

    // function contains web service call for buy now
    private void buyNow()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("UserID", DataManager.getInstance().user.getIdenttity(), Integer.class));
            parameterList.add(new Parameter("Vehicle", vehicle.getID(), Integer.class));
            parameterList.add(new Parameter("Amount", (int) Double.parseDouble(vehicle.getBuyNow() + ""), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("BuyNow");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/BuyNow");
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
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getProperty(0);

                        String msg = inner.getPropertySafelyAsString("Status", "");
                        String fullMessage = inner.getPropertySafelyAsString("Reason", "");

                        if (msg.equalsIgnoreCase("ok"))
                        {
							/*
							 * btnAutoBid.setEnabled(false);
							 * btnPlaceBid.setEnabled(false);
							 * btnBuyNow.setEnabled(false);
							 * edNextBid.setEnabled(false);
							 */
                            intrUpdateList.updateVehicleList(true);
                            CustomDialogManager.showOkDialog(getActivity(), fullMessage, new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    getActivity().getFragmentManager().popBackStack();
                                }
                            });

                        } else if (fullMessage.equalsIgnoreCase("You are already the highest bidder."))
                        {
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.highest_bider));
                        } else if (fullMessage.equalsIgnoreCase("You are not authorised to access this."))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "You are not authorised to access this.");
                        } else if (fullMessage.equalsIgnoreCase("You are not authorized to bid on vehicles"))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "You are not authorized to bid on vehicles");
                        } else if (fullMessage.equalsIgnoreCase("Unknown Error"))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "Your offer did not go through. Please try again. If this issue persists \n please contact");
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), fullMessage);
                            // CustomDialogManager.showOkDialog(getActivity(),
                            // msg);
                        }

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

    // function contains web service call to set auto bidding on vehicle
    private void setAutomatedBidding(int bidLimit)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("ClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("UserID", DataManager.getInstance().user.getIdenttity(), Integer.class));
            parameterList.add(new Parameter("Vehicle", vehicle.getID(), Integer.class));
            parameterList.add(new Parameter("Amount", ((int) Double.parseDouble(vehicle.getMinBid() + "")), Double.class));
            parameterList.add(new Parameter("Limit", (int) Double.parseDouble(bidLimit + ""), Double.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("AutoBid");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/AutoBid");
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
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getProperty(0);
                        btnAutoBid.performClick();
                        String msg = inner.getPropertySafelyAsString("Status", "");
                        String fullMessage = inner.getPropertySafelyAsString("Reason", "");
                        // String Reason = inner.getPropertyAsString("Reason");
                        if (msg.equalsIgnoreCase("ok"))
                        {
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.amendmessage), new DialogListener()
                            {

                                @Override
                                public void onButtonClicked(int type)
                                {
                                    loadVehicle();
                                }
                            });

                            btnAutoActivate.setText("Amend");
                            trAutoBid.setVisibility(View.GONE);
                            btnAutoCancel.setText(Html.fromHtml("<font color=" + getResources().getColor(R.color.red) + ">Disable</font>"));
                            putAmendText();
                        } else if (fullMessage.equalsIgnoreCase("You are not authorised to access this."))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "You are not authorised to access this.");
                        } else if (fullMessage.equalsIgnoreCase("You are not authorized to bid on vehicles"))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "You are not authorized to bid on vehicles");
                        } else if (fullMessage.equalsIgnoreCase("Unknown Error"))
                        {
                            CustomDialogManager.showErrorDialogEmail(getActivity(), "Your offer did not go through. Please try again. If this issue persists \n please contact");
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), fullMessage);
                            // CustomDialogManager.showOkDialog(getActivity(),
                            // msg);
                        }

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
                        String ErrorMessage = obj.getPropertySafelyAsString("Error", "");
                        if (!ErrorMessage.equalsIgnoreCase("You are not set up to see this vehicle"))
                        {
                            SoapObject vehicleObj = (SoapObject) obj
                                    .getPropertySafely("Vehicle", "default");
                            vehicle.setID(Integer.parseInt(vehicleObj
                                    .getPropertySafelyAsString("ID", "")));
                            vehicle.setOwnerID(Integer.parseInt(vehicleObj
                                    .getPropertySafelyAsString("OwnerID", "")));
                            vehicle.setOwnerName(vehicleObj
                                    .getPropertySafelyAsString("OwnerName", ""));
                            vehicle.setYear(Integer.parseInt(vehicleObj
                                    .getPropertySafelyAsString("Year", "")));
                            vehicle.setFriendlyName(vehicleObj
                                    .getPropertySafelyAsString("FriendlyName",
                                            ""));
                            vehicle.setMileage(Integer.parseInt(vehicleObj
                                    .getPropertySafelyAsString("Mileage", "")));
                            vehicle.setMileageType(vehicleObj
                                    .getPropertySafelyAsString("MileageType",
                                            ""));
                            vehicle.setColour(vehicleObj
                                    .getPropertySafelyAsString("Colour", ""));
                            vehicle.setLocation(vehicleObj
                                    .getPropertySafelyAsString("Location", ""));
                            if (vehicleObj.getPropertyAsString("TradePrice") != null)
                            {
                                vehicle.setRetailPrice(Float
                                        .parseFloat(vehicleObj
                                                .getPropertySafelyAsString(
                                                        "TradePrice", "")));
                                vehicle.setBuyNow(true);
                            } else
                            {
                                vehicle.setRetailPrice(Float
                                        .parseFloat(vehicleObj
                                                .getPropertySafelyAsString(
                                                        "TradePrice", "")));
                                vehicle.setBuyNow(false);
                            }
                            vehicle.setCount(Integer.parseInt(vehicleObj
                                    .getPropertySafelyAsString("Count", "")));
                            vehicle.setExpires(vehicleObj
                                    .getPropertySafelyAsString("Expires", ""));
                            vehicle.setTimeLeft(vehicleObj
                                    .getPropertySafelyAsString("TimeLeft", ""));
                            vehicle.setBuyNow(Float.parseFloat(vehicleObj
                                    .getPropertySafelyAsString("BuyNow", "")));
                            vehicle.setMinBid(Float.parseFloat(vehicleObj
                                    .getPropertySafelyAsString("MinBid", "")));
                            vehicle.setMyHighestBid(Float.parseFloat(vehicleObj
                                    .getPropertySafelyAsString("MyHighestBid",
                                            "")));
                            vehicle.setHightestBid(Float.parseFloat(vehicleObj
                                    .getPropertySafelyAsString("HightestBid",
                                            "")));
                            vehicle.setIncrement(Float.parseFloat(vehicleObj
                                    .getPropertySafelyAsString("Increment", "")));
                            vehicle.setStockNumber(vehicleObj
                                    .getPropertySafelyAsString("StockNumber",
                                            ""));
                            vehicle.setRegNumber(vehicleObj
                                    .getPropertySafelyAsString("RegNumber", ""));
                            vehicle.setVIN(vehicleObj
                                    .getPropertySafelyAsString("VIN", ""));
                            vehicle.setComments(vehicleObj
                                    .getPropertySafelyAsString("Comments", ""));
                            vehicle.setExtras(vehicleObj
                                    .getPropertySafelyAsString("Extras", ""));
                            Helper.Log(
                                    "TAG",
                                    ""
                                            + vehicleObj
                                            .getPropertySafelyAsString(
                                                    "AutobidCap", ""),
                                    false);
                            if (!vehicleObj.getPropertySafelyAsString(
                                    "AutobidCap", "").equals("0"))
                            {
                                isAutoBiddingSet = true;
                                edAutoBid.setText(Helper.formatPrice(vehicleObj
                                        .getPropertySafelyAsString(
                                                "AutobidCap", "")));
                            } else
                            {
                                isAutoBiddingSet = false;
                            }
                            SoapObject imageObj = (SoapObject) vehicleObj
                                    .getPropertySafely("Images");
                            MyImage image;
                            ArrayList<MyImage> tImageList = new ArrayList<MyImage>();
                            for (int j = 0; j < imageObj.getPropertyCount(); j++)
                            {

                                if (imageObj.getProperty(j) instanceof SoapObject)
                                {
                                    image = new MyImage();
                                    SoapObject object = (SoapObject) imageObj
                                            .getProperty(j);
                                    image.setFull(object
                                            .getPropertySafelyAsString("Full",
                                                    ""));
                                    image.setThumb(object
                                            .getPropertySafelyAsString("Thumb",
                                                    ""));

                                    tImageList.add(j, image);
                                }
                            }
                            vehicle.setImageList(tImageList);
                            putValues();
                            getSellerRating();
                            ListMessagesForVehicleList();
                        } else
                        {
                            CustomDialogManager.showOkDialog(getActivity(), ErrorMessage, new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    getActivity().getFragmentManager().popBackStack();
                                }
                            });
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

    private void disableAutoBid()
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
            inObj.setMethodname("RemoveAutoBids");
            inObj.setNamespace(Constants.TRADER_NAMESPACE);
            inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/RemoveAutoBids");
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
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getProperty(0);

                        String msg = inner.getPropertySafelyAsString("Status", "");
                        if (msg.equalsIgnoreCase("ok"))
                        {
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.disableautobidmessage));
                            btnAutoCancel.setText(getString(R.string.cancel));
                            btnAutoCancel.setTextColor(Color.WHITE);
                            btnAutoActivate.setText(getString(R.string.activate));
                            edAutoBid.setText("");
                            putAutobidText();
                        } else
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

    private void putAmendText()
    {
        final SpannableStringBuilder sb = new SpannableStringBuilder("Automated bidding active: Started at " + Helper.formatPrice(vehicle.getMinBid() + "") + " with increment of "
                + Helper.formatPrice(vehicle.getIncrement() + "") + " max bid " + edAutoBid.getText().toString() + ". Current bid " + Helper.formatPrice(vehicle.getMinBid() + ""));
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(bss, 0, 26, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tvAutoBidding.setText(sb);
    }

    private void putAutobidText()
    {
        tvAutoBidding.setText("Start Automated Bidding at " + Helper.formatPrice(new BigDecimal(vehicle.getMinBid()) + "") + " " + "with increment of "
                + Helper.formatPrice(new BigDecimal(vehicle.getIncrement()) + ""));
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
        // no subtitle to actionbar in detail screen
        //getActivity().getActionBar().setSubtitle(null);
    }

    public interface IntrUpdateList
    {
        public void updateVehicleList(boolean flag);
    }

	/*
	 * ShowcaseLayout showcaseView; private void displayShowcaseView(){
	 * if(!ShowcaseSessions.isSessionAvailable(getActivity(),
	 * BuyDetailFragment.class.getSimpleName())){ ArrayList<TargetView>
	 * viewList= new ArrayList<TargetView>(); viewList.add(new
	 * TargetView(ivCar,ShowCaseType.Left,
	 * getString(R.string.to_view_large_image_tap_on_the_image))); showcaseView
	 * = new ShowcaseLayout(getActivity());
	 * showcaseView.setShowcaseView(viewList);
	 * 
	 * ((ViewGroup)getActivity().getWindow().getDecorView()).addView(showcaseView
	 * ); ShowcaseSessions.saveSession(getActivity(),
	 * BuyDetailFragment.class.getSimpleName()); } }
	 */
}
