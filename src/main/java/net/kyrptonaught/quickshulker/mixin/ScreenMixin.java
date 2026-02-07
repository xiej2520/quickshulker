package net.kyrptonaught.quickshulker.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.client.QuickShulkerModClient;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenuScreen.class)
@Environment(EnvType.CLIENT)
public abstract class ScreenMixin extends Screen {
    @Shadow
    private InventorySlot hoveredSlot;

    @Shadow
    public InventoryMenu menu;

    @Shadow
    private boolean cancelNextMouseRelease;

    @Inject(method = "init", at = @At("RETURN"))
    private void fixMouse(CallbackInfo ci) {
        if (QuickShulkerMod.lastMouseX != -1.0 && QuickShulkerMod.lastMouseY != -1.0) {
            Mouse.setCursorPosition((int) QuickShulkerMod.lastMouseX, (int) QuickShulkerMod.lastMouseY);
            QuickShulkerMod.lastMouseY = -1;
            QuickShulkerMod.lastMouseX = -1;
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void QS$keyPressed(char chr, int key, CallbackInfo ci) {
        if (QuickShulkerMod.getConfig().keybindInInv) {
            if (QuickShulkerModClient.getKeybinding().isPressed()) {
                handleTrigger();
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void QS$mousePressed(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (QuickShulkerMod.getConfig().rightClickInv) {
            if (this.minecraft.player.inventory.getCursorStack().isEmpty() && mouseButton == 1 && this.hoveredSlot != null && this.hoveredSlot.getStack().getSize() == 1) {
                if (handleTrigger()) {
                    this.cancelNextMouseRelease = true;
                    ci.cancel();
                }
            }
        }
        if (QuickShulkerMod.getConfig().keybindInInv) {
            if (QuickShulkerModClient.getKeybinding().isPressed()) {
                if (handleTrigger()) {
                    this.cancelNextMouseRelease = true;
                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private boolean handleTrigger() {
        InventorySlot slot = this.hoveredSlot;
        // only allow opening player inventory items
        if (slot == null || !(slot.inventory instanceof PlayerInventory)) {
            return false;
        }

        ItemStack stack = slot.getStack();
        if (stack.getSize() != 1) {
            return false;
        }

        int id = ClientUtil.getSlotId(this.menu, slot);
        if (ClientUtil.CheckAndSend(stack, id)) {
            QuickShulkerMod.lastMouseX = Mouse.getX();
            QuickShulkerMod.lastMouseY = Mouse.getY();
            return true;
        }

        return false;
    }


    @Inject(method = "drawSlot", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItem(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;II)V"))
    public void drawSlotBackground(InventorySlot slot, CallbackInfo ci) {
        // lame bar, I'm too lazy to draw something or render a texture
        int playerInvUsedSlot = ((ItemInventoryContainer) this.menu).getPlayerInvUsedSlot();
        ConfigOptions opts = QuickShulkerMod.getConfig();
        if (opts.fillOpenedBackground
                && playerInvUsedSlot != -1
                && slot.inventory instanceof PlayerInventory
                && ((SlotAccessor) slot).getInventoryIndex() == playerInvUsedSlot) {
            int i = slot.x;
            int j = slot.y;

            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuilder();

            this.itemRenderer.fill(bufferBuilder, i, j, 16, 16,
                    (opts.colorBackground >> 16) & 0xFF,
                    (opts.colorBackground >> 8) & 0xFF,
                    (opts.colorBackground >> 0) & 0xFF,
                    255
            );
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture();
            GlStateManager.enableDepthTest();
        }
    }
}
