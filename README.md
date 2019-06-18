# AopPermissions
Aop实现动态权限请求
改造自https://github.com/crazyqiang/Aopermission.git
用法跟这个一致，

为什么要重新实现》》》》》
由于他采用的Activity实现的权限请求，当请求权限时，由于Activity启动，此时系统权限dialog弹出，
界面会出现一闪而过的半透明，对于有强（she）迫（ji）症的人来说，可能是一种缺陷，故改造采用Fragment实现
