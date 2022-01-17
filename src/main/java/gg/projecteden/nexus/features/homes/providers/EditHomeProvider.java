package gg.projecteden.nexus.features.homes.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.homes.HomesMenu;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.features.homes.HomesMenu.getAccessListNames;

public class EditHomeProvider extends MenuUtils implements InventoryProvider {
	private Home home;
	private HomeOwner homeOwner;
	private HomeService service = new HomeService();

	public EditHomeProvider(Home home) {
		this.home = home;
		this.homeOwner = home.getOwner();
	}

	private void refresh() {
		HomesMenu.edit(home);
	}

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.provider(this)
				.size(6, 9)
				.title(StringUtils.colorize((home.isLocked() ? "&4" : "&a") + StringUtils.camelCase(home.getName())))
				.build()
				.open(home.getOwner().getOnlinePlayer(), page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> HomesMenu.edit(homeOwner));

		Material material = home.isLocked() ? Material.IRON_BARS : Material.OAK_FENCE_GATE;
		String name = home.isLocked() ? "&cLocked" : "&aUnlocked";
		String lore = "||" + (home.isLocked() ? "&eClick to unlock" : "&eClick to lock");

		contents.set(0, 4, ClickableItem.from(nameItem(material, name, lore), e -> {
			home.setLocked(!home.isLocked());
			service.save(homeOwner);
			refresh();
		}));

		if (home.isLocked()) {
			contents.set(0, 7, ClickableItem.from(nameItem(
					Material.LIME_CONCRETE_POWDER,
					"&eGive a player access",
					"&eto this home||&f||&fThey will be able to teleport to this home even if it is locked" + getAccessListNames(home.getAccessList())
				),
				e -> HomesMenu.allow(home, response -> refresh())
			));

			contents.set(0, 8, ClickableItem.from(nameItem(
					Material.RED_CONCRETE_POWDER,
					"&eRemove a player's",
					"&eaccess to this home||&f||&fThey will only be able to teleport to this home if it is unlocked"
				),
				e -> HomesMenu.remove(home, response -> refresh())
			));
		}

		contents.set(2, 1, ClickableItem.from(nameItem(
				Material.PAINTING,
				"&eSet display item",
				"&fWish it was easier to find this home in the menu? Change what item it displays as to distinguish it from the rest!"
			),
			e -> HomesMenu.displayItem(home, response -> refresh())
		));

		contents.set(2, 2, ClickableItem.from(nameItem(Material.NAME_TAG, "&eRename"), e -> HomesMenu.rename(home, response -> refresh())));
		contents.set(2, 4, ClickableItem.from(nameItem(Material.COMPASS, "&eTeleport"), e -> home.teleportAsync(player)));

		contents.set(2, 6, ClickableItem.from(nameItem(Material.FILLED_MAP, "&eSet to current location"),
				e -> ConfirmationMenu.builder()
						.onCancel(e2 -> refresh())
						.onConfirm(e2 -> {
							home.setLocation(player.getLocation());
							service.save(homeOwner);
							refresh();
						})
						.open(player)));

		ItemBuilder respawn;
		if (home.isRespawn())
			respawn = new ItemBuilder(Material.ORANGE_BED).name("&eCurrent respawn location").glow();
		else
			respawn = new ItemBuilder(Material.CYAN_BED).name("&eSet as respawn location");
		contents.set(2, 7, ClickableItem.from(respawn.build(), e -> {
			home.setRespawn(true);
			service.save(homeOwner);
			refresh();
		}));

		contents.set(4, 4, ClickableItem.from(nameItem(Material.LAVA_BUCKET, "&eDelete"),
				e -> ConfirmationMenu.builder()
						.onCancel(e2 -> refresh())
						.onConfirm(e2 -> {
							homeOwner.delete(home);
							service.save(homeOwner);
							HomesMenu.edit(homeOwner);
						})
						.open(player)));
	}

}
