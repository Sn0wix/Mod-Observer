package net.sn0wix_.modObserverPlugin.modChecking;

import org.jetbrains.annotations.Nullable;

public class ModEntry {
    private final ModEntry origin;
    private final String hash;
    private final String modid;

    public ModEntry(String modid) {
        this.modid = modid;
        this.hash = "";
        this.origin = null;
    }

    public ModEntry(String modid, String hash) {
        this.modid = modid;
        this.hash = "";
        this.origin = null;
    }

    public ModEntry(String modid, ModEntry origin) {
        this.modid = modid;
        this.hash = "";
        this.origin = origin;
    }

    @Nullable
    public ModEntry getOrigin() {
        return origin;
    }

    public String getHash() {
        return hash;
    }

    public String getId() {
        return modid;
    }
}
