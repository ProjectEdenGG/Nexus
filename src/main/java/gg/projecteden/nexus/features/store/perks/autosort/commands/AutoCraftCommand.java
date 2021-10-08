package gg.projecteden.nexus.features.store.perks.autosort.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.store.perks.autosort.features.AutoCraft;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.autosort.AutoSortUser;
import gg.projecteden.nexus.models.autosort.AutoSortUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
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
		public void open(Player player, int page) {
			SmartInventory.builder()
					.provider(this)
					.title("AutoCraft Editor")
					.size(6, 9)
					.build()
					.open(player, page);
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

			paginator(player, contents, items);
		}

	}

}
