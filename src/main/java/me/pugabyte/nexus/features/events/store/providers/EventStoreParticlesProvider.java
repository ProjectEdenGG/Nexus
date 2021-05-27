package me.pugabyte.nexus.features.events.store.providers;

import eden.utils.TimeUtils.Time;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.store.Purchasable;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.models.particle.ParticleType;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.events.Events.STORE_PREFIX;

@AllArgsConstructor
public class EventStoreParticlesProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.title("Event Store - Particles")
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
		int price = Purchasable.PARTICLES.getPrice();

		List<ClickableItem> items = new ArrayList<>();

		for (ParticleType type : EnumUtils.valuesExcept(ParticleType.class, ParticleType.WINGS)) {
			if (particleOwner.canUse(type))
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
						type.run(player);
						Tasks.wait(Time.SECOND.x(15), particleOwner::cancel);
					}
				} catch (Exception ex) {
					handleException(player, STORE_PREFIX, ex);
				}
			}));
		}

		addPagination(player, contents, items);
	}
}
