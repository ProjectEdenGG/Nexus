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

	public static void say(String message, String targets) {
		say(message, targets, false);
	}

	public static void say(String message, String targets, boolean noDelay) {
		String function = "say";
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

	public static void adminLog(String message) {
		Object[][] parameters = new Object[1][];
		parameters[0] = new String[]{message};
		FunctionUtils.executeFunction("adminLog", parameters);
	}

	public static void sendBridgeMessage(Player player, String message, String channel) {
		Object[][] parameters = new Object[3][];
		parameters[0] = new Player[]{player};
		parameters[1] = new String[]{message};
		parameters[2] = new String[]{channel};
		FunctionUtils.executeFunction("sendBridgeMessage", parameters);
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

	public static void debug(String service, String message) {
		Object[][] parameters = new Object[2][];
		parameters[0] = new String[]{service};
		parameters[1] = new String[]{message};
		FunctionUtils.executeFunction("debug", parameters);
	}

	public static String getFullChatFormat(Player player) {
		String function = "getFullChatFormat";
		Object[][] parameters = new Object[1][];
		parameters[0] = new String[]{player.getUniqueId().toString()};
		return FunctionUtils.executeFunction(function, parameters)[0].toString();
	}

	public static String getHighestRank(Player player) {
		Object[][] parameters = new Object[1][];
		parameters[0] = new String[]{player.getUniqueId().toString()};
		return FunctionUtils.executeFunction("getHighestRank", parameters)[0].toString();
	}

	public static String getPrefix(Player player) {
		Object[][] parameters = new Object[1][];
		parameters[0] = new String[]{player.getUniqueId().toString()};
		return FunctionUtils.executeFunction("getPrefix", parameters)[0].toString();
	}

	public static String getSuffix(Player player) {
		Object[][] parameters = new Object[1][];
		parameters[0] = new String[]{player.getUniqueId().toString()};
		return FunctionUtils.executeFunction("getSuffix", parameters)[0].toString();
	}

	public static void redTint(Player player, double fadeTime, double intensity) {
		Object[][] parameters = new Object[3][];
		parameters[0] = new Player[]{player};
		parameters[1] = new Double[]{fadeTime};
		parameters[2] = new Double[]{intensity};
		FunctionUtils.executeFunction("redTint", parameters);
	}
}
