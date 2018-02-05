package com.nw.widget;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.greysonparrelli.permiso.Permiso;
import com.luminous.pick.CustomGalleryActivity;
import com.nw.adapters.GridImageAdapter;
import com.nw.adapters.GridImageAdapter.AddPhotoListener;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.BaseImage;
import com.nw.webservice.DataManager;
import com.smartmanager.activity.ImageCropActivity;
import com.smartmanager.activity.NewCropperActivity;
import com.smartmanager.android.R;
import com.sonyericsson.util.AlbumStorageDirFactory;
import com.sonyericsson.util.BaseAlbumDirFactory;
import com.sonyericsson.util.FroyoAlbumDirFactory;
import com.utils.Constants;
import com.utils.Helper;

import org.askerov.dynamicgrid.DynamicGridView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DragableGridView implements AddPhotoListener
{
    DynamicGridView gridView;
    RelativeLayout rlImages;
    Activity activity;
    private static final int PICK_FROM_CAMERA = 1888;
    private static final int PICK_FROM_FILE = 1889;
    private static final int CROP_IMAGE = 2000;
    public static final int PICK_MULTIPLE_FILE = 2001;
    public static final int PICK_MULTIPLE_FILE_CROP = 2002;
    Uri fileUri, mImageCaptureUri;
    ArrayList<BaseImage> deletedImages;
    ArrayList<BaseImage> gridImages;
    GridImageAdapter adapter;
    ImageClickListener mListener;
    /**
     * Image position changed in Gridview
     */
    boolean priorityChanged = false;
    public boolean imageAdded = false;
    /**
     * Image Deleted
     */
    boolean imageDeleted = false;
    boolean isEnabled = true;
    boolean video = false;
    boolean optionSelected = false;
    boolean showPlusIcon = true;
    int mNewPosition;

    public DragableGridView(Activity activity)
    {
        //  this.activity =activity;
        createDirectory();
        if (deletedImages == null)
            deletedImages = new ArrayList<BaseImage>();
    }

    public DragableGridView()
    {
        createDirectory();
        if (deletedImages == null)
            deletedImages = new ArrayList<BaseImage>();
    }

    public DragableGridView(boolean isvideoGrid)
    {
        createDirectory();
        video = true;
        if (deletedImages == null)
            deletedImages = new ArrayList<BaseImage>();
    }

    public ArrayList<BaseImage> getDeletedImages()
    {
        return deletedImages;
    }

    public void setImageList(ArrayList<BaseImage> list)
    {

        if (list == null)
            list = new ArrayList<BaseImage>();

        gridImages.clear();
        gridImages.addAll(list);

        imageAdded = false;
        priorityChanged = false;
        imageDeleted = false;
        isEnabled = true;
        video = false;
        deletedImages.clear();

        if (showPlusIcon)
            addPlusItem();

        if (!Helper.isTablet(gridView.getContext()))
            adapter = new GridImageAdapter(gridView.getContext(), gridImages, 3);
        else
            adapter = new GridImageAdapter(gridView.getContext(), gridImages, 4);
        adapter.setAddPhotoListener(this);
        gridView.setAdapter(adapter);
        gridView.smoothScrollToPosition(0);
    }

    public void init(View view, ImageClickListener listener)
    {
        if (!video)
        {
            gridView = (DynamicGridView) view.findViewById(R.id.gvImages);
            rlImages = (RelativeLayout) view.findViewById(R.id.rlImage);
        } else
        {
            gridView = (DynamicGridView) view.findViewById(R.id.gvImages);
            rlImages = (RelativeLayout) view.findViewById(R.id.rlVideo);
        }

        mListener = listener;
        gridView.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                gridView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View position, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                if (gridImages.get(arg2).getType() != -1)
                    mListener.onImageClick(arg2);
                gridView.stopEditMode();
                //addPlusItem();
            }
        });

        gridView.setOnDragListener(new DynamicGridView.OnDragListener()
        {

            @Override
            public void onDragStarted(int position)
            {

            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition)
            {
                mNewPosition = newPosition;
            }
        });

        gridView.setOnDropListener(new DynamicGridView.OnDropListener()
        {
            @Override
            public void onActionDrop()
            {
                priorityChanged = true;
                // stop edit mode immediately after drop item
                gridView.stopEditMode();
                addPlusItem();
                gridView.smoothScrollToPosition(mNewPosition);
            }
        });

        gridView.setOnItemLongClickListener(new DynamicGridView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                removeLastItem();
                gridView.startEditMode(position);
                return true;
            }

        });


        showPlusIcon = true;

        if (gridImages == null)
        {
            gridImages = new ArrayList<BaseImage>();
            addPlusItem();
        } else
        {
            if (!Helper.isTablet(gridView.getContext()))
                adapter = new GridImageAdapter(gridView.getContext(), gridImages, 3);
            else
                adapter = new GridImageAdapter(gridView.getContext(), gridImages, 4);

            adapter.setAddPhotoListener(this);
            gridView.setAdapter(adapter);
            gridView.smoothScrollToPosition(0);
        }

        view.setOnTouchListener(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                gridView.stopEditMode();
                addPlusItem();

                return false;
            }
        });
    }

    public boolean isOptionSelected()
    {
        return optionSelected;
    }

    private void addPlusItem()
    {
        if (isEnabled)
        {
            isEnabled = false;
            if (adapter == null)
            {
                gridImages.add(new BaseImage());
                if (!Helper.isTablet(gridView.getContext()))
                    adapter = new GridImageAdapter(gridView.getContext(), gridImages, 3);
                else
                    adapter = new GridImageAdapter(gridView.getContext(), gridImages, 4);
                adapter.setAddPhotoListener(new AddPhotoListener()
                {

                    @Override
                    public void onAddOptionSelected()
                    {
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            getPermissionChecked();
                        }else{*/
                        getImageFromGalleryOrCamera();
                        //  }
                    }

                    @Override
                    public void onRemoveOptionSelected(int position)
                    {
                        removeItem(position);
                    }
                });
                gridView.setAdapter(adapter);
            } else
            {
                BaseImage imBlog = new BaseImage();
                gridImages.add(imBlog);
                adapter.add(imBlog);
                adapter.notifyDataSetChanged();
            }
            gridView.smoothScrollToPosition(adapter.getCount() - 1);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getPermissionChecked()
    {
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult()
            {
                @Override
                public void onPermissionResult(Permiso.ResultSet resultSet)
                {
                    if (resultSet.areAllPermissionsGranted())
                    {
                        getImageFromGalleryOrCamera();
                    } else
                    {
                        CustomDialogManager.showOkDialog(activity, "Please accept all permissions" +
                                " for proper functioning of app", new DialogListener()
                        {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onButtonClicked(int type)
                            {
                                switch (type)
                                {
                                    case Dialog.BUTTON_POSITIVE:
                                        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA}, 0);
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
            }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        } else
        {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, 0);
        }
    }

    private void removeLastItem()
    {
        if (isEnabled == false)
        {
            isEnabled = true;
            int count = (gridImages.size() - 1);
            adapter.getCount();
            adapter.remove(adapter.getItem(count));
            gridImages.remove(count);

            if (gridImages.isEmpty())
            {
                gridView.stopEditMode();
                addPlusItem();
            } else
                adapter.notifyDataSetChanged();
        }
    }

    private void removeItem(int position)
    {
        // check which image change
        // if last no need to update priority
        if (position == gridImages.size())
            priorityChanged = false;
        else
        {
            priorityChanged = true;
        }

        if (gridImages.get(position).getType() != -1)
        {
            if (gridImages.get(position).getId() != 0)
            {
                deletedImages.add(gridImages.get(position));
                imageDeleted = true;
            }
        }
        gridImages.remove(position);
        if (gridImages.isEmpty())
        {
            gridView.stopEditMode();
            addPlusItem();
        } else
            adapter.notifyDataSetChanged();

    }

    @Override
    public void onAddOptionSelected()
    {
        getImageFromGalleryOrCamera();
    }

    @Override
    public void onRemoveOptionSelected(final int position)
    {
        boolean isLocalImage = false;
        if (gridImages.get(position).isLocal())
            isLocalImage = true;

        removeItem(position);
        if (isLocalImage)
        {
            if (showPlusIcon == false)
            {
                showPlusIcon = true;
                addPlusItem();
            }
        }

        if (mListener != null)
            mListener.onImageDeleted(position);
    }

    public void getImageFromGalleryOrCamera()
    {
        optionSelected = false;
        final String[] items = new String[]{"Take from Camera", "Select from Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(gridView.getContext(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(gridView.getContext());
        builder.setTitle("Select image");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.setAdapter(adapter, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                optionSelected = true;
                if (which == 0)
                {
                    Intent intent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = null;
                    try
                    {
                        mCurrentPhotoPath = null;
                        f = setUpPhotoFile();
                        mCurrentPhotoPath = f.getAbsolutePath();
                        fileUri = Uri.fromFile(f);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        ((FragmentActivity) gridView.getContext()).startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath = null;
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        f = null;
                        mCurrentPhotoPath = null;
                    }
                } else
                {
                    try
                    {
                        if (TextUtils.isEmpty(DataManager.getInstance().albumDirectory))
                        {
                            if (Build.VERSION.SDK_INT >= 23)
                            {
                                if (ActivityCompat.checkSelfPermission((Activity) activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ActivityCompat.checkSelfPermission((Activity) activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                {
                                    Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult()
                                    {
                                        @Override
                                        public void onPermissionResult(Permiso.ResultSet resultSet)
                                        {
                                            if (resultSet.areAllPermissionsGranted())
                                            {
                                                getAlbumDir();
                                            } else
                                            {
                                                CustomDialogManager.showOkCancelDialog(activity, "Please accept all permissions" +
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
                                    }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                                }
                            } else
                            {
                                getAlbumDir();
                            }
                        } else
                        {
                            getAlbumDir();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(gridView.getContext(), CustomGalleryActivity.class);
                    intent.putParcelableArrayListExtra("all_path", getUpdatedImageListWithoutPlus());
                    ((FragmentActivity) gridView.getContext()).startActivityForResult(intent, PICK_MULTIPLE_FILE);
                }

            }
        });
        final AlertDialog ad = builder.create();
        ad.setOnCancelListener(new OnCancelListener()
        {

            @Override
            public void onCancel(DialogInterface dialog)
            {
                optionSelected = false;
            }
        });
        ad.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            optionSelected = false;
            return;
        }
        if (optionSelected)
        {
            switch (requestCode)
            {
                case PICK_FROM_CAMERA:
                    try
                    {
                    /*//Uri imgUri= onCameraIntentResult(requestCode, resultCode, data);
                    Intent intent = new Intent(gridView.getContext(),ImageCropperActivity.class);
					//intent.putExtra("filepath", imgUri.getPath());
					intent.putExtra("filepath", mCurrentPhotoPath);
					intent.putExtra("isCamera", true);
					((FragmentActivity) gridView.getContext()).startActivityForResult(intent, CROP_IMAGE);*/


                        updateGalleryPic();

                        ArrayList<BaseImage> oldImages = getUpdatedImageListWithoutPlus();

                        ArrayList<BaseImage> localImages = new ArrayList<BaseImage>();
                        //ArrayList<String>uriList=new ArrayList<String>();
                        for (int i = 0; i < oldImages.size(); i++)
                        {
                            if (oldImages.get(i).isLocal())
                                localImages.add(oldImages.get(i));
                        }
                        //pathList.add(mCurrentPhotoPath);

                        BaseImage gridImage = new BaseImage();
                        gridImage.setCreateNewFile(true);
                        gridImage.setType(1);
                        gridImage.setPath(mCurrentPhotoPath);
                        gridImage.setLocal(true);
                        gridImage.setPriority((oldImages.size() + 1));
                        localImages.add(gridImage);

                        Intent cropIntent = new Intent(gridView.getContext(), NewCropperActivity.class);
                        cropIntent.putExtra("multiple", true);
                        cropIntent.putExtra("isCamera", true);
                        cropIntent.putParcelableArrayListExtra("all_path", localImages);
                        cropIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        //cropIntent.putExtra("all_uri", uriList.toArray(new String[pathList.size()]));
                        ((FragmentActivity) gridView.getContext()).startActivityForResult(cropIntent, PICK_MULTIPLE_FILE_CROP);

					/*Intent intent = new Intent(gridView.getContext(),CustomGalleryActivity.class);
                    intent.putExtra("all_path",pathList.toArray(new String[pathList.size()]));
					((FragmentActivity) gridView.getContext()).startActivityForResult(intent, PICK_MULTIPLE_FILE);*/
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Helper.showToast("Failed to load image data,Please try again later", ((FragmentActivity) gridView.getContext()));
                    }
                    break;
                case PICK_FROM_FILE:
                    try
                    {
                        mImageCaptureUri = data.getData();
                        String filepath = getPath(mImageCaptureUri);
                        if (!TextUtils.isEmpty(filepath))
                        {
                            Intent intent1 = new Intent(gridView.getContext(), ImageCropActivity.class);
                            intent1.putExtra("filepath", mImageCaptureUri.toString());
                            intent1.putExtra("isCamera", false);
                            ((FragmentActivity) gridView.getContext()).startActivityForResult(intent1, CROP_IMAGE);
                        } else
                            Helper.showToast("Failed to load image data,Please try again later", gridView.getContext());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case CROP_IMAGE:
                    String photopath = data.getStringExtra("filepath");
                    BaseImage gridImage = new BaseImage();
                    gridImage.setType(1);
                    gridImage.setPath(photopath);
                    gridImage.setLocal(true);
                    gridImage.setPriority(adapter.getCount());
                    if (gridImages.size() == 1)
                        gridImages.add(0, gridImage);
                    else if (gridImages.size() >= 2)
                        gridImages.add(gridImages.size() - 1, gridImage);
                    if (adapter == null)
                    {
                        if (!Helper.isTablet(gridView.getContext()))
                            adapter = new GridImageAdapter(gridView.getContext(), gridImages, 3);
                        else
                            adapter = new GridImageAdapter(gridView.getContext(), gridImages, 4);
                        adapter.setAddPhotoListener(this);
                        gridView.setAdapter(adapter);
                    } else
                    {
                        adapter.add(adapter.getCount() - 1, gridImage);
                        imageAdded = true;
                        adapter.notifyDataSetChanged();
                    }
                    gridView.smoothScrollToPosition(adapter.getCount() - 1);
                    optionSelected = false;
                    break;

                case PICK_MULTIPLE_FILE:
                    Intent cropIntent = new Intent(gridView.getContext(), NewCropperActivity.class);
                    cropIntent.putExtra("multiple", true);
                    cropIntent.putExtra("isCamera", false);
                    cropIntent.putParcelableArrayListExtra("all_path", data.getParcelableArrayListExtra("all_path"));
                    cropIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    //cropIntent.putExtra("all_uri",data.getStringArrayExtra("all_uri"));
                    ((FragmentActivity) gridView.getContext()).startActivityForResult(cropIntent, PICK_MULTIPLE_FILE_CROP);
                    break;
                case PICK_MULTIPLE_FILE_CROP:
                    ArrayList<BaseImage> oldImages = getUpdatedImageListWithoutPlus();
                    ArrayList<BaseImage> oldURLImages = new ArrayList<BaseImage>();
                    for (int i = 0; i < oldImages.size(); i++)
                    {
                        if (!oldImages.get(i).isLocal())
                            oldURLImages.add(oldImages.get(i));
                    }

                    //getAll images
                    @SuppressWarnings("unchecked")
                    ArrayList<BaseImage> images = (ArrayList<BaseImage>) data.getSerializableExtra("data");

                    if (images.size() >= Constants.PHOTO_LIMIT)
                        showPlusIcon = false;

                    oldURLImages.addAll(images);
                    setImageList(oldURLImages);
                    break;
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri)
    {
        if (uri == null)
        {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = gridView.getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null)
        {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public boolean isPriorityChanged()
    {
        return priorityChanged;
    }

    public boolean isImageDeleted()
    {
        return imageDeleted;
    }

    public GridImageAdapter getAdapter()
    {
        return adapter;
    }

    public ArrayList<BaseImage> getUpdatedImageListWithoutPlus()
    {
        ArrayList<BaseImage> list = new ArrayList<BaseImage>();
        if (showPlusIcon)
        {
            // plus icon is visible
            for (int i = 0; i < adapter.getCount() - 1; i++)
                list.add((BaseImage) adapter.getItem(i));
        } else
        {
            for (int i = 0; i < adapter.getCount(); i++)
                list.add((BaseImage) adapter.getItem(i));
        }
        return list;
    }

    public ArrayList<BaseImage> getLocalImageListWithoutPlus()
    {
        ArrayList<BaseImage> list = new ArrayList<BaseImage>();

        if (showPlusIcon)
        {
            for (int i = 0; i < adapter.getCount() - 1; i++)
            {
                if (((BaseImage) adapter.getItem(i)).getId() == 0)
                    list.add((BaseImage) adapter.getItem(i));
            }
        } else
        {
            for (int i = 0; i < adapter.getCount(); i++)
            {
                if (((BaseImage) adapter.getItem(i)).getId() == 0)
                    list.add((BaseImage) adapter.getItem(i));
            }
        }
        return list;
    }

    // new method to work with camera
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private String mCurrentPhotoPath;

    /* Photo album for this application */
    private String getAlbumName()
    {
        return gridView.getContext().getString(R.string.app_name);
    }

    private File getAlbumDir()
    {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            DataManager.getInstance().albumDirectory = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName()).getPath();
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName() + "/Camera");

            if (storageDir != null)
            {
                //DataManager.getInstance().albumDirectory=storageDir.getPath();
                if (!storageDir.mkdirs())
                {
                    if (!storageDir.exists())
                    {
                        Helper.Log("AlbumDir", "Failed to create Album directory");
                        Helper.showToast("Failed to create directory", gridView.getContext());
                        return null;
                    }
                }
            }
        } else
        {
            Log.v(gridView.getContext().getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
            Helper.showToast("External storage is not mounted READ/WRITE.", gridView.getContext());
        }
        return storageDir;
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "" + JPEG_FILE_SUFFIX;
        File albumF = getAlbumDir();
        //File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);

        File imageF = new File(albumF, imageFileName);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException
    {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private void createDirectory()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
        {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else
        {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    //edit by Shreyas
    protected static Date dateCameraIntentStarted = null; //Default location where we want the photo to be ideally stored.
    protected static Uri preDefinedCameraUri = null; //Potential 3rd location of photo data.
    protected static Uri photoUriIn3rdLocation = null;//Retrieved location of the photo.
    protected static Uri photoUri = null; //Orientation of the retrieved photo.
    protected static int rotateXDegrees = 0;

    protected void startCameraIntent()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                // NOTE: Do NOT SET: intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPicUri)
                // on Samsung Galaxy S2/S3/.. for the following reasons:
                // 1.) it will break the correct picture orientation
                // 2.) the photo will be stored in two locations (the given path and, additionally, in the MediaStore)
                String manufacturer = Build.MANUFACTURER.toLowerCase(Locale.ENGLISH);
                String model = Build.MODEL.toLowerCase(Locale.ENGLISH);
                String buildType = Build.TYPE.toLowerCase(Locale.ENGLISH);
                String buildDevice = Build.DEVICE.toLowerCase(Locale.ENGLISH);
                String buildId = Build.ID.toLowerCase(Locale.ENGLISH);
//					String sdkVersion = android.os.Build.VERSION.RELEASE.toLowerCase(Locale.ENGLISH);

                boolean setPreDefinedCameraUri = false;
                if (!(manufacturer.contains("samsung")) && !(manufacturer.contains("sony")))
                {
                    setPreDefinedCameraUri = true;
                }
                if (manufacturer.contains("samsung") && model.contains("galaxy nexus"))
                { //TESTED
                    setPreDefinedCameraUri = true;
                }
                if (manufacturer.contains("samsung") && model.contains("gt-n7000") && buildId.contains("imm76l"))
                { //TESTED
                    setPreDefinedCameraUri = true;
                }

                if (buildType.contains("userdebug") && buildDevice.contains("ariesve"))
                {  //TESTED
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("crespo"))
                {   //TESTED
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9100"))
                { //TESTED
                    setPreDefinedCameraUri = true;
                }

                ///////////////////////////////////////////////////////////////////////////
                // TEST
                if (manufacturer.contains("samsung") && model.contains("sgh-t999l"))
                { //T-Mobile LTE enabled Samsung S3
                    setPreDefinedCameraUri = true;
                }
                if (buildDevice.contains("cooper"))
                {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("t0lte"))
                {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("kot49h"))
                {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("t03g"))
                {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9300"))
                {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9195"))
                {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("xperia u"))
                {
                    setPreDefinedCameraUri = true;
                }

                ///////////////////////////////////////////////////////////////////////////


                dateCameraIntentStarted = new Date();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (setPreDefinedCameraUri)
                {
                    String filename = System.currentTimeMillis() + ".jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, filename);
                    preDefinedCameraUri = ((FragmentActivity) gridView.getContext()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, preDefinedCameraUri);
                }
                ((FragmentActivity) gridView.getContext()).startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e)
            {
                //logException(e);
                //onCouldNotTakePhoto();
                Helper.showToast("Could not take picture", ((FragmentActivity) gridView.getContext()));
            }
        } else
        {
            Helper.showToast("SD card not mounted", ((FragmentActivity) gridView.getContext()));
            //onSdCardNotMounted();
        }
    }

    private Uri getFileUriFromContentUri(Uri cameraPicUri)
    {
        try
        {
            if (cameraPicUri != null
                    && cameraPicUri.toString().startsWith("content"))
            {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = ((FragmentActivity) gridView.getContext()).getContentResolver().query(cameraPicUri, proj, null, null, null);
                cursor.moveToFirst();
                // This will actually give you the file path location of the image.
                String largeImagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                return Uri.fromFile(new File(largeImagePath));
            }
            return cameraPicUri;
        } catch (Exception e)
        {
            return cameraPicUri;
        }
    }

    private void updateGalleryPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(mCurrentPhotoPath));
        mediaScanIntent.setData(contentUri);
        ((FragmentActivity) gridView.getContext()).sendBroadcast(mediaScanIntent);
    }
}
