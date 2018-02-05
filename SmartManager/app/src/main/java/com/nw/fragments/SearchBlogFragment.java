package com.nw.fragments;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nw.adapters.SearchBlogAdapter;
import com.nw.interfaces.BlogItemEditClickListener;
import com.nw.interfaces.DateListener;
import com.nw.interfaces.DialogListener;
import com.nw.interfaces.LoadMoreListener;
import com.nw.model.Blog;
import com.nw.webservice.DataManager;
import com.nw.webservice.ParserManager;
import com.nw.webservice.VollyCustomRequest;
import com.nw.webservice.VollyResponseListener;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;
import com.utils.Constants;
import com.utils.Helper;
import com.utils.HelperHttp;

import java.util.ArrayList;

public class SearchBlogFragment extends BaseFragement implements OnClickListener, LoadMoreListener
{
    EditText edtSearch, edtStartDate, edtEndDate;
    Button btnNewPost, btnSearch, btnEdit, btnEnd;
    TextView tvSearchReseult, tvSwipeView;
    ViewPager pager;
    int pageStart = 0;
    int pageIndex = 0;
    int totalBlogsCount = 0;
    ArrayList<Blog> blogs;
    SearchBlogAdapter adapter;
    LinearLayout llBottomLayout, llNewPost;
    CheckBox chkActiveInactive;
    View vwDivider, vwDividerBottom;
    public static boolean isEdited = false;
    CreateBlogFragment createBlogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search_blog, container, false);
        setHasOptionsMenu(true);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        edtStartDate = (EditText) view.findViewById(R.id.edtStartDate);
        edtEndDate = (EditText) view.findViewById(R.id.edtEndDate);
        btnNewPost = (Button) view.findViewById(R.id.btnNewPost);
        llNewPost = (LinearLayout) view.findViewById(R.id.llNewPost);
        llNewPost.setOnClickListener(this);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnEnd = (Button) view.findViewById(R.id.btnEnd);
        tvSearchReseult = (TextView) view.findViewById(R.id.tvSearchResult);
        tvSwipeView = (TextView) view.findViewById(R.id.tvSwipeTiView);
        pager = (ViewPager) view.findViewById(R.id.pager);
        chkActiveInactive = (CheckBox) view.findViewById(R.id.chkActiveInactive);
        vwDivider = view.findViewById(R.id.vwDivider);
        vwDividerBottom = view.findViewById(R.id.vwDividerBottom);
        llBottomLayout = (LinearLayout) view.findViewById(R.id.botttomLayout);
        edtStartDate.setOnClickListener(this);
        edtEndDate.setOnClickListener(this);
        btnNewPost.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnEnd.setOnClickListener(this);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    searchData();
                    return true;
                }
                return false;
            }
        });

        if (blogs == null)
            blogs = new ArrayList<Blog>();
        else
        {
            if (!blogs.isEmpty())
            {
                adapter = new SearchBlogAdapter(getActivity(), blogs, new BlogItemEditClickListener()
                {

                    @Override
                    public void onBlogItemClicked(int position)
                    {
                        btnEdit.performClick();
                    }
                });
                adapter.setLoadMoreListener(this);
                pager.setAdapter(adapter);
                pager.setCurrentItem(0);
                tvSearchReseult.setText("Posts Found: " + totalBlogsCount);
                pager.setVisibility(View.VISIBLE);

                llBottomLayout.setVisibility(View.VISIBLE);
                tvSearchReseult.setVisibility(View.VISIBLE);
                tvSwipeView.setVisibility(View.VISIBLE);
                vwDivider.setVisibility(View.VISIBLE);
                vwDividerBottom.setVisibility(View.VISIBLE);
            }
        }
        hideKeyboard(view);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case android.R.id.home:
                Helper.hidekeybord(edtSearch);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnNewPost:
            case R.id.llNewPost:
                // create blog
                createBlogFragment = new CreateBlogFragment();
                getActivity().getActionBar().setTitle(R.string.create_blog);
                getFragmentManager().beginTransaction().replace(R.id.Container, createBlogFragment).addToBackStack(null).commit();
                break;

            case R.id.btnSearch:
                // search blog
                searchData();
                break;

            case R.id.edtStartDate:
                DatePickerFragment startDate = new DatePickerFragment();
                startDate.setDateListener(new DateListener()
                {
                    @Override
                    public void onDateSet(int year, int monthOfYear, int dayOfMonth)
                    {
                        edtStartDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
                    }
                });
                startDate.show(getActivity().getFragmentManager(), "datePicker");
                break;

            case R.id.edtEndDate:
                DatePickerFragment endDate = new DatePickerFragment();
                endDate.setDateListener(new DateListener()
                {
                    @Override
                    public void onDateSet(int year, int monthOfYear, int dayOfMonth)
                    {
                        edtEndDate.setText(Helper.showDate(Helper.createStringDate(year, monthOfYear, dayOfMonth)));
                    }
                });
                endDate.show(getActivity().getFragmentManager(), "datePicker");
                break;

            case R.id.btnEdit:

                if (HelperHttp.isNetworkAvailable(getActivity()))
                {
                    // edit blog
                    getActivity().getActionBar().setTitle(R.string.edit_blog);
                    Bundle bundle = new Bundle();
                    bundle.putInt("data", blogs.get(pager.getCurrentItem()).getBlogPostID());
                    bundle.putString("title", getString(R.string.edit_blog));
                    createBlogFragment = new CreateBlogFragment();
                    createBlogFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Container, createBlogFragment).addToBackStack(null).commit();
                } else
                    HelperHttp.showNoInternetDialog(getActivity());

                break;
            case R.id.btnEnd:
                if (HelperHttp.isNetworkAvailable(getActivity()))
                {
                    CustomDialogManager.showOkCancelDialog(getActivity(), "Are you sure you want to end this blog?", new DialogListener()
                    {
                        @Override
                        public void onButtonClicked(int type)
                        {
                            switch (type)
                            {
                                case Dialog.BUTTON_POSITIVE:
                                    endBlog();
                                    break;
                            }
                        }
                    });
                } else
                    HelperHttp.showNoInternetDialog(getActivity());
                break;
        }
    }

    private void searchData()
    {
        isEdited = false;
        blogs.clear();
        pageIndex = 0;
        tvSearchReseult.setVisibility(View.GONE);
        tvSwipeView.setVisibility(View.GONE);
        llBottomLayout.setVisibility(View.GONE);
        if (adapter != null)
        {
            adapter.clear();
            pager.setAdapter(adapter);
        }

        if (chkActiveInactive.isChecked())
            getActiveBlogPost();
        else
            getInActiveBlogPost();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showActionBar("Blog Post");
        if (isEdited && !blogs.isEmpty())
            onClick(btnSearch);
    }

    public void getActiveBlogPost()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showProgressDialog();
            pageStart = pageIndex * 12;
            StringBuilder soapMessag = new StringBuilder();
            soapMessag.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessag.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessag.append("<Body>");
            soapMessag.append("<GetActiveBlogPostXML xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessag.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessag.append("<search>" + Helper.getCDATAString(edtSearch.getText().toString().trim()) + "</search>");
            soapMessag.append("<dateFrom>" + edtStartDate.getText().toString() + "</dateFrom>");
            soapMessag.append("<endDate>" + edtEndDate.getText().toString() + "</endDate>");
            soapMessag.append("<nClient>" + DataManager.getInstance().user.getDefaultClient().getId() + "</nClient>");
            soapMessag.append("<nPage>" + 0 + "</nPage>");
            soapMessag.append("<nPageSize>" + 0 + "</nPageSize>");
            soapMessag.append("</GetActiveBlogPostXML>");
            soapMessag.append("</Body>");
            soapMessag.append("</Envelope>");

            Helper.Log(getTag(), "Request:" + soapMessag.toString());

            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    tvSearchReseult.setText(R.string.no_post_found);
                    tvSearchReseult.setVisibility(View.VISIBLE);

                    hideProgressDialog();
                    showErrorDialog();
                    Helper.Log("Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    Helper.Log(getTag(), "" + response);
                    showResult(response);
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, soapMessag.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IBlogService/GetActiveBlogPostXML", vollyResponseListener);
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

    private void showResult(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            ArrayList<Blog> tempblog = ParserManager.parseSearchBlogRespose(response);
            if (tempblog != null)
            {
                if (!tempblog.isEmpty())
                {
                    int currentItem = blogs.size();
                    blogs.addAll(tempblog);
                    adapter = new SearchBlogAdapter(getActivity(), blogs, new BlogItemEditClickListener()
                    {

                        @Override
                        public void onBlogItemClicked(int position)
                        {

                            // TODO Auto-generated method stub

                        }
                    });
                    adapter.setLoadMoreListener(this);
                    pager.setAdapter(adapter);
                    pager.setCurrentItem((currentItem - 1));
                    totalBlogsCount = blogs.get(0).getTotalCount();
                    tvSearchReseult.setText("Posts found: " + totalBlogsCount);
                    pager.setVisibility(View.VISIBLE);

                    llBottomLayout.setVisibility(View.VISIBLE);
                    tvSearchReseult.setVisibility(View.VISIBLE);
                    tvSwipeView.setVisibility(View.VISIBLE);
                    vwDivider.setVisibility(View.VISIBLE);
                    vwDividerBottom.setVisibility(View.VISIBLE);
                    pageIndex++;
                } else
                {
                    if (pageIndex == 0)
                    {
                        tvSearchReseult.setText(R.string.no_post_found);
                        tvSearchReseult.setVisibility(View.VISIBLE);
                        llBottomLayout.setVisibility(View.GONE);
                        vwDividerBottom.setVisibility(View.GONE);
                    } else
                    {
                        totalBlogsCount = blogs.get(0).getTotalCount();
                        tvSearchReseult.setText("Posts found: " + totalBlogsCount);
                        tvSearchReseult.setVisibility(View.VISIBLE);
                        llBottomLayout.setVisibility(View.VISIBLE);
                        vwDividerBottom.setVisibility(View.VISIBLE);
                    }
                    if (adapter != null)
                        adapter.setLoadMoreEnabled(false);
                }
            } else
            {
                if (adapter != null)
                    adapter.setLoadMoreEnabled(false);
                if (pageIndex == 0)
                {
                    tvSearchReseult.setText(R.string.no_post_found);
                    tvSearchReseult.setVisibility(View.VISIBLE);
                    llBottomLayout.setVisibility(View.GONE);
                    vwDividerBottom.setVisibility(View.GONE);
                } else
                {
                    totalBlogsCount = blogs.get(0).getTotalCount();
                    tvSearchReseult.setText("Posts found: " + totalBlogsCount);
                    tvSearchReseult.setVisibility(View.VISIBLE);
                    llBottomLayout.setVisibility(View.VISIBLE);
                    vwDividerBottom.setVisibility(View.VISIBLE);
                }
            }
            hideProgressDialog();
        }
    }

    public void getInActiveBlogPost()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            showLoadingProgressDialog();
            pageStart = pageIndex * 12;

            StringBuilder soapMessag = new StringBuilder();
            soapMessag.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessag.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessag.append("<Body>");
            soapMessag.append("<GetInActiveBlogPostXML xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessag.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessag.append("<search>" + Helper.getCDATAString(edtSearch.getText().toString().trim()) + "</search>");
            soapMessag.append("<dateFrom>" + edtStartDate.getText().toString() + "</dateFrom>");
            soapMessag.append("<endDate>" + edtEndDate.getText().toString() + "</endDate>");
            soapMessag.append("<nClient>" + DataManager.getInstance().user.getDefaultClient().getId() + "</nClient>");
            soapMessag.append("<nStart>" + pageStart + "</nStart>");
            soapMessag.append("</GetInActiveBlogPostXML>");
            soapMessag.append("</Body>");
            soapMessag.append("</Envelope>");

            Helper.Log(getTag(), "Request:" + soapMessag.toString());

            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    hideProgressDialog();
                    tvSearchReseult.setText(R.string.no_post_found);
                    tvSearchReseult.setVisibility(View.VISIBLE);
                    Helper.Log("Error: ", error.toString());
                }

                @Override
                public void onResponse(String response)
                {
                    if (response == null)
                    {
                        return;
                    }
                    showResult(response);
                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, soapMessag.toString(),
                    Constants.TEMP_URI_NAMESPACE + "IBlogService/GetInActiveBlogPostXML", vollyResponseListener);

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

    private void endBlog()
    {
        if (HelperHttp.isNetworkAvailable(getActivity()))
        {
            Blog blog = blogs.get(pager.getCurrentItem());
            showProgressDialog();

            StringBuilder soapMessage = new StringBuilder();
            soapMessage.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soapMessage.append("<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
            soapMessage.append("<Body>");
            soapMessage.append("<EndBlog xmlns=\"" + Constants.TEMP_URI_NAMESPACE + "\">");
            soapMessage.append("<UserHash>" + DataManager.getInstance().user.getUserHash() + "</UserHash>");
            soapMessage.append("<ID>" + blog.getBlogPostID() + "</ID>");
            soapMessage.append("</EndBlog>");
            soapMessage.append("</Body>");
            soapMessage.append("</Envelope>");

            VollyResponseListener vollyResponseListener = new VollyResponseListener()
            {

                @Override
                public void onErrorResponse(VolleyError error)
                {

                    hideProgressDialog();
                    Helper.Log("Error: ", error.toString());
                    CustomDialogManager.showOkDialog(getActivity(), getString(R.string.failed_to_end_blog));
                }

                @Override
                public void onResponse(String response)
                {
                    hideProgressDialog();
                    if (response == null)
                    {
                        return;
                    }
                    Helper.Log("End Blog Response", "" + response);
                    boolean result = ParserManager.parseEndBlogResponse(response);
                    if (result == true)
                    {
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.blog_ended_successfully), new DialogListener()
                        {
                            @Override
                            public void onButtonClicked(int type)
                            {
                                onClick(btnSearch);
                            }
                        });
                    } else
                    {
                        CustomDialogManager.showOkDialog(getActivity(), getString(R.string.failed_to_end_blog));
                    }

                }
            };

            VollyCustomRequest request = new VollyCustomRequest(Constants.BLOG_WEBSERVICE_URL, soapMessage.toString(), Constants.TEMP_URI_NAMESPACE + "IBlogService/EndBlog", vollyResponseListener);

            try
            {
                request.init();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        } else
        {
            HelperHttp.showNoInternetDialog(getActivity());
        }
    }

    @Override
    public void onLoadMore()
    {
        if (!blogs.isEmpty())
        {
            if (blogs.size() < blogs.get(0).getTotalCount())
            {
                pageIndex++;
                if (chkActiveInactive.isChecked())
                    getActiveBlogPost();
                else
                    getInActiveBlogPost();

                if (adapter != null)
                    adapter.setLoadMoreEnabled(true);
            }
        } else
        {
            if (adapter != null)
                adapter.setLoadMoreEnabled(false);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        /*
		 * if(!ShowcaseSessions.isSessionAvailable(getActivity(),
		 * SearchBlogFragment.class.getSimpleName())){ ArrayList<TargetView>
		 * viewList= new ArrayList<TargetView>(); viewList.add(new
		 * TargetView(btnNewPost,
		 * ShowCaseType.Right,getString(R.string.tap_here_to_create_new_post)));
		 * viewList.add(new TargetView(btnSearch,
		 * ShowCaseType.Left,getString(R.string.tap_here_to_search_post)));
		 * 
		 * ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
		 * showcaseView.setShowcaseView(viewList);
		 * showcaseView.setClickable(true);
		 * ((ViewGroup)getActivity().getWindow()
		 * .getDecorView()).addView(showcaseView);
		 * ShowcaseSessions.saveSession(getActivity(),
		 * SearchBlogFragment.class.getSimpleName()); }
		 */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (createBlogFragment != null)
            createBlogFragment.onActivityResult(requestCode, resultCode, data);
    }
}
