# Zephyr

此依赖是作者在日常开发时, 封装的一些比较常用的类和方法

以下为部分方法:

## ::base

### ViewExtensions.kt

- 应用重启: reloadApp
- 代码中设置 view 参数: .setSize, .setMargins
- Glide 封装方法: .loadRadiusImage...
- 快捷发送 toast: .toast
- 权限: .withPermission(->)
- recyclerView: .addLineDecroation, .addOnLoadMoreListener

### Logger.kt

​ 本地文件日志写入

### FragmentHostView

​ 一个类似 NavController 的 View, 更简单并且没有 NavController 重走生命周期的问题

### PreloadLayoutManager

​ 可预加载数个 item 的 LinearLayoutManager 子类

---

## ::util

### DataStoreHelper

​ androidx.datastore 的封装函数

````kotlin
val preferenceXXX = stringPreferencesKey("key_name")

suspend fun example() {
    preferenceXXX.putValue("the value")
    putPreference(preferenceXXX, "the value")

    preferenceXXX.getValue("the value")
    getPreference(preferenceXXX, "default value")
}
````

### Json

​ Gson 封装函数

### ServiceBuilder

​ retrofit2 网络请求封装类, 请求方法分为同步异步以及可挂起三种

---

## ::vbclass

​ 基于 databinding 的 View 封装, 包含 activity, fragment, adapter, dailog, dialogFragment

​ 简化了 databinding 的使用

````kotlin
class YourActivity : ViewBindingActivity<ActivityYourBinding>() {
    override fun ActivityYourBinding.initBinding() {
        exampleTextView.text = "666"
    }
}
````

---

⚠️代码大量使用了 Global.kt 中的值, 使用需要先使用 application context 赋值

````kotlin
class YourApp {
    override fun onCreate() {
        super.onCreate()
        appContext = context
    }
}
````