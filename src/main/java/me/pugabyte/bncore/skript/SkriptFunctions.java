package me.pugabyte.bncore.skript;

import org.bukkit.entity.Player;

public class SkriptFunctions {

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

	public static void redTint(Player player, double fadeTime, double intensity) {
		Object[][] parameters = new Object[3][];
		parameters[0] = new Player[]{player};
		parameters[1] = new Double[]{fadeTime};
		parameters[2] = new Double[]{intensity};
		FunctionUtils.executeFunction("redTint", parameters);
	}
}
