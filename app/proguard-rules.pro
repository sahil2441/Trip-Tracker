# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/sahil/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn com.activate.gcm.**
-dontwarn org.joda.**
-keep class com.android.internal.telephony.ITelephony { *; }

# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

-keepclassmembers class * { public <init>(android.content.​Context); }
-keepattributes *Annotation*

#keep entity
-keep class me.sahiljain.tripTracker.entity.**
-keepclassmembers class me.sahiljain.tripTracker.entity.** {*;}

#keep db package
-keep class me.sahiljain.tripTracker.db.**
-keepclassmembers class me.sahiljain.tripTracker.db.** {*;}

#Logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
