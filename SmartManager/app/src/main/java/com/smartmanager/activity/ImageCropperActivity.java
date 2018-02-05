package com.smartmanager.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


import com.edmodo.cropper.CropImageView;
import com.joanzapata.android.iconify.Iconify;

import com.smartmanager.android.R;
import com.sonyericsson.util.ScalingUtilities;
import com.utils.Helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class ImageCropperActivity extends AppCompatActivity implements OnClickListener
{
    CropImageView civ;
    ImageView btnSave, btnDelete, btnRotate;
    TextView tvBack, tvDelete, tvRotate, tvCrop, tvDone;
    boolean isCropUnselected = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_image_crop);

        civ = (CropImageView) findViewById(R.id.CropImageView);
        assert civ != null;
        civ.setAspectRatio(2, 4);
        civ.disableOverlayView();
        civ.setFixedAspectRatio(false);
        if (getIntent().getBooleanExtra("isCamera", true))
        {
            previewCapturedImage(getIntent().getStringExtra("filepath"));
        } else
        {
            previewPickedImage(getIntent().getStringExtra("filepath"));
        }
        btnSave = (ImageView) findViewById(R.id.btnSave);
        btnDelete = (ImageView) findViewById(R.id.btnDelete);
        btnRotate = (ImageView) findViewById(R.id.btnRotate);

        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnRotate.setOnClickListener(this);

        initialise();
    }

    private void initialise()
    {
        tvBack = (TextView) findViewById(R.id.tvBack);
        tvDelete = (TextView) findViewById(R.id.tvDelete);
        tvRotate = (TextView) findViewById(R.id.tvRotate);
        tvCrop = (TextView) findViewById(R.id.tvCrop);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvDelete.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, Iconify.IconValue.fa_trash, 30), null, null, null);
        tvRotate.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, Iconify.IconValue.fa_repeat, 30), null, null, null);
        tvCrop.setCompoundDrawablesWithIntrinsicBounds(Helper.getIcon(this, Iconify.IconValue.fa_crop, 30), null, null, null);
        tvDone.setText("Done");
        tvBack.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvRotate.setOnClickListener(this);
        tvCrop.setOnClickListener(this);
        tvDone.setOnClickListener(this);

    }

    /**
     * Display image from a path to ImageView
     */
    private void previewCapturedImage(String path)
    {
        try
        {
            ExifInterface ei = null;
            try
            {
                ei = new ExifInterface(path);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            civ.setImageBitmap(decodeScaledBitmapFromSdCard(path, 1200, 600), ei);

        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private void previewPickedImage(String path)
    {
        ExifInterface ei = null;
        try
        {
            if (path != null)
            {
                ei = new ExifInterface(path);
            } else
            {
                Helper.showCropDialog(getString(R.string.failed_please_pick_image), ImageCropperActivity.this);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        civ.setImageBitmap(decodeScaledBitmapFromSdCard(path, 1200, 600), ei);
    }

    File f = null;

    @SuppressLint("SdCardPath")
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tvBack:
                finish();
                break;

            case R.id.tvDelete:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.tvDone:
                try
                {
                    Bitmap croppedImage = civ.getCroppedImage();
                    f = new File("/sdcard/" + Helper.getFileName(Helper.getOutputMediaFileUri().getPath()));
                    if (f.exists())
                    {
                        f.delete();
                    }
                    writeExternalToCache(croppedImage, f);
                    setResult(RESULT_OK, new Intent().putExtra("filepath", f.getPath()));
                    finish();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                break;

            case R.id.tvRotate:
                civ.rotateImage(90);
                break;

            case R.id.btnSave:
                try
                {
                    Bitmap croppedImage = civ.getCroppedImage();
                    f = new File("/sdcard/" + Helper.getFileName(Helper.getOutputMediaFileUri().getPath()));
                    if (f.exists())
                    {
                        f.delete();
                    }
                    writeExternalToCache(croppedImage, f);
                    setResult(RESULT_OK, new Intent().putExtra("filepath", f.getPath()));
                    finish();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case R.id.btnDelete:

                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.btnRotate:
                civ.rotateImage(90);
                break;

            case R.id.tvCrop:
                if (isCropUnselected)
                {
                    isCropUnselected = false;
                    civ.showOverlayView();
                } else
                {
                    isCropUnselected = true;
                    civ.disableOverlayView();
                }

                break;
        }
    }

    public static Bitmap decodeScaledBitmapFromSdCard(String filePath,
                                                      int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static final int BUFFER_SIZE = 1024 * 8;

    void writeExternalToCache(Bitmap bitmap, File file)
    {
        try
        {
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
            //Bitmap.createScaledBitmap(bitmap, 648, 452, true).compress(CompressFormat.JPEG, 80, bos);
            bitmap.compress(CompressFormat.JPEG, 95, bos);
            bos.flush();
            bos.close();
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {

        }
    }

    public void setLocale(String languageCode)
    {
        Locale locale;
        if (languageCode.equals("zh-CN"))
        {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if (languageCode.equals("zh-TW"))
        {
            locale = Locale.TRADITIONAL_CHINESE;
        } else
        {
            locale = new Locale(languageCode);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
