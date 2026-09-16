package com.pixelindiedev.ghast_sight_pixelindiedev;

import com.pixelindiedev.ghast_sight_pixelindiedev.config.GhastModConfig;
import com.pixelindiedev.ghast_sight_pixelindiedev.mixin.MobEntityAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class Ghast_sight_pixelindiedev implements ModInitializer {
    private static final WeakHashMap<Ghast, Float> loadedGhasts = new WeakHashMap<>();
    public static GhastModConfig CONFIG;

    private static final int configCheckIntervalAmountOfTicks = 20;
    private static int tickCounter = 0;

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
        final float newSight = getSightValue();
        final List<Ghast> ghastsToUpdate = new ArrayList<>();

        loadedGhasts.forEach((ghast, rememberedHeightDifference) -> {
            if (rememberedHeightDifference != newSight) ghastsToUpdate.add(ghast);
        });

        for (Ghast ghast : ghastsToUpdate) {
            GoalSelector selector = ((MobEntityAccessor) ghast).getTargetSelector();
            selector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof NearestAttackableTargetGoal);
            selector.addGoal(1, new NearestAttackableTargetGoal<>(ghast, Player.class, 10, true, false, (entity, world) -> Math.abs(entity.getY() - ghast.getY()) <= newSight));
            AddGhast(ghast, newSight);
        }
    }

    public static void AddGhast(Ghast ghast, float sightRange) {
        loadedGhasts.put(ghast, sightRange);
    }

    public static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < configCheckIntervalAmountOfTicks) return;
        tickCounter = 0;

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
