package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.listeners.events.PlayerDamageByPlayerEvent;
import gg.projecteden.nexus.models.pvp.PVP;
import gg.projecteden.nexus.models.pvp.PVPService;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DisarmingEnchant extends CustomEnchant implements Listener {

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@EventHandler
	public void onEntityDamageByEntity(PlayerDamageByPlayerEvent event) {
		final Player player = event.getPlayer();
		final PVP victim = new PVPService().get(player);
		final PVP attacker = new PVPService().get(event.getAttacker());

		if (!attacker.isEnabled() || !victim.isEnabled())
			return;

		final ItemStack weapon = attacker.getOnlinePlayer().getInventory().getItemInMainHand();
		if (Nullables.isNullOrAir(weapon) || weapon.getItemMeta() == null)
			return;
		if (!weapon.getItemMeta().hasEnchant(Enchant.DISARMING))
			return;

		final int level = Math.min(weapon.getEnchantmentLevel(Enchant.DISARMING), getMaxLevel());

		if (!RandomUtils.chanceOf(level * (100 / getMaxLevel())))
			return;

		final PlayerInventory inventory = victim.getOnlinePlayer().getInventory();
		final int slot = inventory.getHeldItemSlot();
		Utils.attempt(100, () -> {
			final int newSlot = RandomUtils.randomInt(0, 8);
			if (slot == newSlot)
				return false;

			inventory.setHeldItemSlot(newSlot);
			return true;
		});
	}

}
