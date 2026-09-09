package com.clearpause;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Method;
import java.util.List;

@Mod(
    modid = ClearPauseMod.MODID, 
    name = ClearPauseMod.NAME, 
    version = ClearPauseMod.VERSION, 
    acceptableRemoteVersions = "*"
)
public class ClearPauseMod {
    public static final String MODID = "clearpause";
    public static final String NAME = "Clear Pause Menu";
    public static final String VERSION = "1.0";

    private static final String[] BUTTON_LIST_FIELD = new String[]{"buttonList", "field_146292_n"};

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.gui != null && event.gui.mc != null && event.gui.mc.theWorld != null) {
            
            String className = event.gui.getClass().getName();
            boolean isContainer = event.gui instanceof GuiContainer;

            boolean isStandardMenu = (event.gui instanceof GuiIngameMenu) 
                                  || (event.gui instanceof GuiOptions) 
                                  || (event.gui instanceof GuiVideoSettings)
                                  || (event.gui instanceof GuiScreenOptionsSounds);

            boolean isChatSettings = className.contains("ChatSettings") || className.contains("ChatOptions");

            boolean isSubMenu = isChatSettings
                               || className.contains("Customiz")
                               || className.contains("GuiOption") 
                               || className.contains("ScreenOptions")
                               || className.contains("GuiDetailSettings")
                               || className.contains("GuiQualitySettings")
                               || className.contains("GuiPerformanceSettings")
                               || className.contains("GuiOtherSettings")
                               || className.contains("GuiAnimation");

            if (isStandardMenu || isSubMenu) {
                event.setCanceled(true);

                try {
                    List<?> buttons = ReflectionHelper.getPrivateValue(GuiScreen.class, event.gui, BUTTON_LIST_FIELD);
                    
                    if (buttons != null) {
                        for (Object obj : buttons) {
                            if (obj instanceof GuiButton) {
                                GuiButton button = (GuiButton) obj;
                                if (button.visible) {
                                    button.drawButton(event.gui.mc, event.mouseX, event.mouseY);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (isContainer) {
                // Pre inventár zrušíme predvolené stmavenie obrazovky, 
                // ale necháme vykresliť textúru aj s itemmi cez správne vrstvy.
                event.setCanceled(true);

                try {
                    GuiContainer container = (GuiContainer) event.gui;

                    // 1. Vykreslíme textúru pozadia GUI (rám, sloty)
                    Method bgLayer = ReflectionHelper.findMethod(GuiContainer.class, container, new String[]{"drawGuiContainerBackgroundLayer", "func_146976_a"}, float.class, int.class, int.class);
                    bgLayer.setAccessible(true);
                    bgLayer.invoke(container, event.renderPartialTicks, event.mouseX, event.mouseY);

                    // 2. Vykreslíme itemy v slotoch (zavoláme vykreslenie slotov, ktoré obchádza absenciu RenderItem kontextu)
                    Method drawSlots = ReflectionHelper.findMethod(GuiScreen.class, container, new String[]{"drawScreen", "func_73863_a"}, int.class, int.class, float.class);
                    
                    // Namiesto toho radšej vyvoláme natívne vykreslenie popredia a slotov:
                    container.drawWorldBackground(255); // Prípadne ošetrenie pre sloty
                    
                    // Bezpečné vykreslenie textov a popredia inventára
                    Method fgLayer = ReflectionHelper.findMethod(GuiContainer.class, container, new String[]{"drawGuiContainerForegroundLayer", "func_146979_h"}, int.class, int.class);
                    fgLayer.setAccessible(true);
                    fgLayer.invoke(container, event.mouseX, event.mouseY);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
