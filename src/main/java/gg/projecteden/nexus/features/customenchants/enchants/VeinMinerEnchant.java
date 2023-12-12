package gg.projecteden.nexus.features.customenchants.enchants;

import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.api.common.utils.RandomUtils.chanceOf;
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

	@EventHandler(ignoreCancelled = true)
	public void on(BlockBreakEvent event) {
		if (event instanceof FakeBlockBreakEvent)
			return;

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
		var breakLimit = BREAK_LIMIT + ((level - 1) * LEVEL_BONUS);

		var blocks = new ArrayList<>(singletonList(block));
		while (blocks.size() <= breakLimit)
			if (!explore(blocks))
				break;

		blocks.sort(comparing(neighbor -> distance(block, neighbor)));
		var toBreak = blocks.subList(0, Math.min(blocks.size(), breakLimit));
		var durability = (Damageable) tool.getItemMeta();
		var unbreaking = tool.getEnchantmentLevel(Enchant.UNBREAKING);

		for (Block result : toBreak) {
			if (!new FakeBlockBreakEvent(result, player).callEvent())
				continue;

			result.breakNaturally(tool, true, true);
			if (chanceOf(100 / (unbreaking + 1)))
				durability.setDamage(durability.getDamage() + 1);
		}

		tool.setItemMeta(durability);
	}

	private boolean explore(List<Block> blocks) {
		var before = blocks.size();
		for (Block block : new ArrayList<>(blocks))
			for (Block relative : BlockUtils.getBlocksInRadius(block, 1))
				if (MaterialTag.MINERAL_ORES.isTagged(relative))
					if (!blocks.contains(relative))
						blocks.add(relative);

		return before != blocks.size();
	}

}
