package me.pugabyte.bncore.features.trust.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.trust.Trust;
import me.pugabyte.bncore.models.trust.Trust.Type;
import me.pugabyte.bncore.models.trust.TrustService;
import me.pugabyte.bncore.utils.ItemBuilder;
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
				.title(trusted.getName())
				.build()
				.open(player);
	}

	public void refresh() {
		open(trust.getPlayer(), trusted);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> TrustProvider.open(player));

		contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(trusted).name("&f" + trusted.getName()).build()));

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
				MenuUtils.confirmMenu(player, ConfirmationMenu.builder()
						.onConfirm(e2 -> {
							for (Type type : Type.values()) {
								trust.get(type).remove(trusted.getUniqueId());
								service.save(trust);
								TrustProvider.open(player);
							}
						}).onCancel(e2 -> refresh())
						.build())));
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}
