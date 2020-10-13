package com.king.basemodel.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.king.basemodel.R;

public class LoadingDialog extends Dialog {
    private Context context;

    public LoadingDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_loading_dialog);
        initWindow();
        setCanceledOnTouchOutside(false);
        setCancelable(true);
    }

    /**
     * 添加透明背景
     */
    private void initWindow() {
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);//设置window背景
        dialogWindow.setDimAmount(0);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//设置输入法显示模式
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = context.getResources().getDisplayMetrics().widthPixels;
        lp.gravity = Gravity.CENTER;//中央居中
        dialogWindow.setAttributes(lp);
    }

    public void show() {
        if (isShowing()) {
            return;
        }
        super.show();
    }

}
