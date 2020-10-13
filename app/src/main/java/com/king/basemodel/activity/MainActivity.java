package com.king.basemodel.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.king.basemodel.R;
import com.king.basemodel.fragment.AgriculturalMachineryMonitoringFragment;
import com.king.basemodel.fragment.HomeFragment;
import com.king.basemodel.fragment.HomeworkStatisticsFragment;
import com.king.basemodel.fragment.PersonalInformationFragment;
import com.king.basemodel.utils.ToastUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @InjectView(R.id.id_tab_tab1)
    RelativeLayout mTabtab1;
    @InjectView(R.id.id_tab_tab2)
    RelativeLayout mTabtab2;
    @InjectView(R.id.id_tab_tab3)
    RelativeLayout mTabtab3;
    @InjectView(R.id.id_tab_tab4)
    RelativeLayout mTabtab4;

    @InjectView(R.id.tab1)
    ImageView img1;
    @InjectView(R.id.tab2)
    ImageView img2;
    @InjectView(R.id.tab3)
    ImageView img3;
    @InjectView(R.id.tab4)
    ImageView img4;
    @InjectView(R.id.tvOne)
    TextView tvOne;
    @InjectView(R.id.tvTwo)
    TextView tvTwo;
    @InjectView(R.id.tvThree)
    TextView tvThree;
    @InjectView(R.id.tvFour)
    TextView tvFour;
    private final String TAG = MainActivity.class.getSimpleName();
    private String[] permissionArray = new String[]{
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.BODY_SENSORS,
            android.Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.REQUEST_INSTALL_PACKAGES,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED,//-
            android.Manifest.permission.FOREGROUND_SERVICE,//-
            android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,//-
            "android.permission.ACCESS_BACKGROUND_LOCATION",//-
    };
    private List<String> mPermissionList = new ArrayList<>();
    private int currentPage = 0;
    private HomeFragment homePageFragment;
    private HomeworkStatisticsFragment homeworkStatisticsFragment;
    private AgriculturalMachineryMonitoringFragment agriculturalMachineryMonitoringFragment;
    private PersonalInformationFragment personalInformationFragment;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initEvent();
        setSelect(0);
    }

    private void initEvent() {
        mTabtab1.setOnClickListener(this);
        mTabtab2.setOnClickListener(this);
        mTabtab3.setOnClickListener(this);
        mTabtab4.setOnClickListener(this);
    }

    private void checkPermission() {
        mPermissionList.clear();
        //判断哪些权限未授予
        for (int i = 0; i < permissionArray.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissionArray[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissionArray[i]);
            }
        }
    }

    public void setSelect(int i) {
        if (i == currentPage && currentPage != 0) {
            return;
        }
        currentPage = i;
        resetImgs();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        // 把图片设置为亮的，设置内容区域
        switch (i) {
            case 0:
                if (homePageFragment == null) {
                    homePageFragment = new HomeFragment();
                    transaction.add(R.id.frameLayout, homePageFragment);//通过事务向fragmentlayout中添加fragment
                } else {
                    transaction.show(homePageFragment);//显示之前隐藏的fragment
                }
                img1.setImageResource(R.mipmap.ic_home_page_selected);
                tvOne.setTextColor(Color.parseColor("#2D2F3C"));
                break;
            case 1:
                if (homeworkStatisticsFragment == null) {
                    homeworkStatisticsFragment = new HomeworkStatisticsFragment();
                    transaction.add(R.id.frameLayout, homeworkStatisticsFragment);
                } else {
                    transaction.show(homeworkStatisticsFragment);
                }
                img2.setImageResource(R.mipmap.ic_car_net_selected);
                tvTwo.setTextColor(Color.parseColor("#2D2F3C"));
                break;
            case 2:
                if (agriculturalMachineryMonitoringFragment == null) {
                    agriculturalMachineryMonitoringFragment = new AgriculturalMachineryMonitoringFragment();
                    transaction.add(R.id.frameLayout, agriculturalMachineryMonitoringFragment);
                } else {
                    transaction.show(agriculturalMachineryMonitoringFragment);
                }
                img3.setImageResource(R.mipmap.ic_mine_selected);
                tvThree.setTextColor(Color.parseColor("#2D2F3C"));
                break;
            case 3:
                if (personalInformationFragment == null) {
                    personalInformationFragment = new PersonalInformationFragment();
                    transaction.add(R.id.frameLayout, personalInformationFragment);
                } else {
                    transaction.show(personalInformationFragment);
                }
                img4.setImageResource(R.mipmap.ic_mine_selected);
                tvFour.setTextColor(Color.parseColor("#2D2F3C"));
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 切换图片至暗色 原始颜色暗色
     */
    private void resetImgs() {
        img1.setImageResource(R.mipmap.ic_home_page);
        img2.setImageResource(R.mipmap.ic_car_net);
        img3.setImageResource(R.mipmap.ic_mine);
        img4.setImageResource(R.mipmap.ic_mine);
        tvOne.setTextColor(Color.parseColor("#A6A6A6"));
        tvTwo.setTextColor(Color.parseColor("#A6A6A6"));
        tvThree.setTextColor(Color.parseColor("#A6A6A6"));
        tvFour.setTextColor(Color.parseColor("#A6A6A6"));
        tvOne.setTypeface(Typeface.DEFAULT);
        tvTwo.setTypeface(Typeface.DEFAULT);
        tvThree.setTypeface(Typeface.DEFAULT);
        tvFour.setTypeface(Typeface.DEFAULT);
    }

    /**
     * 这里隐藏,用show来显示出来
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (homePageFragment != null) {
            transaction.hide(homePageFragment);
        }
        if (homeworkStatisticsFragment != null) {
            transaction.hide(homeworkStatisticsFragment);
        }
        if (agriculturalMachineryMonitoringFragment != null) {
            transaction.hide(agriculturalMachineryMonitoringFragment);
        }
        if (personalInformationFragment != null) {
            transaction.hide(personalInformationFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tab_tab1:
                setSelect(0);
                break;
            case R.id.id_tab_tab2:
                setSelect(1);
                break;
            case R.id.id_tab_tab3:
                setSelect(2);
                break;
            case R.id.id_tab_tab4:
                setSelect(3);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                exitTime = System.currentTimeMillis();
                ToastUtils.showToasts("再按一次退出");
            } else {
                finish();
            }
        }
        return true;
    }
}