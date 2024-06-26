package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffectType;

public class EnergizingEnchant  extends CustomEnchant implements Listener {

	@EventHandler
	public void on(BlockBreakEvent event) {
		var player = event.getPlayer();
		var tool = player.getInventory().getItemInMainHand();

		if (Nullables.isNullOrAir(tool) || tool.getItemMeta() == null)
			return;

		int level = getLevel(tool);
		if (level == 0)
			return;

		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING))
			return;

		new PotionEffectBuilder(PotionEffectType.FAST_DIGGING)
				.amplifier(level)
				.duration(20)
			.build()
			.apply(player);
	}

}
