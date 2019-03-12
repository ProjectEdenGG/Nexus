package me.pugabyte.bncore.features.showenchants;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ShowEnchants {
	static Map<Player, LocalDateTime> coolDownMap = new HashMap<>();

	public ShowEnchants() {
		new ShowEnchantsCommand();
	}

	public static String getPrettyName(String item) {
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

	static String getEnchantNameAndLevel(String enchantment, int lvl) {
		String level = intToRoman(lvl);
		String enchant = getPrettyName(enchantment);
		return enchant + " " + level;
	}

	static String intToRoman(int num) {
		String[] str = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
		int[] val = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			while (num >= val[i]) {
				num -= val[i];
				sb.append(str[i]);
			}
		}
		return sb.toString();
	}
}
