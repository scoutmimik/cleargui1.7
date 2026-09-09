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

import java.util.List;

@Mod(
    modid = ClearPauseMod.MODID, 
    name = ClearPauseMod.NAME, 
    version = ClearPauseMod.VERSION, 
    acceptableRemoteVersions = "*"
)
class ClearPauseMod {
    public static final String MODID = "clearpause";
    public static final String NAME = "Clear Pause Menu";
    public static final String VERSION = "1.0";

    private static final String[] BUTTON_LIST_FIELD = new String[]{"buttonList", "field_146292_n"};

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.gui != null && event.gui.mc != null && event.gui.mc.theWorld != null) {
            String className = event.gui.getClass().getName();

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
            }
        }
    }

    // Pre inventáre (GuiContainer) využijeme Post event, kde vieme zamedziť stmaveniu,
    // prípadne ak chceme odstrániť pozadie pod inventárom bez straty itemov, 
    // najlepšie je nechať vykresliť GUI a hneď upraviť alpha blending.
    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.gui instanceof GuiContainer && event.gui.mc != null && event.gui.mc.theWorld != null) {
            // Tu môžeme pridať dodatočnú úpravu vykreslenia, ak je potrebná, 
            // avšak pre čisté odstránenie pozadia inventára v 1.7.10 bez asmu/coremodu
            // je najspoľahlivejšie nechať kontajner bežať štandardne, 
            // alebo použiť špecifický mixin/coremod. 
        }
    }
}
