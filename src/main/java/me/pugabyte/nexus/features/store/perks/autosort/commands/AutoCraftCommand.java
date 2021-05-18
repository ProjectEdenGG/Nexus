package me.pugabyte.nexus.features.store.perks.autosort.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.store.perks.autosort.features.AutoCraft;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.autosort.AutoSortUserService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Permission("store.autosort")
public class AutoCraftCommand extends CustomCommand {

	public AutoCraftCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void edit() {
		new AutoCraftEditor().open(player());
	}

	private static class AutoCraftEditor extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.provider(this)
					.title("AutoCraft Editor")
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
			for (Material material : AutoCraft.getAutoCraftable().keySet()) {
				ItemBuilder item = new ItemBuilder(material);

				if (!user.getAutoCraftExclude().contains(material))
					item.lore("&aEnabled").glow();
				else
					item.lore("&cDisabled");

				item.lore("", "&f" + AutoCraft.getIngredients(material).stream()
						.map(StringUtils::pretty)
						.collect(joining(", ")));

				items.add(ClickableItem.from(item.build(), e -> {
					if (user.getAutoCraftExclude().contains(material))
						user.getAutoCraftExclude().remove(material);
					else
						user.getAutoCraftExclude().add(material);

					service.save(user);

					open(player, contents.pagination().getPage());
				}));
			}

			addPagination(player, contents, items);
		}

	}

}
