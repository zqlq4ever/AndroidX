//  启用类型安全的项目访问器功能
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        // 添加阿里云 maven 地址（按需保留，用于网络环境较差时加速）
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { url = uri("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //  加阿里云 maven 地址（按需保留，用于网络环境较差时加速）
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "AndroidX"
include(":app")
include(":base")
