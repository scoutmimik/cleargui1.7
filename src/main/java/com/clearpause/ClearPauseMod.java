@SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.gui != null && event.gui.mc != null && event.gui.mc.theWorld != null) {
            
            String className = event.gui.getClass().getName();

            // Zistíme, či ide o GuiContainer (inventáre, truhlice, pece, atď.)
            boolean isContainer = event.gui instanceof net.minecraft.client.gui.inventory.GuiContainer;

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

            // Pridáme isContainer do hlavnej podmienky, aby sa zrušilo stmavenie za inventárom/truhlicou
            if (isStandardMenu || isSubMenu || isContainer) {
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
