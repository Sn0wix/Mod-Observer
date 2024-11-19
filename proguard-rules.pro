 Keep main entry point of Minecraft mods
-keep public class net.fabricmc.loader.impl.launch.knot.KnotClient {
    public static void main(java.lang.String[]);
}

# Keep Fabric API and related classes
-keep class net.fabricmc.api.** { *; }

# Do not obfuscate Minecraft mappings
-keep class net.minecraft.** { *; }

# Keep mod initializer classes
-keep public class net.sn0wix_.modObserver.ModObserver {
    public void onInitialize();
}

# Shrink aggressively
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# Preserve annotations
-keepattributes *Annotation*