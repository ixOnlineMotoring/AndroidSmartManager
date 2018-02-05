package com.nw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nw.adapters.PageAdapter;
import com.nw.interfaces.FragmentListner;
import com.nw.interfaces.HomePageListener;
import com.nw.model.Client;
import com.nw.model.Impersonate;
import com.nw.model.Module;
import com.nw.model.SubModule;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.smartmanager.activity.ActivityBlog;
import com.smartmanager.activity.BuyActivity;
import com.smartmanager.activity.CustomerActivity;
import com.smartmanager.activity.NotificationActivity;
import com.smartmanager.activity.PlannerActivity;
import com.smartmanager.activity.SupportActivity;
import com.smartmanager.activity.SynopsisActivity;
import com.smartmanager.activity.VehicleActivity;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;

import java.util.ArrayList;

public class HomeScreenFragment extends BaseFragement implements OnItemClickListener {
    public boolean isHome = true;
    GridView gridView;
    TextView tvHeader;
    public PageAdapter pageAdapter;
    public static Module pageModule;
    FragmentListner fragmentListner;
    HomePageListener homePageListener;
    public int positionLevelOne, moduleposition = -1;
    public int stepCounter = 0;
    public static int Height, Width;
    public static HomeScreenFragment homeScreenFragment;
    ArrayList<SubModule> subModules;

    public static HomeScreenFragment newInstance(Module module) {
        if (homeScreenFragment == null)
            homeScreenFragment = new HomeScreenFragment();
        pageModule = module;
        return homeScreenFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gridview, container, false);
        gridView = (GridView) rootView.findViewById(R.id.expandableHeightGridView1);
        gridView.setOnItemClickListener(this);
        if (Helper.isTablet(getActivity()))
        {
            gridView.setNumColumns(4);
        } else
        {
            gridView.setNumColumns(3);
        }
        tvHeader = (TextView) rootView.findViewById(R.id.textView1);
        try
        {
            if (pageModule != null)
            {
                if (pageModule.getPages() != null)
                {
                    pageAdapter = new PageAdapter(getActivity(), pageModule.getPages());

                    gridView.setAdapter(pageAdapter);
                    if (subModules == null)
                    {
                        subModules = new ArrayList<SubModule>();
                    }
                    getImpersonationList();
                }
            }
        } catch (Exception e)
        {

            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        if (isHome)
        {
            positionLevelOne = position;
            //	moduleposition= position;
            // main menu page clicked
            if (pageAdapter.isValid(position))
            {
                stepCounter++;
                showSubPages(position);
            }
        } else
        {
            // sub menu page clicked
            if (pageAdapter.isValid(position))
            {
                if (pageAdapter.hasSubModule(position))
                {
                    // this shows grid again
                    if (DataManager.getInstance().user.getModules().get(positionLevelOne).getSubModules().size() != 0)
                    {
                        // it will shows grid again
                        stepCounter++;
                        showSubModulePages(position, DataManager.getInstance().user.getModules().get(positionLevelOne).getSubModules());
                    } else
                    {
                        // it will pages not grid
                        moduleposition = position;
                        showSubPages(position);
                    }
                } else
                {
                    // this does not grid again
                    showSubMenuOption(position);
                }
            }
        }
    }

    public void showSubMenuOption(int position) {
        if (pageAdapter.getItem(position).getName().equals("Blog Post"))
        {
            // start blog activity with blogpost option
            Intent intent = new Intent(getActivity(), ActivityBlog.class);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Delivery"))
        {
            // start blog activity with customer delivery option
            Intent intent = new Intent(getActivity(), ActivityBlog.class);
            intent.putExtra("CustomerDelivery", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Log Activity"))
        {
            // start planner activity with log fragment option
            Intent intent = new Intent(getActivity(),   PlannerActivity.class);
            intent.putExtra("fromLogFragment", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("New Task"))
        {
            // start planner activity with new tasks option
            Intent intent = new Intent(getActivity(), PlannerActivity.class);
            intent.putExtra("NewTask", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Buy"))
        {
            // start Buy activity with buy option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Buy", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("To Do's"))
        {
            // start planner activity with to do option
            Intent intent = new Intent(getActivity(), PlannerActivity.class);
            intent.putExtra("todo", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Photos & Extras"))
        {
            // start vehicle activity with photos and extras module
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("PhotosAndExtras", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Load Vehicle"))
        {
            // start Vehicle activity with load vehicle option
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("LoadVehicle", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Bidding Ended"))
        {
            // start buy activity with sell option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Bidding Ended", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Losing Bids"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Losing Bids", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Winning Bids"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Winning Bids", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Auto Bidding"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Auto Bidding", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Won"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Won", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Lost"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Lost", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Private Offers"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Private Offers", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Withdrawn"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Withdrawn", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Cancelled"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Cancelled", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Bidding Ended"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Bidding Ended", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Bids Received"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Bids Received", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Trade Vehicles"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Trade Vehicles", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Sales"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Sales", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Missing Price"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Missing Price", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Activate Retail"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Activate Retail", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Missing Info"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Missing Info", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Readiness"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Readiness", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Auctions"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Auctions", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Display"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Display", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Trade Price"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Trade Price", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Trade Partners"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Trade Partners", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("My Buyers"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("My Buyers", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("My Sellers"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("My Sellers", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Members"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Members", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Custom Msgs"))
        {
            // start buy activity with my bids option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Custom Msgs", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Tasks by Me"))
        {
            // start planner activity with tasks by me option
            Intent intent = new Intent(getActivity(), PlannerActivity.class);
            intent.putExtra("tasksByMe", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Specials"))
        {
            // start Vehicle activity with specials option
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("Specials", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Edit Stock"))
        {
            // start Vehicle activity with List option
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("Edit Stock", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Stock List"))
        {
            // start Vehicle activity with List option
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("Stock List", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("VIN Lookup"))
        {
            // start Vehicle activity with VIN option
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("VINLookup", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Wanted"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), BuyActivity.class);
            intent.putExtra("Wanted", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Scan License"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), CustomerActivity.class);
            intent.putExtra("ScanLicense", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Leads"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), CustomerActivity.class);
            intent.putExtra("Leads", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("[Notifications"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            intent.putExtra("pushNotification", true);
            startActivity(intent);

        } else if (pageAdapter.getItem(position).getName().equals("Unseen"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            intent.putExtra("Unseen", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Stock Audit"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("StockAudit", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("eBrochure"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("eBrochure", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Scan VIN"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), SynopsisActivity.class);
            intent.putExtra("Scan VIN", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Vehicle Lookup"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), SynopsisActivity.class);
            intent.putExtra("Vehicle Lookup", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Vehicle Code"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), SynopsisActivity.class);
            intent.putExtra("Vehicle Code", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Vehicle in Stock"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), SynopsisActivity.class);
            intent.putExtra("Vehicle in Stock", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("Saved Appraisals"))
        {
            // start Buy activity with wanted option
            Intent intent = new Intent(getActivity(), SynopsisActivity.class);
            intent.putExtra("Saved Appraisals", true);
            startActivity(intent);
        } else if (pageAdapter.getItem(position).getName().equals("My Leads"))
        {
            //start Customer Activity
            Intent intent = new Intent(getActivity(), CustomerActivity.class);
            intent.putExtra("My Leads", true);
            startActivity(intent);
        }
        else if (pageAdapter.getItem(position).getName().equals("Stock Summary")){
            //start Vehicle Activity
            Intent intent = new Intent(getActivity(), VehicleActivity.class);
            intent.putExtra("Stock Summary", true);
            startActivity(intent);
        }
        //Added by Asmita on 14/11/2017
        else if (pageAdapter.getItem(position).getName().equals("Support Request")){
            //start Vehicle Activity
            Intent intent = new Intent(getActivity(), SupportActivity.class);
            intent.putExtra("Support Request", true);
            startActivity(intent);
        }
    }

    // show pages from dash board
    public void showSubPages(int position) {
        isHome = false;
        if (this.fragmentListner != null)
            this.fragmentListner.onModuleSelected(DataManager.getInstance().user.getModules().get(position), isHome);
        if (this.homePageListener != null)
        {
            this.homePageListener.onHomePage(isHome);
        }
        if (DataManager.getInstance().user.getModules().get(position).getSubModules().size() != 0)
        {
            tvHeader.setText("" + DataManager.getInstance().user.getModules().get(position).getName());
            if (subModules.size() == 0)
            {
                if (DataManager.getInstance().user.getModules().get(position).getPages().size() != 0)
                {
                    for (int i = 0; i < DataManager.getInstance().user.getModules().get(position).getPages().size(); i++)
                    {
                        SubModule subModule = new SubModule();
                        subModule.setName(DataManager.getInstance().user.getModules().get(position).getPages().get(i).getName());
                        subModule.setSubModuleName(DataManager.getInstance().user.getModules().get(position).getPages().get(i).getName());
                        DataManager.getInstance().user.getModules().get(position).getSubModules().add(0, subModule);
                    }
                }
                subModules.addAll(DataManager.getInstance().user.getModules().get(position).getSubModules());
            }
            pageAdapter = new PageAdapter(getActivity(), DataManager.getInstance().user.getModules().get(position).getSubModules(), true);
        } else
        {
            tvHeader.setText("" + DataManager.getInstance().user.getModules().get(position).getName());
            pageAdapter = new PageAdapter(getActivity(), DataManager.getInstance().user.getModules().get(position).getPages());

        }
        gridView.setAdapter(pageAdapter);
    }

    // show pages from submodules
    public void showSubModulePages(int position, ArrayList<SubModule> arrayList) {
        isHome = false;
        if (this.fragmentListner != null)
            this.fragmentListner.onModuleSelected(DataManager.getInstance().user.getModules().get(positionLevelOne), isHome);
        if (this.homePageListener != null)
        {
            this.homePageListener.onHomePage(isHome);
        }
        tvHeader.setText("" + arrayList.get(position).getSubModuleName());
        pageAdapter = new PageAdapter(getActivity(), arrayList.get(position).getSubPages());
        gridView.setAdapter(pageAdapter);
    }

    public void showSubModulePagesonBack(int position, ArrayList<SubModule> arrayList) {
        isHome = false;
        if (this.homePageListener != null)
        {
            this.homePageListener.onHomePage(isHome);
        }
        if (this.fragmentListner != null)
            this.fragmentListner.onModuleSelected(DataManager.getInstance().user.getModules().get(moduleposition), isHome);
        tvHeader.setText("" + arrayList.get(position).getSubModuleName());
        pageAdapter = new PageAdapter(getActivity(), arrayList.get(position).getSubPages());
        gridView.setAdapter(pageAdapter);
    }

    /*
     * // show quick link sub module page public void
     * showQuickSubPages(ArrayList<Page> pages) { isHome = false; pageAdapter =
     * new PageAdapter(getActivity(), pages); gridView.setAdapter(pageAdapter);
     * }
     */
    // show home menu
    public void showHomeMenu() {
        if (isHome == false)
        {
            isHome = true;
            if (this.fragmentListner != null)
                this.fragmentListner.onModuleSelected(null, true);
            if (this.homePageListener != null)
            {
                this.homePageListener.onHomePage(isHome);
            }
            tvHeader.setText(R.string.smart_manager);
            stepCounter = 0;
            pageAdapter = new PageAdapter(getActivity(), pageModule.getPages());
            gridView.setAdapter(pageAdapter);
        }
    }

    public void setFragmentListener(FragmentListner fragmentListner) {
        this.fragmentListner = fragmentListner;
    }

    public void setOnHomeListener(HomePageListener homePageListener) {
        this.homePageListener = homePageListener;
    }

    private void getImpersonationList() {
        StringBuilder soapMessage = new StringBuilder();
        soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapMessage.append("<Body>");
        soapMessage.append("<ImpersonationRights xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
        soapMessage.append("<userHash>" + DataManager.getInstance().user.getUserHash() + "</userHash>");
        soapMessage.append("</ImpersonationRights>");
        soapMessage.append("</Body>");
        soapMessage.append("</Envelope>");

        VollyResponseListener vollyResponseListener = new VollyResponseListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Helper.showToast(getString(R.string.error_getting_data), getActivity());
                Helper.Log("Error: ", error.toString());
                hideProgressDialog();
            }

            @Override
            public void onResponse(String response) {
                if (getActivity() != null)
                {
                    Helper.Log("TAG", "" + response);
                    ArrayList<Client> clientList = ParserManager.parseImpersonationRespose(response);
                    Impersonate impersonate = new Impersonate();
                    impersonate.setClients(clientList);
                    Helper.Log("count", clientList.size() + "");
                    if (DataManager.getInstance().user != null)
                        DataManager.getInstance().user.setImpersonate(impersonate);
                }
            }
        };

        VollyCustomRequest customRequest = new VollyCustomRequest(Constants.WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IAuthenticate/ImpersonationRights",
                vollyResponseListener);
        try
        {

            customRequest.init();
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*
         * if(!ShowcaseSessions.isSessionAvailable(getActivity(),
		 * HomeScreenFragment.class.getSimpleName())){ ArrayList<TargetView>
		 * viewList= new ArrayList<TargetView>(); viewList.add(new
		 * TargetView(getActivity().findViewById(R.id.ivRightMenu),
		 * ShowCaseType.Right, getString(R.string.tap_here_to_view_main_menu)));
		 * viewList.add(new TargetView(getActivity().findViewById(R.id.llHome),
		 * ShowCaseType.Left, getString(R.string.tap_here_to_exit)));
		 * ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
		 * showcaseView.setShowcaseView(viewList);
		 * 
		 * ((ViewGroup)getActivity().getWindow().getDecorView()).addView(
		 * showcaseView); ShowcaseSessions.saveSession(getActivity(),
		 * HomeScreenFragment.class.getSimpleName()); }
		 */
    }
}