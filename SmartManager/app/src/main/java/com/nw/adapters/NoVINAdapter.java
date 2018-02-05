package com.nw.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.nw.interfaces.AuditImageClickListener;
import com.nw.model.Audit;
import com.nw.model.MyImage;
import com.nw.widget.CustomTextViewLight;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.util.ArrayList;

public class NoVINAdapter extends ArrayAdapter<Audit>
{
	int type = 0;
	ArrayList<Audit> audits;
	Context mcontext;
	ArrayList<MyImage> tImageList;
	AuditImageClickListener auditImageClickListener;

	public NoVINAdapter(Context context, int resource, ArrayList<Audit> objects, AuditImageClickListener listener)
	{
		super(context, resource, objects);
		// type = Viewtype;
		audits = objects;
		mcontext = context;
		auditImageClickListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_stock_audit, parent, false);
			holder.tvId = (TextView) convertView.findViewById(R.id.tvId);
			holder.tvVIN = (CustomTextViewLight) convertView.findViewById(R.id.tvVINDetail);
			holder.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
			holder.tvLocation.setVisibility(View.GONE);
			holder.tvStock = (TextView) convertView.findViewById(R.id.tvStock);
			holder.tvTime = (CustomTextViewLight) convertView.findViewById(R.id.tvTime);
			holder.tvNewText = (CustomTextViewLight) convertView.findViewById(R.id.tvNewText);
			// holder.tvVehicleDetails=(TextView)
			// convertView.findViewById(R.id.tvVehicleDetails);
			// holder.tvVehicleDetails.setVisibility(View.GONE);
			holder.horizontalListView = (HorizontalListView) convertView.findViewById(R.id.hlvCarImages);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvNewText.setVisibility(View.GONE);
		holder.tvTime.setVisibility(View.VISIBLE);
		holder.horizontalListView.setVisibility(View.VISIBLE);
		// holder.tvLocation.setText("GEO: "+audits.get(position).getGEO());
		tImageList = new ArrayList<MyImage>();
		MyImage image1 = new MyImage();
		image1.setThumb(audits.get(position).getLicenseImagePath());
		image1.setFull(audits.get(position).getLicenseImagePath());
		tImageList.add(image1);
		MyImage image2 = new MyImage();
		image2.setThumb(audits.get(position).getVehicleImagePath());
		image2.setFull(audits.get(position).getVehicleImagePath());
		tImageList.add(image2);
		AuditImageAdapter adapter = new AuditImageAdapter(getContext(), tImageList);
		holder.horizontalListView.setAdapter(adapter);
		holder.horizontalListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
			{
				auditImageClickListener.onImageClick(pos, tImageList, position);
			}
		});
		holder.tvStock.setVisibility(View.GONE);

		if (!audits.get(position).getTime().contains("anyType{}"))
		{
			holder.tvTime.setText(Helper.getTimeHoursAndMinutes(audits.get(position).getTime()));
		}
		else
		{
			holder.tvTime.setText("Time?");
		}
		if (!audits.get(position).getVIN().contains("anyType{}"))
		{
			holder.tvVIN.setText(audits.get(position).getVIN());
		}

		return convertView;
	}

	public static class ViewHolder
	{
		TextView tvId, tvLocation, tvStock, tvNewText;
		CustomTextViewLight tvTime, tvVIN;
		HorizontalListView horizontalListView;
	}
}