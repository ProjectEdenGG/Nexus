package me.pugabyte.bncore.features.votes;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.vote.Vote;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.VoteSite;
import me.pugabyte.bncore.models.vote.Voter;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public class VoteCommand extends CustomCommand {
	Voter voter;
	String PLUS = "&e[+] &3";

	public VoteCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			voter = new VoteService().get(player());
	}

	@Path
	void run() {
		line(3);
		send(json("&3Support the server by voting daily! Each vote gives you &e1 Vote point &3to spend in the &eVote Point Store, &3and the top voters of each month receive a special reward!"));
		line();
		JsonBuilder builder = json("&3 Links &3|| &e");
		for (VoteSite site : VoteSite.values())
			builder.next("&e" + site.name()).url(site.getUrl()).next(" &3|| ");
		send(builder);
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
				send("&e" + site.name() + " &7- &3You can vote in " + StringUtils.timespanDiff(expirationTime));
			} else {
				send(json("&e" + site.name() + " &7- &3Click here to vote").url(site.getUrl()));
			}
		}
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

	@ConverterFor(Month.class)
	Month convertToMonth(String value) {
		try {
			return Month.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("Month from " + value + " not found");
		}
	}

	@TabCompleterFor(Month.class)
	List<String> tabCompleteMonth(String value) {
		return tabCompleteEnum(Month.class, value);
	}

}
