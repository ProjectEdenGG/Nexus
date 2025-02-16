package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

// TODO Recipe: Sugar + Dye + ?
public class Pugmas21CandyCaneCannon implements Listener {

	public Pugmas21CandyCaneCannon() {
		Nexus.registerListener(this);
	}

	public static boolean isCannon(ItemStack item) {
		return ItemModelType.of(item) == getItemModel();
	}

	@NotNull
	private static ItemModelType getItemModel() {
		return ItemModelType.PUGMAS21_CANDY_CANE_CANNON;
	}

	public static ItemBuilder getItem() {
		return new ItemBuilder(getItemModel()).name("&cCandy Cane Cannon");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Player player = event.getPlayer();
		final ItemStack item = player.getInventory().getItem(event.getHand());

		if (!isCannon(item))
			return;

		final ItemStack candyCaneItem = findCandyCane(player);
		if (candyCaneItem == null) {
			if (new CooldownService().check(player, "candycanecannonammo", TickTime.SECOND.x(3)))
				PlayerUtils.send(player, "&cYou are out of candy cane ammo!");

			event.setCancelled(true);
			return;
		}

		final Snowball snowball = player.launchProjectile(Snowball.class);
		snowball.setItem(candyCaneItem);
		snowball.setSilent(true);
		new SoundBuilder(Sound.ENTITY_SNOWBALL_THROW).location(player).pitch(2).play();

		ItemUtils.subtract(player, candyCaneItem);
	}

	@Getter
	@AllArgsConstructor
	private enum CandyCane {
		RED(ItemModelType.PUGMAS21_CANDY_CANE_RED),
		GREEN(ItemModelType.PUGMAS21_CANDY_CANE_GREEN),
		YELLOW(ItemModelType.PUGMAS21_CANDY_CANE_YELLOW),
		;

		private final ItemModelType itemModelType;

		private ItemStack item() {
			return new ItemBuilder(itemModelType).build();
		}

		public static CandyCane of(ItemStack item) {
			if (Nullables.isNullOrAir(item))
				return null;

			for (CandyCane candyCane : values())
				if (ItemModelType.of(item) == candyCane.getItemModelType())
					return candyCane;

			return null;
		}
	}

	private ItemStack findCandyCane(Player player) {
		ItemStack item = player.getInventory().getItemInOffHand();
		final CandyCane offHand = CandyCane.of(item);
		if (offHand != null)
			return item;

		Set<ItemStack> candyCanes = new HashSet<>();
		for (ItemStack _item : player.getInventory().getContents()) {
			final CandyCane candyCane = CandyCane.of(_item);
			if (candyCane != null)
				candyCanes.add(_item);
		}

		return RandomUtils.randomElement(candyCanes);
	}

}
