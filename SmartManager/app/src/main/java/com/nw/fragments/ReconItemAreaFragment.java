package com.nw.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.DragableGridView;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

public class ReconItemAreaFragment extends BaseFragement implements OnClickListener
{
    DragableGridView imageDragableGridView;
    Button bt_save;

    TextView tv_selected_item;
    CheckBox chk_repair, chk_replace;
    EditText edt_comments, edt_price;

    // 0 = nothing selected , 1 = repair selected , 2 = replace selected;
    int is_chk_repair_replace = 0;

    String base64value = "";
    int totalImageCount = 0,currentImageCount = 0;
    VehicleDetails vehicleDetails;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recon_item_area, container, false);
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
        //TextView
        tv_selected_item = (TextView) view.findViewById(R.id.tv_selected_item);
        tv_selected_item.setText(getArguments().getString("selected_str_title"));

        chk_repair = (CheckBox) view.findViewById(R.id.chk_repair);
        chk_replace = (CheckBox) view.findViewById(R.id.chk_replace);

        chk_repair.setOnClickListener(this);
        chk_replace.setOnClickListener(this);

        edt_comments = (EditText) view.findViewById(R.id.edt_comments);
        edt_price = (EditText) view.findViewById(R.id.edt_price);

        bt_save = (Button) view.findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //this code will be executed on devices running Marshmallow or later
            imageDragableGridView = new DragableGridView();
        } else
        {
            imageDragableGridView = new DragableGridView(getActivity());
        } // image grid view implemented in image DraggableGridView
        imageDragableGridView.init(view, new ImageClickListener()
        {

            @Override
            public void onImageClick(int position)
            {
                try
                {
                    navigateToLargeImage(position);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onImageDeleted(int position)
            {
                // updateHeader();
            }
        });
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

    @SuppressWarnings("static-access")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK)
        {
            if (imageDragableGridView != null)
            {
                if (imageDragableGridView.isOptionSelected())
                    imageDragableGridView.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void navigateToLargeImage(int position)
    {
        try
        {
            Intent iToBuyActivity = new Intent(getActivity(), BuyActivity.class);
            iToBuyActivity.putParcelableArrayListExtra("imagelist", imageDragableGridView.getLocalImageListWithoutPlus());
            iToBuyActivity.putExtra("index", position);
            iToBuyActivity.putExtra("vehicleName", "test");
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Recon Item/Area");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bt_save:
                if (checkValidation())
                {
                    saveExteriorRecondition();
                }
                break;
            case R.id.chk_repair:
                is_chk_repair_replace = 1;
                chk_repair.setChecked(true);
                chk_replace.setChecked(false);
                break;
            case R.id.chk_replace:
                is_chk_repair_replace = 2;
                chk_repair.setChecked(false);
                chk_replace.setChecked(true);
                break;
            default:
                break;
        }
    }

    private boolean checkValidation()
    {
        if (is_chk_repair_replace == 0)
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.selection_note_repair_replace));
            return false;
        }

        return true;
    }

    private void saveExteriorRecondition()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SaveExterior xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<ExteriorXML>");
            soapMessage.append("<ExteriorReconditioning>");
            soapMessage.append("<Items>");

            soapMessage.append("<Item>");
            soapMessage.append("<ExteriorTypeId>" + getArguments().getString("selected_item") + "</ExteriorTypeId>");
            if (is_chk_repair_replace == 1)
            {
                soapMessage.append("<Replace>" + "0" + "</Replace>");

                soapMessage.append("<Repair>" + "1" + "</Repair>");
            } else
            {
                soapMessage.append("<Replace>" + "1" + "</Replace>");
                soapMessage.append("<Repair>" + "0" + "</Repair>");
            }
            soapMessage.append("<Value>" + edt_price.getText().toString().trim() + "</Value>");
            soapMessage.append("<Comments>" + edt_comments.getText().toString().trim() + "</Comments>");

            soapMessage.append("</Item>");


            soapMessage.append("</Items>");
            soapMessage.append("<RootInfo>");
            soapMessage.append("<AppraisalId>" + "1" + "</AppraisalId>");
            soapMessage.append("<ClientId>" + "1" + "</ClientId>");
            //   soapMessage.append("<ClientId>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</ClientId>");
            soapMessage.append("<VinNumber>" + vehicleDetails.getVin() + "</VinNumber>");
            soapMessage.append("<Comments>" + "" + "</Comments>");
            soapMessage.append("</RootInfo>");

            soapMessage.append("</ExteriorReconditioning>");
            soapMessage.append("</ExteriorXML>");
            soapMessage.append("</SaveExterior>");
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

                    if (response == null)
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_Exteriorcondition_data), new DialogListener()
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
                        if(imageDragableGridView.getUpdatedImageListWithoutPlus().size() > 0)
                        {
                            saveImageExteriorRecondition();
                        }else
                        {
                            hideProgressDialog();
                            final String PassOrFailed = ParserManager.parsetokenChecker(response, "PassOrFailed");
                            String message = "";
                            if (PassOrFailed.equalsIgnoreCase("true"))
                            {
                                message = getArguments().getString("selected_str_title")+" save successfully";
                            } else
                            {
                                message = "Error while saving "+getArguments().getString("selected_str_title");
                            }

                            final String finalMessage = message;
                            CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                            {
                                @Override
                                public void onButtonClicked(int type)
                                {
                                    if(finalMessage.contains("successfully"))
                                    {
                                        hideKeyboard();
                                        getActivity().getFragmentManager().popBackStack();
                                    }
                                }
                            });
                        }

                    }
                }
            };
            request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SaveExterior", listener);
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

    private void saveImageExteriorRecondition()
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
          // showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<SaveExteriorImage xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<ExteriorImageXML>");
            soapMessage.append("<ExteriorImage>");
            soapMessage.append("<Images>");

            for (int i = 0; i < imageDragableGridView.getUpdatedImageListWithoutPlus().size(); i++)
            {
                soapMessage.append("<Image>");
                soapMessage.append("<ExteriorValueId>"+ getArguments().getString("selected_item")+"</ExteriorValueId>");
                soapMessage.append("<ExteriorImage>"+Helper.convertBitmapToBase64(imageDragableGridView.getUpdatedImageListWithoutPlus().get(i).getPath())+"</ExteriorImage>");
                soapMessage.append("</Image>");
            }

         /*   createBase64Image();
            soapMessage.append(base64value);*/

            soapMessage.append("</Images>");
            soapMessage.append("<RootInfo>");
            soapMessage.append("<AppraisalId>" + "1" + "</AppraisalId>");
            soapMessage.append("<ClientId>" + "1" + "</ClientId>");
            //   soapMessage.append("<ClientId>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</ClientId>");
            soapMessage.append("<VinNumber>" + vehicleDetails.getVin() + "</VinNumber>");
            soapMessage.append("</RootInfo>");
            soapMessage.append("</ExteriorImage>");
            soapMessage.append("</ExteriorImageXML>");
            soapMessage.append("</SaveExteriorImage>");
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
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_saving_images), new DialogListener()
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
                        final String PassOrFailed = ParserManager.parsetokenChecker(response, "PassOrFailed");
                        String message = "";
                        if (PassOrFailed.equalsIgnoreCase("true"))
                        {
                            message = getArguments().getString("selected_str_title")+" save successfully";
                        } else
                        {
                            message = "Error while saving "+getArguments().getString("selected_str_title");
                        }

                        final String finalMessage = message;
                        CustomDialogManager.showOkDialog(getActivity(), message, new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if(finalMessage.contains("successfully"))
                                {
                                    hideKeyboard();
                                    getActivity().getFragmentManager().popBackStack();
                                }
                            }
                        });
                    }
                }
            };
            request = new VollyCustomRequest(Constants.STOCK_WEBSERVICE_URL, soapMessage.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SaveExteriorImage", listener);
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
}
