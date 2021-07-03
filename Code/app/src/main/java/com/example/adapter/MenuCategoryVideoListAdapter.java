package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;

import com.example.model.menu.Category;
import com.example.model.menu.Subcategory;
import com.example.model.menu.VideoStreamApp;
import com.example.streamingapp.R;
import com.example.streamingapp.databinding.ItemMenuCategoryBinding;
import com.example.streamingapp.databinding.ItemMenuSubCategoryBinding;

public class MenuCategoryVideoListAdapter extends BaseExpandableListAdapter {
    VideoStreamApp mVideoStreamApp;
    Context mContext;
    SetOnExpandClickListener mClickListener;

    public MenuCategoryVideoListAdapter(VideoStreamApp mVideoStreamApp, Context mContext, SetOnExpandClickListener mClickListener) {
        this.mVideoStreamApp = mVideoStreamApp;
        this.mContext = mContext;
        this.mClickListener = mClickListener;
    }

    @Override
    public int getGroupCount() {
        return mVideoStreamApp.getCategory().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mVideoStreamApp.getCategory().get(groupPosition).getSubcategory().size();
    }

    @Override
    public Category getGroup(int groupPosition) {
        return mVideoStreamApp.getCategory().get(groupPosition);
    }

    @Override
    public Subcategory getChild(int groupPosition, int childPosition) {
        return mVideoStreamApp.getCategory().get(groupPosition).getSubcategory().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mVideoStreamApp.getCategory().get(groupPosition).getCategoryId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mVideoStreamApp.getCategory().get(groupPosition).getSubcategory().get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View mView = convertView;
        if (mView == null) {
            mView = getView(R.layout.item_menu_category, parent);
        }

        ItemMenuCategoryBinding mBinding = DataBindingUtil.bind(mView);
        assert mBinding != null;
        if (getGroup(groupPosition).getSubcategory() != null && getGroup(groupPosition).getSubcategory().size() > 0) {
            mBinding.ibExp.setVisibility(View.VISIBLE);
        } else {
            mBinding.ibExp.setVisibility(View.GONE);
        }
        mBinding.setTitle(getGroup(groupPosition).getCategoryName());
        mBinding.ibExp.setOnClickListener(v -> {
            mClickListener.onExpand(groupPosition, mBinding.ibExp);
        });
        return mView;
    }

    private View getView(@LayoutRes int layoutId, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layoutId, parent, false);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View mView = convertView;
        if (mView == null) {
            mView = getView(R.layout.item_menu_sub_category, parent);
        }

        ItemMenuSubCategoryBinding mBinding = DataBindingUtil.bind(mView);
        assert mBinding != null;
        mBinding.setTitle(getChild(groupPosition, childPosition).getSubcategoryName());
        return mView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface SetOnExpandClickListener {
        public void onExpand(int groupPos, ImageButton ibExp);
    }
}
