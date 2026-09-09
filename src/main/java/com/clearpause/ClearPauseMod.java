package com.clearpause;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

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

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
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
                // Pre hlavné pauza menu (GuiIngameMenu) zrušíme kreslenie pozadia / zatmenia,
                // ale pre GuiVideoSettings a iné podmenu necháme prebehnúť štandardný render,
                // čím sa nevymažú popisky ani posuvníky.
                if (event.gui instanceof GuiIngameMenu) {
                    event.gui.drawDefaultBackground();
                    // Ak chceš úplne priehľadné menu bez tmavého pozadia v pauze,
                    // môžeš tu pozadie preskočiť alebo ho vykresliť inak.
                }
            }
        }
    }
}
