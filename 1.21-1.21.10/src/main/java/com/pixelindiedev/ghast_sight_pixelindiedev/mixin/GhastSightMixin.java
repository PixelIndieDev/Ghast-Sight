package com.pixelindiedev.ghast_sight_pixelindiedev.mixin;

import com.pixelindiedev.ghast_sight_pixelindiedev.Ghast_sight_pixelindiedev;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GhastEntity.class, priority = 1010)
public abstract class GhastSightMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void modifyTargetingRange(CallbackInfo ci) {
        GhastEntity self = (GhastEntity) (Object) this;

        GoalSelector selector = ((MobEntityAccessor) self).getTargetSelector();

        selector.getGoals().removeIf(g -> g.getGoal() instanceof ActiveTargetGoal);

        float newSight = Ghast_sight_pixelindiedev.getSightValue();
        selector.add(1, new ActiveTargetGoal<>(self, PlayerEntity.class, 10, true, false, (entity, world) -> Math.abs(entity.getY() - self.getY()) <= newSight));

        Ghast_sight_pixelindiedev.AddGhast(self, newSight);
    }
}
