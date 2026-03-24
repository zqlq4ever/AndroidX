plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.zqlq.common"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
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

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.android)

    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.coordinatorlayout)
    api(libs.androidx.swiperefreshlayout)
    api(libs.androidx.recyclerview)
    api(libs.androidx.annotation)

    api(libs.bundles.lifecycle)

    api(libs.okhttp)
    api(libs.okhttp.logging.interceptor)
    api(libs.retrofit)
    api(libs.retrofit.converter.moshi)
    api(libs.retrofit.converter.gson)
    api(libs.retrofit.adapter.rxjava2)
    api(libs.gson)
    api(libs.moshi)
    api(libs.moshi.kotlin)

    api(libs.bundles.coil)

    api(libs.material)
    api(libs.mmkv)
    api(libs.arouter.api)
    api(libs.immersionbar)
    api(libs.xlog)
    api(libs.brvha)
    api(libs.lottie)
    api(libs.easyfloat)
    api(libs.rwidgethelper)
    api(libs.eventbus)
    api(libs.permissionx)
    api(libs.xpopup)
    api(libs.glide)
    annotationProcessor(libs.glide.compiler)

    api(libs.rxjava2)
    api(libs.rxandroid)
}
