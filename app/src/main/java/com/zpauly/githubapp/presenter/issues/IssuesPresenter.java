package com.zpauly.githubapp.presenter.issues;

import android.content.Context;

import com.zpauly.githubapp.base.NetPresenter;
import com.zpauly.githubapp.entity.response.issues.IssueBean;
import com.zpauly.githubapp.network.issues.IssuesMethod;
import com.zpauly.githubapp.view.issues.IssuesActivity;

import java.util.List;

import rx.Subscriber;

/**
 * Created by zpauly on 16/9/4.
 */
public class IssuesPresenter extends NetPresenter implements IssuesContract.Presenter {
    private final String TAG = getClass().getName();

    private Context mContext;
    private IssuesContract.View mIssuesView;

    private String auth;
    private IssuesMethod issuesMethod;

    private Subscriber<List<IssueBean>> issuesSubscriber;

    private int pageId = 1;

    public IssuesPresenter(Context context, IssuesContract.View view) {
        mContext = context;
        mIssuesView = view;
        view.setPresenter(this);
        start();
    }


    @Override
    public int getPageId() {
        return pageId;
    }

    @Override
    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    @Override
    public void getIssues() {
        issuesSubscriber = new Subscriber<List<IssueBean>>() {
            @Override
            public void onCompleted() {
                mIssuesView.getIssueSuccess();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mIssuesView.getIssueFail();
            }

            @Override
            public void onNext(List<IssueBean> issueBeen) {
                mIssuesView.gettingIssues(issueBeen);
            }
        };
        switch (mIssuesView.getIssueType()) {
            case IssuesActivity.USER_ISSUES:
                issuesMethod.getIssues(issuesSubscriber, auth, mIssuesView.getFilter(), mIssuesView.getState(), null, mIssuesView.getSort(), mIssuesView.getDirection(), null, pageId++);
                break;
            case IssuesActivity.REPO_ISSUES:
                issuesMethod.getARepoIssues(issuesSubscriber, auth, mIssuesView.getUsername(), mIssuesView.getRepoName(), null, mIssuesView.getState(), null, null, null, mIssuesView.getSort(), mIssuesView.getDirection(), null, null, pageId++);
                break;
            case IssuesActivity.ORG_ISSUES:
                issuesMethod.getOrgIssues(issuesSubscriber, auth, mIssuesView.getOrgName(), mIssuesView.getFilter(), mIssuesView.getState(), null, mIssuesView.getSort(), mIssuesView.getDirection(), null, pageId++);
                break;
            default:
                break;
        }

    }

    @Override
    public void start() {
        auth = getAuth(mContext);
        issuesMethod = getMethod(IssuesMethod.class);
    }

    @Override
    public void stop() {
        unsubscribe(issuesSubscriber);
    }
}
