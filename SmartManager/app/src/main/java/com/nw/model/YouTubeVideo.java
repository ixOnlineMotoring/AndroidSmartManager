package com.nw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class YouTubeVideo implements Parcelable 
{
	private String videoFullPath;
	private String videoCode;
	private String videoThumbUrl;
	private String video_Description;
	private String video_Tags;
	private String video_title;
	private boolean isSearchable;
	private String video_ID;
	private boolean isLocal;
	private int videoLinkID;
	
	public YouTubeVideo()
	{

	}

    public String getVideoFullPath()
	{
	
		return videoFullPath;
	}

	public void setVideoFullPath(String videoFullPath)
	{
	
		this.videoFullPath = videoFullPath;
	}

	public String getVideoCode()
	{
	
		return videoCode;
	}

	public void setVideoCode(String videoCode)
	{
	
		this.videoCode = videoCode;
	}

	public String getVideoThumbUrl()
	{
		return videoThumbUrl;
	}

	public void setVideoThumbUrl(String videoThumbUrl)
	{
	
		this.videoThumbUrl = videoThumbUrl;
	}

	
    public String getVideo_Description()
	{

		return video_Description;
	}

	public void setVideo_Description(String video_Description)
	{

		this.video_Description = video_Description;
	}

	public String getVideo_Tags()
	{

		return video_Tags;
	}

	public void setVideo_Tags(String video_Tags)
	{

		this.video_Tags = video_Tags;
	}

	public String getVideo_title()
	{

		return video_title;
	}

	public void setVideo_title(String video_title)
	{

		this.video_title = video_title;
	}

	public boolean isSearchable()
	{
		return isSearchable;
	}

	public void setSearchable(boolean isSearchable)
	{
		this.isSearchable = isSearchable;
	}
	
	 protected YouTubeVideo(Parcel in) {
	        videoFullPath = in.readString();
	        videoCode = in.readString();
	        videoThumbUrl = in.readString();
	        video_Description = in.readString();
	        video_Tags = in.readString();
	        video_ID = in.readString();
	        video_title = in.readString();
	        videoLinkID = in.readInt();
	        isSearchable = in.readByte() != 0x00;
	        isLocal = in.readByte() != 0x00;
	    }

	    @Override
	    public int describeContents() {
	        return 0;
	    }

	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        dest.writeString(videoFullPath);
	        dest.writeString(videoCode);
	        dest.writeString(videoThumbUrl);
	        dest.writeString(video_Description);
	        dest.writeString(video_Tags);
	        dest.writeString(video_title);
	        dest.writeInt(videoLinkID);
	        dest.writeString(video_ID);
	        dest.writeByte((byte) (isSearchable ? 0x01 : 0x00));
	        dest.writeByte((byte) (isLocal ? 0x01 : 0x00));
	    }

	    public String getVideo_ID()
		{

			return video_ID;
		}

		public void setVideo_ID(String video_ID)
		{

			this.video_ID = video_ID;
		}

		public boolean isLocal()
		{

			return isLocal;
		}

		public void setLocal(boolean isLocal)
		{

			this.isLocal = isLocal;
		}

		public int getVideoLinkID()
		{

			return videoLinkID;
		}

		public void setVideoLinkID(int videoLinkID)
		{

			this.videoLinkID = videoLinkID;
		}

		public static final Creator<YouTubeVideo> CREATOR = new Creator<YouTubeVideo>() {
	        @Override
	        public YouTubeVideo createFromParcel(Parcel in) {
	            return new YouTubeVideo(in);
	        }

	        @Override
	        public YouTubeVideo[] newArray(int size) {
	            return new YouTubeVideo[size];
	        }
	    };
}