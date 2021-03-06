package com.zpauly.githubapp.view.stars;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zpauly.githubapp.R;
import com.zpauly.githubapp.adapter.ReposRecyclerViewAdapter;
import com.zpauly.githubapp.entity.response.repos.RepositoriesBean;
import com.zpauly.githubapp.listener.OnMenuItemSelectedListener;
import com.zpauly.githubapp.network.activity.ActivityService;
import com.zpauly.githubapp.presenter.star.StarContract;
import com.zpauly.githubapp.presenter.star.StarPresenter;
import com.zpauly.githubapp.widget.RefreshView;
import com.zpauly.githubapp.utils.viewmanager.LoadMoreInSwipeRefreshLayoutMoreManager;
import com.zpauly.githubapp.utils.viewmanager.RefreshViewManager;
import com.zpauly.githubapp.view.ToolbarMenuFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zpauly on 16-7-16.
 */

public class StarsFragment extends ToolbarMenuFragment implements StarContract.View {
    private final String TAG = getClass().getName();

    private StarContract.Presenter mPresenter;

    @BindView(R.id.starred_repos_SRLayout) public SwipeRefreshLayout mStarredReposSRLayout;
    @BindView(R.id.starred_repos_RV) public RecyclerView mStarredReposRV;
    @BindView(R.id.starred_repos_RefreshView) public RefreshView mRefreshView;

    private ReposRecyclerViewAdapter mAdapter;

    private List<RepositoriesBean> list = new ArrayList<>();

    private String sort = ActivityService.SORT_CREATED;
    private String direction = ActivityService.DIRECTION_DESC;

    private LoadMoreInSwipeRefreshLayoutMoreManager loadMoreInSwipeRefreshLayoutMoreManager;
    private RefreshViewManager refreshViewManager;

    @Override
    public void onStop() {
        if (mPresenter != null) {
            mPresenter.stop();
        }
        super.onStop();
    }

    @Override
    protected void initViews(View view) {
        new StarPresenter(getContext(), this);

        setupRecyclerView();
        setupSwipeRefreshLayout();

        setViewManager(new LoadMoreInSwipeRefreshLayoutMoreManager(mStarredReposRV, mStarredReposSRLayout) {
            @Override
            public void load() {
                loadStarredRepositories();
            }
        }, new RefreshViewManager(mRefreshView, mStarredReposSRLayout) {
            @Override
            public void load() {
                loadStarredRepositories();
            }
        });

        loadMoreInSwipeRefreshLayoutMoreManager = getViewManager(LoadMoreInSwipeRefreshLayoutMoreManager.class);
        refreshViewManager = getViewManager(RefreshViewManager.class);
    }

    private void setupSwipeRefreshLayout() {
        mStarredReposSRLayout.measure(View.MEASURED_SIZE_MASK, View.MEASURED_HEIGHT_STATE_SHIFT);
        mStarredReposSRLayout.setColorSchemeResources(R.color.colorAccent);
        mStarredReposSRLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setSwipeRefreshLayoutRefreshing();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void inflateMenu() {
        inflateMenu(R.menu.star_toolbar_menu);
    }

    @Override
    public void createMenu(Menu menu) {
        setOnMenuItemSelectedListener(new OnMenuItemSelectedListener() {
            @Override
            public void onItemSelected(MenuItem item) {
                if (!mRefreshView.isRefreshSuccess())
                    return;
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.star_sort_recently_starred:
                        sort = ActivityService.SORT_CREATED;
                        direction = ActivityService.DIRECTION_DESC;
                        mStarredReposSRLayout.setRefreshing(true);
                        setSwipeRefreshLayoutRefreshing();
                        break;
                    case R.id.star_sort_recently_active:
                        sort = ActivityService.SORT_UPDATED;
                        direction = ActivityService.DIRECTION_DESC;
                        mStarredReposSRLayout.setRefreshing(true);
                        setSwipeRefreshLayoutRefreshing();
                        break;
                }
            }
        });
    }

    private void setSwipeRefreshLayoutRefreshing() {
        StarPresenter presenter = (StarPresenter) mPresenter;
        presenter.setPageId(1);
        loadMoreInSwipeRefreshLayoutMoreManager.setSwipeRefreshLayoutRefreshing(mAdapter);
    }

    private void setupRecyclerView() {
        mAdapter = new ReposRecyclerViewAdapter(getContext());
        mStarredReposRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mStarredReposRV.setAdapter(mAdapter);
    }

    private void loadStarredRepositories() {
        mPresenter.getUserStarredRepos();
    }

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_stars, container, false);
    }

    @Override
    public void setPresenter(StarContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loading(List<RepositoriesBean> starredRepositories) {
        loadMoreInSwipeRefreshLayoutMoreManager.hasNoMoreData(starredRepositories, mAdapter);
        if (mStarredReposSRLayout.isRefreshing()) {
            list.clear();
        }
        list.addAll(starredRepositories);
    }

    @Override
    public void loadFail() {
        mStarredReposSRLayout.setRefreshing(false);
        if (!mRefreshView.isRefreshSuccess()) {
            mRefreshView.refreshFail();
        } else {
            Snackbar.make(mRefreshView, R.string.error_occurred, Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void loadSuccess() {
        mAdapter.swapAllData(list);
        mStarredReposSRLayout.setRefreshing(false);
        if (!mRefreshView.isRefreshSuccess()) {
            mRefreshView.refreshSuccess();
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getSort() {
        return sort;
    }

    @Override
    public String getDirection() {
        return direction;
    }
}
