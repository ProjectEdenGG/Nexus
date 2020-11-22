package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class LightTheTree implements Listener {
	Pugmas20Service service = new Pugmas20Service();
	private static final String lighterRg = Pugmas20.getRegion() + "_lighter";
	private static final String torchRg = Pugmas20.getRegion() + "_torch_";
	private static final String treeTorchRg = Pugmas20.getRegion() + "_treetorch_";
	//
	private static final ItemBuilder lighter_broken = Pugmas20.pugmasItem(Material.FLINT_AND_STEEL).name("Broken Ceremonial Lighter");
	private static final ItemBuilder lighter = Pugmas20.pugmasItem(Material.FLINT_AND_STEEL).name("Ceremonial Lighter");

	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!Pugmas20.isAtPugmas(player)) return;

		Entity entity = event.getRightClicked();
		if (!entity.getType().equals(EntityType.ITEM_FRAME)) return;
		if (!Pugmas20.WGUtils.isInRegion(entity.getLocation(), lighterRg)) return;

		ItemUtils.giveItem(player, lighter_broken.build());
	}
}
