package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.newrankcolors.NewRankColors;
import gg.projecteden.nexus.models.newrankcolors.NewRankColorsService;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;

import java.awt.Color;
import java.util.Arrays;

import static gg.projecteden.nexus.utils.StringUtils.decolorize;

@Permission(Group.STAFF)
@Description("Test rank color updates")
public class NewRankColorsCommand extends CustomCommand {
	private final NewRankColorsService service = new NewRankColorsService();
	private final NewRankColors newRankColors;

	public NewRankColorsCommand(@NonNull CommandEvent event) {
		super(event);
		newRankColors = service.get(player());
	}

	@Description("Preview a color on a rank")
	void test(Rank rank, String hex) {
		if (!StringUtils.getHexPattern().matcher(hex).matches())
			error("Hex code must be in format &e&&e#123456");

		final String message = "&2[G] &8&l[" + hex + camelCase(rank) + "&8&l] " + hex + name() + " &2&l> &fTesting";
		EntityUtils.getNearbyEntities(location(), 500).forEach((entity, distance) -> {
			if (entity.getType() == EntityType.PLAYER)
				send(entity, message);
		});
	}

	@Description("Save a new rank color to your color config")
	void set(Rank rank, String hex) {
		test(rank, hex);
		newRankColors.getColors().put(rank, hex);
		service.save(newRankColors);
		send("Set color for " + camelCase(rank));
	}

	@Description("Reset a rank's color to default")
	void reset(Rank rank) {
		newRankColors.getColors().remove(rank);
		service.save(newRankColors);
		send("Reset color for " + camelCase(rank));
	}

	@Permission(Group.ADMIN)
	@Description("Print your color config's color codes to console for copying")
	void print() {
		newRankColors.getColors().forEach((rank, color) ->
				Nexus.log(rank.name() + "(\"" + color.replaceFirst("&", "")));
		send("Color codes printed to console");
	}

	@Description("View your rank color config")
	void view() {
		line(5);
		Arrays.asList(Rank.values()).forEach(rank -> {
			if (!rank.isActive() && rank != Rank.NOBLE) return;
			String color = decolorize(newRankColors.getColors().getOrDefault(rank, rank.getChatColor().toString()));
			String hex;
			if (StringUtils.getHexPattern().matcher(color).matches()) {
				hex = color.replace("&", "");
			} else {
				Color colorObject = ChatColor.getByChar(color.charAt(1)).getColor();
				hex = "#" + Integer.toHexString(colorObject.getRGB()).substring(2);
			}

			JsonBuilder builder = new JsonBuilder("&3- " + color + camelCase(rank))
					.hover("Click to copy hex code " + hex)
					.copy(hex);

			if (Rank.of(player()) == rank)
				builder.next("  &e&o<-- You are here!");

			send(builder);
		});
		line();
	}

}
