package me.pugabyte.bncore.skript;

import org.bukkit.entity.Player;

public class SkriptFunctions {
	public static void koda(String message, String targets) {
		koda(message, targets, false);
	}

	public static void koda(String message, String targets, boolean noDelay) {
		String function = "koda";
		if (noDelay) function += "NoDelay";
		Object[][] parameters = new Object[2][];
		parameters[0] = new String[]{message};
		parameters[1] = new String[]{targets};
		FunctionUtils.executeFunction(function, parameters);
	}

	public static void log(String message) {
		Object[][] parameters = new Object[1][];
		parameters[0] = new String[]{message};
		FunctionUtils.executeFunction("log", parameters);
	}

	public static void showEnchantsOnBridge(Player player, String message, String name, String enchants, String durability, String channel) {
		Object[][] parameters = new Object[6][];
		parameters[0] = new Player[]{player};
		parameters[1] = new String[]{message};
		parameters[2] = new String[]{name};
		parameters[3] = new String[]{enchants};
		parameters[4] = new String[]{durability};
		parameters[5] = new String[]{channel};
		FunctionUtils.executeFunction("showEnchants", parameters);
	}

	public static String getFullChatFormat(Player player) {
		String function = "getFullChatFormat";
		Object[][] parameters = new Object[1][];
		parameters[0] = new String[]{player.getUniqueId().toString()};
		return FunctionUtils.executeFunction(function, parameters)[0].toString();
	}

	public static void redTint(Player player, double fadeTime, double intensity) {
		Object[][] parameters = new Object[3][];
		parameters[0] = new Player[]{player};
		parameters[1] = new Double[]{fadeTime};
		parameters[2] = new Double[]{intensity};
		FunctionUtils.executeFunction("redTint", parameters);
	}
}
