package com.king.basemodel.activity;

import com.king.basemodel.R;
import com.king.basemodel.base.BaseActivity;

/**
 * @ClassName LoginActivity
 * @Description TODO
 * @Author ：WangZhuang
 * @Date : 2020/10/13 15:42
 * @Version :
 */
public class LoginActivity extends BaseActivity {
    @Override
    protected void setContentView() {
        setView(R.layout.activity_login, true, -1, true, R.color.white);
    }
}
