package com.nw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nw.adapters.SendOfferAdapter;
import com.nw.adapters.VehicleVariantAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.DoAppraisalInputListener;
import com.nw.model.BaseImage;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SendOfferViewMessageResponse;
import com.nw.model.SmartObject;
import com.nw.model.SpecialVehicle;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VolleySingleton;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class SendOfferFragment extends BaseFragement implements OnClickListener
{
    ImageView ivArrowIcon, ivAddExtra;
    RelativeLayout rlChangeVehicle;
    LinearLayout llayoutMessages;
    EditText et_sendoffer_name, et_sendoffer_surname, et_sendoffer_company, et_sendoffer_email, et_sendoffer_mobile, et_sendoffer_my_offer, et_date_drop_down, et_sendcopy_to_email, et_sendoffer_to_sms, et_sendoffer_to_email;
    TextView tv_sendoffer_sms_body, tv_sendoffer_email_subject, tv_sendoffer_email_body, tvTitleCarName, tvCardetails;
    TextView tv_sendoffer_mileage, tv_sendoffer_colour, tv_sendoffer_registration, tv_sendoffer_vin;
    StaticListView listview;
    CheckBox chk_sendcopy_to_email, chk_sendoffer_to_sms, chk_sendoffer_to_email;
    Button btnNext;

    NetworkImageView ivVehicleImage;
    ImageLoader imageLoader;

    SendOfferAdapter sendOfferAdapter;
    ArrayList<String> arrayList;
    ArrayList<SmartObject> daysListDropDown;
    VehicleDetails summeryDetails;
    ArrayList<BaseImage> vehicleImage;
    VehicleDetails vehicleDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_send_offers, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            vehicleDetails = getArguments().getParcelable("vehicleDetails");
        }
        initialise(view);
        return view;
    }

    private void initialise(View view)
    {
        imageLoader = VolleySingleton.getInstance().getImageLoader();

        ivArrowIcon = (ImageView) view.findViewById(R.id.ivArrowIcon);
        rlChangeVehicle = (RelativeLayout) view.findViewById(R.id.rlChangeVehicle);
        llayoutMessages = (LinearLayout) view.findViewById(R.id.llayoutMessages);
        rlChangeVehicle.setOnClickListener(this);

        // EditText
        et_sendoffer_name = (EditText) view.findViewById(R.id.et_sendoffer_name);
        et_sendoffer_surname = (EditText) view.findViewById(R.id.et_sendoffer_surname);
        et_sendoffer_company = (EditText) view.findViewById(R.id.et_sendoffer_company);
        et_sendoffer_email = (EditText) view.findViewById(R.id.et_sendoffer_email);
        et_sendoffer_mobile = (EditText) view.findViewById(R.id.et_sendoffer_mobile);
        et_sendoffer_my_offer = (EditText) view.findViewById(R.id.et_sendoffer_my_offer);
        et_date_drop_down = (EditText) view.findViewById(R.id.et_date_drop_down);

        et_sendcopy_to_email = (EditText) view.findViewById(R.id.et_sendcopy_to_email);
        et_sendoffer_to_sms = (EditText) view.findViewById(R.id.et_sendoffer_to_sms);
        et_sendoffer_to_email = (EditText) view.findViewById(R.id.et_sendoffer_to_email);

        et_date_drop_down.setOnClickListener(this);

        //TextView
        tv_sendoffer_sms_body = (TextView) view.findViewById(R.id.tv_sendoffer_sms_body);
        tv_sendoffer_email_subject = (TextView) view.findViewById(R.id.tv_sendoffer_email_subject);
        tv_sendoffer_email_body = (TextView) view.findViewById(R.id.tv_sendoffer_email_body);
        tvTitleCarName = (TextView) view.findViewById(R.id.tvTitleCarName);
        tvCardetails = (TextView) view.findViewById(R.id.tvCardetails);

        tv_sendoffer_mileage = (TextView) view.findViewById(R.id.tv_sendoffer_mileage);
        tv_sendoffer_colour = (TextView) view.findViewById(R.id.tv_sendoffer_colour);
        tv_sendoffer_registration = (TextView) view.findViewById(R.id.tv_sendoffer_registration);
        tv_sendoffer_vin = (TextView) view.findViewById(R.id.tv_sendoffer_vin);

        //Button
        btnNext = (Button) view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);

        //ImageView
        ivVehicleImage = (NetworkImageView) view.findViewById(R.id.ivVehicleImage);
        ivAddExtra = (ImageView) view.findViewById(R.id.ivAddExtra);
        ivVehicleImage.setOnClickListener(this);
        ivAddExtra.setOnClickListener(this);

        //CheckBox
        chk_sendoffer_to_email = (CheckBox) view.findViewById(R.id.chk_sendoffer_to_email);
        chk_sendoffer_to_sms = (CheckBox) view.findViewById(R.id.chk_sendoffer_to_sms);
        chk_sendcopy_to_email = (CheckBox) view.findViewById(R.id.chk_sendcopy_to_email);

        //ListView
        listview = (StaticListView) view.findViewById(R.id.listview);

        arrayList = new ArrayList<>();
        arrayList.add("");
        sendOfferAdapter = new SendOfferAdapter(getContext(), arrayList, new DoAppraisalInputListener()
        {
            @Override
            public void onButtonClicked(boolean isName, int position, String message)
            {
                if (message.equalsIgnoreCase("delete"))
                {
                    arrayList.remove(position);
                    sendOfferAdapter.notifyDataSetChanged();
                } else
                {
                    arrayList.remove(position);
                    arrayList.add(position, message);
                }
            }
        });

        listview.setAdapter(sendOfferAdapter);
        SetPreviousData();
        // Call the web service to get the Send Offer Records.
        GetSendOffer();
    }

    private void SetPreviousData()
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

            tv_sendoffer_mileage.setText("" + summeryDetails.getMileage()+"km");

            if (summeryDetails.getColour() != null)
                tv_sendoffer_colour.setText(summeryDetails.getColour());

            if (summeryDetails.getRegistration() != null)
                tv_sendoffer_registration.setText(summeryDetails.getRegistration());

            if (summeryDetails.getVin() != null)
                tv_sendoffer_vin.setText(summeryDetails.getVin());

            tvCardetails.setText(detailsBuilder.toString());
            //  getDoAppraisalData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                hideKeyboard();
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Send Offer");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rlChangeVehicle:
                if (llayoutMessages.getVisibility() != View.GONE)
                {
                    llayoutMessages.setVisibility(View.GONE);
                    ivArrowIcon.setRotation(0);
                } else
                {
                    llayoutMessages.setVisibility(View.VISIBLE);
                    ivArrowIcon.setRotation(90);
                }
                break;
            case R.id.ivAddExtra:
                arrayList.add("");
                sendOfferAdapter.notifyDataSetChanged();
                break;
            case R.id.et_date_drop_down:
                if (daysListDropDown != null)
                {
                    if (daysListDropDown.size() > 0)
                    {
                        Helper.showDropDown(et_date_drop_down, new ArrayAdapter(getActivity(), R.layout.list_item_text3, R.id.tvText, daysListDropDown), new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                et_date_drop_down.setText(daysListDropDown.get(position).toString());
                            }
                        });
                    }
                }
                break;
            case R.id.btnNext:
                if(validation())
                {
                    SaveSendOffer();
                }
                break;
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
        }
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

    private void GetSendOffer()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
            //   parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("Year", 2017, Integer.class));
            //     parameterList.add(new Parameter("Year", summeryDetails.getYear(), Integer.class));
            parameterList.add(new Parameter("MakeId", 5, Integer.class));
            //    parameterList.add(new Parameter("MakeId",summeryDetails.getMakeId(), Integer.class));
            parameterList.add(new Parameter("ModelId", 17, Integer.class));
            //    parameterList.add(new Parameter("ModelId", summeryDetails.getModelId(), Integer.class));
            parameterList.add(new Parameter("VariantId", 5595, Integer.class));
            //    parameterList.add(new Parameter("VariantId", summeryDetails.getVariantID(), Integer.class));
            parameterList.add(new Parameter("AppraisalId", 2, Integer.class));
            parameterList.add(new Parameter("SendOfferId", 1, Integer.class));
            parameterList.add(new Parameter("VinNumber", vehicleDetails.getVin(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("SendOfferViewMessage");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/SendOfferViewMessage");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            //Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("soap response", result.toString());
                        hideProgressDialog();
                        Helper.Log("Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject response = (SoapObject) outer.getPropertySafely("SendOffer");

                        // parse the SellerInfo
                        SoapObject responsitems = (SoapObject) response.getPropertySafely("SellerInfo");

                        SendOfferViewMessageResponse sendOfferViewMessageResponse = new SendOfferViewMessageResponse();
                        sendOfferViewMessageResponse.setName(responsitems.getPropertySafelyAsString("Name", ""));
                        sendOfferViewMessageResponse.setSurname(responsitems.getPropertySafelyAsString("Surname", ""));
                        sendOfferViewMessageResponse.setCompany(responsitems.getPropertySafelyAsString("Company", ""));
                        sendOfferViewMessageResponse.setEmail(responsitems.getPropertySafelyAsString("Email", ""));
                        sendOfferViewMessageResponse.setMobile(responsitems.getPropertySafelyAsString("Mobile", ""));

                        // parse the OfferExpiresIn
                        SoapObject OfferExpiresIn = (SoapObject) response.getPropertySafely("OfferExpiresIn");
                        sendOfferViewMessageResponse.setDays(OfferExpiresIn.getPropertySafelyAsString("Days", ""));

                        // parse the SMS
                        SoapObject SMS = (SoapObject) response.getPropertySafely("SMS");
                        sendOfferViewMessageResponse.setBodySMS(SMS.getPropertySafelyAsString("Body", ""));
                        sendOfferViewMessageResponse.setSelectedToSendSMS(SMS.getPropertySafelyAsString("SelectedToSend", ""));

                        // parse the Email
                        SoapObject Email = (SoapObject) response.getPropertySafely("Email");
                        sendOfferViewMessageResponse.setSubject(Email.getPropertySafelyAsString("Subject", ""));
                        sendOfferViewMessageResponse.setBodyEmail(Email.getPropertySafelyAsString("Body", ""));
                        sendOfferViewMessageResponse.setSelectedToSendEmail(Email.getPropertySafelyAsString("SelectedToSend", ""));

                        Setdata(sendOfferViewMessageResponse);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                // getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void SaveSendOffer()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SaveAndSendOffer xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");

            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<SaveAndSendOfferXML>");
            soapMessage.append("<SendOffer>");

            soapMessage.append("<CarInfo>");
            soapMessage.append("<Year>" + 2010 + "</Year>");
            soapMessage.append("<MakeId>" + 5 + "</MakeId>");
            soapMessage.append("<ModelId>" + 17 + "</ModelId>");
            soapMessage.append("<VariantId>" + 5595 + "</VariantId>");
            soapMessage.append("</CarInfo>");

            soapMessage.append("<SellerInfo>");
            soapMessage.append("<Name>" + et_sendoffer_name.getText().toString().trim() + "</Name>");
            soapMessage.append("<Surname>" + et_sendoffer_surname.getText().toString().trim() + "</Surname>");
            soapMessage.append("<Company>" + et_sendoffer_company.getText().toString().trim() + "</Company>");
            soapMessage.append("<Email>" + et_sendoffer_email.getText().toString().trim() + "</Email>");
            soapMessage.append("<Mobile>" + et_sendoffer_mobile.getText().toString().trim() + "</Mobile>");
            soapMessage.append("</SellerInfo>");

            soapMessage.append("<Offer>" + et_sendoffer_my_offer.getText().toString().trim() + "</Offer>");
            soapMessage.append("<OfferExpiresIn>" + et_date_drop_down.getText().toString().trim() + "</OfferExpiresIn>");

            soapMessage.append("<SubjectTo>");
            for (int i = 0; i < arrayList.size(); i++)
            {
                if (!arrayList.get(i).equalsIgnoreCase(""))
                {
                    soapMessage.append("<Subject>");
                    soapMessage.append("<text>" + arrayList.get(i) + "</text>");
                    soapMessage.append("</Subject>");
                }
            }
            soapMessage.append("</SubjectTo>");

            soapMessage.append("<SMS>");
            soapMessage.append("<SMSNumbers>" + et_sendoffer_to_sms.getText().toString().trim() + "</SMSNumbers>");
            if (chk_sendoffer_to_sms.isSelected())
                soapMessage.append("<SMSFlag>" + 1 + "</SMSFlag>");
            else
                soapMessage.append("<SMSFlag>" + 0 + "</SMSFlag>");
            soapMessage.append("</SMS>");

            soapMessage.append("<Email>");
            soapMessage.append("<SendCopyTo>" + et_sendcopy_to_email.getText().toString().trim() + "</SendCopyTo>");
            if (chk_sendcopy_to_email.isSelected())
                soapMessage.append("<EmailSendCopyToFlag>" + 1 + "</EmailSendCopyToFlag>");
            else
                soapMessage.append("<EmailSendCopyToFlag>" + 0 + "</EmailSendCopyToFlag>");
            if(chk_sendoffer_to_email.isSelected())
                soapMessage.append("<EmailSendOfferFlag>" + 1 + "</EmailSendOfferFlag>");
            else
                soapMessage.append("<EmailSendOfferFlag>" + 0 + "</EmailSendOfferFlag>");
            soapMessage.append("</Email>");

            soapMessage.append("<RootInfo>");
            soapMessage.append("<AppraisalId>" + 1 + "</AppraisalId>");
            soapMessage.append("<ClientId>" + 1 + "</ClientId>");
            //   soapMessage.append("<ClientId>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</ClientId>");
            soapMessage.append("<VinNumber>" + vehicleDetails.getVin() + "</VinNumber>");
            soapMessage.append("</RootInfo>");

            soapMessage.append("</SendOffer>");

            soapMessage.append("</SaveAndSendOfferXML>");
            soapMessage.append("</SaveAndSendOffer>");
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
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_condition_data), new DialogListener()
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
                        String PassOrFailed = ParserManager.parsetokenChecker(response, "PassOrFailed");
                        String message = "";
                        if (PassOrFailed.equalsIgnoreCase("true"))
                        {
                            message = "SendOffer save successfully";
                        } else
                        {
                            message = "Error while saving SendOffer";
                        }

                        CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
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
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SaveAndSendOffer", listener);
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

    private void Setdata(SendOfferViewMessageResponse sendOfferViewMessageResponse)
    {
        et_sendoffer_name.setText(sendOfferViewMessageResponse.getName());
        et_sendoffer_surname.setText(sendOfferViewMessageResponse.getSurname());
        et_sendoffer_company.setText(sendOfferViewMessageResponse.getCompany());
        et_sendoffer_email.setText(sendOfferViewMessageResponse.getEmail());
        et_sendoffer_mobile.setText(sendOfferViewMessageResponse.getMobile());
        et_sendoffer_to_email.setText(sendOfferViewMessageResponse.getEmail());

        tv_sendoffer_sms_body.setText(sendOfferViewMessageResponse.getBodySMS());
        tv_sendoffer_email_subject.setText("Subject: " + sendOfferViewMessageResponse.getSubject());
        tv_sendoffer_email_body.setText("Body: " + sendOfferViewMessageResponse.getBodyEmail());

        List<String> DaysList = Arrays.asList(sendOfferViewMessageResponse.getDays().split(","));
        daysListDropDown = new ArrayList<>();
        for (int i = 0; i < DaysList.size(); i++)
        {
            SmartObject vehicleNew = new SmartObject();
            vehicleNew.setName(DaysList.get(i));
            daysListDropDown.add(vehicleNew);
        }

        if (daysListDropDown.size() > 0)
        {
            et_date_drop_down.setText(daysListDropDown.get(0).getName());
        }
    }

    private boolean validation()
    {
        if (TextUtils.isEmpty(et_sendoffer_name.getText().toString().trim()))
        {
            Helper.showToast("Please enter name", getActivity());
            return false;
        }

        if (TextUtils.isEmpty(et_sendoffer_surname.getText().toString().trim()))
        {
            Helper.showToast("Please enter surname", getActivity());
            return false;
        }

        if (TextUtils.isEmpty(et_sendoffer_email.getText().toString().trim()))
        {
            Helper.showToast("Please enter email", getActivity());
            return false;
        }

        if (!Helper.validMail(et_sendoffer_email.getText().toString().trim()))
        {
            Helper.showToast("Please enter valid email address", getActivity());
            return false;
        }

        if(!TextUtils.isEmpty(et_sendcopy_to_email.getText().toString().trim()))
        {
            if (et_sendcopy_to_email.getText().toString().trim().equalsIgnoreCase(""))
            {
                CustomDialogManager.showOkDialog(getActivity(), "Please enter email id");
                return false;
            }

            String emails = et_sendcopy_to_email.getText().toString().trim();
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
        return true;
    }

    public boolean validEmail(String str_newEmail)
    {
        return str_newEmail.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }
}
