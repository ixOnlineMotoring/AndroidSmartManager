package com.nw.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nw.interfaces.WantedSeachDeleteListener;
import com.nw.model.Person;
import com.nw.webservice.VolleySingleton;
import com.smartmanager.android.R;
import com.utils.Helper;

import java.util.List;

public class ScanListAdapter extends ArrayAdapter<Person>
{
	//WantedSeachDeleteListener mListener;
	ImageLoader loader;
	
	public ScanListAdapter(Context context, int resource, List<Person> objects)
	{
		super(context, resource, objects);
	//	mListener=listener;
		loader= VolleySingleton.getInstance().getImageLoader();
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		if(convertView==null)
		{
			convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_scan_license, parent,false);
			holder= new ViewHolder();
			holder.init(convertView);
			
			convertView.setTag(holder);
		}else{
			holder= (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(getItem(position).getInitials()+" "+getItem(position).getSurname());
		holder.tvLicenseId.setText("ID#: "+getItem(position).getIdentity_Number());
        if(getItem(position).getPhoto() != null)
        {
            if(!getItem(position).getPhoto().equals(""))
            {
                String base64 = (getItem(position).getPhoto()).replace("\\.", "");
                byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.ivScan.setImageBitmap(decodedByte);
            }
        }else
		{
			holder.ivScan.setImageResource(R.drawable.ic_user);
		}
		return convertView;
	}

	private static class ViewHolder
	{
		TextView tvDelete, tvName,tvLicenseId,tvSearch;
		ImageView ivScan;
		
		public void init(View convertView){
			tvDelete= (TextView) convertView.findViewById(R.id.tvDelete);
			tvLicenseId= (TextView) convertView.findViewById(R.id.tvLicenseId);
			tvName= (TextView) convertView.findViewById(R.id.tvName);
			tvSearch= (TextView) convertView.findViewById(R.id.tvSearch);
			ivScan= (ImageView) convertView.findViewById(R.id.ivScan);
		}
	}
}
