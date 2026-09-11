# Room rules
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class com.byteutility.dev.quickfill.data.local.** { *; }
-dontwarn com.byteutility.dev.quickfill.data.local.**

# Hilt rules
-dontwarn dagger.hilt.**
-keep class * extends dagger.hilt.android.internal.managers.** { *; }