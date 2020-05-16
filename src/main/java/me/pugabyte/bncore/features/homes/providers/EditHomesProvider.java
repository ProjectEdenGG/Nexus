package me.pugabyte.bncore.features.homes.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.features.homes.HomesMenu;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.trust.providers.TrustProvider;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.features.homes.HomesMenu.getAccessListNames;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

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
		int max = homeOwner.getMaxHomes();
		int left = max - homes;

		ItemBuilder item = new ItemBuilder(Material.NAME_TAG);

		if (left > 0)
			item.name("&eSet a new home").lore("&fYou have set &e" + homes + " &fof your &e" + max + " &fhomes", "",
					"&fYou can set &e" + left + " &fmore");
		else
			item.name("&cYou have used all of").lore("&cyour available homes! &3(" + max + ")", "&f",
					"&fTo set more homes, you will need to either &erank up &for &c/donate");

		contents.set(0, 1, ClickableItem.from(item.build(), e -> HomesMenu.setHome(homeOwner)));
	}

	public void format_AutoLock(InventoryContents contents) {
		ItemBuilder item = new ItemBuilder(Material.REDSTONE);

		if (homeOwner.isAutoLock())
			item.name("&eAuto Lock &f| &aON").lore("&fAny new homes you set will be automatically locked").glow();
		else
			item.name("&eAuto Lock &f| &cOFF").lore("&fAny new homes you set will be unlocked");

		contents.set(0, 3, ClickableItem.from(item.build(), e -> {
			homeOwner.setAutoLock(!homeOwner.isAutoLock());
			service.save(homeOwner);
			refresh();
		}));
	}

	public void format_LockAndUnlockAll(InventoryContents contents) {
		contents.set(0, 5, ClickableItem.from(nameItem(Material.IRON_BARS, "&eLock all homes"), e -> {
			homeOwner.getHomes().forEach(home -> home.setLocked(true));
			service.save(homeOwner);
			refresh();
		}));

		contents.set(0, 6, ClickableItem.from(nameItem(Material.OAK_FENCE_GATE, "&eUnlock all homes"), e -> {
			homeOwner.getHomes().forEach(home -> home.setLocked(false));
			service.save(homeOwner);
			refresh();
		}));
	}

	public void format_Trust(InventoryContents contents) {
		contents.set(0, 8, ClickableItem.from(new ItemBuilder(Material.LEVER)
						.name("&eEdit Trusts")
						.loreize(false)
						.lore("&fManage access to||&fall your homes").build(),
				e -> TrustProvider.open(homeOwner.getPlayer(), this::refresh)
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

			items.add(ClickableItem.from(item.build(), e -> HomesMenu.edit(home)));
		});

		Pagination page = contents.pagination();
		page.setItems(items.toArray(new ClickableItem[0]));
		page.setItemsPerPage(36);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		if (!page.isFirst())
			contents.set(2, 0, ClickableItem.from(nameItem(new ItemStack(Material.PAPER, Math.max(page.getPage() - 1, 1)),
					"&fPrevious Page"), e -> HomesMenu.edit(homeOwner, page.previous().getPage())));
		if (!page.isLast())
			contents.set(2, 8, ClickableItem.from(nameItem(new ItemStack(Material.PAPER, page.getPage() + 1),
					"&fNext Page"), e -> HomesMenu.edit(homeOwner, page.next().getPage())));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
