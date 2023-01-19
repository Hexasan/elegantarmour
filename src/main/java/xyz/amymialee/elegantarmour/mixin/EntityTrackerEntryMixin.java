package xyz.amymialee.elegantarmour.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.elegantarmour.ElegantArmour;
import xyz.amymialee.elegantarmour.config.ElegantPart;
import xyz.amymialee.elegantarmour.util.IEleganttable;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow @Final private Entity entity;

    @Shadow private int lastHeadPitch;

    @Inject(method = "startTracking", at = @At("TAIL"))
    private void elegantArmour$trackEntity(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.entity.isRemoved()) {
            return;
        }
        if (this.entity instanceof IEleganttable eleganttable) {
            int i = 0;
            for (ElegantPart playerModelPart : eleganttable.getEnabledParts()) {
                i |= playerModelPart.getBitFlag();
            }
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(this.entity.getId());
            buf.writeByte(i);
            ServerPlayNetworking.send(player, ElegantArmour.elegantS2C, buf);
        }
    }
}