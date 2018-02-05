package com.nw.webservice;

import android.text.TextUtils;
import android.text.format.Time;
import android.util.Xml;

import com.nw.model.BaseImage;
import com.nw.model.Blog;
import com.nw.model.BlogImage;
import com.nw.model.BlogType;
import com.nw.model.Client;
import com.nw.model.Impersonate;
import com.nw.model.Leads;
import com.nw.model.LogClient;
import com.nw.model.Member;
import com.nw.model.Module;
import com.nw.model.Page;
import com.nw.model.Person;
import com.nw.model.PlannerType;
import com.nw.model.Reference;
import com.nw.model.ReferenceID;
import com.nw.model.Request;
import com.nw.model.RequestUser;
import com.nw.model.SmartObject;
import com.nw.model.SpecialVehicle;
import com.nw.model.SubModule;
import com.nw.model.User;
import com.nw.model.UserImage;
import com.nw.model.VehicleClass;
import com.nw.model.VehicleDetails;
import com.nw.model.VehicleImage;
import com.nw.model.YouTubeVideo;
import com.utils.Helper;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

public class ParserManager
{

    public static Reference parseReferenceId(String result)
    {
        Reference reference = new Reference();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Success"))
                            reference = new Reference();

                        ReferenceID referenceID = new ReferenceID();
                        referenceID.setReferenceId(parser.getAttributeValue(null, "PlanTaskID"));
                        reference.getRequests().add(referenceID);

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return reference;
    }


    public static RequestUser parseRequestResponse(String result)
    {
        RequestUser requestUser = new RequestUser();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("RequestType"))
                            requestUser = new RequestUser();

                        if (name.equals("RequestTypeName"))
                        {
                            Request request = new Request();
                            request.setRequestId(parser.getAttributeValue(null, "RequestTypeID"));
                            request.setRequestName(parser.nextText());
                            requestUser.getRequests().add(request);
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return requestUser;
    }


    public static User parseLoginRespose(String result)
    {
        User user = new User();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            Impersonate impersonate = null;
            Module module = null;
            SubModule subModule = null;
            ArrayList<SubModule> subModules = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("UserInfo"))
                            user = new User();
                        else if (user != null)
                        {
                            if (name.equals("Identity"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    user.setIdenttity(0);
                                else
                                    user.setIdenttity(Integer.parseInt(identity));
                            } else if (name.equals("Name"))
                                user.setName(parser.nextText());
                            else if (name.equals("Surname"))
                                user.setSurName(parser.nextText());
                            else if (name.equals("MemberID"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    user.setMemberId(0);
                                else
                                    user.setMemberId(Integer.parseInt(id));
                            } else if (name.equals("ClientImage"))
                            {
                                UserImage userImage = new UserImage();
                                userImage.setHeight(parser.getAttributeValue(null, "Height"));
                                userImage.setUrl(parser.nextText());
                                if (!TextUtils.isEmpty(userImage.getUrl()))
                                    userImage.getUrl().replace("&amp;", "&");

                                user.getClientImages().add(userImage);
                            } else if (name.equals("MemberImage"))
                            {
                                UserImage memberImage = new UserImage();
                                memberImage.setHeight(parser.getAttributeValue(null, "Height"));
                                memberImage.setUrl(parser.nextText());
                                if (!TextUtils.isEmpty(memberImage.getUrl()))
                                    memberImage.getUrl().replace("&amp;", "&");

                                user.getMemberImages().add(memberImage);
                            } else if (name.equals("Client") && impersonate == null)
                            {
                                Client client = new Client();
                                client.setId(Integer.parseInt(parser.getAttributeValue(null, "id")));
                                client.setName(parser.nextText());
                                user.setClient(client);
                                user.setDefaultClient(client);
                            } else if (name.equals("UserHash"))
                            {
                                user.setUserHash(parser.nextText());
                            } else if (name.equals("NotificationIdentifier"))
                            {
                                user.setNotificationIdentifier(parser.nextText());
                            } else if (name.equals("Module"))
                            {
                                module = new Module();
                                module.setName(parser.getAttributeValue(null, "Name"));
                                module.setQuickLink(Boolean.parseBoolean(parser.getAttributeValue(null, "QuickLink")));

                            } else if (name.equals("SubModule"))
                            {
                                subModule = new SubModule();
                                if (subModules == null)
                                {
                                    subModules = new ArrayList<SubModule>();
                                }
                                subModule.setSubModuleName(parser.getAttributeValue(null, "Name"));
                            } else if (name.equals("Page"))
                            {
                                Page page = new Page();
                                page.setAlert(Boolean.parseBoolean(parser.getAttributeValue(null, "Alerts")));
                                page.setName(parser.nextText());
                                if (subModule != null)
                                {
                                    subModule.getSubPages().add(page);
                                } else
                                {
                                    module.getPages().add(page);
                                }
                            } else if (name.equals("Impersonate"))
                            {
                                impersonate = new Impersonate();
                            } else if (name.equals("Client") && impersonate != null)
                            {
                                Client client = new Client();
                                client.setId(Integer.parseInt(parser.getAttributeValue(null, "id")));
                                client.setName(parser.nextText());
                                impersonate.getClients().add(client);
                            } else if (name.equals("a:IsAuthenticated"))
                                user.setAuthenticated(Boolean.parseBoolean(parser.nextText()));
                            else if (name.equals("a:FailureReason"))
                                user.setFailureReason(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("Impersonate"))
                            user.setImpersonate(impersonate);

                        if (name.equals("Module"))
                        {

                            if (subModules != null)
                            {
                                module.getSubModules().addAll(subModules);
                                subModules = null;
                            }
                            user.getModules().add(module);
                            module = null;

                        }
                        if (name.equals("SubModule"))
                        {
                            subModules.add(subModule);
                            subModule = null;
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return user;
    }

    public static VehicleDetails parseVehicleDetailsRespose(String result)
    {
        VehicleDetails details = new VehicleDetails();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            Impersonate impersonate = null;
            Module module = null;
            SubModule subModule = null;
            ArrayList<SubModule> subModules = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Details"))
                            details = new VehicleDetails();
                        else if (details != null)
                        {
                            if (name.equals("usedVehicleStockID"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    details.setUsedVehicleStockID(0);
                                else
                                    details.setUsedVehicleStockID(Integer.parseInt(identity));
                            } else if (name.equals("department"))
                                details.setDepartment(parser.nextText());
                            else if (name.equals("stockCode"))
                                details.setStockCode(parser.nextText());
                            /*else if (name.equals("age"))
                            {
								String id = parser.nextText();
								if (TextUtils.isEmpty(id))
									details.setAge(0);
								else
									details.setAge(Integer.parseInt(id));
							}*/
                            else if (name.equals("variantID"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setVariantID(0);
                                else
                                    details.setVariantID(Integer.parseInt(id));
                            } else if (name.equals("friendlyName"))
                            {
                                details.setFriendlyName(parser.nextText());
                            } else if (name.equals("mmcode"))
                            {
                                details.setMmcode(parser.nextText());
                            } else if (name.equals("year"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setYear(0);
                                else
                                    details.setYear(Integer.parseInt(id));
                            } else if (name.equals("registration"))
                            {
                                details.setRegistration(parser.nextText());
                            } else if (name.equals("price"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setPrice(0.0);
                                else
                                    details.setPrice(Double.parseDouble(id));
                            } else if (name.equals("tradeprice"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setTradeprice(0.0);
                                else
                                    details.setTradeprice(Double.parseDouble(id));
                            } else if (name.equals("colour"))
                            {
                                details.setColour(parser.nextText());
                            } else if (name.equals("mileage"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setMileage(0);
                                else
                                    details.setMileage(Integer.parseInt(id));
                            } else if (name.equals("comments"))
                            {
                                details.setComments(parser.nextText());
                            } else if (name.equals("location"))
                            {
                                details.setLocation(parser.nextText());
                            } else if (name.equals("extras"))
                            {
                                details.setExtras(parser.nextText());
                            } else if (name.equals("trim"))
                            {
                                details.setTrim(parser.nextText());
                            } else if (name.equals("condition"))
                            {
                                details.setCondition(parser.nextText());
                            } else if (name.equals("vin"))
                            {
                                details.setVin(parser.nextText());
                            } else if (name.equals("engine"))
                            {
                                details.setEngine(parser.nextText());
                            } else if (name.equals("oem"))
                            {
                                details.setOem(parser.nextText());
                            } else if (name.equals("cost"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setCost(0.0f);
                                else
                                    details.setCost(Float.parseFloat(id));
                            } else if (name.equals("standin"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setStandin(0.0f);
                                else
                                    details.setStandin(Float.parseFloat(id));
                            } else if (name.equals("cpaerror"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setCpaerror(false);
                                else
                                    details.setCpaerror(Boolean.parseBoolean(id));
                            } else if (name.equals("internalnote"))
                            {
                                details.setInternalnote(parser.nextText());
                            } else if (name.equals("programname"))
                            {
                                details.setProgramname(parser.nextText());
                            } else if (name.equals("istender"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setIstender(false);
                                else
                                    details.setIstender(Boolean.parseBoolean(id));
                            } else if (name.equals("istrade"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setIstrade(false);
                                else
                                    details.setIstrade(Boolean.parseBoolean(id));
                            } else if (name.equals("isretail"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setIsretail(false);
                                else
                                    details.setIsretail(Boolean.parseBoolean(id));
                            } else if (name.equals("isprogram"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setIsprogram(false);
                                else
                                    details.setIsprogram(Boolean.parseBoolean(id));
                            } else if (name.equals("isexcluded"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setIsexcluded(false);
                                else
                                    details.setIsexcluded(Boolean.parseBoolean(id));
                            } else if (name.equals("isinvalid"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setIsinvalid(false);
                                else
                                    details.setIsinvalid(Boolean.parseBoolean(id));
                            } else if (name.equals("override"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setOverride(false);
                                else
                                    details.setOverride(Boolean.parseBoolean(id));
                            } else if (name.equals("ignoreonimport"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setIgnoreonimport(false);
                                else
                                    details.setIgnoreonimport(Boolean.parseBoolean(id));
                            } else if (name.equals("editable"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    details.setEditable(false);
                                else
                                    details.setEditable(Boolean.parseBoolean(id));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return details;
    }

    public static ArrayList<Client> parseImpersonationRespose(String result)
    {
        ArrayList<Client> clientList = new ArrayList<Client>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Client"))
                        {
                            Client client = new Client();
                            client.setId(Integer.parseInt(parser.getAttributeValue(null, "id")));
                            client.setName(parser.nextText());
                            clientList.add(client);
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return clientList;
    }

    public static ArrayList<Blog> parseSearchBlogRespose(String result)
    {
        ArrayList<Blog> blogs = new ArrayList<Blog>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);

            parser.nextTag();
            int eventType = parser.getEventType();
            Blog blog = null;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Post"))
                            blog = new Blog();
                        else if (blog != null)
                        {
                            if (name.equals("BlogPostID"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    blog.setBlogPostID(0);
                                else
                                    blog.setBlogPostID(Integer.parseInt(identity));
                            } else if (name.equals("Title"))
                                blog.setTitle(parser.nextText());
                            else if (name.equals("Details"))
                                blog.setDetails(parser.nextText());
                            else if (name.equals("CreatedDate"))
                            {
                                String dateResponse = parser.nextText();
                            /*Time time = new Time("UTC");
                            time.parse3339(dateResponse);
							Date date = new Date(time.normalize(false));*/
                                blog.setCreatedDate(Helper.convertDateToNormal(dateResponse));
                            } else if (name.equals("PublishDate"))
                            {
                                String dateResponse = parser.nextText();
							/*Time time = new Time("UTC");
							time.parse3339(dateResponse);
							Date date = new Date(time.normalize(false));*/
                                blog.setPublishDate(Helper.convertDateToNormal(dateResponse));
                            } else if (name.equals("EndDate"))
                            {
                                String dateResponse = parser.nextText();
							/*Time time = new Time("UTC");
							time.parse3339(dateResponse);
							Date date = new Date(time.normalize(false));*/
                                blog.setEndDate(Helper.convertDateToNormal(dateResponse));
                            } else if (name.equals("Name"))
                                blog.setName(parser.nextText());
                            else if (name.equals("BlogType"))
                                blog.setBlogType(parser.nextText());
                            else if (name.equals("ImagePath"))
                            {
                                String path = parser.nextText();
                                path = path.replaceAll(" ", "%20");
                                blog.setImagePath(path);
                            } else if (name.equals("ImageCount"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    blog.setImageCount(0);
                                else
                                    blog.setImageCount(Integer.parseInt(identity));
                            } else if (name.equals("EndStatus"))
                                blog.setEndStatus(parser.nextText());

                            else if (name.equals("TotalCount"))
                                blog.setTotalCount(Integer.parseInt(parser.nextText()));

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("Post"))
                            blogs.add(blog);

                        if (name.equals("Posts"))
                            return blogs;


                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return blogs;
    }

    public static ArrayList<SpecialVehicle> parseExpiredSpecial(String result)
    {
        ArrayList<SpecialVehicle> expiredList = new ArrayList<SpecialVehicle>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            SpecialVehicle vehicle = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Table1"))
                            vehicle = new SpecialVehicle();
                        else if (vehicle != null)
                        {
                            if (name.equals("SpecialID"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    vehicle.setSpecialID(0);
                                else
                                    vehicle.setSpecialID(Integer.parseInt(id));
                            }
                            if (name.equals("SpecialTypeID"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    vehicle.setSpecialTypeID(0);
                                else
                                    vehicle.setSpecialTypeID(Integer.parseInt(id));
                            }
                            if (name.equals("Type"))
                            {
                                String type = parser.nextText();
                                if (TextUtils.isEmpty(type))
                                    vehicle.setType("");
                                else
                                    vehicle.setType(type);
                            }
                            if (name.equals("SpecialStart"))
                            {
                                String start = parser.nextText();
                                if (TextUtils.isEmpty(start))
                                    vehicle.setSpecialstart("");
                                else
                                    vehicle.setSpecialstart(start);
                            }
                            if (name.equals("SpecialEnd"))
                            {
                                String end = parser.nextText();
                                if (TextUtils.isEmpty(end))
                                    vehicle.setSpecialEnd("");
                                else
                                    vehicle.setSpecialEnd(end);
                            }
                            if (name.equals("SpecialCreated"))
                            {
                                String created = parser.nextText();
                                if (TextUtils.isEmpty(created))
                                    vehicle.setSpecialCreated("");
                                else
                                    vehicle.setSpecialCreated(created);
                            }
                            if (name.equals("cmUserID"))
                            {
                                String userid = parser.nextText();
                                if (TextUtils.isEmpty(userid))
                                    vehicle.setCmUserId(0);
                                else
                                    vehicle.setCmUserId(Integer.parseInt(userid));
                            }
                            if (name.equals("ItemID"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    vehicle.setItemID(0);
                                else
                                    vehicle.setItemID(Integer.parseInt(id));
                            }
                            if (name.equals("SpecialPrice"))
                            {
                                String price = parser.nextText();
                                if (TextUtils.isEmpty(price))
                                    vehicle.setSpecialPrice(0);
                                else
                                    vehicle.setSpecialPrice(Float.parseFloat(price));
                            }
                            if (name.equals("NormalPrice"))
                            {
                                String price = parser.nextText();
                                if (TextUtils.isEmpty(price))
                                    vehicle.setNormalPrice(0);
                                else
                                    vehicle.setNormalPrice(Float.parseFloat(price));
                            }
                            if (name.equals("SavePrice"))
                            {
                                String price = parser.nextText();
                                if (TextUtils.isEmpty(price))
                                    vehicle.setSavePrice(0);
                                else
                                    vehicle.setSavePrice(Float.parseFloat(price));
                            }
                            if (name.equals("Details"))
                            {
                                String details = parser.nextText();
                                if (TextUtils.isEmpty(details))
                                    vehicle.setDetails("");
                                else
                                    vehicle.setDetails(details);
                            }
                            if (name.equals("Summary"))
                            {
                                String summary = parser.nextText();
                                if (TextUtils.isEmpty(summary))
                                    vehicle.setSummary("");
                                else
                                    vehicle.setSummary(summary);
                            }
                            if (name.equals("ImageID"))
                            {
                                String imageid = parser.nextText();
                                if (TextUtils.isEmpty(imageid))
                                    vehicle.setImageID(0);
                                else
                                    vehicle.setImageID(Integer.parseInt(imageid));
                            }
                            if (name.equals("StockCode"))
                            {
                                String stock = parser.nextText();
                                if (TextUtils.isEmpty(stock))
                                    vehicle.setStockCode("0");
                                else
                                    vehicle.setStockCode(stock);
                            }
                            if (name.equals("FriendlyName"))
                            {
                                String fname = parser.nextText();
                                if (TextUtils.isEmpty(fname))
                                    vehicle.setFriendlyName("");
                                else
                                    vehicle.setFriendlyName(fname);
                            }
                            if (name.equals("UsedYear"))
                            {
                                String year = parser.nextText();
                                if (TextUtils.isEmpty(year))
                                    vehicle.setUsedYear(0);
                                else
                                    vehicle.setUsedYear(Integer.parseInt(year));
                            }
                            if (name.equals("Colour"))
                            {
                                String color = parser.nextText();
                                if (TextUtils.isEmpty(color))
                                    vehicle.setColour("");
                                else
                                    vehicle.setColour(color);
                            }
                            if (name.equals("Mileage"))
                            {
                                String mileage = parser.nextText();
                                if (TextUtils.isEmpty(mileage))
                                    vehicle.setMileage(0);
                                else
                                    vehicle.setMileage(Integer.parseInt(mileage));
                            }
                            if (name.equals("MileageType"))
                            {
                                String type = parser.nextText();
                                if (TextUtils.isEmpty(type))
                                    vehicle.setMileageType("Km");
                                else
                                    vehicle.setMileageType(type);
                            }
                            if (name.equals("MakeName"))
                            {
                                String makeName = parser.nextText();
                                if (TextUtils.isEmpty(makeName))
                                    vehicle.setMileageType("");
                                else
                                    vehicle.setMakeName(makeName);
                            }
                            if (name.equals("ModelName"))
                            {
                                String modelName = parser.nextText();
                                if (TextUtils.isEmpty(modelName))
                                    vehicle.setMileageType("");
                                else
                                    vehicle.setModelName(modelName);
                            }
                            if (name.equals("VariantName"))
                            {
                                String variantName = parser.nextText();
                                if (TextUtils.isEmpty(variantName))
                                    vehicle.setMileageType("");
                                else
                                    vehicle.setVariantName(variantName);
                            }
                            if (name.equals("TotalCount"))
                            {
                                String count = parser.nextText();
                                if (TextUtils.isEmpty(count))
                                    vehicle.setTotalCount(0);
                                else
                                    vehicle.setTotalCount(Integer.parseInt(count));
                            }

                        }
                        break;

                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("Table1"))
                        {
                            expiredList.add(vehicle);
                        }
                        if (name.equals("DocumentElement"))
                        {
                            return expiredList;
                        }
                }
                eventType = parser.next();
            }

        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return expiredList;
    }

    public static ArrayList<SpecialVehicle> parseActiveSpecial(String result)
    {
        ArrayList<SpecialVehicle> activeList = new ArrayList<SpecialVehicle>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            SpecialVehicle vehicle = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("AUTOSpecial"))
                            vehicle = new SpecialVehicle();
                        else if (vehicle != null)
                        {
                            if (name.equals("SpecialID"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    vehicle.setSpecialID(0);
                                else
                                    vehicle.setSpecialID(Integer.parseInt(id));
                            }
                            if (name.equals("SpecialTypeID"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    vehicle.setSpecialTypeID(0);
                                else
                                    vehicle.setSpecialTypeID(Integer.parseInt(id));
                            }
                            if (name.equals("Title"))
                            {
                                String type = parser.nextText();
                                if (TextUtils.isEmpty(type))
                                    vehicle.setType("");
                                else
                                    vehicle.setType(type);
                            }
                            if (name.equals("Specialstart"))
                            {
                                String start = parser.nextText();
                                if (TextUtils.isEmpty(start))
                                    vehicle.setSpecialstart("");
                                else
                                    vehicle.setSpecialstart(start);
                            }
                            if (name.equals("SpecialEnd"))
                            {
                                String end = parser.nextText();
                                if (TextUtils.isEmpty(end))
                                    vehicle.setSpecialEnd("");
                                else
                                    vehicle.setSpecialEnd(end);
                            }
                            if (name.equals("SpecialCreated"))
                            {
                                String created = parser.nextText();
                                if (TextUtils.isEmpty(created))
                                    vehicle.setSpecialCreated("");
                                else
                                    vehicle.setSpecialCreated(created);
                            }
                            if (name.equals("cmUserID"))
                            {
                                String userid = parser.nextText();
                                if (TextUtils.isEmpty(userid))
                                    vehicle.setCmUserId(0);
                                else
                                    vehicle.setCmUserId(Integer.parseInt(userid));
                            }
                            if (name.equals("itemID"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    vehicle.setItemID(0);
                                else
                                    vehicle.setItemID(Integer.parseInt(id));
                            }
                            if (name.equals("SpecialPrice"))
                            {
                                String price = parser.nextText();
                                if (TextUtils.isEmpty(price))
                                    vehicle.setSpecialPrice(0);
                                else
                                    vehicle.setSpecialPrice(Float.parseFloat(price));
                            }
                            if (name.equals("NormalPrice"))
                            {
                                String price = parser.nextText();
                                if (TextUtils.isEmpty(price))
                                    vehicle.setNormalPrice(0);
                                else
                                    vehicle.setNormalPrice(Float.parseFloat(price));
                            }
                            if (name.equals("SavePrice"))
                            {
                                String price = parser.nextText();
                                if (TextUtils.isEmpty(price))
                                    vehicle.setSavePrice(0);
                                else
                                    vehicle.setSavePrice(Float.parseFloat(price));
                            }
                            if (name.equals("Details"))
                            {
                                String details = parser.nextText();
                                if (TextUtils.isEmpty(details))
                                    vehicle.setDetails("");
                                else
                                    vehicle.setDetails(details);
                            }
                            if (name.equals("Summary"))
                            {
                                String summary = parser.nextText();
                                if (TextUtils.isEmpty(summary))
                                    vehicle.setSummary("");
                                else
                                    vehicle.setSummary(summary);
                            }
                            if (name.equals("ImageID"))
                            {
                                String imageid = parser.nextText();
                                if (TextUtils.isEmpty(imageid))
                                    vehicle.setImageID(0);
                                else
                                    vehicle.setImageID(Integer.parseInt(imageid));
                            }
                            if (name.equals("StockCode"))
                            {
                                String stock = parser.nextText();
                                if (TextUtils.isEmpty(stock))
                                    vehicle.setStockCode("0");
                                else
                                    vehicle.setStockCode(stock);
                            }
                            if (name.equals("friendlyName"))
                            {
                                String fname = parser.nextText();
                                if (TextUtils.isEmpty(fname))
                                    vehicle.setFriendlyName("");
                                else
                                    vehicle.setFriendlyName(fname);
                            }
                            if (name.equals("UsedYear"))
                            {
                                String year = parser.nextText();
                                if (TextUtils.isEmpty(year))
                                    vehicle.setUsedYear(0);
                                else
                                    vehicle.setUsedYear(Integer.parseInt(year));
                            }
                            if (name.equals("Colour"))
                            {
                                String color = parser.nextText();
                                if (TextUtils.isEmpty(color))
                                    vehicle.setColour("");
                                else
                                    vehicle.setColour(color);
                            }
                            if (name.equals("Mileage"))
                            {
                                String mileage = parser.nextText();
                                if (TextUtils.isEmpty(mileage))
                                    vehicle.setMileage(0);
                                else
                                    vehicle.setMileage(Integer.parseInt(mileage));
                            }
                            if (name.equals("MileageType"))
                            {
                                String type = parser.nextText();
                                if (TextUtils.isEmpty(type))
                                    vehicle.setMileageType("Km");
                                else
                                    vehicle.setMileageType(type);
                            }

                            if (name.equals("VariantID"))
                            {
                                String variantid = parser.nextText();
                                if (TextUtils.isEmpty(variantid))
                                    vehicle.setVariantID(0);
                                else
                                    vehicle.setVariantID(Integer.parseInt(variantid));
                            }
                            if (name.equals("MakeId"))
                            {
                                String makeid = parser.nextText();
                                if (TextUtils.isEmpty(makeid))
                                    vehicle.setMakeId(0);
                                else
                                    vehicle.setMakeId(Integer.parseInt(makeid));
                            }
                            if (name.equals("ModelID"))
                            {
                                String modelid = parser.nextText();
                                if (TextUtils.isEmpty(modelid))
                                    vehicle.setModelID(0);
                                else
                                    vehicle.setModelID(Integer.parseInt(modelid));
                            }
                            if (name.equals("TotalCount"))
                            {
                                String total = parser.nextText();
                                if (TextUtils.isEmpty(total))
                                    vehicle.setTotalCount(0);
                                else
                                    vehicle.setTotalCount(Integer.parseInt(total));
                            }
                            if (name.equals("EndStatus"))
                            {
                                String status = parser.nextText();
                                if (TextUtils.isEmpty(status))
                                    vehicle.setEndStatus("");
                                else
                                    vehicle.setEndStatus(status);
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("AUTOSpecial"))
                        {
                            activeList.add(vehicle);
                        }
                        if (name.equals("AUTOSpecials"))
                        {
                            return activeList;
                        }
                }
                eventType = parser.next();
            }

        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return activeList;
    }

    public static ArrayList<BaseImage> parseImageList(String result)
    {
        ArrayList<BaseImage> imageList = new ArrayList<BaseImage>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")),
                    null);

            parser.nextTag();
            int eventType = parser.getEventType();
            VehicleImage image = null;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("image"))
                        {
                            image = new VehicleImage();
                            image.setType(1);
                            image.setLocal(false);
                        } else if (image != null)
                        {
                            if (name.equals("uciID"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    image.setUciid(0);
                                else
                                    image.setUciid(Integer.parseInt(identity));
                            } else if (name.equals("imageID"))
                                image.setId(Integer.parseInt(parser.nextText()));

                            else if (name.equals("ImageTitle2"))
                                image.setImageTitle(parser.nextText());

                            else if (name.equals("imagePriority"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    image.setPriority(0);
                                else
                                    image.setPriority(Integer.parseInt(identity));

                            } else if (name.equals("imageTypeName"))
                            {
                                image.setImageTypeName(parser.nextText());
                            } else if (name.equals("imageSource"))
                            {
                                image.setImageSource(parser.nextText());
                            } else if (name.equals("imagePath"))
                                image.setImageTitle(parser.nextText());
                            else if (name.equals("imageLink"))
                                image.setLink(parser.nextText());
                            else if (name.equals("imageSize"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    image.setImageSize(0);
                                else
                                    image.setImageSize(Integer.parseInt(identity));
                            } else if (name.equals("imageRes"))
                                image.setImageRes(parser.nextText());

                            else if (name.equals("imageType"))
                            {

                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    image.setType(0);
                                else
                                    image.setType(Integer.parseInt(identity));
                            } else if (name.equals("ImageDPI"))
                            {

                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    image.setImagedpi(0);
                                else
                                    image.setImagedpi((int) Float.parseFloat(identity));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("image"))
                        {
                            imageList.add(image);
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return imageList;
    }

    public static ArrayList<YouTubeVideo> parseVideoList(String result)
    {
        ArrayList<YouTubeVideo> videoList = new ArrayList<YouTubeVideo>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            YouTubeVideo video = null;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("video"))
                        {
                            video = new YouTubeVideo();
                            video.setLocal(false);
                        } else if (video != null)
                        {
                            if (name.equals("youtubeVideoID"))
                            {
                                String youtubeVideoID = parser.nextText();
                                if (TextUtils.isEmpty(youtubeVideoID))
                                    video.setVideo_ID("ID?");
                                else
                                    video.setVideo_ID(youtubeVideoID);
                            } else if (name.equals("title"))
                            {
                                String title = parser.nextText();
                                if (TextUtils.isEmpty(title))
                                    video.setVideo_title("Title?");
                                else
                                    video.setVideo_title(title);
                            } else if (name.equals("Keywords"))
                            {
                                String Keywords = parser.nextText();
                                if (TextUtils.isEmpty(Keywords))
                                    video.setVideo_Tags("Keywords?");
                                else
                                    video.setVideo_Tags(Keywords);

                            } else if (name.equals("description"))
                            {

                                String description = parser.nextText();
                                if (TextUtils.isEmpty(description))
                                    video.setVideo_Description("description?");
                                else
                                    video.setVideo_Description(description);
                            } else if (name.equals("VideoLinkID"))
                            {
                                String VideoLinkID = parser.nextText();
                                if (TextUtils.isEmpty(VideoLinkID))
                                    video.setVideoLinkID(0);
                                else
                                    video.setVideoLinkID(Integer.parseInt(VideoLinkID));
                            } else if (name.equals("Searchable"))
                            {

                                String Searchable = parser.nextText();
                                if (TextUtils.isEmpty(Searchable))
                                    video.setSearchable(false);
                                else if (Searchable.equalsIgnoreCase("true"))
                                {
                                    video.setSearchable(true);
                                } else
                                {
                                    video.setSearchable(false);
                                }
                            } else if (name.equals("youtubeID"))
                            {

                                String youtubeID = parser.nextText();
                                if (TextUtils.isEmpty(youtubeID))
                                    video.setVideoCode("VideoCode?");
                                else
                                    video.setVideoCode(youtubeID);
                                video.setVideoThumbUrl("http://img.youtube.com/vi/" + video.getVideoCode() + "/0.jpg");

                            } else if (name.equals("videoURL"))
                            {

                                String videoURL = parser.nextText();
                                if (TextUtils.isEmpty(videoURL))
                                    video.setVideoCode("videoURL?");
                                else
                                    video.setVideoFullPath(videoURL);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("video"))
                        {
                            videoList.add(video);
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        DataManager.getInstance().getYouTubeVideos().addAll(videoList);
        return videoList;
    }

    public static ArrayList<YouTubeVideo> parseVideoListForDevice(String result)
    {
        ArrayList<YouTubeVideo> videoList = new ArrayList<YouTubeVideo>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            YouTubeVideo video = null;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("video"))
                        {
                            video = new YouTubeVideo();
                            video.setLocal(false);
                        } else if (video != null)
                        {
                            if (name.equals("youtubeVideoID"))
                            {
                                String youtubeVideoID = parser.nextText();
                                if (TextUtils.isEmpty(youtubeVideoID))
                                    video.setVideo_ID("ID?");
                                else
                                    video.setVideo_ID(youtubeVideoID);
                            } else if (name.equals("title"))
                            {
                                String title = parser.nextText();
                                if (TextUtils.isEmpty(title))
                                    video.setVideo_title("Title?");
                                else
                                    video.setVideo_title(title);
                            } else if (name.equals("Keywords"))
                            {
                                String Keywords = parser.nextText();
                                if (TextUtils.isEmpty(Keywords))
                                    video.setVideo_Tags("Keywords?");
                                else
                                    video.setVideo_Tags(Keywords);

                            } else if (name.equals("description"))
                            {

                                String description = parser.nextText();
                                if (TextUtils.isEmpty(description))
                                    video.setVideo_Description("description?");
                                else
                                    video.setVideo_Description(description);
                            } else if (name.equals("VideoLinkID"))
                            {
                                String VideoLinkID = parser.nextText();
                                if (TextUtils.isEmpty(VideoLinkID))
                                    video.setVideoLinkID(0);
                                else
                                    video.setVideoLinkID(Integer.parseInt(VideoLinkID));
                            } else if (name.equals("Searchable"))
                            {

                                String Searchable = parser.nextText();
                                if (TextUtils.isEmpty(Searchable))
                                    video.setSearchable(false);
                                else if (Searchable.equalsIgnoreCase("true"))
                                {
                                    video.setSearchable(true);
                                } else
                                {
                                    video.setSearchable(false);
                                }
                            } else if (name.equals("youtubeID"))
                            {

                                String youtubeID = parser.nextText();
                                if (TextUtils.isEmpty(youtubeID))
                                    video.setVideoCode("VideoCode?");
                                else
                                    video.setVideoCode(youtubeID);
                                video.setVideoThumbUrl("http://img.youtube.com/vi/" + video.getVideoCode() + "/0.jpg");

                            } else if (name.equals("videoURL"))
                            {

                                String videoURL = parser.nextText();
                                if (TextUtils.isEmpty(videoURL))
                                    video.setVideoCode("videoURL?");
                                else
                                    video.setVideoFullPath(videoURL);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("video"))
                        {
                            videoList.add(video);
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //DataManager.getInstance().getYouTubeVideos().addAll(videoList);
        return videoList;
    }

    public static ArrayList<BlogType> parseBlogType(String response)
    {
        ArrayList<BlogType> blogTypes = new ArrayList<BlogType>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            BlogType blogType = null;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("a:BlogType"))
                            blogType = new BlogType();
                        else if (blogType != null)
                        {
                            if (name.equals("a:Active"))
                            {
                                blogType.setActive(Boolean.parseBoolean(parser
                                        .nextText()));
                            } else if (name.equals("a:BlogPostTypeID"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    blogType.setBlogPostTypeID(0);
                                else
                                    blogType.setBlogPostTypeID(Integer
                                            .parseInt(identity));
                            } else if (name.equals("a:Name"))
                                blogType.setName(parser.nextText());
                            else if (name.equals("a:Order"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    blogType.setOrder(0);
                                else
                                    blogType.setOrder(Integer.parseInt(identity));
                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("a:BlogType"))
                        {
                            blogTypes.add(blogType);
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return blogTypes;
    }

    public static int parseSaveBlog(String response)
    {
        int result = 0;
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("SaveBlogPostResult"))
                            result = Integer.parseInt(parser.nextText());

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static int parseSaveSpecialResponse(String response)
    {
        int result = 0;
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("SaveSpecialResult"))
                            result = Integer.parseInt(parser.nextText());
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseComments(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("comments"))
                            result = parser.nextText();

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseType(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("department"))
                            result = parser.nextText();

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseExtras(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("extras"))
                            result = parser.nextText();

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<BaseImage> parseBlogImages(String response)
    {
        ArrayList<BaseImage> objects = new ArrayList<BaseImage>();
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            BlogImage object = null;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("BlogImageID"))
                        {
                            object = new BlogImage();
                            object.setType(1);
                        } else if (name.equals("BlogImageID"))
                        {
                            String identity = parser.nextText();
                            if (TextUtils.isEmpty(identity))
                                object.setId(0);
                            else
                                object.setId(Integer.parseInt(identity));
                        } else if (name.equals("Path"))
                            object.setPath(parser.nextText());
                        else if (name.equals("OriginalFileName"))
                            object.setOriginalFileName(parser.nextText());
                        else if (name.equals("thumbpath"))
                            object.setThumbPath(parser.nextText());
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("Table1"))
                            objects.add(object);
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return objects;
    }

    public static Blog parseGetBlog(String result)
    {
        XmlPullParser parser = Xml.newPullParser();
        Blog blog = null;
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")),
                    null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();

            BlogImage blogImage = null;
            ArrayList<BaseImage> blogImages = new ArrayList<BaseImage>();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("GetBlogResult"))
                            blog = new Blog();
                        else if (blog != null)
                        {
                            if (name.equals("a:Active"))
                                blog.setActive(Boolean.parseBoolean(parser.nextText()));
                            else if (name.equals("a:Author"))
                                blog.setAuthor(parser.nextText());

                            else if (name.equals("a:BlogPostID")
                                    && blogImage == null)
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    blog.setBlogPostID(0);
                                else
                                    blog.setBlogPostID(Integer.parseInt(identity));
                            } else if (name.equals("a:BlogPostTypeID"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    blog.setBlogPostTypeId(0);
                                else
                                    blog.setBlogPostTypeId(Integer.parseInt(identity));
                            } else if (name.equals("a:CreatedDate"))
                            {
                                String dateResponse = parser.nextText();
                                Time time = new Time("UTC");
                                time.parse3339(dateResponse);
                                Date date = new Date(time.normalize(false));
                                blog.setCreatedDate(Helper.showDate(date));
                            } else if (name.equals("a:Details"))
                                blog.setDetails(parser.nextText());

                            else if (name.equals("a:EndDate"))
                            {
                                String dateResponse = parser.nextText();
                                Time time = new Time("UTC");
                                time.parse3339(dateResponse);
                                Date date = new Date(time.normalize(false));
                                blog.setEndDate(Helper.showDate(date));
                            } else if (name.equals("a:PublishDate"))
                            {
                                String dateResponse = parser.nextText();
                                Time time = new Time("UTC");
                                time.parse3339(dateResponse);
                                Date date = new Date(time.normalize(false));
                                blog.setPublishDate(Helper.showDate(date));
                            } else if (name.equals("a:Title"))
                                blog.setTitle(parser.nextText());

                            else if (name.equals("a:TotalCount"))
                                blog.setTotalCount(Integer.parseInt(parser.nextText()));

                            else if (name.equals("a:ImageCount"))
                                blog.setImageCount(Integer.parseInt(parser.nextText()));

                            if (name.equals("a:BlogImage"))
                            {
                                blogImage = new BlogImage();
                                blogImage.setType(1);
                            }

                            if (blogImage != null)
                            {
                                if (name.equals("a:BlogPostID"))
                                {
                                    String identity = parser.nextText();
                                    if (TextUtils.isEmpty(identity))
                                        blogImage.setBlogPostID(0);
                                    else
                                        blogImage.setBlogPostID(Integer.parseInt(identity));
                                } else if (name.equals("a:BlogImageID"))
                                {
                                    String identity = parser.nextText();
                                    if (TextUtils.isEmpty(identity))
                                        blogImage.setId(0);
                                    else
                                        blogImage.setId(Integer.parseInt(identity));
                                } else if (name.equals("a:OriginalFileName"))
                                    blogImage.setOriginalFileName(parser.nextText());
                                else if (name.equals("a:Path"))
                                    blogImage.setLink(parser.nextText());
                                else if (name.equals("a:Priority"))
                                    blogImage.setPriority(Integer.parseInt(parser.nextText()));
                                else if (name.equals("a:ThumbPath"))
                                    blogImage.setThumbPath(parser.nextText());
                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("a:BlogImage"))
                        {
                            blogImages.add(blogImage);
                            blogImage = null;
                        }

                        if (name.equals("a:Images"))
                            blog.setGridImages(blogImages);

                }
                eventType = parser.next();
            }

        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return blog;
    }

    public static VehicleDetails parseSpecForVehicle(String result)
    {
        XmlPullParser parser = Xml.newPullParser();
        VehicleDetails vehicleDetails = null;
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("SpecDetails"))
                            vehicleDetails = new VehicleDetails();
                        else if (vehicleDetails != null)
                        {
                            if (name.equals("Engine CC"))
                                vehicleDetails.setEngine_CC(Integer.parseInt(parser.getAttributeValue(null, "Engine CC")));
                            else if (name.equals("Power KW"))
                                vehicleDetails.setPower_KW(Integer.parseInt(parser.getAttributeValue(null, "Power KW")));
                            else if (name.equals("Torque NM"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setTorque_NM(0);
                                else
                                    vehicleDetails.setTorque_NM(Integer.parseInt(parser.getAttributeValue(null, "Torque NM")));
                            } else if (name.equals("Gearbox"))
                                vehicleDetails.setGearbox(parser.getAttributeValue(null, "Gearbox"));
                            else if (name.equals("Fuel Type"))
                                vehicleDetails.setFuel_Type(parser.getAttributeValue(null, "Fuel Type"));
                            else if (name.equals("Gears"))
                            {
                                String identity = parser.getAttributeValue(null, "Gears");
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setGears("0");
                                else
                                    vehicleDetails.setGears(parser.nextText());
                            } else if (name.equals("Accel 0-100"))
                            {
                                String identity = parser.getAttributeValue(null, "Accel 0-100");
                                // Accel 0-100 = setCap for this case
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setAcceleration0_100("");
                                else
                                    vehicleDetails.setAcceleration0_100(identity + "s");
                            } else if (name.equals("Max Speed"))
                            {
                                String identity = parser.getAttributeValue(null, "Max Speed");
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setMax_Speed("");
                                else
                                    vehicleDetails.setMax_Speed(identity + " Kph");
                            } else if (name.equals("Fuel per 100km - average"))
                            {
                                String identity = parser.getAttributeValue(null, "Fuel per 100km - average");
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setVehicle_mileage_100km("");
                                else
                                    vehicleDetails.setVehicle_mileage_100km(Integer.parseInt(identity) + "L");
                            } else if (name.equals("Warranty Period (RSA)"))
                            {
                                String identity = parser.getAttributeValue(null, "Warranty Period (RSA)");
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setWarranty_Period_RSA("");
                                else if (Integer.parseInt(identity) < 12)
                                {
                                    vehicleDetails.setWarranty_Period_RSA(identity + " months");
                                } else
                                {
                                    vehicleDetails.setWarranty_Period_RSA(Integer.parseInt(identity) / 12 + " years");
                                }

                            } else if (name.equals("Warranty (RSA)"))
                            {
                                String identity = parser.getAttributeValue(null, "Warranty (RSA)");
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setWarranty("");
                                else
                                    vehicleDetails.setWarranty(identity);
                            } else if (name.equals("Maintenance Plan Period"))
                            {
                                String identity = parser.getAttributeValue(null, "Maintenance Plan Period");
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setMaintenance_Plan_Period("");
                                else if (Integer.parseInt(identity) < 12)
                                {
                                    vehicleDetails.setMaintenance_Plan_Period(identity + " months");
                                } else
                                {
                                    vehicleDetails.setMaintenance_Plan_Period(Integer.parseInt(identity) / 12 + " years");
                                }

                            } else if (name.equals("Maintenance Plan"))
                            {
                                String identity = parser.getAttributeValue(null, "Maintenance Plan");
                                if (TextUtils.isEmpty(identity))
                                    vehicleDetails.setMaintenance_Plan("");
                                else
                                    vehicleDetails.setMaintenance_Plan(identity);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();

                }
                eventType = parser.next();
            }

        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return vehicleDetails;
    }

    public static boolean parseEndBlogResponse(String response)
    {
        boolean result = false;
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("EndBlogResult"))
                            result = Boolean.parseBoolean(parser.nextText());
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static LogClient parsedoGet(String response)
    {

        LogClient result = new LogClient();
        Client client = null;
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("a:GeoError"))
                        {
                            result.setError(Boolean.parseBoolean(parser.nextText()));
                        }
                        if (name.equals("a:LocationAddress"))
                            result.setAddress(parser.nextText());

                        if (name.equals("a:ClientResult"))
                        {
                            client = new Client();
                            client.setCheckIn(true);
                        }
                        if (name.equals("a:ClientID"))
                            client.setId(Integer.parseInt(parser.nextText()));
                        if (name.equals("a:ClientName"))
                            client.setName(parser.nextText());

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("a:ClientResult"))
                        {
                            result.getClients().add(client);
                            client = null;
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Client> parseClientList(String response)
    {
        ArrayList<Client> clients = null;
        XmlPullParser parser = Xml.newPullParser();
        Client client = null;
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Clients"))
                            clients = new ArrayList<Client>();
                        else if (name.equals("client"))
                            client = new Client();
                        else if (name.equals("clientID"))
                            client.setId(Integer.parseInt(parser.nextText()));
                        else if (name.equals("clientName"))
                            client.setName(parser.nextText());

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("client"))
                        {
                            clients.add(client);
                            client = null;
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return clients;
    }

    public static ArrayList<PlannerType> parsePlannerTypeList(String response,
                                                              int type)
    {
        ArrayList<PlannerType> plannerTypes = null;
        XmlPullParser parser = Xml.newPullParser();
        PlannerType plannerType = null;
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("PlannerTypes"))
                            plannerTypes = new ArrayList<PlannerType>();
                        else if (name.equals("type"))
                        {
                            plannerType = new PlannerType();
                            plannerType.setType(type);
                        } else if (name.equals("activityID"))
                            plannerType.setActivityId(Integer.parseInt(parser
                                    .nextText()));
                        else if (name.equals("activityPastName"))
                            plannerType.setActvityPastName(parser.nextText());
                        else if (name.equals("activityFutureName"))
                            plannerType.setActvityFutureName(parser.nextText());

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("type"))
                        {
                            plannerTypes.add(plannerType);
                            plannerType = null;
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return plannerTypes;
    }

    public static String parseSaveLogActivityResponse(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("LogActivityResult"))
                            result = parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseMessageChecker(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Message"))
                            result = parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parsetokenChecker(String response, String keywordtocheck)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals(keywordtocheck))
                            result = parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Member> parseListMembersForClientXMLRespone(
            String response)
    {
        ArrayList<Member> plannerTypes = null;
        XmlPullParser parser = Xml.newPullParser();
        Member member = null;
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Members"))
                            plannerTypes = new ArrayList<Member>();
                        else if (name.equals("member"))
                        {
                            member = new Member();
                        } else if (name.equals("ID"))
                            member.setMemberID(Integer.parseInt(parser.nextText()));
                        else if (name.equals("memberID"))
                            member.setId(Integer.parseInt(parser.nextText()));
                        else if (name.equals("memberName"))
                            member.setName(parser.nextText());
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("member"))
                        {
                            plannerTypes.add(member);
                            member = null;
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return plannerTypes;
    }

    public static String parsePostNewTaskResponse(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(
                    new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            ;
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("PostNewTaskResult"))
                            result = parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseAddVehicleResponse(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("AddVehicleViaObjResult"))
                            result = parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseUpdateVehicleResponse(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("UpdateVehicleViaObjResult"))
                            result = parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseAddToWantedResponse(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Status"))
                            result = parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String parseAddImageToVehicleResponse(String response)
    {
        String result = "";
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("AddImageToVehicleBase64Result"))
                            result = parser.nextText();
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;

    }

    public static Leads parseLeadDetails(Object result)
    {
        Leads leads = null;
        try
        {
            SoapObject obj = (SoapObject) result;
            SoapObject dataObj = (SoapObject) obj.getPropertySafely("Lead", "default");
            SoapObject leadsObj = (SoapObject) dataObj.getPropertySafely("LeadInfo", "default");

            leads = new Leads();

            if (leadsObj.hasProperty("ID"))
                leads.setId(Integer.parseInt(leadsObj.getPrimitivePropertySafelyAsString("ID")));
            else
                leads.setId(0);

            if (leadsObj.hasProperty("Name"))
                leads.setUsername(leadsObj.getPrimitivePropertySafelyAsString("Name"));
            else
                leads.setUsername("");

            if (leadsObj.hasProperty("MobileNumber"))
                leads.setPhoneNumber(leadsObj.getPrimitivePropertySafelyAsString("MobileNumber"));
            else
                leads.setPhoneNumber("");

            if (leadsObj.hasProperty("HomeNumber"))
                leads.setHomeNumber(leadsObj.getPrimitivePropertySafelyAsString("HomeNumber"));
            else
                leads.setHomeNumber("");

            if (leadsObj.hasProperty("WorkNumber"))
                leads.setWorkNumber(leadsObj.getPrimitivePropertySafelyAsString("WorkNumber"));
            else
                leads.setWorkNumber("");

            if (leadsObj.hasProperty("Email"))
                leads.setUserEmail(leadsObj.getPrimitivePropertySafelyAsString("Email"));
            else
                leads.setUserEmail("");

            if (leadsObj.hasProperty("Source"))
                leads.setSource(leadsObj.getPrimitivePropertySafelyAsString("Source"));
            else
                leads.setSource("");

            if (leadsObj.hasProperty("LogimeterNumber"))
            {
                leads.setLogimeterNumber(leadsObj.getPrimitivePropertySafelyAsString("LogimeterNumber"));
            } else
            {
                leads.setLogimeterNumber("LogimeterNumber?");
            }

            if (leadsObj.hasProperty("Submitted"))
                leads.setSubmitted(leadsObj.getPrimitivePropertySafelyAsString("Submitted"));
            else
                leads.setSource("");

            if (leadsObj.hasProperty("Age"))
                leads.setDaysLeft(leadsObj.getPrimitivePropertySafelyAsString("Age"));
            else
                leads.setDaysLeft("");

            if (dataObj.getPropertySafely("Vehicle") instanceof SoapObject)
            {
                SoapObject vehicleObject = (SoapObject) dataObj.getPropertySafely("Vehicle");

                if (vehicleObject.hasProperty("Type"))
                    leads.setVehicleType(Helper.getVehicleType(vehicleObject.getPrimitivePropertySafelyAsString("Type")));
                if (vehicleObject.hasProperty("Matched"))
                    leads.setMatched(Boolean.parseBoolean(vehicleObject.getPrimitivePropertySafelyAsString("Matched")));
                if (vehicleObject.hasProperty("MakeAsked"))
                    leads.setMakeAsked(vehicleObject.getPrimitivePropertySafelyAsString("MakeAsked"));
                if (vehicleObject.hasProperty("ModelAsked"))
                    leads.setModelAsked(vehicleObject.getPrimitivePropertySafelyAsString("ModelAsked"));
                if (vehicleObject.hasProperty("YearAsked"))
                    leads.setYearAsked(vehicleObject.getPrimitivePropertySafelyAsString("YearAsked"));
                if (vehicleObject.hasProperty("MileageAsked"))
                    leads.setMileageAsked(vehicleObject.getPrimitivePropertySafelyAsString("MileageAsked"));
                if (vehicleObject.hasProperty("ColourAsked"))
                    leads.setColourAsked(vehicleObject.getPrimitivePropertySafelyAsString("ColourAsked"));
                if (vehicleObject.hasProperty("PriceAsked"))
                    leads.setPriceAsked(vehicleObject.getPrimitivePropertySafelyAsString("PriceAsked"));
                if (vehicleObject.hasProperty("VariantID"))
                    leads.setVariant(vehicleObject.getPrimitivePropertySafelyAsString("VariantID"));
                if (vehicleObject.hasProperty("FriendlyName"))
                    leads.setFriendlyName(vehicleObject.getPrimitivePropertySafelyAsString("FriendlyName"));

                // for matched vehicle
                if (vehicleObject.hasProperty("UsedVehicleStockID"))
                    leads.setUsedVehicleStockID(vehicleObject.getPrimitivePropertySafelyAsString("UsedVehicleStockID"));
                if (vehicleObject.hasProperty("MMCode"))
                    leads.setMmCode(vehicleObject.getPrimitivePropertySafelyAsString("MMCode"));
                if (vehicleObject.hasProperty("Colour"))
                    leads.setColor(vehicleObject.getPrimitivePropertySafelyAsString("Colour"));
                if (vehicleObject.hasProperty("Year"))
                    leads.setYear(vehicleObject.getPrimitivePropertySafelyAsString("Year"));
                if (vehicleObject.hasProperty("StockCode"))
                    leads.setStockCode(vehicleObject.getPrimitivePropertySafelyAsString("StockCode"));
                if (vehicleObject.hasProperty("Mileage"))
                    leads.setMilage(vehicleObject.getPrimitivePropertySafelyAsString("Mileage"));
            } else
                leads.setVehicleDescription("No vehicle info.");

            if (dataObj.hasProperty("LastUpdate"))
            {
                if (dataObj.getProperty("LastUpdate") instanceof SoapObject)
                {

                    SoapObject lastUpdate = (SoapObject) dataObj.getPropertySafely("LastUpdate");
                    if (lastUpdate.hasProperty("Date"))
                        leads.setLastUpdate("available");

                    leads.setDate(lastUpdate.getPrimitivePropertySafelyAsString("Date"));
                    leads.setActivity(lastUpdate.getPrimitivePropertySafelyAsString("Activity"));
                    leads.setUser(lastUpdate.getPrimitivePropertySafelyAsString("User"));
                }
            }
            if (dataObj.hasProperty("TradeIn"))
            {
                leads.setTradeIn("tradein");
                SoapObject tradeObj = (SoapObject) dataObj.getPropertySafely("TradeIn");
                leads.setMakdetradeIn(tradeObj.getPrimitivePropertySafelyAsString("Make"));
                leads.setModeltradeIn(tradeObj.getPrimitivePropertySafelyAsString("Model"));
                leads.setYeartradeIn(tradeObj.getPrimitivePropertySafelyAsString("Year"));
                leads.setMileagetradeIn(tradeObj.getPrimitivePropertySafelyAsString("Mileage"));
            } else
            {
                leads.setTradeIn("");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return leads;
    }

    public static Person parseDrivingLicenseRespose(String result)
    {
        Person person = null;
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            VehicleClass vehicleClass1 = null, vehicleClass2 = null, vehicleClass3 = null, vehicleClass4 = null;
            boolean isClass1 = false, isClass2 = false, isClass3 = false, isClass4 = false, isCard = false;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("DrivingLicenseCard"))
                            person = new Person();
                        else if (person != null)
                        {
                            if (name.equals("Number"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    person.setIdentity_Number("0");
                                else
                                    person.setIdentity_Number(identity);
                            } else if (name.equals("Type"))
                                person.setIdentity_Type(parser.nextText());
                            else if (name.equals("Initials"))
                                person.setInitials(parser.nextText());
                            else if (name.equals("Surname"))
                                person.setSurname(parser.nextText());
                            else if (name.equals("DriverRestriction1"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    person.setDriverRestriction1("None");
                                else
                                    person.setDriverRestriction1(id);
                            } else if (name.equals("DriverRestriction2"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    person.setDriverRestriction2("None");
                                else
                                    person.setDriverRestriction2(id);
                            } else if (name.equals("DateOfBirth"))
                            {
                                String DateOfBirth = parser.nextText();
                                if (TextUtils.isEmpty(DateOfBirth))
                                    person.setDateOfBirth("N.A");
                                else
                                    person.setDateOfBirth(DateOfBirth);
                            } else if (name.equals("PreferenceLanguage"))
                            {
                                String preferenceLanguage = parser.nextText();
                                if (TextUtils.isEmpty(preferenceLanguage))
                                    person.setPreferenceLanguage("N.A");
                                else
                                    person.setPreferenceLanguage(preferenceLanguage);
                            } else if (name.equals("Gender"))
                            {
                                String gender = parser.nextText();
                                if (TextUtils.isEmpty(gender))
                                    person.setGender("N.A");
                                else
                                    person.setGender(gender);
                            } else if (name.equals("CountryOfIssue"))
                            {
                                String CountryOfIssue = parser.nextText();
                                if (TextUtils.isEmpty(CountryOfIssue))
                                    person.setCountryOfIssue("N.A");
                                else
                                    person.setCountryOfIssue(CountryOfIssue);
                            } else if (name.equals("IssueNumber"))
                            {
                                String IssueNumber = parser.nextText();
                                if (TextUtils.isEmpty(IssueNumber))
                                    person.setIssueNumber("N.A");
                                else
                                    person.setIssueNumber(IssueNumber);
                            } else if (name.equals("DateValidFrom"))
                            {
                                String DateValidFrom = parser.nextText();
                                if (TextUtils.isEmpty(DateValidFrom))
                                    person.setDateValidFrom("N.A");
                                else
                                    person.setDateValidFrom(DateValidFrom);
                            } else if (name.equals("DateValidUntil"))
                            {
                                String DateValidUntil = parser.nextText();
                                if (isCard)
                                {
                                    if (TextUtils.isEmpty(DateValidUntil))
                                        person.setDateValidUntil("N.A");
                                    else
                                        person.setDateValidUntil(DateValidUntil);
                                } else
                                {
                                    if (TextUtils.isEmpty(DateValidUntil))
                                        person.setProfessionalDrivingPermit_DateValidUntil("N.A");
                                    else
                                        person.setProfessionalDrivingPermit_DateValidUntil(DateValidUntil);
                                }
                            } else if (name.equals("Category"))
                            {
                                String Category = parser.nextText();
                                if (TextUtils.isEmpty(Category))
                                    person.setProfessionalDrivingPermit_Category("N.A");
                                else
                                    person.setProfessionalDrivingPermit_Category(Category);
                            } else if (name.equals("CertificateNumber"))
                            {
                                String certificateNumber = parser.nextText();
                                if (TextUtils.isEmpty(certificateNumber))
                                    person.setCertificateNumber("N.A");
                                else
                                    person.setCertificateNumber(certificateNumber);
                            } else if (name.equals("Card"))
                            {
                                isCard = true;
                            } else if (name.equals("VehicleClass1"))
                            {
                                if (vehicleClass1 == null)
                                    vehicleClass1 = new VehicleClass();
                                isClass1 = true;
                            } else if (name.equals("VehicleClass2"))
                            {
                                if (vehicleClass2 == null)
                                    vehicleClass2 = new VehicleClass();
                                isClass2 = true;
                            } else if (name.equals("VehicleClass3"))
                            {
                                if (vehicleClass3 == null)
                                    vehicleClass3 = new VehicleClass();
                                isClass3 = true;
                            } else if (name.equals("VehicleClass4"))
                            {
                                if (vehicleClass4 == null)
                                    vehicleClass4 = new VehicleClass();
                                isClass4 = true;
                            } else if (name.equals("Code"))
                            {
                                if (isClass1)
                                {
                                    vehicleClass1.setCode(parser.nextText());
                                } else if (isClass2)
                                {
                                    vehicleClass2.setCode(parser.nextText());
                                } else if (isClass3)
                                {
                                    vehicleClass3.setCode(parser.nextText());
                                } else if (isClass4)
                                {
                                    vehicleClass4.setCode(parser.nextText());
                                }
                            } else if (name.equals("VehicleRestriction"))
                            {
                                if (isClass1)
                                {
                                    vehicleClass1.setVehicleRestriction(parser.nextText());
                                } else if (isClass2)
                                {
                                    vehicleClass2.setVehicleRestriction(parser.nextText());
                                } else if (isClass3)
                                {
                                    vehicleClass3.setVehicleRestriction(parser.nextText());
                                } else if (isClass4)
                                {
                                    vehicleClass4.setVehicleRestriction(parser.nextText());
                                }
                            } else if (name.equals("FirstIssueDate"))
                            {
                                if (isClass1)
                                {
                                    vehicleClass1.setFirstIssueDate(parser.nextText());
                                } else if (isClass2)
                                {
                                    vehicleClass2.setFirstIssueDate(parser.nextText());
                                } else if (isClass3)
                                {
                                    vehicleClass3.setFirstIssueDate(parser.nextText());
                                } else if (isClass4)
                                {
                                    vehicleClass4.setFirstIssueDate(parser.nextText());
                                }
                            } else if (name.equals("Photo"))
                            {
                                String Photo = parser.nextText();
                                if (TextUtils.isEmpty(Photo))
                                    person.setPhoto("N.A");
                                else
                                    person.setPhoto(Photo);
                            } else if (name.equals("SavedScanID"))
                            {
                                String scanID = parser.nextText();
                                if (TextUtils.isEmpty(scanID))
                                    person.setScanID("N.A");
                                else
                                    person.setScanID(scanID);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("VehicleClass1"))
                        {
                            person.setVehicleClass1(vehicleClass1);
                            isClass1 = false;
                        }
                        if (name.equals("VehicleClass2"))
                        {
                            person.setVehicleClass2(vehicleClass2);
                            isClass2 = false;
                        }
                        if (name.equals("VehicleClass3"))
                        {
                            person.setVehicleClass3(vehicleClass3);
                            isClass3 = false;
                        }
                        if (name.equals("VehicleClass4"))
                        {
                            person.setVehicleClass4(vehicleClass4);
                            isClass4 = false;
                        }
                        if (name.equals("Card"))
                        {
                            isCard = false;
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return person;
    }

    public static ArrayList<Person> parseDrivingLicenseList(String result)
    {
        Person person = null;
        ArrayList<Person> personList = null;
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")), null);
            parser.nextTag();
            VehicleClass vehicleClass1 = null, vehicleClass2 = null, vehicleClass3 = null, vehicleClass4 = null;
            boolean isClass1 = false, isClass2 = false, isClass3 = false, isClass4 = false, isCard = false;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("SavedScans"))
                        {
                            personList = new ArrayList<Person>();
                        }
                        if (name.equals("DrivingLicenseCard"))
                            person = new Person();
                        else if (person != null)
                        {
                            if (name.equals("Number"))
                            {
                                String identity = parser.nextText();
                                if (TextUtils.isEmpty(identity))
                                    person.setIdentity_Number("0");
                                else
                                    person.setIdentity_Number(identity);
                            } else if (name.equals("Type"))
                                person.setIdentity_Type(parser.nextText());
                            else if (name.equals("Initials"))
                                person.setInitials(parser.nextText());
                            else if (name.equals("Surname"))
                                person.setSurname(parser.nextText());
                            else if (name.equals("DriverRestriction1"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    person.setDriverRestriction1("None");
                                else
                                    person.setDriverRestriction1(id);
                            } else if (name.equals("DriverRestriction2"))
                            {
                                String id = parser.nextText();
                                if (TextUtils.isEmpty(id))
                                    person.setDriverRestriction2("None");
                                else
                                    person.setDriverRestriction2(id);
                            } else if (name.equals("DateOfBirth"))
                            {
                                String DateOfBirth = parser.nextText();
                                if (TextUtils.isEmpty(DateOfBirth))
                                    person.setDateOfBirth("N.A");
                                else
                                    person.setDateOfBirth(DateOfBirth);
                            } else if (name.equals("PreferenceLanguage"))
                            {
                                String preferenceLanguage = parser.nextText();
                                if (TextUtils.isEmpty(preferenceLanguage))
                                    person.setPreferenceLanguage("N.A");
                                else
                                    person.setPreferenceLanguage(preferenceLanguage);
                            } else if (name.equals("Telephone"))
                            {
                                String telephone = parser.nextText();
                                if (TextUtils.isEmpty(telephone))
                                    person.setTelephone("N.A");
                                else
                                    person.setTelephone(telephone);
                            } else if (name.equals("EmailAddress"))
                            {
                                String emailAddress = parser.nextText();
                                if (TextUtils.isEmpty(emailAddress))
                                    person.setEmail_id("N.A");
                                else
                                    person.setEmail_id(emailAddress);
                            } else if (name.equals("Gender"))
                            {
                                String gender = parser.nextText();
                                if (TextUtils.isEmpty(gender))
                                    person.setGender("N.A");
                                else
                                    person.setGender(gender);
                            } else if (name.equals("CountryOfIssue"))
                            {
                                String CountryOfIssue = parser.nextText();
                                if (TextUtils.isEmpty(CountryOfIssue))
                                    person.setCountryOfIssue("N.A");
                                else
                                    person.setCountryOfIssue(CountryOfIssue);
                            } else if (name.equals("IssueNumber"))
                            {
                                String IssueNumber = parser.nextText();
                                if (TextUtils.isEmpty(IssueNumber))
                                    person.setIssueNumber("N.A");
                                else
                                    person.setIssueNumber(IssueNumber);
                            } else if (name.equals("DateValidFrom"))
                            {
                                String DateValidFrom = parser.nextText();
                                if (TextUtils.isEmpty(DateValidFrom))
                                    person.setDateValidFrom("N.A");
                                else
                                    person.setDateValidFrom(DateValidFrom);
                            } else if (name.equals("DateValidUntil"))
                            {
                                String DateValidUntil = parser.nextText();
                                if (isCard)
                                {
                                    if (TextUtils.isEmpty(DateValidUntil))
                                        person.setDateValidUntil("N.A");
                                    else
                                        person.setDateValidUntil(DateValidUntil);
                                } else
                                {
                                    if (TextUtils.isEmpty(DateValidUntil))
                                        person.setProfessionalDrivingPermit_DateValidUntil("N.A");
                                    else
                                        person.setProfessionalDrivingPermit_DateValidUntil(DateValidUntil);
                                }
                            } else if (name.equals("Category"))
                            {
                                String Category = parser.nextText();
                                if (TextUtils.isEmpty(Category))
                                    person.setProfessionalDrivingPermit_Category("N.A");
                                else
                                    person.setProfessionalDrivingPermit_Category(Category);
                            } else if (name.equals("CertificateNumber"))
                            {
                                String certificateNumber = parser.nextText();
                                if (TextUtils.isEmpty(certificateNumber))
                                    person.setCertificateNumber("N.A");
                                else
                                    person.setCertificateNumber(certificateNumber);
                            } else if (name.equals("Card"))
                            {
                                isCard = true;
                            } else if (name.equals("VehicleClass1"))
                            {
                                if (vehicleClass1 == null)
                                    vehicleClass1 = new VehicleClass();
                                isClass1 = true;
                            } else if (name.equals("VehicleClass2"))
                            {
                                if (vehicleClass2 == null)
                                    vehicleClass2 = new VehicleClass();
                                isClass2 = true;
                            } else if (name.equals("VehicleClass3"))
                            {
                                if (vehicleClass3 == null)
                                    vehicleClass3 = new VehicleClass();
                                isClass3 = true;
                            } else if (name.equals("VehicleClass4"))
                            {
                                if (vehicleClass4 == null)
                                    vehicleClass4 = new VehicleClass();
                                isClass4 = true;
                            } else if (name.equals("Code"))
                            {
                                if (isClass1)
                                {
                                    vehicleClass1.setCode(parser.nextText());
                                } else if (isClass2)
                                {
                                    vehicleClass2.setCode(parser.nextText());
                                } else if (isClass3)
                                {
                                    vehicleClass3.setCode(parser.nextText());
                                } else if (isClass4)
                                {
                                    vehicleClass4.setCode(parser.nextText());
                                }
                            } else if (name.equals("VehicleRestriction"))
                            {
                                if (isClass1)
                                {
                                    vehicleClass1.setVehicleRestriction(parser.nextText());
                                } else if (isClass2)
                                {
                                    vehicleClass2.setVehicleRestriction(parser.nextText());
                                } else if (isClass3)
                                {
                                    vehicleClass3.setVehicleRestriction(parser.nextText());
                                } else if (isClass4)
                                {
                                    vehicleClass4.setVehicleRestriction(parser.nextText());
                                }
                            } else if (name.equals("FirstIssueDate"))
                            {
                                if (isClass1)
                                {
                                    vehicleClass1.setFirstIssueDate(parser.nextText());
                                } else if (isClass2)
                                {
                                    vehicleClass2.setFirstIssueDate(parser.nextText());
                                } else if (isClass3)
                                {
                                    vehicleClass3.setFirstIssueDate(parser.nextText());
                                } else if (isClass4)
                                {
                                    vehicleClass4.setFirstIssueDate(parser.nextText());
                                }
                            } else if (name.equals("Photo"))
                            {
                                String Photo = parser.nextText();
                                if (TextUtils.isEmpty(Photo))
                                    person.setPhoto("N.A");
                                else
                                    person.setPhoto(Photo);
                            } else if (name.equals("SavedScanID"))
                            {
                                String scanID = parser.nextText();
                                if (TextUtils.isEmpty(scanID))
                                    person.setScanID("N.A");
                                else
                                    person.setScanID(scanID);
                            } else if (name.equals("Total"))
                            {
                                String totalCount = parser.nextText();
                                int total = Integer.parseInt(totalCount);
                                if (TextUtils.isEmpty(totalCount))
                                    DataManager.getInstance().setTotalLicenseCount(0);
                                else
                                    DataManager.getInstance().setTotalLicenseCount(total);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("VehicleClass1"))
                        {
                            person.setVehicleClass1(vehicleClass1);
                            isClass1 = false;
                        }
                        if (name.equals("VehicleClass2"))
                        {
                            person.setVehicleClass2(vehicleClass2);
                            isClass2 = false;
                        }
                        if (name.equals("VehicleClass3"))
                        {
                            person.setVehicleClass3(vehicleClass3);
                            isClass3 = false;
                        }
                        if (name.equals("VehicleClass4"))
                        {
                            person.setVehicleClass4(vehicleClass4);
                            isClass4 = false;
                        }
                        if (name.equals("Card"))
                        {
                            isCard = false;
                        }
                        if (name.equals("DrivingLicenseCard"))
                        {
                            personList.add(person);
                        }
					/*if (name.equals("DrivingLicenseCard"))
					{
						personList.add(person);
					}*/
                        if (name.equals("Total"))
                        {
                            return personList;
                        }
                }
                eventType = parser.next();
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return personList;
    }

    public static ArrayList<SmartObject> parseFromdaysResponse(String response)
    {

        ArrayList<SmartObject> options = null;
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            SmartObject option = null;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("options"))
                        {
                            options = new ArrayList<SmartObject>();
                        }
                        if (name.equals("option"))
                        {
                            option = new SmartObject();
                            int value = Integer.parseInt(parser.getAttributeValue(null, "value"));
                            String identity = parser.nextText();
                            if (TextUtils.isEmpty(identity))
                            {
                                option.setName("no option");
                                option.setId(-2);
                            } else
                            {
                                option.setName(identity);
                                option.setId(value);
                            }
                            options.add(option);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("option"))
                        {
                            options.add(option);
                            option = null;
                        }
                }
                eventType = parser.next();

            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return options;
    }

    public static ArrayList<SmartObject> listBuyerRatingQuestionsResponse(String response)
    {

        ArrayList<SmartObject> options = null;
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(response.getBytes("UTF-8")), null);
            parser.nextTag();
            int eventType = parser.getEventType();
            SmartObject option = null;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String name = null;
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("Questions"))
                        {
                            options = new ArrayList<SmartObject>();
                        }
                        if (name.equals("question"))
                        {
                            option = new SmartObject();
                            int value = Integer.parseInt(parser.getAttributeValue(null, "value"));
                            String identity = parser.nextText();
                            if (TextUtils.isEmpty(identity))
                            {
                                option.setName("no option");
                                option.setId(-2);
                            } else
                            {
                                option.setName(identity);
                                option.setId(value);
                            }
                            options.add(option);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equals("option"))
                        {
                            options.add(option);
                            option = null;
                        }
                }
                eventType = parser.next();

            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return options;
    }

    public static VehicleDetails parsesSynopsisForVehicle(Object result)
    {
        VehicleDetails details = new VehicleDetails();
        try
        {
            SoapObject outer = (SoapObject) result;
            SoapObject inner = (SoapObject) outer.getPropertySafely("Synopsis");
            SoapObject headerObj = (SoapObject) inner.getPropertySafely("SummaryHeader");
            details.setVin(inner.getPropertySafelyAsString("VIN", "No VIN loaded"));
            if (inner.getPropertySafelyAsString("Kilometers", "0").equals("anyType{}"))
            {
                details.setMileage(0);
            } else
            {
                details.setMileage(Integer.parseInt(inner.getPropertySafelyAsString("Kilometers", "0")));
            }
            try
            {
                SoapObject imageObject = (SoapObject) headerObj.getPropertySafely("VariantImage", "");
                details.setImageURL(imageObject.getPropertySafelyAsString("ImageUrl", ""));
            } catch (Exception e)
            {
                details.setImageURL(null);
            }
            if (headerObj.getPropertySafely("VehicleDetails") != null || !headerObj.getPropertySafely("VehicleDetails").equals("null"))
            {
                SoapObject vehicleObj = (SoapObject) headerObj.getPropertySafely("VehicleDetails");
                details.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Year", "")));
                details.setMakeId(Integer.parseInt(vehicleObj.getPropertySafelyAsString("MakeId", "")));
                details.setModelId(Integer.parseInt(vehicleObj.getPropertySafelyAsString("ModelId", "")));
                details.setModelName(vehicleObj.getPropertySafelyAsString("ModelName", "Model?"));
                details.setVariantID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("VariantId", "")));
                details.setVariantName(vehicleObj.getPropertySafelyAsString("VariantName", "Variant?"));
                details.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));

                details.setMmcode(vehicleObj.getPropertySafelyAsString("MMCode", ""));
                details.setTransmission(vehicleObj.getPropertySafelyAsString("Transmission", ""));
                details.setOfferStart(vehicleObj.getPropertySafelyAsString("StartDate", ""));
                details.setOfferEnd(vehicleObj.getPropertySafelyAsString("EndDate", ""));
            }

            if (headerObj.getPropertySafely("VariantDetails") != null || !headerObj.getPropertySafely("VariantDetails").equals("null"))
            {
                SoapObject variantObj = (SoapObject) headerObj.getPropertySafely("VariantDetails");
                details.setTransmission_Type(variantObj.getPropertySafelyAsString("Transmission_Type", ""));
                details.setGears(variantObj.getPropertySafelyAsString("Gears", "Gears?"));
                details.setFuel_Type(variantObj.getPropertySafelyAsString("Fuel_Type", ""));
                try
                {
                    details.setPower_KW(Integer.parseInt(variantObj.getPropertySafelyAsString("Power_KW", "0")));
                } catch (Exception e)
                {
                    details.setPower_KW((int) Double.parseDouble(variantObj.getPropertySafelyAsString("Power_KW", "0.0")));
                }
                details.setTorque_NM(NumberFormat.getInstance().parse(variantObj.getPropertySafelyAsString("Torque_NM", "0")).intValue());
                details.setEngine_CC(((Number) NumberFormat.getInstance().parse((variantObj.getPropertySafelyAsString("Engine_CC", "0").replaceAll(" ", "")))).intValue());
                details.setGearbox(variantObj.getPropertySafelyAsString("Gearbox", ""));
            }

            if (inner.getPropertySafely("Pricing") != null || !inner.getPropertySafely("Pricing").equals("null"))
            {
                SoapObject pricingObj = (SoapObject) inner.getPropertySafely("Pricing");
                details.setIx_trade(Helper.formatPrice(pricingObj.getPropertySafelyAsString("TradePrice", "0")));
                details.setOnline_price(Helper.formatPrice(pricingObj.getPropertySafelyAsString("RetailPrice", "0")));
                details.setSimple_logic_trade(Helper.formatPrice(pricingObj.getPropertySafelyAsString("SimpleLogicTradePrice", "0")));
                details.setSimple_logic_retail(Helper.formatPrice(pricingObj.getPropertySafelyAsString("SimpleLogicRetailPrice", "0")));
                details.setPrivate_advert(Helper.formatPrice(pricingObj.getPropertySafelyAsString("PrivateAdvertsPrice", "0")));
                details.setTUARetailPrice(Helper.formatPrice(pricingObj.getPropertySafelyAsString("TUARetailPrice", "0")));
                details.setTUATradePrice(Helper.formatPrice(pricingObj.getPropertySafelyAsString("TUATradePrice", "0")));
                details.setTUADate(Helper.convertUTCDateToNormal(pricingObj.getPropertySafelyAsString("SearchDateTime", "Date?")));
            }

            SoapObject demandSummaryObject = (SoapObject) inner.getPropertySafely("DemandSummary", "default");
            SmartObject demandSummaryItem;
            SoapObject internalObject;
            for (int i = 0; i < demandSummaryObject.getPropertyCount(); i++)
            {
                demandSummaryItem = new SmartObject();
                if (demandSummaryObject.getProperty(i) instanceof SoapObject)
                {
                    internalObject = (SoapObject) demandSummaryObject.getProperty(i);
                    demandSummaryItem.setId(Integer.parseInt(internalObject.getPropertySafelyAsString("Value", "0")));
                    demandSummaryItem.setName(internalObject.getPropertySafelyAsString("Area", ""));
                    demandSummaryItem.setType(internalObject.getPropertySafelyAsString("Type", ""));
                    details.getDemandSummaryList().add(demandSummaryItem);
                } else
                {

                    SoapPrimitive p = (SoapPrimitive) demandSummaryObject.getProperty(i);
                    String total = p.getValue().toString();
                }
            }

            SoapObject averageAvailableSummaryObj = (SoapObject) inner.getPropertySafely("AverageAvailableSummary", "default");
            SmartObject averageAvailableSummaryItem;
            for (int i = 0; i < averageAvailableSummaryObj.getPropertyCount(); i++)
            {
                averageAvailableSummaryItem = new SmartObject();
                if (averageAvailableSummaryObj.getProperty(i) instanceof SoapObject)
                {
                    averageAvailableSummaryItem.setId(Integer.parseInt(((SoapObject) averageAvailableSummaryObj.getProperty(i)).getPropertySafelyAsString("Value", "0")));
                    averageAvailableSummaryItem.setName(((SoapObject) averageAvailableSummaryObj.getProperty(i)).getPropertySafelyAsString("Area"));
                    averageAvailableSummaryItem.setType(((SoapObject) averageAvailableSummaryObj.getProperty(i)).getPropertySafelyAsString("Type", ""));
                    details.getAverageAvailableSummaryList().add(averageAvailableSummaryItem);
                } else
                {
                    SoapPrimitive p = (SoapPrimitive) averageAvailableSummaryObj.getProperty(i);
                    String total = p.getValue().toString();
                }
            }


            SoapObject averageDaysInStockSummaryObj = (SoapObject) inner.getPropertySafely("AverageDaysInStockSummary", "default");
            SmartObject averageDaysInStockSummaryItem;
            for (int i = 0; i < averageDaysInStockSummaryObj.getPropertyCount(); i++)
            {
                averageDaysInStockSummaryItem = new SmartObject();
                if (averageDaysInStockSummaryObj.getProperty(i) instanceof SoapObject)
                {
                    averageDaysInStockSummaryItem.setId(Integer.parseInt(((SoapObject) averageDaysInStockSummaryObj.getProperty(i)).getPropertySafelyAsString("Value", "0")));
                    averageDaysInStockSummaryItem.setName(((SoapObject) averageDaysInStockSummaryObj.getProperty(i)).getPropertySafelyAsString("Area"));
                    averageDaysInStockSummaryItem.setType(((SoapObject) averageDaysInStockSummaryObj.getProperty(i)).getPropertySafelyAsString("Type", ""));
                    details.getAverageDaysInStockSummaryList().add(averageDaysInStockSummaryItem);
                } else
                {

                    SoapPrimitive p = (SoapPrimitive) averageDaysInStockSummaryObj.getProperty(i);
                    String total = p.getValue().toString();
                }
            }

            SoapObject leadPoolSummaryObj = (SoapObject) inner.getPropertySafely("LeadPoolSummary", "default");
            SmartObject leadPoolSummaryItem;
            for (int i = 0; i < leadPoolSummaryObj.getPropertyCount(); i++)
            {
                leadPoolSummaryItem = new SmartObject();
                if (leadPoolSummaryObj.getProperty(i) instanceof SoapObject)
                {
                    leadPoolSummaryItem.setId(Integer.parseInt(((SoapObject) leadPoolSummaryObj.getProperty(i)).getPropertySafelyAsString("Value", "0")));
                    leadPoolSummaryItem.setName(((SoapObject) leadPoolSummaryObj.getProperty(i)).getPropertySafelyAsString("Area"));
                    leadPoolSummaryItem.setType(((SoapObject) leadPoolSummaryObj.getProperty(i)).getPropertySafelyAsString("Type", ""));

                    details.getLeadPoolSummaryList().add(leadPoolSummaryItem);
                } else
                {
                    SoapPrimitive p = (SoapPrimitive) leadPoolSummaryObj.getProperty(i);
                    String total = p.getValue().toString();
                }
            }

            SoapObject warrantySummaryObj = (SoapObject) inner.getPropertySafely("WarrantySummary", "default");
            SmartObject warrantySummaryItem;
            for (int i = 0; i < warrantySummaryObj.getPropertyCount(); i++)
            {
                warrantySummaryItem = new SmartObject();
                if (warrantySummaryObj.getProperty(i) instanceof SoapObject)
                {
                    //warrantySummaryItem.setId(Integer.parseInt(((SoapObject) warrantySummaryObj.getProperty(i)).getPropertySafelyAsString("Value","0")));
                    warrantySummaryItem.setName(((SoapObject) warrantySummaryObj.getProperty(i)).getPropertySafelyAsString("Area"));
                    warrantySummaryItem.setType(((SoapObject) warrantySummaryObj.getProperty(i)).getPropertySafelyAsString("Type", ""));
                    details.getWarrantySummaryList().add(warrantySummaryItem);
                }
            }

            int reviewCount = Integer.parseInt(inner.getPropertySafelyAsString("ReviewCount", "0"));
            details.setReviewCounts(reviewCount);


        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return details;
    }

}
