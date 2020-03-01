package me.pugabyte.bncore.features.homes.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.features.homes.HomesMenu;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.features.homes.HomesMenu.getAccessListNames;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public class EditHomesProvider extends MenuUtils implements InventoryProvider {
	private HomeOwner homeOwner;
	private HomeService service;

	public EditHomesProvider(HomeOwner homeOwner) {
		this.homeOwner = homeOwner;
		this.service = new HomeService();
	}

	private void refresh() {
		HomesMenu.edit(homeOwner);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		format_SetNewHome(contents);
		format_AutoLock(contents);
		format_AllowAndRemoveAll(contents);
		format_LockAndUnlockAll(contents);
		format_Homes(contents);
	}

	public void format_SetNewHome(InventoryContents contents) {
		int homes = homeOwner.getHomes().size();
		int max = homeOwner.getMaxHomes();
		int left = max - homes;

		ItemStackBuilder item = new ItemStackBuilder(Material.NAME_TAG);

		if (left > 0)
			item.name("&eSet a new home").lore("You have set &e" + homes + " &fof your &e" + max + " &fhomes||" +
					"&fYou can set &e" + left + " &fmore");
		else
			item.name("&cYou have used all of").lore("&cyour available homes! &3(" + max + ")||&f||" +
					"To set more homes, you will need to either &erank up &3or &c/donate");

		contents.set(0, 3, ClickableItem.from(item.build(), e -> HomesMenu.setHome(homeOwner)));
	}

	public void format_AutoLock(InventoryContents contents) {
		ItemStackBuilder item = new ItemStackBuilder(Material.REDSTONE);

		if (homeOwner.isAutoLock())
			item.name("&eAuto Lock &f| &aON").lore("Any new homes you set will be automatically locked").glow();
		else
			item.name("&eAuto Lock &f| &cOFF").lore("Any new homes you set will be unlocked");

		contents.set(0, 5, ClickableItem.from(item.build(), e -> {
			homeOwner.setAutoLock(!homeOwner.isAutoLock());
			service.save(homeOwner);
			refresh();
		}));
	}

	public void format_AllowAndRemoveAll(InventoryContents contents) {
		contents.set(1, 2, ClickableItem.from(new ItemStackBuilder(ColorType.LIGHT_GREEN.getItemStack(Material.CONCRETE_POWDER))
						.name("&eGrant a player access to all homes")
						.loreize(false)
						.lore("&fThey will be able to teleport to||&fyour homes even if they are locked" + getAccessListNames(homeOwner.getFullAccessList())).build(),
			e -> HomesMenu.allowAll(homeOwner, (owner, response) -> refresh())
		));

		contents.set(1, 3, ClickableItem.from(nameItem(
				ColorType.RED.getItemStack(Material.CONCRETE_POWDER),
				"&eRevoke a player's access from all homes",
				"&fThey will only be able to teleport to your unlocked homes"
			),
			e -> HomesMenu.removeAll(homeOwner, (owner, response) -> refresh())
		));
	}

	public void format_LockAndUnlockAll(InventoryContents contents) {
		contents.set(1, 5, ClickableItem.from(nameItem(Material.IRON_FENCE, "&eLock all homes"), e -> {
			homeOwner.getHomes().forEach(home -> home.setLocked(true));
			service.save(homeOwner);
			refresh();
		}));

		contents.set(1, 6, ClickableItem.from(nameItem(Material.FENCE_GATE, "&eUnlock all homes"), e -> {
			homeOwner.getHomes().forEach(home -> home.setLocked(false));
			service.save(homeOwner);
			refresh();
		}));
	}

	public void format_Homes(InventoryContents contents) {
		if (homeOwner.getHomes() == null || homeOwner.getHomes().size() == 0) return;

		List<ClickableItem> items = new ArrayList<>();

		// TODO: Look into async paginator in SmartInvs
		homeOwner.getHomes().forEach(home -> {
			ItemStackBuilder item;

			if (home.getItem() != null)
				item = new ItemStackBuilder(home.getItem());
			else if (home.isLocked())
				item = new ItemStackBuilder(ColorType.RED.getItemStack(Material.CONCRETE));
			else
				item = new ItemStackBuilder(ColorType.LIGHT_GREEN.getItemStack(Material.CONCRETE));

			if (home.isLocked())
				item.glow().loreize(false).lore("||&f&cLocked||&f||&eClick to edit" + getAccessListNames(home.getAccessList()));
			else
				item.lore("||&f&aUnlocked||&f||&eClick to edit");

			item.name("&f" + camelCase(home.getName()));

			items.add(ClickableItem.from(item.build(), e -> HomesMenu.edit(home)));
		});

		Pagination page = contents.pagination();
		page.setItems(items.toArray(new ClickableItem[0]));
		page.setItemsPerPage(27);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 3, 0));

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
