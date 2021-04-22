package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.reflection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

@AllArgsConstructor
public enum ReflectionGameLamp {
	SOUL_SOIL(Material.SOUL_SOIL, loc(57, -99), 3, 9, ChatColor.of("#452d15")),
	PURPUR(Material.PURPUR_BLOCK, loc(57, -96), 5, 9, ChatColor.of("#9c529c")),
	LODESTONE(Material.LODESTONE, loc(57, -93), 3, 9, ChatColor.of("#7a7a7a")),
	MAGMA(Material.MAGMA_BLOCK, loc(57, -90), 5, 9, ChatColor.of("#7a2f04")),
	KELP(Material.DRIED_KELP_BLOCK, loc(57, -87), 1, 9, ChatColor.of("#314a18")),
	BEDROCK(Material.BEDROCK, loc(55, -85), 4, 10, ChatColor.of("#454545")),
	PUMPKIN(Material.PUMPKIN, loc(52, -85), 2, 8, ChatColor.of("#b36d17")),
	MELON(Material.MELON, loc(46, -85), 2, 8, ChatColor.of("#84bd21")),
	NETHERITE(Material.NETHERITE_BLOCK, loc(43, -85), 4, 10, ChatColor.of("#393739")),
	EMERALD(Material.EMERALD_ORE, loc(41, -87), 1, 9, ChatColor.of("#09af47")),
	PRISMARINE(Material.DARK_PRISMARINE, loc(41, -90), 5, 9, ChatColor.of("#257a5c")),
	HONEYCOMB(Material.HONEYCOMB_BLOCK, loc(41, -93), 3, 9, ChatColor.of("#e2b53b")),
	ICE(Material.BLUE_ICE, loc(41, -96), 5, 9, ChatColor.of("#7eafcb")),
	SPONGE(Material.SPONGE, loc(41, -99), 3, 9, ChatColor.of("#c3c63d")),
	;

	@Getter
	private final Material type;
	@Getter
	private final Location location;
	@Getter
	private final int min;
	@Getter
	private final int max;
	@Getter
	private final ChatColor chatColor;

	private static Location loc(int x, int z) {
		return new Location(BearFair21.getWorld(), x, 135, z);
	}

	public static ReflectionGameLamp from(Material material) {
		for (ReflectionGameLamp lamp : values()) {
			if (lamp.getType().equals(material))
				return lamp;
		}

		return null;
	}
}
