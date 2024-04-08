package net.sn0wix_;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ModObserver implements ModInitializer {
    //TODO fix mod loading?, fix enviroment crash
    public static final String MOD_ID = "mod_observer";
    public static final Logger LOGGER = LoggerFactory.getLogger("mod_observer");
    public static Identifier MODS_FOR_APPROVAL_PACKET = new Identifier(MOD_ID, "mods_for_approval");

    @Override
    public void onInitialize() {
        ClientConfigurationConnectionEvents.INIT.register((handler, client) -> ClientConfigurationNetworking.send(MODS_FOR_APPROVAL_PACKET, getModsBuf()));
    }

    private static PacketByteBuf getModsBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        StringBuilder stringBuilder = new StringBuilder();

        for (String str : getMods()) {
            stringBuilder.append(str).append(",");
        }

        buf.writeBytes(stringBuilder.toString().getBytes());


        return buf;
    }

    private static List<String> getMods() {
        ArrayList<String> mods = new ArrayList<>(FabricLoader.getInstance().getAllMods().size());
        for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            mods.add(modContainer.getMetadata().getId());
        }
        return mods;
    }

    public static String encrypt(String strToEncrypt, String secret) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
    }
}
