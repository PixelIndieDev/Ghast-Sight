package com.pixelindiedev.ghast_sight_pixelindiedev.config.integration;

import com.pixelindiedev.ghast_sight_pixelindiedev.config.GhastModConfig;
import com.pixelindiedev.ghast_sight_pixelindiedev.config.HeightEnum;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class GhastConfigScreen extends Screen {
    private final Screen parent;
    private final GhastModConfig config;

    protected GhastConfigScreen(Screen parent) {
        super(Component.literal("Ghast Sight Config"));
        this.parent = parent;
        this.config = GhastModConfig.load();
    }

    @Override
    protected void init() {
        int y = height / 4;

        addRenderableWidget(ModMenuButtonWidget.builder(Component.literal("Ghast Seeing Max Height Difference: " + config.HeightDifference), (btn) ->
        {
            HeightEnum[] values = HeightEnum.values();
            int next = (config.HeightDifference.ordinal() + 1) % values.length;
            config.HeightDifference = values[next];
            btn.setMessage(Component.literal("Ghast Seeing Max Height Difference: " + config.HeightDifference));
            config.save();
        }).bounds(width / 2 - 125, y, 250, 20).build());

        y += 30;

        addRenderableWidget(ModMenuButtonWidget.builder(Component.literal("Done"), (btn) -> Minecraft.getInstance().setScreenAndShow(parent)).bounds(width / 2 - 100, y, 200, 20).build());
    }

    @Override
    public void onClose() {
        config.save();
        minecraft.setScreenAndShow(parent);
    }
}
