package me.pugabyte.nexus.features.events.store.providers;

import eden.utils.TimeUtils.Time;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.store.Purchasable;
import me.pugabyte.nexus.features.particles.effects.WingsEffect.WingStyle;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.models.particle.ParticleType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.events.Events.STORE_PREFIX;

@AllArgsConstructor
public class EventStoreWingsProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.title("Event Store - Wings")
				.size(6, 9)
				.provider(this)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		ParticleService service = new ParticleService();
		ParticleOwner particleOwner = service.get(player);
		int price = Purchasable.PARTICLE_WINGS.getPrice();

		List<ClickableItem> items = new ArrayList<>();

		for (WingStyle style : WingStyle.values()) {
			if (style.canBeUsedBy(player))
				continue;

			ItemBuilder item = style.getDisplayItem();
			lore(player, item, price);

			items.add(ClickableItem.from(item.build(), e -> {
				try {
					if (isShiftClick(e))
						chargeAndAddPermissions(player, price, "wings.use", style.getPermission());
					else {
						player.closeInventory();
						style.preview(player);
						Tasks.wait(Time.SECOND.x(15), () -> particleOwner.cancel(ParticleType.WINGS));
					}
				} catch (Exception ex) {
					handleException(player, STORE_PREFIX, ex);
				}
			}));
		}

		addPagination(player, contents, items);
	}
}
