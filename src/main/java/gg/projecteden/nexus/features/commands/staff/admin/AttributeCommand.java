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
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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

	@Path("reset <attribute> [player]")
	@Description("Reset a player's attribute to default")
	void reset(Attribute attribute, @Arg("self") Player player) {
		player.getAttribute(attribute).setBaseValue(getDefaultValue(attribute));
		send(PREFIX + "Set attribute " + camelCase(attribute.getKey().getKey()) + " to " + StringUtils.getDf().format(player.getAttribute(attribute).getBaseValue()));
	}

	@Path("reset all [player]")
	@Description("Reset a player's attribute to default")
	void resetAll(@Arg("self") Player player) {
		for (Attribute attribute : Attribute.values())
			if (player.getAttribute(attribute) != null)
				player.getAttribute(attribute).setBaseValue(getDefaultValue(attribute));

		send(PREFIX + "Reset all attributes for " + Nickname.of(player));
	}

	public static double getDefaultValue(Attribute attribute) {
		AttributeSupplier supplier = net.minecraft.world.entity.player.Player.createAttributes().build();
		Holder<net.minecraft.world.entity.ai.attributes.Attribute> holder = BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.withDefaultNamespace(attribute.getKey().value())).orElseThrow();

		if (supplier.hasAttribute(holder))
			return supplier.getBaseValue(holder);
		else
			return holder.value().getDefaultValue();
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
