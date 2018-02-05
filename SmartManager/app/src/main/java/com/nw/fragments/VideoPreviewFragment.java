package com.nw.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
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
import com.smartmanager.android.staging.SmartManagerApplication;

public class VideoPreviewFragment extends BaseFragement implements OnClickListener
{
    ImageView ivVideoThumbnail, ivVideoPlay;
    CustomEditText etTitle, etTags, etDescription;
    CustomButton bnSaveInfo;
    CheckBox chkSearchble;
    Bitmap bitmap;
    YouTubeVideo tubeVideo, video;
    int position = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_video_info, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            tubeVideo = getArguments().getParcelable("videoToPreview");
            position = getArguments().getInt("position");
        }
        init(view);
        putValue(tubeVideo);
        return view;
    }

    private void init(View view)
    {
        ivVideoThumbnail = (ImageView) view.findViewById(R.id.ivVideoThumbnail);
        ivVideoPlay = (ImageView) view.findViewById(R.id.ivVideoPlay);
        etDescription = (CustomEditText) view.findViewById(R.id.etDescription);
        etTitle = (CustomEditText) view.findViewById(R.id.etTitle);
        etTags = (CustomEditText) view.findViewById(R.id.etTags);
        bnSaveInfo = (CustomButton) view.findViewById(R.id.btnSaveInfo);
        chkSearchble = (CheckBox) view.findViewById(R.id.chkSearchable);
        ivVideoPlay.setOnClickListener(this);
        bnSaveInfo.setOnClickListener(this);
    }

    @SuppressWarnings("static-access")
    private void putValue(YouTubeVideo tubeVideo)
    {
        if (tubeVideo.isLocal())
        {
            etDescription.setEnabled(true);
            etTitle.setEnabled(true);
            etTags.setEnabled(true);
            chkSearchble.setClickable(true);
            bnSaveInfo.setVisibility(View.VISIBLE);
            bitmap = ThumbnailUtils.createVideoThumbnail(tubeVideo.getVideoThumbUrl(), Thumbnails.MINI_KIND);
            ivVideoThumbnail.setImageBitmap(bitmap);
        } else
        {
            etDescription.setEnabled(false);
            etTitle.setEnabled(false);
            bnSaveInfo.setVisibility(View.GONE);
            etTags.setEnabled(false);
            chkSearchble.setClickable(false);
            SmartManagerApplication.getInstance().getImageLoader().displayImage(tubeVideo.getVideoThumbUrl(), ivVideoThumbnail);
        }
        etDescription.setText(tubeVideo.getVideo_Description());
        etTitle.setText(tubeVideo.getVideo_title());
        etTags.setText(tubeVideo.getVideo_Tags());
        chkSearchble.setChecked(tubeVideo.isSearchable());
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


    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {

            case R.id.ivVideoPlay:

                if (!tubeVideo.isLocal())
                {
                    watchYoutubeVideo(tubeVideo.getVideoCode());
                } else
                {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("video_url", tubeVideo.getVideoFullPath());
                    startActivity(intent);
                }

                break;
            case R.id.btnSaveInfo:

                if (tubeVideo.isLocal())
                {
                    ///video = getVideoByName(DataManager.getInstance().getYouTubeVideos(), tubeVideo.getVideo_title());
                    video = new YouTubeVideo();
                    video.setVideo_title(etTitle.getText().toString().trim());
                    video.setVideo_Tags(etTags.getText().toString().trim());
                    video.setVideo_Description(etDescription.getText().toString().trim());
                    video.setSearchable(chkSearchble.isChecked());
                    video.setVideoFullPath(tubeVideo.getVideoFullPath());
                    video.setVideoThumbUrl(tubeVideo.getVideoFullPath());
                    video.setLocal(true);
                    DataManager.getInstance().getYouTubeVideos().set(position, video);
                    if (getFragmentManager().getBackStackEntryCount() != 0)
                    {
                        getFragmentManager().popBackStack();
                    } else

                    {
                        getActivity().finish();
                    }
                }

                break;

        }
    }

    private void watchYoutubeVideo(String id)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
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
        }
    }

}
