package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Permission(Group.ADMIN)
public class MaterialTagCommand extends CustomCommand {

	public MaterialTagCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Description("View materials in a tag")
	void materials(Tag<Material> tag) {
		new MaterialTagMaterialsMenu(tag).open(player());
	}

	@Description("View tags containing a material")
	void find(Material material) {
		send(PREFIX + "Applicable tags: &e" + String.join("&3, &e", MaterialTag.getApplicable(material).keySet()));
	}

	@Description("Receive a random material from a tag")
	void random(Tag<Material> tag, @Optional Player player) {
		Material material = RandomUtils.randomMaterial(tag);
		giveItem(new ItemStack(material));
		String output = PREFIX + "Gave " + camelCase(material);
		if (!isSelf(player))
			output += " to &e" + Nickname.of(player);
		send(output);
	}

	@ConverterFor(Tag.class)
	Tag<?> convertToTag(String value) {
		if (!value.contains("."))
			error("Tag is not scoped");

		final String[] split = value.split("\\.");
		final String scope = split[0].toLowerCase();
		final String key = split[1].toUpperCase();

		switch (scope) {
			case "material":
				if (MaterialTag.getTags().containsKey(key))
					return MaterialTag.getTags().get(key);
				else
					throw new InvalidInputException("MaterialTag from &e" + value + " &cnot found");
			case "customblock":
				if (CustomBlockTag.getTags().containsKey(key))
					return CustomBlockTag.getTags().get(key);
				else
					throw new InvalidInputException("CustomBlockTag from &e" + value + " &cnot found");
			default:
				throw new InvalidInputException("Unsupported tag type");
		}
	}

	@TabCompleterFor(Tag.class)
	List<String> tabCompleteTag(String filter) {
		return new ArrayList<>() {{
			addAll(MaterialTag.getTags().keySet().stream()
				.map(material -> "material." + material.toLowerCase())
				.filter(material -> material.toLowerCase().startsWith(filter.toLowerCase()))
				.toList());
			addAll(CustomBlockTag.getTags().keySet().stream()
				.map(customBlock -> "customblock." + customBlock.toLowerCase())
				.filter(customBlock -> customBlock.toLowerCase().startsWith(filter.toLowerCase()))
				.toList());
		}};
	}

	public static class MaterialTagMaterialsMenu extends InventoryProvider {
		private final Tag<Material> materialTag;

		public MaterialTagMaterialsMenu(Tag<Material> materialTag) {
			this.materialTag = materialTag;
		}

		@Override
		public String getTitle() {
			return "&3" + StringUtils.camelCase(materialTag.getKey().getKey());
		}

		@Override
		public void init() {
			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();
			materialTag.getValues().forEach(material -> {
				ItemStack item;
				if (material.isItem())
					item = new ItemStack(material);
				else
					item = new ItemBuilder(Material.BARRIER).name(StringUtils.camelCase(material)).build();

				items.add(ClickableItem.empty(item));
			});

			paginate(items);
		}

	}
}
