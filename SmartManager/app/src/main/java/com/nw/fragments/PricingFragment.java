
package com.nw.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nw.interfaces.DialogListener;
import com.nw.model.ConditionValue;
import com.nw.model.Conditioning;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.Valuation;
import com.nw.model.VehicleDetails;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomEditText;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PricingFragment extends BaseFragement implements OnClickListener, TextWatcher
{
    int PRICING_TYPE = 1, VALUATION = 2, total_trade = 0, count_trade = 0, total_retail = 0, count_retail = 0;

    LinearLayout llTUATradeRetail, llOnlinePrice, llSimpleLogic, llIxTrader, llPrivateAdverts, llValuation;
    TextView tvTransUnion, tvOnlinePriceNow, tvSimplePriceTrade, tvSimplePriceRetail, tvIXTrader, tvPrivateadvert;
    TextView tvAvgTrade, tvAvgMarket, tvAvgRetail;
    boolean isTrade, isMarket, isRetail;
    TextView tvTUAPriceTrade, tvTUAPriceRetail, tvFetch, tvTUAPriceDate, tvUpdate, tvTitleCarName, tvCardetails;
    VehicleDetails vehicleDetails;
    private ArrayList<ConditionValue> conditioningArrayList;

    // Valuation
    CustomEditText edtConditionOffer, edtCondition, et_retailAverage, et_retailExtra, et_retailKilometers, et_retailOther, et_offerTrade, et_offerExtra,
            et_offerKilometer, et_offerInterior, et_offerEngine, et_offerExterior, et_offerOther;

    TextView tv_retailAverage, tv_retailCondition, tv_retailExtra, tv_retailKilometers, tv_retailOther, tv_offerTrade, tv_offerCondition,
            tv_offerExtra, tv_offerKilometer, tv_offerInterior, tv_offerEngine, tv_offerExterior, tv_offerOther, tv_retailTotal, tv_offerTotal, tv_gross_profit, tv_total_percentage;

    int EdittextIdsRetail[] = {R.id.et_retailAverage, R.id.et_retailExtra, R.id.et_retailKilometers, R.id.et_retailOther};
    int EdittextIdsOffer[] = {R.id.et_offerTrade, R.id.et_offerExtra, R.id.et_offerKilometer, R.id.et_offerInterior, R.id.et_offerEngine, R.id.et_offerExterior,
            R.id.et_offerOther};
    private View viewParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        viewParent = inflater.inflate(R.layout.fragment_pricing, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            vehicleDetails = getArguments().getParcelable("vehicleDetails");
        }
        initialise(viewParent);
        putValues();
        return viewParent;
    }

    private void initialise(View view)
    {
        // Editext
        edtConditionOffer = (CustomEditText) view.findViewById(R.id.edtConditionOffer);
        edtCondition = (CustomEditText) view.findViewById(R.id.edtCondition);
        edtConditionOffer.setOnClickListener(this);
        edtCondition.setOnClickListener(this);

        et_retailAverage = (CustomEditText) view.findViewById(R.id.et_retailAverage);
        et_retailExtra = (CustomEditText) view.findViewById(R.id.et_retailExtra);
        et_retailKilometers = (CustomEditText) view.findViewById(R.id.et_retailKilometers);
        et_retailOther = (CustomEditText) view.findViewById(R.id.et_retailOther);
        et_offerTrade = (CustomEditText) view.findViewById(R.id.et_offerTrade);
        et_offerExtra = (CustomEditText) view.findViewById(R.id.et_offerExtra);
        et_offerKilometer = (CustomEditText) view.findViewById(R.id.et_offerKilometer);
        et_offerInterior = (CustomEditText) view.findViewById(R.id.et_offerInterior);
        et_offerEngine = (CustomEditText) view.findViewById(R.id.et_offerEngine);
        et_offerExterior = (CustomEditText) view.findViewById(R.id.et_offerExterior);
        et_offerOther = (CustomEditText) view.findViewById(R.id.et_offerOther);

        et_retailAverage.addTextChangedListener(this);
        et_retailExtra.addTextChangedListener(this);
        et_retailKilometers.addTextChangedListener(this);
        et_offerEngine.addTextChangedListener(this);
        et_offerExterior.addTextChangedListener(this);
        et_offerOther.addTextChangedListener(this);

        et_offerTrade.addTextChangedListener(this);
        et_offerExtra.addTextChangedListener(this);
        et_offerKilometer.addTextChangedListener(this);
        et_offerInterior.addTextChangedListener(this);
        et_retailOther.addTextChangedListener(this);

        tv_retailTotal = (TextView) view.findViewById(R.id.tv_retailTotal);
        tv_offerTotal = (TextView) view.findViewById(R.id.tv_offerTotal);
        tv_gross_profit = (TextView) view.findViewById(R.id.tv_gross_profit);
        tv_total_percentage = (TextView) view.findViewById(R.id.tv_total_percentage);

        tvTUAPriceTrade = (TextView) view.findViewById(R.id.tvTUAPriceTrade);
        tvTUAPriceRetail = (TextView) view.findViewById(R.id.tvTUAPriceRetail);
        tvFetch = (TextView) view.findViewById(R.id.tvTUAPriceFetch);
        tvTitleCarName = (TextView) view.findViewById(R.id.tvTitleCarName);
        tvCardetails = (TextView) view.findViewById(R.id.tvCardetails);
        tvTUAPriceDate = (TextView) view.findViewById(R.id.tvTUAPriceDate);
        tvUpdate = (TextView) view.findViewById(R.id.tvTUAPriceUpdate);
        tvOnlinePriceNow = (TextView) view.findViewById(R.id.tvOnlinePriceNow);
        tvSimplePriceTrade = (TextView) view.findViewById(R.id.tvSimplePriceTrade);
        tvSimplePriceRetail = (TextView) view.findViewById(R.id.tvSimplePriceRetail);
        tvIXTrader = (TextView) view.findViewById(R.id.tvIXTrader);
        tvPrivateadvert = (TextView) view.findViewById(R.id.tvPrivateadvert);
        tvAvgRetail = (TextView) view.findViewById(R.id.tvAvgRetail);
        tvAvgMarket = (TextView) view.findViewById(R.id.tvAvgMarket);
        tvAvgTrade = (TextView) view.findViewById(R.id.tvAvgTrade);
        tvTransUnion = (TextView) view.findViewById(R.id.tvTransUnion);

        // Valuation TextView
        tv_retailAverage = (TextView) view.findViewById(R.id.tv_retailAverage);
        tv_retailCondition = (TextView) view.findViewById(R.id.tv_retailCondition);
        tv_retailExtra = (TextView) view.findViewById(R.id.tv_retailExtra);
        tv_retailKilometers = (TextView) view.findViewById(R.id.tv_retailKilometers);
        tv_retailOther = (TextView) view.findViewById(R.id.tv_retailOther);
        tv_offerTrade = (TextView) view.findViewById(R.id.tv_offerTrade);
        tv_offerCondition = (TextView) view.findViewById(R.id.tv_offerCondition);
        tv_offerExtra = (TextView) view.findViewById(R.id.tv_offerExtra);
        tv_offerKilometer = (TextView) view.findViewById(R.id.tv_offerKilometer);
        tv_offerInterior = (TextView) view.findViewById(R.id.tv_offerInterior);
        tv_offerEngine = (TextView) view.findViewById(R.id.tv_offerEngine);
        tv_offerExterior = (TextView) view.findViewById(R.id.tv_offerExterior);
        tv_offerOther = (TextView) view.findViewById(R.id.tv_offerOther);


        llTUATradeRetail = (LinearLayout) view.findViewById(R.id.llTUATradeRetail);
        llOnlinePrice = (LinearLayout) view.findViewById(R.id.llOnlinePrice);
        llSimpleLogic = (LinearLayout) view.findViewById(R.id.llSimpleLogic);
        llIxTrader = (LinearLayout) view.findViewById(R.id.llIxTrader);
        llPrivateAdverts = (LinearLayout) view.findViewById(R.id.llPrivateAdverts);
        llValuation = (LinearLayout) view.findViewById(R.id.llValuation);


        llSimpleLogic.setOnClickListener(this);
        llIxTrader.setOnClickListener(this);
        llPrivateAdverts.setOnClickListener(this);
        llOnlinePrice.setOnClickListener(this);
        if (getArguments().getInt("Type") == PRICING_TYPE)
        {
            llValuation.setVisibility(View.GONE);
        } else
        {
            llValuation.setVisibility(View.VISIBLE);
            getValuationData();
        }

        tvFetch.setOnClickListener(this);
        tvUpdate.setOnClickListener(this);
    }

    private void putValues()
    {
        if (vehicleDetails.getTUATradePrice() != null && vehicleDetails.getTUARetailPrice() != null)
        {
            tvFetch.setVisibility(View.GONE);
            tvTUAPriceTrade.setVisibility(View.VISIBLE);
            llTUATradeRetail.setVisibility(View.VISIBLE);
            tvTUAPriceDate.setVisibility(View.VISIBLE);
            tvUpdate.setVisibility(View.VISIBLE);

        } else
        {
            tvTUAPriceTrade.setVisibility(View.GONE);
            llTUATradeRetail.setVisibility(View.GONE);
            tvUpdate.setVisibility(View.GONE);
            tvTUAPriceDate.setVisibility(View.GONE);
            tvFetch.setVisibility(View.VISIBLE);
        }

        tvTitleCarName.setText(Html.fromHtml("<font color=#ffffff>" + vehicleDetails.getYear() + "</font> <font color=" + getActivity().getResources().getColor(R.color.dark_blue) + ">"
                + vehicleDetails.getFriendlyName() + "</font>"));
        StringBuilder detailsBuilder = new StringBuilder();
        if (vehicleDetails.getEngine_CC() != 0)
            detailsBuilder.append(vehicleDetails.getEngine_CC() + "cc");
        if (vehicleDetails.getPower_KW() != 0)
            detailsBuilder.append(", " + vehicleDetails.getPower_KW() + "kW");
        if (vehicleDetails.getTorque_NM() != 0)
            detailsBuilder.append(", " + vehicleDetails.getTorque_NM() + "Nm");
        if (vehicleDetails.getFuel_Type() != null && !vehicleDetails.getFuel_Type().isEmpty())
            detailsBuilder.append(", " + vehicleDetails.getFuel_Type() + "");
        if (vehicleDetails.getTransmission_Type() != null && !vehicleDetails.getTransmission_Type().isEmpty())
            detailsBuilder.append(", " + vehicleDetails.getTransmission_Type());
        if (vehicleDetails.getGearbox() != null)
            if (vehicleDetails.getGearbox() != null && !vehicleDetails.getGearbox().isEmpty())
                detailsBuilder.append(", " + vehicleDetails.getGearbox());
        if (vehicleDetails.getGears() != null)
            if (vehicleDetails.getGears() != null && !vehicleDetails.getGears().isEmpty() && !vehicleDetails.getGears().equalsIgnoreCase("Gears?"))
                detailsBuilder.append(", " + vehicleDetails.getGears() + " gears");
        if (vehicleDetails.getOfferStart() != null && !vehicleDetails.getOfferStart().isEmpty())
            if (TextUtils.isEmpty(vehicleDetails.getOfferEnd()) || vehicleDetails.getOfferStart() == null)
            {
                if (TextUtils.isEmpty(detailsBuilder))
                {
                    detailsBuilder.append("Avail. as new from " + Helper.convertUTCDateToMonthYear(vehicleDetails.getOfferStart()));
                } else
                {
                    detailsBuilder.append(". Avail. as new from " + Helper.convertUTCDateToMonthYear(vehicleDetails.getOfferStart()));
                }
            } else
            {
                if (TextUtils.isEmpty(detailsBuilder))
                {
                    detailsBuilder.append("Avail. as new from " + Helper.convertUTCDateToMonthYear(vehicleDetails.getOfferStart())
                            + " to " + Helper.convertUTCDateToMonthYear(vehicleDetails.getOfferEnd()));
                } else
                {
                    detailsBuilder.append(". Avail. as new from " + Helper.convertUTCDateToMonthYear(vehicleDetails.getOfferStart())
                            + " to " + Helper.convertUTCDateToMonthYear(vehicleDetails.getOfferEnd()));
                }
            }
        tvCardetails.setText(detailsBuilder.toString());
        tvOnlinePriceNow.setText(vehicleDetails.getOnline_price());
        tvSimplePriceTrade.setText(vehicleDetails.getSimple_logic_trade());
        tvSimplePriceRetail.setText(vehicleDetails.getSimple_logic_retail());
        tvIXTrader.setText(vehicleDetails.getIx_trade());
        tvPrivateadvert.setText(vehicleDetails.getPrivate_advert());
        tvTUAPriceRetail.setText(vehicleDetails.getTUARetailPrice());
        tvTUAPriceTrade.setText(vehicleDetails.getTUATradePrice());
        tvTUAPriceDate.setText("TUA price check : " + vehicleDetails.getTUADate());
        getAveragePrices();
    }

    private void getAveragePrices()
    {
        if (Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getSimple_logic_trade())) != 0)
        {
            total_trade += Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getSimple_logic_trade()));
            count_trade++;
        }
        if (Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getIx_trade())) != 0)
        {
            total_trade += Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getIx_trade()));
            count_trade++;
        }
        if (Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getTUATradePrice())) != 0)
        {
            total_trade += Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getTUATradePrice()));
            count_trade++;
        }
        if (Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getTUARetailPrice())) != 0)
        {
            total_retail += Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getTUARetailPrice()));
            count_retail++;
        }
        if (Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getSimple_logic_retail())) != 0)
        {
            total_retail += Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getSimple_logic_retail()));
            count_retail++;
        }
        if (Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getOnline_price())) != 0)
        {
            total_retail += Integer.parseInt(Helper.formatPriceToDefault(vehicleDetails.getSimple_logic_trade()));
            count_retail++;
        }
        try
        {
            tvAvgTrade.setText(Helper.formatPrice("" + (total_trade / count_trade)));
            tvAvgMarket.setText(vehicleDetails.getPrivate_advert());
            tvAvgRetail.setText(Helper.formatPrice("" + (total_retail / count_retail)));
        } catch (Exception e)
        {

            e.printStackTrace();
            tvAvgTrade.setText("R0");
            tvAvgMarket.setText(vehicleDetails.getPrivate_advert());
            tvAvgRetail.setText("R0");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getArguments().getInt("Type") == PRICING_TYPE)
        {
            showActionBar("Pricing");
        } else
        {
            showActionBar("Valuation");
        }
    }

    @Override
    public void onClick(View v)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("vehicleDetails", vehicleDetails);
        switch (v.getId())
        {
            case R.id.tvTUAPriceFetch:
                tvFetch.setVisibility(View.GONE);
                tvTUAPriceTrade.setVisibility(View.VISIBLE);
                llTUATradeRetail.setVisibility(View.VISIBLE);
                tvTUAPriceDate.setVisibility(View.VISIBLE);
                tvUpdate.setVisibility(View.VISIBLE);
                break;

            case R.id.tvTUAPriceUpdate:
                CustomDialogManager.showOkCancelDialog(getActivity(), "Are you sure? You will be charged again.",
                        "Yes", "No", new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                switch (type)
                                {
                                    case Dialog.BUTTON_POSITIVE:
                                        getUpdatedPrice();
                                        break;
                                }
                            }
                        });
                break;

            case R.id.llPrivateAdverts:
                PrivateAdvertFragment privateAdvertFragment = new PrivateAdvertFragment();
                privateAdvertFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, privateAdvertFragment).addToBackStack(null).commit();
                break;

            case R.id.llIxTrader:
                IxTraderFragment ixTraderFragment = new IxTraderFragment();
                ixTraderFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, ixTraderFragment).addToBackStack(null).commit();
                break;

            case R.id.llOnlinePrice:
                OnlineTradePriceFragment onlineTradePriceFragment = new OnlineTradePriceFragment();
                onlineTradePriceFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, onlineTradePriceFragment).addToBackStack(null).commit();
                break;

            case R.id.llSimpleLogic:
                SimpleLogicFragment simpleLogicFragment = new SimpleLogicFragment();
                simpleLogicFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.Container, simpleLogicFragment).addToBackStack(null).commit();
                break;
            case R.id.edtConditionOffer:

            case R.id.edtCondition:
                if (conditioningArrayList != null)
                    if (conditioningArrayList.size() > 0)
                        showConditiontypePopup(v);
                break;
        }
    }

    private void getUpdatedPrice()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            if (!vehicleDetails.getVin().equals("No VIN loaded"))
            {
                parameterList.add(new Parameter("vinNumber", vehicleDetails.getVin(), String.class));
            } else
            {
                parameterList.add(new Parameter("vinNumber", "", String.class));
            }
            parameterList.add(new Parameter("manufactureYear", vehicleDetails.getYear(), String.class));
            parameterList.add(new Parameter("registrationNumber", "", String.class));
            parameterList.add(new Parameter("MMCode", vehicleDetails.getMmcode(), String.class));
            parameterList.add(new Parameter("mileage", "", String.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("LoadTransUnionPricing");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/LoadTransUnionPricing");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {
                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject resultObj = (SoapObject) outer.getPropertySafely("Result");
                        SoapObject inner = (SoapObject) resultObj.getPropertySafely("TUAPricing");
                        vehicleDetails.setTUARetailPrice(Helper.formatPrice(inner.getPropertySafelyAsString("TUARetailPrice", "0")));
                        vehicleDetails.setTUATradePrice(Helper.formatPrice(inner.getPropertySafelyAsString("TUATradePrice", "0")));
                        vehicleDetails.setTUADate(Helper.convertUTCDateToNormal(inner.getPropertySafelyAsString("SearchDateTime", "Date?")));
                        tvTUAPriceRetail.setText(Helper.formatPrice(inner.getPropertySafelyAsString("TUARetailPrice", "0")));
                        tvTUAPriceTrade.setText(Helper.formatPrice(inner.getPropertySafelyAsString("TUATradePrice", "0")));
                        tvTUAPriceDate.setText("TUA price check : " + Helper.convertUTCDateToNormal(resultObj.getPropertySafelyAsString("SearchDateTime", "0")));
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

    private void getValuationData()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            //Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("UserHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("AppraisalId", 1, String.class));

            //   parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("Vin", vehicleDetails.getVin(), Integer.class));

            //create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ReturnValuation");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ReturnValuation");
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
                        SoapObject response = (SoapObject) outer.getPropertySafely("Valuation");


                        Valuation valuation = new Valuation();
                        //valuation.setAverageRetail((int)Double.parseDouble(responseCalculatingRetail.getPropertySafelyAsString("AverageRetail", "0.0")));
                        //CalculatingRetail
                        SoapObject responseCalculatingRetail = (SoapObject) response.getPropertySafely("CalculatingRetail");

                        valuation.setAverageRetail(Helper.formatPrice(responseCalculatingRetail.getPropertySafelyAsString("AverageRetail", "0.0")));
                        valuation.setAdjCondition(responseCalculatingRetail.getPropertySafelyAsString("AdjCondition", ""));
                        valuation.setAdjExtras(Helper.formatPrice(responseCalculatingRetail.getPropertySafelyAsString("AdjExtras", "0.0")));
                        valuation.setAdjKilometers(responseCalculatingRetail.getPropertySafelyAsString("AdjKilometers", "0.0"));
                        valuation.setAdjOther(responseCalculatingRetail.getPropertySafelyAsString("AdjOther", "0.0"));
                        valuation.setAnticipatedRetail(responseCalculatingRetail.getPropertySafelyAsString("AnticipatedRetail", "0.0"));

                        //CalculatingOffer
                        SoapObject responseCalculatingOffer = (SoapObject) response.getPropertySafely("CalculatingOffer");

                        valuation.setAverageTrade(Helper.formatPrice(responseCalculatingOffer.getPropertySafelyAsString("AverageTrade", "0.0")));
                        valuation.setAdjConditionOffer(responseCalculatingOffer.getPropertySafelyAsString("AdjCondition", "0.0"));
                        valuation.setAdjExtrasOffer(Helper.formatPrice(responseCalculatingOffer.getPropertySafelyAsString("AdjExtras", "0.0")));
                        valuation.setAdjKilometersOffer(responseCalculatingOffer.getPropertySafelyAsString("AdjKilometers", "0.0"));
                        valuation.setAdjInterior(Helper.formatPrice(responseCalculatingOffer.getPropertySafelyAsString("AdjInterior", "0.0")));
                        valuation.setAdjEngine(Helper.formatPrice(responseCalculatingOffer.getPropertySafelyAsString("AdjEngine", "0.0")));
                        valuation.setAdjExterior(Helper.formatPrice(responseCalculatingOffer.getPropertySafelyAsString("AdjExterior", "0.0")));
                        valuation.setAdjOtherOffer(responseCalculatingOffer.getPropertySafelyAsString("AdjOther", "0.0"));

                        // ConditionValues
                        SoapObject responseConditionValues = (SoapObject) response.getPropertySafely("ConditionValues");

                        conditioningArrayList = new ArrayList<ConditionValue>();

                        for (int i = 0; i < responseConditionValues.getPropertyCount(); i++)
                        {
                            ConditionValue conditionValue = new ConditionValue();
                            SoapObject conditionObj = (SoapObject) responseConditionValues.getProperty(i);
                            conditionValue.setText(conditionObj.getPropertySafelyAsString("Text", ""));
                            conditionValue.setValue(Integer.parseInt(conditionObj.getPropertySafelyAsString("Value", "")));
                            conditioningArrayList.add(conditionValue);
                        }

                        setVauesOfValuation(valuation);


                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        hideProgressDialog();
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                //getActivity().getSupportFragmentManager().popBackStack();
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

    private void setVauesOfValuation(Valuation valuation)
    {
        tv_retailAverage.setText(valuation.getAverageRetail());
        tv_retailCondition.setText(valuation.getAdjCondition());
        tv_retailExtra.setText(valuation.getAdjExtras() + " (+)");
        tv_retailKilometers.setText(valuation.getAdjKilometers());
        tv_retailOther.setText(valuation.getAdjOther() + " (+ -)");

        tv_offerTrade.setText(valuation.getAverageTrade());
        tv_offerCondition.setText(valuation.getAdjConditionOffer());
        tv_offerExtra.setText("(+) " + valuation.getAdjExtrasOffer());
        tv_offerKilometer.setText(valuation.getAdjKilometersOffer());
        tv_offerInterior.setText("(-) " + valuation.getAdjInterior());
        tv_offerEngine.setText("(-) " + valuation.getAdjEngine());
        tv_offerExterior.setText("(-) " + valuation.getAdjExterior());
        tv_offerOther.setText("(+ -) " + valuation.getAdjOtherOffer());

    }


    private void showConditiontypePopup(final View view)
    {
        ArrayList<String> conditions = new ArrayList<>();
        for (int i = 0; i < conditioningArrayList.size(); i++)
        {
            conditions.add(conditioningArrayList.get(i).getText());
        }
        ArrayAdapter<String> conditionType = new ArrayAdapter<String>(getActivity(), R.layout.list_item_text2, conditions);
        Helper.showDropDown(view, conditionType, new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
            {
                EditText editText = (EditText) view;
                editText.setText(conditioningArrayList.get(itemPosition).getText());
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
        EditText editText = (EditText) getActivity().getCurrentFocus();
        if (editText != null)
        {
            boolean isfromRetails = false;
            for (int i = 0; i < EdittextIdsRetail.length; i++)
            {
                if (editText.getId() == EdittextIdsRetail[i])
                {
                    isfromRetails = true;
                    break;
                }
            }
            if (isfromRetails)
            {
                int total_value = 0;
                for (int i = 0; i < EdittextIdsRetail.length; i++)
                {
                    EditText editText1 = (EditText) viewParent.findViewById(EdittextIdsRetail[i]);

                    if (!TextUtils.isEmpty(editText1.getText().toString().trim()))
                    {
                        if (editText1.getText().toString().trim().contains("-"))
                        {
                            String valueFromEdittext = editText1.getText().toString().trim().replaceAll("-", "");

                            if (!TextUtils.isEmpty(valueFromEdittext))
                                total_value -= Integer.parseInt(valueFromEdittext);
                        } else
                        {
                            String valueFromEdittext = editText1.getText().toString().trim().replaceAll("\\+", "");

                            if (!TextUtils.isEmpty(valueFromEdittext))
                                total_value += Integer.parseInt(valueFromEdittext);
                        }

                        String totalValue = String.valueOf(total_value);
                        if (totalValue.contains("-"))
                        {
                            tv_retailTotal.setText("-" + Helper.formatPrice("" + totalValue.toString().trim().replaceAll("-", "")));
                        } else
                            tv_retailTotal.setText(Helper.formatPrice("" + total_value));
                    }

                    if (total_value == 0)
                        tv_retailTotal.setText(Helper.formatPrice("" + total_value));

                    calculateGrossProfitTotal();
                }
            } else
            {
                int total_value = 0;
                for (int i = 0; i < EdittextIdsOffer.length; i++)
                {
                    EditText editText1 = (EditText) viewParent.findViewById(EdittextIdsOffer[i]);

                    if (!TextUtils.isEmpty(editText1.getText().toString().trim()))
                    {
                        if (editText1.getText().toString().trim().contains("-"))
                        {
                            String valueFromEdittext = editText1.getText().toString().trim().replaceAll("-", "");

                            if (!TextUtils.isEmpty(valueFromEdittext))
                                total_value -= Integer.parseInt(valueFromEdittext);
                        } else if (editText1.getId() == R.id.et_offerInterior || editText1.getId() == R.id.et_offerEngine || editText1.getId() == R.id.et_offerExterior)
                        {
                            String valueFromEdittext = editText1.getText().toString().trim().replaceAll("-", "");

                            if (!TextUtils.isEmpty(valueFromEdittext))
                                total_value -= Integer.parseInt(valueFromEdittext);

                        } else
                        {
                            String valueFromEdittext = editText1.getText().toString().trim().replaceAll("\\+", "");

                            if (!TextUtils.isEmpty(valueFromEdittext))
                                total_value += Integer.parseInt(valueFromEdittext);
                        }

                        String totalValue = String.valueOf(total_value);
                        if (totalValue.contains("-"))
                        {
                            tv_offerTotal.setText("-" + Helper.formatPrice("" + totalValue.toString().trim().replaceAll("-", "")));
                        } else
                            tv_offerTotal.setText(Helper.formatPrice("" + total_value));
                    }

                    if (total_value == 0)
                        tv_offerTotal.setText(Helper.formatPrice("" + total_value));

                    calculateGrossProfitTotal();
                }
            }
            System.out.println("Value of edit text : " + s.toString().trim() + " Tags : " + editText.getId());
        }
    }

    private void calculateGrossProfitTotal()
    {
        DecimalFormat f = new DecimalFormat("##.00");

        String retailTotal = tv_retailTotal.getText().toString().trim().replaceAll("R", "").replaceAll("\\s", "");
        int int_totalRetails = Integer.parseInt(retailTotal);

        String offerTotal = tv_offerTotal.getText().toString().trim().replaceAll("R", "").replaceAll("\\s", "");
        int int_offerTotal = Integer.parseInt(offerTotal);

        int int_grossProfit = int_totalRetails - int_offerTotal;

        if (int_grossProfit < 0)
            tv_gross_profit.setText("-" + Helper.formatPrice("" + String.valueOf(int_grossProfit).replaceAll("-", "")));
        else
            tv_gross_profit.setText(Helper.formatPrice(String.valueOf(int_grossProfit)));

        double percentage = ((double) int_grossProfit / (double) int_totalRetails) * 100;
        if (String.valueOf(percentage).contains("-"))
            tv_total_percentage.setText("0.00 %");
        else if(percentage ==0.0)
            tv_total_percentage.setText("0.00%");
        else
            tv_total_percentage.setText("" + f.format(percentage) + "%");
    }
}
