package gg.projecteden.nexus.features.homes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.homes.providers.EditHomeProvider;
import gg.projecteden.nexus.features.homes.providers.EditHomesProvider;
import gg.projecteden.nexus.features.homes.providers.SetHomeProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HomesMenu {
	private static final SignMenuFactory signMenuFactory = Nexus.getSignMenuFactory();

	public static void edit(HomeOwner homeOwner) {
		edit(homeOwner, 0);
	}

	public static void edit(HomeOwner homeOwner, int page) {
		new EditHomesProvider(homeOwner).open(homeOwner.getOnlinePlayer(), page);
	}

	public static void edit(Home home) {
		new EditHomeProvider(home).open(home.getOwner().getOnlinePlayer());
	}

	public static void setHome(HomeOwner homeOwner) {
		new SetHomeProvider(homeOwner).open(homeOwner.getOnlinePlayer());
	}

	private static final List<String> playerNameLines = List.of("", SignMenuFactory.ARROWS, "Enter a", "player's name");

	public static void allow(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.prefix(HomesFeature.PREFIX)
				.response(lines -> {
					if (!lines[0].isEmpty()) {
						home.allow(PlayerUtils.getPlayer(lines[0]));
						new HomeService().save(home.getOwner());
					}
					onResponse.accept(lines);
				})
				.open(home.getOwner().getOnlinePlayer());
	}

	public static void remove(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.prefix(HomesFeature.PREFIX)
				.response(lines -> {
					if (!lines[0].isEmpty()) {
						home.remove(PlayerUtils.getPlayer(lines[0]));
						new HomeService().save(home.getOwner());
					}
					onResponse.accept(lines);
				})
				.open(home.getOwner().getOnlinePlayer());
	}

	public static void displayItem(Home home, Runnable onResponse) {
		displayItem(home, onResponse, null);
	}

	public static void displayItem(Home home, Runnable onResponse, String errorMessage) {
		new DialogBuilder()
			.title("Set Home Display Item")
			.bodyText("You can set your home's display item to any item or player's head")
			.bodyText("Or to the item in your main hand")
			.bodyText("Current Item")
			.bodyItem(home.getDisplayItemBuilder().build())
			.inputText("input", errorMessage)
			.multiAction()
			.button("Submit", response -> {
				try {
					var input = response.getText("input");
					if (!input.isEmpty()) {
						ItemStack itemStack = null;

						Material material = Material.matchMaterial(input);
						if (material != null) {
							itemStack = new ItemStack(material);
						} else {
							try {
								itemStack = new ItemBuilder(Material.PLAYER_HEAD)
									.skullOwner(PlayerUtils.getPlayer(input))
									.build();
							} catch (PlayerNotFoundException ignore) {}
						}

						if (itemStack == null) {
							displayItem(home, onResponse, "&cCould not parse item");
							return;
						} else {
							home.setItem(itemStack);
							new HomeService().save(home.getOwner());
						}
					}

					onResponse.run();
				} catch (Exception ex) {
					MenuUtils.handleException(home.getOnlinePlayer(), HomesFeature.PREFIX, ex);
				}
			})
			.button("Use item in main hand", response -> {
				home.setItem(home.getOnlinePlayer().getInventory().getItemInMainHand());
				new HomeService().save(home.getOwner());
				response.closeDialog();
				onResponse.run();
			})
			.button("Unset item", response -> {
				home.setItem(null);
				new HomeService().save(home.getOwner());
				response.closeDialog();
				onResponse.run();
			})
			.exitButton("Cancel", response -> onResponse.run())
			.columns(3)
			.open(home);
	}

	public static void rename(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines("", SignMenuFactory.ARROWS, "Enter the home's", "new name")
				.prefix(HomesFeature.PREFIX)
				.response(lines -> {
					if (!lines[0].isEmpty()) {
						if (home.getOwner().getHome(lines[0]).isPresent())
							PlayerUtils.send(home.getOnlinePlayer(), HomesFeature.PREFIX + "&cThat home already exists! Please pick a different name");
						else {
							home.setName(lines[0]);
							new HomeService().save(home.getOwner());
						}
					}
					onResponse.accept(lines);
				})
				.open(home.getOwner().getOnlinePlayer());
	}

	public static void create(HomeOwner homeOwner, Consumer<String[]> onResponse) {
		signMenuFactory.lines("", SignMenuFactory.ARROWS, "Enter your new", "home's name")
				.prefix(HomesFeature.PREFIX)
				.response(lines -> {
					if (!lines[0].isEmpty()) {
						if (homeOwner.getHome(lines[0]).isPresent())
							PlayerUtils.send(homeOwner.getOnlinePlayer(), HomesFeature.PREFIX + "&cThat home already exists! Please pick a different name");
						else {
							homeOwner.checkHomesLimit();
							homeOwner.add(Home.builder()
									.uuid(homeOwner.getUuid())
									.name(lines[0])
									.location(homeOwner.getOnlinePlayer().getLocation())
									.build());
							new HomeService().save(homeOwner);
						}
					}
					onResponse.accept(lines);
				})
				.open(homeOwner.getOnlinePlayer());
	}

	public static List<String> getAccessListNames(Set<UUID> accessList) {
		List<String> lore = new ArrayList<>();
		if (accessList.size() > 0) {
			lore.add("&f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m &f&m ");
			lore.add("");
			lore.add("&eAccess List");

			Supplier<Stream<String>> names = () -> accessList.stream().map(Nickname::of);
			if (names.get().count() > 10)
				lore.addAll(StringUtils.loreize(names.get().collect(Collectors.joining(", "))));
			else
				lore.addAll(names.get().map(name -> "&f" + name).toList());
		}
		return lore;
	}

}
