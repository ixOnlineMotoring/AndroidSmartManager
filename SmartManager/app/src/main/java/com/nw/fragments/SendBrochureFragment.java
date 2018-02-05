package com.nw.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.joanzapata.android.iconify.Iconify;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nw.adapters.EBrochureVariantAdapter;
import com.nw.adapters.VehicleDetailsAdapter;
import com.nw.model.DataInObject;
import com.nw.model.Parameter;
import com.nw.model.SmartObject;
import com.nw.model.Variant;
import com.nw.model.Vehicle;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.nw.widget.CustomTextView;
import com.smartmanager.activity.ImageCropperActivity;
import com.smartmanager.android.R;
import com.utils.AppSession;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Akshay.Belapurkar
 */

public class SendBrochureFragment extends BaseFragement implements OnClickListener
{
    ArrayList<Vehicle> myStockList = new ArrayList<>(), groupStockList = new ArrayList<>();
    StocksDetailFragment stocksDetailFragment;
    StockListDetailsFragment stockListDetailsFragment;
    int selectedPageNumber = 0, selectedPageNumber1 = 0, total_no_of_records_mystock = 1000, total_no_of_records_groupstock = 1000, selectedSortType = 1,
            selectedSearchPageNumber, selectedSearchPageNumber1, selectedSortMethod, myStock = 0, opertionType = 1, groupStock = 0;
    VehicleDetailsAdapter myStockadapter, groupStockAdapter;
    boolean isAtEnd = false;
    ListView lvMyStock, lvGroupStock, lvVarientList;
    LinearLayout llFilter, llayout_make_model_variant, llProfilePhotoUpload;
    ImageView ivArrow, ivUserProfilePhoto;
    TextView tvFilter;
    EditText edKeyword, edSordBy, edSortByVehicles, edMake, edModel;
    Button btnList, btnProfilePhotoUpload;
    ValueAnimator mAnimator;
    ArrayList<SortItem> sortbylist = new ArrayList<SortItem>();
    ArrayList<SortItem> sortbylistVehicles = new ArrayList<SortItem>();
    int selectedStatusId = 1, FOR_MY_STOCK_USED = 1, FOR_MY_STOCK_USED_NEW = 2, FOR_GROUP_STOCK_USED = 3, FOR_GROUP_STOCK_USED_NEW = 4;
    String lastFilterMystock, lastFilterGroupstock;
    boolean fromLoadMore = false;
    String keyword;
    boolean isGroupAssociated = false;
    CustomTextView tvNoGroupTab;
    TableRow tablerow_sortby;
    TextView tvCustomiseBroucher;
    ArrayList<SmartObject> makeList;
    ArrayList<SmartObject> modelList;
    ArrayList<Variant> variantList;
    int selectedMakeId, selectedModelId, selectedVariantId;
    Variant variant;
    int selectedPosition = 0;
    int selectedSort = 1;
    int selectedMethod = 0;
    boolean isFirstTimeLoading = true;

    Uri fileUri = null;
    Uri outputFileUri = null;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int PERMISSION_STORAGE = 3;
    private static final int CROP_IMAGE = 4;
    String filePath;
    String isProfilePhotoAvailable = "true";

    ImageLoader loader;
    private DisplayImageOptions user_options;
    private int index = 0;
    View viewDivider;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_stock_list, container, false);
        setHasOptionsMenu(true);
        initImageLoader();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (sortbylist.size() == 0)
            putSortListItems();

        if (sortbylistVehicles.size() == 0)
            putSortListVehiclesItems();

        initialise(view);
        hideImageDetails();

        //Call this webservice only when user navigates from E-Brochure & not from Stock listing
        if (getArguments().getString("fromFragment") != null)
        {
            if (getArguments().getString("fromFragment").equals("E-Brochure"))
            {
                hasProfileImage();
                if (AppSession.getImageDetails(getActivity()).equals("true"))
                {
                    showImageDetails();
                } else
                {
                    hideImageDetails();
                }
            }


        }
        //getClientCorprateGroups();
        setDataFromPreviouslyCalledWebservice();
        hideKeyboard(view);

        return view;
    }

    private void initImageLoader()
    {
        loader = ImageLoader.getInstance();
        initUserImageLoader();

    }

    private void initUserImageLoader()
    {
        user_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)
                .showImageOnFail(Helper.getIcon(getActivity(), Iconify.IconValue.fa_user, 30))
                .showImageForEmptyUri(Helper.getIcon(getActivity(), Iconify.IconValue.fa_user, 30)).cacheInMemory(false)
                .cacheOnDisk(false).build();
    }


    private void initialise(View view)
    {
        tvNoGroupTab = (CustomTextView) view.findViewById(R.id.tvNoGroupTab);
        lvMyStock = (ListView) view.findViewById(R.id.lvMyStock);
        lvGroupStock = (ListView) view.findViewById(R.id.lvGroupStock);
        lvVarientList = (ListView) view.findViewById(R.id.lvVarientList);
        lvGroupStock.setVisibility(View.GONE);
        lvVarientList.setVisibility(View.GONE);
        ivArrow = (ImageView) view.findViewById(R.id.ivArrow);
        llFilter = (LinearLayout) view.findViewById(R.id.llFilter);
        llayout_make_model_variant = (LinearLayout) view.findViewById(R.id.llayout_make_model_variant);
        tvFilter = (TextView) view.findViewById(R.id.tvFilter);
        edKeyword = (EditText) view.findViewById(R.id.edKeywordStock);
        edKeyword.setHintTextColor(Color.WHITE);
        edSordBy = (EditText) view.findViewById(R.id.edSortByStock);
        edSordBy.setText(getString(R.string.price_ascending));
        edSortByVehicles = (EditText) view.findViewById(R.id.edSortByVehicles);

        // Filter Variant
        edMake = (EditText) view.findViewById(R.id.edMake);
        edModel = (EditText) view.findViewById(R.id.edModel);
        btnList = (Button) view.findViewById(R.id.btnList);
        edMake.setOnClickListener(this);
        edModel.setOnClickListener(this);
        btnList.setOnClickListener(this);

        tablerow_sortby = (TableRow) view.findViewById(R.id.tablerow_sortby);
        ivUserProfilePhoto = (ImageView) view.findViewById(R.id.ivUserProfilePhoto);
        btnProfilePhotoUpload = (Button) view.findViewById(R.id.btnProfilePhotoUpload);
        btnProfilePhotoUpload.setOnClickListener(this);
        tvFilter.setOnClickListener(this);
        ivArrow.setOnClickListener(this);
        //	tvFilter.performClick();
        edSordBy.setOnClickListener(this);
        edSortByVehicles.setOnClickListener(this);
        edSortByVehicles.setText(sortbylistVehicles.get(0).sortattribute);
        // method used to call web service on search button click of device keyboard
        llProfilePhotoUpload = (LinearLayout) view.findViewById(R.id.llProfilePhotoUpload);
        viewDivider = (View) view.findViewById(R.id.viewDivider);
        viewDivider.setVisibility(View.GONE);
        tvCustomiseBroucher = (TextView) view.findViewById(R.id.tvCustomiseBroucher);
        tvCustomiseBroucher.setVisibility(View.GONE);
        edKeyword.setOnEditorActionListener(new OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                    {
                        myStockList.clear();
                    } else
                    {
                        groupStockList.clear();
                    }
                    keyword = edKeyword.getText().toString().trim();
                    if (!TextUtils.isEmpty(keyword))
                    {
                        if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                        {
                            selectedSearchPageNumber = 0;
                            searchVehicleByKeyword(keyword, selectedSearchPageNumber, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                        } else
                        {
                            selectedSearchPageNumber1 = 0;
                            searchVehicleByKeyword(keyword, selectedSearchPageNumber1, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                        }
                    } else
                    {
                        if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                        {
                            selectedSearchPageNumber = 0;
                            selectedPageNumber = 0;
                            getVehicleList(selectedPageNumber, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                        } else
                        {
                            selectedSearchPageNumber1 = 0;
                            selectedPageNumber1 = 0;
                            getVehicleList(selectedPageNumber1, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                        }
                    }
                    return true;
                } else
                    return false;
            }
        });

        myStockadapter = new VehicleDetailsAdapter(getActivity(), myStockList, opertionType);
        lvMyStock.setAdapter(myStockadapter);
        groupStockAdapter = new VehicleDetailsAdapter(getActivity(), groupStockList, opertionType);
        lvGroupStock.setAdapter(groupStockAdapter);
        /*
         * go to details on clicking on item pass full object to next fragment
		 */
        lvMyStock.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                if (getArguments().getString("fromFragment").equals("E-Brochure"))
                {
                    stocksDetailFragment = new StocksDetailFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("selectedVehicle", myStockList.get(position));
                    args.putString("fromFragment", "E-Brochure");
                    if (opertionType == 2 || opertionType == 4)
                    {
                        args.putString("operationType", "new");
                    } else
                    {
                        args.putString("operationType", "used");
                    }
                    stocksDetailFragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.Container, stocksDetailFragment).addToBackStack(null).commit();
                } else
                {
                    stockListDetailsFragment = new StockListDetailsFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("selectedVehicle", myStockList.get(position));
                    args.putString("fromFragment", "Stock List");
                    args.putString("enableImageDetails", "false");

                    if (opertionType == 2 || opertionType == 4)
                    {
                        args.putString("operationType", "new");
                    } else
                    {
                        args.putString("operationType", "used");
                    }
                    stockListDetailsFragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.Container, stockListDetailsFragment).addToBackStack(null).commit();

                }
            }
        });

        lvMyStock.setOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                hideKeyboard();
                int threshold = 1;
                int count = lvMyStock.getCount();
                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if (lvMyStock.getLastVisiblePosition() >= count - threshold)
                    {
                        if (!myStockList.isEmpty())
                        {
                            if (myStockList.size() < total_no_of_records_mystock)
                            {
                                fromLoadMore = true;
                                if (TextUtils.isEmpty(keyword))
                                {
                                    selectedPageNumber++;
                                    getVehicleList(selectedPageNumber, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                                } else
                                {
                                    selectedSearchPageNumber++;
                                    searchVehicleByKeyword(keyword, selectedSearchPageNumber, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        });

        // for groupstock section new listview with all things are created
        /*
         * go to details on clicking on item pass full object to next fragment
		 */
        lvGroupStock.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                if (getArguments().getString("fromFragment").equals("E-Brochure"))
                {
                    stocksDetailFragment = new StocksDetailFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("selectedVehicle", groupStockList.get(position));
                    if (opertionType == 2 || opertionType == 4)
                    {
                        args.putString("operationType", "new");
                    } else
                    {
                        args.putString("operationType", "used");
                    }
                    stocksDetailFragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.Container, stocksDetailFragment).addToBackStack(null).commit();
                } else
                {
                    stockListDetailsFragment = new StockListDetailsFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("selectedVehicle", groupStockList.get(position));
                    if (opertionType == 2 || opertionType == 4)
                    {
                        args.putString("operationType", "new");
                    } else
                    {
                        args.putString("operationType", "used");
                    }
                    stockListDetailsFragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.Container, stockListDetailsFragment).addToBackStack(null).commit();

                }
            }
        });

        lvVarientList.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {

                if (getArguments().getString("fromFragment").equals("E-Brochure"))
                {
                    stocksDetailFragment = new StocksDetailFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("varientDetails", variantList.get(position));
                    stocksDetailFragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.Container, stocksDetailFragment).addToBackStack(null).commit();

                } else
                {
                    stockListDetailsFragment = new StockListDetailsFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("varientDetails", variantList.get(position));
                    stockListDetailsFragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.Container, stockListDetailsFragment).addToBackStack(null).commit();
                }
            }
        });

        lvGroupStock.setOnScrollListener(new OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                hideKeyboard();
                int threshold = 1;
                int count = lvGroupStock.getCount();
                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if (lvGroupStock.getLastVisiblePosition() >= count - threshold)
                    {
                        if (!groupStockList.isEmpty())
                        {
                            if (groupStockList.size() < total_no_of_records_groupstock)
                            {

                                fromLoadMore = true;
                                if (TextUtils.isEmpty(keyword))
                                {
                                    selectedPageNumber1++;
                                    getVehicleList(selectedPageNumber1, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                                } else
                                {
                                    selectedSearchPageNumber1++;
                                    searchVehicleByKeyword(keyword, selectedSearchPageNumber1, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        });
        updateSegmentedControl();
    }

    /*
     * Function to update segmented control view*/
    private void updateSegmentedControl()
    {
        tvNoGroupTab.setText("My Stock(" + myStock + ")");
    }

    /*
     * Function to reset segemented control */
    private void resetSegmentedControl()
    {
        myStock = 0;
        groupStock = 0;
        tvNoGroupTab.setText("My Stock(" + myStock + ")");
    }

    // for expanding animation
    private void expand()
    {
        llFilter.setVisibility(View.VISIBLE);
    }

    // collapsing animation
    private void collapse()
    {
        llFilter.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tvFilter:
            case R.id.ivArrow:
                if (llFilter.getVisibility() == View.GONE)
                {
                    tvFilter.setText(getString(R.string.hidefilter));
                    ivArrow.setRotation(0);
                    expand();
                } else
                {
                    tvFilter.setText(getString(R.string.showfilter));
                    ivArrow.setRotation(-90);
                    collapse();
                    hideKeyboard();
                }
                break;

            case R.id.edSortByStock:
                hideKeyboard(v);
                showSortPopUp(v, sortbylist);
                break;

            case R.id.edSortByVehicles:
                hideKeyboard(v);
                showSortPopUp(v, sortbylistVehicles);
                break;

            case R.id.edMake:
                if (makeList == null)
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getMakeList(true);
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                } else
                {
                    if (!makeList.isEmpty())
                        showListPopUp(v, makeList);
                }
                break;

            case R.id.edModel:
                if (selectedMakeId == 0)
                {
                    return;
                }
                if (modelList != null)
                {
                    if (!modelList.isEmpty())
                        showListPopUp(v, modelList);
                } else
                {
                    if (HelperHttp.isNetworkAvailable(getActivity()))
                        getModelList();
                    else
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                }
                break;

            case R.id.btnList:
                //Added by Asmita
                //Onclick of List button hide the Image details (image view & upload button)
                //Make it visible on Screen landing
                AppSession.saveImageDetails(getActivity(), "false");
                hideImageDetails();
                tvCustomiseBroucher.setVisibility(View.GONE);
                if (selectedPosition == 2)
                {
                    if (selectedMakeId == 0)
                    {
                        Helper.showToast(getString(R.string.select_make1), getActivity());
                        return;
                    }
                    if (selectedModelId == 0)
                    {
                        Helper.showToast(getString(R.string.select_model1), getActivity());
                        return;
                    }
                    //AADED AKW

                    if (variantList != null)
                    {
                        if (!variantList.isEmpty())
                        {
                            lvGroupStock.setVisibility(View.GONE);
                            lvMyStock.setVisibility(View.GONE);
                            lvVarientList.setVisibility(View.VISIBLE);

                            EBrochureVariantAdapter variantAdapter = new EBrochureVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
                            lvVarientList.setAdapter(variantAdapter);

                        /*Helper.showDropDown(v, variantAdapter, new OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                               // edVariant.setText(variantList.get(position).getVariantName() + "");
                                selectedVariantId = variantList.get(position).getVariantId();
                                if (selectedVariantId != 0)
                                    variant = variantList.get(position);
                            }
                        });*/
                        }
                    } else
                    {
                        if (HelperHttp.isNetworkAvailable(getActivity()))
                            getVariantList();
                        else
                            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
                    }
                } else
                {
                    getWebServiceCall();
                }

                break;

            case R.id.btnProfilePhotoUpload:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                        return;
                    }
                }
                getImageFromGalleryOrCamera(getActivity());
                break;
        }
    }

    /*
      Add sort items in sort arraylist
     */
    private void putSortListItems()
    {
        /*sortbylist.add(new SortItem(getString(R.string.none), 0));
        sortbylist.add(new SortItem("Age", 0));
        sortbylist.add(new SortItem("Comments", 0));
        sortbylist.add(new SortItem("Extras", 0));
        sortbylist.add(new SortItem("Mileage", 0));
        sortbylist.add(new SortItem("Photos", 0));
        sortbylist.add(new SortItem("Price", 0));
        sortbylist.add(new SortItem("Stock#", 0));
        sortbylist.add(new SortItem("Year", 0));*/


        sortbylist.add(new SortItem(getString(R.string.none), 0));
        sortbylist.add(new SortItem("Price", 0));
        sortbylist.add(new SortItem("Kilometers", 0));
        sortbylist.add(new SortItem("Year", 0));
    }

    /*
      Add sort items in sort vehicle arraylist
     */
    private void putSortListVehiclesItems()
    {
        // sortbylistVehicles.add(new SortItem(getString(R.string.none), 0));
        sortbylistVehicles.add(new SortItem("Used Vehicles in Stock", 0));
        sortbylistVehicles.add(new SortItem("New Vehicles in Stock", 0));
        sortbylistVehicles.add(new SortItem("New Vehicles Full Range", 0));
        sortbylistVehicles.add(new SortItem("Group Used Vehicles in Stock", 0));
        sortbylistVehicles.add(new SortItem("Group New Vehicles in Stock", 0));
    }

    /*
     * show dropdown to show sort attributes
     */
    private void showSortPopUp(final View v, ArrayList<SortItem> list)
    {
        final ArrayList<SortItem> mList = list;
        final View mView = v;
        final EditText ed = (EditText) v;
        /*
         * 0- Descending order 1- Ascending order
		 */
        final ArrayAdapter<SortItem> sortadapter = new ArrayAdapter<SortItem>(
                getActivity(), R.layout.listitem_sort, R.id.tvText, mList)
        {

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_sort, null);
                }
                TextView tv = (TextView) convertView.findViewById(R.id.tvText);
                tv.setText(mList.get(position).sortattribute);
                ImageView iv = (ImageView) convertView.findViewById(R.id.ivSortMethod);
                if (position == 0) // for none do not show ascending/ descending
                    iv.setVisibility(View.GONE);
                if (mView.getId() == R.id.edSortByVehicles)
                    iv.setVisibility(View.GONE);
                if (position != selectedSortType)
                    iv.setVisibility(View.GONE);
                else
                {
                    if (mList.get(position).sortmethod == 0)
                    {
                        iv.setRotation(180);
                        //mList.get(position).sortmethod = 1;
                    } else
                    {
                        iv.setRotation(0);
                        //mList.get(position).sortmethod = 0;
                    }
                }
                return convertView;
            }
        };

        Helper.showDropDown(v, sortadapter, new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (mView.getId() == R.id.edSortByStock)
                {
                    if (selectedSortType == position)
                    {
                        if (mList.get(position).sortmethod == 0)
                        {
                            mList.get(position).sortmethod = 1;
                        } else
                        {
                            mList.get(position).sortmethod = 0;
                        }
                    } else
                    {
                        mList.get(position).sortmethod = 0;
                        selectedSortType = position;
                    }
                    if (position != 0)
                    { // show order in edittext except none
                        // position
                        if (mList.get(position).sortmethod == 0)
                        {
                            ed.setText(mList.get(position).sortattribute + " (Ascending)");
                            selectedSortMethod = 0;
                        } else
                        {
                            ed.setText(mList.get(position).sortattribute + " (Descending )");
                            selectedSortMethod = 1;
                        }
                    } else
                    {
                        ed.setText(getString(R.string.none));
                        if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                        {
                            myStockList.clear();
                            myStockadapter.notifyDataSetChanged();
                        } else
                        {
                            groupStockList.clear();
                            groupStockAdapter.notifyDataSetChanged();
                        }
                    }
                    sortadapter.notifyDataSetChanged();
                   /* if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                    {
                        myStockList.clear();
                        if (TextUtils.isEmpty(edKeyword.getText().toString()))
                        {
                            selectedPageNumber = 0;
                            getVehicleList(selectedPageNumber, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));

                        } else
                        {
                            selectedSearchPageNumber = 0;
                            searchVehicleByKeyword(edKeyword.getText().toString(), selectedSearchPageNumber, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                        }
                    } else
                    {
                        groupStockList.clear();
                        if (TextUtils.isEmpty(edKeyword.getText().toString()))
                        {
                            selectedPageNumber = 0;
                            getVehicleList(selectedPageNumber1, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));

                        } else
                        {
                            selectedSearchPageNumber1 = 0;
                            searchVehicleByKeyword(edKeyword.getText().toString(), selectedSearchPageNumber1, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                        }
                    }*/

                } else if (mView.getId() == R.id.edSortByVehicles)
                {
                    vehiclePopUpselection(position, true);
                    selectedPosition = position;
                }
            }
        });
    }

    private void setDataFromPreviouslyCalledWebservice()
    {
        if (opertionType == FOR_MY_STOCK_USED)
        {
            tablerow_sortby.setVisibility(View.VISIBLE);
            llayout_make_model_variant.setVisibility(View.GONE);
            edKeyword.setVisibility(View.VISIBLE);

            lvMyStock.setVisibility(View.VISIBLE);
            lvGroupStock.setVisibility(View.GONE);
            lvVarientList.setVisibility(View.GONE);
            myStockadapter = new VehicleDetailsAdapter(getActivity(), myStockList, opertionType);
            lvMyStock.setAdapter(myStockadapter);
        } else if (opertionType == FOR_MY_STOCK_USED_NEW)
        {
            tablerow_sortby.setVisibility(View.GONE);
            llayout_make_model_variant.setVisibility(View.GONE);
            edKeyword.setVisibility(View.VISIBLE);

            lvMyStock.setVisibility(View.VISIBLE);
            lvGroupStock.setVisibility(View.GONE);
            lvVarientList.setVisibility(View.GONE);
            myStockadapter = new VehicleDetailsAdapter(getActivity(), myStockList, opertionType);
            lvMyStock.setAdapter(myStockadapter);

        } else if (opertionType == FOR_GROUP_STOCK_USED)
        {
            tablerow_sortby.setVisibility(View.GONE);
            llayout_make_model_variant.setVisibility(View.GONE);
            edKeyword.setVisibility(View.VISIBLE);

            lvMyStock.setVisibility(View.GONE);
            lvGroupStock.setVisibility(View.VISIBLE);
            lvVarientList.setVisibility(View.GONE);
            groupStockAdapter = new VehicleDetailsAdapter(getActivity(), groupStockList, opertionType);
            lvGroupStock.setAdapter(groupStockAdapter);
        } else if (opertionType == FOR_GROUP_STOCK_USED_NEW)
        {
            tablerow_sortby.setVisibility(View.GONE);
            llayout_make_model_variant.setVisibility(View.GONE);
            edKeyword.setVisibility(View.VISIBLE);

            lvMyStock.setVisibility(View.GONE);
            lvGroupStock.setVisibility(View.VISIBLE);
            lvVarientList.setVisibility(View.GONE);
            groupStockAdapter = new VehicleDetailsAdapter(getActivity(), groupStockList, opertionType);
            lvGroupStock.setAdapter(groupStockAdapter);
        } else
        {
            tablerow_sortby.setVisibility(View.GONE);
            llayout_make_model_variant.setVisibility(View.VISIBLE);
            edKeyword.setVisibility(View.GONE);

            lvMyStock.setVisibility(View.GONE);
            lvGroupStock.setVisibility(View.GONE);
            lvVarientList.setVisibility(View.VISIBLE);
            EBrochureVariantAdapter variantAdapter = new EBrochureVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
            lvVarientList.setAdapter(variantAdapter);
        }
    }

    private void vehiclePopUpselection(int position, boolean isCallwebservice)
    {
        switch (position)
        {
            case 0:
                selectedSortType = 1;
                selectedSortMethod = 0;
                edKeyword.setText("");

                // show order in edittext except none
                // position
                tablerow_sortby.setVisibility(View.VISIBLE);
                llayout_make_model_variant.setVisibility(View.GONE);
                edKeyword.setVisibility(View.VISIBLE);

                edSordBy.setText(getResources().getString(R.string.price_ascending));

                //ListView Visibility
                lvMyStock.setVisibility(View.VISIBLE);
                lvGroupStock.setVisibility(View.GONE);
                lvVarientList.setVisibility(View.GONE);
                //  btnList.setVisibility(View.GONE);

                edSortByVehicles.setText(sortbylistVehicles.get(position).sortattribute);

                selectedStatusId = 1;
                opertionType = FOR_MY_STOCK_USED;
                //	if (opertionType!=FOR_GROUP_STOCK){
                lastFilterMystock = getSortString(selectedSortType, selectedSortMethod);
                break;
            case 1:
                selectedSortType = 1;
                selectedSortMethod = 0;
                edKeyword.setText("");
                tablerow_sortby.setVisibility(View.GONE);
                llayout_make_model_variant.setVisibility(View.GONE);

                //   edSordBy.setText(getResources().getString(R.string.none));

                //ListView Visibility
                lvMyStock.setVisibility(View.VISIBLE);
                lvGroupStock.setVisibility(View.GONE);
                lvVarientList.setVisibility(View.GONE);
                //   btnList.setVisibility(View.GONE);
                edKeyword.setVisibility(View.VISIBLE);

                opertionType = FOR_MY_STOCK_USED_NEW;

                edSortByVehicles.setText(sortbylistVehicles.get(position).sortattribute);
                break;
            case 2:
                selectedSortType = 1;
                selectedSortMethod = 0;
                edKeyword.setText("");
                tablerow_sortby.setVisibility(View.GONE);
                llayout_make_model_variant.setVisibility(View.VISIBLE);
                opertionType = 100;
                // edSordBy.setText(getResources().getString(R.string.none));

                //ListView Visibility
                lvMyStock.setVisibility(View.GONE);
                lvGroupStock.setVisibility(View.GONE);
                lvVarientList.setVisibility(View.GONE);
                //    btnList.setVisibility(View.VISIBLE);
                edKeyword.setVisibility(View.GONE);


                edSortByVehicles.setText(sortbylistVehicles.get(position).sortattribute);
                break;
            case 3:
                selectedSortType = 1;
                selectedSortMethod = 0;
                edKeyword.setText("");
                tablerow_sortby.setVisibility(View.GONE);
                llayout_make_model_variant.setVisibility(View.GONE);

                //edSordBy.setText(getResources().getString(R.string.none));

                //ListView Visibility
                lvMyStock.setVisibility(View.GONE);
                lvGroupStock.setVisibility(View.VISIBLE);
                lvVarientList.setVisibility(View.GONE);
                //    btnList.setVisibility(View.GONE);
                edKeyword.setVisibility(View.VISIBLE);

                opertionType = FOR_GROUP_STOCK_USED;

                edSortByVehicles.setText(sortbylistVehicles.get(position).sortattribute);

                selectedStatusId = 1;
                break;
            case 4:

                selectedSortType = 1;
                selectedSortMethod = 0;
                edKeyword.setText("");
                tablerow_sortby.setVisibility(View.GONE);
                llayout_make_model_variant.setVisibility(View.GONE);

                //  edSordBy.setText(getResources().getString(R.string.none));

                //ListView Visibility
                lvMyStock.setVisibility(View.GONE);
                lvGroupStock.setVisibility(View.VISIBLE);
                lvVarientList.setVisibility(View.GONE);
                //    btnList.setVisibility(View.GONE);
                edKeyword.setVisibility(View.VISIBLE);

                opertionType = FOR_GROUP_STOCK_USED_NEW;

                edSortByVehicles.setText(sortbylistVehicles.get(position).sortattribute);
                break;
        }

        myStockList.clear();
        myStockadapter.notifyDataSetChanged();
        groupStockList.clear();
        groupStockAdapter.notifyDataSetChanged();

       /* if (position != 2 && isCallwebservice)
            getWebServiceCall();*/

        // To show the lvVarientList
        if (position == 2 && isCallwebservice == false)
        {
            lvGroupStock.setVisibility(View.GONE);
            lvMyStock.setVisibility(View.GONE);
            lvVarientList.setVisibility(View.VISIBLE);

            if (variantList != null)
            {
                EBrochureVariantAdapter variantAdapter = new EBrochureVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
                lvVarientList.setAdapter(variantAdapter);
            }
        }
    }

    protected boolean isInList(Vehicle vehicle, ArrayList<Vehicle> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getID() == vehicle.getID())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_CANCELED)
        {
            return;
        }
        switch (requestCode)
        {
            case PICK_FROM_CAMERA:
                Intent intent = new Intent(getActivity(), ImageCropperActivity.class);
                if (fileUri != null)
                    intent.putExtra("filepath", fileUri.getPath());
                intent.putExtra("isCamera", true);
                startActivityForResult(intent, CROP_IMAGE);
                break;

            case PICK_FROM_GALLERY:
                if (resultCode == Activity.RESULT_OK)
                {
                    String imagePathToCropActivity;
                    final boolean isCamera;
                    if (data == null)
                    {
                        isCamera = true;
                    } else
                    {
                        final String action = data.getAction();
                        if (action == null)
                        {
                            isCamera = false;
                        } else
                        {
                            isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }
                    Uri selectedImageUri;
                    if (isCamera)
                    {
                        selectedImageUri = outputFileUri;
                    } else
                    {
                        selectedImageUri = data == null ? null : data.getData();
                    }
                    imagePathToCropActivity = Helper.getPath(selectedImageUri, getActivity());

                    if (TextUtils.isEmpty(imagePathToCropActivity))
                    {
                        imagePathToCropActivity = Helper.getPath(selectedImageUri, getActivity());
                    }
                    Intent intentGallery = new Intent(getActivity(), ImageCropperActivity.class);
                    if (imagePathToCropActivity != null && !imagePathToCropActivity.isEmpty())
                    {
                        intentGallery.putExtra("filepath", imagePathToCropActivity);
                    } else
                    {
                        imagePathToCropActivity = Helper.getImagePathFromGalleryAboveKitkat(getActivity(), selectedImageUri);
                        if (imagePathToCropActivity == null)
                        {
                            Helper.showCropDialog(getString(R.string.select_image_from_device_folders), getActivity());
                            return;
                        }
                        intentGallery.putExtra("filepath", imagePathToCropActivity);
                    }
                    intentGallery.putExtra("isCamera", false);
                    startActivityForResult(intentGallery, CROP_IMAGE);
                }
                break;

            case CROP_IMAGE:
                filePath = data.getStringExtra("filepath");
                updateProfileImage();

               /* CustomDialogManager.showOkDialog(HomeScreenActivity.this, getString(R.string.photo_was_saved_successfully), new DialogListener() {
                    @Override
                    public void onButtonClicked(int type) {

                    }
                });*/
                break;


        }
        super.onActivityResult(requestCode, resultCode, data);
        if (stocksDetailFragment != null)
            stocksDetailFragment.onActivityResult(requestCode, resultCode, data);
        if (stockListDetailsFragment != null)
            stockListDetailsFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Select Vehicle");

        if (getArguments().getString("fromFragment").equals("E-Brochure"))
        {
            if (AppSession.getImageDetails(getActivity()).equals("true"))
            {
                showImageDetails();
            } else
            {
                hideImageDetails();
            }
        }
    }

    /*
     * Model to add sort item
     */
    private class SortItem
    {
        String sortattribute;
        int sortmethod;

        public SortItem(String sortattribute, int sortmethod)
        {
            super();
            this.sortattribute = sortattribute;
            this.sortmethod = sortmethod;
        }
    }

    /*
    * Function to get sort string to be passed to web service
    * Parameters - sort attribute selected
    * 			  - Ascending or descending sort*/
    private String getSortString(int position, int method)
    {
        switch (position)
        {
            case 0:
                if (method == 1)
                    return "price:desc";
                else
                    return "price:asc";
            case 1:
                if (method == 1)
                    return "price:desc";
                else
                    return "price:asc";
            case 2:
                if (method == 1)
                    return "mileage:desc";
                else
                    return "mileage:asc";
            case 3:
                if (method == 1)
                    return "usedyear:desc";
                else
                    return "usedyear:asc";
            default:
                return "";
        }
    }

   /* *//*
     * Function to get sort string to be passed to web service
     * Parameters - sort attribute selected
     * 			  - Ascending or descending sort*//*
    private String getSortString(int position, int method)
    {
        switch (position)
        {
            case 0:
                return "";
            case 1:
                if (method == 1)
                    return "age:desc";
                else
                    return "age:asc";
            case 2:
                if (method == 1)
                    return "comments:desc";
                else
                    return "comments:asc";
            case 3:
                if (method == 1)
                    return "extras:desc";
                else
                    return "extras:asc";
            case 4:
                if (method == 1)
                    return "mileage:desc";
                else
                    return "mileage:asc";
            case 5:
                if (method == 1)
                    return "photos:desc";
                else
                    return "photos:asc";
            case 6:
                if (method == 1)
                    return "price:desc";
                else
                    return "price:asc";
            case 7:
                if (method == 1)
                    return "stockcode:desc";
                else
                    return "stockcode:asc";

            case 8:
                if (method == 1)
                    return "usedyear:desc";
                else
                    return "usedyear:asc";
            default:
                return "";
        }
    }*/

    /*private void getClientCorprateGroups()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<GetClientCorprateGroups xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
            soapMessage.append("<ClientID>" + DataManager.getInstance().user.getDefaultClient().getId() + "</ClientID>");
            soapMessage.append("</GetClientCorprateGroups>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");


            VollyResponseListener listener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Helper.showToast(getString(R.string.error_getting_data), getActivity());
                    VolleyLog.e("Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {

                        return;
                    }
                    Helper.Log("GetClientCorprateGroups", "" + response);
                    if (response.contains("<Group>"))
                    {
                        isGroupAssociated = true;
                    } else
                    {
                        isGroupAssociated = false;
                    }
                    if (isGroupAssociated)
                    {
                        tvNoGroupTab.setVisibility(View.GONE);
                    } else
                    {
                        tvNoGroupTab.setVisibility(View.VISIBLE);
                    }
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IAuthenticate/GetClientCorprateGroups", listener);
            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }
*/

    /*
    * Webservice integration to call vehicle list Parameter- pageNo for index
     * of pages
     */
    private synchronized void getVehicleList(final int pageNo, int statusId, String sortAttr)
    {
        hideKeyboard();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("statusID", statusId, Integer.class));
            parameterList.add(new Parameter("pageSize", 10, Integer.class));
            parameterList.add(new Parameter("pageNumber", pageNo, Integer.class));
            if (!TextUtils.isEmpty(sortAttr))
            {
                parameterList.add(new Parameter("sort", sortAttr, String.class));
            } else
            {
                if (getArguments().getString("fromFragment").equals("PhotosAndExtras"))
                    parameterList.add(new Parameter("sort", "photos|extras", String.class));
                else
                    parameterList.add(new Parameter("sort", "friendlyname", String.class));
            }
//		opertionType = webserviceType;
            // create web service inputs

            DataInObject inObj = new DataInObject();
            if (opertionType == FOR_MY_STOCK_USED)
            {
                parameterList.add(new Parameter("newUsed", "used", String.class));
                inObj.setMethodname("ListVehiclesByStatusXML");
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVehiclesByStatusXML");
            } else if (opertionType == FOR_MY_STOCK_USED_NEW)
            {
                parameterList.add(new Parameter("newUsed", "new", String.class));
                inObj.setMethodname("ListVehiclesByStatusXML");
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVehiclesByStatusXML");
            } else if (opertionType == FOR_GROUP_STOCK_USED)
            {
                parameterList.add(new Parameter("newUsed", "used", String.class));
                inObj.setMethodname("ListGroupVehiclesByStatusXML");
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListGroupVehiclesByStatusXML");
            } else if (opertionType == FOR_GROUP_STOCK_USED_NEW)
            {
                parameterList.add(new Parameter("newUsed", "new", String.class));
                inObj.setMethodname("ListGroupVehiclesByStatusXML");
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListGroupVehiclesByStatusXML");
            }

            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showLoadingProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    if (result != null)
                    {
                        Helper.Log("soap response", result.toString());
                        try
                        {

                            SoapObject obj = (SoapObject) result;
                            SoapObject inner = (SoapObject) obj.getPropertySafely("stockList", "default");
                            Vehicle vehicle;

                            for (int i = 0; i < inner.getPropertyCount(); i++)
                            {
                                vehicle = new Vehicle();
                                SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
                                if (vehicleObj.hasProperty("usedVehicleStockID"))
                                {
                                    vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedVehicleStockID", "0")));
                                    if (vehicleObj.getPropertySafelyAsString("EBrochureFlag", "0").equalsIgnoreCase("false"))
                                        vehicle.setEBrochureFlag(false);
                                    else
                                        vehicle.setEBrochureFlag(true);
                                    vehicle.setInternalNote(vehicleObj.getPropertySafelyAsString("internalNote", ""));
                                    vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("vehicleName", ""));
                                    vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("mileage", "0")));
                                    vehicle.setColour(vehicleObj.getPropertySafelyAsString("colour", ""));
                                    vehicle.setDepartment(vehicleObj.getPropertySafelyAsString("department", ""));
                                    String retailPrice = vehicleObj.getPropertySafelyAsString("price", "0.00").replace(",",".");
                                    vehicle.setRetailPrice(Float.parseFloat(retailPrice));

                                    String tradePrice = vehicleObj.getPropertySafelyAsString("tradePrice", "0.00").replace(",",".");
                                    vehicle.setTradePrice(Float.parseFloat(tradePrice));

                                    vehicle.setExpires(vehicleObj.getPropertySafelyAsString("age", ""));
                                    vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedYear", "0")));
                                    vehicle.setStockNumber(vehicleObj.getPropertySafelyAsString("stockCode", ""));
                                    vehicle.setRegNumber(vehicleObj.getPropertySafelyAsString("registration", ""));
                                    vehicle.setComments(vehicleObj.getPropertySafelyAsString("comments", ""));
                                    vehicle.setExtras(vehicleObj.getPropertySafelyAsString("extras", ""));
                                    vehicle.setNumOfPhotos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("photos", "0")));
                                    vehicle.setNumOfVideos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("videos", "0")));
                                    vehicle.setTotal(Integer.parseInt(vehicleObj.getPropertySafelyAsString("total", "0")));

                                    myStock = Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalActive", "0"));
                                    groupStock = Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalInvalid", "0"));
                                    if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                                    {
                                        if (!isInList(vehicle, myStockList))
                                        {
                                            myStockList.add(vehicle);
                                        }
                                    } else
                                    {
                                        if (!isInList(vehicle, groupStockList))
                                        {
                                            groupStockList.add(vehicle);
                                        }
                                    }
                                } else
                                {
                                    SoapObject totalObject = (SoapObject) inner.getPropertySafely("Totals", "default");
                                    if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_GROUP_STOCK_USED)
                                    {
                                        myStock = Integer.parseInt(totalObject.getPropertySafelyAsString("totalUsed", "0"));
                                        groupStock = Integer.parseInt(totalObject.getPropertySafelyAsString("totalUsed", "0"));
                                    } else
                                    {
                                        myStock = Integer.parseInt(totalObject.getPropertySafelyAsString("totalNew", "0"));
                                        groupStock = Integer.parseInt(totalObject.getPropertySafelyAsString("totalNew", "0"));
                                    }
                                }
                            }

                            Helper.Log("list size", myStockList.size() + "");
                            if (inner.getPropertyCount() == 1)
                            {
                                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found));
                                myStockList.clear();
                                updateSegmentedControl();
                            }
                            if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                            {
                                if (!myStockList.isEmpty())
                                {
                                    total_no_of_records_mystock = myStockList.get(0).getTotal();
                                    Helper.Log("total my stock", total_no_of_records_mystock + "");
                                }
                                if (pageNo == 0)
                                {
                                    myStockadapter = new VehicleDetailsAdapter(getActivity(), myStockList, opertionType);
                                    lvMyStock.setAdapter(myStockadapter);
                                } else
                                {
                                    myStockadapter.notifyDataSetChanged();
                                }

                            } else
                            {
                                if (!groupStockList.isEmpty())
                                {
                                    total_no_of_records_groupstock = groupStockList.get(0).getTotal();
                                    Helper.Log("total groupStock", total_no_of_records_groupstock + "");
                                }
                                if (pageNo == 0)
                                {
                                    groupStockAdapter = new VehicleDetailsAdapter(getActivity(), groupStockList, opertionType);
                                    lvGroupStock.setAdapter(groupStockAdapter);
                                } else
                                {
                                    groupStockAdapter.notifyDataSetChanged();
                                }


                            }
                            updateSegmentedControl();
                            hideProgressDialog();
                        } catch (Exception e)
                        {
                            hideProgressDialog();
                            e.printStackTrace();
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialogAutoCancel(getActivity(), getString(R.string.no_record_found));
                        if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                        {
                            myStockList.clear();
                        } else
                        {
                            groupStockList.clear();
                        }
                        resetSegmentedControl();
                        isAtEnd = true;
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    /**
     * web service integration to search vehicle by keyword Parameter 1- Keyword
     * to be searched 2- Page number
     */
    private synchronized void searchVehicleByKeyword(String keyword, int pageNumber, int statusId, String sortAttr)
    {
        hideKeyboard();
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), Integer.class));
            parameterList.add(new Parameter("keyword", keyword, String.class));
            parameterList.add(new Parameter("pageSize", 10, Integer.class));
            parameterList.add(new Parameter("pageNumber", pageNumber, Integer.class));
            parameterList.add(new Parameter("Status", statusId, Integer.class));
            if (!TextUtils.isEmpty(sortAttr))
                parameterList.add(new Parameter("sort", sortAttr, String.class));
            else
            {
                if (getArguments().getString("fromFragment").equals("PhotosAndExtras"))
                    parameterList.add(new Parameter("sort", "photos|extras", String.class));
                else
                    parameterList.add(new Parameter("sort", "friendlyname", String.class));
            }


            // create web service inputs
            DataInObject inObj = new DataInObject();
            if (opertionType == FOR_MY_STOCK_USED)
            {
                parameterList.add(new Parameter("newUsed", "used", String.class));
                inObj.setMethodname("ListVehiclesByKeywordStatusXML");
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVehiclesByKeywordStatusXML");
            } else if (opertionType == FOR_MY_STOCK_USED_NEW)
            {
                parameterList.add(new Parameter("newUsed", "new", String.class));
                inObj.setMethodname("ListVehiclesByKeywordStatusXML");
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListVehiclesByKeywordStatusXML");
            } else if (opertionType == FOR_GROUP_STOCK_USED)
            {
                parameterList.add(new Parameter("newUsed", "used", String.class));
                inObj.setMethodname("ListGroupVehiclesByKeywordStatusXML");
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListGroupVehiclesByKeywordStatusXML");
            } else if (opertionType == FOR_GROUP_STOCK_USED_NEW)
            {
                parameterList.add(new Parameter("newUsed", "new", String.class));
                inObj.setMethodname("ListGroupVehiclesByKeywordStatusXML");
                inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListGroupVehiclesByKeywordStatusXML");
            }

            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showLoadingProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                @Override
                public void onTaskComplete(Object result)
                {
                    if (result != null)
                    {
                        Helper.Log("soap response", result.toString());
                        try
                        {
                            SoapObject obj = (SoapObject) result;
                            SoapObject inner = (SoapObject) obj.getPropertySafely("stockList", "default");
                            Vehicle vehicle;
                            for (int i = 0; i < inner.getPropertyCount(); i++)
                            {
                                vehicle = new Vehicle();

                                SoapObject vehicleObj = (SoapObject) inner.getProperty(i);
                                if (vehicleObj.hasProperty("usedVehicleStockID"))
                                {
                                    vehicle.setInternalNote(vehicleObj.getPropertySafelyAsString("internalNote", ""));
                                    vehicle.setID(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedVehicleStockID", "0")));
                                    vehicle.setFriendlyName(vehicleObj.getPropertySafelyAsString("vehicleName", ""));
                                    vehicle.setMileage(Integer.parseInt(vehicleObj.getPropertySafelyAsString("mileage", "0")));
                                    vehicle.setColour(vehicleObj.getPropertySafelyAsString("colour", ""));
                                    vehicle.setDepartment(vehicleObj.getPropertySafelyAsString("department", ""));
                                    String price = vehicleObj.getPropertySafelyAsString("price", "0.00").replace(",",".");
                                    vehicle.setRetailPrice(Float.parseFloat(price));

                                    String tradePrice = vehicleObj.getPropertySafelyAsString("tradePrice", "0.00").replace(",",".");
                                    vehicle.setTradePrice(Float.parseFloat(tradePrice));
                                    vehicle.setExpires(vehicleObj.getPropertySafelyAsString("age", ""));
                                    vehicle.setYear(Integer.parseInt(vehicleObj.getPropertySafelyAsString("usedYear", "0")));
                                    vehicle.setStockNumber(vehicleObj.getPropertySafelyAsString("stockCode", ""));
                                    vehicle.setRegNumber(vehicleObj.getPropertySafelyAsString("registration", ""));
                                    vehicle.setComments(vehicleObj.getPropertySafelyAsString("comments", ""));
                                    vehicle.setExtras(vehicleObj.getPropertySafelyAsString("extras", ""));
                                    vehicle.setNumOfPhotos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("photos", "0")));
                                    vehicle.setNumOfVideos(Integer.parseInt(vehicleObj.getPropertySafelyAsString("videos", "0")));
                                    vehicle.setTotal(Integer.parseInt(vehicleObj.getPropertySafelyAsString("total", "0")));

                                    if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_GROUP_STOCK_USED)
                                    {
                                        myStock = Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalUsed", "0"));
                                        groupStock = Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalUsed", "0"));
                                    } else
                                    {
                                        myStock = Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalNew", "0"));
                                        groupStock = Integer.parseInt(vehicleObj.getPropertySafelyAsString("totalNew", "0"));
                                    }
                                    if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                                    {
                                        if (!isInList(vehicle, myStockList))
                                        {
                                            myStockList.add(vehicle);
                                        }
                                    } else
                                    {
                                        if (!isInList(vehicle, groupStockList))
                                        {
                                            groupStockList.add(vehicle);
                                        }
                                    }
                                } else
                                {
                                    SoapObject totalObject = (SoapObject) inner.getPropertySafely("Totals", "default");
                                    if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_GROUP_STOCK_USED)
                                    {
                                        myStock = Integer.parseInt(totalObject.getPropertySafelyAsString("totalUsed", "0"));
                                        groupStock = Integer.parseInt(totalObject.getPropertySafelyAsString("totalUsed", "0"));
                                    } else
                                    {
                                        myStock = Integer.parseInt(totalObject.getPropertySafelyAsString("totalNew", "0"));
                                        groupStock = Integer.parseInt(totalObject.getPropertySafelyAsString("totalNew", "0"));
                                    }
                                }
                            }
                            Helper.Log("list size", myStockList.size() + "");

                            if (inner.getPropertyCount() == 1)
                            {
                                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_record_found));
                                myStockList.clear();
                                groupStockList.clear();
                                resetSegmentedControl();
                            }
                            if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                            {
                                if (!myStockList.isEmpty())
                                {
                                    total_no_of_records_mystock = myStockList.get(0).getTotal();
                                    Helper.Log("total my stock", total_no_of_records_mystock + "");
                                }
                                myStockadapter.notifyDataSetChanged();
                            } else
                            {
                                if (!groupStockList.isEmpty())
                                {
                                    total_no_of_records_groupstock = groupStockList.get(0).getTotal();
                                    Helper.Log("total groupStock", total_no_of_records_groupstock + "");
                                }

                                groupStockAdapter.notifyDataSetChanged();
                            }
                            updateSegmentedControl();
                            hideProgressDialog();
                        } catch (Exception e)
                        {
                            hideProgressDialog();
                            e.printStackTrace();
                        }
                    } else
                    {
                        hideProgressDialog();
                        CustomDialogManager.showOkDialogAutoCancel(getActivity(), getString(R.string.no_record_found));
                        if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
                        {
                            myStockList.clear();
                        } else
                        {
                            groupStockList.clear();
                        }
                        resetSegmentedControl();

                        isAtEnd = true;
                    }
                }
            }).execute();
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

	/*private void displayShowcaseView(){
        if(!ShowcaseSessions.isSessionAvailable(getActivity(), ListVehicleFragment.class.getSimpleName())){
			ArrayList<TargetView> viewList= new ArrayList<TargetView>();
			viewList.add(new TargetView(lvVehicleDetails, ShowCaseType.SwipeUpDown, getString(R.string.scroll_up_and_down)));
			ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
			showcaseView.setShowcaseView(viewList);

			((ViewGroup)getActivity().getWindow().getDecorView()).addView(showcaseView);
			ShowcaseSessions.saveSession(getActivity(), ListVehicleFragment.class.getSimpleName());
		}
	}*/

    private void getMakeList(final boolean show)
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), String.class));
            parameterList.add(new Parameter("year", "" + Calendar.getInstance().get(Calendar.YEAR), Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListActiveMakesXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListActiveMakesXML");
            inObj.setUrl(Constants.STOCK_WEBSERVICE_URL);
            inObj.setParameterList(parameterList);

            // Network call
            showProgressDialog();
            new WebServiceTask(getActivity(), inObj, false, new TaskListener()
            {

                // Network callback
                @Override
                public void onTaskComplete(Object result)
                {
                    hideProgressDialog();
                    makeList = new ArrayList<SmartObject>();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Makes");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject makeObj = (SoapObject) inner.getProperty(i);
                            String makeid = makeObj.getPropertySafelyAsString("makeID", "0");
                            String makename = makeObj.getPropertySafelyAsString("makeName", "-");

                            makeList.add(i, new SmartObject(Integer.parseInt(makeid), makename));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (show)
                        {
                            if (!makeList.isEmpty())
                                showListPopUp(edMake, makeList);
                            else
                                Helper.showToast(getString(R.string.no_make), getActivity());
                        }
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void showListPopUp(final View mView, final ArrayList list)
    {
        try
        {
            final EditText ed = (EditText) mView;
            final String edtData = ed.getText().toString().trim();
            boolean showSearch = false;
            if (mView.getId() == R.id.edMake)
                showSearch = true;
            else
                showSearch = false;

            Helper.showDropDownSearch(showSearch, mView, new ArrayAdapter(getActivity(), R.layout.list_item_text2, R.id.tvText, list), new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    SmartObject smartObject = (SmartObject) parent.getItemAtPosition(position);
                    ed.setText(smartObject.getName() + "");
                    if (edtData.equals(ed.getText().toString().trim()))
                        return;
                    if (mView.getId() == R.id.edMake)
                    {
                        // if make is clicked second
                        // time
                        if (modelList != null) // remove modelList and variantL
                            // items
                            modelList = null;
                        if (variantList != null)
                            variantList = null;
                        // btnList.setText(R.string.select_varient); // set default text
                        edModel.setText(R.string.select_model);
                        selectedVariantId = 0;
                        selectedModelId = 0;
                        variant = null;
                        // set default text
                        selectedMakeId = smartObject.getId();

                    } else if (mView.getId() == R.id.edModel)
                    { // if model is clicked remove variant list items
                        if (variantList != null)
                            variantList = null;
                        //  edVariant.setText(R.string.select_varient);
                        selectedVariantId = 0;
                        variant = null;
                        selectedModelId = smartObject.getId();
                    } else if (mView.getId() == R.id.edVariant)
                    {
                        // if variant is
                        selectedVariantId = variantList.get(position).getVariantId();
                        if (selectedVariantId != 0)
                            variant = variantList.get(position);
                    }
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getModelList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("year", "" + Calendar.getInstance().get(Calendar.YEAR), Integer.class));
            parameterList.add(new Parameter("makeID", selectedMakeId, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListActiveModelsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListActiveModelsXML");
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
                    modelList = new ArrayList<SmartObject>();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Models");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject modelObj = (SoapObject) inner.getProperty(i);
                            String modelid = modelObj.getPropertySafelyAsString("modelID", "0");
                            String modelname = modelObj.getPropertySafelyAsString("modelName", "-");

                            modelList.add(i, new SmartObject(Integer.parseInt(modelid), modelname));
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (!modelList.isEmpty())
                            showListPopUp(edModel, modelList);
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getVariantList()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            // Add parameters to request in arraylist
            ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
            parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
            parameterList.add(new Parameter("clientID", DataManager.getInstance().user.getDefaultClient().getId(), String.class));
            parameterList.add(new Parameter("year", "" + Calendar.getInstance().get(Calendar.YEAR), Integer.class));
            parameterList.add(new Parameter("modelID", selectedModelId, Integer.class));

            // create web service inputs
            DataInObject inObj = new DataInObject();
            inObj.setMethodname("ListActiveVariantsXML");
            inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
            inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IStockService/ListActiveVariantsXML");
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
                    variantList = new ArrayList<Variant>();
                    try
                    {
                        SoapObject outer = (SoapObject) result;
                        SoapObject inner = (SoapObject) outer.getPropertySafely("Variants");
                        for (int i = 0; i < inner.getPropertyCount(); i++)
                        {
                            SoapObject variantObj = (SoapObject) inner.getProperty(i);

                            String values = variantObj.getPropertySafelyAsString("price", "0.00").replaceAll("\\s", "");

                            if (values.equalsIgnoreCase(""))
                            {
                                variantList.add(new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("variantID", "0")), variantObj.getPropertySafelyAsString("meadCode", "0"), variantObj
                                        .getPropertySafelyAsString("friendlyName", "-"), variantObj.getPropertySafelyAsString("variantName", "-"), 0, variantObj.getPropertySafelyAsString("MinDate", "-"),
                                        variantObj.getPropertySafelyAsString("MaxDate", "-"), Boolean.parseBoolean(variantObj.getPropertySafelyAsString("EBrochureFlag", "-"))));
                            } else
                            {
                                variantList.add(new Variant(Integer.parseInt(variantObj.getPropertySafelyAsString("variantID", "0")), variantObj.getPropertySafelyAsString("meadCode", "0"), variantObj
                                        .getPropertySafelyAsString("friendlyName", "-"), variantObj.getPropertySafelyAsString("variantName", "-"), Float.parseFloat(variantObj.getPropertySafelyAsString("price", "0.00").replaceAll("\\s", "")), variantObj.getPropertySafelyAsString("MinDate", "-"),
                                        variantObj.getPropertySafelyAsString("MaxDate", "-"), Boolean.parseBoolean(variantObj.getPropertySafelyAsString("EBrochureFlag", "-"))));
                            }
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (!variantList.isEmpty())
                        {
                            lvGroupStock.setVisibility(View.GONE);
                            lvMyStock.setVisibility(View.GONE);
                            lvVarientList.setVisibility(View.VISIBLE);

                            EBrochureVariantAdapter variantAdapter = new EBrochureVariantAdapter(getActivity(), R.layout.list_item_variant, variantList);
                            lvVarientList.setAdapter(variantAdapter);
                            /*Helper.showDropDown(btnList, variantAdapter, new OnItemClickListener()
                            {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                                {
                                    btnList.setText(variantList.get(position).getVariantName() + "");
                                    selectedVariantId = variantList.get(position).getVariantId();
                                    if (selectedVariantId != 0)
                                        variant = variantList.get(position);
                                }
                            });*/
                        }
                    }
                }
            }).execute();
        } else
        {
            CustomDialogManager.showOkDialog(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getWebServiceCall()
    {
        selectedMakeId = 0;
        selectedModelId = 0;
        edMake.setText(getResources().getString(R.string.select_make));
        edModel.setText(getResources().getString(R.string.select_model));

        keyword = edKeyword.getText().toString().trim();

        if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
        {
            myStockList.clear();
        } else
        {
            groupStockList.clear();
        }

        if (!TextUtils.isEmpty(keyword))
        {
            if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
            {
                selectedSearchPageNumber = 0;
                searchVehicleByKeyword(keyword, selectedSearchPageNumber, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                selectedMethod = selectedSortMethod;
                selectedSort = selectedSortType;
            } else
            {
                selectedSearchPageNumber1 = 0;
                searchVehicleByKeyword(keyword, selectedSearchPageNumber1, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                selectedMethod = selectedSortMethod;
                selectedSort = selectedSortType;
            }
        } else
        {
            if (opertionType == FOR_MY_STOCK_USED || opertionType == FOR_MY_STOCK_USED_NEW)
            {
                selectedSearchPageNumber = 0;
                selectedPageNumber = 0;
                getVehicleList(selectedPageNumber, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                selectedMethod = selectedSortMethod;
                selectedSort = selectedSortType;
            } else
            {
                selectedSearchPageNumber1 = 0;
                selectedPageNumber1 = 0;
                getVehicleList(selectedPageNumber1, selectedStatusId, getSortString(selectedSortType, selectedSortMethod));
                selectedMethod = selectedSortMethod;
                selectedSort = selectedSortType;
            }
        }
    }

    public void getImageFromGalleryOrCamera(final Context context)
    {
        final String[] items = new String[]{getString(R.string.camera), getString(R.string.select_from_gallery)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.please_select_image_source));
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.setAdapter(adapter, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                if (which == 0)
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = Helper.getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                    intent = null;
                } else
                {
                    int versionCode = Build.VERSION.SDK_INT;
                    if (versionCode > Build.VERSION_CODES.KITKAT)
                    {
                        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), getString(R.string.upload_image)), PICK_FROM_GALLERY);
                    } else
                    {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICK_FROM_GALLERY);
                    }
                }

            }
        });
        final AlertDialog ad = builder.create();
        ad.setOnCancelListener(new DialogInterface.OnCancelListener()
        {

            @Override
            public void onCancel(DialogInterface dialog)
            {

            }
        });
        ad.show();
    }


    private void setImageToImageView(ImageView ivProfilePic, String photoPath)
    {
        Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
        if (imageBitmap != null)
        {
            ivUserProfilePhoto.setBackgroundDrawable(null);
            ivProfilePic.setImageBitmap(imageBitmap);
        }


    }


    //Webservice implementation to check HasProfileImage
    private void hasProfileImage()
    {
        // Add parameters to request in arraylist
        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));

        // create web service inputs
        DataInObject inObj = new DataInObject();
        inObj.setMethodname("HasProfileImage");
        inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
        inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/HasProfileImage");
        inObj.setUrl(Constants.EBROCHURE_WEBSERVICE_URL);
        inObj.setParameterList(parameterList);

        showProgressDialog();
        new WebServiceTask(getActivity(), inObj, false, new TaskListener()
        {
            @Override
            public void onTaskComplete(Object response)
            {
                hideProgressDialog();
                //on response set the flag to true/false as per response
                try
                {
                    SoapObject outer = (SoapObject) response;
                    SoapObject inner = (SoapObject) outer.getPropertySafely("ProfileImage");
                    isProfilePhotoAvailable = inner.getPropertySafelyAsString("HasImage", "0");
                    if (isProfilePhotoAvailable.equals("true"))
                    {
                        getProfileImage();
                    }
                    if (getArguments().getString("" +
                            "") != null)
                    {
                        if (getArguments().getString("fromFragment").equals("E-Brochure"))
                        {
                            if (AppSession.getImageDetails(getActivity()).equals("true"))
                            {
                                if (isProfilePhotoAvailable.equals("true"))
                                {
                                    //show Replace button
                                    tvCustomiseBroucher.setVisibility(View.GONE);
                                    btnProfilePhotoUpload.setText(getString(R.string.replace_photo_profile));
                                } else
                                {
                                    //show Personalise your eBrochure with a profile photo & Upload button
                                    tvCustomiseBroucher.setVisibility(View.VISIBLE);
                                    btnProfilePhotoUpload.setText(getString(R.string.upload_profile_picture));
                                }

                                ivUserProfilePhoto.setBackgroundDrawable(Helper.getIcon(getActivity(), Iconify.IconValue.fa_user, 40));
                            }

                        } else
                        {
                            llProfilePhotoUpload.setVisibility(View.GONE);
                            tvCustomiseBroucher.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).execute();

    }

    //Webservice implementation to Update Profile photo
    private void updateProfileImage()
    {
        String base64String = null;
        base64String = Helper.convertBitmapToBase64(filePath);

        // Add parameters to request in arraylist
        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));
        parameterList.add(new Parameter("imageFilename", Helper.getFileName(filePath), String.class));
        parameterList.add(new Parameter("base64EncodedString", base64String, String.class));

        // create web service inputs
        DataInObject inObj = new DataInObject();
        inObj.setMethodname("UpdateProfileImage");
        inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
        inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/UpdateProfileImage");
        inObj.setUrl(Constants.EBROCHURE_WEBSERVICE_URL);
        inObj.setParameterList(parameterList);

        showProgressDialog();
        new WebServiceTask(getActivity(), inObj, false, new TaskListener()
        {
            @Override
            public void onTaskComplete(Object response)
            {
                hideProgressDialog();
                //on response set image locally on Home Screen
                setImageToImageView(ivUserProfilePhoto, filePath);
                tvCustomiseBroucher.setVisibility(View.GONE);
                btnProfilePhotoUpload.setText(getString(R.string.replace_photo_profile));
                CustomDialogManager.showOkDialog(getActivity(), getString(R.string.image_updated_successfully));
            }
        }).execute();
    }

    //Webservice implementation to get Profile
    private void getProfileImage()
    {
        // Add parameters to request in arraylist
        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
        parameterList.add(new Parameter("userHash", DataManager.getInstance().user.getUserHash(), String.class));

        // create web service inputs
        DataInObject inObj = new DataInObject();
        inObj.setMethodname("GetProfileImage");
        inObj.setNamespace(Constants.TEMP_URI_NAMESPACE);
        inObj.setSoapAction(Constants.TEMP_URI_NAMESPACE + "IElectronicBrochureGeneratorService/GetProfileImage");
        inObj.setUrl(Constants.EBROCHURE_WEBSERVICE_URL);
        inObj.setParameterList(parameterList);

        showProgressDialog();
        new WebServiceTask(getActivity(), inObj, false, new TaskListener()
        {
            @Override
            public void onTaskComplete(Object response)
            {
                hideProgressDialog();
                try
                {
                    SoapObject outer = (SoapObject) response;
                    SoapObject inner = (SoapObject) outer.getPropertySafely("ProfileImage");
                    String imageUrl = inner.getPropertySafelyAsString("Image", "0");
                    ivUserProfilePhoto.setBackgroundDrawable(null);
                    ImageLoader.getInstance().clearMemoryCache();
                    ImageLoader.getInstance().clearDiskCache();
                    loader.displayImage(imageUrl, ivUserProfilePhoto, user_options);

                } catch (Exception e)
                {
                    e.printStackTrace();

                }
            }
        }).execute();

    }

    private void showImageDetails()
    {
        llProfilePhotoUpload.setVisibility(View.VISIBLE);
        viewDivider.setVisibility(View.VISIBLE);
        btnProfilePhotoUpload.setVisibility(View.VISIBLE);

    }

    private void hideImageDetails()
    {
        llProfilePhotoUpload.setVisibility(View.GONE);
        viewDivider.setVisibility(View.GONE);
        btnProfilePhotoUpload.setVisibility(View.GONE);
        tvCustomiseBroucher.setVisibility(View.GONE);
    }

}
