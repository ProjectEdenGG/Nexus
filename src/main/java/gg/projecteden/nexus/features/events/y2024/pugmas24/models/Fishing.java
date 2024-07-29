package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.PlayerEventFishingBiteEvent;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Fishing implements Listener {

	private static final ItemBuilder ROD_WOOD = new ItemBuilder(CustomMaterial.FISHING_ROD_WOOD)
			.name("Wood Fishing Rod");

	private static final ItemBuilder ROD_REINFORCED = new ItemBuilder(CustomMaterial.FISHING_ROD_REINFORCED)
			.name("Reinforced Fishing Rod")
			.enchant(Enchant.UNBREAKING, 1);

	private static final ItemBuilder ROD_GOLDEN = new ItemBuilder(CustomMaterial.FISHING_ROD_GOLDEN)
			.name("Golden Fishing Rod")
			.unbreakable();

	public Fishing() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onFishBite(PlayerEventFishingBiteEvent event) {
		Player player = event.getPlayer();

		if (!Pugmas24.get().shouldHandle(player))
			return;

		PlayerUtils.send(player, "Loot:");
		for (ItemStack itemStack : event.getLoot()) {
			new JsonBuilder(" - " + itemStack.getType()).hover(itemStack).send(player);
		}
	}

}
