package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.OpenableItemUtil;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.PLAYER_INVENTORY_OFF_HAND_SLOT;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    public void useItem(PlayerEntity player, World world, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResultHolder<ItemStack> result = interactItem(player, world, hand);
        if (result.getResult() != InteractionResult.PASS) {
            cir.setReturnValue(result.getResult());
        }
    }

    @Unique
    private InteractionResultHolder<ItemStack> interactItem(PlayerEntity player, World world, InteractionHand hand) {
        ItemStack stack = player.getHandStack(hand);
        if (!world.isClient && player instanceof ServerPlayerEntity) {
            if (QuickShulkerMod.getConfig().rightClickToOpen) {
                if (OpenableItemUtil.isOpenableItem(stack) && OpenableItemUtil.canOpenInHand(stack)) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        OpenableItemUtil.openItem((ServerPlayerEntity) player, player.inventory.selectedSlot);
                    } else {
                        OpenableItemUtil.openItem((ServerPlayerEntity) player, PLAYER_INVENTORY_OFF_HAND_SLOT);
                    }

                    return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
                }
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

}
