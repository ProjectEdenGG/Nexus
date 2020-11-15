package me.pugabyte.bncore.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;
import java.util.stream.Collectors;

// TODO Add Menus
public class MaterialTagCommand extends CustomCommand {

	public MaterialTagCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("materials [tag]")
	void materials(Tag<Material> tag) {
		send(PREFIX + "Materials in tag: " + tag.getValues().stream().map(Material::name).collect(Collectors.joining(", ")));
	}

	@Path("find [material]")
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
}
