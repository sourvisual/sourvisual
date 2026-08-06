package com.sourvisual.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SourVisualScreen extends Screen {

    // ---- Вкладки сайдбара. Добавляй сюда новые пункты меню ----
    private enum Tab {
        EFFECTS("Effects"),
        VISUAL("Visual"),
        SETTINGS("Settings");

        final String label;

        Tab(String label) {
            this.label = label;
        }
    }

    private Tab currentTab = Tab.EFFECTS;

    private int panelX, panelY, panelWidth, panelHeight;
    private final int sidebarWidth = 150;
    private final int headerHeight = 40;
    private final int rowHeight = 28;

    private String searchText = "";
    private boolean searchFocused = false;

    public SourVisualScreen() {
        super(Text.literal("Sour Visual"));
    }

    @Override
    protected void init() {
        panelWidth = 610;
        panelHeight = 400;
        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Полупрозрачное затемнение игрового мира позади меню
        context.fill(0, 0, this.width, this.height, 0x66000000);

        int panelBg = 0xEE0B0B12;
        int border = 0x33FFFFFF;

        // Основная панель
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, panelBg);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0x22FFFFFF);

        // Сайдбар слева
        context.fill(panelX, panelY, panelX + sidebarWidth, panelY + panelHeight, panelBg);
        context.fill(panelX + sidebarWidth, panelY, panelX + sidebarWidth + 1, panelY + panelHeight, border);

        // Заголовок "Sour Visual" (градиент имитируем двумя цветами)
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Sour").formatted(Formatting.AQUA)
                        .append(Text.literal(" Visual").formatted(Formatting.LIGHT_PURPLE)),
                panelX + 20, panelY + 16, 0xFFFFFF);

        // Поле поиска сверху справа
        int searchW = 170;
        int searchH = 20;
        int searchX = panelX + panelWidth - searchW - 20;
        int searchY = panelY + 12;
        context.fill(searchX, searchY, searchX + searchW, searchY + searchH, 0xFF1A1A22);
        context.drawBorder(searchX, searchY, searchW, searchH, searchFocused ? 0xFF8888FF : 0xFF333340);
        String shown = searchText.isEmpty() ? "Search..." : searchText;
        int searchColor = searchText.isEmpty() ? 0xFF888888 : 0xFFFFFFFF;
        context.drawTextWithShadow(this.textRenderer, shown, searchX + 6, searchY + 6, searchColor);

        // Разделительная линия под шапкой
        context.fill(panelX, panelY + headerHeight, panelX + panelWidth, panelY + headerHeight + 1, border);

        // Пункты сайдбара
        int itemY = panelY + headerHeight + 15;
        for (Tab tab : Tab.values()) {
            boolean hovered = isInRect(mouseX, mouseY, panelX, itemY, sidebarWidth, rowHeight);
            boolean active = currentTab == tab;

            if (active) {
                context.fill(panelX, itemY, panelX + sidebarWidth, itemY + rowHeight, 0x30FFFFFF);
            } else if (hovered) {
                context.fill(panelX, itemY, panelX + sidebarWidth, itemY + rowHeight, 0x18FFFFFF);
            }

            int textColor = active ? 0xFFFFFFFF : 0xFFAAAAAA;
            context.drawTextWithShadow(this.textRenderer, tab.label, panelX + 24, itemY + 10, textColor);

            itemY += rowHeight;
        }

        // Область контента справа — сюда добавляй содержимое каждой вкладки
        int contentX = panelX + sidebarWidth + 20;
        int contentY = panelY + headerHeight + 20;
        renderTabContent(context, currentTab, contentX, contentY);

        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * Здесь рисуй содержимое каждой вкладки.
     * Сейчас — просто заглушка с названием раздела, дополняй под свои нужды.
     */
    private void renderTabContent(DrawContext context, Tab tab, int x, int y) {
        switch (tab) {
            case EFFECTS -> context.drawTextWithShadow(this.textRenderer,
                    Text.literal("Раздел: Effects").formatted(Formatting.GRAY), x, y, 0xAAAAAA);
            case VISUAL -> context.drawTextWithShadow(this.textRenderer,
                    Text.literal("Раздел: Visual").formatted(Formatting.GRAY), x, y, 0xAAAAAA);
            case SETTINGS -> context.drawTextWithShadow(this.textRenderer,
                    Text.literal("Раздел: Settings").formatted(Formatting.GRAY), x, y, 0xAAAAAA);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Клик по пунктам сайдбара
            int itemY = panelY + headerHeight + 15;
            for (Tab tab : Tab.values()) {
                if (isInRect((int) mouseX, (int) mouseY, panelX, itemY, sidebarWidth, rowHeight)) {
                    currentTab = tab;
                    return true;
                }
                itemY += rowHeight;
            }

            // Клик по полю поиска
            int searchW = 170;
            int searchH = 20;
            int searchX = panelX + panelWidth - searchW - 20;
            int searchY = panelY + 12;
            searchFocused = isInRect((int) mouseX, (int) mouseY, searchX, searchY, searchW, searchH);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused) {
            searchText += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchFocused && keyCode == 259 /* backspace */ && !searchText.isEmpty()) {
            searchText = searchText.substring(0, searchText.length() - 1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isInRect(int px, int py, int rx, int ry, int rw, int rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
