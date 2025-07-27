package net.sn0wix_.modObserver.detection;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EntrypointBuilder {
    String icon = "";
    String name = "";
    boolean hasLibraryBadge = false;
    final Set<String> mixins = new LinkedHashSet<>(1);
    final Set<String> modids = new LinkedHashSet<>(1);
    final ModContainer container;

    public EntrypointBuilder(ModContainer container) {
        this.container = container;
    }

    public ModContainer getContainer() {
        return container;
    }

    public boolean hasLibraryBadge() {
        return hasLibraryBadge;
    }

    public Set<String> getMixins() {
        return mixins;
    }

    public Set<String> getModids() {
        return modids;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public EntrypointBuilder addMixin(String mixin) {
        mixins.add(mixin);
        return this;
    }

    public EntrypointBuilder addId(Path path) {
        String s = getId(path);
        if (!s.isEmpty()) {
            modids.add(s);
        }

        return this;
    }

    public EntrypointBuilder checkLibrary(CustomValue value) {
        if (!this.hasLibraryBadge && value.getAsString().equals("library"))
            this.hasLibraryBadge = true;

        return this;
    }

    public EntrypointBuilder addName(String name) {
        this.name = name;
        return this;
    }

    public boolean hasMixinsWithId(String modid) {
        AtomicInteger ids = new AtomicInteger(0);

        mixins.forEach(mixin -> {
            if (mixin.contains(modid) || mixin.toLowerCase().contains(name.toLowerCase())) {
                ids.getAndIncrement();
            }
        });

        return ids.get() > 0;
    }

    public String getValidId() {
        if (!modids.isEmpty()) {
            return (String) modids.toArray()[0];
        }
        return "";
    }

    public EntrypointBuilder addIconPath(Optional<String> optional) {
        this.icon = optional.orElse("");
        return this;
    }


    public String getId(Path path) {
        try {
            byte[] classBytes = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.toString().replace("\\", "/")).readAllBytes();

            ClassReader classReader = new ClassReader(classBytes);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            List<FieldNode> fields = classNode.fields;

            for (FieldNode field : fields) {
                boolean isString = "Ljava/lang/String;".equals(field.desc);

                if (isString && ("modid".equalsIgnoreCase(field.name) || "mod_id".equalsIgnoreCase(field.name))) {
                    if (field.value instanceof String) {
                        return (String) field.value;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return "";
    }
}
