package me.pugabyte.nexus.features.events.y2021.bearfair21;

import eden.utils.StringUtils;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Archery;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Frogger;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Interactables;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Seeker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.reflection.ReflectionGame;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Timer;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.nexus.features.events.y2020.bearfair20.quests.BFQuests.itemLore;

public class Fairgrounds {
	public static List<String> rides = Arrays.asList("carousel", "chairswing", "droptower", "enterprise",
			"ferriswheel", "jets", "pendulum", "swingship", "swingtower", "teacups");

	public Fairgrounds() {
		new Timer("      Interactables", Interactables::new);
		new Timer("      Minigolf", MiniGolf::new);
		new Timer("      Archery", Archery::new);
		new Timer("      Frogger", Frogger::new);
		new Timer("      Seeker", Seeker::new);
		new Timer("      Reflection", ReflectionGame::new);

		ridesTask();
	}

	private void ridesTask() {
		WorldGuardUtils WGUtils = BearFair21.getWGUtils();
		String rg = BearFair21.getRegion();

		// Drop Tower
		Map<String, Location> towerLights = new HashMap<>() {{
			put(rg + "_droptower_light_1", new Location(BearFair21.getWorld(), 147, 145, -37));
			put(rg + "_droptower_light_2", new Location(BearFair21.getWorld(), 147, 157, -37));
			put(rg + "_droptower_light_3", new Location(BearFair21.getWorld(), 147, 169, -37));
			put(rg + "_droptower_light_4", new Location(BearFair21.getWorld(), 147, 176, -37));
		}};

		List<Location> locations = new ArrayList<>();
		Tasks.repeat(Time.SECOND.x(5), Time.TICK.x(2), () -> {
			for (String light_region : towerLights.keySet()) {
				Location location = towerLights.get(light_region);
				if (WGUtils.getPlayersInRegion(light_region).size() > 0) {
					locations.add(location);
					location.getBlock().setType(Material.REDSTONE_BLOCK);
				} else if (locations.contains(location)) {
					locations.remove(location);
					location.getBlock().setType(Material.AIR);
				}
			}
		});

		//
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
