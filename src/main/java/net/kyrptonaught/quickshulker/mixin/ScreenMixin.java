package net.kyrptonaught.quickshulker.mixin;

import malilib.util.data.Color4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.config.ClientConfigs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.game.inventory.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.game.inventory.InventoryMenuScreen;
import net.minecraft.client.gui.screen.game.inventory.SurvivalInventoryScreen;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.vertex.BufferBuilder;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.entity.living.player.PlayerInventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.CreativeModeTab;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.kyrptonaught.quickshulker.client.QuickShulkerModClient.LAST_MOUSE_X;
import static net.kyrptonaught.quickshulker.client.QuickShulkerModClient.LAST_MOUSE_Y;

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
        if (LAST_MOUSE_X != -1.0 && LAST_MOUSE_Y != -1.0) {
            Mouse.setCursorPosition((int) LAST_MOUSE_X, (int) LAST_MOUSE_Y);
            LAST_MOUSE_Y = -1;
            LAST_MOUSE_X = -1;
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void QS$keyPressed(char chr, int key, CallbackInfo ci) {
        if (ClientConfigs.Options.KEYBIND_OPEN_IN_HAND.getValue()) {
            if (ClientConfigs.HotKeys.QUICKSHULKER_KEYBIND.getKeyBind().isKeyBindHeld()) {
                handleTrigger();
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void QS$mousePressed(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (ClientConfigs.Options.RIGHT_CLICK_OPEN_IN_INVENTORY.getValue()) {
            if (this.minecraft.player.inventory.getCursorItem().isEmpty() && mouseButton == 1 && this.hoveredSlot != null && this.hoveredSlot.getItem().getSize() == 1) {
                if (handleTrigger()) {
                    this.cancelNextMouseRelease = true;
                    ci.cancel();
                }
            }
        }
        if (ClientConfigs.Options.KEYBIND_OPEN_IN_INVENTORY.getValue()) {
            if (ClientConfigs.HotKeys.QUICKSHULKER_KEYBIND.getKeyBind().isKeyBindHeld()) {
                if (handleTrigger()) {
                    this.cancelNextMouseRelease = true;
                    ci.cancel();
                }
            }
        }
    }

    @Unique
    // return whether to cancel further processing due to opening a new screen
    private boolean handleTrigger() {
        InventorySlot slot = this.hoveredSlot;
        // only allow opening player inventory items
        if (slot == null || !(slot.inventory instanceof PlayerInventory)) {
            return false;
        }

        ItemStack stack = slot.getItem();
        if (stack.getSize() != 1) {
            return false;
        }

        int playerInvIndex = ((SlotAccessor) slot).getInventoryIndex();
        int currentInvUsedSlot = ((ItemInventoryContainer) this.menu).getPlayerInvUsedSlot();

        if (slot instanceof CreativeInventoryScreen.CreativeInventorySlot && ((Object) this) instanceof CreativeInventoryScreen) {
            CreativeInventoryScreen screen = (CreativeInventoryScreen) (Object) this;
            if (screen.getSelectedTab() == CreativeModeTab.INVENTORY.getId()) {
                playerInvIndex = ((SlotAccessor) ((CreativeSlotMixin) slot).getSlot()).getInventoryIndex();
            }
        }

        if (ClientConfigs.Options.RIGHT_CLICK_CLOSE_IN_INVENTORY.getValue() && playerInvIndex == currentInvUsedSlot) {
            this.minecraft.player.closeMenu();
            this.minecraft.openScreen(new SurvivalInventoryScreen(this.minecraft.player));
            return true;
        }

        if (ClientUtil.tryOpenAndSendPacket(stack, playerInvIndex)) {
            LAST_MOUSE_X = Mouse.getX();
            LAST_MOUSE_Y = Mouse.getY();
            return true;
        }

        return false;
    }


    @Inject(method = "renderSlot", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/render/entity/ItemRenderer;renderGuiItem(Lnet/minecraft/entity/living/LivingEntity;Lnet/minecraft/item/ItemStack;II)V"))
    public void drawSlotBackground(InventorySlot slot, CallbackInfo ci) {
        // lame bar, I'm too lazy to draw something or render a texture
        int playerInvUsedSlot = ((ItemInventoryContainer) this.menu).getPlayerInvUsedSlot();
        if (ClientConfigs.Options.DRAW_OPENED_BACKGROUND_COLOR.getValue()
            && playerInvUsedSlot != -1
            && slot.inventory instanceof PlayerInventory
            && ((SlotAccessor) slot).getInventoryIndex() == playerInvUsedSlot) {
            int i = slot.x;
            int j = slot.y;

            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            Color4f color = ClientConfigs.Options.OPENED_BACKGROUND_COLOR.getColor();

            this.itemRenderer.fill(bufferBuilder, i, j, 16, 16,
                color.ri,
                color.gi,
                color.bi,
                color.ai
            );
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture();
            GlStateManager.enableDepthTest();
        }
    }
}
