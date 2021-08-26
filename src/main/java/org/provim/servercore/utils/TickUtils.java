package org.provim.servercore.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.provim.servercore.ServerCore;
import org.provim.servercore.config.Config;
import org.provim.servercore.config.MessageConfig;

import java.math.BigDecimal;

public final class TickUtils {
    private static final BigDecimal value = new BigDecimal("0.1");
    private static int viewDistance = ServerCore.getServer().getPlayerManager().getViewDistance();
    private static int chunkTickDistance = viewDistance;
    private static BigDecimal mobcapModifier = new BigDecimal("1.0");

    private TickUtils() {
    }

    /**
     * Runs performance checks based on the current MSPT.
     *
     * @param server: The minecraft server
     */

    public static void runPerformanceChecks(MinecraftServer server) {
        if (Config.instance().runPerformanceChecks) {
            double mspt = server.getTickTime();
            checkViewDistance(mspt);
            checkMobcaps(mspt);
            checkTickDistance(mspt);
        }
    }

    /**
     * Modifies the tick distance based on the MSPT.
     *
     * @param mspt: The current MSPT
     */

    private static void checkTickDistance(double mspt) {
        if (mspt > 40 && chunkTickDistance > Config.instance().minTickDistance) {
            chunkTickDistance -= 1;
        } else if (mspt < 30 && chunkTickDistance < Config.instance().maxTickDistance && mobcapModifier.doubleValue() == Config.instance().maxMobcap) {
            chunkTickDistance += 1;
        }
    }

    public static int getChunkTickDistance() {
        return chunkTickDistance;
    }

    public static void setChunkTickDistance(int distance) {
        chunkTickDistance = distance;
    }

    /**
     * Modifies the mobcaps based on the MSPT.
     *
     * @param mspt: The current MSPT
     */

    private static void checkMobcaps(double mspt) {
        if (mspt > 45 && chunkTickDistance == Config.instance().minTickDistance && mobcapModifier.doubleValue() > Config.instance().minMobcap) {
            mobcapModifier = mobcapModifier.subtract(value);
        } else if (mspt < 35 && mobcapModifier.doubleValue() < Config.instance().maxMobcap && viewDistance == Config.instance().maxViewDistance) {
            mobcapModifier = mobcapModifier.add(value);
        }
    }

    public static double getModifier() {
        return mobcapModifier.doubleValue();
    }

    public static void setModifier(BigDecimal modifier) {
        mobcapModifier = modifier;
    }

    /**
     * Modifies the view distance based on the MSPT.
     *
     * @param mspt: The current MSPT
     */

    private static void checkViewDistance(double mspt) {
        if (mspt > 45 && mobcapModifier.doubleValue() == Config.instance().minMobcap && viewDistance > Config.instance().minViewDistance) {
            setViewDistance(viewDistance - 1);
        } else if (mspt < 35 && viewDistance < Config.instance().maxViewDistance) {
            setViewDistance(viewDistance + 1);
        }
    }

    public static int getViewDistance() {
        return viewDistance;
    }

    public static void setViewDistance(int distance) {
        ServerCore.getServer().getPlayerManager().setViewDistance(distance);
        viewDistance = distance;
    }

    /**
     * Decides whether a chunk should tick.
     *
     * @param pos:   The position of the chunk
     * @param world: The world that is ticking chunks
     * @return Boolean: whether the chunk should tick.
     */

    public static boolean shouldTick(ChunkPos pos, ServerWorld world) {
        if (chunkTickDistance >= viewDistance) {
            return true;
        }

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.interactionManager.getGameMode() != GameMode.SPECTATOR && player.getChunkPos().getChebyshevDistance(pos) <= chunkTickDistance) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks for entities of the same type in the surrounding area.
     * If there are more than the limit specifies within the range, it will return true.
     *
     * @param type:  The type of entity
     * @param world: The world to check in
     * @param pos:   The position of the entity
     * @param limit: The entity limit
     * @param range: The range of the limit
     * @return Boolean: if there are more entities of the same type as the limit, within the specified range.
     */

    public static boolean checkForEntities(EntityType<?> type, World world, BlockPos pos, int limit, int range) {
        if (Config.instance().useEntityLimits) {
            return limit <= world.getEntitiesByType(type, new Box(pos.mutableCopy().add(range, range, range), pos.mutableCopy().add(-range, -range, -range)), EntityPredicates.EXCEPT_SPECTATOR).size();
        } else {
            return false;
        }
    }

    public static boolean checkForEntities(Entity entity, int limit, int range) {
        return checkForEntities(entity.getType(), entity.getEntityWorld(), entity.getBlockPos(), limit, range);
    }

    public static MutableText createPerformanceReport() {
        MinecraftServer server = ServerCore.getServer();
        double ms = server.getTickTime();
        var mspt = String.format("%.1f", ms);
        var tps = String.format("%.1f", ms != 0 ? Math.min((1000 / ms), 20) : 20);
        return new LiteralText(String.format(MessageConfig.instance().performance, tps, mspt, server.getCurrentPlayerCount(), viewDistance, String.format("%.1f", mobcapModifier.doubleValue()), chunkTickDistance));
    }
}
