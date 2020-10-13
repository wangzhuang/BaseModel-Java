package com.king.basemodel.fragment;

import android.view.ViewGroup;

import com.king.basemodel.R;
import com.king.basemodel.base.BaseFragment;

/**
 * @ClassName HomeFragment
 * @Description 个人信息Fragment
 * @Author ：WangZhuang
 * @Date : 2020/10/13 13:24
 * @Version :
 */
public class PersonalInformationFragment extends BaseFragment {
    @Override
    public void onCreateView(ViewGroup container) {
        setView(R.layout.fragment_personal_information, container, false, true);
    }
}
