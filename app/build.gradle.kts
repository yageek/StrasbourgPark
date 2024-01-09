plugins {
    id("com.android.application")
}

android {
    namespace = "net.yageek"
    compileSdkVersion = "android-33"

    defaultConfig {
        applicationId = "net.yageek.strasbourgpark"
        minSdk =  14
        targetSdk = 33
        versionName = "1.2.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.android.support:appcompat-v7:27.0.2")
    implementation("com.android.support.constraint:constraint-layout:1.0.2")
    implementation("com.google.android.gms:play-services-maps:11.8.0")
    implementation("com.android.support:design:27.0.2")

    // Api
    implementation(project(":common"))
    // ViewModel and LiveData
    implementation("android.arch.lifecycle:extensions:1.0.0")
    annotationProcessor("android.arch.lifecycle:compiler:1.0.0")

    // Map Utils
    implementation("com.google.maps.android:android-maps-utils:0.4+")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("com.android.support.test:runner:1.0.1")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.1")

}