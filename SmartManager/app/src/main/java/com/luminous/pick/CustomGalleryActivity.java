package com.luminous.pick;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.greysonparrelli.permiso.Permiso;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.nw.interfaces.DialogListener;
import com.nw.model.BaseImage;
import com.nw.webservice.DataManager;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.BaseActivity;
import com.smartmanager.android.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class CustomGalleryActivity extends BaseActivity
{
    GridView gridGallery;
    Handler handler;
    GalleryAdapter adapter;
    ImageView imgNoMedia;
    Button btnGalleryOk;
    private ImageLoader imageLoader;

    ArrayList<BaseImage> oldImages;// =getUpdatedImageListWithoutPlus();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        Permiso.getInstance().setActivity(CustomGalleryActivity.this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        oldImages = getIntent().getParcelableArrayListExtra("all_path");
        initImageLoader();
        init();
    }

    private void initImageLoader()
    {
        try
        {
            String CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();
            File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(), CACHE_DIR);

            @SuppressWarnings("deprecation")
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();

            @SuppressWarnings("deprecation")
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                    getBaseContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .discCacheExtraOptions(200, 200, null)
                    .discCache(new UnlimitedDiskCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());

            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void init()
    {

        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(false);

        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, true, true);
        gridGallery.setOnScrollListener(listener);

        findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
        gridGallery.setOnItemClickListener(mItemMulClickListener);
        adapter.setMultiplePick(true);

        gridGallery.setAdapter(adapter);
        imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

        btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
        btnGalleryOk.setOnClickListener(mOkClickListener);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult()
                {
                    @Override
                    public void onPermissionResult(Permiso.ResultSet resultSet)
                    {
                        if (resultSet.areAllPermissionsGranted())
                        {
                            new ImageTask().execute();
                        } else
                        {
                            CustomDialogManager.showOkCancelDialog(CustomGalleryActivity.this, "Please accept all permissions" +
                                    " for proper functioning of app", "Ok", "Cancel", new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    switch (type)
                                    {
                                        case Dialog.BUTTON_NEGATIVE:
                                            // activity.this.finish();
                                            break;
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions)
                    {
                        Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
            } else
            {
                new ImageTask().execute();
            }
        } else
        {
            new ImageTask().execute();
        }
    }

    class ImageTask extends AsyncTask<Void, Void, ArrayList<BaseImage>>
    {
        Dialog dialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = CustomDialogManager.showProgressDialog(CustomGalleryActivity.this);
        }

        @Override
        protected ArrayList<BaseImage> doInBackground(Void... params)
        {
            return getGalleryPhotos();
        }

        @Override
        protected void onPostExecute(ArrayList<BaseImage> result)
        {
            super.onPostExecute(result);
            if (result != null)
            {
                adapter.addAll(result, true);
            }

            checkImageStatus();
            dialog.dismiss();

            if (oldImages != null)
            {
                ArrayList<BaseImage> temp_array = new ArrayList<>();
                boolean is_found = false;
                for (int i = 0; i < oldImages.size(); i++)
                {
                    for (int j = 0; j < adapter.getCount(); j++)
                    {
                        is_found = false;
                        if (adapter.getItem(j).getPath() != null)
                        {
                            if (adapter.getItem(j).getPath().equals(oldImages.get(i).getPath()))
                            {
                                is_found = true;
                                adapter.getItem(j).setCreateNewFile(false);
                                adapter.changeSelection(j, true);
                                break;
                            }
                        }
                    }
                    if (is_found == false)
                    {
                        if (oldImages.get(i).isLocal())
                            temp_array.add(oldImages.get(i));
                    }
                }
                adapter.addAll(temp_array, false);
                for (int i = 0; i < temp_array.size(); i++)
                {
                    adapter.getItem((adapter.getCount() - 1) - i).setCreateNewFile(false);
                    adapter.changeSelection((adapter.getCount() - 1) - i, true);
                }
            }
            showCountMessage();
        }

    }

    private void checkImageStatus()
    {
        if (adapter.isEmpty())
            imgNoMedia.setVisibility(View.VISIBLE);
        else
            imgNoMedia.setVisibility(View.GONE);
    }

    View.OnClickListener mOkClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            ArrayList<BaseImage> selected = adapter.getSelected();
            Intent data = new Intent();
            data.putParcelableArrayListExtra("all_path", selected);
            // data.putParcelableArrayListExtra(("all_uri", allUri);
            setResult(RESULT_OK, data);
            finish();

        }
    };
    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id)
        {
            adapter.changeSelection(v, position);
            showCountMessage();
        }
    };

    AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id)
        {
            BaseImage item = adapter.getItem(position);
            Intent data = new Intent().putExtra("single_path", item.getPath());
            setResult(RESULT_OK, data);
            finish();
        }
    };

    private ArrayList<BaseImage> getGalleryPhotos()
    {
        ArrayList<BaseImage> galleryList = new ArrayList<BaseImage>();
        try
        {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;
            Cursor imagecursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            if (imagecursor != null && imagecursor.getCount() > 0)
            {

                String data = "";
                //Helper.Log("Directory :",""+DataManager.getInstance().albumDirectory);

                while (imagecursor.moveToNext())
                {
                    BaseImage item = new BaseImage();
                    item.setType(1);
                    item.setLocal(true);
                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    item.setPath(imagecursor.getString(dataColumnIndex));

                    data = item.getPath();
                    data = data.substring(0, data.lastIndexOf("/"));
                    //Helper.Log("Path :", data);

                    if (data.equals(DataManager.getInstance().albumDirectory))
                        item.setCreateNewFile(false);

					/*if(item.getPath().startsWith(DataManager.getInstance().albumDirectory))
                        item.setCreateNewFile(true);*/

                    galleryList.add(item);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

    private void showCountMessage()
    {
        int size = adapter.getSelected().size();
        if (size > 0)
            getActionBar().setTitle(size + " Photos Selected");
        else
            getActionBar().setTitle(getString(R.string.app_name));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Permiso.getInstance().setActivity(CustomGalleryActivity.this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (imageLoader != null)
        {
            imageLoader.clearDiskCache();
            imageLoader.clearMemoryCache();
        }

        if (adapter != null)
        {
            adapter.clearCache();
            adapter.clear();
        }
        System.gc();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
