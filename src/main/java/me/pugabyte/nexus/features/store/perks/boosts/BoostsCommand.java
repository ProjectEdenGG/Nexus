package me.pugabyte.nexus.features.store.perks.boosts;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.boost.BoostConfig;
import me.pugabyte.nexus.models.boost.BoostConfigService;
import me.pugabyte.nexus.models.boost.Boostable;
import me.pugabyte.nexus.models.boost.Booster;
import me.pugabyte.nexus.models.boost.Booster.Boost;
import me.pugabyte.nexus.models.boost.BoosterService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoostsCommand extends CustomCommand {

	public BoostsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void menu() {
		new BoostMenu().open(player());
	}

	@AllArgsConstructor
	private static class BoostMenu extends MenuUtils implements InventoryProvider {
		private final Boostable type;
		private final BoostMenu previousMenu;

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.provider(this)
					.title("Boosts")
					.size(6, 9)
					.build()
					.open(viewer, page);
		}

		public BoostMenu() {
			this(null);
		}

		public BoostMenu(Boostable type) {
			this.type = type;
			this.previousMenu = null;
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			final BoostConfigService configService = new BoostConfigService();
			final BoostConfig config = configService.get();
			final BoosterService service = new BoosterService();
			final Booster booster = service.get(player);

			addCloseItem(contents);

			List<ClickableItem> items = new ArrayList<>();
			if (type == null)
				for (Boostable boostable : Boostable.values())
					items.add(ClickableItem.from(boostable.getDisplayItem().build(), e -> new BoostMenu(boostable, this).open(player)));
			else
				for (Boost boost : booster.get(type)) {
					ItemStack item = boost.getDisplayItem().build();
					if (boost.isActive())
						contents.set(0, 4, ClickableItem.empty(item));
					else if (!boost.isExpired())
						items.add(ClickableItem.from(item, e -> {
							// TODO
						}));
				}


			addPagination(player, contents, items);
		}

	}

}
