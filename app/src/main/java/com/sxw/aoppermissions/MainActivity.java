package com.sxw.aoppermissions;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sxw.permissions_lib.annotation.NeedPermission;
import com.sxw.permissions_lib.annotation.PermissionCanceled;
import com.sxw.permissions_lib.annotation.PermissionDenied;
import com.sxw.permissions_lib.bean.PermissionCancelBean;
import com.sxw.permissions_lib.bean.PermissionDenyBean;
import com.sxw.permissions_lib.util.SettingUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }
    @NeedPermission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void requestPermission() {
        Toast.makeText(this, "sd卡读写权限申请成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionCanceled
    public void dealCancelPermission(PermissionCancelBean bean) {
        Toast.makeText(bean.getContext(), "sd卡读写权限申请被取消，请求码 :" + bean.getRequestCode(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 权限被拒绝
     *
     * @param permissionDenyBean PermissionDenyBean
     */
    @PermissionDenied
    public void dealDeniedPermission(PermissionDenyBean permissionDenyBean) {
        final Context context = permissionDenyBean.getContext();
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("sd卡读写权限被禁止，需要手动打开")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SettingUtil.go2Setting(context);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }

}
