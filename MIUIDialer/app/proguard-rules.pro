-keep class com.miuidialer.app.** { *; }
-keepclassmembers class * {
    @dagger.hilt.* <methods>;
}
