package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.api.common.utils.RandomUtils.chanceOf;
import static gg.projecteden.nexus.utils.NMSUtils.toNMS;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Composter implements Listener {

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!handleBambooComposting(event.getItem(), event.getClickedBlock()))
			return;

		if (event.getHand() == null)
			return;

		switch (event.getHand()) {
			case HAND -> event.getPlayer().swingMainHand();
			case OFF_HAND -> event.getPlayer().swingOffHand();
		}
	}

	@EventHandler
	public void on(InventoryMoveItemEvent event) {
		if (!(event.getDestination().getHolder() instanceof BlockInventoryHolder holder))
			return;

		handleBambooComposting(event.getItem(), holder.getBlock());
	}

	public boolean handleBambooComposting(ItemStack item, Block block) {
		if (isNullOrAir(item))
			return false;

		if (isNullOrAir(block))
			return false;

		if (item.getType() != Material.BAMBOO)
			return false;

		if (block.getType() != Material.COMPOSTER)
			return false;

		if (!(block.getBlockData() instanceof Levelled composter))
			return false;

		if (composter.getLevel() >= composter.getMaximumLevel())
			return false;

		item.subtract();

		final boolean increase = chanceOf(30);
		if (increase) {
			composter.setLevel(composter.getLevel() + 1);
			block.setBlockData(composter);
		}

		// TODO y no work
//		ComposterBlock.handleFill(toNMS(block.getLocation().getWorld()), toNMS(block.getLocation()), increase);

		handleFill(block, increase);

		return true;
	}

	private void handleFill(Block block, boolean increase) {
		final ServerLevel world = toNMS(block.getLocation().getWorld());
		final BlockPos pos = toNMS(block.getLocation());

		new SoundBuilder(increase ? Sound.BLOCK_COMPOSTER_FILL_SUCCESS : Sound.BLOCK_COMPOSTER_FILL)
			.location(block)
			.category(SoundCategory.BLOCKS)
			.play();

		double d0 = world.getBlockState(pos).getShape(world, pos).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
		double d1 = 0.13124999403953552D;
		double d2 = 0.737500011920929D;
		RandomSource randomsource = world.getRandom();

		for (int i = 0; i < 10; ++i) {
			final double x = (double) pos.getX() + d1 + d2 * (double) randomsource.nextFloat();
			final double y = (double) pos.getY() + d0 + (double) randomsource.nextFloat() * (1.0D - d0);
			final double z = (double) pos.getZ() + d1 + d2 * (double) randomsource.nextFloat();

			double d3 = randomsource.nextGaussian() * 0.02D;
			double d4 = randomsource.nextGaussian() * 0.02D;
			double d5 = randomsource.nextGaussian() * 0.02D;

			new ParticleBuilder(Particle.COMPOSTER)
				.location(block.getWorld(), x, y, z)
				.offset(d3, d4, d5)
				.spawn();
		}
	}

}
