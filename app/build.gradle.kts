plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.kotlin.android)
}

android {
   namespace = "com.sd.demo.compose.wheel_picker"
   compileSdk = libs.versions.androidCompileSdk.get().toInt()
   defaultConfig {
      targetSdk = libs.versions.androidCompileSdk.get().toInt()
      minSdk = 21
      applicationId = "com.sd.demo.compose.wheel_picker"
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      vectorDrawables {
         useSupportLibrary = true
      }
   }

   buildTypes {
      release {
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
      kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
   }
}

dependencies {
   implementation(libs.androidx.compose.foundation)
   implementation(libs.androidx.compose.ui.tooling.preview)
   debugImplementation(libs.androidx.compose.ui.tooling)
   debugImplementation(libs.androidx.compose.ui.test.manifest)
   androidTestImplementation(libs.androidx.compose.ui.test.junit4)

   implementation(libs.androidx.compose.material3)
   implementation(libs.androidx.compose.material.ripple)
   implementation(libs.androidx.compose.material.icons.core)

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.lifecycle.viewmodelCompose)
   implementation(libs.androidx.lifecycle.runtimeCompose)

   testImplementation(libs.junit)
   androidTestImplementation(libs.androidx.test.ext.junit)
   androidTestImplementation(libs.androidx.test.espresso.core)

   implementation(project(":lib"))
}