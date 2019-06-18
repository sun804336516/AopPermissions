package com.sxw.permissions_lib;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.sxw.permissions_lib.interf.IPermission;
import com.sxw.permissions_lib.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者：sxw on 2019/6/18 13:38
 */
public class PermissionFragment extends Fragment {

    private static final String TAG = "PermissionFragment";

    private IPermission permissionListener;
    private String[] permissions;
    private static final String PERMISSION_KEY = "permission_key";
    private static final String REQUEST_CODE = "request_code";
    private int requestCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Log.d(TAG, "onCreate");
    }

    public static PermissionFragment instance(int requestCode, String... permissions) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSION_KEY, permissions);
        bundle.putInt(REQUEST_CODE, requestCode);

        PermissionFragment fragment = new PermissionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    /**
     * 申请权限
     */
    public void requestPermission(IPermission permissionListener) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            permissions = bundle.getStringArray(PERMISSION_KEY);
            requestCode = bundle.getInt(REQUEST_CODE, 0);
        }
        if (permissions == null || permissions.length <= 0 || permissionListener == null) {
            return;
        }
        this.permissionListener = permissionListener;
        if (PermissionUtil.hasSelfPermissions(getContext(), permissions)) {
            //all permissions granted
            if (permissionListener != null) {
                permissionListener.PermissionGranted();
                this.permissionListener = null;
            }
        } else {
            //request permissions
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionListener == null) {
            return;
        }
        if (PermissionUtil.verifyPermissions(grantResults)) {
            //所有权限都同意
            permissionListener.PermissionGranted();
        } else {
            if (!PermissionUtil.shouldShowRequestPermissionRationale(getActivity(), permissions)) {
                //权限被拒绝并且选中不再提示
                if (permissions.length != grantResults.length) {
                    return;
                }
                List<String> denyList = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        denyList.add(permissions[i]);
                    }
                }
                permissionListener.PermissionDenied(requestCode, denyList);
            } else {
                //权限被取消
                permissionListener.PermissionCanceled(requestCode);
            }

        }
        permissionListener = null;
    }
}
