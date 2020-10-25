package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.models.newrankcolors.NewRankColors;
import me.pugabyte.bncore.models.newrankcolors.NewRankColorsService;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;

import java.util.Arrays;

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
		send("&2[G] &8&l[" + hex + format + camelCase(rank) + "&8&l] " + hex + format + player().getName() + " &2&l> &fTesting");
	}

	@Path("set <rank> <color>")
	void set(Rank rank, String color) {
		test(rank, color);
		newRankColors.getColors().put(rank, color);
		service.save(newRankColors);
	}

	@Path("view")
	void view() {
		line(5);
		Arrays.asList(Rank.values()).forEach(rank -> {
			if (!rank.isActive()) return;
			JsonBuilder builder = new JsonBuilder("&3- " + newRankColors.getColors().getOrDefault(rank, rank.getChatColor()) + camelCase(rank));
			builder.command("/" + rank.name().toLowerCase());
			if (Rank.getHighestRank(player()) == rank)
				builder.next("  &e&o<-- You are here!");

			send(builder);
		});
		line();
	}

}
