package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.JsonBuilder;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class RanksCommand extends CustomCommand {

	public RanksCommand(CommandEvent event) {
		super(event);
	}

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YY");

	@Path
	void ranks() {
		line(5);
		send("&3Here is a list of server ranks. &eClick &3on one to view more info. You can tell what rank a person is by the &ecolor and format of their name&3.");
		send(json("&3Please do not ask for ranks. You have to ")
				.group()
				.next("&eearn")
				.hover("&eClick here &3for a basic guide to ranking up")
				.command("/faq ranks ranks")
				.group()
				.next("&3 them")
		);
		line();
		Arrays.asList(Rank.values()).forEach(rank -> {
			if (!rank.isActive()) return;
			JsonBuilder builder = new JsonBuilder("&3- " + rank.withFormat());
			if (Rank.getHighestRank(player()) == rank)
				builder.next("  &e&o<-- You are here!");
			builder.command("/" + rank.name().toLowerCase());

			send(builder);
		});
		line();
	}

	static void ranksReturn(Player player) {
		new JsonBuilder()
				.next("&f &3&m<  &e Back")
				.command("/ranks")
				.send(player);
	}
}
