package com.sxw.permissions_lib.aop;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;

import com.sxw.permissions_lib.PermissionFragment;
import com.sxw.permissions_lib.annotation.NeedPermission;
import com.sxw.permissions_lib.annotation.PermissionCanceled;
import com.sxw.permissions_lib.annotation.PermissionDenied;
import com.sxw.permissions_lib.bean.PermissionCancelBean;
import com.sxw.permissions_lib.bean.PermissionDenyBean;
import com.sxw.permissions_lib.interf.IPermission;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


/**
 * 权限切面Aspect类
 * Created by mq on 2018/3/6 上午11:33
 * mqcoder90@gmail.com
 */
@Aspect
public class PermissionAspectJ {
    private static final String THROW_STR = "your method [%s] must only has one ParameterType:%s";

    private static final String TAG = "PermissionFragment";
    Context context;

    private static final String PERMISSION_REQUEST_POINTCUT =
            "execution(@com.sxw.permissions_lib.annotation.NeedPermission * *(..))";

    @Pointcut(PERMISSION_REQUEST_POINTCUT + " && @annotation(needPermission)")
    public void requestPermissionMethod(NeedPermission needPermission) {
    }

    @Around("requestPermissionMethod(needPermission)")
    public void AroundJoinPoint(final ProceedingJoinPoint joinPoint, NeedPermission needPermission) {
        final Object object = joinPoint.getThis();
        if (object == null || needPermission == null) {
            return;
        }
        FragmentManager manager = null;
        if (object instanceof FragmentActivity) {
            context = (FragmentActivity) object;
            manager = ((FragmentActivity) object).getSupportFragmentManager();
        } else if (object instanceof Fragment) {
            context = ((Fragment) object).getActivity();
            manager = ((Fragment) object).getChildFragmentManager();
        } else {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                for (Object obj : args) {
                    if (obj instanceof FragmentActivity) {
                        context = (FragmentActivity) object;
                        manager = ((FragmentActivity) object).getSupportFragmentManager();
                        break;
                    } else if (obj instanceof Fragment) {
                        context = ((Fragment) object).getActivity();
                        manager = ((Fragment) object).getChildFragmentManager();
                        break;
                    }
                }
            }
        }
        if (manager == null || context == null) {
            Log.d(TAG, "不支持此类" + object + "进行权限请求");
            return;
        }
        PermissionFragment fragment = (PermissionFragment) manager.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = PermissionFragment.instance(needPermission.requestCode(), needPermission.value());
            manager.beginTransaction().add(fragment, TAG).commitNow();
        }
        fragment.requestPermission(new IPermission() {
            @Override
            public void PermissionGranted() {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void PermissionDenied(int requestCode, List<String> denyList) {
                Class<?> cls = object.getClass();
                Method[] methods = cls.getDeclaredMethods();
                if (methods == null || methods.length == 0) {
                    return;
                }
                for (Method method : methods) {
                    //过滤不含自定义注解PermissionDenied的方法
                    boolean isHasAnnotation = method.isAnnotationPresent(PermissionDenied.class);
                    if (isHasAnnotation) {
                        method.setAccessible(true);
                        //获取方法类型
                        Class<?>[] types = method.getParameterTypes();
                        String throwStr = null;
                        if (types == null || types.length == 0) {
                            throwStr = String.format(THROW_STR, method.getName(), "PermissionCancelBean");
                        } else if (types.length == 1) {
                            Object newInstance = null;
                            try {
                                newInstance = types[0].newInstance();
                                Log.d(TAG, newInstance.toString());
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            if (newInstance == null || !(newInstance instanceof PermissionDenyBean)) {
                                throwStr = String.format(THROW_STR, method.getName(), "PermissionDenyBean");
                            }
                        } else {
                            throwStr = String.format(THROW_STR, method.getName(), "PermissionDenyBean");
                        }
                        if (!TextUtils.isEmpty(throwStr)) {
                            throw new IllegalArgumentException(throwStr);
                        }

                        //获取方法上的注解
                        PermissionDenied aInfo = method.getAnnotation(PermissionDenied.class);
                        if (aInfo == null) {
                            return;
                        }
                        //解析注解上对应的信息
                        PermissionDenyBean bean = new PermissionDenyBean();
                        bean.setRequestCode(requestCode);
                        bean.setContext(context);
                        bean.setDenyList(denyList);
                        try {
                            method.invoke(object, bean);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void PermissionCanceled(int requestCode) {
                Class<?> cls = object.getClass();
                Method[] methods = cls.getDeclaredMethods();
                if (methods == null || methods.length == 0) {
                    return;
                }
                for (Method method : methods) {
                    //过滤不含自定义注解PermissionCanceled的方法
                    boolean isHasAnnotation = method.isAnnotationPresent(PermissionCanceled.class);
                    if (isHasAnnotation) {
                        method.setAccessible(true);
                        //获取方法类型
                        Class<?>[] types = method.getParameterTypes();
                        String throwStr = null;
                        if (types == null || types.length == 0) {
                            throwStr = String.format(THROW_STR, method.getName(), "PermissionCancelBean");
                        } else if (types.length == 1) {
                            Object newInstance = null;
                            try {
                                newInstance = types[0].newInstance();
                                Log.d(TAG, newInstance.toString());
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            if (newInstance == null || !(newInstance instanceof PermissionCancelBean)) {
                                throwStr = String.format(THROW_STR, method.getName(), "PermissionCancelBean");
                            }
                        } else {
                            throwStr = String.format(THROW_STR, method.getName(), "PermissionCancelBean");
                        }
                        if (!TextUtils.isEmpty(throwStr)) {
                            throw new IllegalArgumentException(throwStr);
                        }

                        //获取方法上的注解
                        PermissionCanceled aInfo = method.getAnnotation(PermissionCanceled.class);
                        if (aInfo == null) {
                            return;
                        }
                        //解析注解上对应的信息
                        PermissionCancelBean bean = new PermissionCancelBean();
                        bean.setContext(context);
                        bean.setRequestCode(requestCode);
                        try {
                            method.invoke(object, bean);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

}
