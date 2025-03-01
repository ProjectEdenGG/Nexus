package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.listeners.events.fake.FakeBlockPlaceEvent;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.Damageable;

import static gg.projecteden.nexus.utils.BlockUtils.getBlocksInRadius;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class PloughEnchant extends CustomEnchant implements Listener {

	// Excluding coarse/rooted dirt because they need two interactions to become farmland
	private static final MaterialTag PLOUGHABLE = new MaterialTag(Material.GRASS_BLOCK, Material.DIRT);

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@EventHandler(ignoreCancelled = true)
	public void on(PlayerInteractEvent event) {
		var originalBlock = event.getClickedBlock();
		var player = event.getPlayer();
		var tool = player.getInventory().getItemInMainHand();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (isNullOrAir(originalBlock) || isNullOrAir(tool))
			return;

		if (PLOUGHABLE.isNotTagged(originalBlock))
			return;

		if (MaterialTag.HOES.isNotTagged(tool))
			return;

		if (!tool.containsEnchantment(Enchant.PLOUGH))
			return;

		int level = tool.getItemMeta().getEnchantLevel(Enchant.PLOUGH);
		var radius = 3 + ((level - 1) * 2);
		var blocks = getBlocksInRadius(originalBlock, radius / 2, 0, radius / 2);
		var durability = (Damageable) tool.getItemMeta();
		var unbreaking = tool.getEnchantmentLevel(Enchant.UNBREAKING);

		if (!new FakeBlockPlaceEvent(originalBlock, player).callEvent())
			return;

		originalBlock.setType(Material.FARMLAND);
		Block up = originalBlock.getRelative(0, 1, 0);
		if (MaterialTag.NEEDS_SUPPORT.isTagged(up))
			up.breakNaturally(tool);

		for (Block block : blocks) {
			if (block.getLocation().equals(originalBlock.getLocation()))
				continue;

			if (PLOUGHABLE.isNotTagged(block))
				continue;

			if (!new FakeBlockPlaceEvent(block, player).callEvent())
				continue;

			block.setType(Material.FARMLAND);
			up = block.getRelative(0, 1, 0);
			if (MaterialTag.NEEDS_SUPPORT.isTagged(up))
				up.breakNaturally(tool);

			if (RandomUtils.chanceOf(100 / (unbreaking + 1)))
				durability.setDamage(durability.getDamage() + 1);
		}

		tool.setItemMeta(durability);
	}

}
