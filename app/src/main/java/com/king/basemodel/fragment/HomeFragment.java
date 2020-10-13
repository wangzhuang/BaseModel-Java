package com.king.basemodel.fragment;

import android.view.ViewGroup;

import com.king.basemodel.R;
import com.king.basemodel.base.BaseFragment;

/**
 * @ClassName HomeFragment
 * @Description 首页Fragment
 * @Author ：WangZhuang
 * @Date : 2020/10/13 13:24
 * @Version :
 */
public class HomeFragment extends BaseFragment {
    @Override
    public void onCreateView(ViewGroup container) {
        setView(R.layout.fragment_home, container, false, true);
    }
}
