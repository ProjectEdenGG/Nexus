package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.menus.BookBuilder;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Aliases("rank")
public class RanksCommand extends CustomCommand {

	public RanksCommand(CommandEvent event) {
		super(event);
	}

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

	@Path
	@Description("View the available ranks on the server")
	void ranks() {
		line(5);
		send("&3Here is a list of our ranks, &eclick &3on each one for more info");
		send("&3You can tell what rank a person is by the &ecolor of their name&3");
		send("&3Please do not ask for ranks, you have to earn them");
		send(json("&eClick here to learn how to rank up").command("/faq ranks"));
		line();
		Arrays.asList(Rank.values()).forEach(rank -> {
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

	@Path("guide")
	@Description("Learn how to progress through the ranks")
	void guide() {
		runCommand("faq ranks");
	}

	@Disabled
	@Path("book")
	@Description("View the server's ranks in a book GUI")
	@Permission(Group.STAFF)
	public void bookMenu() {
		BookBuilder.WrittenBookMenu bookBuilder = new BookBuilder.WrittenBookMenu();

		AtomicReference<JsonBuilder> jsonBuilder = new AtomicReference<>(new JsonBuilder());

		jsonBuilder.get().next("Click a rank to view more information.").line();

		Arrays.asList(Rank.values()).forEach(rank -> {
			String formattedRank = rank.getColoredName();
			if (rank.equals(Rank.GUEST))
				formattedRank = StringUtils.colorize("&8" + rank.getName());
			else if (rank.equals(Rank.MEMBER))
				formattedRank = StringUtils.colorize("&7" + rank.getName());

			jsonBuilder.get().next("&3[+] " + formattedRank);
			jsonBuilder.get().command("/" + rank.name().toLowerCase());
			if (Rank.of(player()) == rank)
				jsonBuilder.get().next("  &0&o<-- You");
			jsonBuilder.get().newline().group();
		});

		bookBuilder.addPage(jsonBuilder.get());
		bookBuilder.open(player());
	}

	@Path("of <player>")
	@Description("View a player's rank")
	void of(Nerd player) {
		send(PREFIX + (isSelf(player) ? "Your" : player.getNickname() + "'s") + " rank: " + player.getRank().getColoredName());
	}
}
