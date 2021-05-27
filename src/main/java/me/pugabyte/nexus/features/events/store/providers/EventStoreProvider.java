package me.pugabyte.nexus.features.events.store.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.store.Purchasable;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class EventStoreProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	public EventStoreProvider() {
		this(null);
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.title("Event Store")
				.size(5, 9)
				.provider(this)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		for (Purchasable purchasable : Purchasable.values()) {
			ItemBuilder item = purchasable.getRawDisplayItem();
			contents.set(purchasable.getSlot(), ClickableItem.from(item.build(), e -> purchasable.onClick(player, this)));
		}
	}

}
