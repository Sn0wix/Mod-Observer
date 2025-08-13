package net.sn0wix_.modobserver.detection;

import net.fabricmc.loader.api.ModContainer;

public class ModEntry {
    private final ModContainer origin;
    private String hash;

    public ModEntry(ModContainer origin) {
        this.origin = origin;
        this.hash = "";
    }

    public ModEntry(ModContainer origin, String hash) {
        this.origin = origin;
        this.hash = hash;
    }

    public ModContainer getOrigin() {
        return origin;
    }

    public String getHash() {
        return hash;
    }

    public String getId() {
        return origin.getMetadata().getId();
    }

    public void removeHash() {
        this.hash = "";
    }
}
