[Wiki](https://github.com/o0o0oo00/NewToast/wiki)

## Dialog、PopupWindow 使用规范

### Dialog 有机会弹出Warning
需要继承 `NoDarkDialog` 来降级 背景阴影的**Window** 层级

### Dialog 无机会弹出Warning
就正常写 也可继承 `NoDarkDialog` 无影响

### PopWindow
可继承`BasePopWindow`  
适用于底部弹窗 且会适配NavigationBar

## 关于Toast的修改

采用的是 WindowManager

层级设置为 `TYPE_APPLICATION_SUB_PANEL`

> are displayed on top their attached window

由于Activity的层级为`TYPE_APPLICATION`  
PopupWindow的层级为`TYPE_APPLICATION_PANEL`   
所以我们的Toast只需要层级设置为高于前面二者即可。

### 使用

```
warning("warning")
```
