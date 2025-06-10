package dev.amble.ait.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;


// This is from DM 1.16 so cope harder ig. I'm gonna get Jayson to take a look at it later. Don't worry 'bout it.
public class AlphaRenderingUtil {

    public static Identifier generateAlphaTexture(Identifier tex) {
        try {
            Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(tex);
            if (resource.isEmpty()) return tex;
            InputStream a = resource.get().getInputStream();

            NativeImage image_a = NativeImage.read(a);
            NativeImage image = new NativeImage(image_a.getHeight(), image_a.getWidth(), true);

            image.copyFrom(image_a);

            for (int x = 0; x < image.getHeight(); x++) {
                for (int y = 0; y < image.getWidth(); y++) {
                    int alpha = image_a.getOpacity(x, y);
                    if (alpha < 0) {
                        image.setColor(x, y, 0xFFFFFFFF); // Set alpha to 255
                    } else {
                        image.setColor(x, y, 0x00FFFFFF); // Set alpha to 0
                    }
                }
            }

            Identifier res = MinecraftClient.getInstance().getTextureManager()
                    .registerDynamicTexture(AITMod.MOD_ID + "/textures/" +
                                    tex.getPath().replaceAll(".png", "") + "_alpha.png",
                            new NativeImageBackedTexture(image));
            return res;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tex;

    }
}
