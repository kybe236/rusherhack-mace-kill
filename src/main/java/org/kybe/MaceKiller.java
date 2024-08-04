package org.kybe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.rusherhack.client.api.events.player.EventInteract;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;


/**
 * Maze Kill Module
 *
 * @author kybe236
 */
public class MazeKiller extends ToggleableModule {

        /**
         * Settings
         */
        private final NumberSetting<Integer> height = new NumberSetting<>("Height", "Height", 170, 1, 170);
        private final BooleanSetting maximum = new BooleanSetting("Maximum", "Maximum", true);


        /**
         * Constructor
         */
        public MazeKiller() {
                super("Maze Killer", "Makes more damage with maze", ModuleCategory.CLIENT);

                //register settings
                this.registerSettings(
                                this.height,
                                this.maximum
                );
        }

        @SuppressWarnings("ConstantConditions")
    @Subscribe

        public void onUpdate(EventInteract event) {
                //check if the module is enabled
                if (!this.isToggled()) {
                        return;
                }

                try {
                        if (!event.getAction().equals(EventInteract.Action.ATTACK)) {
                                return;
                        }
            if (!(mc.player.getMainHandItem().getItem() == Items.MACE)) {
                                return;
                        }

                        Entity targetEntity = event.getTargetEntity();
            if (event.isCancelled() || targetEntity.isInvulnerable()) return;

            Vec3 previouspos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());

                        int blocks = getMaxHeightAbovePlayer();

                        if (blocks == 0) {
                                ChatUtils.print("No suitable position found");
                                return;
                        }

                        int packetsRequired = (int) (double) Math.abs(blocks / 10);

                        if (packetsRequired > 20) {
                                packetsRequired = 1;
                        }

            if (mc.player.getVehicle() != null) {
                for (int packetNumber = 0; packetNumber < (packetsRequired - 1); packetNumber++) {
                    mc.player.connection.send(new ServerboundMoveVehiclePacket(mc.player.getVehicle()));
                }
                mc.player.getVehicle().setPos(mc.player.getVehicle().getX(), mc.player.getVehicle().getY() + blocks, mc.player.getVehicle().getZ());
                mc.player.connection.send(new ServerboundMoveVehiclePacket(mc.player.getVehicle()));
            } else {
                for (int packetNumber = 0; packetNumber < (packetsRequired - 1); packetNumber++) {
                    mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(false));
                }
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ(), false));
            }

            if (mc.player.getVehicle() != null) {
                mc.player.getVehicle().setPos(previouspos);
                mc.player.connection.send(new ServerboundMoveVehiclePacket(mc.player.getVehicle()));
                // Do it again to be sure it happens
                mc.player.getVehicle().setPos(previouspos);
                mc.player.connection.send(new ServerboundMoveVehiclePacket(mc.player.getVehicle()));
            } else {
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(previouspos.get(Direction.Axis.X), previouspos.get(Direction.Axis.Y), previouspos.get(Direction.Axis.Z), false));
                // Do it again to be sure it happens
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(previouspos.get(Direction.Axis.X), previouspos.get(Direction.Axis.Y), previouspos.get(Direction.Axis.Z), false));
            }

        } catch (Exception e) {
                        ChatUtils.print("Error: " + e.getMessage());
                }

        }

        private int getMaxHeightAbovePlayer() {
        assert mc.player != null;
        BlockPos playerPos = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ());
                int maxHeight = maximum.getValue() ? 170 : height.getValue();
                maxHeight += playerPos.getY();

                for (int i = maxHeight; i > playerPos.getY(); i--) {
                        BlockPos isopenair1 = new BlockPos(playerPos.getX(), i, playerPos.getZ());
                        BlockPos isopenair2 = isopenair1.above(1);
                        if (isSafeBlock(isopenair1) && isSafeBlock(isopenair2)) {
                                return i - playerPos.getY();
                        }
                }
                return 0; // Return 0 if no suitable position is found
        }

        private boolean isSafeBlock(BlockPos pos) {
        assert mc.level != null;
                return mc.level.getBlockState(pos).getBlock().equals(Blocks.AIR) || mc.level.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.level.getBlockState(pos).getBlock().equals(Blocks.POWDER_SNOW);
        }

}