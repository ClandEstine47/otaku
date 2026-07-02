# Keep members of serializable classes
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

-keepclassmembers class ** {
    @kotlinx.serialization.SerialName *;
}

# Keep the specific OtakuScreen routes to ensure Navigation can find them by name
-keep class com.example.core.navigation.OtakuScreen** { *; }
