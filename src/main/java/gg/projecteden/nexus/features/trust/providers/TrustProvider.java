package gg.projecteden.nexus.features.trust.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.trust.TrustFeature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.menus.SignMenuFactory.ARROWS;

public class TrustProvider extends MenuUtils implements InventoryProvider {
	private final Trust trust;
	private final Runnable back;
	private final AtomicReference<Trust.Type> filterType = new AtomicReference<>();

	public TrustProvider(Trust trust, Runnable back) {
		this.trust = trust;
		this.back = back;
	}

	// TODO Comply with MenuUtils
	public static void openMenu(Player player) {
		openMenu(player, null);
	}

	public static void openMenu(Player player, Runnable back) {
		openMenu(player, 0, back, null);
	}

	public static void openMenu(Player player, int page, Runnable back, InventoryProvider provider) {
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
		openMenu(trust.getOnlinePlayer(), 1, back, this);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		if (back == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> back.run());

		List<ClickableItem> items = new ArrayList<>();

		trust.getAll().stream()
				.map(PlayerUtils::getPlayer)
				.sorted(Comparator.comparing(Nickname::of))
				.collect(Collectors.toList())
				.forEach(_player -> {
					if (filterType.get() != null)
						if (!trust.trusts(filterType.get(), _player))
							return;

					ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD)
							.skullOwner(_player)
							.name("&e" + Nickname.of(_player));
					for (Trust.Type type : Trust.Type.values())
						if (trust.trusts(type, _player))
							builder.lore("&a" + type.camelCase());
						else
							builder.lore("&c" + type.camelCase());
					builder.lore("").lore("&fClick to edit");

					items.add(ClickableItem.from(builder.build(), e ->
							TrustPlayerProvider.open(player, _player)));
				});

		paginator(player, contents, items);

		ItemBuilder add = new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&aAdd Trust");
		contents.set(0, 8, ClickableItem.from(add.build(), e ->
				Nexus.getSignMenuFactory().lines("", ARROWS, "Enter a", "player's name")
						.prefix(Features.get(TrustFeature.class).getPrefix())
						.response(lines -> {
							if (lines[0].length() > 0) {
								OfflinePlayer trusted = PlayerUtils.getPlayer(lines[0]);
								TrustPlayerProvider.open(player, trusted);
							} else
								openMenu(player);
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

}
