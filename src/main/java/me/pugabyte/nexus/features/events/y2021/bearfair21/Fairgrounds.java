package me.pugabyte.nexus.features.events.y2021.bearfair21;

import eden.utils.StringUtils;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Archery;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Frogger;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Interactables;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Rides;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Seeker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.reflection.ReflectionGame;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.TimeUtils.Timer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static me.pugabyte.nexus.features.events.y2020.bearfair20.quests.BFQuests.itemLore;

public class Fairgrounds {

	public Fairgrounds() {
		new Timer("      Interactables", Interactables::new);
		new Timer("      Rides", Rides::new);
		new Timer("      Minigolf", MiniGolf::new);
		new Timer("      Archery", Archery::new);
		new Timer("      Frogger", Frogger::new);
		new Timer("      Seeker", Seeker::new);
		new Timer("      Reflection", ReflectionGame::new);
	}

	public enum BearFair21Kit {
		ARCHERY(
				new ItemBuilder(Material.BOW)
						.enchant(Enchantment.ARROW_INFINITE)
						.unbreakable()
						.build(),
				new ItemBuilder(Material.ARROW)
						.build()
		),
		MINECART(
				new ItemBuilder(Material.MINECART)
						.lore(itemLore)
						.build()
		),
		;

		List<ItemStack> items;

		BearFair21Kit(ItemStack... items) {
			this.items = Arrays.asList(items);
		}

		public ItemStack getItem() {
			return getItems().get(0);
		}

		public List<ItemStack> getItems() {
			return items;
		}

		public void giveItems(Player player) {
			if (!PlayerUtils.hasRoomFor(player, this.getItems())) {
				BearFair21.send("&cCouldn't give " + StringUtils.camelCase(this) + " kit", player);
				return;
			}

			PlayerUtils.giveItems(player, getItems());
		}

	}
}
