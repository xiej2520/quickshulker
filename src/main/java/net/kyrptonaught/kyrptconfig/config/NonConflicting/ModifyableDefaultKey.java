package net.kyrptonaught.kyrptconfig.config.NonConflicting;

import net.minecraft.client.util.InputUtil;

public interface ModifyableDefaultKey {

    void setDefaultKey(InputUtil.KeyCode newKey);
}
