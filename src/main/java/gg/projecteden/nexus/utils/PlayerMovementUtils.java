package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.parchment.HasPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMovementUtils {

	public static void swingHand(Player player) {
		player.swingMainHand();
	}

	public static void swingOffHand(Player player) {
		player.swingOffHand();
	}

	public static void swingHand(Player player, EquipmentSlot slot) {
		player.swingHand(slot);
	}

	public static void lookAt(HasPlayer hasPlayer, Location lookAt) {
		Player player = hasPlayer.getPlayer();
		Vector direction = player.getEyeLocation().toVector().subtract(lookAt.toCenterLocation().toVector()).normalize();
		double x = direction.getX();
		double y = direction.getY();
		double z = direction.getZ();

		// Now change the angle
		Location changed = player.getLocation().clone();
		changed.setYaw(180 - LocationUtils.toDegree(Math.atan2(x, z)));
		changed.setPitch(90 - LocationUtils.toDegree(Math.acos(y)));
		player.teleport(changed);
	}

	private static Map<UUID, Long> lastJumpMap = new HashMap<>();

	public static void jump(Player player) {
		Vector vel = player.getVelocity();

		long time = System.nanoTime();
		boolean canCrit = false;

		long lastJumpTime = lastJumpMap.getOrDefault(player.getUniqueId(), 0L);

		if (time - lastJumpTime > (long) (0.250e9)) {
			lastJumpMap.put(player.getUniqueId(), time);
			canCrit = true;
		}

		net.minecraft.world.entity.player.Player nmsPlayer = NMSUtils.toNMS(player);
		ServerLevel nmsLevel = NMSUtils.toNMS(player.getWorld());

		double jumpPower = 0.42F * getBlockJumpFactor(nmsLevel, nmsPlayer) + nmsPlayer.getJumpBoostPower();

		vel.setY(jumpPower);
		player.setVelocity(vel);
		nmsPlayer.awardStat(Stats.JUMP);

		if (nmsPlayer.isSprinting()) {
			float f = nmsPlayer.getYRot() * 0.017453292F;

			if (canCrit)
				nmsPlayer.setDeltaMovement(nmsPlayer.getDeltaMovement().add((double) (-Mth.sin(f) * 0.2F), 0.0D, (double) (Mth.cos(f) * 0.2F)));
		}
	}

	///

	// protected method in nms Entity
	private static float getBlockJumpFactor(ServerLevel nmsLevel, net.minecraft.world.entity.player.Player nmsPlayer) {
		float f = nmsLevel.getBlockState(nmsPlayer.blockPosition()).getBlock().getJumpFactor();
		float f1 = nmsLevel.getBlockState(getBlockPosBelowThatAffectsMyMovement(nmsPlayer, nmsLevel)).getBlock().getJumpFactor();

		return (double) f == 1.0D ? f1 : f;
	}

	// protected method in nms Entity
	private static BlockPos getBlockPosBelowThatAffectsMyMovement(net.minecraft.world.entity.player.Player nmsPlayer, ServerLevel nmsLevel) {
		return getOnPos(nmsPlayer, nmsLevel, 0.500001F);
	}

	// protected method in nms Entity
	private static BlockPos getOnPos(net.minecraft.world.entity.player.Player nmsPlayer, ServerLevel nmsLevel, float offset) {
		Vec3 position = nmsPlayer.position();
		if (nmsPlayer.mainSupportingBlockPos.isPresent() && nmsLevel.getChunkIfLoadedImmediately(nmsPlayer.mainSupportingBlockPos.get()) != null) {
			BlockPos blockposition = (BlockPos) nmsPlayer.mainSupportingBlockPos.get();

			if (offset <= 1.0E-5F) {
				return blockposition;
			} else {
				BlockState iblockdata = nmsLevel.getBlockState(blockposition);

				return ((double) offset > 0.5D || !iblockdata.is(BlockTags.FENCES)) && !iblockdata.is(BlockTags.WALLS) && !(iblockdata.getBlock() instanceof FenceGateBlock) ? blockposition.atY(Mth.floor(position.y - (double) offset)) : blockposition;
			}
		} else {
			int i = Mth.floor(position.x);
			int j = Mth.floor(position.y - (double) offset);
			int k = Mth.floor(position.z);

			return new BlockPos(i, j, k);
		}
	}

}
