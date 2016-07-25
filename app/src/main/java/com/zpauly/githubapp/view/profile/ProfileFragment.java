package com.zpauly.githubapp.view.profile;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zpauly.githubapp.R;
import com.zpauly.githubapp.base.BaseFragment;
import com.zpauly.githubapp.db.UserDao;
import com.zpauly.githubapp.entity.response.AuthenticatedUserBean;
import com.zpauly.githubapp.presenter.profile.ProfileContract;
import com.zpauly.githubapp.presenter.profile.ProfilePresenter;
import com.zpauly.githubapp.view.events.EventsActivity;
import com.zpauly.githubapp.view.followers.FollowersActivity;
import com.zpauly.githubapp.view.repositories.ReposActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zpauly on 16-6-10.
 */
public class ProfileFragment extends BaseFragment implements ProfileContract.View{
    private final String TAG = getClass().getName();
    private ProfileContract.Presenter mPresenter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CircleImageView mAvatarIV;
    private TextView mLoginTV;
    private TextView mNameTV;
    private TextView mBioTV;
    private TextView mLocationTV;
    private TextView mEmailTV;
    private TextView mJoinTimeTV;
    private TextView mFollowersTV;
    private TextView mFollowingTV;
    private LinearLayout mFollowersLayout;
    private LinearLayout mFollowingLayout;
    private RelativeLayout mEventsLayout;
    private RelativeLayout mReposLayout;
    private RelativeLayout mOrgsLayout;
    private RelativeLayout mGistsLayout;

    @Override
    protected void initViews(View view) {
        new ProfilePresenter(this, getContext());
        mPresenter.start();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.profile_SRLayout);

        mAvatarIV = (CircleImageView) view.findViewById(R.id.profile_avatar);
        mLoginTV = (TextView) view.findViewById(R.id.profile_login_TV);
        mNameTV = (TextView) view.findViewById(R.id.profile_name_TV);
        mBioTV = (TextView) view.findViewById(R.id.profile_bio_TV);
        mLocationTV = (TextView) view.findViewById(R.id.profile_location_TV);
        mEmailTV = (TextView) view.findViewById(R.id.profile_email_TV);
        mJoinTimeTV = (TextView) view.findViewById(R.id.profile_join_time_TV);
        mFollowersTV = (TextView) view.findViewById(R.id.profile_followers_TV);
        mFollowingTV = (TextView) view.findViewById(R.id.profile_following_TV);
        mFollowersLayout = (LinearLayout) view.findViewById(R.id.profile_followers_layout);
        mFollowingLayout = (LinearLayout) view.findViewById(R.id.profile_following_layout);
        mEventsLayout = (RelativeLayout) view.findViewById(R.id.profile_events_layout);
        mReposLayout = (RelativeLayout) view.findViewById(R.id.profile_repos_layout);
        mOrgsLayout = (RelativeLayout) view.findViewById(R.id.profile_orgs_layout);
        mGistsLayout = (RelativeLayout) view.findViewById(R.id.profile_gists_layout);

        setUserInfo();

        setupSwpieRefreshLayout();

        setClickListener();

        mSwipeRefreshLayout.setRefreshing(true);
        loadUserInfo();
    }

    private void setUserInfo() {
        if (userInfo == null)
            return;
        Glide.with(this)
                .load(Uri.parse(userInfo.getAvatar_url()))
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mAvatarIV);
        mFollowersTV.setText(String.valueOf(userInfo.getFollowers()));
        mFollowingTV.setText(String.valueOf(userInfo.getFollowing()));
        mLoginTV.setText(userInfo.getLogin());
        mNameTV.setText(userInfo.getName());
        mBioTV.setText(userInfo.getBio());
        mLocationTV.setText(userInfo.getLocation());
        mEmailTV.setText(userInfo.getEmail());
        mJoinTimeTV.setText(userInfo.getCreated_at());
    }

    @Override
    public void onStop() {
        mPresenter.stop();
        super.onStop();
    }

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void setupSwpieRefreshLayout() {
        mSwipeRefreshLayout.measure(View.MEASURED_SIZE_MASK, View.MEASURED_HEIGHT_STATE_SHIFT);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUserInfo();
            }
        });
    }

    private void setClickListener() {
        mFollowersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(FollowersActivity.FOLLOW_ID, FollowersActivity.FOLLOWERS);
                intent.setClass(getContext(), FollowersActivity.class);
                startActivity(intent);
            }
        });

        mFollowingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(FollowersActivity.FOLLOW_ID, FollowersActivity.FOLLOWING);
                intent.setClass(getContext(), FollowersActivity.class);
                startActivity(intent);
            }
        });

        mEventsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(EventsActivity.EVENTS_ID, EventsActivity.USER_EVENTS);
                intent.setClass(getContext(), EventsActivity.class);
                startActivity(intent);
            }
        });

        mReposLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ReposActivity.class);
                startActivity(intent);
            }
        });

        mOrgsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mGistsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

    private void loadUserInfo() {
        mPresenter.loadUserInfo();
    }

    @Override
    public void loadInfoSuccess() {
        mSwipeRefreshLayout.setRefreshing(false);
        Log.i(TAG, "load success");
        if (UserDao.queryUser() == null) {
            Log.i(TAG, "data save fail");
        } else {
            userInfo = UserDao.queryUser();
            setUserInfo();
        }
    }

    @Override
    public void loadInfoFail() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadInfo(AuthenticatedUserBean user) {
        UserDao.deleteUser();
        UserDao.insertUser(user);
    }

    @Override
    public void setPresenter(ProfileContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
