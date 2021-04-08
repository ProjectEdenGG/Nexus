package me.pugabyte.nexus.features.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import fr.minuskube.inv.content.SlotPos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.shops.Shops;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static me.pugabyte.nexus.features.menus.SignMenuFactory.ARROWS;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.loreize;

public abstract class MenuUtils {

	public static int getRows(int items) {
		return getRows(items, 2, 9);
	}

	public static int getRows(int items, int extraRows) {
		return getRows(items, extraRows, 9);
	}

	public static int getRows(int items, int extraRows, int itemsAcross) {
		return (int) Math.min(6, Math.ceil(Integer.valueOf(items).doubleValue() / itemsAcross) + extraRows);
	}

	public void open(Player player) {
		open(player, 0);
	}

	public void open(Player viewer, int page) {
	}

	protected boolean isRightClick(ItemClickData e) {
		return isClickType(e, ClickType.RIGHT);
	}

	protected boolean isLeftClick(ItemClickData e) {
		return isClickType(e, ClickType.LEFT);
	}

	protected boolean isClickType(ItemClickData e, ClickType clickType) {
		return e.getEvent() instanceof InventoryClickEvent && ((InventoryClickEvent) e.getEvent()).getClick() == clickType;
	}

	protected ItemStack addGlowing(ItemStack itemStack) {
		return ItemBuilder.glow(itemStack);
	}

	protected ItemStack nameItem(Material material, String name) {
		return nameItem(new ItemStack(material), name, (List<String>) null);
	}

	protected ItemStack nameItem(Material material, String name, String lore) {
		return nameItem(new ItemStack(material), name, lore);
	}

	protected ItemStack nameItem(ItemStack item, String name) {
		return nameItem(item, name, (List<String>) null);
	}

	protected ItemStack nameItem(ItemStack item, String name, String lore) {
		return nameItem(item, name, lore == null ? null : Arrays.asList(loreize(colorize(lore)).split("\\|\\|")));
	}

	protected ItemStack nameItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		if (name != null)
			meta.setDisplayName(colorize(name));
		if (lore != null)
			meta.setLore(lore);

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}

	protected void warp(Player player, String warp) {
		PlayerUtils.runCommand(player, "warp " + warp);
	}

	public void command(Player player, String command) {
		PlayerUtils.runCommand(player, command);
	}

	public static String getLocationLore(Location location) {
		if (location == null) return null;
		return "&3X:&e " + (int) location.getX() + "||&3Y:&e " + (int) location.getY() + "||&3Z:&e " + (int) location.getZ();
	}

	protected void addBackItem(InventoryContents contents, Consumer<ItemClickData> consumer) {
		addBackItem(contents, 0, 0, consumer);
	}

	protected void addBackItem(InventoryContents contents, int row, int col, Consumer<ItemClickData> consumer) {
		contents.set(row, col, ClickableItem.from(backItem(), consumer));
	}

	protected void addCloseItem(InventoryContents contents) {
		addCloseItem(contents, 0, 0);
	}

	protected void addCloseItem(InventoryContents contents, int row, int col) {
		contents.set(row, col, ClickableItem.from(closeItem(), e -> e.getPlayer().closeInventory()));
	}

	protected ItemStack backItem() {
		return new ItemBuilder(Material.BARRIER).name("&cBack").build();
	}

	protected ItemStack closeItem() {
		return new ItemBuilder(Material.BARRIER).name("&cClose").build();
	}

	public static void handleException(Player player, String prefix, Throwable ex) {
		if (ex.getCause() != null && ex.getCause() instanceof NexusException)
			PlayerUtils.send(player, prefix + "&c" + ex.getCause().getMessage());
		else if (ex instanceof NexusException)
			PlayerUtils.send(player, prefix + "&c" + ex.getMessage());
		else {
			PlayerUtils.send(player, "&cAn internal error occurred while attempting to execute this command");
			ex.printStackTrace();
		}
	}

	public static void centerItems(ClickableItem[] items, InventoryContents contents, int row) {
		centerItems(items, contents, row, true);
	}

	public static void centerItems(ClickableItem[] items, InventoryContents contents, int row, boolean space) {
		if (items.length > 9)
			throw new InvalidInputException("Cannot center more than 9 items on one row");
		int[] even = {3, 5, 1, 7};
		int[] odd = {4, 2, 6, 0, 8};
		int[] noSpace = {4, 3, 5, 2, 6, 1, 7, 0, 8};
		if (items.length < 5 && space)
			if (items.length % 2 == 0)
				for (int i = 0; i < items.length; i++)
					contents.set(row, even[i], items[i]);
			else
				for (int i = 0; i < items.length; i++)
					contents.set(row, odd[i], items[i]);
		else
			for (int i = 0; i < items.length; i++)
				contents.set(row, noSpace[i], items[i]);
	}

	protected void addPagination(Player player, InventoryContents contents, List<ClickableItem> items) {
		int perPage = 36;
		Pagination page = contents.pagination();

		if (page.getPage() > items.size() / perPage)
			page.page(items.size() / perPage);
		int curPage = page.getPage() + 1;

		String[] lore = {"&f", "&7Right click to jump to a page"};
		boolean hasResourcePack = PlayerUtils.hasResourcePack(player);

		int prevPage = Math.max(curPage - 1, 1);
		int nextPage = curPage + 1;

		ItemBuilder prev = new ItemBuilder(Material.ARROW).name("&fPrevious Page").lore(lore);
		ItemBuilder next = new ItemBuilder(Material.ARROW).name("&fNext Page").lore(lore);

		if (hasResourcePack) {
			prev.customModelData(4000 + prevPage);
			next.customModelData(4000 + nextPage);
		} else {
			prev.amount(prevPage);
			next.amount(nextPage);
		}

		addPagination(contents, items, perPage,
				ClickableItem.from(prev.build(), e -> {
					if (isRightClick(e))
						jumpToPage(player, page.getPage());
					else
						open(player, page.previous().getPage());
				}),
				ClickableItem.from(next.build(), e -> {
					if (isRightClick(e))
						jumpToPage(player, page.getPage());
					else
						open(player, page.next().getPage());
				}));
	}

	protected void addPagination(InventoryContents contents, List<ClickableItem> items, int perPage, ClickableItem previous, ClickableItem next) {
		Pagination page = contents.pagination();
		page.setItemsPerPage(perPage);
		page.setItems(items.toArray(new ClickableItem[0]));
		if (page.getPage() > items.size() / perPage)
			page.page(items.size() / perPage);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		if (!page.isFirst())
			contents.set(contents.inventory().getRows() - 1, 0, previous);
		if (!page.isLast())
			contents.set(contents.inventory().getRows() - 1, 8, next);
	}

	private void jumpToPage(Player player, int currentPage) {
		Nexus.getSignMenuFactory()
				.lines("", ARROWS, "Enter a", "page number")
				.prefix(Shops.PREFIX)
				.onError(() -> open(player, currentPage))
				.response(lines -> {
					if (lines[0].length() > 0) {
						String input = lines[0].replaceAll("[^0-9.-]+", "");
						if (!Utils.isInt(input))
							throw new InvalidInputException("Could not parse &e" + lines[0] + " &cas a page number");
						int pageNumber = Math.max(0, Integer.parseInt(input) - 1);
						open(player, pageNumber);
					} else
						open(player, currentPage);
				}).open(player);
	}

	public static void openAnvilMenu(Player player, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete, Consumer<Player> onClose) {
		new AnvilGUI.Builder()
				.text(text)
				.onComplete(onComplete)
				.onClose(onClose)
				.plugin(Nexus.getInstance())
				.open(player);
	}

	public static void colorSelectMenu(Player player, Material type, Consumer<ItemClickData> onClick) {
		SmartInventory.builder()
				.size(3, 9)
				.title("Select Color")
				.provider(new ColorSelectMenu(type, onClick))
				.build().open(player);
	}

	@Builder(buildMethodName = "_build")
	@AllArgsConstructor
	public static class ConfirmationMenu extends MenuUtils implements InventoryProvider {
		@Getter
		@Builder.Default
		private String title = "&4Are you sure?";
		@Builder.Default
		private String cancelText = "&cNo";
		private String cancelLore;
		@Builder.Default
		private String confirmText = "&aYes";
		private String confirmLore;
		@Builder.Default
		private Consumer<ItemClickData> onCancel = (e) -> e.getPlayer().closeInventory();
		@NonNull
		private Consumer<ItemClickData> onConfirm;

		public static class ConfirmationMenuBuilder {

			public void open(Player player) {
				Tasks.sync(() -> {
					ConfirmationMenu build = _build();
					SmartInventory.builder()
							.title(colorize(build.getTitle()))
							.provider(build)
							.size(3, 9)
							.build()
							.open(player);
				});
			}

			@Deprecated
			public ConfirmationMenu build() {
				throw new UnsupportedOperationException("Use open(player)");
			}

		}

		@Override
		public void init(Player player, InventoryContents contents) {
			ItemStack cancelItem = nameItem(Material.RED_CONCRETE, cancelText, cancelLore);
			ItemStack confirmItem = nameItem(Material.LIME_CONCRETE, confirmText, confirmLore);

			contents.set(1, 2, ClickableItem.from(cancelItem, (e) -> {
				if (onCancel != null)
					onCancel.accept(e);

				if (title.equals(e.getPlayer().getOpenInventory().getTitle()))
					e.getPlayer().closeInventory();
			}));

			contents.set(1, 6, ClickableItem.from(confirmItem, e -> {
				onConfirm.accept(e);

				if (colorize(title).equals(e.getPlayer().getOpenInventory().getTitle()))
					e.getPlayer().closeInventory();
			}));
		}
	}

	public void formatInventoryContents(InventoryContents contents, ItemStack[] inventory) {
		formatInventoryContents(contents, inventory, true);
	}

	public void formatInventoryContents(InventoryContents contents, ItemStack[] inventory, boolean editable) {
		ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		contents.set(4, 4, ClickableItem.empty(nameItem(redPane.clone(), "&eArmor ➝")));
		contents.set(4, 1, ClickableItem.empty(nameItem(redPane.clone(), "&e← Offhand")));
		contents.fillRect(4, 2, 4, 3, ClickableItem.empty(nameItem(redPane.clone(), "&e⬇ Hot Bar ⬇")));

		if (inventory == null || inventory.length == 0)
			return;

		// Hotbar
		for (int i = 0; i < 9; i++) {
			if (editable)
				contents.setEditable(SlotPos.of(5, i), true);

			if (inventory[i] == null)
				continue;

			contents.set(5, i, ClickableItem.empty(inventory[i]));
		}

		// Inventory
		int row = 1;
		int column = 0;
		for (int i = 9; i < 36; i++) {
			if (editable)
				contents.setEditable(SlotPos.of(row, column), true);

			if (inventory[i] != null)
				contents.set(row, column, ClickableItem.empty(inventory[i]));

			if (column != 8)
				++column;
			else {
				column = 0;
				++row;
			}
		}

		// Offhand
		if (editable)
			contents.setEditable(SlotPos.of(4, 0), true);

		if (inventory[40] != null)
			contents.set(4, 0, ClickableItem.empty(inventory[40]));

		// Armor
		column = 8;
		for (int i = 36; i < 40; i++) {
			if (editable)
				contents.setEditable(SlotPos.of(4, column), true);

			if (inventory[i] != null)
				contents.set(4, column, ClickableItem.empty(inventory[i]));
			--column;
		}
	}

}
