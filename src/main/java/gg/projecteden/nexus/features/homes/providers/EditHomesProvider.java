package gg.projecteden.nexus.features.homes.providers;

import gg.projecteden.nexus.features.homes.HomesMenu;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.trust.providers.TrustProvider;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.homes.HomesMenu.getAccessListNames;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

public class EditHomesProvider extends MenuUtils implements InventoryProvider {
	private HomeOwner homeOwner;
	private HomeService service = new HomeService();

	public EditHomesProvider(HomeOwner homeOwner) {
		this.homeOwner = homeOwner;
	}

	private void refresh() {
		HomesMenu.edit(homeOwner);
	}

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.provider(this)
				.rows((int) Math.min(6, Math.ceil(Integer.valueOf(homeOwner.getHomes().size()).doubleValue() / 9) + 2))
				.title("&3Home Editor")
				.build()
				.open(homeOwner.getOnlinePlayer(), page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		format_SetNewHome(contents);
		format_AutoLock(contents);
		format_LockAndUnlockAll(contents);
		format_Trust(contents);
		format_Homes(contents);
	}

	public void format_SetNewHome(InventoryContents contents) {
		int homes = homeOwner.getHomes().size();
		int max = homeOwner.getHomesLimit();
		int left = max - homes;

		ItemBuilder item = new ItemBuilder(Material.NAME_TAG);

		if (left > 0)
			item.name("&eSet a new home").lore("&fYou have set &e" + homes + " &fof your &e" + max + " &fhomes", "",
					"&fYou can set &e" + left + " &fmore");
		else
			item.name("&cYou have used all of").lore("&cyour available homes! &3(" + max + ")", "&f",
					"&fTo set more homes, you will need to either &erank up &for purchase more from the &c/store");

		contents.set(0, 1, ClickableItem.of(item.build(), e -> HomesMenu.setHome(homeOwner)));
	}

	public void format_AutoLock(InventoryContents contents) {
		ItemBuilder item = new ItemBuilder(Material.REDSTONE);

		if (Rank.of(homeOwner).isStaff()) {
			item.name("&eAuto Lock &f| &aON").lore("&fAny new homes you set will be automatically locked", "", "&cRequired for staff members").glow();
			contents.set(0, 3, ClickableItem.empty(item.build()));
		} else {
			if (homeOwner.isAutoLock())
				item.name("&eAuto Lock &f| &aON").lore("&fAny new homes you set will be automatically locked").glow();
			else
				item.name("&eAuto Lock &f| &cOFF").lore("&fAny new homes you set will be unlocked");

			contents.set(0, 3, ClickableItem.of(item.build(), e -> {
				homeOwner.setAutoLock(!homeOwner.isAutoLock());
				service.save(homeOwner);
				refresh();
			}));
		}
	}

	public void format_LockAndUnlockAll(InventoryContents contents) {
		contents.set(0, 5, ClickableItem.of(Material.IRON_BARS, "&eLock all homes", e -> {
			homeOwner.getHomes().forEach(home -> home.setLocked(true));
			service.save(homeOwner);
			refresh();
		}));

		contents.set(0, 6, ClickableItem.of(Material.OAK_FENCE_GATE, "&eUnlock all homes", e -> {
			homeOwner.getHomes().forEach(home -> home.setLocked(false));
			service.save(homeOwner);
			refresh();
		}));
	}

	public void format_Trust(InventoryContents contents) {
		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.LEVER)
						.name("&eEdit Trusts")
						.loreize(false)
						.lore("&fManage access to", "&fall your homes").build(),
				e -> TrustProvider.openMenu(homeOwner.getOnlinePlayer(), this::refresh)
		));
	}

	public void format_Homes(InventoryContents contents) {
		if (homeOwner.getHomes() == null || homeOwner.getHomes().size() == 0) return;

		List<ClickableItem> items = new ArrayList<>();

		// TODO: Look into async paginator in SmartInvs
		homeOwner.getHomes().forEach(home -> {
			ItemBuilder item;

			if (home.getItem() != null && home.getItem().getItemMeta() != null)
				item = new ItemBuilder(home.getItem());
			else if (home.isLocked())
				item = new ItemBuilder(Material.RED_CONCRETE);
			else
				item = new ItemBuilder(Material.LIME_CONCRETE);

			if (home.isLocked())
				item.glow().loreize(false).lore("", "&f&cLocked", "&f", "&eClick to edit" + getAccessListNames(home.getAccessList()));
			else
				item.lore("", "&f&aUnlocked", "&f", "&eClick to edit");

			item.name("&f" + camelCase(home.getName()));

			items.add(ClickableItem.of(item.build(), e -> HomesMenu.edit(home)));
		});

		paginator(homeOwner.getOnlinePlayer(), contents, items);
	}

}
