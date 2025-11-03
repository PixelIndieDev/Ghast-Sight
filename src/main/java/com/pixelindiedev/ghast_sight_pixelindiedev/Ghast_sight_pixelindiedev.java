package com.pixelindiedev.ghast_sight_pixelindiedev;

import com.pixelindiedev.ghast_sight_pixelindiedev.config.GhastModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.WeakHashMap;

public class Ghast_sight_pixelindiedev implements ModInitializer {
    private static final WeakHashMap<GhastEntity, Float> loadedGhasts = new WeakHashMap<>();
    public static GhastModConfig CONFIG;

    public static float getSightValue() {
        return switch (CONFIG.HeightDifference) {
            case Low -> 8.0F;
            case Medium -> 16.0F;
            case Large -> 28.0F;
            case VeryLarge -> 40.0F;
            case null, default -> 4.0F; //vanilla minecraft
        };
    }

    public static void UpdateGhastViews() {
        float newSight = getSightValue();

        loadedGhasts.forEach((ghast, rememberedHeightDifference) -> {
            if (rememberedHeightDifference != newSight) {
                GoalSelector selector = ((com.pixelindiedev.ghast_sight_pixelindiedev.mixin.MobEntityAccessor) ghast).getTargetSelector();

                selector.getGoals().removeIf(g -> g.getGoal() instanceof ActiveTargetGoal);
                selector.add(1, new ActiveTargetGoal<>(ghast, PlayerEntity.class, 10, true, false, (entity, world) -> Math.abs(entity.getY() - ghast.getY()) <= newSight));

                AddGhast(ghast, newSight);
            }
        });
    }

    public static void AddGhast(GhastEntity ghast, float sightRange) {
        loadedGhasts.put(ghast, sightRange);
    }

    public static void onServerTick(MinecraftServer server) {
        if (CONFIG.hasExternalChange()) {
            CONFIG = GhastModConfig.load();
            UpdateGhastViews();
        }
    }

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(Ghast_sight_pixelindiedev::onServerTick);
        CONFIG = GhastModConfig.load();
        if (CONFIG.lastModified == 0L) CONFIG.lastModified = GhastModConfig.configFile.lastModified();
    }
}
