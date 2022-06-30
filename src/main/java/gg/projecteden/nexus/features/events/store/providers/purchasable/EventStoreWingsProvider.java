package gg.projecteden.nexus.features.events.store.providers.purchasable;

import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.features.events.store.providers.EventStoreMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.particles.effects.WingsEffect.WingStyle;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.events.Events.STORE_PREFIX;
import static gg.projecteden.nexus.features.menus.MenuUtils.handleException;

@AllArgsConstructor
@Title("Event Store - Wings")
public class EventStoreWingsProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	@NotNull
	@Override
	protected List<ClickableItem> getItems(Player player) {
		List<ClickableItem> items = new ArrayList<>();

		ParticleService service = new ParticleService();
		ParticleOwner particleOwner = service.get(player);
		int price = EventStoreItem.WINGS.getPrice();

		for (WingStyle style : WingStyle.values()) {
			if (style.canBeUsedBy(player))
				continue;

			ItemBuilder item = style.getDisplayItem();
			lore(player, item, price);

			items.add(ClickableItem.of(item.build(), e -> {
				try {
					if (e.isShiftClick()) {
						chargeAndAddPermissions(player, price, "wings.use", style.getPermission());
						PlayerUtils.send(player, STORE_PREFIX + "Purchased wing style #" + (style.ordinal() + 1) + ", manage with &c/wings");
						open(player);
					} else {
						player.closeInventory();
						style.preview(player);
						Tasks.wait(TickTime.SECOND.x(15), () -> particleOwner.cancel(ParticleType.WINGS));
					}
				} catch (Exception ex) {
					handleException(player, STORE_PREFIX, ex);
				}
			}));
		}
		return items;
	}

}
