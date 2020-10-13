package com.king.basemodel.fragment;

import android.view.ViewGroup;

import com.king.basemodel.R;
import com.king.basemodel.base.BaseFragment;

/**
 * @ClassName HomeworkStatisticsFragment
 * @Description 作业统计Fragment
 * @Author ：WangZhuang
 * @Date : 2020/10/13 14:21
 * @Version :
 */
public class HomeworkStatisticsFragment extends BaseFragment {
    @Override
    public void onCreateView(ViewGroup container) {
        setView(R.layout.fragment_homework_statistics, container, false, true);
    }
}
