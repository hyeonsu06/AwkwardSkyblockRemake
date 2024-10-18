package io.hyonsu06.core.functions;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Base64;
import java.util.UUID;

public class CustomHead {

    public static ItemMeta setTexture(SkullMeta meta, String textureURL) {
        PlayerProfile profile = createProfile(textureURL);
        meta.setPlayerProfile(profile);
        return meta;
    }

    private static PlayerProfile createProfile(String textureURL) {
        // Create a PlayerProfile with a random UUID
        PlayerProfile profile = org.bukkit.Bukkit.createProfile(UUID.randomUUID(), null);

        // Encode the texture in Base64
        String base64 = Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + textureURL + "\"}}}").getBytes());

        // Add the texture property to the profile
        profile.setProperty(new ProfileProperty("textures", base64));

        return profile;
    }
}
