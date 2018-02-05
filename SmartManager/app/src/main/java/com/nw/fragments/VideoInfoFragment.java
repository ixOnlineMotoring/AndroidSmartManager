package com.nw.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nw.interfaces.DialogListener;
import com.nw.model.YouTubeVideo;
import com.nw.webservice.DataManager;
import com.nw.widget.CustomButton;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.smartmanager.activity.PlayerActivity;
import com.smartmanager.android.R;
import com.utils.Helper;

public class VideoInfoFragment extends BaseFragement implements OnClickListener
{
    String videoPath, default_data;
    ImageView ivVideoThumbnail, ivVideoPlay;
    CustomEditText etTitle, etTags, etDescription;
    CustomButton bnSaveInfo;
    CheckBox chkSearchble;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_video_info, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            videoPath = getArguments().getString("videoPath");
            default_data = getArguments().getString("default_name");
        }
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, Thumbnails.MINI_KIND);
        init(view);
        return view;
    }

    private void init(View view)
    {
        ivVideoThumbnail = (ImageView) view.findViewById(R.id.ivVideoThumbnail);
        ivVideoThumbnail.setImageBitmap(bitmap);
        ivVideoPlay = (ImageView) view.findViewById(R.id.ivVideoPlay);
        etDescription = (CustomEditText) view.findViewById(R.id.etDescription);
        etTitle = (CustomEditText) view.findViewById(R.id.etTitle);
        etTags = (CustomEditText) view.findViewById(R.id.etTags);
        etTitle.setText(default_data);
        etTags.setText(default_data);
        etDescription.setText(default_data);
        bnSaveInfo = (CustomButton) view.findViewById(R.id.btnSaveInfo);
        chkSearchble = (CheckBox) view.findViewById(R.id.chkSearchable);
        bnSaveInfo.setOnClickListener(this);
        ivVideoPlay.setOnClickListener(this);


        if (getArguments() != null)
        {
            if (getArguments().containsKey("from_ebrochure"))
            {
                chkSearchble.setChecked(false);
            } else
            {
                chkSearchble.setChecked(true);
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Video Info");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (!isInformationFilled())
                {
                    onBackPressed();
                } else
                {
                    getActivity().getFragmentManager().popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isInformationFilled()
    {
        if (TextUtils.isEmpty(etTitle.getText().toString().trim()))
        {
            return false;
        }
        if (TextUtils.isEmpty(etTags.getText().toString().trim()))
        {
            return false;
        }
        if (TextUtils.isEmpty(etDescription.getText().toString().trim()))
        {
            return false;
        }
        return true;

    }


    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.btnSaveInfo:
                if (TextUtils.isEmpty(etTitle.getText().toString().trim()))
                {
                    Helper.showToast("Enter Video Title", getActivity());
                    return;
                }
                if (TextUtils.isEmpty(etTags.getText().toString().trim()))
                {
                    Helper.showToast("Enter Video Tags", getActivity());
                    return;
                }
                if (TextUtils.isEmpty(etDescription.getText().toString().trim()))
                {
                    Helper.showToast("Enter Video Description", getActivity());
                    return;
                }
                YouTubeVideo tubeVideo = new YouTubeVideo();
                tubeVideo.setVideo_title(etTitle.getText().toString().trim());
                tubeVideo.setVideo_Tags(etTags.getText().toString().trim());
                tubeVideo.setVideo_Description(etDescription.getText().toString().trim());
                tubeVideo.setSearchable(chkSearchble.isChecked());
                tubeVideo.setVideoFullPath(videoPath);
                tubeVideo.setVideoThumbUrl(videoPath);
                tubeVideo.setLocal(true);
                DataManager.getInstance().getYouTubeVideos().add(tubeVideo);
                if (getFragmentManager().getBackStackEntryCount() != 0)
                {
                    getFragmentManager().popBackStack();
                } else

                {
                    getActivity().finish();
                }

                break;

            case R.id.ivVideoPlay:
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra("video_url", videoPath);
                startActivity(intent);
                break;

        }

    }

    public void onBackPressed()
    {
        if (!isInformationFilled())
        {
            CustomDialogManager.showOkCancelDialog(getActivity(),
                    "You have not added any video information do you want to continue?", new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            if (type == Dialog.BUTTON_POSITIVE)
                            {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        }
                    });
        } else
        {
            getActivity().getFragmentManager().popBackStack();
        }
    }
}
