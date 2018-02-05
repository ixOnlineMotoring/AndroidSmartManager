package com.luminous.pick;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nw.model.BaseImage;
import com.nw.widget.SquareImageView;
import com.smartmanager.android.R;
import com.smartmanager.android.live.SmartManagerApplication;
import com.utils.Constants;
import com.utils.Helper;

import java.util.ArrayList;


public class GalleryAdapter extends BaseAdapter
{

    private Context mContext;
    private LayoutInflater infalter;
    private ArrayList<BaseImage> data = new ArrayList<BaseImage>();
    ImageLoader imageLoader;
    private boolean isActionMultiplePick;
    private int selected_count = 0;


    public GalleryAdapter(Context c, ImageLoader imageLoader)
    {
        infalter = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = c;
        this.imageLoader = imageLoader;
        // clearCache();
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public BaseImage getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void setMultiplePick(boolean isMultiplePick)
    {
        this.isActionMultiplePick = isMultiplePick;
    }

    public void selectAll(boolean selection)
    {
        for (int i = 0; i < data.size(); i++)
        {
            data.get(i).setSeleted(selection);
        }
        notifyDataSetChanged();
    }

    public boolean isAllSelected()
    {
        boolean isAllSelected = true;

        for (int i = 0; i < data.size(); i++)
        {
            if (!data.get(i).isSeleted())
            {
                isAllSelected = false;
                break;
            }
        }

        return isAllSelected;
    }

    public boolean isAnySelected()
    {
        boolean isAnySelected = false;

        for (int i = 0; i < data.size(); i++)
        {
            if (data.get(i).isSeleted())
            {
                isAnySelected = true;
                break;
            }
        }

        return isAnySelected;
    }

    public ArrayList<BaseImage> getSelected()
    {
        ArrayList<BaseImage> dataT = new ArrayList<BaseImage>();

        for (int i = 0; i < data.size(); i++)
        {
            if (data.get(i).isSeleted())
            {
                dataT.add(data.get(i));
            }
        }

        return dataT;
    }

    public void addAll(ArrayList<BaseImage> files,boolean is_clear)
    {
        try
        {
            if(is_clear)
            this.data.clear();

            this.data.addAll(files);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public void changeSelection(View v, int position)
    {
        if (data.get(position).isSeleted())
        {
            data.get(position).setSeleted(false);
            if (selected_count > 0)
                selected_count = selected_count - 1;
        } else
        {
            if (selected_count >= Constants.PHOTO_LIMIT)
            {
                Toast.makeText(mContext, "Max " + Constants.PHOTO_LIMIT + " items allowed", Toast.LENGTH_LONG).show();
                return;
            }
            data.get(position).setSeleted(true);
            selected_count = selected_count + 1;
        }

        ((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data.get(position).isSeleted());
        ((ViewHolder) v.getTag()).imgQueue_selected.setSelected(data.get(position).isSeleted());
    }

    public void changeSelection(int position, boolean state)
    {
        data.get(position).setSeleted(state);
        if (state)
            selected_count = selected_count + 1;
        else
            selected_count = selected_count - 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        if (convertView == null)
        {
            convertView = infalter.inflate(R.layout.gallery_item, parent, false);
            holder = new ViewHolder();
            holder.imgQueue = (SquareImageView) convertView.findViewById(R.id.imgQueue);
            holder.imgQueue_selected = (SquareImageView) convertView.findViewById(R.id.imgQueue_selected);
            holder.imgQueueMultiSelected = (ImageView) convertView.findViewById(R.id.imgQueueMultiSelected);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);

            if (isActionMultiplePick)
            {
                holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
            } else
            {
                holder.imgQueueMultiSelected.setVisibility(View.GONE);
            }
            holder.imgQueue.setLayoutParams(new FrameLayout.LayoutParams(Helper.dpToPx(mContext, 120), Helper.dpToPx(mContext, 90)));
            holder.imgQueue_selected.setLayoutParams(new FrameLayout.LayoutParams(Helper.dpToPx(mContext, 120), Helper.dpToPx(mContext, 90)));
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imgQueue.setTag(position);
        holder.imgQueue_selected.setTag(position);

        try
        {

			
			/*ImageSize targetSize = new ImageSize(120, 120); // result Bitmap will be fit to this size

			imageLoader.loadImage("file://" + data.get(position).sdcardPath, targetSize, new SimpleImageLoadingListener() 
			{
				@Override
				public void onLoadingStarted(String imageUri, View view) 
				{
					holder.imgQueue.setImageResource(R.drawable.no_media);
					super.onLoadingStarted(imageUri, view);
				}
			});*/

			/*imageLoader.loadImage(imageUri, targetSize,  new SimpleImageLoadingListener() {
                @Override
			    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			        // Do whatever you want with Bitmap
			    }
			});
			*/


            if (data.get(position).getImageUri() != null)
                holder.imgQueue.setImageURI(data.get(position).getImageUri());
            else
                SmartManagerApplication.getImageLoader().displayImage("file://" + data.get(position).getPath(), holder.imgQueue, new SimpleImageLoadingListener()
                {
                    @Override
                    public void onLoadingStarted(String imageUri, View view)
                    {
                        super.onLoadingStarted(imageUri, view);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.imgQueue.setImageResource(R.drawable.rectangle_border_tranperent);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                    {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                    {
                        super.onLoadingFailed(imageUri, view, failReason);
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view)
                    {
                        // TODO Auto-generated method stub
                        super.onLoadingCancelled(imageUri, view);
                        holder.progressBar.setVisibility(View.GONE);
                    }


                });

            //holder.imgQueue.setImageURI(data.get(position).uri);

			
			/*imageLoader.displayImage("file://" + data.get(position).sdcardPath,
					holder.imgQueue, new SimpleImageLoadingListener() 
					{
						@Override
						public void onLoadingStarted(String imageUri, View view) 
						{
							holder.imgQueue.setImageResource(R.drawable.no_media);
							super.onLoadingStarted(imageUri, view);
						}
					});*/

            if (isActionMultiplePick)
            {
                holder.imgQueueMultiSelected.setSelected(data.get(position).isSeleted());
                holder.imgQueue_selected.setSelected(data.get(position).isSeleted());
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return convertView;
    }

    public class ViewHolder
    {
        SquareImageView imgQueue, imgQueue_selected;
        ImageView imgQueueMultiSelected;
        ProgressBar progressBar;
    }

    @SuppressWarnings("deprecation")
    public void clearCache()
    {
        imageLoader.clearDiscCache();
        imageLoader.clearMemoryCache();
    }

    public void clear()
    {
        data.clear();
        notifyDataSetChanged();
    }
}
