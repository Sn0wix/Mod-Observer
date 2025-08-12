package net.sn0wix_.modObserver.detection;

import net.sn0wix_.modObserver.ModObserver;

public enum IllegalStates {
    INCOMPATIBLE("text.mod_observer.incompatible", "tooltip.mod_observer.incompatible"),
    REQUIRED("text.mod_observer.required", "tooltip.mod_observer.required"),
    HASH_MISMATCH("text.mod_observer.hash_mismatch", "tooltip.mod_observer.hash_mismatch"),
    BAD_CHILDREN("text.mod_observer.bad_children", "tooltip.mod_observer.bad_children");

    private String translationKey;
    private String tooltip;

    IllegalStates(String translationKey, String tooltip) {
        this.translationKey = translationKey;
        this.tooltip = tooltip;
    }

    public static final String IDENTIFIER = "$" + ModObserver.MOD_ID + "$";
}
