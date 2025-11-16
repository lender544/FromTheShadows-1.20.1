package net.sonmok14.fromtheshadows.server.utils.handler;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.server.Fromtheshadows;
import net.sonmok14.fromtheshadows.server.entity.mob.NehemothEntity;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID)
public class RideLookHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        if (player.isPassenger()) {
            Entity vehicle = player.getVehicle();


            if (vehicle instanceof NehemothEntity) {
                Vec3 eyePos = player.getEyePosition();
                Vec3 targetPos = vehicle.position().add(0, vehicle.getBbHeight() * 0.5, 0);

                Vec3 dir = targetPos.subtract(eyePos);
                double dx = dir.x;
                double dy = dir.y;
                double dz = dir.z;

                double distXZ = Math.sqrt(dx * dx + dz * dz);

                float yaw = (float)(Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
                float pitch = (float)-(Mth.atan2(dy, distXZ) * (180F / Math.PI));

                player.setYRot(yaw);
                player.setXRot(pitch);

                player.yHeadRot = yaw;
                player.yBodyRot = yaw;
            }
        }
    }
}