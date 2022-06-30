package gg.projecteden.nexus.features.events.store.providers;

import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
	public String getTitle() {
		return "Event Store";
	}

	@Override
	protected @NotNull List<ClickableItem> getItems(Player player) {
		List<ClickableItem> items = new ArrayList<>();

		for (EventStoreItem eventStoreItem : EventStoreItem.values()) {
			if (!eventStoreItem.canView(player))
				continue;

			ItemBuilder item = eventStoreItem.getDisplayItem();
			items.add(ClickableItem.of(item.build(), e -> eventStoreItem.onClick(player, this)));
		}

		return items;
	}

}
