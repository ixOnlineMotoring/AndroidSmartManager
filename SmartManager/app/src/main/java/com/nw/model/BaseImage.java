package com.nw.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class BaseImage implements Parcelable
{

    int id;
    int priority;
    String link;
    int type = -1;
    boolean local;
    boolean doc;
    String path;
    String thumbPath;
    String caption;
    String uri;
    boolean createNewFile = true;
    Uri imageUri;
    public boolean seleted = false;
    String docPath;

    public BaseImage(Parcel source)
    {
        readParcel(source);
    }

    public BaseImage()
    {
    }

    public BaseImage(String link, int id)
    {
        this.link = link;
        this.local = false;
        this.doc = false;
        this.type = 0;
        this.id = id;
        this.docPath = "";

    }

    public String getDocPath()
    {
        if (docPath == null)
            return "";
        else
            return docPath;
    }

    public void setDocPath(String docPath)
    {
        this.docPath = docPath;
    }

    public String getThumbPath()
    {
        if (thumbPath == null)
            return "";
        else
            return thumbPath;
    }

    public void setThumbPath(String thumbPath)
    {
        this.thumbPath = thumbPath;
    }

    public String getPath()
    {
        if (path == null)
            return "";
        else
            return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isLocal()
    {
        return local;
    }

    public void setLocal(boolean local)
    {
        this.local = local;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public String getLink()
    {
        if (link == null)
            return "";
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public boolean isDoc()
    {
        return doc;
    }

    public void setDoc(boolean doc)
    {
        this.doc = doc;
    }

    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getUri()
    {
        if (uri == null)
            return "";
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public void setImageUri(Uri imageUri)
    {
        this.imageUri = imageUri;
    }

    public Uri getImageUri()
    {
        return imageUri;
    }

    public boolean isCreateNewFile()
    {
        return createNewFile;
    }

    public void setCreateNewFile(boolean createNewFile)
    {
        this.createNewFile = createNewFile;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeInt(priority);
        dest.writeString((link == null) ? "" : link);
        dest.writeInt(type);
        dest.writeInt((local == true) ? 1 : 0);
        dest.writeString((path == null ? "" : path));
        dest.writeString((thumbPath == null) ? "" : thumbPath);
        dest.writeString((caption == null) ? "" : caption);
        dest.writeString((uri == null) ? "" : uri);
        dest.writeInt((createNewFile == true) ? 1 : 0);
        dest.writeValue(imageUri);
        dest.writeInt((seleted == true) ? 1 : 0);
        dest.writeInt((doc == true) ? 1 : 0);
        dest.writeString(docPath);
    }

    public String getCaption()
    {
        if (caption == null)
            return "";
        return caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    public boolean isSeleted()
    {
        return seleted;
    }

    public void setSeleted(boolean seleted)
    {
        this.seleted = seleted;
    }


    public void readParcel(Parcel in)
    {
        id = in.readInt();
        priority = in.readInt();
        link = in.readString();
        type = in.readInt();
        local = (in.readInt() != 0 ? true : false);
        path = in.readString();
        thumbPath = in.readString();
        caption = in.readString();
        uri = in.readString();
        createNewFile = (in.readInt() != 0 ? true : false);
        imageUri = (Uri) in.readValue(Uri.class.getClassLoader());
        seleted = (in.readInt() != 0 ? true : false);
        doc = (in.readInt() != 0 ? true : false);
        docPath = in.readString();
    }

    public static final Creator<BaseImage> CREATOR = new Creator<BaseImage>()
    {

        @Override
        public BaseImage createFromParcel(Parcel source)
        {
            // TODO Auto-generated method stub
            return new BaseImage(source);
        }

        @Override
        public BaseImage[] newArray(int size)
        {
            // TODO Auto-generated method stub
            return new BaseImage[size];
        }
    };

}
