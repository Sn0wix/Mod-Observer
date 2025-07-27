package net.sn0wix_.modObserver.detection.tampering;

import net.fabricmc.loader.api.ModContainer;

import java.util.List;

public class TamperingException extends Exception {
    private final List<ModContainer> detectedOn;

    public TamperingException(List<ModContainer> detectedOn) {
        this.detectedOn = detectedOn;
    }

    /*public TamperingErrorScreen getScreen() {
        return new TamperingErrorScreen(detectedOn);
    }*/
}
