package gg.projecteden.nexus.features.trust.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.trust.TrustFeature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Title("Trusts")
@RequiredArgsConstructor
public class TrustProvider extends InventoryProvider {
	private final InventoryProvider back;
	private final AtomicReference<Trust.Type> filterType = new AtomicReference<>();

	public TrustProvider() {
		back = null;
	}

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(new TrustService().get(viewer).getAll().size(), 3);
	}

	@Override
	public void init() {
		Trust trust = new TrustService().get(viewer);
		if (back == null)
			addCloseItem();
		else
			addBackItem(e -> back.open(viewer));

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
					if (trust.trusts(type, trusted))
						builder.lore("&f✅ &a" + type.camelCase());
					else
						builder.lore("&f❌ &c" + type.camelCase());
				}

				builder.lore("").lore("&fClick to edit");

				items.add(ClickableItem.of(builder.build(), e ->
					new TrustPlayerProvider(trusted, back).open(viewer)));
			});

		paginate(items);

		ItemBuilder add = new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&aAdd Trust");
		contents.set(0, 8, ClickableItem.of(add.build(), e ->
			Nexus.getSignMenuFactory().lines("", SignMenuFactory.ARROWS, "Enter a", "player's name")
				.prefix(Features.get(TrustFeature.class).getPrefix())
				.response(lines -> {
					if (!lines[0].isEmpty()) {
						OfflinePlayer trusted = PlayerUtils.getPlayer(lines[0]);
						new TrustPlayerProvider(trusted, back).open(viewer);
					} else
						open(viewer, contents.pagination());
				})
				.open(viewer)));

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
		contents.set(contents.config().getRows() - 1, 4, ClickableItem.of(item.build(), e -> {
			filterType.set(finalNext);
			open(viewer, contents.pagination());
		}));
	}

}
