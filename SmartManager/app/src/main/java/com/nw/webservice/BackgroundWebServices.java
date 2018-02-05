package com.nw.webservice;

import android.os.AsyncTask;

import com.android.volley.VolleyError;
import com.nw.database.SMDatabase;
import com.nw.fragments.PhotosAndExtrasDetailFragment;
import com.nw.interfaces.BackgroundWebServiceListener;
import com.nw.interfaces.DialogListener;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Swapnil on 17-03-2017.
 */

public class BackgroundWebServices
{
    BackgroundWebServiceListener backgroundWebServiceListener;

    public BackgroundWebServices(BackgroundWebServiceListener backgroundWebServiceListener, String type, String webServiceRequest,ArrayList<String> videoRecodes)
    {
        this.backgroundWebServiceListener = backgroundWebServiceListener;
        methodToBeCalled(type, webServiceRequest,videoRecodes);
    }

    private void methodToBeCalled(String type, String webServiceRequest,ArrayList<String> videoRecodes)
    {
        switch (type)
        {
            case Constants.SaveBlogImage:
                saveBlogImage(webServiceRequest);
                break;
            case Constants.SaveDeliveryImage:
                saveDeliveryImage(webServiceRequest);
                break;
            case Constants.SavePhotosAndExtrasImage:
                savePhotosAndExtrasImage(webServiceRequest);
                break;
            case Constants.SaveSpecialImage:
                saveSpecialImage(webServiceRequest);
                break;
            case Constants.SaveVideos:
                new SaveVideoUpload(videoRecodes).execute();
                break;
        }
    }


    private void saveBlogImage(String webServiceRequest)
    {
        VollyResponseListener vollyResponseListener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Helper.Log("Error: ", "" + error);
                backgroundWebServiceListener.statusService(false);
            }

            @Override
            public void onResponse(String response)
            {
                Helper.Log("TAG", "" + response);
                backgroundWebServiceListener.statusService(true);
            }
        };
        VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, webServiceRequest, Constants.TEMP_URI_NAMESPACE + "IBlogService/SaveBlogImage", vollyResponseListener);
        try
        {
            request.init("" + 1);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void saveDeliveryImage(String webServiceRequest)
    {
        VollyResponseListener vollyResponseListener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Helper.Log("Error: ", "" + error);
                backgroundWebServiceListener.statusService(false);
            }

            @Override
            public void onResponse(String response)
            {
                Helper.Log("TAG", "" + response);
                backgroundWebServiceListener.statusService(true);
            }
        };
        VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, webServiceRequest, Constants.TEMP_URI_NAMESPACE + "IBlogService/SaveBlogImage", vollyResponseListener);
        try
        {
            request.init("" + 1);
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    private void savePhotosAndExtrasImage(String webServiceRequest)
    {
        VollyResponseListener vollyResponseListener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Helper.Log("Error: ", "" + error);
                backgroundWebServiceListener.statusService(false);
            }

            @Override
            public void onResponse(String response)
            {
                Helper.Log("TAG", "" + response);
                backgroundWebServiceListener.statusService(true);
            }
        };

        VollyCustomRequest request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, webServiceRequest, Constants.TEMP_URI_NAMESPACE + "IStockService/AddImageToVehicleBase64", vollyResponseListener);
        try
        {
            request.init("" + 1);
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    private void saveSpecialImage(String webServiceRequest)
    {
        VollyResponseListener vollyResponseListener = new VollyResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Helper.Log("Error: ", "" + error);
                backgroundWebServiceListener.statusService(false);
            }

            @Override
            public void onResponse(String response)
            {
                Helper.Log("TAG", "" + response);
                backgroundWebServiceListener.statusService(true);
            }
        };

        VollyCustomRequest request = new VollyCustomRequest(Constants.SPECIAL_WEBSERVICE_URL, webServiceRequest, Constants.TEMP_URI_NAMESPACE + "ISpecialsService/SaveSpecialsImage", vollyResponseListener);
        try
        {
            request.init("" + 1);
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    private class SaveVideoUpload extends AsyncTask<Void, Void, String>
    {
        ArrayList<String> videoRecodes ;
        String VideoName;
        public SaveVideoUpload(ArrayList<String> videoRecodes)
        {
            this.videoRecodes = videoRecodes;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            VideoName = HelperHttp.uploadVideoFile(new File(videoRecodes.get(0)),
                    Integer.parseInt(videoRecodes.get(1)),
                    videoRecodes.get(2),
                    videoRecodes.get(3),
                    videoRecodes.get(4),
                    Boolean.parseBoolean(videoRecodes.get(5)));

            return VideoName;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            backgroundWebServiceListener.statusService(true);
        }
    }
}
