package gg.projecteden.nexus.features.trust.providers;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.TrustsUser;
import gg.projecteden.nexus.models.trust.TrustsUser.TrustType;
import gg.projecteden.nexus.models.trust.TrustsUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Set;
import java.util.UUID;

@Rows(4)
public class TrustsPlayerMenu extends InventoryProvider {
	private final TrustsUser user;
	private final TrustsUser editing;
	private final InventoryProvider back;
	private final TrustsUserService service = new TrustsUserService();

	public TrustsPlayerMenu(HasUniqueId user, HasUniqueId editing) {
		this(user, editing, null);
	}

	public TrustsPlayerMenu(HasUniqueId user, HasUniqueId editing, InventoryProvider back) {
		this.user = new TrustsUserService().get(user);;
		this.editing = new TrustsUserService().get(editing);
		this.back = back;
	}

	@Override
	public String getTitle() {
		if (viewer.getUniqueId().equals(user.getUuid()))
			return Nickname.of(editing);
		else
			return Nickname.of(user) + "'s Trusts - " + Nickname.of(editing);
	}

	@Override
	public void init() {
		addBackOrCloseItem(back);

		contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(editing).name("&f" + Nickname.of(editing)).build()));

		for (TrustType type : TrustType.values()) {
			Set<UUID> list = user.get(type);

			ItemBuilder builder = new ItemBuilder(type.getDisplayItem()).name("&e" + type.camelCase());
			if (list.contains(editing.getUniqueId()))
				builder.lore("&aTrusted");
			else
				builder.lore("&cNot trusted");
			builder.lore("").lore("&fClick to toggle");

			int column = type.getColumn();

			contents.set(2, column, ClickableItem.of(builder.build(), e -> {
				if (list.contains(editing.getUniqueId()))
					list.remove(editing.getUniqueId());
				else
					list.add(editing.getUniqueId());
				service.save(user);
				open(viewer, contents.pagination());
			}));
		}

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.TNT).name("&cUntrust from all").build(), e ->
			ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					for (TrustType type : TrustType.values()) {
						user.remove(type, editing.getUniqueId());
						service.save(user);
						new TrustsMenu(user, back).open(viewer);
					}
				})
				.onCancel(e2 -> open(viewer, contents.pagination()))
				.open(viewer)));
	}

}
