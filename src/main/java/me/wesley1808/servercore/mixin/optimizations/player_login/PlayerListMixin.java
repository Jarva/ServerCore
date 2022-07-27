package me.wesley1808.servercore.mixin.optimizations.player_login;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

/**
 * From: PaperMC (Don-t-move-existing-players-to-world-spawn.patch)
 * License: GPL-3.0 (licenses/GPL.md)
 */

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    // Paper - Finds random spawn location for new players.
    @Inject(
            method = "placeNewPlayer",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;setLevel(Lnet/minecraft/server/level/ServerLevel;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void servercore$moveToSpawn(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci, GameProfile gameProfile, GameProfileCache gameProfileCache, Optional optional, String string, CompoundTag compoundTag, ResourceKey resourceKey, ServerLevel serverLevel, ServerLevel serverLevel2) {
        if (compoundTag == null) serverPlayer.fudgeSpawnLocation(serverLevel2);
    }

    // ServerCore - Finds random spawn location for respawning players without spawnpoint.
    @Inject(
            method = "respawn",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void servercore$moveToSpawn(ServerPlayer oldPlayer, boolean bl, CallbackInfoReturnable<ServerPlayer> cir, BlockPos blockPos, float f, boolean bl2, ServerLevel serverLevel, Optional optional, ServerLevel serverLevel2, ServerPlayer newPlayer) {
        if (optional.isEmpty()) newPlayer.fudgeSpawnLocation(serverLevel2);
    }
}
