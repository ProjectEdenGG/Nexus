package me.pugabyte.bncore.features.holidays.testing;

import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class ExampleHunt extends SkullHuntEvent {

	public ExampleHunt() {
		this.settingType = "ExampleSettingType";
		this.skullUuids = Arrays.asList(
				"UUID_1",
				"UUID_2");

		World world = Bukkit.getWorld("world");
		this.activeWorlds = Collections.singletonList(world);
		this.skullLocations = Arrays.asList(
				new Location(world, 1, 100, 1),
				new Location(world, 2, 100, 1),
				new Location(world, 3, 100, 1));

		this.activeRegions = Arrays.asList(
				new WorldGuardUtils(world).getProtectedRegion("test_region1"),
				new WorldGuardUtils(world).getProtectedRegion("test_region1"));

		this.randomSinglePrizes = true;
		this.singlePrizes = Arrays.asList(
				new ItemBuilder(Material.DIRT).amount(25),
				new ItemBuilder(Material.STONE).amount(5));

		this.randomOverallPrizes = false;
		this.overallPrizes = Arrays.asList(
				new ItemBuilder(Material.DIAMOND).amount(16),
				new ItemBuilder(Material.GOLD_INGOT).amount(8));
	}

	@Override
	public void giveSinglePrize(Player player) {
		super.giveSinglePrize(player);

		// Also...
		Utils.runCommandAsConsole("eco give " + player.getName() + " 5000");
	}

	@Override
	public void giveOverallPrize(Player player) {
		// Instead...
		Utils.runCommandAsConsole("eco give " + player.getName() + " 5000");
	}

}
