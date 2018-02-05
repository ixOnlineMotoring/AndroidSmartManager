package com.nw.adapters;

/**
 * Author: alex askerov
 * Date: 9/9/13
 * Time: 10:52 PM
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.BaseImage;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.smartmanager.android.live.SmartManagerApplication;
import com.utils.Constants;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: alex askerov Date: 9/7/13 Time: 10:56 PM
 */
public class GridImageAdapter extends BaseDynamicGridAdapter
{
    AddPhotoListener addPhotoListener;
    DisplayImageOptions options;

    ArrayList<BaseImage> mList;

    @SuppressWarnings("deprecation")
    public GridImageAdapter(Context context, List<BaseImage> items,
                            int columnCount)
    {
        super(context, items, columnCount);
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .resetViewBeforeLoading(false)

                .displayer(new SimpleBitmapDisplayer()).build();
        mList = (ArrayList<BaseImage>) items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        CheeseViewHolder holder;
        final BaseImage gridImage = (BaseImage) getItem(position);
        if (gridImage.getType() != -1)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_blog, parent, false);
            holder = new CheeseViewHolder(convertView);
            holder.build((BaseImage) getItem(position));
            holder.close.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    CustomDialogManager.showOkCancelDialog(getContext(),
                            getContext().getString(R.string.are_you_sure_you_want_to_delete),
                            new DialogListener()
                            {

                                @Override
                                public void onButtonClicked(int type)
                                {
                                    if (type == Dialog.BUTTON_POSITIVE)
                                    {
                                        remove(gridImage);
                                        if (addPhotoListener != null)
                                            addPhotoListener.onRemoveOptionSelected(position);
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                }
            });
        } else
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_blog_default, parent, false);
            convertView.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    //
                    if (addPhotoListener != null)
                        addPhotoListener.onAddOptionSelected();
                }
            });
        }
        return convertView;
    }

    private class CheeseViewHolder
    {
        private ImageView close;
        private ImageView image;
        private ProgressBar pBar;

        private CheeseViewHolder(View view)
        {
            image = (ImageView) view.findViewById(R.id.ivPicture);
            close = (ImageView) view.findViewById(R.id.ivClose);
            pBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }

        void build(BaseImage gridImage)
        {
            if (gridImage.isDoc())
            {
                if (gridImage.getDocPath().contains(".pdf"))
                {
                    image.setImageResource(R.drawable.pdfplaceholder);
                } else if (gridImage.getDocPath().contains(".csv"))
                {
                    image.setImageResource(R.drawable.excelplaceholder);
                } else
                {
                    image.setImageResource(R.drawable.docplaceholder);
                }


            } else
            {
                if (gridImage.isLocal())
                {
                    SmartManagerApplication.getImageLoader().displayImage("file://" + gridImage.getPath(), image, options);
                } else
                {

                    if (gridImage.getThumbPath() == null || gridImage.getThumbPath().equals(""))
                    {
                        SmartManagerApplication.getImageLoader().displayImage(gridImage.getLink(), image, options, new ImageLoadingListener()
                        {

                            @Override
                            public void onLoadingStarted(String arg0, View arg1)
                            {
                                pBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
                            {
                                pBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2)
                            {
                                pBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingCancelled(String arg0, View arg1)
                            {
                                pBar.setVisibility(View.GONE);
                            }
                        });
                    } else
                    {
                        SmartManagerApplication.getImageLoader().displayImage(
                                Constants.IMAGE_BASE_URL + gridImage.getThumbPath(), image, options, new ImageLoadingListener()
                                {

                                    @Override
                                    public void onLoadingStarted(String arg0, View arg1)
                                    {
                                        pBar.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
                                    {
                                        pBar.setVisibility(View.GONE);                                            // TODO Auto-generated method stub
                                    }

                                    @Override
                                    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2)
                                    {
                                        pBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onLoadingCancelled(String arg0, View arg1)
                                    {
                                        pBar.setVisibility(View.GONE);
                                    }
                                });
                        //Helper.Log("thumbpath", gridImage.getThumbPath());
                    }
                }
            }


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
