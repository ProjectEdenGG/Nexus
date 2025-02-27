package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class OrbseekerEnchant extends CustomEnchant implements Listener {

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public @NotNull EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
	}

	@EventHandler
	public void on(EntityDeathEvent event) {
		if (!(event.getDamageSource().getCausingEntity() instanceof Player player))
			return;

		if (event.getDamageSource().getDamageType() != DamageType.PLAYER_ATTACK)
			return;

		var weapon = player.getInventory().getItemInMainHand();
		if (!weapon.containsEnchantment(Enchant.ORBSEEKER))
			return;

		var level = weapon.getEnchantmentLevel(Enchant.ORBSEEKER);
		var multiplier = 1 + (level * .3);
		int newAmount = (int) Math.round(event.getDroppedExp() * multiplier);
		event.setDroppedExp(newAmount);
	}

}

