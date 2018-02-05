package com.nw.model;

/**
 * Created by Swapnil on 07-02-2017.
 */

public class NotificationListing
{
    private String message;

    private String identity;

    private String sent;

    private String source;

    private String MessageLogID;

    private String readDate;

    private String heading;

    private boolean isRead;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        if (message.equals("anyType{}"))
        {
            this.message = "";
        } else
        {
            this.message = message;
        }
    }

    public String getIdentity()
    {
        return identity;
    }

    public void setIdentity(String identity)
    {
        if (identity.equals("anyType{}"))
        {
            this.identity = "";
        } else
        {
            this.identity = identity;
        }
    }

    public String getSent()
    {
        return sent;
    }

    public void setSent(String sent)
    {
        if (sent.equals("anyType{}"))
        {
            this.sent = "";
        } else
        {
            this.sent = sent;
        }
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        if (source.equals("anyType{}"))
        {
            this.source = "";
        } else
        {
            this.source = source;
        }
    }

    public String getMessageLogID()
    {
        return MessageLogID;
    }

    public void setMessageLogID(String MessageLogID)
    {
        if (MessageLogID.equals("anyType{}"))
        {
            this.MessageLogID = "";
        } else
        {
            this.MessageLogID = MessageLogID;
        }
    }

    public String getReadDate()
    {
        return readDate;
    }

    public void setReadDate(String readDate)
    {
        if (readDate.equals("anyType{}"))
        {
            this.readDate = "";
        } else
        {
            this.readDate = readDate;
        }
    }

    public String getHeading()
    {
        return heading;
    }

    public void setHeading(String heading)
    {
        if (heading.equals("anyType{}"))
        {
            this.heading = "";
        } else
        {
            this.heading = heading;
        }
    }

    public boolean getIsRead()
    {
        return isRead;
    }

    public void setIsRead(boolean isRead)
    {
        this.isRead = isRead;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = " + message + ", identity = " + identity + ", sent = " + sent + ", source = " + source + ", MessageLogID = " + MessageLogID + ", readDate = " + readDate + ", heading = " + heading + ", isRead = " + isRead + "]";
    }
}

