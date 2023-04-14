package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.menus.BookBuilder;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Aliases("rank")
public class RanksCommand extends CustomCommand {

	public RanksCommand(CommandEvent event) {
		super(event);
	}

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

	@Override
	@NoLiterals
	@Description("View the available ranks on the server")
	public void help() {
		line(5);
		send("&3Here is a list of ranks. &eClick &3on one to view more info. You can tell what rank a person is by the &ecolor and format of their name&3.");
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
			JsonBuilder builder = new JsonBuilder("&3- " + rank.getColoredName());
			builder.command("/" + rank.name().toLowerCase());
			if (Rank.of(player()) == rank)
				builder.next("  &e&o<-- You are here!");

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

	@Description("Learn how to progress through the ranks")
	void guide() {
		runCommand("faq ranks");
	}

	@Disabled
	@Description("View the server's ranks in a book GUI")
	@Permission(Group.STAFF)
	public void book() {
		BookBuilder.WrittenBookMenu bookBuilder = new BookBuilder.WrittenBookMenu();

		AtomicReference<JsonBuilder> jsonBuilder = new AtomicReference<>(new JsonBuilder());

		jsonBuilder.get().next("Click a rank to view more information.").line();

		Arrays.asList(Rank.values()).forEach(rank -> {
			if (!rank.isActive()) return;

			String formattedRank = rank.getColoredName();
			if (rank.equals(Rank.GUEST))
				formattedRank = colorize("&8" + rank.getName());
			else if (rank.equals(Rank.MEMBER))
				formattedRank = colorize("&7" + rank.getName());

			jsonBuilder.get().next("&3[+] " + formattedRank);
			jsonBuilder.get().command("/" + rank.name().toLowerCase());
			if (Rank.of(player()) == rank)
				jsonBuilder.get().next("  &0&o<-- You");
			jsonBuilder.get().newline().group();
		});

		bookBuilder.addPage(jsonBuilder.get());
		bookBuilder.open(player());
	}

	@Description("View a player's rank")
	void of(Nerd player) {
		send(PREFIX + (isSelf(player) ? "Your" : player.getNickname() + "'s") + " rank: " + player.getRank().getColoredName());
	}
}
