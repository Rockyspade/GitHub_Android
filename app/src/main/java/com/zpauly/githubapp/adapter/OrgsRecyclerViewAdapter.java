package com.zpauly.githubapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zpauly.githubapp.R;
import com.zpauly.githubapp.entity.response.OrganizationBean;
import com.zpauly.githubapp.utils.ImageUtil;
import com.zpauly.githubapp.view.profile.OthersActivity;
import com.zpauly.githubapp.view.viewholder.UsersViewHolder;

/**
 * Created by zpauly on 16-8-4.
 */

public class OrgsRecyclerViewAdapter extends LoadMoreRecyclerViewAdapter<OrganizationBean, UsersViewHolder> {
    public OrgsRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public UsersViewHolder createContentViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.item_recyclerview_users, parent, false);
        UsersViewHolder viewHolder = new UsersViewHolder(mView);
        return viewHolder;
    }

    @Override
    public void bindContentViewHolder(UsersViewHolder holder, int position) {
        final OrganizationBean data = getData().get(position);
        ImageUtil.loadAvatarImageFromUrl(getContext(), data.getAvatar_url(), holder.mAvatarIV);
        holder.mUsernameTV.setText(data.getLogin());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OthersActivity.launchOrganizationActivity(getContext(), data.getLogin());
            }
        });
    }
}
