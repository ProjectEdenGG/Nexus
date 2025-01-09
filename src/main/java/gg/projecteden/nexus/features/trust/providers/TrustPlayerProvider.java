package gg.projecteden.nexus.features.trust.providers;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.UUID;

@Rows(4)
public class TrustPlayerProvider extends InventoryProvider {
	private InventoryProvider back;
	private final OfflinePlayer trusted;
	private final TrustService service = new TrustService();

	public TrustPlayerProvider(OfflinePlayer trusted, InventoryProvider back) {
		this(trusted);
		this.back = back;
	}

	public TrustPlayerProvider(OfflinePlayer trusted) {
		this.trusted = trusted;
	}

	@Override
	public String getTitle() {
		return Nickname.of(trusted);
	}

	@Override
	public void init() {
		addBackOrCloseItem(back);

		Trust trust = service.get(viewer);

		contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(trusted).name("&f" + Nickname.of(trusted)).build()));

		for (Trust.Type type : Trust.Type.values()) {
			Set<UUID> list = trust.get(type);

			ItemBuilder builder = new ItemBuilder(type.getDisplayItem()).name("&e" + type.camelCase());
			if (list.contains(trusted.getUniqueId()))
				builder.lore("&aTrusted");
			else
				builder.lore("&cNot trusted");
			builder.lore("").lore("&fClick to toggle");

			int column = type.getColumn();

			contents.set(2, column, ClickableItem.of(builder.build(), e -> {
				if (list.contains(trusted.getUniqueId()))
					list.remove(trusted.getUniqueId());
				else
					list.add(trusted.getUniqueId());
				service.save(trust);
				open(viewer, contents.pagination());
			}));
		}

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.TNT).name("&cUntrust from all").build(), e ->
			ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					for (Type type : Type.values()) {
						trust.remove(type, trusted.getUniqueId());
						service.save(trust);
						new TrustProvider(back).open(viewer);
					}
				})
				.onCancel(e2 -> open(viewer, contents.pagination()))
				.open(viewer)));
	}

}
