package com.nw.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nw.model.BaseImage;
import com.nw.model.MyImage;
import com.smartmanager.android.R;
import com.utils.Constants;

import java.util.ArrayList;

public class CustomGridAdapter extends BaseAdapter
{

    Context mContext;
    ArrayList<BaseImage> imageList;
    ArrayList<String> imageUrlList;
    ImageLoader loader;
    private DisplayImageOptions options;

    public CustomGridAdapter(Context c, ArrayList<BaseImage> list)
    {
        mContext = c;
        imageList = list;
        loader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)
                .showImageOnFail(R.drawable.noimage)
                .showImageForEmptyUri(R.drawable.noimage)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    public CustomGridAdapter(Context c, ArrayList<String> list, boolean abc)
    {
        mContext = c;
        imageUrlList = list;
        loader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)
                .showImageOnFail(R.drawable.noimage)
                .showImageForEmptyUri(R.drawable.noimage)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public int getCount()
    {
        if (imageList != null)
            return imageList.size();
        else
            return imageUrlList.size();
    }

    @Override
    public Object getItem(int position)
    {
        if (imageList != null)
            return imageList.get(position);
        else
            return imageUrlList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);

            holder = new ViewHolder();
            holder.init(convertView);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.listener = new SimpleImageLoadingListener()
        {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {
                holder.pBar.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
                holder.imageView.setVisibility(View.GONE);
            }
        };
        holder.pBar.setVisibility(View.VISIBLE);
        if (imageList != null)
        {
            if (imageList.get(position) instanceof MyImage)
            {
                MyImage image = (MyImage) imageList.get(position);
                loader.loadImage(image.getThumb() + "width=200", options, holder.listener);
            } else
            {

                if (imageList.get(position).isLocal())
                    loader.displayImage("File://" + imageList.get(position).getPath(), holder.imageView, options, holder.listener);
                else
                {
                    if (imageList.get(position).getLink().contains("http://"))
                    {
                        loader.displayImage(imageList.get(position).getLink(), holder.imageView, options, holder.listener);
                    } else
                    {
                        loader.displayImage(Constants.IMAGE_BASE_URL + imageList.get(position).getLink(), holder.imageView, options, holder.listener);
                    }
                }

            }
        } else
        {
            loader.loadImage(imageUrlList.get(position), options, holder.listener);
        }
        return convertView;
    }

    private static class ViewHolder
    {
        ImageView imageView;
        ProgressBar pBar;
        SimpleImageLoadingListener listener;

        public void init(View view)
        {
            imageView = (ImageView) view.findViewById(R.id.ivItemImage);
            pBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

}
