package com.pixelindiedev.ghast_sight_pixelindiedev.config.integration;

import com.pixelindiedev.ghast_sight_pixelindiedev.config.GhastModConfig;
import com.pixelindiedev.ghast_sight_pixelindiedev.config.HeightEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class GhastConfigScreen extends Screen {
    private final Screen parent;
    private final GhastModConfig config;

    protected GhastConfigScreen(Screen parent) {
        super(Text.literal("Ghast Sight Config"));
        this.parent = parent;
        this.config = GhastModConfig.load();
    }

    @Override
    protected void init() {
        int y = height / 4;

        addDrawableChild(ButtonWidget.builder(Text.literal("Ghast Seeing Max Height Difference: " + config.HeightDifference), (btn) ->
        {
            HeightEnum[] values = HeightEnum.values();
            int next = (config.HeightDifference.ordinal() + 1) % values.length;
            config.HeightDifference = values[next];
            btn.setMessage(Text.literal("Ghast Seeing Max Height Difference: " + config.HeightDifference));
            config.save();
        }).dimensions(width / 2 - 125, y, 250, 20).build());

        y += 30;

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), (btn) -> MinecraftClient.getInstance().setScreen(parent)).dimensions(width / 2 - 100, y, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        config.save();
        assert client != null;
        client.setScreen(parent);
    }
}
