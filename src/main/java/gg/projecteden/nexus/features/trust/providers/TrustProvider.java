package gg.projecteden.nexus.features.trust.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.trust.TrustFeature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.features.menus.MenuUtils.getRows;
import static gg.projecteden.nexus.features.menus.SignMenuFactory.ARROWS;

@RequiredArgsConstructor
public class TrustProvider extends InventoryProvider {
	private final InventoryProvider back;
	private final AtomicReference<Trust.Type> filterType = new AtomicReference<>();

	public TrustProvider() {
		back = null;
	}

	@Override
	public void open(Player player, int page) {
		Trust trust = new TrustService().get(player);
		SmartInventory.builder()
			.provider(this)
			.title("Trusts")
			.rows(getRows(trust.getAll().size(), 3))
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Trust trust = new TrustService().get(player);
		if (back == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> back.open(player));

		List<ClickableItem> items = new ArrayList<>();

		trust.getAll().stream()
			.map(PlayerUtils::getPlayer)
			.sorted(Comparator.comparing(Nickname::of))
			.toList()
			.forEach(trusted -> {
				if (filterType.get() != null)
					if (!trust.trusts(filterType.get(), trusted))
						return;

				ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD)
					.skullOwner(trusted)
					.name("&e" + Nickname.of(trusted));
				for (Trust.Type type : Trust.Type.values()) {
					// TODO Decorations
					if (type.equals(Type.DECORATIONS) && !Rank.of(player).isStaff())
						continue;
					//

					if (trust.trusts(type, trusted))
						builder.lore("&a" + type.camelCase());
					else
						builder.lore("&c" + type.camelCase());
				}

				builder.lore("").lore("&fClick to edit");

				items.add(ClickableItem.of(builder.build(), e ->
					new TrustPlayerProvider(trusted).open(player)));
			});

		paginator(player, contents, items).build();

		ItemBuilder add = new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&aAdd Trust");
		contents.set(0, 8, ClickableItem.of(add.build(), e ->
			Nexus.getSignMenuFactory().lines("", ARROWS, "Enter a", "player's name")
				.prefix(Features.get(TrustFeature.class).getPrefix())
				.response(lines -> {
					if (lines[0].length() > 0) {
						OfflinePlayer trusted = PlayerUtils.getPlayer(lines[0]);
						new TrustPlayerProvider(trusted).open(player);
					} else
						open(player, contents.pagination());
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
		contents.set(contents.inventory().getRows() - 1, 4, ClickableItem.of(item.build(), e -> {
			filterType.set(finalNext);
			open(player, contents.pagination());
		}));
	}

}
