package gg.projecteden.nexus.features.events.store.providers.purchasable;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.features.events.store.providers.EventStoreMenu;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.store.perks.visuals.EmojiHatsCommand.EmojiHat;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
						PlayerUtils.send(player, EdenEvent.PREFIX_STORE + "Purchased &e" + StringUtils.camelCase(type) + "&3, use with &c/emojihats " + type.name().toLowerCase());
						open(player);
					} else {
						player.closeInventory();
						type.runSelf(player);
						PlayerUtils.send(player, EdenEvent.PREFIX_STORE + "Use F5 to view the emoji");
					}
				} catch (Exception ex) {
					MenuUtils.handleException(player, EdenEvent.PREFIX_STORE, ex);
				}
			}));
		}
		return items;
	}

}
