package me.pugabyte.bncore.features.homes;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.homes.providers.EditHomeProvider;
import me.pugabyte.bncore.features.homes.providers.EditHomesProvider;
import me.pugabyte.bncore.features.homes.providers.SetHomeProvider;
import me.pugabyte.bncore.features.menus.SignMenuFactory;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.pugabyte.bncore.utils.Utils.loreize;

public class HomesMenu {
	private static SignMenuFactory signMenuFactory = BNCore.getInstance().getSignMenuFactory();

	public static void edit(HomeOwner homeOwner) {
		edit(homeOwner, 0);
	}

	public static void edit(HomeOwner homeOwner, int page) {
		SmartInventory.builder()
				.provider(new EditHomesProvider(homeOwner))
				.size((int) Math.min(6, Math.ceil(Integer.valueOf(homeOwner.getHomes().size()).doubleValue() / 9) + 3), 9)
				.title(Utils.colorize("&3Home Editor"))
				.build()
				.open(homeOwner.getPlayer(), page);
	}

	public static void edit(Home home) {
		SmartInventory.builder()
				.provider(new EditHomeProvider(home))
				.size(5, 9)
				.title(Utils.colorize((home.isLocked() ? "&4" : "&2") + Utils.camelCase(home.getName())))
				.build()
				.open(home.getOwner().getPlayer());
	}

	public static void setHome(HomeOwner homeOwner) {
		SmartInventory.builder()
				.provider(new SetHomeProvider(homeOwner))
				.size(5, 9)
				.title(Utils.colorize("&3Set a new home"))
				.build()
				.open(homeOwner.getPlayer());
	}

	private static String[] playerNameLines = {"", "^ ^ ^ ^ ^ ^", "Enter a", "player's name"};

	public static void allowAll(HomeOwner homeOwner, BiConsumer<Player, String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.response((player, response) -> {
					try {
						homeOwner.allowAll(Utils.getPlayer(response[0]));
						new HomeService().save(homeOwner);
						onResponse.accept(player, response);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				})
				.open(homeOwner.getPlayer());

	}

	public static void removeAll(HomeOwner homeOwner, BiConsumer<Player, String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.response((player, response) -> {
					homeOwner.removeAll(Utils.getPlayer(response[0]));
					new HomeService().save(homeOwner);
					onResponse.accept(player, response);
				})
				.open(homeOwner.getPlayer());
	}

	public static void allow(Home home, BiConsumer<Player, String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.response((player, response) -> {
					home.allow(Utils.getPlayer(response[0]));
					new HomeService().save(home.getOwner());
					onResponse.accept(player, response);
				})
				.open(home.getOwner().getPlayer());
	}

	public static void remove(Home home, BiConsumer<Player, String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.response((player, response) -> {
					home.remove(Utils.getPlayer(response[0]));
					new HomeService().save(home.getOwner());
					onResponse.accept(player, response);
				})
				.open(home.getOwner().getPlayer());
	}

	public static void item(Home home, BiConsumer<Player, String[]> onResponse) {
		signMenuFactory.lines("", "Enter a player's", "name, \"hand\"", "or an item name")
				.response((player, response) -> {
					// TODO Item parsing
					new HomeService().save(home.getOwner());
					onResponse.accept(player, response);
				})
				.open(home.getOwner().getPlayer());
	}

	public static void rename(Home home, BiConsumer<Player, String[]> onResponse) {
		signMenuFactory.lines("", "^ ^ ^ ^ ^ ^", "Enter the home's", "new name")
				.response((player, response) -> {
					home.setName(response[0]);
					new HomeService().save(home.getOwner());
					onResponse.accept(player, response);
				})
				.open(home.getOwner().getPlayer());
	}

	public static String getAccessListNames(Set<UUID> accessList) {
		String lore = "";
		if (accessList.size() > 0) {
			lore += "||&f&m                         ||&f||&eAccess List||&f";
			Supplier<Stream<String>> names = () -> accessList.stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName());
			if (names.get().count() > 10)
				lore += loreize(names.get().collect(Collectors.joining(", ")));
			else
				lore += names.get().collect(Collectors.joining("||&f"));
		}
		return lore;
	}

}
