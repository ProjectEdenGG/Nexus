package me.pugabyte.bncore.features.homes;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.homes.providers.EditHomeProvider;
import me.pugabyte.bncore.features.homes.providers.EditHomesProvider;
import me.pugabyte.bncore.features.menus.SignMenuFactory;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;

public class HomesMenu {
	private static SignMenuFactory signMenuFactory = BNCore.getInstance().getSignMenuFactory();

	public static void edit(HomeOwner homeOwner) {
		SmartInventory.builder()
				.provider(new EditHomesProvider(homeOwner))
				.size((int) Math.min(6, Math.ceil(homeOwner.getHomes().size() / 9) + 3), 9)
				.title(Utils.colorize("&3Home Editor"))
				.build()
				.open(homeOwner.getPlayer());
	}

	public static void edit(Home home) {
		SmartInventory.builder()
				.provider(new EditHomeProvider(home.getOwner()))
				.size(5, 9)
				.title(Utils.colorize(Utils.camelCase(home.getName())))
				.build()
				.open(home.getOwner().getPlayer());

	}

	private static String[] playerNameLines = {"", "^ ^ ^ ^ ^ ^", "Enter a", "player's name"};

	public static void allowAll(HomeOwner homeOwner) {
		signMenuFactory.lines(playerNameLines)
				.response((player, response) -> {
					homeOwner.allowAll(Bukkit.getPlayer(response[0]));
					new HomeService().save(homeOwner);
				})
				.open(homeOwner.getPlayer());

	}

	public static void removeAll(HomeOwner homeOwner) {
		signMenuFactory.lines(playerNameLines)
				.response((player, response) -> {
					homeOwner.removeAll(Bukkit.getPlayer(response[0]));
					new HomeService().save(homeOwner);
				})
				.open(homeOwner.getPlayer());
	}

	public static void allow(Home home) {
		signMenuFactory.lines(playerNameLines)
				.response((player, response) -> {
					home.allow(Bukkit.getPlayer(response[0]));
					new HomeService().save(home.getOwner());
				})
				.open(home.getOwner().getPlayer());
	}

	public static void remove(Home home) {
		signMenuFactory.lines(playerNameLines)
				.response((player, response) -> {
					home.remove(Bukkit.getPlayer(response[0]));
					new HomeService().save(home.getOwner());
				})
				.open(home.getOwner().getPlayer());
	}

	public static void item(Home home) {
		signMenuFactory.lines("", "Enter a player's", "name, \"hand\"", "or an item name")
				.response((player, response) -> {
					// TODO Item parsing
					new HomeService().save(home.getOwner());
				})
				.open(home.getOwner().getPlayer());
	}

	public static void rename(Home home) {
		signMenuFactory.lines("", "^ ^ ^ ^ ^ ^", "Enter the home's", "new name")
				.response((player, response) -> {
					home.setName(response[0]);
					new HomeService().save(home.getOwner());
				})
				.open(home.getOwner().getPlayer());
	}

}
