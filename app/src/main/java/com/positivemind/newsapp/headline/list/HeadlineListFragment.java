package com.positivemind.newsapp.headline.list;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.positivemind.newsapp.NewsApplication;
import com.positivemind.newsapp.R;
import com.positivemind.newsapp.base.BaseFragment;
import com.positivemind.newsapp.databinding.FragmentHeadlineListBinding;
import com.positivemind.newsapp.headline.HeadlineListItemListener;
import com.positivemind.newsapp.headline.HeadlineModel;
import com.positivemind.newsapp.utils.Utils;

import java.util.List;

import javax.inject.Inject;


public class HeadlineListFragment extends BaseFragment<FragmentHeadlineListBinding,
        HeadlineListViewModel> implements HeadlineListItemListener {

    //----------------------------------------------------------------------------------------------
    // Class Variables
    //----------------------------------------------------------------------------------------------
    private OnFragmentInteractionListener mListener;
    private HeadlineRecyclerViewAdapter mHeadLineRecyclerViewAdapter;
    @Inject
    HeadlineListViewModel headlineListViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //init DI
        NewsApplication.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set toolbar title
        getViewDataBinding().toolbarHeadline.setTitle(getResources().getString(R.string.title_headlines));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
        initLiveData();
        initListener();

    }

    @Override
    public void onResume() {
        super.onResume();
        // fetch headlines
        fetchHeadlines();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //----------------------------------------------------------------------------------------------
    // Base Implemented methods
    //----------------------------------------------------------------------------------------------
    @Override
    public HeadlineListViewModel getViewModel() {
        return headlineListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return com.positivemind.newsapp.BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_headline_list;
    }


    public static HeadlineListFragment newInstance() {
        HeadlineListFragment fragment = new HeadlineListFragment();
        return fragment;
    }

    //----------------------------------------------------------------------------------------------
    // Private methods
    //----------------------------------------------------------------------------------------------
    private void initAdapter() {
        RecyclerView recyclerView = getViewDataBinding().rvHeadline;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHeadLineRecyclerViewAdapter = new HeadlineRecyclerViewAdapter(this);
        recyclerView.setAdapter(mHeadLineRecyclerViewAdapter);
    }

    private void initLiveData() {
        //headlines from cloud
        headlineListViewModel.getHeadlineListMutableLiveData().observe(this,
                headlineModelList -> {
                    displayHeadlines(headlineModelList);
                    // save in DB
                    headlineListViewModel.saveNewsInDB(headlineModelList);
                });

        // headlines from DB
        headlineListViewModel.getSavedHeadlinesMutableLiveData().observe(this,
                headlineModelList -> {
                    if (headlineModelList != null && headlineModelList.size() > 0)
                        displayHeadlines(headlineModelList);
                    else {
                        // No articles saved in DB
                        // show no connection view to request connection
                        getViewDataBinding().viewNoConnection.setVisibility(View.VISIBLE);
                    }

                });
        // handle throwable
        headlineListViewModel.getThrowableMutableLiveData().observe(this,
                throwable -> {
                    showSnackBar(throwable.getMessage(), false);
                });

    }

    private void initListener() {
        getViewDataBinding().viewNoConnection.setOnClickListener(v -> {
                    getViewDataBinding().viewNoConnection.setVisibility(View.GONE);
                    fetchHeadlines();
                }
        );
    }


    private void fetchHeadlines() {
        // show shimmer
        getViewDataBinding().shimmerHeadlineList.setVisibility(View.VISIBLE);
        getViewDataBinding().shimmerHeadlineList.startShimmer();
        // check connection
        boolean isConnected = Utils.isDeviceHasInternetConnection(getContext());
        if (isConnected)
            // load data
            headlineListViewModel.fetchTopHeadlinesFromRemote();
        else
            headlineListViewModel.fetchHeadlinesFromDB();
    }

    private void displayHeadlines(List<HeadlineModel> headlineModelList) {
        // stop shimmer animation
        getViewDataBinding().shimmerHeadlineList.stopShimmer();
        // hide shimmer View
        getViewDataBinding().shimmerHeadlineList.setVisibility(View.GONE);
        if (headlineModelList.size() > 0) {
            // set data on adapter
            mHeadLineRecyclerViewAdapter.updateData(headlineModelList);
        } else {
            String noDataMessage = getResources().getString(R.string.data_not_found);
            showSnackBar(noDataMessage, false);
        }
    }


    public interface OnFragmentInteractionListener {
        void onTapHeadline(HeadlineModel headlineModel);
    }


    //----------------------------------------------------------------------------------------------
    // HeadlineListItemListener implemented methods
    //----------------------------------------------------------------------------------------------
    @Override
    public void onClickHeadlineListItem(HeadlineModel headlineModel) {
        // open detail fragment
        if (mListener != null)
            mListener.onTapHeadline(headlineModel);
    }
}
