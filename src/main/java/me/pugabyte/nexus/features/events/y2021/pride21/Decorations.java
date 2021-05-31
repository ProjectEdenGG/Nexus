package me.pugabyte.nexus.features.events.y2021.pride21;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;

@RequiredArgsConstructor
public enum Decorations {
	RED("40533", -649, 76, 185),
	ORANGE("40534", -732, 73, 57),
	LIME("40537", -689, 81, 130),
	TEAL("40532", -571, 71, 118),
	BLUE("40536", -681, 74, 268),
	MAGENTA("40535", -696, 76, 266);

	@Getter
	private final String headID;
	private final int x;
	private final int y;
	private final int z;

	public @NotNull ItemStack getHead() {
		return Nexus.getHeadAPI().getItemHead(headID);
	}

	public @NotNull Location getLocation() {
		return new Location(Bukkit.getWorld("events"), x, y, z);
	}


	public static @Nullable Decorations getByLocation(Location location) {
		return Arrays.stream(Decorations.values()).parallel().filter(decorations -> LocationUtils.blockLocationsEqual(location, decorations.getLocation())).findAny().orElse(null);
	}

	public static @Nullable Decorations getByLocation(Block block) {
		return getByLocation(block.getLocation());
	}
}
