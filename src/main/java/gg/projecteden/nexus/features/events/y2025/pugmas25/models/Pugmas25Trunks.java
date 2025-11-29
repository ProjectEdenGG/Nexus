package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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

	@Getter
	@AllArgsConstructor
	public enum Pugmas25Trunk {
		DIAMOND(ItemModelType.EVENT_TRUNK_DIAMOND, Map.of(
			new ItemBuilder(Material.DIAMOND).build(), 5d,
			new ItemBuilder(Material.EMERALD).build(), 5d,
			new ItemBuilder(Material.NETHERITE_INGOT).build(), 5d
		)),
		GOLD(ItemModelType.EVENT_TRUNK_GOLDEN, Map.of(
			new ItemBuilder(Material.GOLD_INGOT).build(), 5d,
			new ItemBuilder(Material.LAPIS_LAZULI).build(), 5d,
			new ItemBuilder(Material.COPPER_ORE).build(), 5d
		)),
		IRON(ItemModelType.EVENT_TRUNK_IRON, Map.of(
			new ItemBuilder(Material.IRON_INGOT).build(), 5d,
			new ItemBuilder(Material.COAL).build(), 5d,
			new ItemBuilder(Material.REDSTONE).build(), 5d
		)),
		;

		private final ItemModelType model;
		private final Map<ItemStack, Double> items;

		public static Pugmas25Trunk of(ItemModelType model) {
			for (Pugmas25Trunk trunk : values())
				if (trunk.getModel().equals(model))
					return trunk;
			return null;
		}
	}
}
