package net.sn0wix_.modObserver.detection;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.ModOrigin;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ModEntry implements ModContainer {
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

    public String getHash() {
        return hash;
    }

    @Override
    public ModMetadata getMetadata() {
        return origin.getMetadata();
    }

    @Override
    public List<Path> getRootPaths() {
        return origin.getRootPaths();
    }

    @Override
    public ModOrigin getOrigin() {
        return origin.getOrigin();
    }

    @Override
    public Optional<ModContainer> getContainingMod() {
        return origin.getContainingMod();
    }

    @Override
    public Collection<ModContainer> getContainedMods() {
        return origin.getContainedMods();
    }

    @Override
    public Path getRootPath() {
        return origin.getRootPath();
    }

    @Override
    public Path getPath(String file) {
        return origin.getPath(file);
    }

    @Override
    public String toString() {
        return origin.getMetadata().getId() + (getHash().isEmpty() ? "" : " " + getHash());
    }
}
