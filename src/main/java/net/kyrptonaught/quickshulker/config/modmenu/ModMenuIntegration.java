package net.kyrptonaught.quickshulker.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import malilib.gui.BaseScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.config.ConfigScreen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (currentScreen) -> {
            BaseScreen screen = ConfigScreen.create();
            screen.setParent(currentScreen);
            return screen;
        };
    }

}