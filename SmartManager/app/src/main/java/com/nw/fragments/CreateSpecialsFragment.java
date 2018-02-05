package com.nw.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nw.adapters.NewVariantAdapter;
import com.nw.adapters.VariantAdapter;
import com.nw.broadcast.NetworkUtil;
import com.nw.database.SMDatabase;
import com.nw.interfaces.DateListener;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.ImageClickListener;
import com.nw.interfaces.SpecialEditListener;
import com.nw.model.BaseImage;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.SpecialVehicle;
import com.nw.model.Variant;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.DragableGridView;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class CreateSpecialsFragment extends BaseFragement implements OnClickListener
{

    EditText edSelectType, edSelectVehicle, edNormalPrice, edSpecialPrice, edTitle, edDescription, edDateStart, edDateEnd, edItem;
    TextView tvType, tvVehicleName, tvColor, tvMileage, tvDti, tvPrice, tvVehicleTitle;
    EditText edMake, edModel, edVariant, edYear;
    CheckBox cbIsCorrected;
    Button btnSave;
    int selectedType = 0, selectedMakeId = 0, selectedModelId = 0, selectedVariantId = 0, selectedItemId = 1;
    DragableGridView imageDragableGridView;
    ArrayList<SmartObject> typeList;
    ArrayList<SpecialVehicle> vehicleList;
    ArrayList<SmartObject> makeList;
    ArrayList<SmartObject> modelList;
    ArrayList<Variant> variantList;
    SpecialVehicle selectedVehicle, vehicle;
    LinearLayout llMakeModelVariant, llitems;
    ScrollView scvCreateSpecial;
    boolean isShowcaseShown = false;
    ArrayList<BaseImage> images;
    VariantAdapter variantAdapter;
    int noOfImagesInGrid = 0;
    int uploadImageCount = 0;
    int deleteImageCount = 0;
    int priorityImageCount = 0;
    SpecialEditListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_specials, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setHasOptionsMenu(true);
        initialise(view);
        typeList = new ArrayList<SmartObject>();
        vehicleList = new ArrayList<SpecialVehicle>();
        makeList = new ArrayList<SmartObject>();
        modelList = new ArrayList<SmartObject>();
        variantList = new ArrayList<Variant>();
        getSpecialTypeList();
        hideKeyboard(scvCreateSpecial);
        return view;
    }

    private void initialise(View view)
    {
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        edSelectType = (EditText) view.findViewById(R.id.edSelectType);
        edSelectVehicle = (EditText) view.findViewById(R.id.edSelectVehicle);
        tvVehicleTitle = (TextView) view.findViewById(R.id.tvVehicleTitle);
        edNormalPrice = (EditText) view.findViewById(R.id.edNormalPrice);
        edSpecialPrice = (EditText) view.findViewById(R.id.edSpecialPrice);
        edTitle = (EditText) view.findViewById(R.id.edTitle);
        edDescription = (EditText) view.findViewById(R.id.edDescription);
        edDateStart = (EditText) view.findViewById(R.id.edStartDate);
        edDateEnd = (EditText) view.findViewById(R.id.edEndDate);
        edItem = (EditText) view.findViewById(R.id.edItem);
        // llCreateSpecial = (LinearLayout)
        // view.findViewById(R.id.llCreateSpecial);
        llMakeModelVariant = (LinearLayout) view.findViewById(R.id.llMakeModelVariant);
        llitems = (LinearLayout) view.findViewById(R.id.llitems);
        edMake = (EditText) view.findViewById(R.id.edMake);
        edModel = (EditText) view.findViewById(R.id.edModel);
        edVariant = (EditText) view.findViewById(R.id.edVariant);
        edYear = (EditText) view.findViewById(R.id.edYear);
        edYear.setText("" + nowYear);
        scvCreateSpecial = (ScrollView) view.findViewById(R.id.scvCreateSpecial);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvVehicleName = (TextView) view.findViewById(R.id.tvVehicleName);
        // tvYear = (TextView) view.findViewById(R.id.tvYear);
        tvColor = (TextView) view.findViewById(R.id.tvColor);
        tvMileage = (TextView) view.findViewById(R.id.tvMileage);
        tvDti = (TextView) view.findViewById(R.id.tvDti);
        tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        btnSave = (Button) view.findViewById(R.id.bSave);
        cbIsCorrected = (CheckBox) view.findViewById(R.id.cbIsCorrected);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //this code will be executed on devices running Marshmellow or later
            imageDragableGridView = new DragableGridView();
        } else
        {
            imageDragableGridView = new DragableGridView(getActivity());
        }
        imageDragableGridView.init(view, new ImageClickListener()
        {
            @Override
            public void onImageClick(int position)
            {
                navigateToLargeImage(position);
            }

            @Override
            public void onImageDeleted(int position)
            {
            }
        });
        edSelectType.setOnClickListener(this);
        edSelectVehicle.setOnClickListener(this);
        edDateStart.setOnClickListener(this);
        edDateEnd.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        edMake.setOnClickListener(this);
        edModel.setOnClickListener(this);
        edVariant.setOnClickListener(this);
        edYear.setOnClickListener(this);
    }

    /*
     * Navigate to large screen fragment As Fragment to called belongs to other
     * activity we call activity and pass parameters through intent Parameter-
     * position of image clicked
     */
    private void navigateToLargeImage(int position)
    {
        try
        {
            Intent iToBuyActivity = new Intent(getActivity(), BuyActivity.class);
            iToBuyActivity.putParcelableArrayListExtra("imagelist", imageDragableGridView.getUpdatedImageListWithoutPlus());
            iToBuyActivity.putExtra("index", position);
            if (vehicle != null)
                iToBuyActivity.putExtra("vehicleName", vehicle.getFriendlyName());
            else
                iToBuyActivity.putExtra("vehicleName", getString(R.string.create_special1));
            startActivity(iToBuyActivity);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void putValues()
    {
        edNormalPrice.setText("R" + Helper.doubleToInt(vehicle.getNormalPrice() + ""));
        if (TextUtils.isEmpty(vehicle.getSpecialstart()))
        {
            edDateStart.setText("");
        } else
        {
            edDateStart.setText(Helper.convertUTCDateToNormal(vehicle.getSpecialstart()));
        }
        if ((TextUtils.isEmpty(vehicle.getSpecialEnd()) || vehicle.getSpecialEnd().equalsIgnoreCase("End?")))
        {
            edDateEnd.setText("");
        } else
        {
            edDateEnd.setText(Helper.convertUTCDateToNormal(vehicle.getSpecialEnd()));
        }
        edDescription.setText(Html.fromHtml(vehicle.getDetails().replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&")));
        edSpecialPrice.setText("R" + Helper.doubleToInt(vehicle.getSpecialPrice() + ""));
        edTitle.setText(vehicle.getSummary());
        tvColor.setText(vehicle.getColour());
        tvMileage.setText(Helper.getFormattedDistance(vehicle.getMileage() + "") + "Km");
        tvVehicleName.setText(Html.fromHtml("<font color=#ffffff>" + vehicle.getUsedYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
                + vehicle.getFriendlyName() + "</font>"));
        // tvYear.setText(vehicle.getUsedYear() + "");
        if (!vehicle.getFriendlyName().equals("No Vehicle Name Loaded"))
        {
            edSelectVehicle.setText(vehicle.getFriendlyName());
            selectedVehicle = getVehicleByFriendlyName(vehicle.getFriendlyName());
            if (selectedVehicle != null)
                selectedItemId = selectedVehicle.getItemID();
        } else
        {
            edSelectVehicle.setText("");
            selectedVehicle = getVehicleByFriendlyName("");
        }
        edSelectType.setText(getTypeNameByTypeId(vehicle.getSpecialTypeID()));
        selectedType = vehicle.getSpecialTypeID();
        tvType.setText(getTypeNameByTypeId(vehicle.getSpecialTypeID()));
        tvPrice.setText(Helper.formatPrice(vehicle.getNormalPrice() + ""));
        edYear.setText(vehicle.getUsedYear() + "");
        edMake.setText(vehicle.getMakeName());
        edModel.setText(vehicle.getModelName());
        edVariant.setText(vehicle.getVariantName());
        selectedMakeId = vehicle.getMakeId();
        selectedModelId = vehicle.getModelID();
        selectedVariantId = vehicle.getVariantID();
        tvDti.setText(vehicle.getStockCode() + "");
        if (vehicle.getSpecialTypeID() == 1 || vehicle.getSpecialTypeID() == 3)
        {
            stockMode();
            if (vehicle.getSpecialTypeID() == 1)
                getVehicleList("Used");
            else if (vehicle.getSpecialTypeID() == 3)
                getVehicleList("New");
        } else
            genericMode();
        images = new ArrayList<BaseImage>();
        getSpecialImagesBySpecialId(vehicle.getSpecialID());
        // llCreateSpecial.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.edStartDate:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEndDate", true);
                DatePickerFragment startDate = new DatePickerFragment();
                startDate.setDateListener(new DateListener()
                {
                    @Override
                    public void onDateSet(int year, int monthOfYear, int dayOfMonth)
                    {
                        edDateStart.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
                    }
                });
                startDate.show(getActivity().getFragmentManager(), "datePicker");
                break;

            case R.id.edEndDate:
                if (!TextUtils.isEmpty(edDateStart.getText().toString()))
                {
                    bundle = new Bundle();
                    bundle.putBoolean("isEndDate", true);
                    DatePickerFragment endDate = new DatePickerFragment();
                    endDate.setDateListener(new DateListener()
                    {
                        @Override
                        public void onDateSet(int year, int monthOfYear, int dayOfMonth)
                        {
                            edDateEnd.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
                        }
                    });
                    endDate.show(getActivity().getFragmentManager(), "datePicker");
                } else
                {
                    Helper.showToast(getString(R.string.please_select_start_date), getActivity());
                }
                break;

            case R.id.bSave:
                saveSpecial();
                break;

            case R.id.edSelectType:
                if (typeList.isEmpty())
                {
                    CustomDialogManager.showOkDialog(getActivity(), "No record(s) found.");
                    return;
                } else
                {
                    Helper.showDropDown(v, new ArrayAdapter<SmartObject>(getActivity(), R.layout.list_item_text2, R.id.tvText, typeList), new OnItemClickListener()
                    {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            edSelectType.setText(typeList.get(position) + "");
                            edSelectVehicle.setText("");
                            tvType.setText(typeList.get(position).toString());
                            selectedType = position;
                            vehicleList.clear();
                            if (position == 2)
                            {
                                stockMode();
                                getVehicleList("New");
                            } else if (position == 0)
                            {
                                stockMode();
                                getVehicleList("Used");
                            } else if (position == 1 || position == 3)
                            {
                                genericMode();
                            } else
                            {
                                serviceSparesAccessories();
                            }
                        }
                    });
                }
                break;

            case R.id.edSelectVehicle:
                if (vehicleList.size() != 0)
                {
                    if (!TextUtils.isEmpty(edSelectType.getText().toString().trim()))
                    {
                        showSearch(getActivity());
                    } else
                    {
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.please_select_type));
                    }
                } else
                {

                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found));
                }
                break;

            case R.id.edMake:
                if (makeList.isEmpty())
                {
                    CustomDialogManager.showOkDialog(getActivity(), "No record(s) found.");
                    return;
                } else
                {
                    Helper.showDropDown(v, new ArrayAdapter<SmartObject>(getActivity(), R.layout.list_item_text2, R.id.tvText, makeList), new OnItemClickListener()
                    {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            edMake.setText(makeList.get(position) + "");
                            edModel.setText("");
                            edVariant.setText("");
                            modelList.clear();
                            selectedMakeId = makeList.get(position).getId();
                            getModelList(position);
                        }
                    });
                }
                break;

            case R.id.edModel:
                if (!TextUtils.isEmpty(edMake.getText().toString().trim()))
                {
                    Helper.showDropDown(v, new ArrayAdapter<SmartObject>(getActivity(), R.layout.list_item_text2, R.id.tvText, modelList), new OnItemClickListener()
                    {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            edModel.setText(modelList.get(position) + "");
                            edVariant.setText("");
                            variantList.clear();
                            selectedModelId = modelList.get(position).getId();
                            getVariantList(position);
                        }
                    });
                } else
                {
                    Helper.showToast(getString(R.string.select_make1), getActivity());
                }
                break;

            case R.id.edVariant:
                if (!TextUtils.isEmpty(edModel.getText().toString().trim()))
                {
                    NewVariantAdapter variantAdapter = new NewVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
                    Helper.showDropDown(v, variantAdapter, new OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            edVariant.setText(variantList.get(position).getVariantName() + "");
                            selectedVariantId = variantList.get(position).getVariantId();
                        }
                    });
                } else
                {
                    Helper.showToast(getString(R.string.select_model1), getActivity());
                }
                break;

            case R.id.edYear:
                int defaultYear = 1990;
                Calendar cal = Calendar.getInstance();
                int nowYear = cal.get(Calendar.YEAR);
                cal.set(Calendar.YEAR, defaultYear);

                int subtraction = nowYear - defaultYear;
                final List<String> years = new ArrayList<String>();
                int i = 0;
                cal.set(Calendar.YEAR, defaultYear);
                while (i <= subtraction)
                {
                    years.add(cal.get(Calendar.YEAR) + i + "");
                    i++;
                }
                Collections.reverse(years);
                Helper.showDropDown(v, new ArrayAdapter<String>(getActivity(), R.layout.list_item_text2, R.id.tvText, years), new OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        edYear.setText(years.get(position) + "");
                        edMake.setText("");
                        edModel.setText("");
                        edVariant.setText("");

                        getMakeList();
                    }
                });
                break;
        }
    }

    public void showSearch(Context context)
    {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_search);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        ListView lstData = (ListView) dialog.findViewById(R.id.listView);
        lstData.setEmptyView(dialog.findViewById(R.id.emptyView));
        final EditText edtClientName = (EditText) dialog.findViewById(R.id.edtClientName);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        variantAdapter = new VariantAdapter(getActivity(), R.layout.list_item_variant_details, vehicleList);
        lstData.setAdapter(variantAdapter);
        edtClientName.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                variantAdapter.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
            }

            @Override
            public void afterTextChanged(Editable arg0)
            {
            }
        });
        lstData.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                dialog.dismiss();
                Helper.hidekeybord(edtClientName);
                edSelectVehicle.setText(vehicleList.get(position) + "");
                selectedVehicle = vehicleList.get(position);
                selectedItemId = vehicleList.get(position).getItemID();
                if (Integer.parseInt(vehicleList.get(position).getRetailPrice()) != 0)
                {
                    edNormalPrice.setText(Helper.doubleToInt(vehicleList.get(position).getRetailPrice()));
                }
                getVehicleDetailsSoap(selectedItemId);
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }

    private void genericMode()
    {
        edItem.setText("");
        edSelectVehicle.setVisibility(View.GONE);
        tvVehicleTitle.setVisibility(View.GONE);
        llMakeModelVariant.setVisibility(View.VISIBLE);
        // llCreateSpecial.setVisibility(View.GONE);
        edMake.setText("");
        edModel.setText("");
        edVariant.setText("");
        getMakeList();
    }

    private void stockMode()
    {
        edItem.setText("");
        edSelectVehicle.setVisibility(View.VISIBLE);
        tvVehicleTitle.setVisibility(View.VISIBLE);
        llMakeModelVariant.setVisibility(View.GONE);
        llitems.setVisibility(View.GONE);
    }

    private void serviceSparesAccessories()
    {
        edItem.setText("");
        edSelectVehicle.setVisibility(View.GONE);
        tvVehicleTitle.setVisibility(View.GONE);
        llMakeModelVariant.setVisibility(View.GONE);
        llitems.setVisibility(View.VISIBLE);
    }

    /*
     * Function to create special and edit special
     */
    private void saveSpecial()
    {
        if (TextUtils.isEmpty(edSelectType.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_select_type), getActivity());
            return;
        }
        if (llMakeModelVariant.getVisibility() == View.GONE)
        {
            if (TextUtils.isEmpty(edSelectVehicle.getText().toString().trim()))
            {
                Helper.showToast(getString(R.string.please_select_vehicle), getActivity());
                return;
            }
        }
        if (llMakeModelVariant.getVisibility() == View.VISIBLE)
        {
            if (TextUtils.isEmpty(edMake.getText().toString().trim()))
            {
                Helper.showToast(getString(R.string.please_select_make), getActivity());
                return;
            }
            if (TextUtils.isEmpty(edModel.getText().toString().trim()))
            {
                Helper.showToast(getString(R.string.please_select_model), getActivity());
                return;
            }
            if (TextUtils.isEmpty(edVariant.getText().toString().trim()))
            {
                Helper.showToast(getString(R.string.please_select_variant), getActivity());
                return;
            }
        }
        if (TextUtils.isEmpty(edSelectType.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_select_type), getActivity());
            return;
        } else if (TextUtils.isEmpty(edNormalPrice.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_enter_normal_price), getActivity());
            return;
        } else if (TextUtils.isEmpty(edSpecialPrice.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_enter_special_price), getActivity());
            return;
        } else if (TextUtils.isEmpty(edTitle.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_enter_title), getActivity());
            return;
        } else if (TextUtils.isEmpty(edDescription.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_enter_description), getActivity());
            return;
        } else if (TextUtils.isEmpty(edDateStart.getText().toString().trim()))
        {
            Helper.showToast(getString(R.string.please_select_start_date), getActivity());
            return;
        } else
        {
            if (HelperHttp.isNetworkAvailable(getActivity()))
            {
                showProgressDialog();
                final StringBuilder soapBuilder = new StringBuilder();
                soapBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                soapBuilder.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
                soapBuilder.append("<Body><SaveSpecial xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
                soapBuilder.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
                soapBuilder.append("<coreClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</coreClientID>");
                soapBuilder.append("<specialTypeId>" + getTypeIdByTypeName(edSelectType.getText().toString()) + "</specialTypeId>");
                soapBuilder.append("<dateSpecialStart>" + edDateStart.getText().toString() + "</dateSpecialStart>");
                if (edDateEnd.getText().toString().equals("1 Jan 1900"))
                    soapBuilder.append("<endSpecialEnd></endSpecialEnd>");
                else
                    soapBuilder.append("<endSpecialEnd>" + edDateEnd.getText().toString() + "</endSpecialEnd>");

                soapBuilder.append("<itemID>" + selectedItemId + "</itemID>");
                soapBuilder.append("<ItemValue>" + edItem.getText().toString().trim() + "</ItemValue>");
                if (llMakeModelVariant.getVisibility() == View.GONE)
                {
                    if (selectedVehicle != null)
                    {
                        soapBuilder.append("<variantID>" + selectedVehicle.getVariantID() + "</variantID>");
                        soapBuilder.append("<modelID>" + selectedVehicle.getModelID() + "</modelID>");
                        soapBuilder.append("<makeID>" + selectedVehicle.getMakeId() + "</makeID>");
                    } else
                    {
                        soapBuilder.append("<variantID>" + 0 + "</variantID>");
                        soapBuilder.append("<modelID>" + 0 + "</modelID>");
                        soapBuilder.append("<makeID>" + 0 + "</makeID>");
                    }
                } else
                {
                    soapBuilder.append("<variantID>" + selectedVariantId + "</variantID>");
                    soapBuilder.append("<modelID>" + selectedModelId + "</modelID>");
                    soapBuilder.append("<makeID>" + selectedMakeId + "</makeID>");
                }

                if (getArguments() == null)
                {
                    soapBuilder.append("<specialPrice>" + Double.parseDouble(edSpecialPrice.getText().toString()) + "</specialPrice>");
                    soapBuilder.append("<normalPrice>" + Double.parseDouble(edNormalPrice.getText().toString()) + "</normalPrice>");
                } else
                {
                    soapBuilder.append("<specialPrice>" + Double.parseDouble(Helper.formatPriceToDefault(edSpecialPrice.getText().toString())) + "</specialPrice>");
                    soapBuilder.append("<normalPrice>" + Double.parseDouble(Helper.formatPriceToDefault(edNormalPrice.getText().toString())) + "</normalPrice>");
                }
                soapBuilder.append("<details>" + Helper.getCDATAString(edDescription.getText().toString()) + "</details>");
                soapBuilder.append("<title>" + Helper.getCDATAString(edTitle.getText().toString()) + "</title>");
                soapBuilder.append("<allowGroup>" + false + "</allowGroup>");
                soapBuilder.append("<correction>" + cbIsCorrected.isChecked() + "</correction>");

                if (getArguments() != null)
                    soapBuilder.append("<specialId>" + vehicle.getSpecialID() + "</specialId>");
                else
                    soapBuilder.append("<specialId>" + 0 + "</specialId>");
                soapBuilder.append("</SaveSpecial></Body></Envelope>");
                Helper.Log("request:", soapBuilder.toString());

                VollyResponseListener vollyResponseListener = new VollyResponseListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        hideProgressDialog();
                        try
                        {
                            Helper.Log("Error: ", error.getMessage());

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        if (!TextUtils.isEmpty(response))
                        {
                            Helper.Log("soap Response", response.toString());
                            int specialId = ParserManager.parseSaveSpecialResponse(response.toString());

                            if (imageDragableGridView.getUpdatedImageListWithoutPlus().isEmpty())
                            {
                                if (imageDragableGridView.getDeletedImages().isEmpty())
                                    showSuccessDialog();
                                else
                                {
                                    noOfImagesInGrid = imageDragableGridView.getUpdatedImageListWithoutPlus().size();
                                    checkDelete();
                                }
                            } else
                            {
                                noOfImagesInGrid = imageDragableGridView.getUpdatedImageListWithoutPlus().size();
                                uploadSpecialImage(specialId);
                            }
                        }
                    }
                };
                VollyCustomRequest request = new VollyCustomRequest(Constants.SPECIAL_WEBSERVICE_URL, soapBuilder.toString(), Constants.TEMP_URI_NAMESPACE + "ISpecialsService/SaveSpecial",
                        vollyResponseListener);
                try
                {
                    request.init();
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                }

            } else
                HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    /*
     * Function to get all special type list
     */
    private void getSpecialTypeList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {

            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadSpecialTypesXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "ISpecialsService/LoadSpecialTypesXML");
            inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            showProgressDialog();
            // Network call
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("soap Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("AUTOSpecialTypes");
                        // SoapObject inner1 = (SoapObject)
                        // inner.getPropertySafely("NewDataSet");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject makeObj = (SoapObject) inner.getProperty(i);
                            String makeid = makeObj.getPropertySafelyAsString("AUTOSpecialTypeID", "0");
                            String makename = makeObj.getPropertySafelyAsString("Name", "-");
                            typeList.add(i, new SmartObject(Integer.parseInt(makeid), makename));
                        }

                        if (getArguments() != null)
                        {
                            vehicle = getArguments().getParcelable("vehicle");
                            getSpecialBySpecialId(vehicle.getSpecialID());
                        } else
                        {
                            hideProgressDialog();
                            if (!isShowcaseShown)
                            {
                                // displayShowCaseView();
                                isShowcaseShown = true;
                            }
                        }
                    } catch (Exception e)
                    {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    /*
     * Function to get all used vehicles in stock
     */
    private void getVehicleList(String listType)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {

            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("vehicleType", listType, String.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListVehicleByClientXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "ISpecialsService/ListVehicleByClientXML");
            inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {

                    vehicleList.clear();
                    try
                    {
                        SpecialVehicle vehicleUsed = null;
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Vehicles");
                        // SoapObject inner1 = (SoapObject)
                        // inner.getPropertySafely("DocumentElement");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            vehicleUsed = new SpecialVehicle();
                            SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
                            vehicleUsed.setItemID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedVehicleStockID", "0")));
                            vehicleUsed.setStockCode(vehicleObj.getPropertySafelyAsString("Stockcode", ""));
                            vehicleUsed.setFriendlyName(vehicleObj.getPropertySafelyAsString("FriendlyName", ""));
                            vehicleUsed.setUsedYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("UsedYear", "0")));
                            vehicleUsed.setColour(vehicleObj.getPropertySafelyAsString("Colour", ""));
                            vehicleUsed.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("Mileage", "0")));
                            vehicleUsed.setMakeId(Integer.parseInt(vehicleObj.getPropertySafelyAsString("MakeID", "0")));
                            vehicleUsed.setModelID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("modelID", "0")));
                            vehicleUsed.setVariantID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("VariantID", "0")));
                            vehicleUsed.setRetailPrice(vehicleObj.getPropertySafelyAsString("RetailPrice", "0"));

                            vehicleList.add(vehicleUsed);
                        }
                        if (vehicle != null)
                            selectedVehicle = getVehicleByFriendlyName(vehicle.getFriendlyName());
                        selectedItemId = selectedVehicle.getItemID();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    /*
     * Function to get all special images from special id Parameter- special id
     */
    private void getSpecialImagesBySpecialId(int specialId)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("specialID", specialId, Integer.class));
            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetSpecialImagesBySpecialID");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/GetSpecialImagesBySpecialID");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("diffgram");
                        SoapObject inner1 = (SoapObject) inner.getPropertySafely("DocumentElement");
                        BaseImage image;
                        for (int i = 0; i < inner1.getPropertyCount(); i++)
                        {
                            SoapObject imgObj = (SoapObject) inner1.getProperty(i);
                            image = new BaseImage(Constants.IMAGE_BASE_URL_NEW + Integer.parseInt(imgObj.getPropertySafelyAsString("ImageID", "")), Integer.parseInt(imgObj.getPropertySafelyAsString(
                                    "SpecialImageID", "")));
                            image.setPriority(Integer.parseInt(imgObj.getPropertySafelyAsString("LinkImagePriority", "")));
                            images.add(image);
                        }

                        imageDragableGridView.setImageList(images);
                        hideProgressDialog();
                        if (!isShowcaseShown)
                        {
                            // displayShowCaseView();
                            isShowcaseShown = true;
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                }
            }).execute();

        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    /*
     * Function to upload special image
     */
    private void uploadSpecialImage(final int specialId)
    {
        // Check is on wifi or mobile network
        if (NetworkUtil.getConnectivityStatusString(getActivity()) == ConnectivityManager.TYPE_WIFI)
        {
            sendImagesToServerOrDataBase(specialId, false);
        } else
        {
            boolean showImageAleart = false;

            if (imageDragableGridView.getLocalImageListWithoutPlus().size() > 0)
            {
                showImageAleart = true;
            }

            if (showImageAleart)
            {
                CustomDialogManager.showOkCancelDialog(getActivity(), getString(R.string.network_title), getString(R.string.Upload_Now), getString(R.string.upload_with_wifi), new DialogListener()
                {
                    @Override
                    public void onButtonClicked(int type)
                    {
                        switch (type)
                        {
                            // Upload Now
                            case Dialog.BUTTON_POSITIVE:
                                sendImagesToServerOrDataBase(specialId, false);
                                break;
                            // Upload With WIFI
                            case Dialog.BUTTON_NEGATIVE:
                                sendImagesToServerOrDataBase(specialId, true);
                                break;
                        }
                    }
                });
            }else
            {
                sendImagesToServerOrDataBase(specialId, false);
            }

        }
    }

    /**
     * This Method is used to send selected images to server.
     *
     * @param specialId      = specialId
     * @param isSaveDataBase = if true (Save to local database) if false (send directly to server on any network)
     */
    private void sendImagesToServerOrDataBase(final int specialId, final boolean isSaveDataBase)
    {
        if (!imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).isLocal())
        {
            uploadImageCount++;
            if (noOfImagesInGrid == uploadImageCount)
                checkImagePriority();
            else
                sendImagesToServerOrDataBase(specialId, isSaveDataBase);
        } else
        {
            Helper.Log("path", imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath());

            String base64String = Helper.convertBitmapToBase64(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath());

            if (TextUtils.isEmpty(base64String))
            {
                uploadImageCount++;
                if (noOfImagesInGrid == uploadImageCount)
                    checkImagePriority();
                else
                    sendImagesToServerOrDataBase(specialId, isSaveDataBase);
            }

            if (HelperHttp.isNetworkAvailable(getActivity()))
            {
                if (isSaveDataBase)
                {
                    StringBuilder soapMessage = new StringBuilder();
                    soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                    soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
                    soapMessage.append("<Body>");
                    soapMessage.append("<SaveSpecialsImage xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
                    soapMessage.append("<userHash>" + Constants.CHANGE_THIS_USER_HASH + "</userHash>");
                    soapMessage.append("<autoSpecialId>" + specialId + "</autoSpecialId>");
                    soapMessage.append("<coreClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</coreClientID>");
                    soapMessage.append("<originalFileName>" + Helper.getFileName(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath()) + "</originalFileName>");
                    soapMessage.append("<base64ImageString>" + base64String + "</base64ImageString>");
                    soapMessage.append("</SaveSpecialsImage>");
                    soapMessage.append("</Body>");
                    soapMessage.append("</Envelope>");

                    // Save in local Database
                    SMDatabase myDatabase = new SMDatabase(getActivity());
                    myDatabase.insertRecords(soapMessage.toString(), Constants.SaveSpecialImage);

                    try
                    {
                        uploadImageCount++;
                        if (uploadImageCount == noOfImagesInGrid)
                        {
                            checkImagePriority();
                        } else
                        {
                            sendImagesToServerOrDataBase(specialId, isSaveDataBase);
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                } else
                {
                    ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
                    parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
                    parameterList.add(new Parameter("autoSpecialId", specialId, Integer.class));
                    parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
                    // parameterList.add(new
                    // Parameter("linkImagePriority",(uploadImageCount+1),
                    // Integer.class));
                    parameterList.add(new Parameter("originalFileName", Helper.getFileName(imageDragableGridView.getUpdatedImageListWithoutPlus().get(uploadImageCount).getPath()), String.class));
                    parameterList.add(new Parameter("base64ImageString", base64String, String.class));
                    // parameterList.add(new Parameter("cmUserID", 0,
                    // Integer.class));

                    // create web service inputs
                    DataInObject inObj = new DataInObject();
                    inObj.setMethodname("SaveSpecialsImage");
                    inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                    inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "ISpecialsService/SaveSpecialsImage");
                    inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
                    inObj.setParameterList(parameterList);

                    // Network call
                    new WebServiceTask(getActivity(), inObj, false, new TaskListener()
                    {

                        @Override
                        public void onTaskComplete(Object result)
                        {
                            try
                            {
                                Helper.Log("response", result.toString());
                                uploadImageCount++;
                                if (Boolean.parseBoolean(result.toString()))
                                {
                                    if (uploadImageCount == noOfImagesInGrid)
                                    {
                                        checkImagePriority();
                                    } else
                                    {
                                        sendImagesToServerOrDataBase(specialId, isSaveDataBase);
                                    }
                                } else
                                {
                                    hideProgressDialog();
                                }
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                                hideProgressDialog();
                            }
                        }
                    }).execute();
                }
            } else
            {
                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
            }
        }
    }

    /*
     * Function to check whether image priority has changed or not
     */
    private void checkImagePriority()
    {
        if (imageDragableGridView.isPriorityChanged())
        {
            changeSpecialImagePriority();
        } else
        {
            checkDelete();
        }
    }

    /**
     * Function to change image priority by calling WS
     * UpdateSpecialImagePriority
     */
    private void changeSpecialImagePriority()
    {

        if (noOfImagesInGrid == priorityImageCount)
        {
            checkDelete();
        } else
        {
            if (HelperHttp.isNetworkAvailable(getActivity()))
            {
                ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
                parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
                parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
                parameterList.add(new Parameter("specialImageID", imageDragableGridView.getUpdatedImageListWithoutPlus().get(priorityImageCount).getId(), Integer.class));
                parameterList.add(new Parameter("linkImagePriority", priorityImageCount + 1, Integer.class));

                // create web service inputs
                DataInObject inObj = new DataInObject();
                inObj.setMethodname("UpdateSpecialImagePriority");
                inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/UpdateSpecialImagePriority");
                inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
                inObj.setParameterList(parameterList);

                // Network call
                new WebServiceTask(getActivity(), inObj, false, new TaskListener()
                {

                    @Override
                    public void onTaskComplete(Object result)
                    {
                        try
                        {
                            Helper.Log("response", result.toString());
                            priorityImageCount++;
                            if (priorityImageCount == noOfImagesInGrid)
                            {
                                checkDelete();
                            } else
                            {
                                changeSpecialImagePriority();
                            }

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            hideProgressDialog();
                        }
                    }
                }).execute();
            } else
            {
                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
            }
        }
    }

    /*
     * Function to check whether images are deleted or not
     */
    private void checkDelete()
    {
        if (imageDragableGridView.getDeletedImages().isEmpty())
            showSuccessDialog();
        else
            deleteSpecialImage();
    }

    /*
     * Function to delete Special image
     */
    private void deleteSpecialImage()
    {

        if (deleteImageCount == imageDragableGridView.getDeletedImages().size())
        {
            showSuccessDialog();
        } else
        {

            if (HelperHttp.isNetworkAvailable(getActivity()))
            {
                ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
                parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
                parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
                parameterList.add(new Parameter("specialImageID", imageDragableGridView.getDeletedImages().get(deleteImageCount).getId(), Integer.class));
                parameterList.add(new Parameter("cmUserID", 0, Integer.class));
                // create web service inputs
                DataInObject inObj = new DataInObject();
                inObj.setMethodname("DeleteSpecialImage");
                inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/DeleteSpecialImage");
                inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
                inObj.setParameterList(parameterList);

                // Network call
                new WebServiceTask(getActivity(), inObj, false, new TaskListener()
                {

                    @Override
                    public void onTaskComplete(Object result)
                    {
                        try
                        {
                            Helper.Log("response", result.toString());
                            deleteImageCount++;
                            if (deleteImageCount == imageDragableGridView.getDeletedImages().size())
                            {
                                showSuccessDialog();
                            } else
                            {
                                deleteSpecialImage();
                            }

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            hideProgressDialog();
                        }

                    }
                }).execute();
            } else
            {
                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
            }
        }
    }

    /*
     * Function to show success dialog on special created and special editted
     */
    private void showSuccessDialog()
    {
        hideProgressDialog();
        String msg = "";
        if (getArguments() == null)
            msg = getString(R.string.special_created_successfully);
        else
            msg = getString(R.string.special_updated_successfully);
        CustomDialogManager.showOkDialog(getActivity(), msg, new DialogListener()
        {

            @Override
            public void onButtonClicked(int type)
            {
                if (type == Dialog.BUTTON_POSITIVE)
                {
                    getActivity().getFragmentManager().popBackStack();
                    getActivity().getFragmentManager().popBackStack();
                }
            }
        });
    }

    // Function fetches Make list and adds to Arraylist - makeList
    private void getMakeList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", edYear.getText().toString(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetVehicleMakesByYearXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "ISpecialsService/GetVehicleMakesByYearXML");
            inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {

                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    makeList = new ArrayList<SmartObject>();
                    // makeList.add(new SmartObject(0,
                    // getString(R.string.selectall)));
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        // SoapObject inner = (SoapObject)
                        // outer.getPropertySafely("diffgram");
                        SoapObject inner1 = (SoapObject) outer.getPropertySafely("Makes");
                        for (int i = 0; i < inner1.getPropertyCount(); i++)
                        {
                            SoapObject makeObj = (SoapObject) inner1.getProperty(i);
                            String makeid = makeObj.getPropertySafelyAsString("MakeID", "0");
                            String makename = makeObj.getPropertySafelyAsString("MakeName", "-");
                            makeList.add(new SmartObject(Integer.parseInt(makeid), makename));
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();

                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    // Function fetches Model list and adds to Arraylist - modelList
    // Function gets position as parameter used to get which make is selected
    private void getModelList(int position)
    {

        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("id", makeList.get(position).getId(), Integer.class));
            parameterList.add(new Parameter("year", edYear.getText().toString(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetVehicleModelsByMakeIdXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "ISpecialsService/GetVehicleModelsByMakeIdXML");
            inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    modelList = new ArrayList<SmartObject>();
                    modelList.add(new SmartObject(0, getString(R.string.selectall)));
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        // SoapObject inner = (SoapObject)
                        // outer.getPropertySafely("diffgram");
                        SoapObject inner1 = (SoapObject) outer.getPropertySafely("Models");
                        for (int i = 0; i < inner1.getPropertyCount(); i++)
                        {
                            SoapObject makeObj = (SoapObject) inner1.getProperty(i);
                            String modelid = makeObj.getPropertySafelyAsString("ModelID", "0");
                            String modelname = makeObj.getPropertySafelyAsString("ModelName", "-");
                            modelList.add(i + 1, new SmartObject(Integer.parseInt(modelid), modelname));
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    // Function fetches variant list and adds to arraylist- variantList
    // Function gets position as parameter used for getting which model is
    // selected
    private void getVariantList(int position)
    {

        if (HelperHttp.isNetworkAvailable(getActivity()))
        {

            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("id", modelList.get(position).getId(), Integer.class));
            parameterList.add(new Parameter("year", edYear.getText().toString(), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetVehicleVariantByModelIdXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "ISpecialsService/GetVehicleVariantByModelIdXML");
            inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    variantList = new ArrayList<Variant>();
                    // variantList.add(new Variant(0, "",
                    // getString(R.string.selectall), ""));
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        // SoapObject inner = (SoapObject)
                        // outer.getPropertySafely("diffgram");
                        SoapObject inner1 = (SoapObject) outer.getPropertySafely("Models");
                        for (int i = 0; i < inner1.getPropertyCount(); i++)
                        {
                            SoapObject variantObj = (SoapObject) inner1.getProperty(i);
                            variantList.add(new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("VariantID", "0")), variantObj.getPropertySafelyAsString("MMCode", "0"), variantObj
                                    .getPropertySafelyAsString("FriendlyName", "-"), variantObj.getPropertySafelyAsString("VariantName", "-"), variantObj.getPropertySafelyAsString("StartYear", "-"),
                                    variantObj.getPropertySafelyAsString("EndYear", "-")));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    /*
     * Function to get Special from special Id Parameter - special id
     */
    private void getSpecialBySpecialId(int specialID)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {

            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("coreClientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("autoSpecialId", specialID, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("GetSpecialXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "ISpecialsService/GetSpecialXML");
            inObj.setUrl(Constants.SPECIAL_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {

                    try
                    {
                        Helper.Log("soap Response", result.toString());
                        SoapObject outer = (SoapObject) result;
                        SoapObject specialObj = (SoapObject) outer.getPropertySafely("AUTOSpecials");
                        vehicle.setSpecialID(Integer.parseInt(specialObj.getPropertySafelyAsString("AUTOSpecialID", "0")));
                        vehicle.setSpecialTypeID(Integer.parseInt(specialObj.getPropertySafelyAsString("AUTOSpecialTypeID", "0")));
                        vehicle.setNormalPrice(Float.parseFloat(specialObj.getPropertySafelyAsString("NormalPrice", "0.0")));
                        vehicle.setSpecialPrice(Float.parseFloat(specialObj.getPropertySafelyAsString("Price", "0.0")));
                        vehicle.setSpecialstart(specialObj.getPropertySafelyAsString("Specialstart", ""));
                        vehicle.setSpecialEnd(specialObj.getPropertySafelyAsString("SpecialEnd", "End?"));
                        vehicle.setSummary(specialObj.getPropertySafelyAsString("Title", ""));
                        vehicle.setDetails(specialObj.getPropertySafelyAsString("Details", ""));
                        vehicle.setUsedYear(Integer.parseInt(specialObj.getPropertySafelyAsString("UsedYear", "0")));
                        vehicle.setFriendlyName(specialObj.getPropertySafelyAsString("friendlyName", ""));
                        vehicle.setItemID(Integer.parseInt(specialObj.getPropertySafelyAsString("UsedVehicleStockID", "0")));
                        vehicle.setVariantID(Integer.parseInt(specialObj.getPropertySafelyAsString("AUTOVariantID", "0")));
                        vehicle.setMakeId(Integer.parseInt(specialObj.getPropertySafelyAsString("AUTOMakeID", "0")));
                        vehicle.setModelID(Integer.parseInt(specialObj.getPropertySafelyAsString("AUTOModelID", "0")));
                        vehicle.setCorrection(Boolean.parseBoolean(specialObj.getPropertySafelyAsString("Correction", "false")));
                        vehicle.setCanPublish(Boolean.parseBoolean(specialObj.getPropertySafelyAsString("CanPublish", "false")));
                        selectedItemId = vehicle.getItemID();
                        putValues();

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getVehicleDetailsSoap(int vehicleID)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("usedVehicleStockID", vehicleID, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadVehicleDetailsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadVehicleDetailsXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            new WebServiceTask(getActivity(), inObj, true, new TaskListener()
            {
                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    try
                    {
                        Helper.Log("response", result.toString());
                        @SuppressWarnings("unchecked")
                        Vector<SoapObject> vectorResult = (Vector<SoapObject>) result;

                        SoapObject outer = vectorResult.get(0);
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Details");
                        selectedVehicle.setStockCode(inner.getPropertySafelyAsString("stockCode", ""));
                        selectedVehicle.setFriendlyName(inner.getPropertySafelyAsString("friendlyName", ""));
                        selectedVehicle.setUsedYear(Integer.parseInt(inner.getPropertySafelyAsString("year", "0")));
                        selectedVehicle.setNormalPrice(Float.parseFloat(inner.getPropertySafelyAsString("price", "0.0")));
                        selectedVehicle.setColour(inner.getPropertySafelyAsString("colour", ""));
                        selectedVehicle.setMileage(Integer.parseInt(inner.getPropertySafelyAsString("mileage", "0")));

                        tvType.setText(typeList.get(selectedType).toString());
                        tvVehicleName.setText(Html.fromHtml("<font color=#ffffff>" + selectedVehicle.getUsedYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue)
                                + ">" + selectedVehicle.getFriendlyName() + "</font>"));
                        tvDti.setText(selectedVehicle.getStockCode() + "");
                        // tvYear.setText(selectedVehicle.getUsedYear()+ "");
                        tvColor.setText(selectedVehicle.getColour());
                        tvMileage.setText(Helper.getFormattedDistance(selectedVehicle.getMileage() + "") + "Km");
                        tvPrice.setText(Helper.formatPrice(selectedVehicle.getNormalPrice() + ""));

						/*
                         * if (llCreateSpecial.getVisibility() == View.GONE)
						 * llCreateSpecial.setVisibility(View.VISIBLE);
						 */
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    /*
     * Funtion to get type id from type name Parameter- type name
     */
    private int getTypeIdByTypeName(String name)
    {
        int id = 0;
        for (int i = 0; i < typeList.size(); i++)
        {
            if (typeList.get(i).getName().equals(name))
            {
                id = typeList.get(i).getId();
                break;
            }
        }
        return id;
    }

    /*
     * Function to get special type name from type id Parameter - typeId
     */
    private String getTypeNameByTypeId(int id)
    {
        String name = "";
        for (int i = 0; i < typeList.size(); i++)
        {
            if (typeList.get(i).getId() == id)
            {
                name = typeList.get(i).getName();
                break;
            }
        }
        return name;
    }

    /*
     * Function to get SpecialVehicle obj from friendlyName Parameter -
     * friendlyName
     */
    private SpecialVehicle getVehicleByFriendlyName(String friendlyName)
    {
        SpecialVehicle mVehicle = null;
        for (int i = 0; i < vehicleList.size(); i++)
        {
            if (vehicleList.get(i).getFriendlyName().equals(friendlyName))
            {
                mVehicle = vehicleList.get(i);
                break;
            }
        }
        return mVehicle;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageDragableGridView != null)
        {
            if (imageDragableGridView.isOptionSelected())
                imageDragableGridView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getArguments() != null)
        {
            showActionBar("Edit Special");
        } else
        {
            showActionBar("Create Special");
        }
    }

	/*
     * private void displayShowCaseView() {
	 * 
	 * String msg=""; if(getArguments()==null) msg=
	 * getString(R.string.create_special_by_selecting); else msg=
	 * getString(R.string.update_special_by_selecting); if
	 * (!ShowcaseSessions.isSessionAvailable
	 * (getActivity(),CreateSpecialsFragment.class.getSimpleName())) {
	 * ArrayList<TargetView> viewList = new ArrayList<TargetView>();
	 * viewList.add(new
	 * TargetView(scvCreateSpecial,ShowCaseType.SwipeUpDown,msg));
	 * ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
	 * showcaseView.setShowcaseView(viewList);
	 * 
	 * ((ViewGroup)
	 * getActivity().getWindow().getDecorView()).addView(showcaseView);
	 * ShowcaseSessions
	 * .saveSession(getActivity(),CreateSpecialsFragment.class.getSimpleName());
	 * } }
	 */

    public void setSpecialEditListener(SpecialEditListener listner)
    {
        mListener = listner;
    }

}
