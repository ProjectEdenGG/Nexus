package me.pugabyte.bncore.features.holidays.testing;

import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ExampleHunt extends SkullHuntEvent {
	World world = Bukkit.getWorld("world");
	private final List<Location> skullLocs = Arrays.asList(
			new Location(world, 1, 100, 1),
			new Location(world, 2, 100, 1),
			new Location(world, 3, 100, 1));

	@Override
	public void setSettingType(@Nonnull String settingType) {
		super.setSettingType("ExampleSettingType");
	}

	@Override
	public void setSkullUUIDs(@Nonnull List<String> skullUUIDs) {
		super.setSkullUUIDs(Arrays.asList("UUID_1", "UUID_2"));
	}

	@Override
	public void setTotalHeads(@Nonnull Integer totalHeads) {
		super.setTotalHeads(3);
	}

	@Override
	public void setSkullLocs(List<Location> skullLocs) {
		super.setSkullLocs(this.skullLocs);
	}

	@Override
	public void setActiveRegions(List<String> activeRegions) {
		super.setActiveRegions(Arrays.asList("test_region1", "test_region2"));
	}

	@Override
	public void setRandomSinglePrizes(boolean randomSinglePrizes) {
		super.setRandomSinglePrizes(true);
	}

	@Override
	public void setSinglePrizes(List<ItemBuilder> singlePrizes) {
		super.setSinglePrizes(Arrays.asList(
				new ItemBuilder(Material.DIRT).amount(25),
				new ItemBuilder(Material.STONE).amount(5)));
	}

	@Override
	public void setRandomOverallPrizes(boolean randomOverallPrizes) {
		super.setRandomOverallPrizes(false);
	}

	@Override
	public void setOverallPrizes(List<ItemBuilder> overallPrizes) {
		super.setOverallPrizes(Arrays.asList(
				new ItemBuilder(Material.DIAMOND).amount(16),
				new ItemBuilder(Material.GOLD_INGOT).amount(8)));
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
