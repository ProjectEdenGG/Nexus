package me.pugabyte.nexus.features.events.store.providers;

import fr.minuskube.inv.ClickableItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.store.EventStoreItem;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EventStoreProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	public EventStoreProvider() {
		this(null);
	}

	@Override
	protected String getTitle() {
		return "Event Store";
	}

	@Override
	protected @NotNull List<ClickableItem> getItems(Player player) {
		List<ClickableItem> items = new ArrayList<>();

		for (EventStoreItem eventStoreItem : EventStoreItem.values()) {
			ItemBuilder item = eventStoreItem.getDisplayItem();
			items.add(ClickableItem.from(item.build(), e -> eventStoreItem.onClick(player, this)));
		}

		return items;
	}

}
