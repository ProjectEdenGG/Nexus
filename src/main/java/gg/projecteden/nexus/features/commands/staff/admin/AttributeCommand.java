package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

@Aliases("attr")
@NoArgsConstructor
@Permission(Group.ADMIN)
public class AttributeCommand extends CustomCommand implements Listener {

	public AttributeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<attribute> <value> [player]")
	@Description("Set a player's attribute")
	void attribute(Attribute attribute, double value, @Arg("self") Player player) {
		player.getAttribute(attribute).setBaseValue(value);
		send(PREFIX + "Set attribute " + camelCase(attribute.getKey().getKey()) + " to " + StringUtils.getDf().format(value));
	}

	@TabCompleterFor(Attribute.class)
	List<String> tabComplete(String filter) {
		return Registry.ATTRIBUTE.stream()
			.map(attribute -> attribute.getKey().getKey())
			.filter(attribute -> attribute.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@ConverterFor(Attribute.class)
	Attribute convertToAttribute(String value) {
		return Registry.ATTRIBUTE.stream()
			.filter(attribute -> attribute.getKey().getKey().equalsIgnoreCase(value))
			.findFirst().orElseThrow(() -> new InvalidInputException("Attribute &e" + value + " &c not found"));
	}

}
