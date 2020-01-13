package com.positivemind.newsapp.di;



import com.positivemind.newsapp.data.remote.HeadlineRemoteService;
import com.positivemind.newsapp.db.headline.HeadlineDataSource;
import com.positivemind.newsapp.headline.detail.HeadlineDetailViewModel;
import com.positivemind.newsapp.headline.list.HeadlineListViewModel;
import com.positivemind.newsapp.utils.rx.AppSchedulerProvider;
import com.positivemind.newsapp.utils.rx.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModelModule {

    @Provides
    @Singleton
    public SchedulerProvider provideScheduler()
    {
        return new AppSchedulerProvider();
    }

    @Provides
    public HeadlineListViewModel provideHeadlineViewModel(SchedulerProvider schedulerProvider,
                                                         HeadlineDataSource headlineDataSource,
                                                         HeadlineRemoteService headlineRemoteService)
    {
        return new HeadlineListViewModel(schedulerProvider, headlineRemoteService,
                headlineDataSource);

    }


    @Provides
    public HeadlineDetailViewModel provideHeadlineDetailViewModel(SchedulerProvider schedulerProvider)
    {
        return new HeadlineDetailViewModel(schedulerProvider);

    }

}
