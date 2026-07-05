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

import static gg.projecteden.api.common.utils.StringUtils.getDf;

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
		send(PREFIX + "Set attribute " + camelCase(attribute.getKey().getKey()) + " to " + getDf().format(value));
	}

	@Path("get <attribute> [player]")
	@Description("Get a player's attribute's value")
	void attribute(Attribute attribute, @Arg("self") Player player) {
		var attributeInstance = player.getAttribute(attribute);
		if (attributeInstance == null)
			error("Could not find attribute " + attribute.getKey().getKey());

		send(PREFIX + camelCase(attribute.getKey().getKey()) + " for " + Nickname.of(player));
		send("&7Base value: &e" + getDf().format(attributeInstance.getBaseValue()));

		if (attributeInstance.getModifiers().isEmpty()) {
			send("&7Modifiers: &enone");
		} else {
			send("&7Modifiers:");
			for (var modifier : attributeInstance.getModifiers())
				send(json("&8- &e" + getDf().format(modifier.getAmount()) + " &7(" + camelCase(modifier.getOperation()) + ")").hover("&eKey: &7" + modifier.getKey().getKey()));
		}

		send("&7Final value: &e" + getDf().format(attributeInstance.getValue()));
	}

	@Path("reset <attribute> [player]")
	@Description("Reset a player's attribute to default")
	void reset(Attribute attribute, @Arg("self") Player player) {
		var attributeInstance = player.getAttribute(attribute);
		var attributeName = camelCase(attribute.getKey().getKey());
		if (attributeInstance == null)
			error("Attribute " + attributeName + " not found");

		var count = reset(player, attribute);

		send(PREFIX + "Reset attribute " + attributeName + " to " + getDf().format(attributeInstance.getBaseValue()) + (count == 0 ? "" : " and removed " + count + " modifiers"));
	}

	@Path("reset all [player]")
	@Description("Reset all of a player's attributes to default")
	void resetAll(@Arg("self") Player player) {
		var modifiers = 0;
		for (Attribute attribute : Attribute.values())
			modifiers += reset(player, attribute);

		send(PREFIX + "Reset all attributes for " + Nickname.of(player) + (modifiers == 0 ? "" : " and removed " + modifiers + " modifiers"));
	}

	private static int reset(Player player, Attribute attribute) {
		var attributeInstance = player.getAttribute(attribute);
		if (attributeInstance == null)
			return 0;

		attributeInstance.setBaseValue(getDefaultValue(attribute));
		var modifiers = attributeInstance.getModifiers();
		var count = modifiers.size();
		for (var modifier : modifiers)
			attributeInstance.removeModifier(modifier);
		return count;
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
