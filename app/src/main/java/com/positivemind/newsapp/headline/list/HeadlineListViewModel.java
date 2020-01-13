package com.positivemind.newsapp.headline.list;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.positivemind.newsapp.base.BaseViewModel;
import com.positivemind.newsapp.data.Constants;
import com.positivemind.newsapp.data.remote.HeadlineRemoteService;
import com.positivemind.newsapp.db.headline.HeadlineDataSource;
import com.positivemind.newsapp.headline.HeadlineModel;
import com.positivemind.newsapp.utils.rx.SchedulerProvider;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Rajeev Tomar on 21,December,2019
 */
public class HeadlineListViewModel extends BaseViewModel {


    //----------------------------------------------------------------------------------------------
    // Class Variables
    //----------------------------------------------------------------------------------------------
    private MutableLiveData<List<HeadlineModel>> headlineListMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<HeadlineModel>> savedHeadlinesMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Throwable> throwableMutableLiveData = new MutableLiveData<>();
    private ObservableBoolean mNoConnection = new ObservableBoolean();

    private HeadlineRemoteService headlineRemoteService;
    private HeadlineDataSource headlineDataSource;


    public HeadlineListViewModel(SchedulerProvider schedulerProvider,
                                 HeadlineRemoteService headlineRemoteService,
                                 HeadlineDataSource headlineDataSource) {
        super(schedulerProvider);
        this.headlineRemoteService = headlineRemoteService;
        this.headlineDataSource = headlineDataSource;
    }



    //----------------------------------------------------------------------------------------------
    // Public methods
    //----------------------------------------------------------------------------------------------
    public MutableLiveData<List<HeadlineModel>> getHeadlineListMutableLiveData() {
        return headlineListMutableLiveData;
    }

    public MutableLiveData<Throwable> getThrowableMutableLiveData() {
        return throwableMutableLiveData;
    }

    public MutableLiveData<List<HeadlineModel>> getSavedHeadlinesMutableLiveData() {
        return savedHeadlinesMutableLiveData;
    }


    public ObservableBoolean getNoConnection() {
        return mNoConnection;
    }

    public void fetchTopHeadlinesFromRemote() {
        mNoConnection.set(false);
        getCompositeDisposable().add(headlineRemoteService.getTopHeadlines(Constants.INDIA_COUNTRY_CODE).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).
                subscribe(
                        headlineDataResponse -> {
                            if (headlineDataResponse != null) {
                                List<HeadlineModel> headlineModels = headlineDataResponse.getArticles();
                                headlineListMutableLiveData.setValue(headlineModels);
                            }
                        }, throwable -> {
                            throwableMutableLiveData.setValue(throwable);
                        }
                ));
    }

    public void fetchHeadlinesFromDB() {
        getCompositeDisposable().add(headlineDataSource.getAll().
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).
                subscribe(headlineModels -> {
                    savedHeadlinesMutableLiveData.setValue(headlineModels);
                }, throwable -> {
                    throwableMutableLiveData.setValue(throwable);
                }));
    }


    void saveNewsInDB(List<HeadlineModel> headlineModelList) {
        if (headlineModelList == null || headlineModelList.size() == 0)
            return;

        setIsLoading(true);
        getCompositeDisposable().add(Observable.fromCallable(() -> {
            // delete all news
            headlineDataSource.deleteAll();
            // insert new news
            headlineDataSource.insertAll(headlineModelList);
            return true;
        }).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).
                subscribe(result -> {
                    setIsLoading(false);
                }, throwable -> {
                    setIsLoading(false);
                    throwableMutableLiveData.setValue(throwable);
                }));
    }


}
