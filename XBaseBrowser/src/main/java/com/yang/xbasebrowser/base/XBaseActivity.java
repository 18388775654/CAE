package com.yang.xbasebrowser.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import com.yang.xbasebrowser.dialog.CustomWeiboDialogUtils;
import com.yang.xbasebrowser.activity.ActivityStack;
import com.yang.xbasebrowser.utils.NetworkUtils;
import com.yang.xbasebrowser.xutils.PermissionUtils;
import com.yang.xbasebrowser.xutils.ToastUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by 繁华 on 2017/5/14.
 */
public abstract class XBaseActivity extends AppCompatActivity {
    private Dialog loadingDialog = null;
    private Context context;
    private Boolean isInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setBaseContentView());//设置布局文件
        ActivityStack.getInstance().pushActivity(this);//将界面加入堆栈
        context = this;//复制上下文
    }

    public abstract int setBaseContentView();
    public abstract void init();

    public boolean getNetworkStatus(){
        return NetworkUtils.isNetworkConnected(this);
    }

    public Context getContext(){
        return context;
    }

    public void toast(String mess){
        ToastUtils.showShort(mess);
    }

    /**
     * 显示正在加载的提示
     * @param message
     */
    public void showLoadingDialog(String message){
        loadingDialog = CustomWeiboDialogUtils.createLoadingDialog(this,message);
        CustomWeiboDialogUtils.showDialog(loadingDialog);
    }

    /**
     * 关闭正在显示的提示
     */
    public void closeLoadingDialog(){
        CustomWeiboDialogUtils.closeDialog(loadingDialog);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode){
            ActivityStack.getInstance().popActivity(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isInit) {
            init();
            isInit = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().popActivity(this);
    }

    public void onPermissionSuccess(){

    }

    public void onPermissionDenied(String[] deniedPermissions){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    public void hasPermission(String... permission){
        PermissionUtils.hasPermissions(context,permission);
    }

    public void requestPermissions(final int requestCode, final String[] permission){
        PermissionUtils.requestPermissions(getContext(), requestCode, permission, new PermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                onPermissionSuccess();
            }

            @Override
            public void onPermissionDenied(String[] deniedPermissions) {
                onPermissionDenied(deniedPermissions);
            }
        });
    }
}
