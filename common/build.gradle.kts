plugins {
    id("com.android.library")
}

android {
    namespace = "net.yageek.strasbourgpark.common"
    compileSdkVersion = "android-34"

    defaultConfig {
        minSdk =  24
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
    implementation("com.squareup.okhttp3:okhttp:3.9.1")
    implementation("com.google.code.gson:gson:2.8.2")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0")
}

//
//android {
//    compileSdkVersion 27
//    defaultConfig {
//        minSdkVersion 14
//        targetSdkVersion 27
//        versionCode 1
//        versionName "1.0"
//
//        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
//
//    }
//    buildTypes {
//        release {
//            minifyEnabled false
//            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
//        }
//    }
//    buildToolsVersion '26.0.2'
//}
//
//dependencies {
//    implementation fileTree(include: ['*.jar'], dir: 'libs')
//    implementation 'com.squareup.okhttp3:okhttp:3.9.1'
//    implementation 'com.google.code.gson:gson:2.8.2'
//    implementation 'com.android.support:recyclerview-v7:27.0.2'
//    testImplementation 'junit:junit:4.12'
//    androidTestImplementation 'com.android.support.test:runner:1.0.1'
//    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.1'
//}
