package net.sn0wix_.modObserver;

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
    boolean isLibrary = false;
    final Set<String> mixins = new LinkedHashSet<>(1);
    final Set<String> modids = new LinkedHashSet<>(1);


    EntrypointBuilder addMixin(String mixin) {
        mixins.add(mixin);
        return this;
    }

    EntrypointBuilder addId(Path path) {
        String s = getId(path);
        if (!s.isEmpty()) {
            modids.add(s);
        }

        return this;
    }

    EntrypointBuilder checkLibraryBadge(CustomValue value) {
        if (!this.isLibrary && value.getAsString().equals("library"))
            this.isLibrary = true;

        return this;
    }

    boolean hasLibraryBadge() {
        return isLibrary;
    }

    EntrypointBuilder setName(String name) {
        this.name = name;
        return this;
    }

    boolean hasMixinsWithId(String modid) {
        AtomicInteger ids = new AtomicInteger(0);

        mixins.forEach(mixin -> {
            if (mixin.contains(modid) || mixin.toLowerCase().contains(name.toLowerCase())) {
                ids.getAndIncrement();
            }
        });

        return ids.get() > 0;
    }

    boolean isValidId(String modid) {
        for (String id : modids) {
            if (id.equals(modid)) {
                return true;
            }
        }

        return false;
    }

    EntrypointBuilder addIconPath(Optional<String> optional) {
        this.icon = optional.orElse("");
        return this;
    }


    private String getId(Path path) {
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
