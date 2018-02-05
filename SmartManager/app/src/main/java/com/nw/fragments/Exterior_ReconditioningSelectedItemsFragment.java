package com.nw.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.nw.adapters.Exterior_ReconditioningAdapter;
import com.nw.adapters.InteriorReconditioningAdapter;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.DoAppraisalInputListener;
import com.nw.interfaces.OnListItemClickWithKeyListener;
import com.nw.model.DataInObject;
import com.nw.model.ExteriorReconditioning;
import com.nw.model.InteriorReconditioning;
import com.nw.model.Parameter;
import com.nw.model.Vehicle;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.StaticListView;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class Exterior_ReconditioningSelectedItemsFragment extends BaseFragement implements OnClickListener
{
    StaticListView lv_ExteriorReconditioningItems;
    Exterior_ReconditioningAdapter exterior_ReconditioningAdapter;
    ArrayList<ExteriorReconditioning> arrayList;

    ImageView bt_add;
    Button btn_save_exterior;
    EditText edtDetails;
    TextView tv_total_value;
    View view_total;
    Exterior_ReconditioningFragment exteriorReconditioningFragment;
    VehicleDetails vehicleDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exterior_reconditioning_selected_items, container, false);
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
        lv_ExteriorReconditioningItems = (StaticListView) view.findViewById(R.id.lv_ExteriorReconditioningItems);

        bt_add = (ImageView) view.findViewById(R.id.iv_add);

        btn_save_exterior = (Button) view.findViewById(R.id.btn_save_exterior);

        edtDetails = (EditText) view.findViewById(R.id.edtDetails);

        tv_total_value = (TextView) view.findViewById(R.id.tv_total_value);

        view_total = (View)view.findViewById(R.id.view_total);

        bt_add.setOnClickListener(this);
        btn_save_exterior.setOnClickListener(this);
        GetExteriorReconditioning();
    }

    private void deleteDialog(final int position)
    {
        CustomDialogManager.showOkCancelDialog(getContext(), "Are you sure you want to delete " +arrayList.get(position).getExteriorType(), new DialogListener()
        {
            @Override
            public void onButtonClicked(int type)
            {
                if (type == Dialog.BUTTON_POSITIVE)
                {
                    DeleteExteriorRecondition(position);
                }
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

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Exterior Reconditioning");
        //getActivity().getActionBar().setSubtitle(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (exteriorReconditioningFragment != null)
            exteriorReconditioningFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.iv_add:
                Bundle bundle = new Bundle();
                bundle.putParcelable("vehicleDetails", getArguments().getParcelable("vehicleDetails"));
                exteriorReconditioningFragment = new Exterior_ReconditioningFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                exteriorReconditioningFragment.setArguments(bundle);
                ft.replace(R.id.Container, exteriorReconditioningFragment).addToBackStack(null);
                ft.commit();
                break;
            case R.id.btn_save_exterior:
                saveExteriorRecondition();
                break;

            default:
                break;
        }

    }

    private void setTotalValue()
    {
        if (arrayList != null)
        {
            int total_value = 0;
            for (int i = 0; i < arrayList.size(); i++)
            {
                if (arrayList.get(i).isActive())
                    total_value += arrayList.get(i).getValue();
            }
            tv_total_value.setText(Helper.formatPrice("" + total_value));
        }
    }

    private void GetExteriorReconditioning()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
            //   parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("AppraisalId", 1, Integer.class));
          //  parameterList.add(new Parameter("VinNumber", vehicleDetails.getVin(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadExterior");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadExterior");
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
                        SoapObject response = (SoapObject) outer.getPropertySafely("EngineReconditioning");
                        SoapObject responsitems = (SoapObject) response.getPropertySafely("Items");
                        if (response.getPropertySafelyAsString("Comments", "").equalsIgnoreCase("anyType{}"))
                        {
                            edtDetails.setText("");
                        } else
                        {
                         //   edtDetails.setText(response.getPropertySafelyAsString("Comments", ""));
                        }

                        arrayList = new ArrayList<ExteriorReconditioning>();
                        for (int i = 0; i < responsitems.getPropertyCount(); i++)
                        {
                            SoapObject conditionObj = (SoapObject) responsitems.getProperty(i);
                            ExteriorReconditioning exteriorReconditioning = new ExteriorReconditioning();
                            exteriorReconditioning.setExteriorValueId(conditionObj.getPropertySafelyAsString("ExteriorValueId", ""));
                            exteriorReconditioning.setRepair(conditionObj.getPropertySafelyAsString("Repair", ""));
                            exteriorReconditioning.setReplace(conditionObj.getPropertySafelyAsString("Replace", ""));
                            double d = Double.parseDouble(conditionObj.getPropertySafelyAsString("Value", "0.0"));
                            exteriorReconditioning.setValue((int) d);
                            exteriorReconditioning.setExteriorPositionID(conditionObj.getPropertySafelyAsString("ExteriorPositionID", ""));
                            exteriorReconditioning.setExteriorType(conditionObj.getPropertySafelyAsString("ExteriorType", ""));
                            exteriorReconditioning.setExteriorTypeId(conditionObj.getPropertySafelyAsString("ExteriorTypeId", ""));
                            exteriorReconditioning.setComments(conditionObj.getPropertySafelyAsString("ItemComment", ""));
                            arrayList.add(exteriorReconditioning);
                        }

                        exterior_ReconditioningAdapter = new Exterior_ReconditioningAdapter(getActivity(), arrayList, new OnListItemClickWithKeyListener()
                        {
                            @Override
                            public void onClick(int position, String key)
                            {
                                if (key.equalsIgnoreCase("delete"))
                                {

                                    deleteDialog(position);
                                } else if (key.equalsIgnoreCase("check"))
                                {
                                    arrayList.get(position).setActive(true);
                                    setTotalValue();
                                } else if (key.equalsIgnoreCase("uncheck"))
                                {
                                    arrayList.get(position).setActive(false);
                                    setTotalValue();
                                }
                            }
                        });

                        lv_ExteriorReconditioningItems.setAdapter(exterior_ReconditioningAdapter);
                        setTotalValue();

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                // getActivity().getFragmentManager().popBackStack();
                                view_total.setVisibility(View.GONE);
                                tv_total_value.setVisibility(View.GONE);
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

            soapMessage.append("<SaveSelectedExterior xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<ExteriorXML>");
            soapMessage.append("<ExteriorReconditioning>");
            soapMessage.append("<Items>");

            for (int i = 0; i < arrayList.size(); i++)
            {

                soapMessage.append("<Item>");
                soapMessage.append("<ExteriorValueId>" + arrayList.get(i).getExteriorValueId() + "</ExteriorValueId>");
                if (arrayList.get(i).isSelected())
                {
                    soapMessage.append("<Selected>" + "1" + "</Selected>");
                } else
                {
                    soapMessage.append("<Selected>" + "0" + "</Selected>");
                }

                soapMessage.append("</Item>");
            }

            soapMessage.append("</Items>");
            soapMessage.append("<RootInfo>");
            soapMessage.append("<AppraisalId>" + "1" + "</AppraisalId>");
            soapMessage.append("<ClientId>" + "1" + "</ClientId>");
            //   soapMessage.append("<ClientId>" + DataManager.getInstance().user.getDefaultClient().getId()+ "</ClientId>");
            soapMessage.append("<VinNumber>" +vehicleDetails.getVin()+ "</VinNumber>");
            soapMessage.append("<Comments>" + edtDetails.getText().toString().trim() + "</Comments>");
            soapMessage.append("</RootInfo>");
            soapMessage.append("</ExteriorReconditioning>");
            soapMessage.append("</ExteriorXML>");
            soapMessage.append("</SaveSelectedExterior>");
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
                        final String PassOrFailed = ParserManager.parsetokenChecker(response, "PassOrFailed");
                        String message = "";
                        if (PassOrFailed.equalsIgnoreCase("true"))
                        {
                            message = "Exterior Recondition save successfully";
                        } else
                        {
                            message = "Error while saving Exterior Recondition";
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
                    Constants.TEMP_URI_NAMESPACE + "IStockService/SaveSelectedExterior", listener);
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

    private void DeleteExteriorRecondition(final int position)
    {
        VollyCustomRequest request;
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");

            soapMessage.append("<DeleteSelectedExterior xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<ExteriorXML>");
            soapMessage.append("<ExteriorReconditioning>");
            soapMessage.append("<Items>");

            soapMessage.append("<Item>");
            soapMessage.append("<ExteriorValueId>" + arrayList.get(position).getExteriorValueId() + "</ExteriorValueId>");

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
            soapMessage.append("</DeleteSelectedExterior>");
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
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.error_while_deleting_Exteriorcondition_data), new DialogListener()
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
                            message = arrayList.get(position).getExteriorType()+" deleted successfully";
                        } else
                        {
                            message = "Error while deleting "+arrayList.get(position).getExteriorType();
                        }

                        arrayList.remove(position);
                        exterior_ReconditioningAdapter.notifyDataSetChanged();
                        setTotalValue();

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
                    Constants.TEMP_URI_NAMESPACE + "IStockService/DeleteSelectedExterior", listener);
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
