# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn net.yageek.common.APIClient
-dontwarn net.yageek.common.Parking
-dontwarn net.yageek.common.ParkingResult$Comparators
-dontwarn net.yageek.common.ParkingResult
-dontwarn net.yageek.common.ParkingState
-dontwarn net.yageek.common.Webservice
-dontwarn net.yageek.common.adapters.ParkingBaseAdapter
-dontwarn net.yageek.common.repository.ParkingRepository$Callback
-dontwarn net.yageek.common.repository.ParkingRepository
-dontwarn net.yageek.common.utils.ParkingStatusUtils
-dontwarn net.yageek.common.utils.Position