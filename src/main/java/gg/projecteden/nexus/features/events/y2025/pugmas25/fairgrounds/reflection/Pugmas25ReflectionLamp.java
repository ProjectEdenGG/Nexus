package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.reflection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public enum Pugmas25ReflectionLamp {
	// @formatter:off
	KELP(Material.DRIED_KELP_BLOCK, 		8, 8, 1, 9, ChatColor.of("#314a18")),
	MAGMA(Material.MAGMA_BLOCK, 			8, 5, 5, 9, ChatColor.of("#b04609")),
	LODESTONE(Material.LODESTONE, 			8, 2, 3, 9, ChatColor.of("#aaaaaa")),
	PURPUR(Material.PURPUR_BLOCK, 			8, -1, 5, 9, ChatColor.of("#9c529c")),
	SOUL_SOIL(Material.SOUL_SOIL, 			8, -4, 3, 9, ChatColor.of("#593a1b")),

	BEDROCK(Material.BEDROCK, 				6, 10, 4, 10, ChatColor.of("#828282")),
	PUMPKIN(Material.PUMPKIN, 				3, 10, 2, 8, ChatColor.of("#b36d17")),
	MELON(Material.MELON, 					-3, 10, 2, 8, ChatColor.of("#84bd21")),
	NETHERITE(Material.NETHERITE_BLOCK, 	-6, 10, 4, 10, ChatColor.of("#696969")),

	EMERALD(Material.EMERALD_ORE, 			-8, 8, 1, 9, ChatColor.of("#09af47")),
	PRISMARINE(Material.DARK_PRISMARINE, 	-8, 5, 5, 9, ChatColor.of("#257a5c")),
	HONEYCOMB(Material.HONEYCOMB_BLOCK, 	-8, 2, 3, 9, ChatColor.of("#e29f3b")),
	ICE(Material.BLUE_ICE, 					-8, -1, 5, 9, ChatColor.of("#68bae8")),
	SPONGE(Material.SPONGE, 				-8, -4, 3, 9, ChatColor.of("#c3c63d")),
	;
	// @formatter:on

	@Getter
	private final Material type;
	private final int xDiff;
	private final int zDiff;
	@Getter
	private final int min;
	@Getter
	private final int max;
	@Getter
	private final ChatColor chatColor;

	public Location getLocation() {
		return Pugmas25Reflection.getCenter().clone().add(xDiff, 3, zDiff);
	}

	public static @Nullable Pugmas25ReflectionLamp from(Material material) {
		for (Pugmas25ReflectionLamp lamp : values()) {
			if (lamp.getType().equals(material))
				return lamp;
		}

		return null;
	}
}
