package me.pugabyte.nexus.features.events.store.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.store.Purchasable;
import me.pugabyte.nexus.features.store.perks.emojihats.EmojiHat;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.events.Events.STORE_PREFIX;

@AllArgsConstructor
public class EventStoreEmojiHatProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.title("Event Store - Emoji Hats")
				.size(6, 9)
				.provider(this)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		int price = Purchasable.EMOJI_HATS.getPrice();

		List<ClickableItem> items = new ArrayList<>();

		for (EmojiHat type : EmojiHat.values()) {
			if (type.canUse(player))
				continue;

			ItemBuilder item = type.getDisplayItem()
					.lore("Click to test")
					.lore("Shift+click to buy");

			items.add(ClickableItem.from(item.build(), e -> {
				try {
					if (isShiftClick(e))
						chargeAndAddPermission(player, price, type.getPermission());
					else {
						player.closeInventory();
						type.runSelf(player);
					}
				} catch (Exception ex) {
					handleException(player, STORE_PREFIX, ex);
				}
			}));
		}

		addPagination(player, contents, items);
	}

}
