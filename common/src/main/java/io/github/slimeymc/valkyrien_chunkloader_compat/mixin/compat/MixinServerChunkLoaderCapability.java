package io.github.slimeymc.valkyrien_chunkloader_compat.mixin.compat;

import com.supermartijn642.chunkloaders.capability.ServerChunkLoadingCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.valkyrienskies.mod.common.VSGameUtilsKt.getShipManagingPos;

@Mixin(ServerChunkLoadingCapability.class)
public class MixinServerChunkLoaderCapability {

    private static ServerLevel serverLevel;

    {
        try {
            Field levelField = ServerChunkLoadingCapability.class.getDeclaredField("level");
            levelField.setAccessible(true);
            try {
                serverLevel = (ServerLevel) levelField.get(this);
            } catch (IllegalAccessException e) {
                throw new IllegalAccessException();
            }
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(
            method = "startLoadingChunk",
            at = @At("HEAD")
    )
    public void onLoadChunk(UUID player, ChunkPos chunkPos, CallbackInfo ci) {
        Ship ship = getShipManagingPos(serverLevel, chunkPos);
        if(ship != null) {
            Vector3d offset = new Vector3d(0.0);
            ship.getWorldToShip().getTranslation(offset);
            // >> shift the fourth bit to the right, effectively changing from block to chunk coordinate
            chunkPos = new ChunkPos(chunkPos.x + ((int) offset.x >> 4), chunkPos.z + ((int) offset.z >> 4));
        }
    }
}
