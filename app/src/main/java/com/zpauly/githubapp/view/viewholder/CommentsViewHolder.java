package com.zpauly.githubapp.view.viewholder;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.zpauly.githubapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zpauly on 16/9/6.
 */
public class CommentsViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout mLayout;

    public final CircleImageView mAvatarIV;

    public final AppCompatTextView mUsernameTV;

    public final AppCompatTextView mTimeTV;

    public final AppCompatTextView mBodyTV;

    public CommentsViewHolder(View itemView) {
        super(itemView);

        mLayout = (LinearLayout) itemView.findViewById(R.id.comment_item_layout);
        mAvatarIV = (CircleImageView) itemView.findViewById(R.id.comment_item_Avatar);
        mUsernameTV = (AppCompatTextView) itemView.findViewById(R.id.comment_item_username);
        mTimeTV = (AppCompatTextView) itemView.findViewById(R.id.comment_item_time);
        mBodyTV = (AppCompatTextView) itemView.findViewById(R.id.comment_item_body);
    }
}