package me.pugabyte.bncore.features.trust.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.trust.Trust;
import me.pugabyte.bncore.models.trust.Trust.Type;
import me.pugabyte.bncore.models.trust.TrustService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TrustProvider extends MenuUtils implements InventoryProvider {
	private final Trust trust;
	private final Runnable back;
	private final TrustService service = new TrustService();
	private final AtomicReference<Trust.Type> filterType = new AtomicReference<>();

	public TrustProvider(Trust trust, Runnable back) {
		this.trust = trust;
		this.back = back;
	}

	public static void open(Player player) {
		open(player, 1);
	}

	public static void open(Player player, Runnable back) {
		open(player, 1, back, null);
	}

	public static void open(Player player, int page) {
		open(player, page, null, null);
	}

	public static void open(Player player, int page, Runnable back, InventoryProvider provider) {
		Trust trust = new TrustService().get(player);
		int rows = (int) Math.min(6, Math.ceil(trust.getAll().size() / 9) + 3);
		SmartInventory.builder()
				.provider(provider == null ? new TrustProvider(trust, back) : provider)
				.size(rows, 9)
				.title("Trusts")
				.build()
				.open(player, page);
	}

	public void refresh() {
		open(trust.getPlayer(), 1, back, this);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		if (back == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> back.run());

		List<ClickableItem> items = new ArrayList<>();

		trust.getAll().stream()
				.map(Utils::getPlayer)
				.sorted(Comparator.comparing(OfflinePlayer::getName))
				.collect(Collectors.toList())
				.forEach(_player -> {
					if (filterType.get() != null)
						if (!trust.get(filterType.get()).contains(_player.getUniqueId()))
							return;

					ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD)
							.skullOwner(_player)
							.name("&e" + _player.getName());
					for (Trust.Type type : Trust.Type.values())
						if (trust.get(type).contains(_player.getUniqueId()))
							builder.lore("&a" + type.camelCase());
						else
							builder.lore("&c" + type.camelCase());
					builder.lore("").lore("&fClick to edit");

					items.add(ClickableItem.from(builder.build(), e ->
							TrustPlayerProvider.open(player, _player)));
				});

		addPagination(player, contents, items);

		ItemBuilder add = new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&aAdd Trust");
		contents.set(0, 8, ClickableItem.from(add.build(), e ->
				BNCore.getSignMenuFactory().lines("", "^ ^ ^ ^ ^ ^", "Enter a", "player's name")
						.response(lines -> {
							if (lines[0].length() > 0) {
								OfflinePlayer trusted = Utils.getPlayer(lines[0]);
								TrustPlayerProvider.open(player, trusted);
							} else
								open(player);
						})
						.open(player)));

		Trust.Type previous = filterType.get() == null ? Type.values()[0].previousWithLoop() : filterType.get().previous();
		Trust.Type current = filterType.get();
		Trust.Type next = filterType.get() == null ? Type.values()[0] : filterType.get().next();
		if (current == previous) previous = null;
		if (current == next) next = null;

		ItemBuilder item = new ItemBuilder(Material.HOPPER).name("&6Filter by:")
				.lore("&7⬇ " + (previous == null ? "All" : previous.camelCase()))
				.lore("&e⬇ " + (current == null ? "All" : current.camelCase()))
				.lore("&7⬇ " + (next == null ? "All" : next.camelCase()));

		Trust.Type finalNext = next;
		contents.set(contents.inventory().getRows() - 1, 4, ClickableItem.from(item.build(), e -> {
			filterType.set(finalNext);
			refresh();
		}));
	}

	protected void addPagination(Player player, InventoryContents contents, List<ClickableItem> items) {
		Pagination page = contents.pagination();
		int perPage = 36;
		page.setItemsPerPage(perPage);
		page.setItems(items.toArray(new ClickableItem[0]));
		if (page.getPage() > items.size() / perPage)
			page.page(items.size() / perPage);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		int curPage = page.getPage() + 1;
		if (!page.isFirst())
			contents.set(5, 0, ClickableItem.from(nameItem(new ItemStack(Material.ARROW, Math.max(curPage - 1, 1)),
					"&fPrevious Page"), e -> open(player, page.previous().getPage())));
		if (!page.isLast())
			contents.set(5, 8, ClickableItem.from(nameItem(new ItemStack(Material.ARROW, curPage + 1),
					"&fNext Page"), e -> open(player, page.next().getPage())));
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}
