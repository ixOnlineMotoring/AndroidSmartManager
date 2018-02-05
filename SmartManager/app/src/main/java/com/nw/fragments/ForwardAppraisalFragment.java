package com.nw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nw.interfaces.DialogListener;
import com.nw.model.BaseImage;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ForwardAppraisalFragment extends BaseFragement implements OnClickListener
{

    NetworkImageView ivVehicleImage;
    TextView tvTitleCarName, tvCardetails;
    Button btnSend;
    EditText edemailids;

    VehicleDetails summeryDetails;
    ImageLoader imageLoader;
    ArrayList<BaseImage> vehicleImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_forward_appraisal, container, false);
        setHasOptionsMenu(true);
        initialise(view);

        return view;
    }

    private void initialise(View view)
    {
        imageLoader = VolleySingleton.getInstance().getImageLoader();

        ivVehicleImage = (NetworkImageView) view.findViewById(R.id.ivVehicleImage);
        ivVehicleImage.setOnClickListener(this);

        tvTitleCarName = (TextView) view.findViewById(R.id.tvTitleCarName);
        tvCardetails = (TextView) view.findViewById(R.id.tvCardetails);

        btnSend = (Button) view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        edemailids = (EditText) view.findViewById(R.id.edemailids);


        setPreviousVehicledata();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Forward Internal Appraisal");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ivVehicleImage:
                if (vehicleImage == null)
                {
                    vehicleImage = new ArrayList<BaseImage>();
                    BaseImage baseImage = new BaseImage();
                    baseImage.setPath(summeryDetails.getImageURL());
                    baseImage.setLink(summeryDetails.getImageURL());
                    baseImage.setThumbPath(summeryDetails.getImageURL());
                    vehicleImage.add(baseImage);
                }
                navigateToLargeImage(0, vehicleImage);
                break;
            case R.id.btnSend:
                if (validate())
                {
                    SendForwardAppraisal();
                }
                break;
        }
    }

    private void SendForwardAppraisal()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SendFowardAppraisalEmails xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");

            soapMessage.append("<AppraisalId>" + "1" + "</AppraisalId>");    // Need To make dynamic
            soapMessage.append("<FowardEmails>" + edemailids.getText().toString().trim() + "</FowardEmails>"); // Need To make dynamic

            soapMessage.append("</SendFowardAppraisalEmails>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            Helper.Log("SaveVehicle request", "" + soapMessage);

            VollyResponseListener listener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    VolleyLog.e("Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    hideProgressDialog();
                    if (response == null)
                    {
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_VehicleExtras), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                //getFragmentManager().popBackStack();
                            }
                        });
                        return;
                    } else
                    {
                        final String PassOrFailed = ParserManager.parsetokenChecker(response, "Message");

                        CustomDialogManager.showOkDialog(getActivity(), PassOrFailed, new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {

                            }
                        });
                    }
                }
            };
            request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SendFowardAppraisalEmails", listener);
            try
            {
                request.init();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private boolean validate()
    {
        if (edemailids.getText().toString().trim().equalsIgnoreCase(""))
        {
            CustomDialogManager.showOkDialog(getActivity(), "Please enter email id");
            return false;
        }

        String emails = edemailids.getText().toString().trim();
        StringTokenizer st = new StringTokenizer(emails, ";");

        while (st.hasMoreElements())
        {
            String email = st.nextToken().trim();
            if (!validEmail(email))
            {
                CustomDialogManager.showOkDialog(getActivity(), "Email address: " + email + " is incorrect");
                return false;
            }
        }
        return true;
    }

    public boolean validEmail(String str_newEmail)
    {
        return str_newEmail.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    private void navigateToLargeImage(int position, ArrayList<BaseImage> images)
    {
        try
        {
            Intent iToBuyActivity = new Intent(getActivity(), BuyActivity.class);
            iToBuyActivity.putParcelableArrayListExtra("imagelist", images);
            iToBuyActivity.putExtra("index", position);
            iToBuyActivity.putExtra("vehicleName", tvTitleCarName.getText().toString());
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setPreviousVehicledata()
    {
        if (getArguments() != null)
        {
            summeryDetails = getArguments().getParcelable("vehicleDetails");

            ivVehicleImage.setImageUrl(summeryDetails.getImageURL(), imageLoader);
            ivVehicleImage.setErrorImageResId(R.drawable.noimage);
            ivVehicleImage.setDefaultImageResId(R.drawable.noimage);

            tvTitleCarName.setText(Html.fromHtml("<font color=#ffffff>" + summeryDetails.getYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
                    + summeryDetails.getFriendlyName() + "</font>"));

            StringBuilder detailsBuilder = new StringBuilder();
            if (summeryDetails.getEngine_CC() != 0)
                detailsBuilder.append(summeryDetails.getEngine_CC() + "cc");
            if (summeryDetails.getPower_KW() != 0)
                detailsBuilder.append(", " + summeryDetails.getPower_KW() + "kW");
            if (summeryDetails.getTorque_NM() != 0)
                detailsBuilder.append(", " + summeryDetails.getTorque_NM() + "Nm");
            if (summeryDetails.getFuel_Type() != null && !summeryDetails.getFuel_Type().isEmpty())
                detailsBuilder.append(", " + summeryDetails.getFuel_Type() + "");
            if (summeryDetails.getTransmission_Type() != null && !summeryDetails.getTransmission_Type().isEmpty())
                detailsBuilder.append(", " + summeryDetails.getTransmission_Type());
            if (summeryDetails.getGearbox() != null)
                if (summeryDetails.getGearbox() != null && !summeryDetails.getGearbox().isEmpty() && !summeryDetails.getGearbox().equalsIgnoreCase("Gears?"))
                    detailsBuilder.append(", " + summeryDetails.getGearbox());
            if (summeryDetails.getGears() != null)
                if (summeryDetails.getGears() != null && !summeryDetails.getGears().isEmpty() && !summeryDetails.getGears().equalsIgnoreCase("Gears?"))
                    detailsBuilder.append(", " + summeryDetails.getGears() + " gears");
            if (summeryDetails.getOfferStart() != null && !summeryDetails.getOfferStart().isEmpty())
                if (TextUtils.isEmpty(summeryDetails.getOfferEnd()) || summeryDetails.getOfferStart() == null)
                {
                    if (TextUtils.isEmpty(detailsBuilder))
                    {
                        detailsBuilder.append("Avail. as new from " + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferStart()));
                    } else

                    {
                        detailsBuilder.append(". Avail. as new from " + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferStart()));
                    }
                } else
                {
                    if (TextUtils.isEmpty(detailsBuilder))
                    {
                        detailsBuilder.append("Avail. as new from " + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferStart()) + " to "
                                + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferEnd()));
                    } else
                    {
                        detailsBuilder.append(". Avail. as new from " + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferStart()) + " to "
                                + Helper.convertUTCDateToMonthYear(summeryDetails.getOfferEnd()));
                    }
                }

            tvCardetails.setText(detailsBuilder.toString());
            //  getDoAppraisalData();
        }
    }
}
