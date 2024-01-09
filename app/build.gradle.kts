plugins {
    id("com.android.application")
}

android {
    namespace = "net.yageek.strasbourgpark"
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
    implementation("androidx.appcompat:appcompat:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("com.google.android.gms:play-services-maps:11.8.0")
    implementation("com.google.android.material:material:1.0.0")

    // Api
    implementation(project(":common"))
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:2.0.0")

    // Map Utils
    implementation("com.google.maps.android:android-maps-utils:0.4+")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0")

}