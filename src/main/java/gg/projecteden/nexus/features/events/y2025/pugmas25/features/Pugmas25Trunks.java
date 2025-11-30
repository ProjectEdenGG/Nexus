package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Trunk;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Pugmas25Trunks implements Listener {

	public Pugmas25Trunks() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onOpenTrunk(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (!Pugmas25.get().isAtEvent(player))
			return;

		ItemStack item = event.getItem();
		if (isNullOrAir(item))
			return;

		var itemModelType = ItemModelType.of(item);
		var trunk = Pugmas25Trunk.of(itemModelType);
		if (trunk == null)
			return;

		var randomItem = RandomUtils.getWeightedRandom(trunk.getItems());

		item.subtract();
		PlayerUtils.giveItem(player, randomItem);
		new SoundBuilder(Sound.BLOCK_CHEST_LOCKED)
			.receiver(player)
			.location(event.getPlayer().getLocation())
			.category(SoundCategory.UI)
			.play();
	}

}
