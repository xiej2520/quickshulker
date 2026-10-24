package net.kyrptonaught.quickshulker.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.client.QuickShulkerModClient;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// @Mixin(HandledScreen.class)
@Mixin(AbstractContainerScreen.class)
@Environment(EnvType.CLIENT)
public abstract class ScreenMixin extends Screen {
    @Shadow
    protected Slot hoveredSlot;

    @Shadow
    @Final
    // AbstractContainerMenu menu (ScreenHandler handler in 1.16+ yarn)
    protected AbstractContainerMenu menu;

    @Shadow
    private boolean skipNextRelease;

    protected ScreenMixin(Component text) {
        super(text);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void fixMouse(CallbackInfo ci) {
        if (QuickShulkerMod.lastMouseX != 0 && QuickShulkerMod.lastMouseY != 0) {
            GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(), QuickShulkerMod.lastMouseX, QuickShulkerMod.lastMouseY);
            QuickShulkerMod.lastMouseY = 0;
            QuickShulkerMod.lastMouseX = 0;
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void QS$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShulkerMod.getConfig().keybindInInv) {
            if (QuickShulkerModClient.getKeybinding().matches(keyCode, InputConstants.Type.KEYSYM)) {
                if (handleTrigger())
                    cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void QS$mousePressed(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShulkerMod.getConfig().rightClickInv) {
            // cursorStack moved from PlayerInventory to ScreenHandler in 1.16?
            if (this.menu.getCarried().isEmpty() && button == 1 && this.hoveredSlot != null && this.hoveredSlot.getItem().getCount() == 1) {
                if (handleTrigger()) {
                    this.skipNextRelease = true;
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
        if (QuickShulkerMod.getConfig().keybindInInv) {
            if (QuickShulkerModClient.getKeybinding().matches(button, InputConstants.Type.MOUSE)) {
                if (handleTrigger()) {
                    this.skipNextRelease = true;
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Unique
    private boolean handleTrigger() {
        if (this.hoveredSlot != null) {
            if (QuickShulkerMod.getConfig().rightClickClose && this.hoveredSlot.container instanceof Inventory
                    && ClientUtil.getPlayerInvSlot(menu, this.hoveredSlot) == ((ItemInventoryContainer) menu).getPlayerInvUsedSlot()) {
                QuickShulkerMod.lastMouseX = this.minecraft.mouseHandler.xpos();
                QuickShulkerMod.lastMouseY = this.minecraft.mouseHandler.ypos();
                this.minecraft.player.closeContainer();
                this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
                return true;
            }
            return isValid(this.hoveredSlot.getItem(), ClientUtil.getSlotId(menu, this.hoveredSlot));
        }
        return false;
    }

    @Unique
    private boolean isValid(ItemStack stack, int id) {
        if (this.hoveredSlot.container instanceof Inventory)
            if (ClientUtil.CheckAndSend(stack, id)) {
                QuickShulkerMod.lastMouseX = Minecraft.getInstance().mouseHandler.xpos();
                QuickShulkerMod.lastMouseY = Minecraft.getInstance().mouseHandler.ypos();
                return true;
            }
        return false;
    }


    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void drawSlotBackground(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        // Mark the slot holding the opened item, behind the item itself.
        int playerInvUsedSlot = ((ItemInventoryContainer) this.menu).getPlayerInvUsedSlot();
        ConfigOptions opts = QuickShulkerMod.getConfig();
        if (opts.fillOpenedBackground
                && playerInvUsedSlot != -1
                && slot.container instanceof Inventory
                && ((SlotAccessor) slot).getIndex() == playerInvUsedSlot) {
            GuiComponent.fill(poseStack, slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFF000000 | opts.colorBackground);
        }
    }
    //@Inject(method = "renderSlot",
    //        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderAndDecorateItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;II)V",
    //                shift = At.Shift.AFTER))
    //public void renderSlot(Slot slot, CallbackInfo ci) {
    //    // lame bar, I'm too lazy to draw something or render a texture
    //    int playerInvUsedSlot = ((ItemInventoryContainer) this.menu).getPlayerInvUsedSlot();
    //    if (playerInvUsedSlot != -1 && slot.inventory instanceof PlayerInventory && ((SlotAccessor) slot).getIndex() == playerInvUsedSlot) {
    //        int i = slot.xPosition;
    //        int j = slot.yPosition;

    //        RenderSystem.disableDepthTest();
    //        RenderSystem.disableTexture();
    //        RenderSystem.disableAlphaTest();
    //        RenderSystem.disableBlend();
    //        Tessellator tessellator = Tessellator.getInstance();
    //        BufferBuilder bufferBuilder = tessellator.getBuffer();
    //        this.itemRenderer.fillRect(bufferBuilder, i + 2, j + 13, 13, 2, 90, 40, 240, 255);
    //        this.itemRenderer.fillRect(bufferBuilder, i + 2, j + 13, 13, 1, 40,240, 40,255);
    //        RenderSystem.enableBlend();
    //        RenderSystem.enableAlphaTest();
    //        RenderSystem.enableTexture();
    //        RenderSystem.enableDepthTest();

    //    }
    //}
}
