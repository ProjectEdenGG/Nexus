package gg.projecteden.nexus.features.trust.providers;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.dialog.DialogCommand.SingleInputDialogBuilder;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.TrustsUser;
import gg.projecteden.nexus.models.trust.TrustsUser.TrustType;
import gg.projecteden.nexus.models.trust.TrustsUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Title("Trusts")
@RequiredArgsConstructor
public class TrustsMenu extends InventoryProvider {
	private final TrustsUser user;
	private final InventoryProvider back;
	private final AtomicReference<TrustType> filterType = new AtomicReference<>();

	public TrustsMenu(HasUniqueId user) {
		this(user, null);
	}

	public TrustsMenu(HasUniqueId user, InventoryProvider back) {
		super();
		this.user = new TrustsUserService().get(user);
		this.back = back;
	}

	@Override
	public String getTitle() {
		if (viewer.getUniqueId().equals(user.getUuid()))
			return "Your Trusts";
		else
			return Nickname.of(user) + "'s Trusts";
	}

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(user.getAll().size(), 3);
	}

	@Override
	public void init() {
		if (back == null)
			addCloseItem();
		else
			addBackItem(e -> back.open(viewer));

		List<ClickableItem> items = new ArrayList<>();

		user.getAll().stream()
			.map(PlayerUtils::getPlayer)
			.sorted(Comparator.comparing(Nickname::of))
			.toList()
			.forEach(target -> {
				if (filterType.get() != null)
					if (!user.trusts(filterType.get(), target))
						return;

				ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD)
					.skullOwner(target)
					.name("&e" + Nickname.of(target));
				for (TrustType type : TrustType.values()) {
					if (user.trusts(type, target))
						builder.lore("&f✅ &a" + type.camelCase());
					else
						builder.lore("&f❌ &c" + type.camelCase());
				}

				builder.lore("").lore("&fClick to edit");

				items.add(ClickableItem.of(builder.build(), e ->
					new TrustsPlayerMenu(user, target, this).open(viewer)));
			});

		paginate(items);

		ItemBuilder add = new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&aAdd Trust");
		contents.set(0, 8, ClickableItem.of(add.build(), e ->
			SingleInputDialogBuilder.builder()
				.title("Edit Trusts")
				.body("Enter a player's name to edit their trusts")
				.label("Username")
				.onConfirm(lookup -> {
					try {
						new TrustsPlayerMenu(user, PlayerUtils.getPlayer(lookup), this).open(viewer);
					} catch (Exception ex) {
						MenuUtils.handleException(viewer, StringUtils.getPrefix("Trusts"), ex);
					}
				})
				.onCancel(() -> open(viewer, contents.pagination()))
				.show(viewer)));

		TrustType previous = filterType.get() == null ? TrustType.values()[0].previousWithLoop() : filterType.get().previous();
		TrustType current = filterType.get();
		TrustType next = filterType.get() == null ? TrustType.values()[0] : filterType.get().next();
		if (current == previous) previous = null;
		if (current == next) next = null;

		ItemBuilder item = new ItemBuilder(Material.HOPPER).name("&6Filter by:")
			.lore("&7⬇ " + (previous == null ? "All" : previous.camelCase()))
			.lore("&e⬇ " + (current == null ? "All" : current.camelCase()))
			.lore("&7⬇ " + (next == null ? "All" : next.camelCase()));

		TrustType finalNext = next;
		contents.set(contents.config().getRows() - 1, 4, ClickableItem.of(item.build(), e -> {
			filterType.set(finalNext);
			open(viewer, contents.pagination());
		}));
	}

}
