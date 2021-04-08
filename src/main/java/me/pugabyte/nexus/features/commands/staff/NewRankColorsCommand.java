package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.newrankcolors.NewRankColors;
import me.pugabyte.nexus.models.newrankcolors.NewRankColorsService;
import me.pugabyte.nexus.utils.EntityUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;

import java.awt.*;
import java.util.Arrays;

import static me.pugabyte.nexus.utils.StringUtils.decolorize;

@Permission("group.staff")
public class NewRankColorsCommand extends CustomCommand {
	private final NewRankColorsService service = new NewRankColorsService();
	private final NewRankColors newRankColors;

	public NewRankColorsCommand(@NonNull CommandEvent event) {
		super(event);
		newRankColors = service.get(player());
	}

	@Path("test <rank> <color>")
	void test(Rank rank, String hex) {
		if (!StringUtils.getHexPattern().matcher(hex).matches())
			error("Hex code must be in format &e&&e#123456");

		String format = (rank.isMod() ? "&o" : "");
		EntityUtils.getNearbyEntities(location(), 500).forEach((entity, distance) -> {
			if (entity.getType() == EntityType.PLAYER)
				send(entity, "&2[G] &8&l[" + hex + format + camelCase(rank) + "&8&l] " + hex + format + name() + " &2&l> &fTesting");
		});
	}

	@Path("set <rank> <color>")
	void set(Rank rank, String color) {
		test(rank, color);
		newRankColors.getColors().put(rank, color);
		service.save(newRankColors);
		send("Set color for " + camelCase(rank));
	}

	@Path("reset <rank>")
	void reset(Rank rank) {
		newRankColors.getColors().remove(rank);
		service.save(newRankColors);
		send("Reset color for " + camelCase(rank));
	}

	@Path("print")
	void print() {
		newRankColors.getColors().forEach((rank, color) ->
				Nexus.log(rank.name() + "(\"" + color.replaceFirst("&", "")));
		send("Color codes printed to console");
	}

	@Path("view")
	void view() {
		line(5);
		Arrays.asList(Rank.values()).forEach(rank -> {
			if (!rank.isActive() && rank != Rank.NOBLE) return;
			String color = decolorize(newRankColors.getColors().getOrDefault(rank, rank.getColor().toString()));
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
