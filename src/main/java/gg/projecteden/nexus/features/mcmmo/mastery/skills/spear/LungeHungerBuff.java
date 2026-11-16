package gg.projecteden.nexus.features.mcmmo.mastery.skills.spear;

import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason;
import org.bukkit.inventory.ItemStack;

public class LungeHungerBuff implements Listener {

	public static final String PERMISSION = "nexus.mcmmo.spears.lunge_hunger_buff";

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLungeExhaustion(EntityExhaustionEvent event) {
		if (event.getExhaustionReason() != ExhaustionReason.ENCHANTMENT_EFFECT) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!player.hasPermission(LungeHungerBuff.PERMISSION)) return;

		ItemStack spear = player.getInventory().getItemInMainHand();
		if (!MaterialTag.SPEARS.isTagged(spear)) return;

		int lungeLevel = spear.getEnchantmentLevel(Enchantment.LUNGE);
		if (lungeLevel > 0 && event.getExhaustion() == 4.0f * lungeLevel)
			event.setCancelled(true);
	}

}
