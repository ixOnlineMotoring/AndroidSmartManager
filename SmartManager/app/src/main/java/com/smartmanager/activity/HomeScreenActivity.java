package com.smartmanager.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Fragment;

import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify;
import com.joanzapata.android.iconify.Iconify.IconValue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nw.fragments.HomeScreenFragment;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.FragmentListner;
import com.nw.interfaces.HomePageListener;
import com.nw.model.Client;
import com.nw.model.DataInObject;
import com.nw.model.Module;
import com.nw.model.Page;
import com.nw.model.Parameter;
import com.nw.webservice.DataManager;
import com.nw.webservice.TaskListener;
import com.nw.webservice.WebServiceTask;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.AppSession;
import com.utils.Constants;
import com.utils.Helper;

import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.util.ArrayList;

public class HomeScreenActivity extends BaseActivity implements FragmentListner, HomePageListener
{
    private final String USER_MENU[] = {"Impersonate", "Change Profile Photo", "Logout"};
   // private final String USER_MENU[] = {"Impersonate", "Logout"};
    private static ArrayList<Module> quickModules;
    TextView tvPageTitle, tvSubTitle, tvClientID;
    ImageView ivUser, ivCompanyLogo;
    View vwCustomActionBar;
    boolean isImpersonate = false, isImpersonationClicked = false;
    HomeScreenFragment awesomeCardFragment;
    LinearLayout llBottomMenu, llHome;
    ImageLoader loader;
    private DisplayImageOptions user_options, company_options;
    TextView tvHome;
    ProgressBar pBarHome;
    LayoutParams params;
    RelativeLayout rlLayout, rlLayoutTemp;
    Uri fileUri = null;
    Uri outputFileUri = null;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int PERMISSION_STORAGE = 3;
    private static final int CROP_IMAGE = 4;
    String filePath;
    String isProfilePhotoAvailable = "true";
    boolean isNotUpdateProfile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_base_activity);
        overridePendingTransition(0, 0);
        getActionBar().hide();

        pBarHome = (ProgressBar) findViewById(R.id.pBarHome);
        vwCustomActionBar = findViewById(R.id.customActionBarView);
        rlLayout = (RelativeLayout) vwCustomActionBar.findViewById(R.id.rlLayout);
        rlLayoutTemp = (RelativeLayout) vwCustomActionBar.findViewById(R.id.rlLayoutTemp);

        tvPageTitle = (TextView) vwCustomActionBar.findViewById(R.id.tvTitle);
        tvSubTitle = (TextView) vwCustomActionBar.findViewById(R.id.tvSubTitle);
        tvClientID = (TextView) vwCustomActionBar.findViewById(R.id.tvClientID);
        ivUser = (ImageView) vwCustomActionBar.findViewById(R.id.ivUserProfile);
        ivCompanyLogo = (ImageView) findViewById(R.id.ivCompanyLogo);
        tvHome = (TextView) findViewById(R.id.ivHome);
        tvHome.setVisibility(View.GONE);
        initImageLoader();
        showClientLogoAndMember();
        if (Helper.isTablet(HomeScreenActivity.this))
        {
            tvHome.setCompoundDrawablesWithIntrinsicBounds(null, Helper
                    .getIcon(HomeScreenActivity.this, IconValue.fa_home, 65), null, null);
        } else
        {
            tvHome.setCompoundDrawablesWithIntrinsicBounds(null, Helper
                    .getIcon(HomeScreenActivity.this, IconValue.fa_home, 40), null, null);
        }
        ivUser.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                awesomeCardFragment.showHomeMenu();
            }
        });

        getActionBar().setCustomView(vwCustomActionBar);
        vwCustomActionBar.findViewById(R.id.relativeLayout1).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ArrayAdapter<String> adapter = null;
                if (isImpersonate == false)
                    adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item_text, R.id.tvText, USER_MENU);
                else
                {
                    USER_MENU[0] = "Revert";
                    adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item_text, R.id.tvText, USER_MENU);
                }
                Helper.showDropDownActionBar(rlLayoutTemp, adapter, new OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int itemPosition, long arg3)
                    {
                        switch (itemPosition)
                        {
                            case 0:
                                if (!DataManager.getInstance().user.getImpersonate().getClients().isEmpty())
                                {
                                    if (isImpersonate == false)
                                    {
                                        showSearch(HomeScreenActivity.this);
                                    } else
                                    {
                                        isImpersonate = false;
                                        USER_MENU[0] = "Impersonate";
                                        DataManager.getInstance().user.setDefaultClient(DataManager.getInstance().user.getClient());
                                        tvSubTitle.setText(DataManager.getInstance().user.getClient().getName());
                                        tvClientID.setText("" + DataManager.getInstance().user.getClient().getId());
                                    }
                                } else
                                {
                                    Helper.showToast("Please wait, Data is loading..", HomeScreenActivity.this);
                                }
                                break;


                           case 1:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                {
                                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                                        return;
                                    }
                                }
                                getImageFromGalleryOrCamera(HomeScreenActivity.this);

                                break;

                            case 2:
                                CustomDialogManager.showOkCancelDialog(HomeScreenActivity.this,
                                        getString(R.string.are_you_sure_you_want_to_logout),
                                        "Yes", "No", new DialogListener()
                                        {
                                            @Override
                                            public void onButtonClicked(int type)
                                            {
                                                if (type == DialogInterface.BUTTON_POSITIVE)
                                                {
                                                    AppSession.saveLoginStatus(HomeScreenActivity.this, false);
                                                    finish();
                                                    DataManager.getInstance().user = null;

                                                    if (awesomeCardFragment != null)
                                                        HomeScreenFragment.homeScreenFragment = null;
                                                    HomeScreenFragment.pageModule = null;
                                                    awesomeCardFragment = null;
                                                    if (quickModules != null)
                                                    {
                                                        quickModules.clear();
                                                        quickModules = null;
                                                    }
                                                    ImageLoader.getInstance().clearDiskCache();
                                                    ImageLoader.getInstance().clearMemoryCache();
                                                    Helper.flushFileContents();
                                                    System.gc();
                                                    // start
                                                    // login
                                                    // screen
                                                    Intent intent = new Intent(HomeScreenActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                break;
                        }
                    }
                });
            }
        });

        // show user details with client details
        try
        {
            if (DataManager.getInstance().user == null)
            {
                AppSession.saveLoginStatus(HomeScreenActivity.this, false);
                finish();
                DataManager.getInstance().user = null;
                if (awesomeCardFragment != null)
                    HomeScreenFragment.homeScreenFragment = null;
                HomeScreenFragment.pageModule = null;
                awesomeCardFragment = null;
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                Helper.flushFileContents();
                System.gc();
                // start login screen
                Intent intent = new Intent(HomeScreenActivity.this, LoginActivity.class);
                startActivity(intent);
            } else
            {
                tvPageTitle.setText(DataManager.getInstance().user.getName() + " " + DataManager.getInstance().user.getSurName());
                tvSubTitle.setText(DataManager.getInstance().user.getClient().getName());
                tvClientID.setText("" + DataManager.getInstance().user.getClient().getId());
                quickModules = new ArrayList<Module>();
                {
                    Module module = new Module();
                    module.setName("Home");
                    module.setQuickLink(true);
                    quickModules.add(module);
                }
                // check for quick links
                for (Module module : DataManager.getInstance().user.getModules())
                {
                    if (module.isQuickLink())
                        quickModules.add(module);
                    // add all this menu to option for home screen
                    Page page = new Page();
                    page.setAlert(false);
                    page.setName(module.getName());
                    quickModules.get(0).getPages().add(page);
                }
                if (awesomeCardFragment != null)
                    awesomeCardFragment = null;
                awesomeCardFragment = HomeScreenFragment.newInstance(quickModules.get(0));
                awesomeCardFragment.setFragmentListener(this);
                awesomeCardFragment.setOnHomeListener(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.Container, awesomeCardFragment).commit();
                llBottomMenu = (LinearLayout) findViewById(R.id.llBottomMenu);
                // set dimension to company logo and home screen.
                tvHome.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                        {
                            tvHome.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else
                        {
                            tvHome.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        RelativeLayout.LayoutParams lpp = (RelativeLayout.LayoutParams) tvHome.getLayoutParams();
                        lpp.rightMargin = HomeScreenFragment.Width / 4;
                        tvHome.setLayoutParams(lpp);
                        FrameLayout.LayoutParams lpp1 = (FrameLayout.LayoutParams) ivCompanyLogo.getLayoutParams();
                        lpp1.leftMargin = HomeScreenFragment.Width / 5;
                        ivCompanyLogo.setLayoutParams(lpp1);
                    }
                });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initImageLoader()
    {
        loader = ImageLoader.getInstance();
        initUserImageLoader();
        initCompanyImageLoader();
    }

    private void initUserImageLoader()
    {
        user_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)
                .showImageOnFail(Helper.getIcon(HomeScreenActivity.this, IconValue.fa_user, 30))
                .showImageForEmptyUri(Helper.getIcon(HomeScreenActivity.this, IconValue.fa_user, 30)).cacheInMemory(false)
                .cacheOnDisk(false).build();
    }

    private void initCompanyImageLoader()
    {
        company_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)
                .showImageOnFail(R.drawable.ic_ix_co_za)
                .showImageForEmptyUri(R.drawable.ic_ix_co_za)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }

    SimpleImageLoadingListener listener = new SimpleImageLoadingListener()
    {
        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage)
        {
            pBarHome.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingStarted(String imageUri, View view)
        {
            pBarHome.setVisibility(View.VISIBLE);
        }

        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason)
        {
            pBarHome.setVisibility(View.INVISIBLE);
        }

        ;
    };

    public void showSearch(Context context)
    {
        /*
         * AlertDialog.Builder build = new AlertDialog.Builder(context); final
		 * AlertDialog dialog = null; View view =
		 * LayoutInflater.from(context).inflate(R.layout.popup_window_search,
		 * null); dialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(android.graphics.Color.TRANSPARENT));
		 * build.setView(view); dialog = build.create();
		 */
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_window_search);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        final TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        final ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        ListView lstData = (ListView) dialog.findViewById(R.id.listView);
        lstData.setEmptyView(dialog.findViewById(R.id.emptyView));
        final EditText edtClientName = (EditText) dialog.findViewById(R.id.edtClientName);
        final ArrayAdapter<Client> clientAdapter = new ArrayAdapter<Client>(
                context, R.layout.list_item_text4, R.id.tvText,
                DataManager.getInstance().user.getImpersonate().getClients());
        lstData.setAdapter(clientAdapter);
        edtClientName.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3)
            {
                clientAdapter.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3)
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
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3)
            {
                Helper.hidekeybord(edtClientName);
                dialog.dismiss();
                isImpersonate = true;
                // set default client key
                DataManager.getInstance().user.setDefaultClient(clientAdapter
                        .getItem(position));
                tvSubTitle.setText(clientAdapter.getItem(position).getName());
                tvClientID
                        .setText("" + clientAdapter.getItem(position).getId());
            }
        });
        // tvTitle.setText(R.string.app_name);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter
    {
        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return quickModules.get(position).getName();
        }

        @Override
        public int getCount()
        {
            return quickModules.size();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            return HomeScreenFragment.newInstance(quickModules.get(position));
        }
    }

    @Override
    public void onBackPressed()
    {
        if (awesomeCardFragment != null)
        {
            if (awesomeCardFragment.isHome == false)
            {
                if (DataManager.getInstance().user.getModules()
                        .get(awesomeCardFragment.positionLevelOne)
                        .getSubModules().size() != 0
                        && awesomeCardFragment.stepCounter != 1)
                {
                    // it will shows grid again
                    awesomeCardFragment.stepCounter--;
                    awesomeCardFragment
                            .showSubPages(awesomeCardFragment.positionLevelOne);

                    // awesomeCardFragment.showSubModulePagesonBack(awesomeCardFragment.moduleposition,
                    // DataManager.getInstance().user.getModules().get(awesomeCardFragment.moduleposition).getSubModules());
                } else
                {
                    awesomeCardFragment.showHomeMenu();
                }
            } else
            {
                // ask before exiting from app
                CustomDialogManager.showOkCancelDialog(HomeScreenActivity.this,
                        getString(R.string.are_you_sure_you_want_to_exit),
                        "Yes", "No", new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (type == DialogInterface.BUTTON_POSITIVE)
                                {
                                    DataManager.getInstance().user = null;

                                    if (awesomeCardFragment != null)
                                        HomeScreenFragment.homeScreenFragment = null;
                                    HomeScreenFragment.pageModule = null;
                                    awesomeCardFragment = null;
                                    if (quickModules != null)
                                    {
                                        quickModules.clear();
                                        quickModules = null;
                                    }
                                    ImageLoader.getInstance().clearDiskCache();
                                    ImageLoader.getInstance()
                                            .clearMemoryCache();
                                    System.gc();
                                    finish();
                                }
                            }
                        });
            }
        } else
        {
            if (getFragmentManager().getBackStackEntryCount() != 0)
            {
                getFragmentManager().popBackStack();
            } else
            {
                super.onBackPressed();
            }

        }
    }

    @Override
    public void onModuleSelected(Module module, boolean isHome)
    {
        if (isHome == false)
        {
            //	showSlideMenu(module);
        } else
        {

        }
        //	showSlideMenu(quickModules.get(0));
    }

    private long enqueue;
    DownloadManager downloadManager;

    @SuppressWarnings("unused")
    private void showUpdateAppDialog()
    {
        CustomDialogManager
                .showOkCancelDialog(
                        this,
                        "New update available for Smart Mangaer, do you want to update",
                        "Update", "No thanks,", new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                if (type == Dialog.BUTTON_POSITIVE)
                                {
                                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                    Request request = new Request(
                                            Uri.parse("https://flickrj-android.googlecode.com/files/flickrj-android-sample-android.apk"));
                                    request.setTitle("Smart Manager Apk");
                                    request.setDescription("");
                                    request.setDestinationInExternalPublicDir(
                                            Environment.DIRECTORY_DOWNLOADS,
                                            "SM_V_1.apk");
                                    enqueue = downloadManager.enqueue(request);
                                }
                            }
                        });
    }

    BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
            {
                Query query = new Query();
                query.setFilterById(enqueue);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst())
                {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex))
                    {
                        Intent inStallIntent = new Intent(Intent.ACTION_VIEW);
                        inStallIntent.setDataAndType(Uri.fromFile(new File(
                                        Environment.getExternalStorageDirectory() + "/"
                                                + Environment.DIRECTORY_DOWNLOADS,
                                        "SM_V_1.apk")),
                                "application/vnd.android.package-archive");
                        inStallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(inStallIntent);
                    }
                }
            }
        }
    };

    private void showClientLogoAndMember()
    {
        try
        {
            if (!DataManager.getInstance().user.getMemberImages().isEmpty())
            {
                //Added by Asmita
                if (DataManager.getInstance().user.getMemberImages().size() >= 1)
                    loader.displayImage(DataManager.getInstance().user.getMemberImages().get(0).getUrl(), ivUser, user_options);
            } else
            {
                ivUser.setBackgroundDrawable(Helper.getIcon(HomeScreenActivity.this, Iconify.IconValue.fa_user, 40));

            }
            //the sm logo at the bottom strip
            if (!DataManager.getInstance().user.getClientImages().isEmpty())
            {
                if (DataManager.getInstance().user.getClientImages().size() >= 5)
                    loader.displayImage(DataManager.getInstance().user
                                    .getClientImages().get(checkDensity()).getUrl(),
                            ivCompanyLogo, company_options, listener);
                AppSession.saveClientImage(this, DataManager.getInstance().user.getClientImages().get(checkDensity()).getUrl(), DataManager.getInstance().user.getDefaultClient()
                        .getId());
            } else
            {
                pBarHome.setVisibility(View.GONE);
                loader.displayImage("", ivCompanyLogo, company_options);
            }
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private int checkDensity()
    {
        int i = 5;
        switch (getResources().getDisplayMetrics().densityDpi)
        {
            case DisplayMetrics.DENSITY_LOW:
                i = 1;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
            case DisplayMetrics.DENSITY_HIGH:
            case DisplayMetrics.DENSITY_TV:
                // ...
                i = 2;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                // ...
                i = 3;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_XXXHIGH:
                i = 5;
                // ...
                break;
        }
        return i;
    }

    @Override
    public void onHomePage(boolean isHomeStatus)
    {
        if (isHomeStatus)
        {
            tvHome.setText("Home");
            tvHome.setVisibility(View.GONE);
            if (Helper.isTablet(HomeScreenActivity.this))
            {
                tvHome.setCompoundDrawablesWithIntrinsicBounds(null,
                        Helper.getIcon(HomeScreenActivity.this,
                                IconValue.fa_home, 65), null, null);
            } else
            {
                tvHome.setCompoundDrawablesWithIntrinsicBounds(null,
                        Helper.getIcon(HomeScreenActivity.this,
                                IconValue.fa_home, 40), null, null);
            }

            tvHome.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // show Home Menu
                    awesomeCardFragment.showHomeMenu();
                    //showSlideMenu(quickModules.get(0));
                }
            });

        } else
        {
            tvHome.setText("Back");
            tvHome.setVisibility(View.VISIBLE);
            if (Helper.isTablet(HomeScreenActivity.this))
            {
                tvHome.setCompoundDrawablesWithIntrinsicBounds(null, Helper
                        .getIcon(HomeScreenActivity.this,
                                IconValue.fa_arrow_circle_left, 65), null, null);
            } else
            {
                tvHome.setCompoundDrawablesWithIntrinsicBounds(null, Helper
                        .getIcon(HomeScreenActivity.this,
                                IconValue.fa_arrow_circle_left, 40), null, null);
            }
            tvHome.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // show Home Menu
                    onBackPressed();
                    // showSlideMenu(quickModules.get(0));
                }
            });

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
                    isNotUpdateProfile = false;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = Helper.getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                    intent = null;
                } else
                {
                    isNotUpdateProfile = false;
                    int versionCode = Build.VERSION.SDK_INT;
                    if (versionCode > Build.VERSION_CODES.KITKAT)
                    {
                        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT)
                                        .setType("image/*"),
                                getString(R.string.upload_image)), PICK_FROM_GALLERY);

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
                Intent intent = new Intent(HomeScreenActivity.this, ImageCropperActivity.class);
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
                    imagePathToCropActivity = Helper.getPath(selectedImageUri, HomeScreenActivity.this);

                    if (TextUtils.isEmpty(imagePathToCropActivity))
                    {
                        imagePathToCropActivity = Helper.getPath(selectedImageUri, HomeScreenActivity.this);
                    }
                    Intent intentGallery = new Intent(HomeScreenActivity.this, ImageCropperActivity.class);
                    if (imagePathToCropActivity != null && !imagePathToCropActivity.isEmpty())
                    {
                        intentGallery.putExtra("filepath", imagePathToCropActivity);
                    } else
                    {
                        imagePathToCropActivity = Helper.getImagePathFromGalleryAboveKitkat(HomeScreenActivity.this, selectedImageUri);
                        if (imagePathToCropActivity == null)
                        {
                            Helper.showCropDialog(getString(R.string.select_image_from_device_folders), HomeScreenActivity.this);
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
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImageToImageView(ImageView ivProfilePic, String photoPath)
    {
        Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
        if (imageBitmap != null)
        {
            ivProfilePic.setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //Set Image if user updates the photo picture from eBroucher Fragment
       /* if (!AppSession.getFileName(HomeScreenActivity.this).isEmpty())
        {
            String filePath = AppSession.getFileName(HomeScreenActivity.this);
            {
                setImageToImageView(ivUser, filePath);
            }
        }*/

        //Added by Asmita
        //call webservice here to get Profile Photo
        //    hasProfileImage();

        if (isNotUpdateProfile)
        {
            hasProfileImage();
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
        new WebServiceTask(HomeScreenActivity.this, inObj, false, new TaskListener()
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

                    } else
                    {
                        ivUser.setBackgroundDrawable(Helper.getIcon(HomeScreenActivity.this, Iconify.IconValue.fa_user, 40));

                    }


                } catch (Exception e)
                {
                    e.printStackTrace();
                }
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
        new WebServiceTask(HomeScreenActivity.this, inObj, false, new TaskListener()
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
                    ImageLoader.getInstance().clearMemoryCache();
                    ImageLoader.getInstance().clearDiskCache();
                    loader.displayImage(imageUrl, ivUser, user_options);

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
        new WebServiceTask(HomeScreenActivity.this, inObj, false, new TaskListener()
        {
            @Override
            public void onTaskComplete(Object response)
            {
                hideProgressDialog();
                try
                {
                    isNotUpdateProfile = true;
                    //on response set image locally on Home Screen
                    //AppSession.saveFileName(HomeScreenActivity.this, filePath);
                    ImageLoader.getInstance().clearMemoryCache();
                    ImageLoader.getInstance().clearDiskCache();
                    ivUser.setBackgroundDrawable(null);
                    setImageToImageView(ivUser, filePath);
                    CustomDialogManager.showOkDialog(HomeScreenActivity.this, getString(R.string.image_updated_successfully));

                } catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        }).execute();
    }

}
