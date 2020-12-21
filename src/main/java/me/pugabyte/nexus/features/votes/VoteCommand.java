package me.pugabyte.nexus.features.votes;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.vote.Vote;
import me.pugabyte.nexus.models.vote.VoteService;
import me.pugabyte.nexus.models.vote.VoteSite;
import me.pugabyte.nexus.models.vote.Voter;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static me.pugabyte.nexus.utils.StringUtils.ProgressBarStyle.NONE;
import static me.pugabyte.nexus.utils.StringUtils.progressBar;

@Aliases("votes")
public class VoteCommand extends CustomCommand {
	Voter voter;
	String PLUS = "&e[+] &3";

	public VoteCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			voter = new VoteService().get(player());
	}

	@Async
	@Path
	void run() {
		line(3);
		send(json("&3Support the server by voting daily! Each vote gives you &e1 Vote point &3to spend in the " +
				"&eVote Point Store, &3and the top voters of each month receive a special reward!"));
		line();
		JsonBuilder builder = json("&3 Links &3|| &e");
		for (VoteSite site : VoteSite.values())
			builder.next("&e" + site.name()).url(site.getUrl()).next(" &3|| ").group();
		send(builder);
		int sum = new VoteService().getTopVoters(LocalDateTime.now().getMonth()).stream()
				.mapToInt(topVoter -> Long.valueOf(topVoter.getCount()).intValue()).sum();
		line();
		send(json("&3Server goal: " + progressBar(sum, 2000, NONE, 75) + " &e" + sum + "&3/&e2000")
				.hover("&eReach the goal together for a monthly reward!"));
		line();
		send(PLUS + "You have &e" + voter.getPoints() + " &3vote points");
		line();
		send(json(PLUS + "Visit the &eVote Points Store").command("/vps"));
		send(json(PLUS + "View top voters, prizes and more on our &ewebsite").url("https://bnn.gg/vote"));
	}

	@Path("time")
	void time() {
		line();
		for (VoteSite site : VoteSite.values()) {
			Optional<Vote> first = voter.getActiveVotes().stream().filter(_vote -> _vote.getSite() == site).findFirst();
			if (first.isPresent()) {
				LocalDateTime expirationTime = first.get().getTimestamp().plusHours(site.getExpirationHours());
				send("&e" + site.name() + " &7- &3You can vote in &e" + StringUtils.timespanDiff(expirationTime));
			} else {
				send(json("&e" + site.name() + " &7- &3Click here to vote").url(site.getUrl()));
			}
		}
	}

	@Path("points [player]")
	void points(@Arg("self") OfflinePlayer player) {
		if (PlayerUtils.isModeratorGroup(player())) {
			Voter voter = new VoteService().get(player);
			send("&e" + player.getName() + " &3has &e" + voter.getPoints() + " &3vote points");
		} else
			send("&3You have &e" + voter.getPoints() + " &3vote points");
	}

	@Path("points set <player> <number>")
	@Permission("group.seniorstaff")
	void setPoints(OfflinePlayer player, int number) {
		Voter voter = new VoteService().get(player);
		voter.setPoints(number);
		send("&e" + player.getName() + " &3now has &e" + voter.getPoints() + " &3vote points");
	}

	@Path("points add <player> <number>")
	@Permission("group.seniorstaff")
	void addPoints(OfflinePlayer player, int number) {
		Voter voter = new VoteService().get(player);
		voter.addPoints(number);
		send("&e" + player.getName() + " &3now has &e" + voter.getPoints() + " &3vote points");
	}

	@Path("points take <player> <number>")
	@Permission("group.seniorstaff")
	void takePoints(OfflinePlayer player, int number) {
		Voter voter = new VoteService().get(player);
		voter.takePoints(number);
		send("&e" + player.getName() + " &3now has &e" + voter.getPoints() + " &3vote points");
	}

	@Path("endOfMonth [month]")
	@Permission("group.admin")
	void endOfMonth(Month month) {
		console();
		EndOfMonth.run(month);
	}

	@Path("write")
	@Permission("group.admin")
	void write() {
		Votes.write();
		send(PREFIX + "Done");
	}

	@ConverterFor(Voter.class)
	Voter convertToVoter(String value) {
		return new VoteService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(Voter.class)
	List<String> tabCompleteVoter(String value) {
		return tabCompletePlayer(value);
	}

}
