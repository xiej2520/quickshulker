package net.kyrptonaught.quickshulker.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.client.QuickShulkerModClient;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.InputUtil;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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
@Mixin(ContainerScreen.class)
@Environment(EnvType.CLIENT)
public abstract class ScreenMixin extends Screen {
    @Shadow
    protected Slot focusedSlot;

    @Shadow
    @Final
    protected PlayerInventory playerInventory;

    @Shadow
    @Final
    // ScreenHandler handler in 1.16+
    protected Container container;

    @Shadow
    private boolean cancelNextRelease;

    protected ScreenMixin(Text text) {
        super(text);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void fixMouse(CallbackInfo ci) {
        if (QuickShulkerMod.lastMouseX != 0 && QuickShulkerMod.lastMouseY != 0) {
            GLFW.glfwSetCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), QuickShulkerMod.lastMouseX, QuickShulkerMod.lastMouseY);
            QuickShulkerMod.lastMouseY = 0;
            QuickShulkerMod.lastMouseX = 0;
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void QS$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShulkerMod.getConfig().keybindInInv) {
            if (QuickShulkerModClient.getKeybinding().matches(keyCode, InputUtil.Type.KEYSYM)) {
                if (handleTrigger())
                    cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void QS$mousePressed(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShulkerMod.getConfig().rightClickInv) {
            // cursorStack moved from PlayerInventory to ScreenHandler in 1.16?
            if (playerInventory.getCursorStack().isEmpty() && button == 1 && this.focusedSlot != null && this.focusedSlot.getStack().getCount() == 1) {
                if (handleTrigger()) {
                    this.cancelNextRelease = true;
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
        if (QuickShulkerMod.getConfig().keybindInInv) {
            if (QuickShulkerModClient.getKeybinding().matches(button, InputUtil.Type.MOUSE)) {
                if (handleTrigger()) {
                    this.cancelNextRelease = true;
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Unique
    private boolean handleTrigger() {
        if (this.focusedSlot != null) {
            return isValid(this.focusedSlot.getStack(), ClientUtil.getSlotId(container, this.focusedSlot));
        }
        return false;
    }

    @Unique
    private boolean isValid(ItemStack stack, int id) {
        if (this.focusedSlot.inventory instanceof PlayerInventory)
            if (ClientUtil.CheckAndSend(stack, id)) {
                QuickShulkerMod.lastMouseX = MinecraftClient.getInstance().mouse.getX();
                QuickShulkerMod.lastMouseY = MinecraftClient.getInstance().mouse.getY();
                return true;
            }
        return false;
    }


    @Inject(method = "drawSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V"))
    public void drawSlotBackground(Slot slot, CallbackInfo ci) {
        // lame bar, I'm too lazy to draw something or render a texture
        int playerInvUsedSlot = ((ItemInventoryContainer) this.container).getPlayerInvUsedSlot();
        ConfigOptions opts = QuickShulkerMod.getConfig();
        if (opts.fillOpenedBackground
                && playerInvUsedSlot != -1
                && slot.inventory instanceof PlayerInventory
                && ((SlotAccessor) slot).getIndex() == playerInvUsedSlot) {
            int i = slot.xPosition;
            int j = slot.yPosition;

            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            this.itemRenderer.renderGuiQuad(bufferBuilder, i, j, 16, 16,
                    (opts.colorBackground >> 16) & 0xFF,
                    (opts.colorBackground >> 8) & 0xFF,
                    (opts.colorBackground >> 0) & 0xFF,
                    255
            );
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();

        }
    }
    //@Inject(method = "drawSlot",
    //        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V",
    //                shift = At.Shift.AFTER))
    //public void renderSlot(Slot slot, CallbackInfo ci) {
    //    // lame bar, I'm too lazy to draw something or render a texture
    //    int playerInvUsedSlot = ((ItemInventoryContainer) this.container).getPlayerInvUsedSlot();
    //    if (playerInvUsedSlot != -1 && slot.inventory instanceof PlayerInventory && ((SlotAccessor) slot).getIndex() == playerInvUsedSlot) {
    //        int i = slot.xPosition;
    //        int j = slot.yPosition;

    //        RenderSystem.disableDepthTest();
    //        RenderSystem.disableTexture();
    //        RenderSystem.disableAlphaTest();
    //        RenderSystem.disableBlend();
    //        Tessellator tessellator = Tessellator.getInstance();
    //        BufferBuilder bufferBuilder = tessellator.getBuffer();
    //        this.itemRenderer.renderGuiQuad(bufferBuilder, i + 2, j + 13, 13, 2, 90, 40, 240, 255);
    //        this.itemRenderer.renderGuiQuad(bufferBuilder, i + 2, j + 13, 13, 1, 40,240, 40,255);
    //        RenderSystem.enableBlend();
    //        RenderSystem.enableAlphaTest();
    //        RenderSystem.enableTexture();
    //        RenderSystem.enableDepthTest();

    //    }
    //}
}
