# RakshaSetu ProGuard Rules

# Keep Room entities
-keep class com.rakshasetu.app.data.entity.** { *; }

# Keep Hilt injected classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep SMS/Call related classes
-keep class com.rakshasetu.app.domain.sms.** { *; }
-keep class com.rakshasetu.app.domain.call.** { *; }

# Keep AccessibilityService
-keep class com.rakshasetu.app.service.** { *; }

# General Android
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
