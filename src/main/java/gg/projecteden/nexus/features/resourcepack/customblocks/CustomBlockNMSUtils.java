package gg.projecteden.nexus.features.resourcepack.customblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.SoundCategory;
import org.bukkit.SoundGroup;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomBlockNMSUtils {

	// https://github.com/oraxen/oraxen/blob/master/v1_21_R3/src/main/java/io/th0rgal/oraxen/nms/v1_21_R3/NMSHandler.java#L154
	public static BlockData tryPlaceVanillaBlock(Player player, ItemStack itemStack) {
		InteractionHand hand = InteractionHand.MAIN_HAND;
		net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
		BlockHitResult hitResult = getPlayerPOVHitResult(serverPlayer.level(), serverPlayer, ClipContext.Fluid.NONE);
		BlockPlaceContext placeContext = new BlockPlaceContext(new UseOnContext(serverPlayer, hand, hitResult));

		if (!(nmsStack.getItem() instanceof BlockItem blockItem)) {
			nmsStack.getItem().useOn(new UseOnContext(serverPlayer, hand, hitResult));

			if (!player.isSneaking())
				serverPlayer.gameMode.useItem(serverPlayer, serverPlayer.level(), nmsStack, hand);

			return null;
		}

		// Shulker-Boxes are DirectionalPlace based unlike other directional-blocks
		if (org.bukkit.Tag.SHULKER_BOXES.isTagged(itemStack.getType())) {
			placeContext = new DirectionalPlaceContext(serverPlayer.level(), hitResult.getBlockPos(),
				hitResult.getDirection(), nmsStack, hitResult.getDirection().getOpposite());
		}

		BlockPos pos = hitResult.getBlockPos();
		InteractionResult result = blockItem.place(placeContext);
		if (result == InteractionResult.FAIL)
			return null;

		World world = player.getWorld();
		BlockPos clickPos = placeContext.getClickedPos();
		Block block = world.getBlockAt(clickPos.getX(), clickPos.getY(), clickPos.getZ());
		SoundGroup sound = block.getBlockData().getSoundGroup();

		world.playSound(block.getLocation().toCenterLocation(), sound.getPlaceSound(),
			SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

		return player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getBlockData();
	}

	public static BlockHitResult getPlayerPOVHitResult(Level world, net.minecraft.world.entity.player.Player player, ClipContext.Fluid fluidHandling) {
		float pitch = player.getXRot();
		float yaw = player.getYRot();
		Vec3 eyePosition = player.getEyePosition();

		// Calculate direction vector components based on pitch and yaw
		float yawCos = Mth.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
		float yawSin = Mth.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
		float pitchCos = -Mth.cos(-pitch * ((float) Math.PI / 180F));
		float pitchSin = Mth.sin(-pitch * ((float) Math.PI / 180F));

		float directionX = yawSin * pitchCos;
		float directionY = pitchSin;
		float directionZ = yawCos * pitchCos;

		double reachDistance = 5.0D;
		Vec3 vec32 = eyePosition.add((double) directionX * reachDistance, (double) directionY * reachDistance, (double) directionZ * reachDistance);

		return world.clip(new ClipContext(eyePosition, vec32, ClipContext.Block.OUTLINE, fluidHandling, player));
	}
}
