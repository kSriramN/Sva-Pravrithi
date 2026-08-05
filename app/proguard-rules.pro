# Add project specific ProGuard rules here.
# Minification is disabled by default in this build (see app/build.gradle.kts).
# If you enable it later, keep Room entities and Hilt-generated classes:
-keep class com.svapravrithi.app.data.local.entity.** { *; }
-keepattributes *Annotation*
