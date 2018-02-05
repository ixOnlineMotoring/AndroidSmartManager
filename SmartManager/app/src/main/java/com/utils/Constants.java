package com.utils;

public class Constants
{
    //common in both
    public static final String GET_LOGIN = "/";
    public static String TEMP_URI_NAMESPACE = "http://tempuri.org/";
    public static int TYPE_NEW = 0;
    public static int TYPE_USED = 1;
    public static final String SCAN_LICENSE_KEY = "BTH7-L4JO-UI5T-JAFP-YSKX-BXZT-SDKE-LKIZ";
    public static final String LOG_FILE = "SM-STAGING-LOG.txt";

    public static final String e_BROCHURE_NAMESPACE = "http://schemas.datacontract.org/2004/07/EBrochureService";
    public static int VIDEO_RECORDING_CUSTOM = 1201;
    public static int VIDEO_GALLERY = 1301;
    public static int PHOTO_LIMIT = 20;

    // Loca Database Types :
    public static final String SaveBlogImage = "SaveBlogImage";
    public static final String SaveDeliveryImage = "Delivery";
    public static final String SavePhotosAndExtrasImage = "PhotosAndExtras";
    public static final String SaveSpecialImage = "Special";
    public static final String SaveVideos = "SaveVideos";
    public static String CHANGE_THIS_USER_HASH = "change_this_user_hash";

    //old staging
   /* public static String WEBSERVICE_URL = "http://p4.authentication.ixstaging.co.za/Authenticate.svc";
    public static String STOCK_WEBSERVICE_URL = "http://stockservice.ixstaging.co.za/StockService.svc?wsdl";
    public static String PLANNER_WEBSERVICE_URL = "http://planner.ixstaging.co.za/PlannerService.svc?wsdl";
    public static String BLOG_WEBSERVICE_URL = "http://blog.ixstaging.co.za/BlogService.svc?wsdl";
    public static String TRADER_WEBSERVICE_URL = "http://tradeser vice.ixstaging.co.za/TradeService.svc?wsdl";
    public static String IMAGE_BASE_URL = "http://netwin.ixstaging.co.za/";
    public static String IMAGE_BASE_URL_NEW = "http://netwin.ixstagingtest.co.za/GetImage.aspx?ImageID=";
    public static String LEADS_WEBSERVICE_URL = "http://leadservicenew.ixstaging.co.za/LeadService.svc?wsdl";
    public static String LICENSE_WEBSERVICE_URL = "http://licensesvc.ixstaging.co.za/License.svc?wsdl";
    public static String EBROCHURE_WEBSERVICE_URL = "http://ebrochure.ixstaging.co.za/electronicbrochuregeneratorservice.svc?wsdl";
    public static String SPECIAL_WEBSERVICE_URL = "http://specialsservice.ixstaging.co.za/SpecialsService.svc?singleWsdl";
    public static final String TRADER_NAMESPACE = "Http://TradeService.ixstaging.co.za";
    public static final String VIDEO_WEBSERVICE = "http://uploadservice.ixstaging.co.za/api/Video";*/

    //live web service
    /*public static final String WEBSERVICE_URL="http://p4.authentication.ix.co.za/Authenticate.svc";
    public static final String STOCK_WEBSERVICE_URL="http://stockservice.ix.co.za/StockService.svc?wsdl";
	public static final String PLANNER_WEBSERVICE_URL="http://planner.ix.co.za/PlannerService.svc?wsdl";
	public static final String BLOG_WEBSERVICE_URL="http://blog.ix.co.za/BlogService.svc?wsdl";
	public static final String TRADER_WEBSERVICE_URL="http://tradeservice.ix.co.za/TradeService.svc?wsdl";
	public static final String IMAGE_BASE_URL_NEW="http://www.ix.co.za/GetImage.aspx?ImageID=";
	public static final String IMAGE_BASE_URL="http://netwin.ix.co.za/";
	public static  String LEADS_WEBSERVICE_URL="http://leadservicenew.ix.co.za/LeadService.svc?wsdl";
	public static String LICENSE_WEBSERVICE_URL = "http://licensesvc.ix.co.za/License.svc?wsdl";
	public static  String EBROCHURE_WEBSERVICE_URL="http://ebrochure.ix.co.za/electronicbrochuregeneratorservice.svc?wsdl";
	public static String SPECIAL_WEBSERVICE_URL = "http://specialsservice.ix.co.za/SpecialsService.svc?singleWsdl";
	public static final String TRADER_NAMESPACE="Http://TradeService.ix.co.z\na";
	public static final String VIDEO_WEBSERVICE="http://uploadservice.ix.co.za/api/Video";*/


    // Always should point to live (Client requirement)
    public static final String LEADS_NAMESPACE = "Http://LeadService.ix.co.za";


    //Added by Asmita
    //New Staging URLs

    public static String WEBSERVICE_URL = "http://p4authentication.qa.ix.co.za/Authenticate.svc?wsdl";

    public static String STOCK_WEBSERVICE_URL = "http://stock.qa.ix.co.za/StockService.svc?wsdl";

    public static String PLANNER_WEBSERVICE_URL = "http://planner.qa.ix.co.za/PlannerService.svc?wsdl";

    public static String BLOG_WEBSERVICE_URL = "http://blog.qa.ix.co.za/BlogService.svc";

    //public static String IMAGE_BASE_URL = "http://netwin.ixstaging.co.za/";

    //Added by Asmita on 02/02/2018
    public static String IMAGE_BASE_URL = "http://netwin.qa.ix.co.za/";

    //public static String IMAGE_BASE_URL_NEW = "http://netwin.ixstagingtest.co.za/GetImage.aspx?ImageID=";
    //Added by Asmita on 02/02/2018
    public static String IMAGE_BASE_URL_NEW = "http://netwin.ixstagingtest.co.za/GetImage.aspx?ImageID=";

    public static String LEADS_WEBSERVICE_URL = "http://lead.qa.ix.co.za/LeadService.svc?wsdl ";

    //public static String LICENSE_WEBSERVICE_URL = "http://licensesvc.ixstaging.co.za/License.svc?wsdl";
    //Added by Asmita on 31/01/2018
    public static String LICENSE_WEBSERVICE_URL = "http://licensesvc.qa.ix.co.za/License.svc";

    public static String EBROCHURE_WEBSERVICE_URL = "http://ebrochure.qa.ix.co.za/ElectronicBrochureGeneratorService.svc?wsdl";

    public static String SPECIAL_WEBSERVICE_URL = "http://specialsservice.ixstaging.co.za/SpecialsService.svc?singleWsdl";

    /*public static String TRADER_WEBSERVICE_URL = "http://tradeservice.ixstaging.co.za/TradeService.svc?wsdl";*/
    //Added by Asmita on 01-02-2018
    public static String TRADER_WEBSERVICE_URL = "http://tradeservice.ix.co.za/TradeService.svc?wsdl";

    public static final String TRADER_NAMESPACE = "http://tradeservice.ix.co.za";

    public static final String VIDEO_WEBSERVICE = "http://uploadservice.qa.ix.co.za/api/Video";
}