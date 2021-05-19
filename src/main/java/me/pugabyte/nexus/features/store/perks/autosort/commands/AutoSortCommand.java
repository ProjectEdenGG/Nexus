package me.pugabyte.nexus.features.store.perks.autosort.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.store.perks.autosort.AutoSortFeature;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.autosort.AutoSortUser.AutoSortInventoryType;
import me.pugabyte.nexus.models.autosort.AutoSortUserService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class AutoSortCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "store.autosort";
	private final AutoSortUserService service = new AutoSortUserService();
	private AutoSortUser user;

	public AutoSortCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("<feature> [enable]")
	void toggle(AutoSortFeature feature, Boolean enable) {
		feature.checkPermission(player());

		if (enable == null)
			enable = !user.hasFeatureEnabled(feature);

		if (enable)
			if (!user.getDisabledFeatures().contains(feature))
				error(feature + " is already enabled");
			else
				user.getDisabledFeatures().remove(feature);
		else
			if (user.getDisabledFeatures().contains(feature))
				error(feature + " is already disabled");
			else
				user.getDisabledFeatures().add(feature);

		service.save(user);
		send(PREFIX + feature + " " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("inventoryTypes")
	void types() {
		new AutoSortInventoryTypeEditor().open(player());
	}

	private static class AutoSortInventoryTypeEditor extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.provider(this)
					.title("AutoSort Inventory Editor")
					.size(6, 9)
					.build()
					.open(viewer, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			final AutoSortUserService service = new AutoSortUserService();
			final AutoSortUser user = service.get(player);

			addCloseItem(contents);

			List<ClickableItem> items = new ArrayList<>();
			for (AutoSortInventoryType inventoryType : AutoSortInventoryType.values()) {
				Material material = inventoryType.getMaterial();
				int customModelData = inventoryType.getCustomModelData();

				ItemBuilder item = new ItemBuilder(material).name(StringUtils.camelCase(inventoryType));
				if (customModelData > 0)
					item.customModelData(customModelData);

				if (!user.getDisabledInventoryTypes().contains(inventoryType))
					item.lore("&aEnabled");
				else
					item.lore("&cDisabled");

				items.add(ClickableItem.from(item.build(), e -> {
					if (user.getDisabledInventoryTypes().contains(inventoryType))
						user.getDisabledInventoryTypes().remove(inventoryType);
					else
						user.getDisabledInventoryTypes().add(inventoryType);

					service.save(user);

					open(player, contents.pagination().getPage());
				}));
			}

			addPagination(player, contents, items);
		}

	}

}
