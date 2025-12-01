package gg.projecteden.nexus.utils;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffectType;

// https://minecraft.wiki/w/Effect_colors/Java_Edition_potion_color_changes_in_1.19.4

@Getter
public enum PotionEffectColor {
	SPEED(PotionEffectType.SPEED, "#33EBFF"),
	SLOWNESS(PotionEffectType.SLOWNESS, "#8BAFE0"),
	HASTE(PotionEffectType.HASTE, "#D9C043"),
	MINING_FATIGUE(PotionEffectType.MINING_FATIGUE, "#4A4217"),
	STRENGTH(PotionEffectType.STRENGTH, "#FFC700"),
	INSTANT_HEALTH(PotionEffectType.INSTANT_HEALTH, "#F82423"),
	INSTANT_DAMAGE(PotionEffectType.INSTANT_DAMAGE, "#A9656A"),
	JUMP_BOOST(PotionEffectType.JUMP_BOOST, "#FDFF84"),
	NAUSEA(PotionEffectType.NAUSEA, "#551D4A"),
	REGENERATION(PotionEffectType.REGENERATION, "#CD5CAB"),
	RESISTANCE(PotionEffectType.RESISTANCE, "#9146F0"),
	FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE, "#FF9900"),
	WATER_BREATHING(PotionEffectType.WATER_BREATHING, "#98DAC0"),
	INVISIBILITY(PotionEffectType.INVISIBILITY, "#F6F6F6"),
	BLINDNESS(PotionEffectType.BLINDNESS, "#1F1F23"),
	NIGHT_VISION(PotionEffectType.NIGHT_VISION, "#C2FF66"),
	HUNGER(PotionEffectType.HUNGER, "#587653"),
	WEAKNESS(PotionEffectType.WEAKNESS, "#484D48"),
	POISON(PotionEffectType.POISON, "#87A363"),
	WITHER(PotionEffectType.WITHER, "#736156"),
	HEALTH_BOOST(PotionEffectType.HEALTH_BOOST, "#F87D23"),
	ABSORPTION(PotionEffectType.ABSORPTION, "#2552A5"),
	SATURATION(PotionEffectType.SATURATION, "#F82423"),
	GLOWING(PotionEffectType.GLOWING, "#94A061"),
	LEVITATION(PotionEffectType.LEVITATION, "#CEFFFF"),
	LUCK(PotionEffectType.LUCK, "#59C106"),
	UNLUCK(PotionEffectType.UNLUCK, "#C0A44D"),
	SLOW_FALLING(PotionEffectType.SLOW_FALLING, "#F3CFB9"),
	CONDUIT_POWER(PotionEffectType.CONDUIT_POWER, "#1DC2D1"),
	DOLPHINS_GRACE(PotionEffectType.DOLPHINS_GRACE, "#88A3BE"),
	BAD_OMEN(PotionEffectType.BAD_OMEN, "#0B6138"),
	HERO_OF_THE_VILLAGE(PotionEffectType.HERO_OF_THE_VILLAGE, "#44FF44"),
	DARKNESS(PotionEffectType.DARKNESS, "#292721");

	private final PotionEffectType type;
	private final String hex;

	PotionEffectColor(PotionEffectType type, String hex) {
		this.type = type;
		this.hex = hex;
	}

	public static PotionEffectColor from(PotionEffectType type) {
		for (PotionEffectColor c : values())
			if (c.type.equals(type)) return c;
		return null;
	}

	private static Color hexToBukkit(String hex) {
		hex = hex.replace("#", "");
		int r = Integer.parseInt(hex.substring(0, 2), 16);
		int g = Integer.parseInt(hex.substring(2, 4), 16);
		int b = Integer.parseInt(hex.substring(4, 6), 16);
		return Color.fromRGB(r, g, b);
	}

	public Color toBukkit() {
		return hexToBukkit(hex);
	}
}
