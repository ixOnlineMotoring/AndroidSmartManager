package com.nw.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nw.interfaces.DialogInputListener;
import com.nw.interfaces.DialogListener;
import com.smartmanager.android.R;
import com.utils.Helper;

public class CustomDialogManager
{

    static AlertDialog okCancelDialog;

    public static void showOkCancelDialog(Context context, final DialogListener dialogListener)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog okCancelDialog;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_yes_no, null);
        build.setView(view);
        okCancelDialog = build.create();
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        final Button btnNo = (Button) view.findViewById(R.id.btnNo);
        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
                if (dialogListener != null)
                    dialogListener.onButtonClicked(Dialog.BUTTON_POSITIVE);
            }
        });

        btnNo.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
                if (dialogListener != null)
                    dialogListener.onButtonClicked(Dialog.BUTTON_NEGATIVE);
            }

        });
        tvTitle.setText("");
        tvMessage.setText("");
        okCancelDialog.show();
    }

    public static void showOkCancelDialog(Context context, String message, final DialogListener dialogListener)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog okCancelDialog;
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_yes_no, null);
        build.setView(view);
        okCancelDialog = build.create();
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        btnYes.setText("Yes");
        final Button btnNo = (Button) view.findViewById(R.id.btnNo);
        btnNo.setText("No");

        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
                if (dialogListener != null)
                    dialogListener.onButtonClicked(Dialog.BUTTON_POSITIVE);
            }
        });

        btnNo.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
                if (dialogListener != null)
                    dialogListener.onButtonClicked(Dialog.BUTTON_NEGATIVE);
            }

        });
        tvTitle.setText(R.string.app_name);
        okCancelDialog.setCancelable(false);
        if (TextUtils.isEmpty(message))
            tvMessage.setText("");
        else
            tvMessage.setText(message);

        okCancelDialog.show();
    }

    public static void showOkCancelDialog(Context context, String message, String yesButtonMessage, String noButtonMessage,
                                          final DialogListener dialogListener)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog okCancelDialog;
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_yes_no, null);
        build.setView(view);
        okCancelDialog = build.create();
        okCancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        btnYes.setText(yesButtonMessage);
        final Button btnNo = (Button) view.findViewById(R.id.btnNo);
        btnNo.setText(noButtonMessage);

        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
                if (dialogListener != null)
                    dialogListener.onButtonClicked(Dialog.BUTTON_POSITIVE);
            }
        });

        btnNo.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
                if (dialogListener != null)
                    dialogListener.onButtonClicked(Dialog.BUTTON_NEGATIVE);
            }

        });
        tvTitle.setText(R.string.app_name);
        if (TextUtils.isEmpty(message))
            tvMessage.setText("");
        else
            tvMessage.setText(message);

        okCancelDialog.setCancelable(false);
        okCancelDialog.show();
    }

    public static void showOkDialog(Context context, String message)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_yes, null);
        build.setView(view);
        okCancelDialog = build.create();
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        btnYes.setText("Ok");
        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });

        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });
        tvTitle.setText(R.string.app_name);

        if (TextUtils.isEmpty(message))
            tvMessage.setText("");
        else
            tvMessage.setText(message);

        okCancelDialog.show();
    }

    public static void cancleDialog()
    {
        if (okCancelDialog != null)
        {
            if (okCancelDialog.isShowing())
                okCancelDialog.dismiss();
        }
    }

    public static void showOkDialogAutoCancel(Context context, String message)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog okCancelDialog;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_yes, null);
        build.setView(view);
        okCancelDialog = build.create();
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        btnYes.setText("Ok");
    /*	Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				okCancelDialog.dismiss();
			}
		}, 3000);*/
        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });

        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });
        tvTitle.setText(R.string.app_name);

        if (TextUtils.isEmpty(message))
            tvMessage.setText("");
        else
            tvMessage.setText(message);

        okCancelDialog.show();
    }

    public static void showOkDialog(Context context, CharSequence message, final DialogListener dialogListener)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog okCancelDialog;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_yes, null);
        build.setView(view);
        okCancelDialog = build.create();
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        btnYes.setText("Ok");

        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
                if (dialogListener != null)
                    dialogListener.onButtonClicked(Dialog.BUTTON_POSITIVE);
            }
        });
        ivClose.setVisibility(View.GONE);
        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });
        tvTitle.setText(R.string.app_name);
        if (TextUtils.isEmpty(message))
            tvMessage.setText("");
        else
            tvMessage.setText(message);
        okCancelDialog.setCancelable(false);
        okCancelDialog.show();
    }

    public static void showOkDialog(Context context, CharSequence title,
                                    CharSequence message)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog okCancelDialog;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_yes,
                null);
        build.setView(view);
        okCancelDialog = build.create();
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        btnYes.setText("Ok");

        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });

        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });
        if (TextUtils.isEmpty(title))
            tvTitle.setText(R.string.app_name);
        else
            tvTitle.setText(title);

        if (TextUtils.isEmpty(message))
            tvMessage.setText("");
        else
            tvMessage.setText(message);

        okCancelDialog.show();
    }

    public static Dialog showProgressDialog(Context context)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.vw_custom_progress_bar);
        WebView imageView = (WebView) dialog.findViewById(R.id.imageView1);
        imageView.setBackgroundColor(Color.TRANSPARENT); //for gif without background
        imageView.loadUrl("file:///android_asset/html/logo_h.html");
        /*imageView.setBackgroundResource(R.drawable.logo_animation);
        // Get the background, which has been compiled to an AnimationDrawable object.
		 AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();

		 // Start the animation (looped playback by default).
		 frameAnimation.start();*/
		/*GifAnimationDrawable little;
		try 
		{
			little = new GifAnimationDrawable(context.getResources().openRawResource(R.raw.ix_logo));
			little.setOneShot(false);
			imageView.setImageDrawable(little);
			little.setVisible(true, true);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/


        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static Dialog showProgressDialog(Context context, String title)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.vw_custom_progress_bar);
        ((TextView) dialog.findViewById(R.id.login_status_message)).setText(title);

        WebView webView = (WebView) dialog.findViewById(R.id.imageView1);
        webView.setBackgroundColor(Color.TRANSPARENT); //for gif without background
        webView.loadUrl("file:///android_asset/html/logo_h.html");
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static void showErrorDialog(final Context context, String message)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog okCancelDialog;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_error_message, null);
        build.setView(view);
        okCancelDialog = build.create();
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        TextView mContactView = (TextView) view.findViewById(R.id.tvContact);
        TextView mContactPhoneView = (TextView) view.findViewById(R.id.tvPhone);

        mContactView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:support@ix.co.za"));
                emailIntent.setType("message/rfc822");
                String aEmailList[] = {"support@ix.co.za"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Smart Manager");

                try
                {
                    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        mContactPhoneView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                String posted_by = "0861 292 999";
                String uri = "tel:" + posted_by.trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                context.startActivity(intent);
            }
        });

        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        btnYes.setText("Ok");
        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });

        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });
        tvTitle.setText(R.string.app_name);

        if (TextUtils.isEmpty(message))
            tvMessage.setText("");
        else
            tvMessage.setText(message);

        okCancelDialog.show();
    }

    public static void showErrorDialogEmail(final Context context, String message)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog errorDialog;
        View view = LayoutInflater.from(context).inflate(R.layout.login_failed_dialog, null);
        build.setView(view);
        errorDialog = build.create();

        Button btnCancel = (Button) view.findViewById(R.id.btnCancle);
        TextView errorMessage = (TextView) view.findViewById(R.id.tvMessage);
        errorMessage.setText(message);
        TextView mContactView = (TextView) view.findViewById(R.id.tvContact);
        TextView mContactPhoneView = (TextView) view.findViewById(R.id.tvPhone);

        mContactView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:support@ix.co.za"));
                emailIntent.setType("message/rfc822");
                String aEmailList[] = {"support@ix.co.za"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Smart Manager");

                try
                {
                    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        mContactPhoneView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                String posted_by = "0861 292 999";
                String uri = "tel:" + posted_by.trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                context.startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                errorDialog.dismiss();
            }
        });
        errorDialog.show();
    }

    public static void showOkCancelDialogWithInputBox(final Context context, String hintMessage, final DialogInputListener dialogListener)
    {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        final AlertDialog okCancelDialog;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_yes_no_with_inputbox, null);
        build.setView(view);
        okCancelDialog = build.create();
        final TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        final EditText edReason = (EditText) view.findViewById(R.id.edReason);
        edReason.setHint(hintMessage);
        edReason.setHintTextColor(context.getResources().getColor(R.color.gray));
        final ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
        final Button btnYes = (Button) view.findViewById(R.id.btnYes);
        btnYes.setText("Send");
        final Button btnNo = (Button) view.findViewById(R.id.btnNo);
        btnNo.setText("Cancel");

        ivClose.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(edReason.getText().toString()))
                {
                    Helper.showToast("Please enter your reason", context);
                    return;
                }

                okCancelDialog.dismiss();
                if (dialogListener != null)
                    dialogListener.onButtonClicked(Dialog.BUTTON_POSITIVE, edReason.getText().toString());
            }
        });

        btnNo.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okCancelDialog.dismiss();
            }

        });
        tvTitle.setText(R.string.app_name);
        okCancelDialog.show();
    }
}
