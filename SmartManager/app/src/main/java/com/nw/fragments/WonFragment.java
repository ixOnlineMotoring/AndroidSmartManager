package com.nw.fragments;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.adapters.HorizontalListViewAdapter;
import com.nw.adapters.MessageListingAdapter;
import com.nw.adapters.RateBuyersAdapter;
import com.nw.model.DataInObject;
import com.nw.model.MessageListing;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.RattingQuestions;
import com.nw.model.SmartObject;
import com.nw.model.Vehicle;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class WonFragment extends BaseFragement implements OnClickListener, OnItemClickListener
{
	StaticListView lvRateBuyers, lvMessage;
	ArrayList<SmartObject> listBuyerRatingQuestionslist;
	ArrayList<MessageListing> messageList;
	ArrayList<RattingQuestions> rattingquestionsList;
	RateBuyersAdapter rateBuyersAdapter;
	MessageListingAdapter messageListingAdapter;
	int intUservehicleStockId, intOfferID, intRateCounter;
	TextView tvHeaderCount1;
	Vehicle vehicle;
	LinearLayout llRatingSeller;
	HorizontalListView hlvCarImages;
	HorizontalListViewAdapter adapter;
	NetworkImageView ivCar;
	ImageView ivRibbon;
	TextView tvTitle, tvMileage, tvColor, tvLocation;
	TextView tvOwnerName, tvStockNo, tvRegNo, tvVinNo, tvComments, tvExtras, tvMyBid, tvMyBidStatus;
	RelativeLayout rlMessage, rlrating;
	Button btRating;
	ImageLoader imageLoader;
	ArrayList<MyImage> tempList;
	int bidLimit, vehicleID;
//	private boolean isAutoBiddingSet = false;
	TableRow trAutoBid, trBuyNow;
	ImageView ivArrowIcon, ivArrowIconRating;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_won, container, false);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		imageLoader = VolleySingleton.getInstance().getImageLoader();

		intUservehicleStockId = getArguments().getInt("UsedVehicleStockID");
		intOfferID = getArguments().getInt("OfferID");

		// ListView
		lvRateBuyers = (StaticListView) view.findViewById(R.id.lvRateBuyers);
		lvMessage = (StaticListView) view.findViewById(R.id.lvMessage);

		// TextView
		tvHeaderCount1 = (TextView) view.findViewById(R.id.tvHeaderCount1);

		listBuyerRatingQuestionsResponse();
		ListMessagesForVehicleList();

		// Views initialisation
		ivCar = (NetworkImageView) view.findViewById(R.id.ivItemBuyList);
		ivRibbon = (ImageView) view.findViewById(R.id.ivRibbon);
		tvTitle = (TextView) view.findViewById(R.id.tvTitleItemBuyList);
		tvTitle.setTextColor(getResources().getColor(R.color.dark_blue));
		tvMileage = (TextView) view.findViewById(R.id.tvDistanceItemBuyList);
		tvColor = (TextView) view.findViewById(R.id.tvColorItemBuyList);
		tvLocation = (TextView) view.findViewById(R.id.tvLocationItemBuyList);
		// tvTradePrice.setTextColor(getResources().getColor(R.color.green));
		tvOwnerName = (TextView) view.findViewById(R.id.tvOwnerName);
		tvStockNo = (TextView) view.findViewById(R.id.tvStockNumber);
		tvRegNo = (TextView) view.findViewById(R.id.tvRegNumber);
		tvVinNo = (TextView) view.findViewById(R.id.tvVinNumber);
		tvComments = (TextView) view.findViewById(R.id.tvComments);
		tvExtras = (TextView) view.findViewById(R.id.tvExtras);
		hlvCarImages = (HorizontalListView) view.findViewById(R.id.hlvCarImages);
		hlvCarImages.setOnItemClickListener(this);
		trAutoBid = (TableRow) view.findViewById(R.id.trAutoBid);
		trBuyNow = (TableRow) view.findViewById(R.id.trBuyNow);
		tvMyBid = (TextView) view.findViewById(R.id.tvMyBidItemBuyList);
		tvMyBidStatus = (TextView) view.findViewById(R.id.tvBidStatusItemBuyList);

		ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
		ivArrowIconRating = (ImageView) view.findViewById(R.id.ivArrowIconRating);

		rlMessage = (RelativeLayout) view.findViewById(R.id.rlMessage);
		rlMessage.setOnClickListener(this);
		rlrating = (RelativeLayout) view.findViewById(R.id.rlrating);
		rlrating.setOnClickListener(this);

		btRating = (Button) view.findViewById(R.id.btRating);
		
		llRatingSeller  = (LinearLayout) view.findViewById(R.id.llRatingSeller);
		ivCar.setOnClickListener(this);
		btRating.setOnClickListener(this);

		tvMyBidStatus.setText(Helper.formatPrice(String.valueOf(getArguments().getDouble("Amount"))));
		loadVehicle();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Won");
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

	private void listBuyerRatingQuestionsResponse()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			showProgressDialog();
			StringBuilder soapMessage = new StringBuilder();
			soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			soapMessage.append("<Body>");
			soapMessage.append("<ListSellerRatingQuestions xmlns=\"" + Constants.TRADER_NAMESPACE + "\">");
			soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
			soapMessage.append("</ListSellerRatingQuestions>");
			soapMessage.append("</Body>");
			soapMessage.append("</Envelope>");

			Helper.Log("ListBuyerRatingQuestions request:", soapMessage.toString());

			VollyResponseListener listener = new VollyResponseListener()
			{

				@Override
				public void onErrorResponse(VolleyError error)
				{
					hideProgressDialog();
					Helper.Log("ListSellerRatingQuestions Error: ", error.getMessage());
				}

				@Override
				public void onResponse(String response)
				{
					hideProgressDialog();
					if (response == null)
					{
						return; 
					}
					Helper.Log("ListSellerRatingQuestions Response:", response);
					listBuyerRatingQuestionslist = new ArrayList<SmartObject>();
					listBuyerRatingQuestionslist = ParserManager.listBuyerRatingQuestionsResponse(response);
					hideProgressDialog();
				}
			};

			VollyCustomRequest request = new VollyCustomRequest(Constants.TRADER_WEBSERVICE_URL, soapMessage.toString(), Constants.TRADER_NAMESPACE + "/ITradeService/ListSellerRatingQuestions",
					listener);

			try
			{
				request.init("ListBuyerRatingQuestions");
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		else
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
			parameterList.add(new Parameter("usedVehicleStockID", intUservehicleStockId, Integer.class));

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
							/*
							 * else { SoapPrimitive p = (SoapPrimitive)
							 * inner.getProperty(i); String total =
							 * p.getValue().toString(); total_no_of_records =
							 * Integer.parseInt(total); Helper.Log("Total",
							 * total); }
							 */
						}

						// if (messageListingAdapter == null)
						// {
						tvHeaderCount1.setText("" + messageList.size());
						messageListingAdapter = new MessageListingAdapter(getActivity(), R.layout.single_item_seller_message, messageList, "Message");
						lvMessage.setAdapter(messageListingAdapter);
						// }
						// else
						// {
						// messageListingAdapter.notifyDataSetChanged();
						// }
					} catch (Exception e)
					{
						e.printStackTrace();
					}

				}
			}).execute();
		}
		else
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
			parameterList.add(new Parameter("Vehicle", intUservehicleStockId, Integer.class));

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
							vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
							vehicle.setBuyNow(true);
						}
						else
						{
							vehicle.setRetailPrice(Float.parseFloat(vehicleObj.getPropertySafelyAsString("TradePrice", "")));
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
						/*
						 * if(!vehicleObj.getPropertySafelyAsString("AutobidCap",
						 * "").equals("0")){ isAutoBiddingSet= true;
						 * edAutoBid.setText
						 * (Helper.formatPrice(vehicleObj.getPropertySafelyAsString
						 * ("AutobidCap", ""))); }else{ isAutoBiddingSet= false;
						 * }
						 */
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
						vehicle.setImageList(tImageList);
						putValues();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	// function puts values from vehicle object in views
	// function set price values in Rand format Helper methods are used
	private void putValues()
	{
	//	getActivity().getActionBar().setTitle(vehicle.getFriendlyName()); // set
																			// vehicle
																			// name
																			// to
																			// action
																			// bar

		if (vehicle.getImageList() != null)
		{
			if (vehicle.getImageList() != null && !vehicle.getImageList().isEmpty())
			{

				if (vehicle.getImageList().get(0).getThumb() != null)
				{
					ivCar.setImageUrl(vehicle.getImageList().get(0).getThumb() + "width=200", imageLoader);
				}
			}
			else
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
		tvColor.setText(vehicle.getColour());
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

		/*
		 * if (vehicle.getMyHighestBid() == 0) {
		 * tvMyBid.setText("My Bid: None Yet");
		 * tvMyBidStatus.setVisibility(View.INVISIBLE); } else if
		 * (vehicle.getMyHighestBid() >= vehicle.getHightestBid()) {
		 * tvMyBidStatus.setVisibility(View.VISIBLE); tvMyBid.setText("My Bid: "
		 * + Helper.formatPrice(new BigDecimal(vehicle.getMyHighestBid()) +
		 * "")); tvMyBid.setTextColor(getResources().getColor(R.color.violet));
		 * tvMyBidStatus.setText("Winning");
		 * tvMyBidStatus.setTextColor(getResources().getColor(R.color.green)); }
		 * else { tvMyBidStatus.setVisibility(View.VISIBLE);
		 * tvMyBid.setText("My Bid: " + Helper.formatPrice(new
		 * BigDecimal(vehicle.getMyHighestBid()) + ""));
		 * tvMyBidStatus.setText("Beaten");
		 * tvMyBid.setTextColor(getResources().getColor(R.color.violet));
		 * tvMyBidStatus.setTextColor(getResources().getColor(R.color.red)); }
		 */
		// tvMinBid.setText(Helper.formatPrice(new
		// BigDecimal(vehicle.getMinBid())+""));
		tvOwnerName.setText("Seller: " + vehicle.getOwnerName() + ", " + vehicle.getLocation());
		tvComments.setText(vehicle.getComments());
		tvExtras.setText(vehicle.getExtras());
		tvStockNo.setText(vehicle.getStockNumber());
		tvRegNo.setText(vehicle.getRegNumber());
		tvVinNo.setText(vehicle.getVIN());

		getVehicleRatting();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.ivItemBuyList:
				navigateToLargeImage(0, "image");
				break;
			case R.id.btRating:
				intRateCounter = 0;
				setRattings(intRateCounter);
				break;

			case R.id.rlMessage:
				if (lvMessage.getVisibility() != View.GONE)
				{
					lvMessage.setVisibility(View.GONE);
					ivArrowIcon.setRotation(0);
				}
				else
				{
					lvMessage.setVisibility(View.VISIBLE);
					ivArrowIcon.setRotation(90);
				}
				break;
			case R.id.rlrating:
				if (llRatingSeller.getVisibility() != View.GONE)
				{
					llRatingSeller.setVisibility(View.GONE);
					ivArrowIconRating.setRotation(0);
				}
				else
				{
					llRatingSeller.setVisibility(View.VISIBLE);
					ivArrowIconRating.setRotation(90);
				}
				break;

			default:
				break;
		}
	}

	private void setRattings(final int intRateCounter)
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			System.out.println("Counter Value : " + intRateCounter);

			View view = lvRateBuyers.getChildAt(intRateCounter);
			CustomEditText tvInputRateBuyers = (CustomEditText) view.findViewById(R.id.tvInputRateBuyers);
			String ratingValueByUser = tvInputRateBuyers.getText().toString();
			if (Integer.parseInt(ratingValueByUser) < 13)
			{
				// Add parameters to request in arraylist
				ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
				parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
				parameterList.add(new Parameter("usedVehicleStockID", intUservehicleStockId, Integer.class));
				parameterList.add(new Parameter("tradeOfferID", intOfferID, Integer.class));
				parameterList.add(new Parameter("coreClientID", vehicle.getOwnerID(), Integer.class));
				parameterList.add(new Parameter("ratingQuestionID", listBuyerRatingQuestionslist.get(intRateCounter).getId(), Integer.class));
				parameterList.add(new Parameter("ratingValue", Integer.parseInt(ratingValueByUser), Integer.class));

				// create web service inputs
				DataInObject inObj = new DataInObject();
				inObj.setMethodname("AddSellerRating");
				inObj.setNamespace(Constants.TRADER_NAMESPACE);
				inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/AddSellerRating");
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
							if (intRateCounter < listBuyerRatingQuestionslist.size()-1)
							{
								int localintcount = intRateCounter;
								localintcount++;
								setRattings(localintcount);
							}
							else
							{
								CustomDialogManager.showOkDialog(getActivity(), "Rating added successfully");
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}

					}
				}).execute();
			}
			else
			{
				Helper.showToast("Please enter value less then 12", getActivity());
			}

		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	private void getVehicleRatting()
	{
		if (HelperHttp.isNetworkAvailable(getActivity()))
		{
			System.out.println("Counter Value : " + intRateCounter);

			// Add parameters to request in arraylist
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("buyerClientID", vehicle.getOwnerID(), Integer.class));
			parameterList.add(new Parameter("stockID", intUservehicleStockId, Integer.class));
			parameterList.add(new Parameter("offerID", intOfferID, Integer.class));
			parameterList.add(new Parameter("ratingClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("ratingMemberID", DataManager.getInstance().user.getMemberId(), Integer.class));

			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("GetRatingQuestionsForSeller");
			inObj.setNamespace(Constants.TRADER_NAMESPACE);
			inObj.setSoapAction(Constants.TRADER_NAMESPACE + "/ITradeService/GetRatingQuestionsForSeller");
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
						rattingquestionsList = new ArrayList<RattingQuestions>();
						Helper.Log("Response", result.toString());
						SoapObject outer = (SoapObject) result;
						SoapObject inner = (SoapObject) outer.getPropertySafely("Questions");
						RattingQuestions rattingQuestions;
						for (int i = 0; i < inner.getPropertyCount(); i++)
						{
							if (inner.getProperty(i) instanceof SoapObject)
							{
								SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
								rattingQuestions = new RattingQuestions();

								rattingQuestions.setName(vehicleObj.getPropertySafelyAsString("Name", ""));
								rattingQuestions.setValue(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Value", "")));
								rattingquestionsList.add(rattingQuestions);
							}
						}

						if (listBuyerRatingQuestionslist != null && listBuyerRatingQuestionslist.size() > 0)
						{
							rateBuyersAdapter = new RateBuyersAdapter(getActivity(), R.layout.list_item_rate_buyers, listBuyerRatingQuestionslist, rattingquestionsList, "won");
							lvRateBuyers.setAdapter(rateBuyersAdapter);
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}

				}
			}).execute();
		}
		else
		{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
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
}
