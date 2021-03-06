package com.yang.xbasebrowser.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yang.xbasebrowser.dialog.CustomWeiboDialogUtils;
import com.yang.xbasebrowser.utils.NetworkUtils;
import com.yang.xbasebrowser.xutils.ToastUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created: 2018/3/23 9:52
 * Author:fanhua
 * Email:90fanhua@gmail.com
 * Project:XBaseAndroid
 * Use    :Fragment基类
 */
public abstract class XBaseFragment extends Fragment {
    private Dialog loadingDialog = null;
    private Boolean isInit = false;


    public abstract int setBaseContentView();
    public abstract void init();

    public boolean getNetworkStatus(){
        return NetworkUtils.isNetworkConnected(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(setBaseContentView(),null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!isInit) {
            init();
            isInit = true;
        }
    }

    public Context getContext(){
        return getActivity();
    }

    public void toast(String mess){
        ToastUtils.showShort(mess);
    }

    /**
     * 显示正在加载的提示
     * @param message
     */
    public void showLoadingDialog(String message){
        loadingDialog = CustomWeiboDialogUtils.createLoadingDialog(getContext(),message);
    }

    /**
     * 关闭正在显示的提示
     */
    public void closeLoadingDialog(){
        CustomWeiboDialogUtils.closeDialog(loadingDialog);
    }
}
