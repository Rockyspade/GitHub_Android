package com.zpauly.githubapp.view.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zpauly.githubapp.R;
import com.zpauly.githubapp.base.BaseFragment;
import com.zpauly.githubapp.entity.response.CommentBean;
import com.zpauly.githubapp.presenter.comment.CommentCreateContract;
import com.zpauly.githubapp.presenter.comment.CommentCreatePresenter;
import com.zpauly.githubapp.ui.FloatingActionButton;

/**
 * Created by zpauly on 16/9/8.
 */
public class CommentCreateFragment extends BaseFragment implements CommentCreateContract.View {
    private final String TAG = getClass().getName();

    private CommentCreateContract.Presenter mPresenter;

    private AppCompatEditText mCommentET;
    private FloatingActionButton mCommentFAB;

    private int commentType;
    private String repo;
    private String owner;
    private int number;
    private String sha;

    private MaterialDialog uploadDialog;
    private CommentBean commentBean;

    @Override
    public void onStop() {
        mPresenter.stop();
        super.onStop();
    }

    @Override
    protected void initViews(View view) {
        getAttrs();

        new CommentCreatePresenter(getContext(), this);

        uploadDialog = new MaterialDialog.Builder(getContext())
                .progress(true, 0)
                .cancelable(false)
                .title(R.string.please_wait)
                .content(R.string.uploading)
                .build();

        mCommentET = (AppCompatEditText) view.findViewById(R.id.create_comment_ET);
        mCommentFAB = (FloatingActionButton) view.findViewById(R.id.create_comment_FAB);
        mCommentET.setText("");

        setFloatingActionButton();
    }

    private void getAttrs() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            commentType = bundle.getInt(CommentActivity.COMMENT_TYPE);
            repo = bundle.getString(CommentCreateActivity.REPO);
            owner = bundle.getString(CommentCreateActivity.OWNER);
            number = bundle.getInt(CommentCreateActivity.NUM);
            sha = bundle.getString(CommentCreateActivity.SHA);
        }
    }

    private void setFloatingActionButton() {
        mCommentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCommentET.getText() == null || mCommentET.getText().toString().equals("")) {
                    Snackbar.make(getView(), R.string.please_input_body_first, Snackbar.LENGTH_SHORT).show();
                } else {
                    createAComment();
                }
            }
        });
    }

    private void createAComment() {
        uploadDialog.show();
        mPresenter.createAComment();
    }

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_create_comment, container, false);
    }

    @Override
    public void createCommentSuccess() {
        uploadDialog.dismiss();
        Snackbar.make(getView(), R.string.upload_success, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void createCommentFail() {
        uploadDialog.dismiss();
        Snackbar.make(getView(), R.string.upload_fail, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void creatingComment(CommentBean commentBean) {
        this.commentBean = commentBean;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getRepo() {
        return repo;
    }

    @Override
    public int getNum() {
        return number;
    }

    @Override
    public String getSha() {
        return sha;
    }

    @Override
    public String getCommentBody() {
        return mCommentET.getText().toString();
    }

    @Override
    public int getCommentType() {
        return commentType;
    }

    @Override
    public void setPresenter(CommentCreateContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
