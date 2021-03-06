package com.nw.adapters;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SpecialVehicle;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.smartmanager.android.live.SmartManagerApplication;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.util.ArrayList;
import java.util.List;

public class PublishSpecialAdapter extends ArrayAdapter<SpecialVehicle>
{
	ImageLoader imageLoader;
	TaskListener mListener,publishListener;
	DisplayImageOptions options;
	
	public PublishSpecialAdapter(Context context, int resource, List<SpecialVehicle> objects, TaskListener listener, 
			TaskListener publishListener) 
	{
		super(context, resource, objects);
		imageLoader = VolleySingleton.getInstance().getImageLoader();
		mListener= listener;
		this.publishListener= publishListener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		if(convertView==null)
		{
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.list_item_publish_special, parent,false);
			holder= new ViewHolder();
			holder.init(convertView);
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}
		SmartManagerApplication.getImageLoader().displayImage(Constants.IMAGE_BASE_URL+"GetImage.aspx?ImageID="+ getItem(position).getImageID(),holder.ivVehicle, options);
		//holder.ivVehicle.setImageUrl(Constants.IMAGE_BASE_URL+"GetImage.aspx?ImageID="+ getItem(position).getImageID(), imageLoader);
		//holder.ivVehicle.setErrorImageResId(R.drawable.noimage);
		if(getItem(position).getUsedYear()==0){
			holder.tvTitle.setText(Html.fromHtml("<font color=#ffffff>Year?</font> <font color="+getContext().getResources().getColor(R.color.dark_blue)+">"+getItem(position).getFriendlyName()+"</font>"));
		}else {
			holder.tvTitle.setText(Html.fromHtml("<font color=#ffffff>"+getItem(position).getUsedYear()+
				    "</font> <font color="+getContext().getResources().getColor(R.color.dark_blue)+">"+  getItem(position).getFriendlyName()+"</font>"));
		}
		holder.tvDescription.setText(getItem(position).getSummary());
		holder.tvNormalPrice.setText(Helper.formatPrice(getItem(position).getNormalPrice()+""));
		holder.tvSpecialPrice.setText(Helper.formatPrice(getItem(position).getSpecialPrice()+""));
		holder.tvSavedPrice.setText(Helper.formatPrice(getItem(position).getSavePrice()+""));
		holder.tvFromDate.setText(Helper.convertUTCDateToNormal(getItem(position).getSpecialstart()));
		holder.tvToDate.setText(Helper.convertUTCDateToNormal(getItem(position).getSpecialEnd()));
		holder.tvColor.setText(getItem(position).getColour());
		holder.tvStockNo.setText(getItem(position).getStockCode()+"");
		holder.tvMileage.setText(Helper.getFormattedDistance(getItem(position).getMileage()+"")+"Km");
		holder.tvCreatedDate.setText(Helper.convertUTCDateToNormal(getItem(position).getSpecialCreated()));
		holder.tvStatus.setText(getItem(position).getEndStatus());
		holder.btnEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onTaskComplete(position);
			}
		});
		holder.btnEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getItem(position).isCanPublish())
				{
					
					publishSpecial(position);
					/*CustomDialogManager.showOkCancelDialog(getContext(),getContext().getString(R.string.publish_warning),new DialogListener() {
						@Override
						public void onButtonClicked(int type) {
							if(type== Dialog.BUTTON_POSITIVE){
								
							}
						}
					});*/
				}else {
					CustomDialogManager.showOkDialog(getContext(), getContext().getString(R.string.unable_to_publish));
				}
			}
		});
		holder.ivVehicle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		return convertView;
	}
	
	private static class ViewHolder
	{
		ImageView ivVehicle;
		TextView tvTitle, tvDescription, tvColor, tvStockNo, tvMileage, tvStatus;
		TextView tvNormalPrice, tvSpecialPrice, tvSavedPrice;
		TextView tvCreatedDate, tvFromDate, tvToDate;
		Button btnEdit, btnEnd;
		
		public void init(View convertView){
			ivVehicle= (ImageView) convertView.findViewById(R.id.ivVehicle);
			tvTitle= (TextView) convertView.findViewById(R.id.tvTitle);
			tvDescription= (TextView) convertView.findViewById(R.id.tvDescription);
			tvColor= (TextView) convertView.findViewById(R.id.tvColor);
			tvStockNo= (TextView) convertView.findViewById(R.id.tvStockNumber);
			tvMileage= (TextView) convertView.findViewById(R.id.tvMileage);
			tvStatus= (TextView) convertView.findViewById(R.id.tvStatus);
			tvNormalPrice= (TextView) convertView.findViewById(R.id.tvNormalPrice);
			tvSpecialPrice= (TextView) convertView.findViewById(R.id.tvSpecialPrice);
			tvSavedPrice= (TextView) convertView.findViewById(R.id.tvSavedPrice);
			tvCreatedDate= (TextView) convertView.findViewById(R.id.tvCreatedDate);
			tvFromDate= (TextView) convertView.findViewById(R.id.tvFromDate);
			tvToDate= (TextView) convertView.findViewById(R.id.tvToDate);
			btnEnd= (Button) convertView.findViewById(R.id.btnEdit);
			btnEdit= (Button) convertView.findViewById(R.id.btnActivate);
		}
	}
	
	/* * Function to publish Special from special Id
	 * Parameter - special id*/
	private void publishSpecial(final int position)
	{
		final Dialog dialog;
		dialog= CustomDialogManager.showProgressDialog(getContext());
		if(HelperHttp.isNetworkAvailable(getContext()))
		{
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash",DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
			parameterList.add(new Parameter("autoSpecialId", getItem(position).getSpecialID(), Integer.class));
	
			// create web service inputs
			DataInObject inObj = new DataInObject();
			inObj.setMethodname("PublishSpecial");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+ "ISpecialsService/PublishSpecial");
			inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
	
			// Network call
			new WebServiceTask(getContext(), inObj, false, new TaskListener() {
	
				@Override
				public void onTaskComplete(Object result) {
	
					try {
						Helper.Log("soap Response", result.toString());
						dialog.dismiss();
				//		SoapObject outer = (SoapObject) result;
	            		if (Integer.parseInt(result.toString())==1)
						{
							CustomDialogManager.showOkDialog(getContext(),getContext().getString(R.string.special_publish_successfully),
									new DialogListener()
									{
										@Override
										public void onButtonClicked(int type)
										{
											if (type == Dialog.BUTTON_POSITIVE)
											{
												publishListener.onTaskComplete(position);
											}
										}
									});
						}else {
							CustomDialogManager.showOkDialog(getContext(),getContext().getString(R.string.error_occured_try_later));
						}
					} catch (Exception e) {
						CustomDialogManager.showOkDialog(getContext(),getContext().getString(R.string.error_occured_try_later));
						e.printStackTrace();
					}
				}
			}).execute();
		}else{
			CustomDialogManager.showOkDialog(getContext(), getContext().getString(R.string.no_internet_connection));
		}
	}

}
