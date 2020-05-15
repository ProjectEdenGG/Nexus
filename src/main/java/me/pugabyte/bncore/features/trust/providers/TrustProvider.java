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
import java.util.UUID;
import java.util.stream.Collectors;

public class TrustProvider extends MenuUtils implements InventoryProvider {
	private Trust trust;
	private TrustService service = new TrustService();

	public TrustProvider(Trust trust) {
		this.trust = trust;
	}

	public static void open(Player player) {
		open(player, 1);
	}

	public static void open(Player player, int page) {
		Trust trust = new TrustService().get(player);
		int rows = (int) Math.min(6, Math.ceil(trust.getAll().size() / 9) + 3);
		SmartInventory.builder()
				.provider(new TrustProvider(trust))
				.size(rows, 9)
				.title("Trusts")
				.build()
				.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		List<ClickableItem> items = new ArrayList<>();

		trust.getAll().stream()
				.sorted(Comparator.comparing(UUID::toString))
				.map(Utils::getPlayer)
				.collect(Collectors.toList())
				.forEach(_player -> {
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

		addPagination(player, contents, items);
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
