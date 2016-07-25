package com.zpauly.githubapp.view.viewholder;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zpauly.githubapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zpauly on 16-6-15.
 */
public class FollowersViewHolder extends RecyclerView.ViewHolder {
    public final CircleImageView mAvatarIV;

    public final AppCompatTextView mUsernameTV;

    public FollowersViewHolder(View itemView) {
        super(itemView);

        mAvatarIV = (CircleImageView) itemView.findViewById(R.id.followers_avatar_IV);
        mUsernameTV = (AppCompatTextView) itemView.findViewById(R.id.followers_username_TV);
    }
}
