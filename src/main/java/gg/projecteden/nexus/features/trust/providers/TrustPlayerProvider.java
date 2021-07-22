package gg.projecteden.nexus.features.trust.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Name;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TrustPlayerProvider extends MenuUtils implements InventoryProvider {
	private final Trust trust;
	private final OfflinePlayer trusted;
	private final TrustService service = new TrustService();

	public TrustPlayerProvider(Trust trust, OfflinePlayer trusted) {
		this.trust = trust;
		this.trusted = trusted;
	}

	public static void open(Player player, OfflinePlayer trusted) {
		SmartInventory.builder()
				.provider(new TrustPlayerProvider(new TrustService().get(player), trusted))
				.size(4, 9)
				.title(Name.of(trusted))
				.build()
				.open(player);
	}

	public void refresh() {
		open(trust.getOnlinePlayer(), trusted);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> TrustProvider.openMenu(player));

		contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(trusted).name("&f" + Name.of(trusted)).build()));

		for (Trust.Type type : Trust.Type.values()) {
			List<UUID> list = trust.get(type);

			ItemBuilder builder = new ItemBuilder(type.getMaterial()).name("&e" + type.camelCase());
			if (list.contains(trusted.getUniqueId()))
				builder.lore("&aTrusted");
			else
				builder.lore("&cNot trusted");
			builder.lore("").lore("&fClick to toggle");

			contents.set(2, type.getColumn(), ClickableItem.from(builder.build(), e -> {
				if (list.contains(trusted.getUniqueId()))
					list.remove(trusted.getUniqueId());
				else
					list.add(trusted.getUniqueId());
				service.save(trust);
				refresh();
			}));
		}

		contents.set(0, 8, ClickableItem.from(new ItemBuilder(Material.TNT).name("&cUntrust from all").build(), e ->
				ConfirmationMenu.builder()
						.onConfirm(e2 -> {
							for (Type type : Type.values()) {
								trust.get(type).remove(trusted.getUniqueId());
								service.save(trust);
								TrustProvider.openMenu(player);
							}
						})
						.onCancel(e2 -> refresh())
						.open(player)));
	}

}
