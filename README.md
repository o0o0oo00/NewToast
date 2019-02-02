阅前请看 [Wiki](https://github.com/o0o0oo00/NewToast/wiki)

实现在Dialog存在的情况下，仍然能够显示一个警告框Alert 而不被Dialog覆盖在阴影之下

采用的是 **`WindowManager`** 而**不需要**申请权限

层级设置为 `TYPE_APPLICATION_SUB_PANEL`

> are displayed on top their attached window

由于Activity的层级为`TYPE_APPLICATION`  
PopupWindow的层级为`TYPE_APPLICATION_PANEL`   
所以我们的Toast只需要层级设置为高于前面二者即可。

### 使用

```
warning("warning")
```

如下图所示，我的Alert显示在了Dialog的**阴影之上**


![image](https://raw.githubusercontent.com/o0o0oo00/NewToast/master/image/image.png)
