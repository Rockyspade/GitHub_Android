package com.zpauly.githubapp.network.organizations;

import com.zpauly.githubapp.Api;
import com.zpauly.githubapp.base.BaseNetMethod;
import com.zpauly.githubapp.entity.response.OrganizationBean;
import com.zpauly.githubapp.entity.response.UserBean;
import com.zpauly.githubapp.utils.RetrofitUtil;

import java.util.List;

import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zpauly on 16-8-4.
 */

public class OrganizationsMethod extends BaseNetMethod {
    private Retrofit retrofit;

    private OrganizationsService service;

    private static class Nested {
        static OrganizationsMethod instance = new OrganizationsMethod();
    }

    private OrganizationsMethod() {
        retrofit = RetrofitUtil.initRetrofit(Api.GitHubApi);
        service = retrofit.create(OrganizationsService.class);
    }

    public static OrganizationsMethod getInstance() {
        return Nested.instance;
    }

    public void getUserOrgs(Observer<List<OrganizationBean>> observer, String auth, int pageId) {
        service.getUserOrgs(auth, pageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUserOrgs(Observer<List<OrganizationBean>> observer, String auth, String username, int pageId) {
        service.getUserOrgs(auth, username, pageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getMembers(Observer<List<UserBean>> observer, String auth, String org, int pageId) {
        service.getMembers(auth, org, pageId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
