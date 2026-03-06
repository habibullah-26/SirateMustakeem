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

# Common ProGuard rules
-keepattributes Signature, *Annotation*, SourceFile,LineNumberTable

# Keep classes annotated with androidx Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers @androidx.annotation.Keep class * { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Parcelable creators
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep enum methods used by reflection
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Common JSON libraries (if used)
-keep class com.google.gson.** { *; }
-keep class com.squareup.moshi.** { *; }# Common ProGuard rules
-keepattributes Signature, *Annotation*, SourceFile,LineNumberTable

# Keep classes annotated with androidx Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers @androidx.annotation.Keep class * { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Parcelable creators
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep enum methods used by reflection
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Common JSON libraries (if used)
-keep class com.google.gson.** { *; }
-keep class com.squareup.moshi.** { *; }

# StAX / XML streaming classes referenced by libraries
-keep class javax.xml.stream.** { *; }
-dontwarn javax.xml.stream.**
-keep class org.codehaus.stax2.** { *; }
-dontwarn org.codehaus.stax2.**
-keep class com.fasterxml.aalto.** { *; }
-dontwarn com.fasterxml.aalto.**
-dontwarn com.fasterxml.**
# Keep Parcelable creators
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep enum methods used by reflection
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Common JSON libraries (if used)
-keep class com.google.gson.** { *; }
-keep class com.squareup.moshi.** { *; }