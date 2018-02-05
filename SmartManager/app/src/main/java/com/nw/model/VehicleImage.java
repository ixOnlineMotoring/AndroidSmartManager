package com.nw.model;

import android.os.Parcel;

public class VehicleImage extends BaseImage
{


    int uciid;
    String imageTitle;
    int imagedpi;
    String imageRes;
    int imageSize;
    String imageSource;
    String imageTypeName;

    public VehicleImage()
    {
    }

    public VehicleImage(Parcel source)
    {
        //super(source);
        // TODO Auto-generated constructor stub
        readParcel(source);
    }

    public int getUciid()
    {
        return uciid;
    }

    public void setUciid(int uciid)
    {
        this.uciid = uciid;
    }

    public String getImageTitle()
    {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle)
    {
        this.imageTitle = imageTitle;
    }

    public int getImagedpi()
    {
        return imagedpi;
    }

    public void setImagedpi(int imagedpi)
    {
        this.imagedpi = imagedpi;
    }

    public String getImageRes()
    {
        return imageRes;
    }

    public void setImageRes(String imageRes)
    {
        this.imageRes = imageRes;
    }

    public int getImageSize()
    {
        return imageSize;
    }

    public void setImageSize(int imageSize)
    {
        this.imageSize = imageSize;
    }

    public String getImageSource()
    {
        return imageSource;
    }

    public void setImageSource(String imageSource)
    {
        this.imageSource = imageSource;
    }

    public String getImageTypeName()
    {
        return imageTypeName;
    }

    public void setImageTypeName(String imageTypeName)
    {
        this.imageTypeName = imageTypeName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        // TODO Auto-generated method stub
        //super.writeToParcel(dest, flags);

        dest.writeInt(uciid);
        dest.writeString(imageTitle);
        dest.writeInt(imagedpi);
        dest.writeString(imageRes);
        dest.writeInt(imageSize);
        dest.writeString(imageSource);
        dest.writeString(imageTypeName);
        dest.writeString(link);
        dest.writeInt(id);
    }

    @Override
    public void readParcel(Parcel in)
    {
        uciid = in.readInt();
        imageTitle = in.readString();
        imagedpi = in.readInt();
        imageRes = in.readString();
        imageSize = in.readInt();
        imageSource = in.readString();
        imageTypeName = in.readString();
        link = in.readString();
        id = in.readInt();
    }

    public static final Creator<VehicleImage> CREATOR = new Creator<VehicleImage>()
    {

        @Override
        public VehicleImage createFromParcel(Parcel source)
        {
            // TODO Auto-generated method stub
            return new VehicleImage(source);
        }

        @Override
        public VehicleImage[] newArray(int size)
        {
            // TODO Auto-generated method stub
            return new VehicleImage[size];
        }


    };

    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
