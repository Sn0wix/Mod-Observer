package net.sn0wix_.modObserver.detection;

import net.fabricmc.loader.api.ModContainer;

public class ModEntry {
    private final ModContainer origin;
    private final String hash;

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
}
