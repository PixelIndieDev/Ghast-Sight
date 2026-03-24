package com.pixelindiedev.ghast_sight_pixelindiedev.mixin;

import com.pixelindiedev.ghast_sight_pixelindiedev.Ghast_sight_pixelindiedev;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Ghast.class, priority = 1010)
public abstract class GhastSightMixin {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void modifyTargetingRange(CallbackInfo ci) {
        Ghast self = (Ghast) (Object) this;

        GoalSelector selector = ((MobEntityAccessor) self).getTargetSelector();

        selector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof NearestAttackableTargetGoal);

        float newSight = Ghast_sight_pixelindiedev.getSightValue();
        selector.addGoal(1, new NearestAttackableTargetGoal<>(self, Player.class, 10, true, false, (entity, world) -> Math.abs(entity.getY() - self.getY()) <= newSight));

        Ghast_sight_pixelindiedev.AddGhast(self, newSight);
    }
}
