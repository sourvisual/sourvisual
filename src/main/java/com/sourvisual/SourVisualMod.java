package com.sourvisual;

import com.sourvisual.gui.SourVisualScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SourVisualMod implements ClientModInitializer {

    public static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        // Регистрируем клавишу Right Shift для открытия меню
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sourvisual.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.sourvisual.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                // Открываем меню только если сейчас ничего не открыто
                // (чтобы не мешать инвентарю, чату и т.д.)
                if (client.currentScreen == null) {
                    client.setScreen(new SourVisualScreen());
                }
            }
        });
    }
}
