package com.nw.webservice;

import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nw.model.BaseImage;
import com.nw.model.Reference;
import com.nw.model.ReferenceID;
import com.nw.model.Request;
import com.nw.model.RequestUser;
import com.nw.model.User;
import com.nw.model.YouTubeVideo;

import java.util.ArrayList;

public class DataManager {
    private static DataManager dataManager;
    public User user;
    public RequestUser requestUser;
    public Reference reference;
    //public Vehicle vehicle;
    public static final String MODULES[] = {
            "Stock", "Leads", "Trader",
            "Validate", "Social", "Documents",
            "Members", "Planner", "ixSupport",
            "Home", "Vehicles", "Delivery",
            "VIN Lookup", "Load Vehicle", "Edit Stock", "Stock List",
            "Specials", "To Do's", "Log Activity",
            "New Task", "Tasks by Me", "Buy",
            "Sell", "Vehicle Alerts", "Notifications", "My Bids",
            "Photos & Extras", "Blog Post", "Wanted", "Customers", "Scan License",
            "Unseen", "Stock Audit", "eBrochure", "Test",
            "Losing Bids", "Winning Bids", "Auto Bidding", "Won", "Lost",
            "Private Offers", "Withdrawn", "Cancelled",
            "Bidding Ended", "Bids Received", "Trade Vehicles", "Sales", "Missing Price"
            , "Activate Retail", "Missing Info", "My Buyers", "My Sellers", "Settings", "Readiness",
            "Auctions", "Display", "Trade Price", "Trade Partners", "Custom Msgs",
            "Synopsis", "Scan VIN", "Vehicle Lookup", "Vehicle Code", "Vehicle in Stock",
            "Saved Appraisals", /*"My Leads",*/ "Stock Summary", "Support","Support Request"

    };

    public static final IconValue module_icon[] = {
            null, IconValue.fa_bar_chart, IconValue.fa_share_alt,
            IconValue.fa_check, IconValue.fa_rss, IconValue.fa_file,
            IconValue.fa_male, IconValue.fa_calendar, null,
            IconValue.fa_home, IconValue.fa_car, IconValue.fa_child,
            IconValue.fa_barcode, IconValue.fa_upload, IconValue.fa_edit, IconValue.fa_search,
            IconValue.fa_tags, IconValue.fa_tasks, IconValue.fa_check_square,
            IconValue.fa_plus_square, IconValue.fa_indent, IconValue.fa_shopping_cart,
            IconValue.fa_gavel, IconValue.fa_exclamation_triangle, IconValue.fa_bell, IconValue.fa_list,
            IconValue.fa_camera, IconValue.fa_send, IconValue.fa_arrows_alt, IconValue.fa_user, IconValue.fa_barcode,
            IconValue.fa_bullhorn, IconValue.fa_adjust, IconValue.fa_send, IconValue.fa_android,
            IconValue.fa_bomb, IconValue.fa_dot_circle_o, IconValue.fa_eye,
            IconValue.fa_check, IconValue.fa_close, IconValue.fa_lemon_o, IconValue.fa_eraser, IconValue.fa_fire,
            IconValue.fa_clock_o, IconValue.fa_check_square_o, IconValue.fa_share, IconValue.fa_thumbs_up,
            IconValue.fa_flag_o, IconValue.fa_money, IconValue.fa_exclamation, IconValue.fa_expand,
            IconValue.fa_compress, IconValue.fa_gears, IconValue.fa_sort, IconValue.fa_institution,
            IconValue.fa_desktop, IconValue.fa_usd, IconValue.fa_sitemap, IconValue.fa_wrench,
            IconValue.fa_crosshairs, IconValue.fa_barcode, IconValue.fa_eye, IconValue.fa_code, IconValue.fa_cubes,
            IconValue.fa_folder_open, /*IconValue.fa_bar_chart,*/ IconValue.fa_dot_circle_o, IconValue.fa_thumb_tack,IconValue.fa_thumb_tack
    };

    public String albumDirectory;
    public int AuditType = 0;
    private int TotalLicenseCount = 0;
    public int FIRST_POSITION_KEY = 0;
    public int SECOND_POSITION_KEY = 1;
    public int THIRD_POSITION_KEY = 2;
    public int WON = 3;
    public int LOST = 4;
    public int PRIVATEOFFERS = 5;
    public int WITHDRAWN = 6;
    public int CANCELLED = 7;
    public String VIDEO_PATH_FIRST, VIDEO_PATH_SECOND;
    public boolean isClientSetUptoUploadVideo = false;

    public YouTubeVideo youTubeVideo = null;
    private ArrayList<YouTubeVideo> youTubeVideos;
    private ArrayList<BaseImage> imageArrayList;

    private DataManager() {

    }

    public static IconValue getIconValue(String name) {
        int position = -1;
        for (int i = 0; i < DataManager.module_icon.length; i++)
        {
            if (name.equalsIgnoreCase(DataManager.MODULES[i]))
            {
                position = i;
                break;
            }
        }
        if (position != -1)
            return DataManager.module_icon[position];
        else
            return null;
    }

    public synchronized static DataManager getInstance() {
        if (dataManager == null)
            dataManager = new DataManager();
        return dataManager;
    }

    public int getTotalLicenseCount() {

        return TotalLicenseCount;
    }

    public void setTotalLicenseCount(int totalLicenseCount) {

        TotalLicenseCount = totalLicenseCount;
    }

    public ArrayList<YouTubeVideo> getYouTubeVideos() {
        if (youTubeVideos == null)
        {
            youTubeVideos = new ArrayList<>();
        }
        return youTubeVideos;
    }

    public void setYouTubeVideos(ArrayList<YouTubeVideo> youTubeVideos) {
        this.youTubeVideos = youTubeVideos;
    }

    public ArrayList<BaseImage> getimageArray() {
        if (imageArrayList == null)
        {
            imageArrayList = new ArrayList<>();
        }
        return imageArrayList;
    }

    public void setimageArray(ArrayList<BaseImage> imageArrayList) {
        this.imageArrayList = imageArrayList;
    }

}
