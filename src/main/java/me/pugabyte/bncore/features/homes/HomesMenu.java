package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.SignMenuFactory;
import me.pugabyte.bncore.models.homes.Home;
import org.bukkit.entity.Player;

public class HomesMenu {
	private static SignMenuFactory signMenuFactory = BNCore.getInstance().getSignMenuFactory();

	public static void edit(Player player) {

	}

	public static void edit(Player player, Home home) {

	}

	public static void allow(Player target) {
		signMenuFactory
				.create("", "^ ^ ^ ^ ^ ^", "Enter a", "player's name")
				.response((player, lines) -> {

				})
				.open(target);
	}

	public static void remove(Player player) {

	}

	public static void allowAll(Player player) {

	}

	public static void removeAll(Player player) {

	}

	public static void item(Player player) {

	}

}
