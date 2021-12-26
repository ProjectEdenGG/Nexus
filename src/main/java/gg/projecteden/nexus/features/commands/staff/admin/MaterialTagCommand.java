package gg.projecteden.nexus.features.commands.staff.admin;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Permission(Group.ADMIN)
public class MaterialTagCommand extends CustomCommand {

	public MaterialTagCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("materials <tag>")
	void materials(Tag<Material> tag) {
		new MaterialTagMaterialsMenu(tag).open(player());
	}

	@Path("find <material>")
	void materialTag(Material material) {
		send(PREFIX + "Applicable tags: &e" + String.join("&3, &e", MaterialTag.getApplicable(material).keySet()));
	}

	@ConverterFor(Tag.class)
	Tag<Material> convertToMaterialTag(String value) {
		if (MaterialTag.getTags().containsKey(value.toUpperCase()))
			return MaterialTag.getTags().get(value.toUpperCase());
		throw new InvalidInputException("MaterialTag from " + value + " not found");
	}

	@TabCompleterFor(Tag.class)
	List<String> tabCompleteMaterialTag(String filter) {
		return MaterialTag.getTags().keySet().stream()
			.map(String::toLowerCase)
			.filter(s -> s.startsWith(filter.toLowerCase()))
			.toList();
	}

	public static class MaterialTagMaterialsMenu extends MenuUtils implements InventoryProvider {
		private final Tag<Material> materialTag;

		public MaterialTagMaterialsMenu(Tag<Material> materialTag) {
			this.materialTag = materialTag;
		}

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
					.provider(this)
					.title(colorize("&3" + StringUtils.camelCase(materialTag.getKey().getKey())))
					.size(6, 9)
					.build()
					.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			List<ClickableItem> items = new ArrayList<>();
			materialTag.getValues().forEach(material -> {
				ItemStack item;
				if (material.isItem())
					item = new ItemStack(material);
				else
					item = new ItemBuilder(Material.BARRIER).name(StringUtils.camelCase(material)).build();

				items.add(ClickableItem.empty(item));
			});

			paginator(player, contents, items);
		}

	}
}
