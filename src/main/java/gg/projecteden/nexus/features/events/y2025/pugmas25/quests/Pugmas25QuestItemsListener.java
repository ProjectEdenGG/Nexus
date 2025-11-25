package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.virtualinventories.events.VirtualInventoryConstructEvent;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualPersonalBarrel;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class Pugmas25QuestItemsListener implements Listener {

	public Pugmas25QuestItemsListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(VirtualInventoryConstructEvent event) {
		if (!(event.getInventory() instanceof VirtualPersonalBarrel virtualPersonalBarrel))
			return;

		if (!Pugmas25.get().isAtEvent(virtualPersonalBarrel.getLocation()))
			return;

		virtualPersonalBarrel.getItems().add(CommonQuestItem.COIN_POUCH.get());
	}

	@EventHandler
	public void on(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!Pugmas25.get().shouldHandle(player))
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		if (!Pugmas25QuestItem.RED_BALLOON.isInInventoryOf(player))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(DecorationInteractEvent event) {
		if (event.getDecorationType() != DecorationType.SNOWMAN_PLAIN)
			return;

		Player player = event.getPlayer();
		if (!Pugmas25.get().isAtEvent(player))
			return;

		ItemStack tool = player.getInventory().getItemInMainHand();
		if (!Pugmas25QuestItem.SNOWMAN_DECORATIONS.fuzzyMatch(tool))
			return;

		event.setCancelled(true);
		tool.subtract();
		Dev.WAKKA.send("Decorated snowman");
	}

	// TODO: MAYBE SWITCH TO A TOOL ATTRIBUTE THING
	@EventHandler
	public void on(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25.get().isAtEvent(player))
			return;

		ItemStack item = event.getItem();
		if (!Pugmas25QuestItem.SHRINK_POTION.fuzzyMatch(item))
			return;

		PlayerUtils.send(player, "TODO: SHRINK PLAYER FOR X MINUTES");
	}

	@EventHandler
	public void on(EntityPotionEffectEvent event) {
		if (event.getCause() != Cause.EXPIRATION)
			return;
	}


}
