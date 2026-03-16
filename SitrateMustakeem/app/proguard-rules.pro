# Preserve generic type info for Gson
-keepattributes Signature, *Annotation*, SourceFile,LineNumberTable

# Keep model classes for Gson
-keep class com.habib.siratemustakeem.models.** { *; }

# Keep Gson TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }

# Keep Gson classes
-keep class com.google.gson.** { *; }

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

# StAX / XML streaming classes referenced by libraries
-keep class javax.xml.stream.** { *; }
-dontwarn javax.xml.stream.**
-keep class org.codehaus.stax2.** { *; }
-dontwarn org.codehaus.stax2.**

# Suppress warnings from common Java classes
-dontwarn aQute.bnd.annotation.spi.ServiceConsumer
-dontwarn aQute.bnd.annotation.spi.ServiceProvider
-dontwarn java.awt.Color
-dontwarn java.awt.Dimension
-dontwarn java.awt.Rectangle
-dontwarn java.awt.color.ColorSpace
-dontwarn java.awt.geom.AffineTransform
-dontwarn java.awt.geom.Dimension2D
-dontwarn java.awt.geom.Path2D
-dontwarn java.awt.geom.PathIterator
-dontwarn java.awt.geom.Point2D
-dontwarn java.awt.geom.Rectangle2D
-dontwarn java.awt.image.BufferedImage
-dontwarn java.awt.image.ColorModel
-dontwarn java.awt.image.ComponentColorModel
-dontwarn java.awt.image.DirectColorModel
-dontwarn java.awt.image.IndexColorModel
-dontwarn java.awt.image.PackedColorModel