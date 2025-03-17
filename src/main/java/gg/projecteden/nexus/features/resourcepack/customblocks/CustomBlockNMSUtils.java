package gg.projecteden.nexus.features.resourcepack.customblocks;

import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.nms.NMSUtils;
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
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.SoundGroup;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomBlockNMSUtils {

	public static Block tryPlaceVanillaBlock(Player player, ItemStack itemStack) {
		// TODO: Fix boats, currently Item#use in BoatItem calls PlayerInteractEvent, thus causing a StackOverflow, find a workaround
		if (MaterialTag.ITEMS_BOATS.isTagged(itemStack.getType()))
			return null;

		InteractionHand hand = InteractionHand.MAIN_HAND;
		net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
		BlockHitResult hitResult = getPlayerPOVHitResult(serverPlayer.level(), serverPlayer, ClipContext.Fluid.NONE);

		Location clickedBlockNMS = NMSUtils.fromNMS(player.getWorld(), hitResult.getBlockPos());
		if (Nullables.isNullOrAir(clickedBlockNMS.getBlock().getType())) {
			CustomBlockUtils.debug(player, "&c- nms clicked block was null/air");
			return null;
		}

		BlockPlaceContext placeContext = new BlockPlaceContext(new UseOnContext(serverPlayer, hand, hitResult));

		boolean shouldSubtract = ItemUtils.shouldSubtract(player, itemStack);
		int originalAmount = itemStack.getAmount();
		if (!(nmsStack.getItem() instanceof BlockItem blockItem)) {
			CustomBlockUtils.debug(player, "&e- item is not a BlockItem");
			nmsStack.getItem().useOn(new UseOnContext(serverPlayer, hand, hitResult));

			if (!shouldSubtract && originalAmount != itemStack.getAmount())
				itemStack.setAmount(originalAmount);

			if (!player.isSneaking())
				serverPlayer.gameMode.useItem(serverPlayer, serverPlayer.level(), nmsStack, hand);

			return null;
		}

		CustomBlockUtils.debug(player, "&e- item is a BlockItem");

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

		CustomBlockUtils.debug(player, "&e- playing nms sound: " + sound.getPlaceSound().getKey().getKey());
		world.playSound(block.getLocation().toCenterLocation(), sound.getPlaceSound(),
			SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

		return player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
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
