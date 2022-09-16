package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.trophy.TrophyType;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class DecorationCommand extends CustomCommand {

	public DecorationCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		// Init all decoration creators
		DecorationType.init();
		Pose.init();
		TrophyType.init();
	}

	@Path("get <type>")
	void get(DecorationConfig config) {
		giveItem(config.getItem());
		send("Given " + StringUtils.camelCase(config.getName()));
	}

	@Path("debug [enabled]")
	void debug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationUtils.debuggers.contains(uuid());

		if (enabled)
			DecorationUtils.debuggers.add(uuid());
		else
			DecorationUtils.debuggers.remove(uuid());

		send(PREFIX + "Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}

	@ConverterFor(DecorationConfig.class)
	DecorationConfig convertToDecorationConfig(String value) {
		final DecorationConfig config = DecorationConfig.of(value);
		if (config != null)
			return config;

		throw new InvalidInputException("Decoration &e" + value + " &cnot found");
	}

	@TabCompleterFor(DecorationConfig.class)
	List<String> tabCompleteDecorationConfig(String filter) {
		return DecorationConfig.getAllDecorationTypes().stream()
			.map(DecorationConfig::getId)
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

}
