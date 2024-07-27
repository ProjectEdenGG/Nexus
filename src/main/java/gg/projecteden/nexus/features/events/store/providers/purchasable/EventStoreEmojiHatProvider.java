package gg.projecteden.nexus.features.events.store.providers.purchasable;

import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.features.events.store.providers.EventStoreMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.store.perks.visuals.EmojiHatsCommand.EmojiHat;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.events.EdenEvent.PREFIX_STORE;
import static gg.projecteden.nexus.features.menus.MenuUtils.handleException;
import static gg.projecteden.nexus.utils.PlayerUtils.send;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@AllArgsConstructor
@Title("Event Store - Emoji Hats")
public class EventStoreEmojiHatProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

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

			items.add(ClickableItem.of(item.build(), e -> {
				try {
					if (e.isShiftClick()) {
						chargeAndAddPermissions(player, price, type.getPermission());
						send(player, PREFIX_STORE + "Purchased &e" + camelCase(type) + "&3, use with &c/emojihats " + type.name().toLowerCase());
						open(player);
					} else {
						player.closeInventory();
						type.runSelf(player);
						send(player, PREFIX_STORE + "Use F5 to view the emoji");
					}
				} catch (Exception ex) {
					handleException(player, PREFIX_STORE, ex);
				}
			}));
		}
		return items;
	}

}
