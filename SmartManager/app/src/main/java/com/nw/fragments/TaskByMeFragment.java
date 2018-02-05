package com.nw.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nw.adapters.CustomGridAdapter;
import com.nw.interfaces.DialogInputListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.TodoTask;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskByMeFragment extends BaseFragement implements OnGroupClickListener{

	ExpandableListView expandableListView;
	String[] headers= new String[]
			{"Done: Accept / Reject",
			"Overdue / Due Today",
			"Due Tomorrow",
			"Due Day 3 to 7",
			"Due Day 8+"};
	SimpleExpandableAdapter adapter;
	ArrayList<TodoTask> taskList;
	SparseArray<ArrayList<TodoTask>> hashMap;
	enum DateType{Before,Today,After}
	Date currentDate=new Date();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_todo, container,false);
		expandableListView=(ExpandableListView) view;
		expandableListView.setGroupIndicator(null);
		expandableListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		setHasOptionsMenu(true);
		
		adapter= new SimpleExpandableAdapter();
		expandableListView.setAdapter(adapter);
		expandableListView.setOnGroupClickListener(this);
		
		taskList= new ArrayList<TodoTask>();
		initialiseHashMap();
		listActivitiesByMember();
		return view;
	}
	private void initialiseHashMap(){
		if(hashMap==null)
			hashMap=new SparseArray<ArrayList<TodoTask>>();
		for(int i=0;i<5;i++)
			hashMap.put(i, new ArrayList<TodoTask>());
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id) {
		return false;
	}
	
	public class SimpleExpandableAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return headers.length;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if(hashMap.get(groupPosition)!=null)
				return hashMap.get(groupPosition).size();
			else
				return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return headers[groupPosition];
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if(hashMap.get(groupPosition).size()>0)
				return hashMap.get(groupPosition).get(childPosition);
			else
				return 0;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			 	View v = convertView;
			     if (v == null) {
			         v = LayoutInflater.from(getActivity()).inflate(R.layout.header_item_s, parent, false);
			     }
			     v.setTag(getGroup(groupPosition).toString());
			     TextView groupName = (TextView) v.findViewById(R.id.tvHeaderTitle);
			     TextView groupCount = (TextView) v.findViewById(R.id.tvHeaderCount);
			  
			     groupName.setText(headers[groupPosition]);
			     groupCount.setText(getChildrenCount(groupPosition)+"");
			  
			     ImageView ivIcon=(ImageView) v.findViewById(R.id.ivIcon);
			     if(getChildrenCount(groupPosition)>0){	              
		             if(!isExpanded )
		            	 ivIcon.setRotation(0);
		 			 else
		 				 ivIcon.setRotation(90);
			     }
			     if (groupPosition == 1) 
					v.setBackgroundResource(R.color.dark_blue);
				 else
					v.setBackgroundResource(R.color.gray);
			     return v;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition,final boolean isLastChild, View convertView, final ViewGroup parent) {
			final ViewHolder holder;
			if(convertView==null){
				convertView =  getActivity().getLayoutInflater().inflate(R.layout.list_item_task_by_me, parent,false);
				holder= new ViewHolder();
				
				holder.tRow= (TableRow) convertView.findViewById(R.id.trExpandable);
				holder.trButtons = (TableRow) convertView.findViewById(R.id.trButtons);
				holder.llExpandable= (LinearLayout) convertView.findViewById(R.id.llExpandable);
				
				
				holder.ivArrow= (ImageView) convertView.findViewById(R.id.ivArrow);
				holder.tvTaskId= (TextView) convertView.findViewById(R.id.tvTaskId);
				holder.tvClientName= (TextView) convertView.findViewById(R.id.tvClientName);
				holder.tvTitle= (TextView) convertView.findViewById(R.id.tvTaskTitle);
				holder.rlImages= (RelativeLayout) convertView.findViewById(R.id.rlImage);
				holder.tvAuthor= (TextView) convertView.findViewById(R.id.tvAuthor);
				holder.tvAssignee= (TextView) convertView.findViewById(R.id.tvAssignee);
				holder.tvDetails= (TextView) convertView.findViewById(R.id.tvDetails);
				holder.tvReadMore= (TextView) convertView.findViewById(R.id.tvReadMore);
				holder.bReject= (Button) convertView.findViewById(R.id.btnReject);
				holder.bAccept= (Button) convertView.findViewById(R.id.btnAccept);
				holder.bCancel= (Button) convertView.findViewById(R.id.btnCancelTask);
				holder.gvImages= (GridView) convertView.findViewById(R.id.gvImages);
				holder.tvEmpty= (TextView) convertView.findViewById(R.id.tvEmpty);
				holder.tvEmpty.setVisibility(View.GONE);
				
				convertView.setTag(holder);
			}else{
				holder= (ViewHolder) convertView.getTag();
			}
			
			holder.tvReadMore.setVisibility(View.GONE);
			if(hashMap.get(groupPosition).get(childPosition).getOpenTag()==null)
				hashMap.get(groupPosition).get(childPosition).setOpenTag("0");
			
			holder.gvImages.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					holder.gvImages.getParent().requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});
			holder.gvImages.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					navigateToLargeImage(position, hashMap.get(groupPosition).get(childPosition).getImageList());
				}
			});
			
			if(hashMap.get(groupPosition).get(childPosition).getImageList()==null){
				ArrayList<String> imagelisttemp= new ArrayList<String>();
				hashMap.get(groupPosition).get(childPosition).setImageList(imagelisttemp);
			}
			CustomGridAdapter imagesAdapter=new CustomGridAdapter(getActivity(), hashMap.get(groupPosition).get(childPosition).getImageList(), true);
			holder.gvImages.setTag(groupPosition+""+childPosition);
			hashMap.get(groupPosition).get(childPosition).setAdapter(imagesAdapter);
			
			if(holder.gvImages.getTag().equals(groupPosition+""+childPosition))
				holder.gvImages.setAdapter(imagesAdapter);
			
			if(holder.gvImages.getAdapter().getCount()==0){
				holder.rlImages.setVisibility(View.GONE);
				holder.tvEmpty.setVisibility(View.GONE);
			}else{
				holder.rlImages.setVisibility(View.VISIBLE);
				holder.tvEmpty.setVisibility(View.VISIBLE);
			}
			if(hashMap.get(groupPosition).get(childPosition).getState()==8){
				holder.trButtons.setVisibility(View.GONE);
				holder.bCancel.setVisibility(View.VISIBLE);
			}else if(hashMap.get(groupPosition).get(childPosition).getState()==11){
				holder.bCancel.setVisibility(View.GONE);
				holder.trButtons.setVisibility(View.VISIBLE);
			}
			holder.bReject.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CustomDialogManager.showOkCancelDialogWithInputBox(getActivity(), getString(R.string.please_enter_your_reason), new DialogInputListener() {
					
						@Override
						public void onButtonClicked(int type, String message) {
							rejectTask(hashMap.get(groupPosition).get(childPosition),message);
						}
					});
				} 
			});
			
			holder.bAccept.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(holder.tvAuthor.getText().toString().equals(holder.tvAssignee.getText().toString())){
						acceptTask(hashMap.get(groupPosition).get(childPosition),true);
					}else{
						closeTask(hashMap.get(groupPosition).get(childPosition), false);
					}
				}
			});
			holder.bCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					closeTask(hashMap.get(groupPosition).get(childPosition), false);
				}
			});
			if(hashMap.get(groupPosition).get(childPosition).getImageList().size()==0){
				holder.rlImages.setVisibility(View.GONE);
				holder.tvEmpty.setVisibility(View.VISIBLE);
			}else{
				holder.tvEmpty.setVisibility(View.GONE);
				holder.rlImages.setVisibility(View.VISIBLE);
			}
			holder.tvTaskId.setText(""+hashMap.get(groupPosition).get(childPosition).getId());
			holder.tvTaskId.append(Html.fromHtml(" | <font color=#3476BE>"+hashMap.get(groupPosition).get(childPosition).getTaskTargetClientName()+"</font>"));
			holder.tvTaskId.append(" | "+hashMap.get(groupPosition).get(childPosition).getTitle());
			
			if(hashMap.get(groupPosition).get(childPosition).getDetails()!=null)
				holder.tvDetails.setText(hashMap.get(groupPosition).get(childPosition).getDetails());
			if(hashMap.get(groupPosition).get(childPosition).getAuthor()!=null)
				holder.tvAuthor.setText(hashMap.get(groupPosition).get(childPosition).getAuthor());
			if(hashMap.get(groupPosition).get(childPosition).getAssignee()!=null)
				holder.tvAssignee.setText(hashMap.get(groupPosition).get(childPosition).getAssignee());
			
			if(!isTooLarge(holder.tvDetails, hashMap.get(groupPosition).get(childPosition).getDetails())){
				holder.tvReadMore.setVisibility(View.GONE);
			}else{
				holder.tvReadMore.setVisibility(View.VISIBLE);
				holder.tvReadMore.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(isTooLarge(holder.tvDetails, hashMap.get(groupPosition).get(childPosition).getDetails()))
							CustomDialogManager.showOkDialog(getActivity(), hashMap.get(groupPosition).get(childPosition).getDetails());
					}
				});
			}
			holder.cView=convertView;
			if(hashMap.get(groupPosition).get(childPosition).getOpenTag().toString().equals("0")){
				holder.llExpandable.setVisibility(View.GONE);
				holder.ivArrow.setRotation(0);
				holder.cView.setBackgroundColor(Color.TRANSPARENT);
			}else {
				if(hashMap.get(groupPosition).size()>0){
					holder.llExpandable.setVisibility(View.VISIBLE);
					holder.ivArrow.setRotation(90);
					holder.cView.setBackgroundColor(getResources().getColor(R.color.bar_color));
				}
			}
			
			holder.tRow.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
						if(getChildrenCount(groupPosition)>0)
						{
							if(hashMap.get(groupPosition).get(childPosition).getOpenTag().toString().equals("0")){
								
								holder.llExpandable.setVisibility(View.VISIBLE);
								holder.ivArrow.setRotation(90);
								hashMap.get(groupPosition).get(childPosition).setOpenTag("1");
								holder.cView.setBackgroundColor(getResources().getColor(R.color.bar_color));
								if(hashMap.get(groupPosition).get(childPosition).getDetails()==null &&
										hashMap.get(groupPosition).get(childPosition).getAuthor()==null &&
										hashMap.get(groupPosition).get(childPosition).getAssignee()==null){
									
									getTaskDetails(hashMap.get(groupPosition).get(childPosition));
									getTaskImages(hashMap.get(groupPosition).get(childPosition));
								}
								
							}else{
								holder.cView.setBackgroundColor(Color.TRANSPARENT);
								holder.llExpandable.setVisibility(View.GONE);
								holder.ivArrow.setRotation(0);
								hashMap.get(groupPosition).get(childPosition).setOpenTag("0");
							}
						}
					}
			});
			return convertView;
		}

		@Override
 		public void onGroupExpanded(int groupPosition) 
 		{
 			if(getChildrenCount(groupPosition)>0)
 				super.onGroupExpanded(groupPosition);
 		}
		
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	
	private static class ViewHolder{
		TableRow tRow, trButtons;
		LinearLayout llExpandable;
		ImageView ivArrow;
		TextView tvTaskId, tvClientName, tvTitle, tvAuthor, tvAssignee, tvDetails, tvEmpty, tvReadMore;
		Button bReject, bAccept, bCancel;
		GridView gvImages;
		RelativeLayout rlImages;
		View cView;
	}
	
	/*
	 * Navigate to large screen fragment
	 * As Fragment to called belongs to other activity we call activity and pass parameters through intent
	 * Parameter- position of image clicked
	 * */
	private void navigateToLargeImage(int position, ArrayList<String> imageList){
		try{
			Intent iToBuyActivity= new Intent(getActivity(),BuyActivity.class);
			iToBuyActivity.putStringArrayListExtra("urllist", imageList);
			iToBuyActivity.putExtra("index", position);
			iToBuyActivity.putExtra("vehicleName", "Task By Me");
			startActivity(iToBuyActivity);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isTooLarge (TextView text, String newText) 
	{
		if(text==null)
			return false;
		try
		{
		    float textWidth = text.getPaint().measureText(newText);
		    return (textWidth >= text.getMeasuredWidth ());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private void getTaskImages(final TodoTask task){
		if(HelperHttp.isNetworkAvailable(getActivity())){
		//Add parameters to request in arraylist
				ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
				parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
				parameterList.add(new Parameter("plannerTaskID", task.getId() ,Integer.class));
				
				//create web service inputs
				DataInObject inObj= new DataInObject();
				inObj.setMethodname("GetTaskImages");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IPlannerService/GetTaskImages");
				inObj.setUrl(Constants.PLANNER_WEBSERVICE_URL);
				inObj.setParameterList(parameterList);
				
				//Network call
				new WebServiceTask(getActivity(),inObj, true,new TaskListener() {
				
				//Network callback
				@Override
				public void onTaskComplete(Object result) {
					try{
						Helper.Log("Response", result.toString());
						SoapObject obj= (SoapObject) result;
						SoapObject inner = (SoapObject) obj.getPropertySafely("Images", "default");
						for(int i=0;i<inner.getPropertyCount();i++){
							String url=inner.getPropertyAsString(i);
							task.getImageList().add(url);
						}
						task.getAdapter().notifyDataSetChanged();
						adapter.notifyDataSetChanged();
						
					}catch (Exception e) {
						e.printStackTrace();
					}
						
					}
				}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}

	private void getTaskDetails(final TodoTask task){
		if(HelperHttp.isNetworkAvailable(getActivity())){
		//Add parameters to request in arraylist
		ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
		parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
		parameterList.add(new Parameter("plannerTaskID", task.getId() ,Integer.class));
		
		//create web service inputs
		DataInObject inObj= new DataInObject();
		inObj.setMethodname("GetTaskDetail");
		inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
		inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IPlannerService/GetTaskDetail");
		inObj.setUrl(Constants.PLANNER_WEBSERVICE_URL);
		inObj.setParameterList(parameterList);
		
		//Network call
		new WebServiceTask(getActivity(),inObj, true,new TaskListener() {
		
		//Network callback
		@Override
		public void onTaskComplete(Object result) {
			try{
				Helper.Log("Response", result.toString());
				SoapObject obj= (SoapObject) result;
				task.setAssignee(obj.getPropertySafelyAsString("Assignee", ""));
				task.setAuthor(obj.getPropertySafelyAsString("Author", ""));
				task.setDetails(obj.getPropertySafelyAsString("Details", ""));
				task.setMessage(obj.getPropertySafelyAsString("Message", ""));
				task.setCreatedDate(obj.getPropertySafelyAsString("CreateDate", ""));
				task.setDueDate(obj.getPropertySafelyAsString("DueDate", ""));
				task.setNewItem(Boolean.parseBoolean(obj.getPropertySafelyAsString("IsNew", "false")));
				task.setState(Integer.parseInt(obj.getPropertySafelyAsString("State", "8")));
				task.setStatus(Boolean.parseBoolean(obj.getPropertySafelyAsString("Status", "true")));
				task.setTitle(obj.getPropertySafelyAsString("Title", ""));
				adapter.notifyDataSetChanged();
			}catch (Exception e) {
				e.printStackTrace();
			}
			}
		}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}
	
	private void listActivitiesByMember(){
		if(HelperHttp.isNetworkAvailable(getActivity())){
			showProgressDialog();
		//Add parameters to request in arraylist
				ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
				parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
				parameterList.add(new Parameter("coreMemberID", DataManager.getInstance().user.getMemberId(),Integer.class));
				
				//create web service inputs
				DataInObject inObj= new DataInObject();
				inObj.setMethodname("ListActivitiesByMemberXML");
				inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
				inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IPlannerService/ListActivitiesByMemberXML");
				inObj.setUrl(Constants.PLANNER_WEBSERVICE_URL);
				inObj.setParameterList(parameterList);
				
				//Network call
				new WebServiceTask(getActivity(),inObj, false ,new TaskListener() {
				
				//Network callback
				@Override
				public void onTaskComplete(Object result) {
					try{
						hideProgressDialog();
						Helper.Log("response", result.toString()+"");
						SoapObject soapObj= (SoapObject) result;
						SoapObject inner= (SoapObject) soapObj.getPropertySafely("Tasks","default");
						TodoTask task;
						for(int i=0;i<inner.getPropertyCount();i++){
							task= new TodoTask();
							SoapObject taskObj= (SoapObject) inner.getProperty(i);
							task.setId(Integer.parseInt(taskObj.getPropertySafelyAsString("taskID", "")));
							task.setTitle(taskObj.getPropertySafelyAsString("taskTitle", ""));
							task.setNewItem(Boolean.parseBoolean(taskObj.getPropertySafelyAsString("taskIsNew", "")));
							Date date= Helper.convertStringToDate(taskObj.getPropertySafelyAsString("taskDue", ""));
							task.setDueDate(Helper.showDate(date));
							task.setTaskTargetClientID(Integer.parseInt(taskObj.getPropertySafelyAsString("taskTargetClientID", "")));
							task.setTaskTargetClientName(taskObj.getPropertySafelyAsString("taskTargetClientName", ""));
							taskList.add(task);
							
							DateType type=getType(date);
							switch (type) {
							case Before:
								checkAndAddItem(task, 1);
								break;
								
							case Today:
								checkAndAddItem(task, 0);
								break;
								
							case After:
								int dayOnward=(int) getDays(date, type);
								switch (dayOnward) 
								 {
									//tommorow
									case 1:
										checkAndAddItem(task, 2);
										break;	
									// due 3-7 days	
									case 3:	
									case 4:	
									case 5:	
									case 6:	
									case 7:	
										checkAndAddItem(task, 3);
										break;	
								// due 8+ days	
									default:
										checkAndAddItem(task, 4);
										break;
								 }
								break;

							default:
								break;
							}
						}
						adapter.notifyDataSetChanged();
					}catch (Exception e) {
						e.printStackTrace();
					}
					}
				}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}
	
	private void acceptTask(final TodoTask task, final boolean flag){
		if(HelperHttp.isNetworkAvailable(getActivity())){
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("plannerTaskID", task.getId() ,Integer.class));
			parameterList.add(new Parameter("optionalComment", "" ,String.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("AcceptTask");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IPlannerService/AcceptTask");
			inObj.setUrl(Constants.PLANNER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			new WebServiceTask(getActivity(),inObj, true,new TaskListener() {
			
			//Network callback
			@Override
			public void onTaskComplete(Object result) {
				try{
					Helper.Log("Response", result.toString());
					if(result.toString().equalsIgnoreCase("ok"))
					{
						if(flag){
							closeTask(task, flag);
						}else{
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.task_accepted_successfully));
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				}
			}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}
	
	
	private void rejectTask(TodoTask task, String reason){
		if(HelperHttp.isNetworkAvailable(getActivity())){
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("plannerTaskID", task.getId() ,Integer.class));
			parameterList.add(new Parameter("optionalReason", reason ,String.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("RejectTask");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IPlannerService/RejectTask");
			inObj.setUrl(Constants.PLANNER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			new WebServiceTask(getActivity(),inObj, true,new TaskListener() {
			
			//Network callback
			@Override
			public void onTaskComplete(Object result) {
				try{
					Helper.Log("Response", result.toString());
					if(result.toString().equalsIgnoreCase("ok"))
					{
						CustomDialogManager.showOkDialog(getActivity(), getString(R.string.task_rejected_successfully),new DialogListener() {
							
							@Override
							public void onButtonClicked(int type) {
								if(type==Dialog.BUTTON_POSITIVE)
									getActivity().finish();
							}
						});
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
					
				}
			}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}
	
	private void closeTask(final TodoTask task, final boolean flag){
		if(HelperHttp.isNetworkAvailable(getActivity())){
			ArrayList<Parameter> parameterList= new ArrayList<Parameter>();
			parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
			parameterList.add(new Parameter("plannerTaskID", task.getId() ,Integer.class));
			parameterList.add(new Parameter("optionComment", "" ,String.class));
			
			//create web service inputs
			DataInObject inObj= new DataInObject();
			inObj.setMethodname("CloseTask");
			inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
			inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE+"IPlannerService/CloseTask");
			inObj.setUrl(Constants.PLANNER_WEBSERVICE_URL);
			inObj.setParameterList(parameterList);
			
			//Network call
			new WebServiceTask(getActivity(),inObj, true,new TaskListener() {
			
			//Network callback
			@Override
			public void onTaskComplete(Object result) {
				try{
					Helper.Log("Response", result.toString());
					if(result.toString().equalsIgnoreCase("ok"))
					{
						if(flag)
							CustomDialogManager.showOkDialog(getActivity(), getString(R.string.task_accepted_closed_successfully), new DialogListener() {
								
								@Override
								public void onButtonClicked(int type) {
									getActivity().finish();
								}
							});
						else
							CustomDialogManager.showOkDialog(getActivity(),getString(R.string.task_closed), new DialogListener() {
								
								@Override
								public void onButtonClicked(int type) {
									getActivity().finish();
								}
							});
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
					
				}
			}).execute();
		}else{
			HelperHttp.showNoInternetDialog(getActivity());
		}
	}
	public DateType getType(Date inputDate)
	{
		Calendar input = Helper.getDatePart(inputDate);;
		Calendar current = Helper.getDatePart(currentDate);;
		
		if(input.after(current))
			return DateType.After;
		else if(input.before(current))
			return DateType.Before;
		else
			return DateType.Today;
	}
	
	private void checkAndAddItem(TodoTask todoTask,int position)
	{
		if(hashMap.get(position)!=null)
		{
			ArrayList<TodoTask>temp=hashMap.get(position);
			if(position==1)
				todoTask.setOverdue(true);
			
			int id=-1;
			for(int index=0;index<temp.size();index++)
			{
				if(temp.get(index).getId()==todoTask.getId())
				{
					id=index;
					break;
				}
			}
			if(id<0)
				temp.add(todoTask);
			hashMap.put(position, temp);
		}
	}
	
	public long getDays(Date inputDate,DateType dateType)
	{
		Calendar sDate = null;
		Calendar eDate = null;
		if(dateType==DateType.Before)
		{
			  sDate = Helper.getDatePart(inputDate);
			  eDate = Helper.getDatePart(currentDate);
		}
		if(dateType==DateType.After)
		{
			  sDate = Helper.getDatePart(currentDate);
			  eDate = Helper.getDatePart(inputDate);
		}
		  long daysBetween = 0;
		  while (sDate.before(eDate))
		  {
		      sDate.add(Calendar.DAY_OF_MONTH, 1);
		      daysBetween++;
		  }
		  return daysBetween;
	}

	@Override
	public void onResume() {
		super.onResume();
		showActionBar("Tasks By Me");
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	getActivity().finish();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
