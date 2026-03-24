# AndroidX

一个基于 **Kotlin** 开发的 Android 技术示例项目，采用 **MVVM + Jetpack** 架构，集成了多种 Android 开发常用功能模块和自定义组件。

## 项目概览

本项目采用模块化架构设计，包含 `app` 主模块和 `base` 基础模块，展示了现代 Android 开发的最佳实践。

## 功能模块

### 1. 手势控制系统

支持双指缩放和单指拖拽的通用手势解决方案。

**核心特性：**
- 双指缩放（ScaleGesture）
- 单指拖拽（ScrollGesture）
- 自适应边界约束
- 支持全屏模式切换

**使用方式：**
```kotlin
// 为任意 View 绑定手势控制
GestureScaleHelper.bind(context, parentView, targetView)
```

**相关文件：**
- [GestureScaleHelper.kt](app/src/main/java/com/luqian/androidx/gesture/GestureScaleHelper.kt)
- [ScaleGestureListener.kt](app/src/main/java/com/luqian/androidx/gesture/ScaleGestureListener.kt)
- [ScrollGestureListener.kt](app/src/main/java/com/luqian/androidx/gesture/ScrollGestureListener.kt)

### 2. ECG 心电图绘制

医疗级心电图数据可视化组件，支持实时波形渲染和滚动浏览。

**核心特性：**
- 平滑曲线绘制（贝塞尔曲线优化）
- 动态网格背景（主次网格区分）
- 发光效果渲染
- 数据范围高亮指示
- 支持大数据量展示

**相关文件：**
- [EcgAllDataView.kt](app/src/main/java/com/luqian/androidx/widget/ecgview/EcgAllDataView.kt) - 全数据概览视图
- [ScrollEcgView.kt](app/src/main/java/com/luqian/androidx/widget/ecgview/ScrollEcgView.kt) - 滚动浏览视图

### 3. WiFi 管理模块

完整的 WiFi 扫描、连接管理解决方案。

**核心特性：**
- WiFi 热点扫描与列表展示
- 信号强度可视化（图标分级）
- 密码输入弹窗
- 连接状态监听
- 运行时权限处理

**相关文件：**
- [WifiActivity.kt](app/src/main/java/com/luqian/androidx/ui/wifi/WifiActivity.kt)
- [WifiUtil.kt](app/src/main/java/com/luqian/androidx/uitls/WifiUtil.kt)
- [WifiAdapter.kt](app/src/main/java/com/luqian/androidx/ui/wifi/WifiAdapter.kt)

### 4. CameraX 相机模块

基于 CameraX 的现代化相机功能实现。

**核心特性：**
- 前后摄像头切换
- 闪光灯控制
- 触摸对焦
- 照片拍摄与预览
- 自动宽高比适配

**相关文件：**
- [CameraActivity.kt](app/src/main/java/com/luqian/androidx/ui/camerax/CameraActivity.kt)
- [CameraViewModel.kt](app/src/main/java/com/luqian/androidx/ui/camerax/CameraViewModel.kt)


## 依赖配置

本项目使用 **Gradle Version Catalog** 进行依赖版本统一管理：

```toml
[versions]
compileSdk = "36"
minSdk = "23"
targetSdk = "35"
kotlin = "2.3.10"
agp = "9.1.0"

[libraries]
androidx-core-ktx = { module = "androidx.core:core-ktx", version.ref = "core" }
androidx-room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
```

## 快速开始

### 环境要求

- Android Studio Koala 或更高版本
- JDK 17
- Android SDK 36

## 自定义组件使用示例

### WaveView 波浪动画

```kotlin
// 在 XML 布局中使用
<com.luqian.androidx.widget.WaveView
    android:id="@+id/waveView"
    android:layout_width="match_parent"
    android:layout_height="200dp" />
```

### ZoomView 缩放视图

```kotlin
// 启用缩放功能
val zoomView = findViewById<ZoomView>(R.id.zoomView)
zoomView.setMaxScale(3.0f)
```

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进本项目。
