package gg.projecteden.nexus.features.homes.providers;

import gg.projecteden.nexus.features.homes.HomesFeature;
import gg.projecteden.nexus.features.homes.HomesMenu;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Rows(5)
@Title("&3Set a new home")
@RequiredArgsConstructor
public class SetHomeProvider extends InventoryProvider {
	private final HomeOwner homeOwner;

	@Override
	public void init() {
		addBackItem(e -> HomesMenu.edit(homeOwner));

		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK)
			.name("&eInfo")
			.lore("&fChoose one of the pre-set homes to automatically set the display item, or set your own home, and manually set the display item later")));

		contents.set(3, 4, ClickableItem.of(new ItemBuilder(Material.NAME_TAG)
				.name("&eCustom name")
				.lore("&fNone of these names fit?", "&fNo worries, you can still name it anything you'd like!"),
			e -> HomesMenu.create(homeOwner, response ->
				homeOwner.getHome(response[0]).ifPresent(HomesMenu::edit))));

		Map<String, ItemStack> options = new LinkedHashMap<>() {{
			put("home", new ItemBuilder(Material.CYAN_BED)
				.loreize(false)
				.lore("&fThis is your main home. You can teleport to it with &c/h &for &c/home")
				.build());

			put("spawner", new ItemStack(Material.SPAWNER));
			put("farm", new ItemStack(Material.WHEAT));
			put("mine", new ItemBuilder(Material.DIAMOND_PICKAXE).itemFlags(ItemFlag.HIDE_ATTRIBUTES).build());
			put("storage", new ItemStack(Material.CHEST));
			put("friend", new ItemStack(Material.PLAYER_HEAD));

			if (viewer.getWorld().getEnvironment().equals(Environment.NETHER))
				put("nether", new ItemStack(Material.NETHERRACK));
			else if (viewer.getWorld().getEnvironment().equals(Environment.THE_END))
				put("end", new ItemStack(Material.END_STONE_BRICKS));
			else
				put("explore", new ItemStack(Material.GRASS_BLOCK));
		}};

		AtomicInteger column = new AtomicInteger(1);
		options.forEach((name, item) ->
			contents.set(1, column.getAndIncrement(), ClickableItem.of(item, "&e" + StringUtils.camelCase(name), e -> {
				try {
					HomesMenu.edit(addHome(name, item));
				} catch (Exception ex) {
					MenuUtils.handleException(homeOwner.getOnlinePlayer(), HomesFeature.PREFIX, ex);
				}
			})));
	}

	private Home addHome(String homeName, ItemStack itemStack) {
		if (!homeOwner.getHome(homeName).isPresent())
			homeOwner.checkHomesLimit();

		Home home = Home.builder()
			.uuid(homeOwner.getUuid())
			.name(homeName)
			.location(homeOwner.getOnlinePlayer().getLocation())
			.item(itemStack)
			.build();

		homeOwner.add(home);
		new HomeService().save(homeOwner);
		return home;
	}

}
