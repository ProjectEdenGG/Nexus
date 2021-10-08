package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

// TODO Recipe: Sugar + Dye + ?
public class CandyCaneCannon implements Listener {

	public CandyCaneCannon() {
		Nexus.registerListener(this);
	}

	public static boolean isCannon(ItemStack item) {
		return !isNullOrAir(item) && item.getType() == Material.STICK && CustomModelData.of(item) == 49;
	}

	public static ItemBuilder getItem() {
		return new ItemBuilder(Material.STICK).customModelData(49).name("&cCandy Cane Cannon");
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

		final CandyCane candyCane = findCandyCane(player);
		if (candyCane == null) {
			if (new CooldownService().check(player, "candycanecannonammo", TickTime.SECOND.x(3)))
				PlayerUtils.send(player, "&cYou are out of candy cane ammo!");

			event.setCancelled(true);
			return;
		}

		final Snowball snowball = player.launchProjectile(Snowball.class);
		snowball.setItem(candyCane.item());
		snowball.setSilent(true);
		new SoundBuilder(Sound.ENTITY_SNOWBALL_THROW).location(player).pitch(2).play();

		if (!GameModeWrapper.of(player).isCreative())
			PlayerUtils.removeItem(player, candyCane.item());
	}

	private enum CandyCane {
		RED,
		GREEN,
		YELLOW,
		;

		private int customModelData() {
			return ordinal() + 100;
		}

		private ItemStack item() {
			return new ItemBuilder(Material.COOKIE).customModelData(customModelData()).build();
		}

		public static CandyCane of(ItemStack item) {
			if (isNullOrAir(item))
				return null;

			if (item.getType() != Material.COOKIE)
				return null;

			return of(CustomModelData.of(item));
		}

		public static CandyCane of(int customModelData) {
			for (CandyCane candyCane : values())
				if (candyCane.customModelData() == customModelData)
					return candyCane;

			return null;
		}
	}

	private CandyCane findCandyCane(Player player) {
		final CandyCane offHand = CandyCane.of(player.getInventory().getItemInOffHand());
		if (offHand != null)
			return offHand;

		for (ItemStack item : player.getInventory().getContents()) {
			final CandyCane candyCane = CandyCane.of(item);
			if (candyCane != null)
				return candyCane;
		}

		return null;
	}

}
