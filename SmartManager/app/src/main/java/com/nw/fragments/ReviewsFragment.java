package com.nw.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.nw.adapters.ReviewAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.MyImage;
import com.nw.model.Parameter;
import com.nw.model.Review;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class ReviewsFragment extends BaseFragement
{
	ListView lvVariantReview,lvModelReview;
	ReviewAdapter variantReviewAdapter,modelReviewsAdapter;
	ArrayList<Review> variantReviews,modelReviews;
	VehicleDetails vehicleDetails;
	TextView tvVehicleName,tvOtherLine,tvNoModelArticles,tvNoVariantArticles;
	String modelname,makename;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_reviews, container,	false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if(getArguments()!=null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		setHasOptionsMenu(true);
		if (variantReviews==null)
		{
			variantReviews = new ArrayList<Review>();
		}
		if (modelReviews==null)
		{
			modelReviews = new ArrayList<Review>();
		}
		initialise(view);
		if (variantReviews.size()==0 && modelReviews.size()==0)
		{
			getReviewsByVarintID();
		}
		if (vehicleDetails!=null&& variantReviews.size()!=0|| modelReviews.size()!=0)
		{
			putValues();
		}
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Reviews");
	}

	private void initialise(View view)
	{
		lvVariantReview = (ListView) view.findViewById(R.id.lvVariantReview);
		lvModelReview = (ListView) view.findViewById(R.id.lvModelReview);
		tvVehicleName  = (TextView) view.findViewById(R.id.tvVehicleName);
		tvNoModelArticles= (TextView) view.findViewById(R.id.tvNoModelArticles);
		tvNoVariantArticles= (TextView) view.findViewById(R.id.tvNoVariantArticles);
		tvOtherLine = (TextView) view.findViewById(R.id.tvOtherLine);
		variantReviewAdapter = new ReviewAdapter(getActivity(), R.layout.list_item_review, variantReviews);
		lvVariantReview.setAdapter(variantReviewAdapter);
		lvVariantReview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3)
			{
				ReviewDetailsFragment reviewsFragment = new ReviewDetailsFragment();
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				Bundle bundle = new Bundle();
				bundle.putParcelable("reviewDetail", variantReviews.get(position));
				reviewsFragment.setArguments(bundle);
				fragmentTransaction.replace(R.id.Container, reviewsFragment);
				fragmentTransaction.addToBackStack(null).commit();
			}
		});
		modelReviewsAdapter = new ReviewAdapter(getActivity(), R.layout.list_item_review, modelReviews);
		lvModelReview.setAdapter(modelReviewsAdapter);
		lvModelReview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3)
			{
				ReviewDetailsFragment reviewsFragment = new ReviewDetailsFragment();
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				Bundle bundle = new Bundle();
				bundle.putParcelable("reviewDetail", modelReviews.get(position));
				reviewsFragment.setArguments(bundle);
				fragmentTransaction.replace(R.id.Container, reviewsFragment);
				fragmentTransaction.addToBackStack(null).commit();
			}
		});
	}

	private void getReviewsByVarintID()
	{
		showProgressDialog();
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("variantID", vehicleDetails.getVariantID(),Integer.class));
	//		parameterList.add(new Parameter("variantID", 23142,Integer.class));

			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadReviewsForVariantByID");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadReviewsForVariantByID");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			//Network call
			showProgressDialog();
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {

				@Override
				public void onTaskComplete(Object result) 
				{
					try{
						SoapObject outer= (SoapObject) result;
						SoapObject inner= (SoapObject) outer.getPropertySafely("Reviews");
						Review review;
						for(int i=0;i<inner.getPropertyCount();i++)
						{
							SoapObject reviewObj= (SoapObject) inner.getProperty(i);
							review = new Review();
							review.setID(Integer.parseInt(reviewObj.getPropertySafelyAsString("reviewID", "0")));
							review.setTitle(reviewObj.getPropertySafelyAsString("title", "Title?"));
							review.setDate(reviewObj.getPropertySafelyAsString("date", "Date?"));
							review.setType(reviewObj.getPropertySafelyAsString("type", "Type?").trim());
							review.setAuthor(reviewObj.getPropertySafelyAsString("author", "Author?").trim());
							review.setSource(reviewObj.getPropertySafelyAsString("source", "Source?").trim());
							review.setBody(reviewObj.getPropertySafelyAsString("body", "Body?").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&"));
							review.setVehicle_name(vehicleDetails.getFriendlyName());
							review.setYear(vehicleDetails.getYear());
							if (reviewObj.hasProperty("images"))
							{
								SoapObject imgListObj = (SoapObject) reviewObj.getPropertySafely("images");
								ArrayList<MyImage> imageList = new ArrayList<MyImage>();
								MyImage image = null;
								for (int j = 0; j < imgListObj.getPropertyCount(); j++) {
									String  path =  imgListObj.getPropertyAsString(j)+"&width=350";
									image = new MyImage();
									image.setLink(path.replaceAll("&amp;", "&"));
									image.setThumb(path.replaceAll("&amp;", "&"));
									image.setFull(path.replaceAll("&amp;", "&"));
									imageList.add(image);
								}
								review.setImages(imageList);
							}
							variantReviews.add(review);
						}
						variantReviewAdapter.notifyDataSetChanged();
						getReviewsByModelID();
					}catch(Exception e){
						e.printStackTrace();
						getReviewsByModelID();
					}
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}
	
	private void getReviewsByModelID()
	{
		if(HelperHttp.isNetworkAvailable(getActivity())){
			//Add parameters to request in arraylist
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("modelID", vehicleDetails.getModelId(),Integer.class));
			parameterList.add(new Parameter("variantID", vehicleDetails.getVariantID(),Integer.class));
		//	parameterList.add(new Parameter("modelID", 43,Integer.class));
		//	parameterList.add(new Parameter("variantID", 9601,Integer.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("LoadReviewsForModelByIDExcludeVariantByCode");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IStockService/LoadReviewsForModelByIDExcludeVariantByCode");
			inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);

			//Network call
			new WebServiceTask(getActivity(),inObj, false,new TaskListener() {

				@Override
				public void onTaskComplete(Object result) 
				{
					try{
						SoapObject outer= (SoapObject) result;
						SoapObject reviewObject = (SoapObject) outer.getPropertySafely("review");
						modelname = reviewObject.getPropertySafelyAsString("modelName");
						makename = reviewObject.getPropertySafelyAsString("makeName");
						SoapObject inner= (SoapObject) reviewObject.getPropertySafely("reviews");
						Review review;
						for(int i=0;i<inner.getPropertyCount();i++){
							SoapObject reviewObj= (SoapObject) inner.getProperty(i);
							review = new Review();
							review.setID(Integer.parseInt(reviewObj.getPropertySafelyAsString("reviewID", "0")));
							review.setTitle(reviewObj.getPropertySafelyAsString("title", "Title?"));
							review.setDate(reviewObj.getPropertySafelyAsString("date", "Date?"));
							review.setType(reviewObj.getPropertySafelyAsString("type", "Type?").trim());
							review.setAuthor(reviewObj.getPropertySafelyAsString("author", "Author?").trim());
							review.setSource(reviewObj.getPropertySafelyAsString("source", "Source?").trim());
							review.setBody(reviewObj.getPropertySafelyAsString("summary", "Body?").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&"));
							review.setVehicle_name(vehicleDetails.getFriendlyName());
							review.setYear(vehicleDetails.getYear());
							if (reviewObj.hasProperty("images"))
							{
								SoapObject imgListObj = (SoapObject) reviewObj.getPropertySafely("images");
								ArrayList<MyImage> imageList = new ArrayList<MyImage>();
								MyImage image = null;
								for (int j = 0; j < imgListObj.getPropertyCount(); j++) {
									String  path =  imgListObj.getPropertyAsString(j)+"&width=350";
									image = new MyImage();
									image.setLink(path.replaceAll("&amp;", "&"));
									image.setThumb(path.replaceAll("&amp;", "&"));
									image.setFull(path.replaceAll("&amp;", "&"));
									imageList.add(image);
								}
								review.setImages(imageList);
							}
							modelReviews.add(review);
						}
						putValues();
						hideProgressDialog();
					}catch(Exception e){
						e.printStackTrace();
						hideProgressDialog();
						CustomDialogManager.showOkDialog(getActivity(),getActivity().getString(R.string.error_occured_try_later), new DialogListener()
						{
							@Override
							public void onButtonClicked(int type)
							{
								getFragmentManager().popBackStack();
							}
						});
					}
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
		}
	}

	protected void putValues()
	{
		if (vehicleDetails!=null)
		{
			tvVehicleName.setText(Html.fromHtml("<font color=#ffffff>" + vehicleDetails.getYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
					+ vehicleDetails.getFriendlyName() + "</font>"));
			tvOtherLine.setText(Html.fromHtml("<font color=#ffffff> Other </font> <font color=" 
					+ getActivity().getResources().getColor(R.color.dark_blue) + "> "+ makename+" "+ modelname + "</font> articles"));
		}
		if (variantReviews.size()==0)
		{
			lvVariantReview.setVisibility(View.GONE);
			tvNoVariantArticles.setVisibility(View.VISIBLE);
		}else {
			lvVariantReview.setSelection(0);
			lvVariantReview.setVisibility(View.VISIBLE);
			tvNoVariantArticles.setVisibility(View.GONE);
		}
		if (modelReviews.size()!=0 ) 
		{
				lvModelReview.setSelection(0);
				lvModelReview.setVisibility(View.VISIBLE);
				tvNoModelArticles.setVisibility(View.GONE);
				modelReviewsAdapter.notifyDataSetChanged();
		}else {
			lvModelReview.setVisibility(View.GONE);
			tvNoModelArticles.setVisibility(View.VISIBLE);
		}
	}

}
