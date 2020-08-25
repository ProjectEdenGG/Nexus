package me.pugabyte.bncore.features.holidays.aeveonproject;

import java.util.Arrays;
import java.util.List;

public class Regions {

	// Lobby
	public static String lobby = "lobby";
	public static String lobby_shipColor = lobby + "_shipcolor";
	public static String lobby_shipColor_update = lobby_shipColor + "_update";

	// Sialia
	public static String sialia = "sialia";
	public static String sialia_shipColor = sialia + "_shipcolor";
	public static String sialia_shipColor_update = sialia_shipColor + "_update";

	// Sialia Crashing
	public static String sialiaCrashing = "sialia_crashing";
	public static String sialiaCrashing_shipColor = sialiaCrashing + "_shipcolor";
	public static String sialiaCrashing_shipColor_update = sialiaCrashing_shipColor + "_update";

	// Groups
	public static List<String> group_shipColor_Update = Arrays.asList(lobby_shipColor_update, sialia_shipColor_update, sialiaCrashing_shipColor_update);


	public Regions() {

	}

	public static String getShipColorRegion(String updateRg) {
		return updateRg.replaceAll("_update", "");
	}
}
