package gg.projecteden.nexus.features.equipment.stattrack.stats.armor;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Id("fall_damage_absorbed")
@DisplayName("Fall Damage Absorbed")
public class FallDamageAbsorbed extends StatTrackStatistic {
	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.ALL_BOOTS;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	@SuppressWarnings("deprecation")
	public void onFallDamage(EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		if (!event.isApplicable(EntityDamageEvent.DamageModifier.MAGIC))
			return;

		ItemStack boots = player.getInventory().getBoots();
		if (boots.getType().isAir())
			return;

		double totalEnchantmentMitigation = Math.max(0, -event.getDamage(EntityDamageEvent.DamageModifier.MAGIC));

		if (totalEnchantmentMitigation <= 0)
			return;

		int bootsProtection = getFallProtectionPoints(boots, true);
		if (bootsProtection <= 0)
			return;

		int totalProtection = 0;

		totalProtection += getFallProtectionPoints(player.getInventory().getHelmet(), false);
		totalProtection += getFallProtectionPoints(player.getInventory().getChestplate(), false);
		totalProtection += getFallProtectionPoints(player.getInventory().getLeggings(), false);

		totalProtection += bootsProtection;

		if (totalProtection <= 0)
			return;

		double bootsShare = (double) bootsProtection / totalProtection;
		double mitigatedByBoots = totalEnchantmentMitigation * bootsShare;

		track(boots, mitigatedByBoots);
	}

	private int getFallProtectionPoints(ItemStack item, boolean boots) {
		if (item == null || item.getType().isAir())
			return 0;

		int points = item.getEnchantmentLevel(Enchantment.PROTECTION);

		if (boots)
			points += item.getEnchantmentLevel(Enchantment.FEATHER_FALLING) * 3;

		return points;
	}

}
