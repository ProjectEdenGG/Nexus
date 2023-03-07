package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.models.trophy.TrophyType;
import gg.projecteden.nexus.utils.FontUtils;
import gg.projecteden.nexus.utils.TitleBuilder;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@Permission(Group.STAFF)
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

	@Path("catalog [theme]")
	void catalog_view(Catalog.Theme theme) {
		if (theme == null)
			theme = Theme.GENERAL;

		Catalog.openCatalog(player(), theme, null, null);
	}

	@Path("getCatalog <theme>")
	void catalog_get(Catalog.Theme theme) {
		giveItem(theme.getNamedItem());
		send("Given " + StringUtils.camelCase(theme) + " Catalog");
	}

	@Path("get <type>")
	void get(DecorationConfig config) {
		giveItem(config.getItem());
		send("Given " + StringUtils.camelCase(config.getName()));
	}

	@Path("dye color <color>")
	@Permission(Group.STAFF)
	void dye(ChatColor chatColor) {
		ItemStack item = getToolRequired();
		Colored.of(chatColor.getColor()).apply(item);
		// TODO: APPLY LORE
	}

	@Path("dye stain <stain>")
	@Permission(Group.STAFF)
	void dye(StainChoice stainChoice) {
		ItemStack item = getToolRequired();
		Colored.of(stainChoice.getColor()).apply(item);
		// TODO: APPLY LORE
	}

	@Path("debug tooltip [--line1] [--line2] [--line3] [--addSpaces]")
	@Permission(Group.ADMIN)
	void tooltip(@Switch String line1, @Switch String line2, @Switch String line3, @Switch int addSpaces) {
		new TitleBuilder().subtitle(FontUtils.getToolTip(line1, line2, line3, addSpaces, player())).players(player()).send();
	}

	@Path("debug tabTypeMap")
	void debug_a() {
		Nexus.log(StringUtils.toPrettyString(DecorationType.getTabTypeMap()));
	}

	@Path("debug categoryTree")
	void debug_b() {
		Nexus.log(StringUtils.toPrettyString(DecorationType.getCategoryTree()));
	}

	@Path("debug [enabled]")
	@Permission(Group.ADMIN)
	void debug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationUtils.getDebuggers().contains(uuid());

		if (enabled)
			DecorationUtils.getDebuggers().add(uuid());
		else
			DecorationUtils.getDebuggers().remove(uuid());

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
