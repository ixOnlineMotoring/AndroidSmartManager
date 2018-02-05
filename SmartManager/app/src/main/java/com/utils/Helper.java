package com.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nw.interfaces.DialogListener;
import com.nw.model.VehicleType;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.VideoCaptureActivity;
import com.smartmanager.android.BuildConfig;
import com.smartmanager.android.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Helper
{
    public static final int SCANNER_REQUEST_CODE = 120;
    public static boolean optionSelected = false;

    public static void Log(String tag, String msg)
    {
        if (msg == null)
            return;
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg);
        else
            writeFile(tag, msg);
    }

    @SuppressLint("NewApi")
    public static void nonEditableEditText(EditText edtView)
    {
        edtView.setBackground(null);
        edtView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        edtView.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.4f));
        params.setMargins(0, 5, 0, 5);
        edtView.setLayoutParams(params);
        edtView.setOnClickListener(null);
    }

    public static void getVideoFromGalleryOrCamera(final Context context)
    {

        final String[] items = new String[]{"Record from Camera", "Select from Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select the Video");
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
                    Intent intent = new Intent(context, VideoCaptureActivity.class);
                    ((FragmentActivity) context).startActivityForResult(intent, Constants.VIDEO_RECORDING_CUSTOM);
                } else
                {
                    Intent pickVideo = new Intent(Intent.ACTION_PICK);
                    pickVideo.setType("video/*");
                    ((FragmentActivity) context).startActivityForResult(pickVideo, Constants.VIDEO_GALLERY);// one

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




    public static String getAge(String string)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        Date testDate = null;
        try
        {
            testDate = sdf.parse(string);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(testDate);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
        {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    public static void Log(String tag, String msg, boolean isWriteOnFile)
    {
        if (msg == null)
            return;
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg);
        if (isWriteOnFile)
            writeFile(tag, msg);
    }

    public static void showToast(CharSequence msg, Context ctx)
    {
        if (ctx != null)
        {
            CustomDialogManager.showOkDialogAutoCancel(ctx, msg.toString());
        }
    }

    public static Bitmap StringToBitMap(String encodedString)
    {
        try
        {
            byte[] decodedString = Base64.decode(encodedString.replaceAll(".", ""), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        } catch (Exception e)
        {
            e.getMessage();
            return null;
        }
    }

    public static void showDebugToast(CharSequence msg, Context ctx)
    {
        if (BuildConfig.DEBUG)
        {
            if (ctx != null)
            {
                Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
            + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    public static boolean validMail(String email)
    {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public static void hidekeybord(EditText view)
    {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hidekeybord(View view)
    {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String unescape(String description)
    {
        return description.replaceAll("\\\\n", "\\\n");
    }

    public static String showDate(Date date)
    {

        SimpleDateFormat formatedDate = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String newDate = "";
        try
        {
            if (date != null)
                newDate = formatedDate.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return newDate;
    }

    public static Date getDate(String date)
    {

        SimpleDateFormat formatedDate = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        Date newDate = null;
        try
        {
            if (date != null)
                newDate = formatedDate.parse(date);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String md5(String inputString)
    {
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            try
            {
                digest.update(inputString.getBytes("UTF-8"));

            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            String hash = new BigInteger(1, digest.digest()).toString(16);
            while (hash.length() < 32)
            {
                hash = "0" + hash;
            }
            return hash;
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String createStringDate(int year, int monthOfYear, int dayOfMonth)
    {
        String day = "" + dayOfMonth;
        if (day.length() == 1)
            day = "0" + day;
        String month = "" + (monthOfYear + 1);
        if (month.length() == 1)
            month = "0" + month;

        return day + "" + month + "" + year;
    }

    public static String createStringDateNew(int year, int monthOfYear, int dayOfMonth)
    {
        String day = "" + dayOfMonth;
        if (day.length() == 1)
            day = "0" + day;
        String month = "" + (monthOfYear + 1);
        if (month.length() == 1)
            month = "0" + month;

        return year + "-" + month + "-" + day;
    }

    public static String createStringDateTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute)
    {
        String day = "" + dayOfMonth;
        if (day.length() == 1)
            day = "0" + day;
        String month = "" + (monthOfYear + 1);
        if (month.length() == 1)
            month = "0" + month;

        return day + "" + month + "" + year + "" + hour + "" + minute;
    }

    public static String createStringDateTimeDayMonthYear(int year, int monthOfYear, int dayOfMonth)
    {
        String day = "" + dayOfMonth;
        if (day.length() == 1)
            day = "0" + day;
        String month = "" + (monthOfYear + 1);
        if (month.length() == 1)
            month = "0" + month;

        return day + "" + month + "" + year;
    }

    public static String showDate(String dateInString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy", Locale.US);
        SimpleDateFormat formatedDate = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String newDate = "";
        try
        {
            // Log.d("Date before", dateInString);
            Date date = formatter.parse(dateInString);
            newDate = formatedDate.format(date);
            // Log.d("Date after", newDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (java.text.ParseException e)
        {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String showDateWithDash(String dateInString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
        SimpleDateFormat formatedDate = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String newDate = "";
        try
        {
            // Log.d("Date before", dateInString);
            Date date = formatter.parse(dateInString);
            newDate = formatedDate.format(date);
            // Log.d("Date after", newDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (java.text.ParseException e)
        {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String showDateWithDay(String dateInString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd hh:mm:ss", Locale.US);
        SimpleDateFormat formatedDate = new SimpleDateFormat("EEE dd MMM yyyy HH:mm a", Locale.US);
        String newDate = "";
        try
        {
            // Log.d("Date before", dateInString);
            Date date = formatter.parse(dateInString);
            //Day of Name in full form like,"Saturday", or if you need the first three characters you have
            // to put "EEE" in the date format and your result will be "Sat".
            newDate = formatedDate.format(date);
            // Log.d("Date after", newDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (java.text.ParseException e)
        {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String getAddress(Context context, double currentLatitude, double currentLongitude)
    {
        String address = "";
        try
        {

            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
            if (addresses.size() > 0)
            {
                StringBuilder builder = new StringBuilder();

                if (!TextUtils.isEmpty(addresses.get(0).getAddressLine(0)))
                {
                    builder.append(addresses.get(0).getAddressLine(0));
                }
                if (!TextUtils.isEmpty(addresses.get(0).getAddressLine(1)))
                {
                    builder.append(", " + addresses.get(0).getAddressLine(1));
                }
                if (!TextUtils.isEmpty(addresses.get(0).getSubAdminArea()))
                {
                    builder.append(", " + addresses.get(0).getSubAdminArea());
                }
                if (!TextUtils.isEmpty(addresses.get(0).getAdminArea()))
                {
                    builder.append(", " + addresses.get(0).getAdminArea());
                }
                if (!TextUtils.isEmpty(addresses.get(0).getCountryName()))
                {
                    builder.append(", " + addresses.get(0).getCountryName());
                }
                if (!TextUtils.isEmpty(addresses.get(0).getPostalCode()))
                {
                    builder.append(", " + addresses.get(0).getPostalCode());
                }
                return builder.toString();
                /*
				 * return addresses.get(0).getAddressLine(0) +", "+
				 * addresses.get(0).getAddressLine(1)+", "+
				 * addresses.get(0).getSubAdminArea() +", "+
				 * addresses.get(0).getAdminArea()+", "+
				 * addresses.get(0).getPostalCode();
				 */
            }
        } catch (IOException ex)
        {

        }
        return address;
    }

    public static String showDateTime(String dateInString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmm", Locale.US);
        SimpleDateFormat formatedDate = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US);
        String newDate = "";
        try
        {
            Date date = formatter.parse(dateInString);
            newDate = formatedDate.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (java.text.ParseException e)
        {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String formatDate(String date)
    {
        try
        {
            StringTokenizer tokenizer = new StringTokenizer(date, ".");
            String first = tokenizer.nextToken();
            StringTokenizer tokenizer2 = new StringTokenizer(first, ":");
            return tokenizer2.nextToken() + "h " + tokenizer2.nextToken() + "m ";
        } catch (NumberFormatException e)
        {
            return "";
        } catch (NoSuchElementException e)
        {
            return "";
        }
    }

    public static String formatPrice(String price)
    {
        try
        {
            Locale sAfrica = new Locale("en", "ZA");
            NumberFormat sAfricaFormat = NumberFormat.getCurrencyInstance(sAfrica);
            StringTokenizer tokenizer = new StringTokenizer(price, ".");
            String first = tokenizer.nextToken();
            return sAfricaFormat.format(Float.parseFloat(first)).replace(",00", "");
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatPriceInK(double value)
    {
        int convertedValue = 0;
        if (value != 0 && value > 1000)
        {
            convertedValue = (int) value / 1000;
            return convertedValue + "K";
        }
        return "0k";
    }

    public static String formatPriceToDefault(String price)
    {
        try
        {
            Locale locale = new Locale("en", "ZA");
            // return
            // NumberFormat.getCurrencyInstance(locale).parse(price).doubleValue()
            // + "";//---please don't delete this line
            return NumberFormat.getCurrencyInstance(locale).parse(price).intValue() + "";

        } catch (Exception e)
        {
            e.printStackTrace();
            return price;
        }
    }

    public static String formatMobileNumber(String number)
    {
        try
        {
            return number.substring(0, 3) + " " + number.substring(3, 6) + " " + number.substring(6);
        } catch (Exception e)
        {
            e.printStackTrace();
            return number;
        }

    }

    public static void setMarquee(TextView tv)
    {
        tv.setSelected(true);
        tv.setEllipsize(TruncateAt.MARQUEE);
        tv.setSingleLine(true);
        tv.setMarqueeRepeatLimit(-1);
    }

    public static int convertTimeLeftToInt(String timeLeft)
    {
        int convertedTime = 0;
        try
        {
            StringTokenizer tokenizer = new StringTokenizer(timeLeft, ".");
            String first = tokenizer.nextToken();
            StringTokenizer tokenizer2 = new StringTokenizer(first, ":");
            String convertedString = "";
            while (tokenizer2.hasMoreElements())
            {
                convertedString += tokenizer2.nextToken();
            }

            convertedTime = Integer.parseInt(convertedString);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return convertedTime;
    }

    public static String convertResizedBitmapToBase64(String path)
    {
        String encoded = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options;
        try
        {
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565; // Use this if
            // you dont
            // require Alpha
            // channel
            options.inSampleSize = 4;
            bitmap = BitmapFactory.decodeFile(path, options);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (OutOfMemoryError e)
        {
            e.printStackTrace();

            try
            {
                bitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (OutOfMemoryError e1)
            {
                e.printStackTrace();
            } catch (Exception e2)
            {
                e.printStackTrace();
            }

        } catch (Exception e)
        {
            e.printStackTrace();

            try
            {
                bitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (OutOfMemoryError e1)
            {
                e.printStackTrace();
            } catch (Exception e2)
            {
                e.printStackTrace();
            }
        } finally
        {
            if (bitmap != null)
                bitmap.recycle();
            bitmap = null;
        }
        return encoded;
    }

    public static String convertFileToByteArray(String f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 11];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();

            Log.e("Byte array", ">" + byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public static String convertBitmapToBase64(String path)
    {
        String encoded = null;
        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (OutOfMemoryError e)
        {
            e.printStackTrace();

            try
            {
                bitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (OutOfMemoryError e1)
            {
                e.printStackTrace();
            } catch (Exception e2)
            {
                e.printStackTrace();
            }

        } catch (Exception e)
        {
            e.printStackTrace();

            try
            {
                bitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (OutOfMemoryError e1)
            {
                e.printStackTrace();
            } catch (Exception e2)
            {
                e.printStackTrace();
            }
        } finally
        {
            if (bitmap != null)
                bitmap.recycle();
            bitmap = null;
        }
        return encoded;
    }

    public static String getCurrentTimeStamp()
    {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public static String getFileName(String path)
    {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static String getName(String path)
    {
        return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
    }

    public static String getNameWithExtenstion(String path)
    {
        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    public static String getSubStringBeforeString(String baseString, String key)
    {
        return baseString.split(key)[0];
    }

    public static Calendar getDatePart(Date date)
    {
        Calendar cal = Calendar.getInstance(); // get calendar instance
        cal.setTime(date);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        return cal; // return the date part
    }

    public static void showDropDown(View view, ListAdapter adapter, final AdapterView.OnItemClickListener clickListener)
    {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        final ListView hightListView = (ListView) dialog.findViewById(R.id.lvDialog);
        hightListView.setFastScrollEnabled(true);
        EditText edSearch = (EditText) dialog.findViewById(R.id.edSearch);
        edSearch.setVisibility(View.GONE);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });
        hightListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                dialog.dismiss();
                clickListener.onItemClick(parent, view, position, id);
            }
        });

        hightListView.setAdapter(adapter);
    }

    public static void showDropDownSearch(boolean showSearchBox, View view, final ArrayAdapter adapter, final AdapterView.OnItemClickListener clickListener)
    {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        final ListView hightListView = (ListView) dialog.findViewById(R.id.lvDialog);
        hightListView.setFastScrollEnabled(true);
        hightListView.setTextFilterEnabled(true);

        final EditText edSearch = (EditText) dialog.findViewById(R.id.edSearch);
        if (showSearchBox)
            edSearch.setVisibility(View.VISIBLE);
        else
            edSearch.setVisibility(View.GONE);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Helper.hidekeybord(edSearch);
                dialog.dismiss();

            }
        });
        hightListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Helper.hidekeybord(edSearch);
                dialog.dismiss();
                clickListener.onItemClick(parent, view, position, id);
            }
        });

        hightListView.setAdapter(adapter);
        edSearch.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
                adapter.getFilter().filter(s.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    public static void showDropDownYear(View view, ArrayAdapter<String> adapter, final AdapterView.OnItemClickListener clickListener)
    {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_year_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // dialog.getWindow().getAttributes().windowAnimations =
        // R.style.DialogAnimation;
        dialog.show();
        final ListView hightListView = (ListView) dialog.findViewById(R.id.lvDialog);
        hightListView.setFastScrollEnabled(true);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        hightListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                dialog.dismiss();
                clickListener.onItemClick(parent, view, position, id);
            }
        });

        hightListView.setAdapter(adapter);

    }

    public static void showDropDownActionBar(View view, ListAdapter adapter, final AdapterView.OnItemClickListener clickListener)
    {
        final ListPopupWindow window = new ListPopupWindow(view.getContext());
        window.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                window.dismiss();
                clickListener.onItemClick(arg0, arg1, arg2, arg3);
            }
        });
        window.setAdapter(adapter);
        window.setModal(true);
        window.setAnchorView(view);
        window.setWidth(ListPopupWindow.WRAP_CONTENT);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.show();
        if (window.isShowing())
            window.getListView().setFastScrollEnabled(true);
    }

	/*
	 * public static void showDropDown(View view, ListAdapter adapter, final
	 * AdapterView.OnItemClickListener clickListener, int position) { final
	 * ListPopupWindow window = new ListPopupWindow(view.getContext());
	 * window.setOnItemClickListener(new OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * arg2, long arg3) { window.dismiss(); clickListener.onItemClick(arg0,
	 * arg1, arg2, arg3); } }); window.setAdapter(adapter);
	 * window.setModal(true); window.setAnchorView(view); window.show(); if
	 * (window.isShowing()) { window.getListView().setDivider( new
	 * ColorDrawable(Color.TRANSPARENT));
	 * window.getListView().setFastScrollEnabled(true); }
	 * 
	 * window.setSelection(position); }
	 */

    public static ListPopupWindow getDropDown(View view, ListAdapter adapter, final AdapterView.OnItemClickListener clickListener)
    {
        final ListPopupWindow window = new ListPopupWindow(view.getContext());
        window.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                window.dismiss();
                clickListener.onItemClick(arg0, arg1, arg2, arg3);
            }
        });
        window.setAdapter(adapter);
        window.setModal(true);
        window.setAnchorView(view);
        window.show();
        if (window.isShowing())
        {
            window.getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
            window.getListView().setFastScrollEnabled(true);
        }

        return window;
    }

    public static String convertUTCDateToNormal(String text)
    {
        String formattedDate = "";
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(text);
            formattedDate += (String) android.text.format.DateFormat.format("dd", myDate) + " ";
            formattedDate += (String) android.text.format.DateFormat.format("MMM", myDate) + " ";
            formattedDate += (String) android.text.format.DateFormat.format("yyyy", myDate);

        } catch (Exception e)
        {
            e.printStackTrace();
            formattedDate = text;
        }
        return formattedDate;
    }

    public static String convertUTCDateToMonthYear(String text)
    {
        String formattedDate = "";
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            Date myDate = simpleDateFormat.parse(text);
            formattedDate += (String) android.text.format.DateFormat.format("MMM", myDate) + " ";
            formattedDate += (String) android.text.format.DateFormat.format("yyyy", myDate);

        } catch (Exception e)
        {
            e.printStackTrace();
            formattedDate = text;
        }
        return formattedDate;
    }

    public static String convertDateToNormal(String text)
    {
        String formattedDate = "";
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            // simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(text);
            formattedDate += (String) android.text.format.DateFormat.format("dd", myDate) + " ";
            formattedDate += (String) android.text.format.DateFormat.format("MMM", myDate) + " ";
            formattedDate += (String) android.text.format.DateFormat.format("yyyy", myDate);

        } catch (Exception e)
        {
            e.printStackTrace();
            formattedDate = text;
        }
        return formattedDate;
    }

    /**
     * converts date from T format to other format
     *
     * @param inputDate
     * @return
     */
    public static Date convertStringToDate(String inputDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date testDate = null;
        Date dLastUpdateDate = null;
        try
        {
            testDate = sdf.parse(inputDate);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String newFormat = formatter.format(testDate);
            dLastUpdateDate = formatter.parse(newFormat);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return dLastUpdateDate;

    }

    public static String convertStringToDatewithMONTHS(String inputDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date testDate = null;
        String newFormat = "";
        try
        {
            testDate = sdf.parse(inputDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            newFormat = formatter.format(testDate);
            // dLastUpdateDate = formatter.parse(newFormat);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return newFormat;
    }

    public static String convertUnixTimestamp(long unixSeconds)
    {
        Date date = new Date(unixSeconds * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating (see comment at the bottom
        return sdf.format(date);
    }

    public static Bitmap getBitmapFromUrl(String url)
    {
        Bitmap bmp = null;
        try
        {

            URL ulrn = new URL(url);
            HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            return bmp;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return bmp;
    }

    public static boolean isTablet(Context context)
    {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static String checkEmpty(String value)
    {
        if (value == null)
            return "";
        else
        {
            value = value.replace("%20", " ");
            if (value.equals("anyType{}") || value.equals("0.00") || value.equals("0.0") || value.equals("0") || value.equals("null"))
                return "";
            else
                return value.trim();
        }
    }

    @SuppressWarnings("unused")
    public long getDateDifference(Date startDate, Date endDate)
    {

        // milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return elapsedDays;
    }

    @SuppressWarnings("static-access")
    public static int dpToPx(Context context, int dp)
    {
        return (int) (dp * context.getResources().getSystem().getDisplayMetrics().density);
    }

    @SuppressWarnings("static-access")
    public static int pxToDp(Context context, int px)
    {
        return (int) (px / context.getResources().getSystem().getDisplayMetrics().density);
    }

    public static String getCDATAString(String input)
    {
        return "<![CDATA[" + input + "]]>";
    }

    public static String getCDATAString(EditText input)
    {
        return "<![CDATA[" + input.getText().toString().trim() + "]]>";
    }

    public static String getString(EditText input)
    {
        return input.getText().toString().trim();
    }

    public static Drawable getIcon(Context c, IconValue iconValue, int sizeInDp)
    {
        return new IconDrawable(c, iconValue).colorRes(R.color.white).sizeDp(sizeInDp);
    }

    public static String getFormattedDistance(String distance)
    {
        try
        {
            if (TextUtils.isEmpty(distance) || distance.equals("anyType{}") || distance.equals("null") || TextUtils.isEmpty(distance))
                return "";
            else
                return formatPrice(distance).replace("R", "");

        } catch (Exception e)
        {
            e.printStackTrace();
            return distance;
        }
    }


    public static String doubleToInt(String value)
    {
        if (value == null)
            return "";
        else
        {
            value = value.replace("%20", " ");
            if (value.equals("anyType{}") || value.equals("0.00") || value.equals("0.0") || value.equals("0") || value.equals("null") || value.equals("(null)"))
                return "";
            else
                return BigDecimal.valueOf((long) Double.parseDouble(value.trim())).toPlainString();

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String downloadImage(String imageUrl)
    {
        String filepath = null;
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.connect();
            File SDCardRoot = Environment.getExternalStorageDirectory();
            String filename = "downloadedFile.png";
            Log("Local filename:", "" + filename);
            File file = new File(SDCardRoot, filename);
            if (file.createNewFile())
            {
                file.createNewFile();
            }
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0)
            {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                Log("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
            }
            fileOutput.close();
            if (downloadedSize == totalSize)
                filepath = file.getPath();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            filepath = null;
            e.printStackTrace();
        }
        Log("filepath:", " " + filepath);
        return filepath;
    }

    public static boolean deleteFile(String filepath)
    {
        boolean result = false;
        File file = new File(filepath);
        if (file.exists())
            result = file.delete();
        return result;
    }

    public static void setSwipeListViewHeightBasedOnChildren(SwipeListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    public static String getFormattedYearRange(String yearRange)
    {
        String formattedYear = "", firstNew, secondNew;
        StringTokenizer tokenizer = new StringTokenizer(yearRange, "-");
        String first = tokenizer.nextToken();
        String second = tokenizer.nextToken();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy", Locale.US);
        SimpleDateFormat formatedDate = new SimpleDateFormat("yy", Locale.US);
        try
        {
            Date date = formatter.parse(first);
            firstNew = formatedDate.format(date);

            date = formatter.parse(second);
            secondNew = formatedDate.format(date);

            formattedYear = "'" + firstNew + "-" + secondNew + "'";
        } catch (Exception e)
        {
            e.printStackTrace();
            return yearRange;
        }
        return formattedYear;
    }

    public static String getDoubleValue(EditText editext)
    {

        double value = 0.0000;
        if (!TextUtils.isEmpty(getString(editext)))
            value = Double.parseDouble(getString(editext));
        return String.format(Locale.US, "%.2f", value);
    }

    @SuppressLint("SdCardPath")
    public static void writeFile(String tag, String data)
    {
        FileWriter f;
        try
        {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.LOG_FILE);
            if (!file.exists())
                file.createNewFile();
            f = new FileWriter(file, true);
            f.write("\n ------------------------------------" + tag + "------------------------------------------------- \n" + data + "\n");
            f.flush();
            f.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public static void flushFileContents()
    {
        FileWriter f;
        try
        {
            File file = new File(Environment.getExternalStorageDirectory() + "/SM-log" + ".txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            f = new FileWriter(file, false);
            f.write("");
            f.flush();
            f.close();
        } catch (IOException e)
        {
            e.printStackTrace();

        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public static String getTimeAgo(String data)
    {
        // Helper.Log("getTimeAgo input", ""+data);

        if (TextUtils.isEmpty(data))
            return "N/A";

        String[] time = data.split(":");
        int day = Integer.parseInt(time[0]);
        int hour = Integer.parseInt(time[1]);
        int minute = Integer.parseInt(time[2]);
        // int sec=Integer.parseInt(time[2]);

        // Helper.Log("getTime","HH: "+hour+" MM: "+minute+" SS: "+sec);

        if (day > 0)
        {
            // hours are greater
            if (day < 10)
                return day + " day ago";
            else
                return day + " days ago";

        } else if (hour > 0)
        {
            // hours are greater
            if (hour < 10)
                return hour + " hour ago";
            else
                return hour + " hours ago";

        } else if (minute > 0)
        {
            if (minute < 10)
                return minute + " minute ago";
            else
                return minute + " minutes ago";
        }
		/*
		 * else if(sec>0) { if(sec<10) return sec+" second ago"; else return
		 * sec+" seconds ago"; }
		 */
        else
        {
            return " just now";
        }
    }

    public static String getTimeLastUpdate(String data)
    {
        // Helper.Log("getTimeAgo input", ""+data);

        if (TextUtils.isEmpty(data))
            return "N/A";

        String[] time = data.split(":");
        int day = Integer.parseInt(time[0]);
        int hour = Integer.parseInt(time[1]);
        int minute = Integer.parseInt(time[2]);
        // int sec=Integer.parseInt(time[2]);

        // Helper.Log("getTime","HH: "+hour+" MM: "+minute+" SS: "+sec);

        if (day > 0)
        {
            // hours are greater
            if (day < 10)
                return day + " day ";
            else
                return day + " days ";

        } else if (hour > 0)
        {
            // hours are greater
            if (hour < 10)
                return hour + " hour ";
            else
                return hour + " hours ";

        } else if (minute > 0)
        {
            if (minute < 10)
                return minute + " minute ";
            else
                return minute + " minutes ";
        } else
        {
            return " ";
        }

    }

    public static String getTimeHoursAndMinutes(String data)
    {
        // Helper.Log("getTimeAgo input", ""+data);

        if (TextUtils.isEmpty(data))
            return "N/A";
        String finalTime = "";
        String[] time = data.split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        // int sec=Integer.parseInt(time[2]);
        DecimalFormat formatter = new DecimalFormat("00");

        // Helper.Log("getTime","HH: "+hour+" MM: "+minute+" SS: "+sec);
        finalTime = formatter.format(hour) + "h" + formatter.format(minute) + "m";

        return finalTime;
    }

    public static int getColorTimeAgo(String data)
    {
        // Helper.Log("getTimeAgo input", ""+data);
        String[] time = data.split(":");

        int day = Integer.parseInt(time[0]);
        int hour = Integer.parseInt(time[1]);
        int minute = Integer.parseInt(time[2]);
        if (day > 0)
        {
            // hours are greater
            return Color.RED;
        } else if (hour > 0)
        {
            return Color.RED;

        } else if (minute > 0)
        {
            if (minute < 5)
                return Color.GREEN;
            else
                return Color.RED;
        } else
        {
            return Color.GREEN;
        }
    }

    public static int getColorTimeAgoWIP(String data)
    {
        // Helper.Log("getTimeAgo input", ""+data);
        String[] time = data.split(":");
        int day = Integer.parseInt(time[0]);
        // int hour=Integer.parseInt(time[1]);
        // int minute=Integer.parseInt(time[2]);
        if (day > 7)
        {
            // hours are greater
            return Color.RED;
        } else
        {
            return Color.GREEN;
        }

    }

    public static VehicleType getVehicleType(String vehicle)
    {
        if (!TextUtils.isEmpty(vehicle))
        {
            if (vehicle.equalsIgnoreCase("Used"))
                return VehicleType.Used;
            else if (vehicle.equalsIgnoreCase("New"))
                return VehicleType.New;
            else
                return VehicleType.New;
        } else
            return VehicleType.New;
    }

    public static long getDateInMillis(String srcDate)
    {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        long dateInMillis = 0;
        try
        {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static String setPrefix(String value)
    {
        switch (value)
        {
            case "1":
                value = value + "st";
                break;
            case "2":
                value = value + "nd";
                break;
            case "3":
                value = value + "rd";
                break;

            default:
                value = value + "th";
                break;
        }
        return value;

    }

    public static Float floatFromStringOrZero(String s)
    {
        Float val = Float.valueOf(0);
        try
        {
            val = Float.valueOf(s);
        } catch (NumberFormatException ex)
        {
            DecimalFormat df = new DecimalFormat();
            Number n = null;
            try
            {
                n = df.parse(s);
            } catch (ParseException ex2)
            {
            } catch (java.text.ParseException e)
            {
                e.printStackTrace();
            }
            if (n != null)
                val = n.floatValue();
        }
        return val;
    }

    public static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "mremploy");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
    public static String getPath(Uri uri, Activity activity) {

        Cursor cursor = null;
        int column_index = 0;
        try {

            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = activity.managedQuery(uri, projection, null, null, null);
            if(cursor!=null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
        return cursor.getString(column_index);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getImagePathFromGalleryAboveKitkat(Context cntxt, Uri uri) {
        if (uri == null) {
            return null;
        }

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = null;
        if (Build.VERSION.SDK_INT > 19) {
            try {
                // Will return "image:x*"
                String wholeID = DocumentsContract.getDocumentId(uri);
                // Split at colon, use second item in the array
                if (!wholeID.contains(":"))
                    return null;

                String id = wholeID.split(":")[1];
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";
                cursor = cntxt.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, sel, new String[]{id}, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            cursor = cntxt.getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        } catch (NullPointerException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
        return path;
    }

    public static void showCropDialog(CharSequence msg, Context ctx) {
        if (ctx != null) {
            CustomDialogManager.showOkDialog(ctx, msg.toString(), new DialogListener() {
                @Override
                public void onButtonClicked(int type) {

                }
            });
        }
    }


}
