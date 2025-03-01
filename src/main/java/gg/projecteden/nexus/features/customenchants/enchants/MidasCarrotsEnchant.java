package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.api.common.utils.RandomUtils.chanceOf;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class MidasCarrotsEnchant extends CustomEnchant implements Listener {

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@EventHandler(ignoreCancelled = true)
	public void on(BlockBreakEvent event) {
		var block = event.getBlock();
		var player = event.getPlayer();
		var tool = player.getInventory().getItemInMainHand();

		if (isNullOrAir(block) || isNullOrAir(tool))
			return;

		if (MaterialTag.HOES.isNotTagged(tool))
			return;

		if (!tool.containsEnchantment(Enchant.MIDAS_CARROTS))
			return;

		int level = tool.getItemMeta().getEnchantLevel(Enchant.MIDAS_CARROTS);
		int chance = 2 + level;
		if (!chanceOf(chance))
			return;

		block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), new ItemStack(Material.GOLDEN_CARROT));
	}

}
