package gg.projecteden.nexus.features.votes;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.votes.vps.VPS;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.utils.TimeUtils;
import gg.projecteden.utils.TimeUtils.Timespan;
import gg.projecteden.utils.Utils;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.votes.Votes.GOAL;
import static gg.projecteden.nexus.utils.StringUtils.ProgressBarStyle.NONE;
import static gg.projecteden.nexus.utils.StringUtils.progressBar;
import static gg.projecteden.utils.TimeUtils.shortishDateTimeFormat;

@Aliases("votes")
public class VoteCommand extends CustomCommand {
	private final VoterService service = new VoterService();
	private Voter voter;

	public VoteCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			voter = service.get(player());
	}

	@Path
	@Async
	void run() {
		int sum = service.getMonthsVotes().size();

		line(3);
		send(json("&3Support the server by voting daily! Each vote gives you &e1 Vote point &3to spend in the " +
				"&eVote Point Store, &3and the top voters of each month receive a special reward!"));
		line();
		JsonBuilder builder = json("&3 Links");
		for (VoteSite site : VoteSite.getActiveSites())
			builder.next(" &3|| &e").next("&e" + site.name()).url(site.getUrl(Nerd.of(player()).getName())).group();
		send(builder);
		line();
		send(json("&3Server goal: " + progressBar(sum, GOAL, NONE, 75) + " &e" + sum + "&3/&e" + GOAL)
				.hover("&eReach the goal together for a monthly reward!"));
		line();
		send("&e[+] &3" + "You have &e" + voter.getPoints() + " &3vote points");
		line();
		send(json("&e[+] &3" + "Visit the &eVote Points Store").command("/vps"));
		send(json("&e[+] &3" + "View top voters, prizes and more on our &ewebsite").url(EdenSocialMediaSite.WEBSITE.getUrl() + "/vote"));
	}

	@Permission(Group.ADMIN)
	@Path("extra")
	void extra() {
		send("Extra config: " + Votes.getExtras());
	}

	@Path("times [player]")
	void time(@Arg(value = "self", permission = Group.STAFF) OfflinePlayer player) {
		voter = service.get(player);
		line();
		for (VoteSite site : VoteSite.getActiveSites()) {
			Optional<Vote> first = voter.getActiveVotes().stream().filter(_vote -> _vote.getSite() == site).findFirst();
			if (first.isPresent()) {
				LocalDateTime expirationTime = first.get().getTimestamp().plusHours(site.getExpirationHours());
				send("&e" + site.name() + " &7- &3You can vote in &e" + Timespan.of(expirationTime).format());
			} else {
				send(json("&e" + site.name() + " &7- &3Click here to vote").url(site.getUrl(Nerd.of(player()).getName())));
			}
		}
	}

	@Path("history [player] [page]")
	void history(@Arg(value = "self", permission = Group.STAFF) Voter voter, @Arg("1") int page) {
		if (voter.getVotes().isEmpty())
			error(voter.getNickname() + " has not voted");

		send(PREFIX + (isSelf(voter) ? "Your" : voter.getNickname() + "'s") + " vote history");

		voter.getVotes().sort(Comparator.comparing(Vote::getTimestamp).reversed());

		final BiFunction<Vote, String, JsonBuilder> formatter = (vote, index) ->
			json("&3" + index + " &7" + shortishDateTimeFormat(vote.getTimestamp()) + " - &e" + vote.getSite().name())
				.hover("&3" + Timespan.of(vote.getTimestamp()).format() + " ago");

		paginate(voter.getVotes(), formatter, "/vote history " + voter.getName(), page);
	}

	@Async
	@Permission(Group.ADMIN)
	@Path("getTopDaysThisMonth [page]")
	void getTopDaysThisMonth(@Arg("1") int page) {
		Map<LocalDate, Integer> days = service.getVotesByDay();

		send(PREFIX + "Most votes in a day");
		BiFunction<LocalDate, String, JsonBuilder> formatter = (date, index) -> {
			String color = date.equals(LocalDate.now()) ? "&6" : "&e";
			return json(index + " " + color + TimeUtils.shortishDateFormat(date) + " &7- " + days.get(date));
		};

		paginate(Utils.sortByValueReverse(days).keySet(), formatter, "/votes getTopDays", page);
	}

	@Async
	@Permission(Group.ADMIN)
	@Path("onlineCounts")
	void onlineCounts() {
		Map<Integer, List<Player>> activeVotes = new HashMap<>();
		for (Player player : OnlinePlayers.getAll()) {
			Voter voter = service.get(player);
			int count = voter.getActiveVotes().size();

			List<Player> players = activeVotes.getOrDefault(count, new ArrayList<>());
			players.add(player);
			activeVotes.put(count, players);
		}

		send(PREFIX + "Vote counts");
		Utils.sortByKeyReverse(activeVotes).forEach((count, players) ->
				send("&e" + count + " &7- " + players.stream().map(Nickname::of).collect(Collectors.joining(", "))));
	}

	@Path("reminders [enable] [player]")
	void reminders(Boolean enable, @Arg(value = "self", permission = Group.STAFF) Voter voter) {
		if (enable == null)
			enable = !voter.isReminders();

		voter.setReminders(enable);
		send(PREFIX + "Discord voting reminders " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("points store [page]")
	void run(@Arg("1") int page) {
		VPS.open(player(), page);
	}

	@Path("points [player]")
	void points(@Arg("self") Voter voter) {
		if (!isSelf(voter)) {
			send("&e" + voter.getNickname() + " &3has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
		} else
			send("&3You have &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("points set <player> <number>")
	void setPoints(Voter voter, int number) {
		voter.setPoints(number);
		service.save(voter);
		send("&e" + voter.getNickname() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("points give <player> <number>")
	void givePoints(Voter voter, int number) {
		voter.givePoints(number);
		service.save(voter);
		send("&e" + voter.getNickname() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("points take <player> <number>")
	void takePoints(Voter voter, int number) {
		voter.takePoints(number);
		service.save(voter);
		send("&e" + voter.getNickname() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Path("endOfMonth")
	@Permission(Group.ADMIN)
	void endOfMonth() {
		console();
		EndOfMonth.run();
	}

	@Path("write")
	@Permission(Group.ADMIN)
	void write() {
		Votes.write();
		send(PREFIX + "Done");
	}

}
