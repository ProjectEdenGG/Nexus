package me.pugabyte.nexus.features.wither;

import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wither {

	public static boolean active = false;
	public static List<UUID> activePlayers = new ArrayList<>();
	public static Difficulty difficulty = Difficulty.EASY;

	public static void setDifficulty(Difficulty difficulty) {
		Wither.difficulty = difficulty;
	}

	public static void reset() {
		active = false;
		//activePlayers.clear();
		difficulty = Difficulty.EASY;
		for (Location location : BeginningCutscene.LIGHTNING_LOCATIONS)
			location.getBlock().setType(Material.AIR);
	}

	public enum Difficulty {
		EASY("&a"),
		MEDIUM("&6"),
		HARD("&c");

		String color;

		Difficulty(String color) {
			this.color = color;
		}

		public String getTitle() {
			return StringUtils.colorize(color + "&l" + StringUtils.camelCase(name()));
		}
	}

}
