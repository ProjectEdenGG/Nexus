package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs;

import lombok.Getter;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.MerchantBuilder.TradeBuilder;
import me.pugabyte.nexus.utils.RandomUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Collector {
	@Getter
	public static List<TradeBuilder> randomTrades = new ArrayList<>();
	//
	private static List<TradeBuilder> possibleTrades = new ArrayList<>();
	private static List<Location> locations = new ArrayList<>();
	private static final List<Location> prevLocations = new ArrayList<>();
	private static final World world = BearFair21.getWorld();

	public static void startup() {
		loadLocations();
		loadTrades();
	}

	private static void loadLocations() {
		Location observatory = new Location(world, -1097, 157, -1550);
		Location town = new Location(world, -1095, 139, -1666);
		Location forest = new Location(world, -1031, 140, -1556);
		Location flag = new Location(world, -984, 144, -1615);
		Location campsite = new Location(world, -1020, 153, -1760);
		Location ruins = new Location(world, -919, 137, -1711);
		Location carnival = new Location(world, -888, 136, -1659);
		locations = Arrays.asList(observatory, town, forest, flag, campsite, ruins, carnival);
	}

	private static void loadTrades() {
		possibleTrades = new ArrayList<>() {{
			// Fishing Loot
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(FishingLoot.TIGER_TROUT.getItem()));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(FishingLoot.GLACIERFISH.getItem()));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(FishingLoot.CRIMSONFISH.getItem()));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(FishingLoot.BLOBFISH.getItem()));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(FishingLoot.STONEFISH.getItem()));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(FishingLoot.MIDNIGHT_CARP.getItem()));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(FishingLoot.SUNFISH.getItem()));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(FishingLoot.NAUTILUS_SHELL.getItem()));
			// Food
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.CAKE)));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.PUMPKIN_PIE)));
			// Items
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.BONE_BLOCK)));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.LEATHER_HORSE_ARMOR)));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.ANVIL)));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.CAMPFIRE)));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.CROSSBOW)));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.LECTERN)));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.BOOKSHELF)));
			add(new TradeBuilder()
					.result(Merchants.goldNugget.clone().amount(1))
					.ingredient(new ItemStack(Material.BOOK)));

		}};
	}

	public static void move() {
		prevLocations.clear();

		Location newLoc = RandomUtils.randomElement(locations);
		if (newLoc == null) return;

		for (int i = 0; i < 10; i++) {
			if (!prevLocations.contains(newLoc)) {
				prevLocations.add(newLoc);
				break;
			}
			newLoc = RandomUtils.randomElement(locations);
		}

		if (newLoc == null) return;
		newLoc = LocationUtils.getCenteredLocation(newLoc);
		Location finalNewLoc = newLoc;

		NPC npc = BearFair21NPC.COLLECTOR.getNPC();
		Location oldLoc = npc.getEntity().getLocation();

		world.playSound(oldLoc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1F);
		world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, oldLoc, 500, 0.5, 1, 0.5, 0);
		world.spawnParticle(Particle.FLASH, oldLoc, 10, 0, 0, 0);
		npc.despawn();

		newTrades();

		world.playSound(finalNewLoc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1F);
		world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, finalNewLoc, 500, 0.5, 1, 0.5, 0);
		world.spawnParticle(Particle.FLASH, finalNewLoc, 10, 0, 0, 0);
		npc.spawn(finalNewLoc);
	}

	private static void newTrades() {
		List<TradeBuilder> choices = new ArrayList<>(possibleTrades);
		// Remove current trades from choices
		for (TradeBuilder trade : randomTrades)
			choices.remove(trade);
		randomTrades.clear();
		// Pick new trades
		for (int i = 0; i < 4; i++) {
			TradeBuilder random = RandomUtils.randomElement(choices);
			choices.remove(random);
			randomTrades.add(random);
		}
	}
}
