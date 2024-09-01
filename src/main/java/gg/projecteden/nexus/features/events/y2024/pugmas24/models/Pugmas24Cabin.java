package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Pugmas24Cabin implements Listener {

	public Pugmas24Cabin() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!Pugmas24.get().isAtEvent(event))
			return;

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		ItemStack itemStack = itemFrame.getItem();
		if (Nullables.isNullOrAir(itemStack))
			return;

		if (!CustomMaterial.PUGMAS24_CALENDAR.is(itemStack))
			return;

		PlayerUtils.runCommand(event.getPlayer(), "pugmas24 advent");
	}
}
