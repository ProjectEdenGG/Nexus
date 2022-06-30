package gg.projecteden.nexus.features.events.store.providers.purchasable;

import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.features.events.store.providers.EventStoreMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.events.Events.STORE_PREFIX;
import static gg.projecteden.nexus.features.menus.MenuUtils.handleException;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@AllArgsConstructor
@Title("Event Store - Particles")
public class EventStoreParticlesProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	@NotNull
	@Override
	protected List<ClickableItem> getItems(Player player) {
		List<ClickableItem> items = new ArrayList<>();

		ParticleOwner particleOwner = new ParticleService().get(player);
		int price = EventStoreItem.PARTICLES.getPrice();

		for (ParticleType type : EnumUtils.valuesExcept(ParticleType.class, ParticleType.WINGS)) {
			if (particleOwner.canUse(type))
				continue;

			ItemBuilder item = type.getDisplayItem();
			lore(player, item, price);

			items.add(ClickableItem.of(item.build(), e -> {
				try {
					if (e.isShiftClick()) {
						chargeAndAddPermissions(player, price, type.getPermission());
						PlayerUtils.send(player, STORE_PREFIX + "Purchased " + camelCase(type) + " particle, manage with &c/particles");
						open(player);
					} else {
						player.closeInventory();
						type.run(player);
						Tasks.wait(TickTime.SECOND.x(15), () -> particleOwner.cancel(type));
					}
				} catch (Exception ex) {
					handleException(player, STORE_PREFIX, ex);
				}
			}));
		}
		return items;
	}

}
