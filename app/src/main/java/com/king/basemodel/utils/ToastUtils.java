package com.king.basemodel.utils;

import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.king.basemodel.R;
import com.king.basemodel.beans.ResponseBean;
import com.king.basemodel.common.GlobalKt;


public class ToastUtils {
    public static final String TAG = ToastUtils.class.getSimpleName();
    private static Toast toast = null;

    public static void showToasts(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(GlobalKt.getAppCtx());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        ViewGroup layout = new RelativeLayout(GlobalKt.getAppCtx());
        View view = LayoutInflater.from(GlobalKt.getAppCtx()).inflate(R.layout.common_toast, null);
        TextView tvToast = view.findViewById(R.id.tv_common_toast);
        tvToast.setText(text);
        layout.addView(view);
        toast.setView(layout);
        toast.show();
    }
    public static void showTopToasts(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(GlobalKt.getAppCtx());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        ViewGroup layout = new RelativeLayout(GlobalKt.getAppCtx());
        View view = LayoutInflater.from(GlobalKt.getAppCtx()).inflate(R.layout.layout_top_toast, null);
        TextView tvToast = view.findViewById(R.id.tv_content);
        tvToast.setText(text);
        layout.addView(view);
        toast.setView(layout);
        toast.show();
    }

    public static void showToasts(String text, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(GlobalKt.getAppCtx());
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        ViewGroup layout = new RelativeLayout(GlobalKt.getAppCtx());
        View view = LayoutInflater.from(GlobalKt.getAppCtx()).inflate(R.layout.common_toast, null);
        TextView tvToast = view.findViewById(R.id.tv_common_toast);
        tvToast.setText(text);
        layout.addView(view);
        toast.setView(layout);
        toast.show();
    }

    public static void showErrorToasts(String jsonStr) {
        String text = "";
        if (toast != null) {
            toast.cancel();
        }
        ResponseBean ResponseBean = (ResponseBean) JsonUtil.json2Bean(jsonStr, ResponseBean.class);
        text = ResponseBean.getMsg();
        try {
            makeToastAndShow(text);
        } catch (Exception e) {
            Looper.prepare();
            makeToastAndShow(text);
            Looper.loop();
        }

    }

    private static void makeToastAndShow(String text) {
        toast = new Toast(GlobalKt.getAppCtx());
        toast.setGravity(Gravity.CENTER, 0, 0);
        ViewGroup layout = new RelativeLayout(GlobalKt.getAppCtx());
        View view = LayoutInflater.from(GlobalKt.getAppCtx()).inflate(R.layout.common_toast, null);
        TextView tvToast = view.findViewById(R.id.tv_common_toast);
        tvToast.setText(text);
        layout.addView(view);
        toast.setView(layout);
        toast.show();
    }
}
