package gg.projecteden.nexus.features.trust.providers;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TrustPlayerProvider extends InventoryProvider {
	private final OfflinePlayer trusted;
	private final TrustService service = new TrustService();

	public TrustPlayerProvider(OfflinePlayer trusted) {
		this.trusted = trusted;
	}

	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.title(Nickname.of(trusted))
			.rows(4)
			.build()
			.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Trust trust = service.get(player);
		addBackItem(contents, e -> new TrustProvider().open(player));

		contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(trusted).name("&f" + Nickname.of(trusted)).build()));

		for (Trust.Type type : Trust.Type.values()) {
			List<UUID> list = trust.get(type);

			ItemBuilder builder = new ItemBuilder(type.getMaterial()).name("&e" + type.camelCase());
			if (list.contains(trusted.getUniqueId()))
				builder.lore("&aTrusted");
			else
				builder.lore("&cNot trusted");
			builder.lore("").lore("&fClick to toggle");

			// TODO Decorations
			int column = type.getColumn();
			if (!Rank.of(player).isStaff()) {
				++column;
				if (type == Type.DECORATIONS)
					continue;
			}
			//

			contents.set(2, column, ClickableItem.of(builder.build(), e -> {
				if (list.contains(trusted.getUniqueId()))
					list.remove(trusted.getUniqueId());
				else
					list.add(trusted.getUniqueId());
				service.save(trust);
				open(player, contents.pagination());
			}));
		}

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.TNT).name("&cUntrust from all").build(), e ->
			ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					for (Type type : Type.values()) {
						trust.get(type).remove(trusted.getUniqueId());
						service.save(trust);
						new TrustProvider().open(player);
					}
				})
				.onCancel(e2 -> open(player, contents.pagination()))
				.open(player)));
	}

}
