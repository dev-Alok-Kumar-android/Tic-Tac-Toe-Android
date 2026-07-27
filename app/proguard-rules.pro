# Kotlin Serialization Rules
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions, SourceFile, LineNumberTable

# Keep serializable classes and their members
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Keep the serializer for all serializable classes
-keepclassmembers class **$serializer {
    public static ** INSTANCE;
}

# Keep Enums names and values (important for serialization and UI display)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Specific Tic Tac Toe Models
-keep class com.tuto.alokkumar.tictactoe.data.** { *; }
-keep class com.tuto.alokkumar.tictactoe.domain.model.** { *; }
-keep class com.tuto.alokkumar.tictactoe.Route** { *; }
-keep class com.tuto.alokkumar.tictactoe.Route$** { *; }
