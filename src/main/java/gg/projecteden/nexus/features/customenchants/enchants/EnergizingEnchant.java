package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class EnergizingEnchant  extends CustomEnchant implements Listener {

	@EventHandler
	public void on(BlockBreakEvent event) {
		var player = event.getPlayer();
		var tool = player.getInventory().getItemInMainHand();

		if (Nullables.isNullOrAir(tool) || tool.getItemMeta() == null)
			return;

		if (getLevel(tool) == 0)
			return;

		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING))
			return;

		new PotionEffectBuilder(PotionEffectType.FAST_DIGGING)
			.amplifier(2)
			.duration(2)
			.build()
			.apply(player);
	}

}
