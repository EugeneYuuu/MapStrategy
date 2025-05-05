plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
}

android {
  namespace = "com.eugene.mapstrategy"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.eugene.mapstrategy"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  signingConfigs {
    create("release") {
      storeFile = File("$rootDir/map-strategy.keystore")
      storePassword = "yushan250"
      keyAlias = "alias_name"
      keyPassword = "yushan250"
    }
  }

  buildTypes {
    getByName("release") {
      signingConfig = signingConfigs.getByName("release")
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation("com.tencent.mm.opensdk:wechat-sdk-android:6.8.0")
  debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
  implementation("com.amap.api:search:9.7.0") {
    exclude(group = "com.amap.apis.utils.core.api", module = "AMapUtilCoreApi")
    exclude(group = "com.amap.apis.utils.core.api", module = "NetProxy")
  }
  implementation("com.amap.api:3dmap:10.0.600")

  implementation("androidx.viewpager2:viewpager2:1.0.0")
  implementation("com.github.bumptech.glide:glide:4.15.1")
  implementation("androidx.recyclerview:recyclerview:1.2.0")
  implementation("com.squareup.okhttp3:okhttp:4.9.1")
  implementation("com.google.code.gson:gson:2.10.1")
  implementation("com.tencent:mmkv:1.3.4")
  implementation("com.google.android.material:material:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.appcompat)
  implementation(libs.play.services.location)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}