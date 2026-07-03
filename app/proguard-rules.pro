# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Kotlin Serialization
-keepattributes *Annotation*, EnclosingMethod, Signature, InnerClasses
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName *;
}

# Haze
-keep class dev.chrisbanes.haze.** { *; }

# Keep Navigation Routes for Type Safety
-keep class com.example.core.navigation.OtakuScreen** { *; }

# Keep BottomNavBar UI state classes
-keep class com.example.feature.screens.NavBarItem { *; }
-keep class com.example.feature.screens.NavDestination** { *; }
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