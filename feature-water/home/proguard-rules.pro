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

# Material Icons Extended - 사용하지 않는 아이콘 제거를 위한 설정
# R8/ProGuard가 사용되지 않는 아이콘을 자동으로 제거하도록 함
-keep class androidx.compose.material.icons.** { *; }
-keep class androidx.compose.material.icons.filled.LocalDrinkKt
-keep class androidx.compose.material.icons.filled.AddKt
-keep class androidx.compose.material.icons.filled.MoreVertKt
-keep class androidx.compose.material.icons.filled.NotificationsKt
-keep class androidx.compose.material.icons.filled.ShareKt

# Compose 관련 keep 규칙
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }