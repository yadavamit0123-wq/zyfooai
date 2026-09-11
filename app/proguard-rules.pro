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


-keep class com.growwthapps.dailypost.model**{*;}
-keep class com.growwthapps.dailypost.custom.poster.model**{*;}

-keep class com.growwthapps.dailypost.v2.api.ApiClient{*;}


-keep class com.huawei** { *; }
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

# the following line is for illustration purposes

-keep class com.arthenica.mobileffmpeg.Config {
    native <methods>;
    void log(int, byte[]);
    void statistics(int, float, float, long , int, double, double);
}
-keep class com.arthenica.mobileffmpeg** { *; }
-keep class com.arthenica.mobileffmpeg.AbiDetect {
    native <methods>;
}

-keep class com.google.gson**{*;}

-keep public class com.google.ads** {
    public *;
}
# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.firebase.** { *; }

# Keep Google Play Services classes
-keep class com.google.android.gms.** { *; }

# Keep any classes or methods required for Firebase and Google sign-in
-keep class com.crafto.apps.** { *; }




-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.squareup.okhttp.**


# Retrofit
-keep class com.google.gson** { *; }
-keep public class com.google.gson** {public private protected *;}
-keep class org.apache.http** { *; }
-keep class retrofit2** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.okhttp.*
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**

-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit** { *; }
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}

-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

#-keep class com.arthenica.mobileffmpeg.** { *; }
#-keep class com.arthenica.ffmpegkit.** { *; }