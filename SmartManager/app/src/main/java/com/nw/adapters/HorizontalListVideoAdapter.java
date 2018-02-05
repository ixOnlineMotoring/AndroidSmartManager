package com.nw.adapters;

/**
 * Author: Swapnil.Desale
 * Date: 22/11/16
 * Time: 10:52 PM
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nw.model.YouTubeVideo;
import com.smartmanager.android.R;
import com.smartmanager.android.live.SmartManagerApplication;

import java.util.ArrayList;


public class HorizontalListVideoAdapter extends ArrayAdapter<YouTubeVideo>
{
    AddPhotoListener addPhotoListener;
    DisplayImageOptions options;
    ArrayList<YouTubeVideo> mList;

    boolean showClosIcon = true;

    public HorizontalListVideoAdapter(Context context, int resource, ArrayList<YouTubeVideo> objects, boolean show_close_icon)
    {
        super(context, resource, objects);
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .showImageForEmptyUri(R.drawable.noimage)
                .showImageOnFail(R.drawable.noimage)
                .resetViewBeforeLoading(false)

                .displayer(new SimpleBitmapDisplayer()).build();
        mList = objects;
        showClosIcon = show_close_icon;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        HorizontalListVideoAdapter.ViewHolder holder;
        if (convertView == null)
        {
            holder = new HorizontalListVideoAdapter.ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_video, null);
            holder.init(convertView);
            convertView.setTag(holder);
        } else
        {
            holder = (HorizontalListVideoAdapter.ViewHolder) convertView.getTag();
        }

        if (showClosIcon)
        {
            holder.close.setVisibility(View.VISIBLE);
        } else
        {
            holder.close.setVisibility(View.GONE);
        }

        holder.close.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (addPhotoListener != null)
                    addPhotoListener.onRemoveOptionSelected(position);
                notifyDataSetChanged();
            }
        });


        if (getItem(position).isLocal())
        {
            Bitmap fisrtBitmap = ThumbnailUtils.createVideoThumbnail(getItem(position).getVideoFullPath(), MediaStore.Video.Thumbnails.MINI_KIND);
            holder.image.setImageBitmap(fisrtBitmap);
        } else
        {
            if (getItem(position).getVideoThumbUrl().contains("http"))
            {
                SmartManagerApplication.getImageLoader().displayImage(getItem(position).getVideoThumbUrl(), holder.image, options);
            } else
            {
                Bitmap fisrtBitmap = ThumbnailUtils.createVideoThumbnail(getItem(position).getVideoFullPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                holder.image.setImageBitmap(fisrtBitmap);
            }
        }

        return convertView;
    }

    private static class ViewHolder
    {
        private ImageView close;
        private ImageView image;

        public void init(View convertView)
        {
            image = (ImageView) convertView.findViewById(R.id.ivPicture);
            close = (ImageView) convertView.findViewById(R.id.ivClose);
        }
    }

    public void setAddPhotoListener(AddPhotoListener addPhotoListener)
    {
        this.addPhotoListener = addPhotoListener;
    }

    public interface AddPhotoListener
    {
        public void onAddOptionSelected();

        public void onRemoveOptionSelected(int postition);
    }
}
