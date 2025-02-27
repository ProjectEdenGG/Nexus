package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.listeners.KillerMoney.KillerMoneyEarnedEvent;
import gg.projecteden.nexus.utils.Enchant;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class BountyEnchant extends CustomEnchant implements Listener {

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public @NotNull EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
	}

	@EventHandler
	public void on(KillerMoneyEarnedEvent event) {
		var weapon = event.getPlayer().getInventory().getItemInMainHand();
		if (!weapon.containsEnchantment(Enchant.BOUNTY))
			return;

		var level = weapon.getEnchantmentLevel(Enchant.BOUNTY);
		var multiplier = 1 + (level * .3);
		event.setMoney(event.getMoney() * multiplier);
	}

}
