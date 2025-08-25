package gg.projecteden.nexus.features.homes.providers;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.homes.HomesMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

public class EditHomeProvider extends InventoryProvider {
	private final Home home;
	private final HomeOwner homeOwner;
	private final HomeService service = new HomeService();

	public EditHomeProvider(Home home) {
		this.home = home;
		this.homeOwner = home.getOwner();
	}

	@Override
	public String getTitle() {
		return (home.isLocked() ? "&4" : "&a") + StringUtils.camelCase(home.getName());
	}

	@Override
	public void init() {
		addBackItem(e -> HomesMenu.edit(homeOwner));

		Material material = home.isLocked() ? Material.IRON_BARS : Material.OAK_FENCE_GATE;
		String name = home.isLocked() ? "&cLocked" : "&aUnlocked";
		String lore = home.isLocked() ? "&eClick to unlock" : "&eClick to lock";

		contents.set(0, 4, ClickableItem.of(new ItemBuilder(material).name(name).lore("", lore), e -> {
			home.setLocked(!home.isLocked());
			service.save(homeOwner);
			refresh();
		}));

		if (home.isLocked()) {
			contents.set(0, 7, ClickableItem.of(new ItemBuilder(Material.LIME_CONCRETE_POWDER)
				.name("&eGive a player access")
				.lore("&eto this home", "", "&fThey will be able to teleport to this home even if it is locked")
				.lore(HomesMenu.getAccessListNames(home.getAccessList())),
				e -> HomesMenu.allow(home, response -> refresh())
			));

			contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.RED_CONCRETE_POWDER)
				.name("&eRemove a player's")
				.lore("&eaccess to this home", "", "&fThey will only be able to teleport to this home if it is unlocked"),
				e -> HomesMenu.remove(home, response -> refresh())
			));
		}

		contents.set(2, 1, ClickableItem.of(new ItemBuilder(Material.PAINTING)
			.name("&eSet display item")
			.lore("&fWish it was easier to find this home in the menu? Change what item it displays as to distinguish it from the rest!"),
			e -> HomesMenu.displayItem(home, this::refresh)
		));

		contents.set(2, 2, ClickableItem.of(Material.NAME_TAG, "&eRename", e -> HomesMenu.rename(home, response -> refresh())));
		contents.set(2, 4, ClickableItem.of(Material.COMPASS, "&eTeleport", e -> home.teleportAsync(viewer)));

		contents.set(2, 6, ClickableItem.of(Material.FILLED_MAP, "&eSet to current location", e -> ConfirmationMenu.builder()
			.onCancel(e2 -> refresh())
			.onConfirm(e2 -> {
				home.setLocation(viewer.getLocation());
				service.save(homeOwner);
				refresh();
			})
			.open(viewer)));

		ItemBuilder respawn;
		if (home.isRespawn())
			respawn = new ItemBuilder(Material.ORANGE_BED).name("&eCurrent respawn location").glow();
		else
			respawn = new ItemBuilder(Material.CYAN_BED).name("&eSet as respawn location");
		contents.set(2, 7, ClickableItem.of(respawn.build(), e -> {
			home.setRespawn(true);
			service.save(homeOwner);
			refresh();
		}));

		contents.set(4, 4, ClickableItem.of(Material.LAVA_BUCKET, "&eDelete",
			e -> ConfirmationMenu.builder()
				.onCancel(e2 -> refresh())
				.onConfirm(e2 -> {
					homeOwner.delete(home);
					service.save(homeOwner);
					HomesMenu.edit(homeOwner);
				})
				.open(viewer)));
	}

}
