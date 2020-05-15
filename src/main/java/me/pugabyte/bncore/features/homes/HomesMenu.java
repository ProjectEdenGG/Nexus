package me.pugabyte.bncore.features.homes;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.homes.providers.EditHomeProvider;
import me.pugabyte.bncore.features.homes.providers.EditHomesProvider;
import me.pugabyte.bncore.features.homes.providers.SetHomeProvider;
import me.pugabyte.bncore.features.menus.SignMenuFactory;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.pugabyte.bncore.features.homes.HomesFeature.PREFIX;
import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.loreize;

public class HomesMenu {
	private static final SignMenuFactory signMenuFactory = BNCore.getSignMenuFactory();

	public static void edit(HomeOwner homeOwner) {
		edit(homeOwner, 0);
	}

	public static void edit(HomeOwner homeOwner, int page) {
		SmartInventory.builder()
				.provider(new EditHomesProvider(homeOwner))
				.size((int) Math.min(6, Math.ceil(Integer.valueOf(homeOwner.getHomes().size()).doubleValue() / 9) + 3), 9)
				.title(StringUtils.colorize("&3Home Editor"))
				.build()
				.open(homeOwner.getPlayer(), page);
	}

	public static void edit(Home home) {
		SmartInventory.builder()
				.provider(new EditHomeProvider(home))
				.size(6, 9)
				.title(StringUtils.colorize((home.isLocked() ? "&4" : "&a") + StringUtils.camelCase(home.getName())))
				.build()
				.open(home.getOwner().getPlayer());
	}

	public static void setHome(HomeOwner homeOwner) {
		SmartInventory.builder()
				.provider(new SetHomeProvider(homeOwner))
				.size(5, 9)
				.title(StringUtils.colorize("&3Set a new home"))
				.build()
				.open(homeOwner.getPlayer());
	}

	private static final String[] playerNameLines = {"", "^ ^ ^ ^ ^ ^", "Enter a", "player's name"};

	public static void allowAll(HomeOwner homeOwner, Consumer<String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.response(lines -> {
					if (lines[0].length() > 0) {
						homeOwner.allowAll(Utils.getPlayer(lines[0]));
						new HomeService().save(homeOwner);
					}
					onResponse.accept(lines);
				})
				.open(homeOwner.getPlayer());

	}

	public static void removeAll(HomeOwner homeOwner, Consumer<String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.response(lines -> {
					if (lines[0].length() > 0) {
						homeOwner.removeAll(Utils.getPlayer(lines[0]));
						new HomeService().save(homeOwner);
					}
					onResponse.accept(lines);
				})
				.open(homeOwner.getPlayer());
	}

	public static void allow(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.response(lines -> {
					if (lines[0].length() > 0) {
						home.allow(Utils.getPlayer(lines[0]));
						new HomeService().save(home.getOwner());
					}
					onResponse.accept(lines);
				})
				.open(home.getOwner().getPlayer());
	}

	public static void remove(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines(playerNameLines)
				.response(lines -> {
					if (lines[0].length() > 0) {
						home.remove(Utils.getPlayer(lines[0]));
						new HomeService().save(home.getOwner());
					}
					onResponse.accept(lines);
				})
				.open(home.getOwner().getPlayer());
	}

	public static void displayItem(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines("", "Enter a player's", "name, 'hand'", "or an item name")
				.response(lines -> {
					String input = lines[0];
					if (input.length() > 0) {
						ItemStack itemStack = null;

						if ("hand".equalsIgnoreCase(input)) {
							itemStack = home.getPlayer().getInventory().getItemInMainHand();
						} else {
							Material material = Material.matchMaterial(input);
							if (material != null) {
								itemStack = new ItemStack(material);
							} else {
								try {
									OfflinePlayer offlinePlayer = Utils.getPlayer(input);
									if (offlinePlayer != null) {
										itemStack = new ItemBuilder(Material.PLAYER_HEAD).build();
										SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
										meta.setOwningPlayer(offlinePlayer);
										itemStack.setItemMeta(meta);
									}
								} catch (PlayerNotFoundException ignore) {}
							}
						}

						if (itemStack == null) {
							home.getPlayer().sendMessage(colorize(PREFIX + "&cCould not parse item"));
							displayItem(home, onResponse);
						} else {
							home.setItem(itemStack);
							new HomeService().save(home.getOwner());
						}
					}

					onResponse.accept(lines);
				})
				.open(home.getOwner().getPlayer());
	}

	public static void rename(Home home, Consumer<String[]> onResponse) {
		signMenuFactory.lines("", "^ ^ ^ ^ ^ ^", "Enter the home's", "new name")
				.response(lines -> {
					if (lines[0].length() > 0) {
						if (home.getOwner().getHome(lines[0]).isPresent())
							home.getPlayer().sendMessage(PREFIX + colorize("&cThat home already exists! Please pick a different name"));
						else {
							home.setName(lines[0]);
							new HomeService().save(home.getOwner());
						}
					}
					onResponse.accept(lines);
				})
				.open(home.getOwner().getPlayer());
	}

	public static void create(HomeOwner homeOwner, Consumer<String[]> onResponse) {
		signMenuFactory.lines("", "^ ^ ^ ^ ^ ^", "Enter your new", "home's name")
				.response(lines -> {
					if (lines[0].length() > 0) {
						if (homeOwner.getHome(lines[0]).isPresent())
							homeOwner.getPlayer().sendMessage(PREFIX + colorize("&cThat home already exists! Please pick a different name"));
						else {
							homeOwner.add(Home.builder()
									.uuid(homeOwner.getUuid())
									.name(lines[0])
									.location(homeOwner.getPlayer().getLocation())
									.build());
							new HomeService().save(homeOwner);
						}
					}
					onResponse.accept(lines);
				})
				.open(homeOwner.getPlayer());
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
