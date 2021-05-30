package me.pugabyte.nexus.features.events.store.providers.purchasable;

import fr.minuskube.inv.ClickableItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.store.EventStoreItem;
import me.pugabyte.nexus.features.events.store.providers.EventStoreMenu;
import me.pugabyte.nexus.features.store.perks.emojihats.EmojiHat;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.events.Events.STORE_PREFIX;

@AllArgsConstructor
public class EventStoreEmojiHatProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	@Override
	protected String getTitle() {
		return "Event Store - Emoji Hats";
	}

	@NotNull
	@Override
	protected List<ClickableItem> getItems(Player player) {
		List<ClickableItem> items = new ArrayList<>();

		int price = EventStoreItem.EMOJI_HATS.getPrice();

		for (EmojiHat type : EmojiHat.values()) {
			if (type.canBeUsedBy(player))
				continue;

			ItemBuilder item = type.getDisplayItem();
			lore(player, item, price);

			items.add(ClickableItem.from(item.build(), e -> {
				try {
					if (isShiftClick(e))
						chargeAndAddPermissions(player, price, type.getPermission());
					else {
						player.closeInventory();
						type.runSelf(player);
					}
				} catch (Exception ex) {
					handleException(player, STORE_PREFIX, ex);
				}
			}));
		}
		return items;
	}

}
