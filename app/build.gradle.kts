plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.luqian.androidx"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.luqian.androidx"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(project(":base"))

    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.flowlayout)
    implementation(libs.agentweb.core)
    implementation(libs.liveeventbus)
    implementation(libs.persistentcookiejar)
    implementation(libs.banner)
    implementation(libs.androidutilcode)

    // 这些依赖在 app 源码里有直接使用（不再由 :base 透传）
    implementation(libs.bundles.coil)
    implementation(libs.xlog)
    implementation(libs.eventbus)
    implementation(libs.permissionx)
    implementation(libs.xpopup)
    implementation(libs.brvha)

    implementation(libs.bundles.camerax)
}
