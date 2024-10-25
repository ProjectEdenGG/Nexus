package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.listeners.events.fake.FakeBlockBreakEvent;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.Distance.distance;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;

public class ColumnQuakeEnchant extends CustomEnchant implements Listener {

	@Override
	public int getMaxLevel() {
		return 3;
	}

	private static final int BREAK_LIMIT = 10;
	private static final int LEVEL_BONUS = 5;
	private static final NamespacedKey KEY = new NamespacedKey(Nexus.getInstance(), "column-quake-entity");

	@EventHandler(ignoreCancelled = true)
	public void on(BlockBreakEvent event) {
		if (event instanceof FakeBlockBreakEvent)
			return;

		var original = event.getBlock();

		if (!MaterialTag.NATURAL_GRAVITY_SEDIMENT.isTagged(original))
			return;

		var player = event.getPlayer();
		var tool = player.getInventory().getItemInMainHand();
		if (!MaterialTag.SHOVELS.isTagged(tool))
			return;

		if (!tool.getItemMeta().hasEnchant(Enchant.COLUMN_QUAKE))
			return;

		var level = Math.min(tool.getItemMeta().getEnchantLevel(Enchant.COLUMN_QUAKE), getMaxLevel());
		var breakLimit = BREAK_LIMIT + ((level - 1) * LEVEL_BONUS);

		var blocks = new ArrayList<>(singletonList(original));
		while (blocks.size() <= breakLimit)
			if (!explore(blocks))
				break;

		blocks.sort(comparing(neighbor -> distance(original, neighbor)));
		var toBreak = blocks.subList(0, Math.min(blocks.size(), breakLimit));

		final Map<Location, BlockData> fallingBlockData = new HashMap<>();

		for (Block block : toBreak) {
			if (block.getLocation().equals(original.getLocation()))
				continue;

			if (!new FakeBlockBreakEvent(block, player).callEvent())
				continue;

			fallingBlockData.put(block.getLocation().toCenterLocation(), block.getBlockData());
			block.setType(Material.AIR);
		}

		fallingBlockData.keySet().stream().sorted(Comparator.comparing(Location::getY)).forEach(location -> {
			location.getWorld().spawn(location, FallingBlock.class, fb -> {
				fb.setBlockData(fallingBlockData.get(location));
				fb.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
			});
		});
	}

	private boolean explore(List<Block> blocks) {
		var before = blocks.size();
		for (Block block : new ArrayList<>(blocks))
			for (Block relative : BlockUtils.getBlocksInRadius(block, 0, 1, 0))
				if (MaterialTag.NATURAL_GRAVITY_SEDIMENT.isTagged(relative))
					if (!blocks.contains(relative))
						blocks.add(relative);

		return before != blocks.size();
	}

	@EventHandler
	public void on(EntityChangeBlockEvent event) {
		if (!event.getEntity().getPersistentDataContainer().has(KEY))
			return;

		event.setCancelled(true);
		event.getEntity().remove();
		event.getBlock().getWorld().dropItem(event.getBlock().getLocation().toCenterLocation(), new ItemStack(event.getTo()));
	}

}
