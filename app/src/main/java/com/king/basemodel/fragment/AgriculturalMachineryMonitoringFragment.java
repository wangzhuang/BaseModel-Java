package com.king.basemodel.fragment;

import android.view.ViewGroup;

import com.king.basemodel.R;
import com.king.basemodel.base.BaseFragment;

/**
 * @ClassName AgriculturalMachineryMonitoringFragment
 * @Description 农机监控Fragment
 * @Author ：WangZhuang
 * @Date : 2020/10/13 14:24
 * @Version :
 */
public class AgriculturalMachineryMonitoringFragment extends BaseFragment {
    @Override
    public void onCreateView(ViewGroup container) {
        setView(R.layout.fragment_agricultural_machinery_monitoring, container, false, true);
    }
}
