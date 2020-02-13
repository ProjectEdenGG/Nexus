package me.pugabyte.bncore.features.homes.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.homes.HomesMenu;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.bncore.utils.Utils.camelCase;

public class SetHomeProvider extends MenuUtils implements InventoryProvider {
	private HomeOwner homeOwner;

	public SetHomeProvider(HomeOwner homeOwner) {
		this.homeOwner = homeOwner;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> HomesMenu.edit(homeOwner));

		contents.set(0, 8, ClickableItem.empty(nameItem(Material.BOOK, "&eInfo", "&fChoose one of the pre-set homes to " +
				"automatically set the display item, or set your own home, and manually set the display item later")));

		contents.set(3, 4, ClickableItem.from(nameItem(
				Material.NAME_TAG,
				"&eCustom name",
				"&fNone of these names fit?||&fNo worries, you can still name it anything you'd like!"
			), e -> HomesMenu.create(homeOwner, (owner, response) ->
				homeOwner.getHome(response[0]).ifPresent(HomesMenu::edit))));

		Map<String, ItemStack> options = new LinkedHashMap<String, ItemStack>() {{
			put("home", new ItemStackBuilder(Material.BED)
					.durability(ColorType.CYAN.getDurability().shortValue())
					.loreize(false)
					.lore("&fThis is your main home. You can teleport to it with &c/h &for &c/home")
					.build());

			put("spawner", new ItemStack(Material.MOB_SPAWNER));
			put("farm", new ItemStack(Material.WHEAT));
			put("mine", new ItemStackBuilder(Material.DIAMOND_PICKAXE).itemFlags(ItemFlag.HIDE_ATTRIBUTES).build());
			put("storage", new ItemStack(Material.CHEST));
			put("shop", new ItemStack(Material.SIGN));

			if (player.getWorld().getEnvironment().equals(Environment.NETHER))
				put("nether", new ItemStack(Material.NETHERRACK));
			else if (player.getWorld().getEnvironment().equals(Environment.THE_END))
				put("end", new ItemStack(Material.END_BRICKS));
			else
				put("explore", new ItemStack(Material.GRASS));
		}};

		AtomicInteger column = new AtomicInteger(1);
		options.forEach((name, item) ->
				contents.set(1, column.getAndIncrement(), ClickableItem.from(nameItem(item, "&e" + camelCase(name)),
				e -> {
					try {
						HomesMenu.edit(addHome(name, item));
					} catch (Exception ex) {
						HomesMenu.handleException(homeOwner.getPlayer(), ex);
					}
				})));
	}

	private Home addHome(String homeName, ItemStack itemStack) {
		Home home = Home.builder()
				.uuid(homeOwner.getUuid())
				.name(homeName)
				.location(homeOwner.getPlayer().getLocation())
				.item(itemStack)
				.build();

		homeOwner.add(home);
		new HomeService().save(homeOwner);
		return home;
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
