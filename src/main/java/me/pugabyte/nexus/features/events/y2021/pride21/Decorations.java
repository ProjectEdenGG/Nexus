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
	RED("40533", -1659, 9, -1468),
	ORANGE("40534", -1742, 6, -1596),
	LIME("40537", -1699, 14, -1523),
	TEAL("40532", -1581, 4, -1535),
	BLUE("40536", -1691, 7, -1385),
	MAGENTA("40535", -1706, 9, -1387);

	@Getter
	private final String headID;
	private final int x;
	private final int y;
	private final int z;

	public @NotNull ItemStack getHead() {
		return Nexus.getHeadAPI().getItemHead(headID);
	}

	public @NotNull Location getLocation() {
		return new Location(Bukkit.getWorld("buildadmin"), x, y, z);
	}


	public static @Nullable Decorations getByLocation(Location location) {
		return Arrays.stream(Decorations.values()).parallel().filter(decorations -> LocationUtils.blockLocationsEqual(location, decorations.getLocation())).findAny().orElse(null);
	}

	public static @Nullable Decorations getByLocation(Block block) {
		return getByLocation(block.getLocation());
	}
}
