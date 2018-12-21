# Android上的弹窗及其层级关系协调
## Alert弹窗 的一种解决方案
需求：在屏幕的最上方弹出一个横条Alert（警告框） 居于StateBar（状态栏）之下

初始的解决方案是直接修改系统的Toast，可以通过反射字段来达到Toast可以交互的目的。但是在Android8.0以上的手机，Toast 的Animation被固定为了淡入淡出，不可以是上滑下滑的动画效果，所以PASS。  

Android上的**Alert**（警告）弹窗，**本质上**就是一个**View**。 既然是一个View那么我们完全可以自定义与它的交互效果。  

下面说几种我想到的解决方案
### 1. 普通Alert
此时只有Activity一个Window，所以不存在层级之间的覆盖关系。  
直接在**Activity的DecorView上添加AlertView**

### 2. Activity中存在Dialog的时候
如果用上面的方法的话，会出现一个问题，Dialog的阴影把我们的Alert弹窗覆盖上了，针对这个问题我们分析一下   

由于Dialog所属的Window层级为System级别，所以显示优先级高于Activity的Window层级Application

### 3. 直男解决办法
经过观察发现，可以通过`dialog.window.decorView`来获取到dialog的DecorView，那么我们是不是可以将我们的AlertView添加到这个decorView之上呢？
事实证明是可以的，不过Dialog的Window大小需要调整，此时就遇到了另一个问题。如何将Dialog全屏，以便于Alert可以显示在屏幕的最上方。

下面粘贴了关键代码（详见`BaseFullScreenDialog`） + XML布局 双match_parent

```
window?.requestFeature(Window.FEATURE_NO_TITLE)
window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
}

dialog = newDialog()// 创建出dialog对象
dialog.setContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
```
这样Dialog全屏的时候我们的Alert果然就显示在了屏幕的最上方！

不过 **又双叒叕** 引申出另一个问题，如果Dialog中存在输入框，需要弹出输入法，我们知道，默认的dialog在输入法弹出的时候会自动被推上去一段距离，可是我们的dialog已经全屏了啊，此时我又写了一个通用扩展方法`dialog.pushDialogAboveSoftInput(root, content)`其中计算了输入法高度以及NavigationBar高度，来将Dialog xml布局中的**内容view**手动的推上去一段距离。这才勉强算是完成了需求。擦汗...... 

又双叒叕引申出一些关于NavigationBar的坑就不太好处理了

### 4 奇技淫巧解决办法
我们回归问题的原始，又回到最初的起点......   

是因为dialog的阴影挡住了我们的Alert所以才要将Alert添加到Dialog的Window上去。那么我们可不可以将Dialog的阴影去掉，换成改变Activity的透明度。岂不是绕过了上面的所有问题。☆´∀｀☆

首先应该设置dialog弹出时候背景不变黑

```
 dialog.window.setDimAmount(0F)
```

分别在dialog的show 和 dismiss方法中去改变Activity的透明度。

```
// 黑暗 0.0F ~ 1.0F 透明
fun Context.setBackgroundAlpha(alpha: Float) {
    val act = this as? Activity ?: return
    val attributes = act.window.attributes
    attributes.alpha = alpha
    act.window.attributes = attributes
}
```
这样就把问题控制在了AlertView的层级上了，只需要配合WindowManager，就可以实现Alert不会被背景阴影挡住了。

### 5. WindowManager 设置Alert层级
(╥﹏╥...) 
  
我们知道Window的显示是有层级顺序的，层级高的覆盖在层级低的Window之上。  
Activity的层级为 `TYPE_APPLICATION`
PopupWindow的层级为`TYPE_APPLICATION_PANEL`
而我们只需要添加一个层级为`TYPE_APPLICATION_SUB_PANEL`的View到Window上面即可

> These windows are displayed on top their attached window and any{@link #TYPE_APPLICATION_PANEL} panels.

关于层级顺序详见SDK WindowManager.java

**注!!!**  
使用WindowManager如果是在**层级为2000`FIRST_SYSTEM_WINDOW`**以下的是**不需要**申请弹窗权限的。

而这种创建出来的Window是依附于Activity的，如果界面被销毁，或者还没有初始化就去使用WindowManager来`addView`或者`removeView`会报异常`view not attached to window`

针对这个问题，做如下控制

```
if ((act as? Activity)?.isFinishing == true) {
    return
}

windowManager.addView(root, layoutParams)
```
```
Handler().postDelayed({
    if (root.isAttachedToWindow && act?.isFinishing != true) {
        windowManager.removeView(root)
    }
}, DURATION_ANIM)

```
以及关联Application 的 `registerActivityLifecycleCallbacks`生命周期

```
override fun onActivityDestroyed(activity: Activity?) {
    if (root.isAttachedToWindow) {
        windowManager.removeView(root)
    }
}
```

### 6 特殊问题
在one plus 的 9.0 系统上面 windowManager添加顶部Alert会**被statebar遮挡一部分**  

解决办法：给WindowManager.LayoutParams添加如下FLAG

```
layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or // 不获取焦点，以便于在弹出的时候 下层界面仍然可以进行操作
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR // 确保你的内容不会被装饰物(如状态栏)掩盖.

```

### 干脆Dialog都不用了直接用PopupWindow
我们知道popWindow是与Activity共用同一个Window的，这一点上来讲，非常好控制，配合WindowManager 完全可以实现需求。

上面列举了问题所在和解决方法，具体的实现请看代码...
