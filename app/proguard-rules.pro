# BiblioMobil - ProGuard/R8 Rules

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class com.fasby.bibliomobil.BiblioMobilApp_HiltComponents** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.RoomDatabase { *; }

# Retrofit / OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleAnnotations, RuntimeVisibleTypeAnnotations
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Gemini AI (Generative AI)
-keep class com.google.ai.client.generativeai.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }

# Coil
-keep class coil.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext {
    private final android.os.Handler handler;
}

# Image Cropper
-keep class com.canhub.cropper.** { *; }
