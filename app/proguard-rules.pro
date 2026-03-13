# ══════════════════════════════════════════════════════════════
# OnlinePDKS — ProGuard / R8 Kuralları
# ══════════════════════════════════════════════════════════════

# Stack trace okunabilirliği için satır numaralarını koru
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ══════════════ GSON + MODEL SINIFLARI ══════════════
# Gson, @SerializedName anotasyonlu alanları reflection ile okur.
# Bu alanlar obfuscate edilirse JSON dönüşümü bozulur.

-keepattributes Signature
-keepattributes *Annotation*

# Tüm model sınıflarını koru — Gson serialization/deserialization için
-keep class com.pdks.mobile.model.** { *; }

# Gson iç yapısı
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ══════════════ RETROFIT ══════════════
# Retrofit interface metodları reflection ile çağrılır

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Retrofit annotation'larını koru
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# ApiService interface'ini koru
-keep interface com.pdks.mobile.api.ApiService { *; }

# ApiConfig sabitlerini koru
-keep class com.pdks.mobile.api.ApiConfig { *; }

# ══════════════ OKHTTP ══════════════

-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ══════════════ ML KIT (Barcode) ══════════════

-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# ══════════════ CAMERAX ══════════════

-keep class androidx.camera.** { *; }

# ══════════════ ZXING (QR Üretimi) ══════════════

-keep class com.google.zxing.** { *; }

# ══════════════ PLAY SERVICES (Location) ══════════════

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ══════════════ ENCRYPTED SHARED PREFERENCES ══════════════

-keep class androidx.security.crypto.** { *; }

# ══════════════ GENEL ══════════════

# Enum'ları koru
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ViewBinding sınıfları
-keep class com.pdks.mobile.databinding.** { *; }