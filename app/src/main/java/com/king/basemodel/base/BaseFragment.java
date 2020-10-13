package com.king.basemodel.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.gyf.immersionbar.ImmersionBar;
import com.king.basemodel.R;
import com.king.basemodel.utils.SharePreferenceUtils;
import com.king.basemodel.utils.ToastUtils;
import com.king.basemodel.widget.LoadingDialog;

import java.io.Serializable;

import butterknife.ButterKnife;

import static com.king.basemodel.common.GlobalKt.log;
import static com.king.basemodel.utils.Judge.p;


/**
 * Created by Zmy on 2015-10-21.
 * Project: CarWin2.0_android.
 */
public abstract class BaseFragment extends Fragment {
    private View rootView;//定义一个缓存View
    private LayoutInflater mInflate;
    protected Context mContext;
    protected Activity mActivity;
    private LoadingDialog mProgressDialog;
    protected int statusBarHeight;
    protected boolean mHidden = false;

    private final String TAG = "BaseFragment";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach: " + getClass().getSimpleName());
        mContext = activity.getApplicationContext();
        mActivity = getActivity();    // 保证Fragment即使在onDetach后，仍持有Activity的引用
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: " + getClass().getSimpleName());
        if (rootView != null) {
            Log.d(TAG, "onCreateView: rootView != null  " + getClass().getSimpleName());

            // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，
            // 要不然会发生这个rootview已经有parent的错误。
//            ViewGroup parent = (ViewGroup) rootView.getParent();
//            if (null != parent) {
//                parent.removeView(rootView);
//            }
            //直接返回，不用重建视图，缓存对象
            return rootView;
        } else {
            setHasOptionsMenu(true);
            mActivity = getActivity();
            this.mInflate = inflater;
            //初始化图片载入工具
            //初始化View，执行数据逻辑
            onCreateView(container);
            Log.d(TAG, "onCreateView: rootView == null  " + getClass().getSimpleName());
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + getClass().getSimpleName());

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e(TAG, getClass().getSimpleName() + "-- onHiddenChanged:  ---  " + hidden);
        mHidden = hidden;
        super.onHiddenChanged(hidden);
    }

    /**
     * 启动一个activity不带参数
     *
     * @param pClass 要启动的activity
     */
    protected void startActivity(Class<?> pClass) {
        Intent intent = new Intent(getActivity(), pClass);
        getActivity().startActivity(intent);

    }


    /**
     * 启动一个activity 带有Serializable参数
     *
     * @param pClass 要启动的activity
     * @param name   键名
     * @param value  值名 这里通常是一个实体类
     */
    protected void startActivity(Class<?> pClass, String name, Serializable value) {
        Intent intent = new Intent(getActivity(), pClass);
        intent.putExtra(name, value);
        startActivity(intent);
    }

    protected void startActivity4Result(Class<?> pClass, int requestCode, String name, Serializable value) {
        Intent intent = new Intent(getActivity(), pClass);
        intent.putExtra(name, value);
        getActivity().startActivityForResult(intent, requestCode);
    }

    /**
     * 启动一个activity,带有bundle参数
     *
     * @param clazz  要启动的activity
     * @param bundle
     */
    protected void startActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showToast(String ToastMsg) {
        ToastUtils.showToasts(ToastMsg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: " + getClass().getSimpleName());
    }

    public View setView(int layoutId, ViewGroup container, boolean b, boolean isStatusBarCover) {
//        return setView(layoutId, container, b, isStatusBarCover, -1);
        return setView(layoutId, container, b, isStatusBarCover, -1, true, R.color.white);
    }

    public View setView(int layoutId, ViewGroup container, boolean b) {
        View childView = mInflate.inflate(layoutId, container, b);
        this.rootView = childView;
        ButterKnife.inject(this, childView);
        return childView;
    }

    public View setViewNo(int layoutId, ViewGroup container, boolean b, boolean isStatusBarCover, int offsetViewId) {
        View childView = mInflate.inflate(layoutId, container, b);
        this.rootView = childView;
        ButterKnife.inject(this, childView);
        statusBarHeight = getStatusBarHeight();
        if (!isStatusBarCover) {
            //状态栏不透明显示时，需要将整体布局下移
            childView.setPadding(childView.getPaddingLeft(), childView.getPaddingTop() + statusBarHeight, childView.getPaddingRight(), childView.getPaddingBottom());
            ImmersionBar.with(this).statusBarDarkFont(false, 1.0f).statusBarColor(R.color.white).init();
//            ImmersionBar.with(this).statusBarDarkFont(true, 1.0f).init();
        } else {
            //状态栏透明覆盖时，需要将标题栏下移
            if (offsetViewId > 0) {
                View offsetView = childView.findViewById(offsetViewId);
                if (offsetView != null) {
                    offsetView.setPadding(offsetView.getPaddingLeft(), offsetView.getPaddingTop() + statusBarHeight, offsetView.getPaddingRight(), offsetView.getPaddingBottom());
                    log("setView", "offsetView.getPaddingTop=" + offsetView.getPaddingTop());
                }
            }
//            ImmersionBar.with(this).statusBarDarkFont(false, 0.0f).statusBarColor(android.R.color.transparent).init();
            ImmersionBar.with(this).statusBarDarkFont(false, 0.0f).init();
        }
        return childView;
    }

    public View setView(int layoutId, ViewGroup container, boolean b, boolean isStatusBarCover, int offsetViewId) {
        View childView = mInflate.inflate(layoutId, container, b);
        this.rootView = childView;
        ButterKnife.inject(this, childView);
        statusBarHeight = getStatusBarHeight();
        if (!isStatusBarCover) {
            //状态栏不透明显示时，需要将整体布局下移
            childView.setPadding(childView.getPaddingLeft(), childView.getPaddingTop() + statusBarHeight, childView.getPaddingRight(), childView.getPaddingBottom());
            ImmersionBar.with(this).statusBarDarkFont(false, 1.0f).statusBarColor(R.color.white).init();
//            ImmersionBar.with(this).statusBarDarkFont(true, 1.0f).init();
        } else {
            //状态栏透明覆盖时，需要将标题栏下移
            if (offsetViewId > 0) {
                View offsetView = childView.findViewById(offsetViewId);
                if (offsetView != null) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(offsetView.getLayoutParams());
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + statusBarHeight, layoutParams.rightMargin, layoutParams.bottomMargin);
                    offsetView.setLayoutParams(layoutParams);
                }
            }
            ImmersionBar.with(this).statusBarDarkFont(false, 0.0f).init();
        }
        return childView;
    }

    public View setView(int layoutId, ViewGroup container, boolean b, boolean isStatusBarCover, int offsetViewId, boolean isDarkWord, int statusBarColor) {
        View childView = mInflate.inflate(layoutId, container, b);
        this.rootView = childView;
        ButterKnife.inject(this, childView);
        statusBarHeight = getStatusBarHeight();
        if (!isStatusBarCover) {
            //状态栏不透明显示时，需要将整体布局下移
            childView.setPadding(childView.getPaddingLeft(), childView.getPaddingTop() + statusBarHeight, childView.getPaddingRight(), childView.getPaddingBottom());
            ImmersionBar.with(this).statusBarDarkFont(isDarkWord, 1.0f).statusBarColor(statusBarColor).init();
        } else {
            //状态栏透明覆盖时，需要将标题栏下移
            if (offsetViewId > 0) {
                View offsetView = childView.findViewById(offsetViewId);
                if (offsetView != null) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(offsetView.getLayoutParams());
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + statusBarHeight, layoutParams.rightMargin, layoutParams.bottomMargin);
                    offsetView.setLayoutParams(layoutParams);
                }
            }
//            ImmersionBar.with(this).statusBarDarkFont(isDarkWord, 0.0f).statusBarColor(android.R.color.transparent).init();
            ImmersionBar.with(this).statusBarDarkFont(isDarkWord, 0.0f).init();

        }
        return childView;
    }

    /**
     * 用于子类View初始化以及数据的操作
     */
    public abstract void onCreateView(ViewGroup container);

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(mActivity);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public boolean isLogin() {
        return p(SharePreferenceUtils.getString(mContext, "id"));
    }

    protected int getStatusBarHeight() {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        } else {
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
                statusBarHeight = getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        log("getStatusBarHeight", "" + statusBarHeight);
        return statusBarHeight;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "setUserVisibleHint: " + getClass().getSimpleName() + "  isVisibleToUser --> " + isVisibleToUser);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated:  " + getClass().getSimpleName());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy:  " + getClass().getSimpleName());

    }
}
