
plugins {
    id("com.android.application")
}
android {
    namespace = "net.yageek.strasbourgpark.wear"
    compileSdkVersion = "android-34"
    defaultConfig {
        applicationId = "net.yageek.strasbourgpark.wear"
        minSdk = 24
        targetSdk = 33
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly("com.google.android.wearable:wearable:2.9.0")
    implementation("com.google.android.support:wearable:2.9.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("androidx.wear:wear:1.0.0")

    implementation(project(":common"))
}
