package me.pugabyte.nexus.features.commands.staff.admin;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

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
		send(PREFIX + "Applicable tags: " + String.join(", ", MaterialTag.getApplicable(material).keySet()));
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
				.filter(tagName -> tagName.toLowerCase().startsWith(filter.toLowerCase()))
				.map(String::toLowerCase)
				.collect(Collectors.toList());
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

			addPagination(player, contents, items);
		}

	}
}
