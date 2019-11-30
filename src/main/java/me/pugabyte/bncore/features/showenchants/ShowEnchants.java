package me.pugabyte.bncore.features.showenchants;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ShowEnchants {
	private final static String[] ROMAN_NUMERALS = {"0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
	private final static int[] ENCHANTMENT_IDS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 22, 32, 33, 34, 35, 48, 49, 50, 51, 61, 62, 65, 66, 67, 68, 70, 71};
	private final static String[] ENCHANTMENT_NAMES = {"Protection", "Fire Protection", "Feather Falling", "Blast Protection",
			"Projectile Protection", "Respiration", "Aqua Affinity", "Thorns", "Depth Strider", "Frost Walker",
			"Curse of Binding", "Sharpness", "Smite", "Bane of Arthropods", "Knockback", "Fire Aspect", "Looting",
			"Sweeping Edge", "Efficiency", "Silk Touch", "Unbreaking", "Fortune", "Power", "Punch", "Flame", "Infinity",
			"Luck of the Sea", "Lure", "Loyalty", "Impaling", "Riptide", "Channeling", "Mending", "Curse Of Vanishing"};
	static Map<Player, LocalDateTime> coolDownMap = new HashMap<>();

	public ShowEnchants() {
		new ShowEnchantsCommand();
	}

	static String getPrettyName(String item) {
		String out = "";
		if (item.contains("_")) {
			String[] parts = item.split("_");
			for (String part : parts) {
				String temp = part.substring(0, 1).toUpperCase();
				String temp2 = part.substring(1).toLowerCase();
				out += temp + temp2 + " ";
			}
		} else {
			out = item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
		}
		return out.trim();
	}

	static String getRealName(String item) {
		String temp = item.toLowerCase();
		if (temp.contains("spade")) temp = temp.replaceAll("spade", "shovel");
		if (temp.contains("gold")) temp = temp.replaceAll("gold", "golden");
		if (temp.contains("skull")) temp = "Skull";
		// add more if needed.
		return temp;
	}

	static String getEnchantNameAndLevel(int id, int lvl) {
		String level = null;
		String enchant = null;
		// Get level as roman numeral (1-10)
		for (int i = 0; i < ROMAN_NUMERALS.length; i++) {
			if (i == lvl) {
				level = ROMAN_NUMERALS[i];
				break;
			}
		}
		// If lvl is > 10, use the number itself
		if (level == null) level = String.valueOf(lvl);

		// Using the index of the enchant ID, get the enchantment name
		for (int i = 0; i < ENCHANTMENT_IDS.length; i++) {
			if (ENCHANTMENT_IDS[i] == id) {
				enchant = ENCHANTMENT_NAMES[i];
			}
		}
		return enchant + " " + level;
	}
}
