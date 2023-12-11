package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.Distance.distance;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;

public class VeinMinerEnchant extends CustomEnchant implements Listener {

	public VeinMinerEnchant(@NotNull NamespacedKey key) {
		super(key);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	private static final int BREAK_LIMIT = 8;
	private static final int LEVEL_BONUS = 4;

	@EventHandler
	public void on(BlockBreakEvent event) {
		var block = event.getBlock();

		if (!MaterialTag.MINERAL_ORES.isTagged(block))
			return;

		var player = event.getPlayer();
		var tool = player.getInventory().getItemInMainHand();
		if (!MaterialTag.PICKAXES.isTagged(tool))
			return;

		if (!tool.getItemMeta().hasEnchant(Enchant.VEIN_MINER))
			return;

		var level = Math.min(tool.getItemMeta().getEnchantLevel(Enchant.VEIN_MINER), getMaxLevel());
		var breakLimit = BREAK_LIMIT + (--level * LEVEL_BONUS);

		var blocks = new ArrayList<>(singletonList(block));
		while (blocks.size() <= breakLimit)
			if (!explore(blocks))
				break;

		blocks.sort(comparing(neighbor -> distance(block, neighbor)));
		final List<Block> toBreak = blocks.subList(0, Math.min(blocks.size(), breakLimit));
		for (Block result : toBreak)
			result.breakNaturally(tool, true, true);
	}

	private boolean explore(List<Block> blocks) {
		var before = blocks.size();
		for (Block block : new ArrayList<>(blocks))
			for (Block relative : BlockUtils.getBlocksInRadius(block, 1))
				if (relative.getType() == block.getType())
					if (!blocks.contains(relative))
						blocks.add(relative);

		return before != blocks.size();
	}

}
