
plugins {
    id("com.android.application")
}
android {
    namespace = "net.yageek.strasbourgpark.wear"
    compileSdkVersion = "android-33"
    defaultConfig {
        applicationId = "net.yageek.strasbourgpark.wear"
        minSdk = 33
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
    implementation("com.google.android.support:wearable:2.1.0")
    implementation("com.google.android.gms:play-services-wearable:11.8.0")
    implementation("com.android.support:percent:27.0.2")
    implementation("com.android.support:support-v4:27.0.2")
    implementation("com.android.support:recyclerview-v7:27.0.2")
    implementation("com.android.support:wear:27.0.2")

    compileOnly("com.google.android.wearable:wearable:1.0.0")
    implementation("com.google.android.gms:play-services-maps:11.8.0")

    compileOnly("com.google.android.wearable:wearable:2.1.0")
    implementation(project(":common"))
}
