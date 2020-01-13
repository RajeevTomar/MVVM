package com.positivemind.newsapp.di;



import com.positivemind.newsapp.headline.detail.HeadlineDetailFragment;
import com.positivemind.newsapp.headline.list.HeadlineListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RetrofitModule.class,
        RemoteServiceModule.class, DBServiceModule.class,
        ViewModelModule.class})
public interface AppComponent {

    void inject(HeadlineListFragment headlineListFragment);
    void inject(HeadlineDetailFragment headlineDetailFragment);

}
