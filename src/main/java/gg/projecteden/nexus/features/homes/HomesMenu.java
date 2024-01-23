package gg.projecteden.nexus.features.homes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.homes.providers.EditHomeProvider;
import gg.projecteden.nexus.features.homes.providers.EditHomesProvider;
import gg.projecteden.nexus.features.homes.providers.SetHomeProvider;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
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

import static gg.projecteden.nexus.features.menus.api.SignMenuFactory.ARROWS;
import static gg.projecteden.nexus.utils.StringUtils.loreize;

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

	private static final List<String> playerNameLines = List.of("", ARROWS, "Enter a", "player's name");

	public static void allow(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.prefix(HomesFeature.PREFIX)
				.response(lines -> {
					if (lines[0].length() > 0) {
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
					if (lines[0].length() > 0) {
						home.remove(PlayerUtils.getPlayer(lines[0]));
						new HomeService().save(home.getOwner());
					}
					onResponse.accept(lines);
				})
				.open(home.getOwner().getOnlinePlayer());
	}

	public static void displayItem(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines("", "Enter a player's", "name, 'hand'", "or an item name")
				.prefix(HomesFeature.PREFIX)
				.response(lines -> {
					String input = lines[0];
					if (input.length() > 0) {
						ItemStack itemStack = null;

						if ("hand".equalsIgnoreCase(input)) {
							itemStack = home.getOnlinePlayer().getInventory().getItemInMainHand();
						} else {
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
						}

						if (itemStack == null) {
							PlayerUtils.send(home.getOnlinePlayer(), HomesFeature.PREFIX + "&cCould not parse item");
							displayItem(home, onResponse);
						} else {
							home.setItem(itemStack);
							new HomeService().save(home.getOwner());
						}
					}

					onResponse.accept(lines);
				})
				.open(home.getOwner().getOnlinePlayer());
	}

	public static void rename(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines("", ARROWS, "Enter the home's", "new name")
				.prefix(HomesFeature.PREFIX)
				.response(lines -> {
					if (lines[0].length() > 0) {
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
		signMenuFactory.lines("", ARROWS, "Enter your new", "home's name")
				.prefix(HomesFeature.PREFIX)
				.response(lines -> {
					if (lines[0].length() > 0) {
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
				lore.addAll(loreize(names.get().collect(Collectors.joining(", "))));
			else
				lore.addAll(names.get().map(name -> "&f" + name).toList());
		}
		return lore;
	}

}
