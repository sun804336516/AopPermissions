package com.sxw.permissions_lib.bean;

import android.content.Context;

import java.util.List;

/**
 * Created by mq on 2018/3/28 下午5:19
 * mqcoder90@gmail.com
 */

public class PermissionDenyBean {

    private int requestCode;
    private List<String> denyList;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public List<String> getDenyList() {
        return denyList;
    }

    public void setDenyList(List<String> denyList) {
        this.denyList = denyList;
    }
}
