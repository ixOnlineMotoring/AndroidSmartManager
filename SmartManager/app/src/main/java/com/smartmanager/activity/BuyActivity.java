package com.smartmanager.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;


import com.nw.fragments.AuctionSettingFragment;
import com.nw.fragments.BuyDetailFragment.IntrUpdateList;
import com.nw.fragments.BuyFragment;
import com.nw.fragments.CustomMessageFragment;
import com.nw.fragments.DisplayFragment;
import com.nw.fragments.ImageDetailFragment;
import com.nw.fragments.MyBidsDetailsFragment;
import com.nw.fragments.MyBuyersFragment;
import com.nw.fragments.MySellersFragment;
import com.nw.fragments.ReadinessFragment;
import com.nw.fragments.SalesFragment;
import com.nw.fragments.SellDetailsFrament;
import com.nw.fragments.SellFragment;
import com.nw.fragments.TradeMembersFragment;
import com.nw.fragments.TradePartnersFragment;
import com.nw.fragments.TradePriceFragment;
import com.nw.fragments.TradeVehiclesFragment;
import com.nw.fragments.VehicleAlertFragment;
import com.nw.webservice.DataManager;
import com.smartmanager.android.R;

public class BuyActivity extends BaseActivity implements IntrUpdateList
{
    BuyFragment buyFragment;
    SellFragment sellFragment;
    MyBidsDetailsFragment myBidsDetailsFragment;
    TradeVehiclesFragment tradeVehiclesFragment;
    SalesFragment salesFragment;
    SellDetailsFrament sellDetailsFrament;
    VehicleAlertFragment vehicleAlertFragment;
    CustomMessageFragment customMessageFragment;
    TradePriceFragment tradePriceFragment;
    TradePartnersFragment tradePartnersFragment;
    TradeMembersFragment membersFragment;
    ReadinessFragment readinessFragment;
    AuctionSettingFragment auctionSettingFragment;
    MyBuyersFragment myBuyersFragment;
    MySellersFragment mySellersFragment;
    DisplayFragment displayFragment;
    public static VehicleActivity vehicleActivity;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_blog);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras().containsKey("vehicleName"))
        {
            ImageDetailFragment imageDetailFragment = new ImageDetailFragment();
            Bundle args = new Bundle();
            if (getIntent().hasExtra("imagelist"))
                args.putParcelableArrayList("imagelist", getIntent().getParcelableArrayListExtra("imagelist"));
            if (getIntent().hasExtra("urllist"))
                args.putStringArrayList("urllist", getIntent().getStringArrayListExtra("urllist"));
            args.putInt("index", getIntent().getExtras().getInt("index"));
            args.putString("vehicleName", getIntent().getExtras().getString("vehicleName"));
            imageDetailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, imageDetailFragment).commit();
        } else if (getIntent().getExtras().containsKey("Buy"))
        {
            buyFragment = new BuyFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, buyFragment).commit();
        } else if (getIntent().getExtras().containsKey("Losing Bids"))
        {
            myBidsDetailsFragment = new MyBidsDetailsFragment();
            Bundle
                    bundle = new Bundle();
            bundle.putInt("Position",
                    DataManager.getInstance().FIRST_POSITION_KEY);
            myBidsDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBidsDetailsFragment).commit();

			/*
             * OthersWantFragment othersWantFragment = new OthersWantFragment();
			 * FragmentTransaction fragmentTransaction =
			 * getFragmentManager().beginTransaction();
			 * fragmentTransaction.replace(R.id.Container, othersWantFragment);
			 * fragmentTransaction.commit();
			 */
        } else if (getIntent().getExtras().containsKey("Winning Bids"))
        {


            myBidsDetailsFragment = new MyBidsDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().SECOND_POSITION_KEY);
            myBidsDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBidsDetailsFragment).commit();
        } else if (getIntent().getExtras().containsKey("Auto Bidding"))
        {
            myBidsDetailsFragment = new MyBidsDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().THIRD_POSITION_KEY);
            myBidsDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBidsDetailsFragment).commit();

        } else if (getIntent().getExtras().containsKey("Won"))
        {
            myBidsDetailsFragment = new MyBidsDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().WON);
            myBidsDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBidsDetailsFragment).commit();
        } else if (getIntent().getExtras().containsKey("Lost"))
        {
            myBidsDetailsFragment = new MyBidsDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().LOST);
            myBidsDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBidsDetailsFragment).commit();
        } else if (getIntent().getExtras().containsKey("Private Offers"))
        {
            myBidsDetailsFragment = new MyBidsDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().PRIVATEOFFERS);
            myBidsDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBidsDetailsFragment).commit();
        } else if (getIntent().getExtras().containsKey("Withdrawn"))
        {
            myBidsDetailsFragment = new MyBidsDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().WITHDRAWN);
            myBidsDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBidsDetailsFragment).commit();
        } else if (getIntent().getExtras().containsKey("Cancelled"))
        {
            myBidsDetailsFragment = new MyBidsDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().CANCELLED);
            myBidsDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBidsDetailsFragment).commit();
        } else if (getIntent().getExtras().containsKey("Bidding Ended"))
        {
            Bundle bundle = new Bundle();
            bundle.putInt("groupPosition", 0);
            bundle.putString("title", "Action: Bidding Ended");
            sellDetailsFrament = new SellDetailsFrament();
            sellDetailsFrament.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, sellDetailsFrament).commit();
        } else if (getIntent().getExtras().containsKey("Bids Received"))
        {
            Bundle bundle = new Bundle();
            bundle.putInt("groupPosition", 1);
            bundle.putString("title", "Active Bids Received");
            sellDetailsFrament = new SellDetailsFrament();
            sellDetailsFrament.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, sellDetailsFrament).commit();
        } else if (getIntent().getExtras().containsKey("Sales"))
        {
            salesFragment = new SalesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, salesFragment).commit();
        } else if (getIntent().getExtras().containsKey("Missing Price"))
        {
            vehicleAlertFragment = new VehicleAlertFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().FIRST_POSITION_KEY);
            bundle.putString("title", "Missing Price");
            vehicleAlertFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, vehicleAlertFragment).commit();
        } else if (getIntent().getExtras().containsKey("Activate Retail"))
        {
            vehicleAlertFragment = new VehicleAlertFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().SECOND_POSITION_KEY);
            bundle.putString("title", "Activate Retail");
            vehicleAlertFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, vehicleAlertFragment).commit();
        } else if (getIntent().getExtras().containsKey("Missing Info"))
        {
            vehicleAlertFragment = new VehicleAlertFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position", DataManager.getInstance().THIRD_POSITION_KEY);
            bundle.putString("title", "Missing Info");
            vehicleAlertFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, vehicleAlertFragment).commit();
        } else if (getIntent().getExtras().containsKey("Readiness"))
        {
            readinessFragment = new ReadinessFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, readinessFragment).commit();
        } else if (getIntent().getExtras().containsKey("Auctions"))
        {
            auctionSettingFragment = new AuctionSettingFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, auctionSettingFragment).commit();
        } else if (getIntent().getExtras().containsKey("Display"))
        {
            displayFragment = new DisplayFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, displayFragment).commit();
        } else if (getIntent().getExtras().containsKey("Trade Price"))
        {
            tradePriceFragment = new TradePriceFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, tradePriceFragment).commit();
        } else if (getIntent().getExtras().containsKey("Trade Partners"))
        {
            tradePartnersFragment = new TradePartnersFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, tradePartnersFragment).commit();
        } else if (getIntent().getExtras().containsKey("My Buyers"))
        {
            myBuyersFragment = new MyBuyersFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, myBuyersFragment).commit();
        } else if (getIntent().getExtras().containsKey("My Sellers"))
        {
            mySellersFragment = new MySellersFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, mySellersFragment).commit();
        } else if (getIntent().getExtras().containsKey("Members"))
        {
            membersFragment = new TradeMembersFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, membersFragment).commit();
        } else if (getIntent().getExtras().containsKey("Custom Msgs"))
        {
            customMessageFragment = new CustomMessageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, customMessageFragment).commit();
        } else if (getIntent().getExtras().containsKey("Trade Vehicles"))
        {
            tradeVehiclesFragment = new TradeVehiclesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.Container, tradeVehiclesFragment).commit();
        }
    }

    @Override
    public void updateVehicleList(boolean flag)
    {
        if (buyFragment != null)
            buyFragment.isNeedToUpdate(flag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (sellFragment != null)
            sellFragment.onActivityResult(requestCode, resultCode, data);
        if (vehicleAlertFragment != null)
            vehicleAlertFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        buyFragment = null;
        sellFragment = null;
        salesFragment = null;
        myBidsDetailsFragment = null;
        customMessageFragment = null;
        tradePriceFragment = null;
        displayFragment = null;
        readinessFragment = null;
        sellDetailsFrament = null;
        vehicleAlertFragment = null;
        membersFragment = null;
        auctionSettingFragment = null;
        myBuyersFragment = null;
        mySellersFragment = null;
        tradeVehiclesFragment = null;
        if (vehicleActivity != null)
            vehicleActivity = null;
        System.gc();
    }
}
