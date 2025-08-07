package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.newrankcolors.NewRankColors;
import gg.projecteden.nexus.models.newrankcolors.NewRankColorsService;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;

import java.awt.*;
import java.util.Arrays;

@Permission(Group.STAFF)
@Description("Test rank color updates")
public class NewRankColorsCommand extends CustomCommand {
	private final NewRankColorsService service = new NewRankColorsService();
	private final NewRankColors newRankColors;

	public NewRankColorsCommand(@NonNull CommandEvent event) {
		super(event);
		newRankColors = service.get(player());
	}

	@Path("test <rank> <color>")
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

	@Path("set <rank> <color>")
	@Description("Save a new rank color to your color config")
	void set(Rank rank, String color) {
		test(rank, color);
		newRankColors.getColors().put(rank, color);
		service.save(newRankColors);
		send("Set color for " + camelCase(rank));
	}

	@Path("reset <rank>")
	@Description("Reset a rank's color to default")
	void reset(Rank rank) {
		newRankColors.getColors().remove(rank);
		service.save(newRankColors);
		send("Reset color for " + camelCase(rank));
	}

	@Path("print")
	@Permission(Group.ADMIN)
	@Description("Print your color config's color codes to console for copying")
	void print() {
		newRankColors.getColors().forEach((rank, color) ->
				Nexus.log(rank.name() + "(\"" + color.replaceFirst("&", "")));
		send("Color codes printed to console");
	}

	@Path("view")
	@Description("View your rank color config")
	void view() {
		line(5);
		Arrays.asList(Rank.values()).forEach(rank -> {
			String color = StringUtils.decolorize(newRankColors.getColors().getOrDefault(rank, rank.getChatColor().toString()));
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
