package net.sn0wix_.modObserverPlugin.modChecking;

public class ModEntry {
    private final String hash;
    private final String modid;

    public ModEntry(String modid) {
        this.modid = modid;
        this.hash = "";
    }

    public ModEntry(String modid, String hash) {
        this.modid = modid;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public String getId() {
        return modid;
    }

    @Override
    public String toString() {
        return "[modid=" + getId() + (getHash().isEmpty() ? "" : ", hash=" + getHash()) + "]";
    }

    public boolean equals(ModEntry entry) {
        return entry.getId().equals(this.getId());
    }
}
