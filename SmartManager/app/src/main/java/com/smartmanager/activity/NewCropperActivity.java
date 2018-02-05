package com.smartmanager.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.greysonparrelli.permiso.Permiso;
import com.joanzapata.android.iconify.Iconify.IconValue;
import com.luminous.pick.CustomGalleryActivity;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nw.interfaces.DialogListener;
import com.nw.model.BaseImage;
import com.nw.webservice.DataManager;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.DragableGridView;
import com.smartmanager.android.R;
import com.smartmanager.android.staging.SmartManagerApplication;
import com.sonyericsson.util.AlbumStorageDirFactory;
import com.sonyericsson.util.BaseAlbumDirFactory;
import com.sonyericsson.util.FroyoAlbumDirFactory;
import com.sonyericsson.util.ScalingUtilities;
import com.sonyericsson.util.ScalingUtilities.ScalingLogic;
import com.utils.Constants;
import com.utils.Helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewCropperActivity extends AppCompatActivity implements OnClickListener
{
    CropImageView civ;
    LinearLayout llparentView;
    HorizontalScrollView horizontalScrollView;
    EditText edtCaption;
    ArrayList<BaseImage> images;
    String filePath;
    public static final int Height = 600;
    public static final int Width = 1200;
    private static final int PICK_FROM_CAMERA = 1888;
    int currentItem;
    int priority = 0;
    View lastView = null;
    int angle = 0;
    int rotate = 0;
    boolean showMenu = true;

    enum IMAGE_OPTION
    {
        ROTATE, CROP
    }

    ;
    IMAGE_OPTION imageOption = null;
    Bitmap currentBitmap = null;
    TextView tvBack, tvDelete, tvRotate, tvCrop, tvDone;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_new_crop);
        civ = (CropImageView) findViewById(R.id.CropImageView);
        initCropingToolBar();
        civ.hideOverlayView();
        civ.setAspectRatio(1, 2);
        civ.setFixedAspectRatio(false);
        edtCaption = (EditText) findViewById(R.id.edtCaption);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
        llparentView = (LinearLayout) findViewById(R.id.horizontalview);
        images = new ArrayList<BaseImage>();
        currentItem = 0;
        if(getIntent().getParcelableArrayListExtra("all_path")!= null){
            images = getIntent().getParcelableArrayListExtra("all_path");
        }

        showData();
        // for picture path
        createDirectory();
    }

    private void initCropingToolBar()
    {
        tvBack = (TextView) findViewById(R.id.tvBack);
        tvDelete = (TextView) findViewById(R.id.tvDelete);
        tvRotate = (TextView) findViewById(R.id.tvRotate);
        tvCrop = (TextView) findViewById(R.id.tvCrop);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvDelete.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, IconValue.fa_trash, 30), null, null, null);
        tvRotate.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, IconValue.fa_repeat, 30), null, null, null);
        tvCrop.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, IconValue.fa_crop, 30), null, null, null);
        tvDone.setText("Done");
        tvBack.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvRotate.setOnClickListener(this);
        tvCrop.setOnClickListener(this);
        tvDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tvBack:
                finish();
                break;

            case R.id.tvDelete:
                civ.hideOverlayView();
                if (currentItem != -1)
                {
                    if (llparentView != null)
                        llparentView.removeAllViews();
                    images.remove(currentItem);
                    int type = images.get(images.size() - 1).getType();
                    if (type != -1)
                    {
                        BaseImage gridImage = new BaseImage();
                        gridImage.setType(-1);
                        images.add(gridImage);
                        plusIconAdded = true;
                    }
                    if (images.size() == 1)
                    {
                        currentItem = -1;
                        showMenu = false;
                        civ.setImageBitmap(null);
                        InvalidateOptionsMenu();
                        //        showCountMessage();
                        // close the current activity
                        finish();
                    } else
                    {
                        if (plusIconAdded)
                            currentItem = (images.size() - 2);
                        else
                            currentItem = (images.size() - 1);
                    }
                    addView();
                    if (currentItem != -1)
                    {
                        loadFitImage(images.get(currentItem).getPath());
                        View view = llparentView.getChildAt(currentItem);
                        if (view != null)
                        {
                            view.setBackgroundResource(R.drawable.rectangle_border_blue);
                            lastView = view;
                        }
                    }
                    imageOption = null;
                    InvalidateOptionsMenu();
                }
                break;

            case R.id.tvRotate:
                imageOption = IMAGE_OPTION.ROTATE;
                InvalidateOptionsMenu();
                try
                {
                    // civ.hideOverlayView();
                    rotate();
                    civ.rotateImage(angle);
                } catch (OutOfMemoryError e)
                {
                    e.printStackTrace();
                    Helper.showToast("Failed to load image data,Please try again later", this);
                } catch (NullPointerException e)
                {
                    e.printStackTrace();
                    Helper.showToast("Failed to load image data,Please try again later", this);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Helper.showToast("Failed to load image data,Please try again later", this);
                }
                break;

            case R.id.tvCrop:
                civ.showOverlayView();
                imageOption = IMAGE_OPTION.CROP;
                tvDone.setText("Crop");
                InvalidateOptionsMenu();
                break;

            case R.id.tvDone:
                if (imageOption == IMAGE_OPTION.CROP)
                {
                    Bitmap croppedImage = civ.getCroppedImage();
                    if (croppedImage.getWidth() >= 400)
                    {
                        if (croppedImage.getHeight() >= 200)
                        {
                            try
                            {
                                File file = null;
                                if (images.get(currentItem).isCreateNewFile())
                                {
                                    file = setUpPhotoFile();
                                    if (file.exists())
                                        file.delete();

                                    images.get(currentItem).setCreateNewFile(false);
                                } else
                                {
                                    file = new File(images.get(currentItem).getPath());
                                }


                                writeExternalToCache(croppedImage, file);
                                double bytes = file.length();
                                double kilobytes = (bytes / 1024);
                                if (kilobytes > 350)
                                    Helper.showToast("Image larger than 350Kb", this);
                                else
                                {
                                    images.get(currentItem).setPath(file.getPath());
                                    civ.setImageBitmap(croppedImage);
                                    ImageView imageView = (ImageView) ((RelativeLayout) llparentView.getChildAt(currentItem)).getChildAt(0);
                                    SmartManagerApplication.getImageLoader().displayImage("file://" + images.get(currentItem).getPath(), imageView);
                                    images.get(currentItem).setUri(null);
                                    imageOption = null;
                                    civ.hideOverlayView();
                                    InvalidateOptionsMenu();
                                    updateGalleryPic(file.getPath());
                                }
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        } else
                            Helper.showToast("Height is too small", this);
                    } else
                        Helper.showToast("Width is too small", this);
                } else if (imageOption == IMAGE_OPTION.ROTATE)
                {
                    Bitmap croppedImage = civ.getBitmap();
                    if (croppedImage.getWidth() >= 400)
                    {
                        if (croppedImage.getHeight() >= 200)
                        {
                            try
                            {
                                File file = null;
                                if (images.get(currentItem).isCreateNewFile())
                                {
                                    file = setUpPhotoFile();
                                    if (file.exists())
                                        file.delete();
                                    images.get(currentItem).setCreateNewFile(false);
                                } else
                                {
                                    file = new File(images.get(currentItem).getPath());
                                }

                                writeExternalToCache(croppedImage, file);
                                double bytes = file.length();
                                double kilobytes = (bytes / 1024);
                                if (kilobytes > 350)
                                    Helper.showToast("Image larger than 350Kb", this);
                                else
                                {
                                    images.get(currentItem).setPath(file.getPath());
                                    civ.setImageBitmap(croppedImage);
                                    ImageView imageView = (ImageView) ((RelativeLayout) llparentView.getChildAt(currentItem)).getChildAt(0);
                                    SmartManagerApplication.getImageLoader().displayImage("file://" + images.get(currentItem).getPath(), imageView);
                                    images.get(currentItem).setUri(null);
                                    imageOption = null;
                                    InvalidateOptionsMenu();
                                    updateGalleryPic(file.getPath());

                                }
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        } else
                            Helper.showToast("Height is too small", this);
                    } else
                        Helper.showToast("Width is too small", this);
                } else
                {
                    int type = images.get(images.size() - 1).getType();
                    if (type == -1)
                        images.remove(images.size() - 1);
                    saveImage();
                }
                break;
        }
    }

    private void InvalidateOptionsMenu()
    {
        if (imageOption != null)
        {
            if (imageOption == IMAGE_OPTION.CROP)
                tvDone.setText("Crop");
            if (imageOption == IMAGE_OPTION.ROTATE)
                tvDone.setText("Apply");
        } else
            tvDone.setText("Done");
    }

    ;

    boolean plusIconAdded = false;

    private void showData()
    {
        try
        {
            if (llparentView != null)
                llparentView.removeAllViews();
            priority = 0;
            // show image to list
            if (images.size() < Constants.PHOTO_LIMIT)
            {
                BaseImage gridImage = new BaseImage();
                gridImage.setType(-1);
                images.add(gridImage);
                plusIconAdded = true;
            }
            addView();
            // show initial image
            if (images.size() > 1)
            {
                if (plusIconAdded)
                    currentItem = images.size() - 2;
                else
                    currentItem = images.size() - 1;
                loadFitImage(images.get(currentItem).getPath());
                View view = llparentView.getChildAt(currentItem);
                if (view != null)
                    view.setBackgroundResource(R.drawable.rectangle_border_blue);
                lastView = view;
            } else
                finish();
        } catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addView()
    {
        for (int position = 0; position < images.size(); position++)
        {
            final View convertView;
            ImageView image;
            final ProgressBar progressBar;
            final BaseImage gridImage = images.get(position);
            if (gridImage.getType() != -1)
            {
                convertView = getLayoutInflater().inflate(R.layout.list_item_image, null);
                convertView.setTag(position);
                convertView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View arg0)
                    {
                        if (lastView == arg0)
                            return;
                        currentItem = Integer.parseInt(arg0.getTag().toString());
                        try
                        {


                            loadFitImage(images.get(currentItem).getPath());
                        } catch (OutOfMemoryError e)
                        {

                            e.printStackTrace();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        } finally
                        {
                            if (lastView != null)
                                lastView.setBackgroundResource(R.drawable.rectangle_border_tranperent);
                            arg0.setBackgroundResource(R.drawable.rectangle_border_blue);
                            lastView = arg0;
                            imageOption = null;
                            invalidateOptionsMenu();
                        }
                    }
                });

                convertView.setBackgroundResource(R.drawable.rectangle_border_tranperent);
                image = (ImageView) convertView.findViewById(R.id.ivPicture);
                progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
                if (gridImage.isLocal())
                {
                    if (!TextUtils.isEmpty(gridImage.getUri()))
                        image.setImageURI(Uri.parse(gridImage.getUri()));
                    else
                        SmartManagerApplication.getImageLoader().displayImage("file://" + gridImage.getPath(), image, new ImageLoadingListener()
                        {

                            @Override
                            public void onLoadingStarted(String arg0, View arg1)
                            {
                                progressBar.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
                            {
                                progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2)
                            {
                                progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onLoadingCancelled(String arg0, View arg1)
                            {
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                } else
                {
                    if (gridImage.getThumbPath() == null || gridImage.getThumbPath().equals(""))
                    {
                        SmartManagerApplication.getImageLoader().displayImage(gridImage.getLink(), image);
                        Helper.Log("Link", gridImage.getLink());
                    } else
                    {
                        SmartManagerApplication.getImageLoader().displayImage(Constants.IMAGE_BASE_URL + gridImage.getThumbPath(), image, new ImageLoadingListener()
                        {

                            @Override
                            public void onLoadingStarted(String arg0, View arg1)
                            {
                                progressBar.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
                            {
                                progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2)
                            {
                                progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onLoadingCancelled(String arg0, View arg1)
                            {
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                        Helper.Log("thumbpath", gridImage.getThumbPath());
                    }
                }
            } else
            {
                convertView = getLayoutInflater().inflate(R.layout.grid_item_blog_default, null);
                convertView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        /*Intent intent = new Intent(NewCropperActivity.this, CustomGalleryActivity.class);
                        intent.putParcelableArrayListExtra("all_path", images);
                        startActivityForResult(intent, DragableGridView.PICK_MULTIPLE_FILE);*/
                        if (getIntent().hasExtra("isCamera"))
                        {
                            if (getIntent().getBooleanExtra("isCamera", true))
                            {
                                openCamera();
                            } else
                            {
                                openGallery();
                            }
                        } else
                        {
                            openGallery();
                        }
                    }
                });
                convertView.setBackgroundResource(R.drawable.rectangle_border_tranperent);
            }
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.rightMargin = 5;
            llparentView.addView(convertView, layoutParams);
        }

        horizontalScrollView.postDelayed(new Runnable()
        {
            public void run()
            {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);

    }

    private void openCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try
        {
            mCurrentPhotoPath = null;
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            Uri fileUri = Uri.fromFile(f);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            ((FragmentActivity) NewCropperActivity.this).startActivityForResult(intent, PICK_FROM_CAMERA);
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
    }

    private void openGallery()
    {
        try
        {
            if (TextUtils.isEmpty(DataManager.getInstance().albumDirectory))
            {
                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (ActivityCompat.checkSelfPermission(NewCropperActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(NewCropperActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
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
                                    CustomDialogManager.showOkCancelDialog(NewCropperActivity.this, "Please accept all permissions" +
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

        Intent intent = new Intent(NewCropperActivity.this, CustomGalleryActivity.class);
        intent.putParcelableArrayListExtra("all_path", images);
        startActivityForResult(intent, DragableGridView.PICK_MULTIPLE_FILE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        currentBitmap = null;
        System.gc();
    }

    private void rotate()
    {

        switch (rotate)
        {
            case 0:
            case 360:
                rotate = 90;
                break;
            case 90:
                rotate = 180;
                break;
            case 180:
                rotate = 270;
                break;
            case 270:
                rotate = 0;
                break;
        }
        angle = 90;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private void createDirectory()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        else
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
    }

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    /* Photo album for this application */
    private String getAlbumName()
    {
        return getString(R.string.app_name);
    }

    private File getAlbumDir()
    {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null)
            {
                if (!storageDir.mkdirs())
                {
                    if (!storageDir.exists())
                    {
                        Log.d("CameraSample", "failed to create directory");
                        Helper.showToast("failed to create directory", this);
                        return null;
                    }
                }
            }
        } else
        {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
            Helper.showToast("External storage is not mounted READ/WRITE.", this);
        }
        return storageDir;
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "" + JPEG_FILE_SUFFIX;
        File albumF = getAlbumDir();
        File imageF = new File(albumF, imageFileName);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException
    {
        File f = createImageFile();
        return f;
    }

    private void saveImage()
    {
        final Dialog dialog = CustomDialogManager.showProgressDialog(this);
        final Thread thread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                int type = images.get(images.size() - 1).getType();
                if (type == -1)
                    images.remove(images.size() - 1);

                int size = images.size();

				
				/*File albumPath=getAlbumDir();
                String albumdirecrectory="";
				if(albumPath!=null)
				{
					if(!TextUtils.isEmpty(albumPath.getPath()))
					{
						albumdirecrectory=albumPath.getPath();
					}				
				}*/
                //Helper.Log("albumdirecrectory", ""+albumdirecrectory);

                //boolean alreadyCropped=false;
                for (int index = 0; index < size; index++)
                {
                    try
                    {
                        //alreadyCropped=false;
                        File file = null;
                        Helper.Log("current path", "" + images.get(index).getPath());
                        //alreadyCropped=images.get(index).getPath().startsWith(albumdirecrectory);

                        if (images.get(index).isCreateNewFile())
                        {
                            file = setUpPhotoFile();
                            if (file.exists())
                                file.delete();
                            images.get(index).setCreateNewFile(false);
                            filePath = images.get(index).getPath();

                            // Part 1: Decode image
                            Bitmap unscaledBitmap = ScalingUtilities.decodeResource(getResources(), filePath, Width, Height, ScalingLogic.FIT);
                            // Part 2: Scale image
                            Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, Width, Height, ScalingLogic.FIT);
                            unscaledBitmap.recycle();

                            rotateImageForSave(scaledBitmap, file, filePath, index);

                        }
                         /*else
                         {
							  //filePath = images.get(index).getPath();	
							  //file = new File(images.get(index).getPath());							 
						 }*/

                    } catch (OutOfMemoryError e)
                    {
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        Thread.sleep(1200);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        //update the gallery pic
                        int size = images.size();
                        for (int index = 0; index < size; index++)
                            updateGalleryPic(images.get(index).getPath());

                        dialog.dismiss();
                        // go to calling activity
                        Intent data = new Intent().putExtra("data", images);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
            }
        });
        thread.start();
    }

    void rotateImageForSave(Bitmap bitmap, File file, String path, int index)
    {

        ExifInterface exif = null;
        try
        {
            exif = new ExifInterface(path);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        final Matrix matrix = new Matrix();
        final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotate = -1;

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }

        if (rotate == -1)
        {

            try
            {
                FileOutputStream fos = new FileOutputStream(file);
                final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
                bitmap.compress(CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
                fos.close();

                Helper.Log("saveImage", "oldPath:" + images.get(index).getPath());
                images.get(index).setPath(file.getPath());
                Helper.Log("saveImage", "NewPath:" + images.get(index).getPath());

                bitmap.recycle();
            } catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else
        {
            matrix.postRotate(rotate);

            try
            {
                final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                FileOutputStream fos = new FileOutputStream(file);
                final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
                rotatedBitmap.compress(CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
                fos.close();

                Helper.Log("saveImage", "oldPath:" + images.get(index).getPath());
                images.get(index).setPath(file.getPath());
                Helper.Log("saveImage", "NewPath:" + images.get(index).getPath());
            } catch (OutOfMemoryError e)
            {
                e.printStackTrace();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            bitmap.recycle();
        }
    }

    private void updateGalleryPic(String filepath)
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(filepath));
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    public static final int BUFFER_SIZE = 1024 * 10;

    void writeExternalToCache(Bitmap bitmap, File file)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
            bitmap.compress(CompressFormat.JPEG, 95, bos);
            bos.flush();
            bos.close();
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }

        switch (requestCode)
        {
            case DragableGridView.PICK_MULTIPLE_FILE:
                if (currentItem == -1)
                    currentItem = 0;
                plusIconAdded = false;
                images = data.getParcelableArrayListExtra("all_path");
                showData();
                break;

            case PICK_FROM_CAMERA:
                plusIconAdded = false;
                ArrayList<BaseImage> images_temp = new ArrayList<>();
                for (int i = 0; i < images.size(); i++)
                {
                    BaseImage gridImage = images.get(i);
                    if (gridImage.getType() != -1)
                    {
                        images_temp.add(gridImage);
                    }
                }
                images.clear();
                images.addAll(images_temp);

                try
                {
                    BaseImage gridImage = new BaseImage();
                    gridImage.setCreateNewFile(true);
                    gridImage.setType(1);
                    gridImage.setPath(mCurrentPhotoPath);
                    gridImage.setLocal(true);
                    gridImage.setPriority(images.size() - 1);
                    images.add(gridImage);
                    showData();
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Helper.showToast("Failed to load image data,Please try again later", this);
                }
                break;
        }
    }

    /**
     * Invoked when pressing button for showing result of the "Fit" decoding
     * method
     */
    protected void loadFitImage(String filePath)
    {
        // Part 1: Decode image
        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(getResources(), filePath, Width, Height, ScalingLogic.FIT);
        // Part 2: Scale image
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmapAndRotate(unscaledBitmap, Width, Height, ScalingLogic.FIT);
        unscaledBitmap.recycle();

        ExifInterface ei = null;
        try
        {
            ei = new ExifInterface(filePath);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if (ei != null)
            civ.setImageBitmap(scaledBitmap, ei);
        else
            civ.setImageBitmap(scaledBitmap);

        //	showCountMessage();
    }
}
