package com.king.basemodel.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;


import com.gyf.immersionbar.ImmersionBar;
import com.king.basemodel.R;
import com.king.basemodel.utils.SharePreferenceUtils;
import com.king.basemodel.widget.LoadingDialog;

import java.io.Serializable;

import butterknife.ButterKnife;

import static com.king.basemodel.common.GlobalKt.log;
import static com.king.basemodel.utils.Judge.p;


public abstract class BaseActivity extends FragmentActivity {
    private LayoutInflater mLayoutInflater;
    protected Context mContext;
    protected Activity mActivity;
    private Toast toast = null;
    private LoadingDialog mProgressDialog;
    private LinearLayout mContentView = null;
    protected int statusBarHeight;
    private View childView = null;
    protected boolean mIsLogin = false;
    protected TextView tvTitle;
    protected String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater.from(this).setFactory2(new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                return null;
            }

            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                return null;
            }
        });

        super.onCreate(savedInstanceState);
        log("BaseActivity", getClass().getSimpleName() + " --  onCreate");
        mContext = this;
        mActivity = this;
        mLayoutInflater = LayoutInflater.from(this);
        mIsLogin = !TextUtils.isEmpty(SharePreferenceUtils.getString(mActivity, "phone"));
        initialze();
        setContentView(R.layout.activity_base);
        mContentView = findViewById(R.id.llContentView);
        setContentView();
        setBackLinstener();
        //找到设置标题的tv 如果调用了setpagetitle设置了标题，并且没有通过其它方式设置标题
        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (tvTitle != null && !TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(tvTitle.getText().toString())) {
            tvTitle.setText(mTitle);
        }

    }

    protected void setBackLinstener() {
        //默认实现返回按钮
        View imgLeft = findViewById(R.id.iv_back);
        if (imgLeft != null) {
            imgLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected void setPageTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle = title;
            if (tvTitle != null) {
                tvTitle.setText(title);
            }

        }
    }

    protected abstract void setContentView();

    protected void initialze() {
    }

    public void setView(int layoutId, boolean isStatusBarCover) {
//        setView(layoutId, isStatusBarCover, -1);
        setView(layoutId, isStatusBarCover, -1, true, R.color.white);
    }

    public void setView(int layoutId, boolean isStatusBarCover, int offsetViewId) {
        mContentView.removeAllViews();
        View childView = mLayoutInflater.inflate(layoutId, null);
        ButterKnife.inject(this, childView);
        statusBarHeight = getStatusBarHeight();
        if (!isStatusBarCover) {
            //状态栏不透明显示时，需要将整体布局下移
            childView.setPadding(childView.getPaddingLeft(), childView.getPaddingTop() + statusBarHeight, childView.getPaddingRight(), childView.getPaddingBottom());
            ImmersionBar.with(this).statusBarDarkFont(true, 1.0f).statusBarColor(R.color.white).init();
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
            ImmersionBar.with(this).statusBarDarkFont(true, 0.0f).autoStatusBarDarkModeEnable(true, 0.2f).init();

        }
        mContentView.addView(childView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public void setView(int layoutId, boolean isStatusBarCover, int offsetViewId, boolean isDarkWord, int statusBarColor) {
        mContentView.removeAllViews();
        View childView = mLayoutInflater.inflate(layoutId, null);
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
                    offsetView.setPadding(offsetView.getPaddingLeft(), offsetView.getPaddingTop() + statusBarHeight, offsetView.getPaddingRight(), offsetView.getPaddingBottom());
                    log("setView", "offsetView.getPaddingTop=" + offsetView.getPaddingTop());
                }
            }
//            ImmersionBar.with(this).statusBarDarkFont(isDarkWord, 1.0f).statusBarColor(android.R.color.transparent).init();
            ImmersionBar.with(this).statusBarDarkFont(isDarkWord, 0.0f).init();

        }
        mContentView.addView(childView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public void setView(int layoutId, boolean isStatusBarCover, boolean isChangeStatusBarCover, int offsetViewId) {
        mContentView.removeAllViews();
        if (isChangeStatusBarCover) {
            if (childView == null) {
                childView = mLayoutInflater.inflate(layoutId, null);
            }
        } else {
            childView = mLayoutInflater.inflate(layoutId, null);
        }
        ButterKnife.inject(this, childView);
        statusBarHeight = getStatusBarHeight();
        if (!isStatusBarCover) {
            //状态栏不透明显示时，需要将整体布局下移
//            childView.setPadding(childView.getPaddingLeft(), childView.getPaddingTop() + statusBarHeight, childView.getPaddingRight(), childView.getPaddingBottom());
            childView.setPadding(childView.getPaddingLeft(), statusBarHeight, childView.getPaddingRight(), childView.getPaddingBottom());
            ImmersionBar.with(this).statusBarDarkFont(true, 1.0f).statusBarColor(android.R.color.white).init();
        } else {
            childView.setPadding(childView.getPaddingLeft(), 0, childView.getPaddingRight(), childView.getPaddingBottom());
            //状态栏透明覆盖时，需要将标题栏下移
            if (offsetViewId > 0) {
                View offsetView = childView.findViewById(offsetViewId);
                if (offsetView != null) {
                    offsetView.setPadding(offsetView.getPaddingLeft(), offsetView.getPaddingTop() + statusBarHeight, offsetView.getPaddingRight(), offsetView.getPaddingBottom());
                    log("setView", "offsetView.getPaddingTop=" + offsetView.getPaddingTop());
                }
            }
//            ImmersionBar.with(this).statusBarDarkFont(true, 0.0f).statusBarColor(android.R.color.transparent).init();
            ImmersionBar.with(this).statusBarDarkFont(true, 0.0f).init();

        }
        mContentView.addView(childView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    /**
     * 设置视图资源
     */
    protected void setView(int layoutId) {
        setView(layoutId, false);
    }

    // ***********************生命周期方法**********************************//
    @Override
    protected void onResume() {
        super.onResume();
//        log("BaseActivity", getClass().getSimpleName() + " --  onResume");
//        JPushInterface.onResume(getApplicationContext());
        // MobclickAgent.onResume(this); // 统计时长
        mIsLogin = !TextUtils.isEmpty(SharePreferenceUtils.getString(getActivity(), "phone"));

    }

    @Override
    protected void onPause() {
        super.onPause();
//        log("BaseActivity", getClass().getSimpleName() + " --  onPause");

//        JPushInterface.onPause(getApplicationContext());
        // MobclickAgent.onPause(this); // 统计时长
    }

    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
//        AppManager.getAppManager().finishActivity(this);
//        log("BaseActivity", getClass().getSimpleName() + " --  onDestroy");
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mContentView != null) {
            mContentView.removeAllViews();
            mContentView = null;
        }
        mLayoutInflater = null;
        childView = null;
        System.gc();
    }

    public Activity getActivity() {
        return mActivity;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    // **********************工具方法***********************************//
    public void showToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        ViewGroup layout = new RelativeLayout(getApplicationContext());
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.common_toast, null);
        TextView tvToast = view.findViewById(R.id.tv_common_toast);
        tvToast.setText(text);
        layout.addView(view);
        toast.setView(layout);
        toast.show();
    }

    public void showProgressDialog() {
        if (isDestroyed()) {
            return;
        }
        if (mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(this);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (!isDestroyed() && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 启动一个activity不带参数
     *
     * @param pClass 要启动的activity
     */
    protected void startActivity(Class<?> pClass) {
        Intent intent = new Intent(mContext, pClass);
        this.startActivity(intent);
    }

    /**
     * 启动一个activity 带有Serializable参数
     * 重载的startActivity方法
     *
     * @param pClass 要启动的activity
     * @param name   键名
     * @param value  值名 这里通常是一个实体类
     */
    protected void startActivity(Class<?> pClass, String name, Serializable value) {
        Intent intent = new Intent(mContext, pClass);
        intent.putExtra(name, value);
        this.startActivity(intent);
    }

    protected void startActivity(Class<?> pClass, String name, Parcelable value) {
        Intent intent = new Intent(mContext, pClass);
        intent.putExtra(name, value);
        this.startActivity(intent);
    }

    /**
     * 启动一个activity,带有bundle参数
     *
     * @param clazz  要启动的activity
     * @param bundle
     */
    protected void startActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(mContext, clazz);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    /**
     * 加载布局
     *
     * @param pResId 布局id
     * @return 视图view
     */
    protected View inflate(int pResId) {
        return mLayoutInflater.inflate(pResId, null);
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

    protected void hideV(View v) {
        v.setVisibility(View.GONE);
    }

    protected void showV(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public boolean isLogin() {
        return p(SharePreferenceUtils.getString(mContext, "id"));
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

}
