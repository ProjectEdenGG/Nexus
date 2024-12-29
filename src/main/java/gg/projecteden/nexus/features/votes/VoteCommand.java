package gg.projecteden.nexus.features.votes;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.features.commands.AgeCommand.ServerAge;
import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand;
import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand.NumericPermission;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.lobby.exchange.MGMExchange;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.votes.party.RewardTier;
import gg.projecteden.nexus.features.votes.party.VoteParty;
import gg.projecteden.nexus.features.votes.vps.VPS;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.voter.VotePartyService;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NonNull;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Aliases("votes")
@Redirect(from = "/vps", to = "/vote points store")
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
	@Description("View information about voting and get links to the vote sites")
	void run() {
		int sum = service.getMonthsVotes().size();

		line(3);
		send(json("&3Support the server by voting daily! Each vote gives you &e1 Vote point &3to spend in the " +
			"&eVote Point Store, &3and the top voters of each month receive a special reward!"));
		line();
		send("&6&lLinks");
		for (VoteSite site : VoteSite.getActiveSites()) {
			Optional<Vote> vote = voter.getActiveVote(site);
			if (vote.isPresent())
				send("&e " + site.name() + " &7- &3Vote in &e" + Timespan.of(vote.get().getExpiration()).format());
			else
				send(json("&e " + site.name() + " &7- &eClick here to vote").url(site.getUrl(Nerd.of(player()).getName())));
		}
		line();
		if (!VoteParty.isFeatureEnabled(player())) {
			send(json("&3Server goal: " + ProgressBar.builder().progress(sum).goal(Votes.GOAL).summaryStyle(ProgressBar.SummaryStyle.NONE).length(300).seamless(true).build() + " &e" + sum + "&3/&e" + Votes.GOAL)
				.hover("&eReach the goal together for a monthly reward!"));
			line();
		}
		send("&e[+] &3" + "You have &e" + voter.getPoints() + " &3vote points");
		line();
		send(json("&e[+] &3" + "Visit the &eVote Points Store").command("/vps"));
		send(json("&e[+] &3" + "View top voters, prizes, and more on our &ewebsite").url(EdenSocialMediaSite.WEBSITE.getUrl() + "/vote"));
	}

	@Permission(Group.ADMIN)
	@Path("extra")
	@Description("View the extra chances config")
	void extra() {
		send("Extra config: " + Votes.getExtraChances(null));
	}

	@Path("history [player] [page]")
	@Description("View your voting history")
	void history(@Arg(value = "self", permission = Group.STAFF) Voter voter, @Arg("1") int page) {
		if (voter.getVotes().isEmpty())
			error(voter.getNickname() + " has not voted");

		send(PREFIX + (isSelf(voter) ? "Your" : voter.getNickname() + "'s") + " vote history");

		voter.getVotes().sort(Comparator.comparing(Vote::getTimestamp).reversed());

		final BiFunction<Vote, String, JsonBuilder> formatter = (vote, index) ->
			json("&3" + index + " &7" + TimeUtils.shortishDateTimeFormat(vote.getTimestamp()) + " - &e" + vote.getSite().name())
				.hover("&3" + Timespan.of(vote.getTimestamp()).format() + " ago");

		paginate(voter.getVotes(), formatter, "/vote history " + voter.getName(), page);
	}

	@Async
	@Permission(Group.STAFF)
	@Path("bestDays monthly [month] [page]")
	@Description("View the days with the most votes in a month")
	void bestDays_monthly(@Arg("current") YearMonth yearMonth, @Arg("1") int page) {
		Map<LocalDate, Integer> days = service.getVotesByDay(yearMonth);
		send(PREFIX + "Most votes in a day | " + arg(1));
		showBestDays(page, days, "monthly " + arg(1));
	}

	@Async
	@Permission(Group.STAFF)
	@Path("bestDays yearly [year] [page]")
	@Description("View the days with the most votes in a year")
	void bestDays_yearly(@Arg("current") Year year, @Arg("1") int page) {
		Map<LocalDate, Integer> days = service.getVotesByDay(year);
		send(PREFIX + "Most votes in a day | " + year.getValue());
		showBestDays(page, days, "yearly " + year.getValue());
	}

	@Async
	@Permission(Group.STAFF)
	@Path("bestDays allTime [page]")
	@Description("View the days with the most votes of all time")
	void bestDays_allTime(@Arg("1") int page) {
		Map<LocalDate, Integer> days = service.getAllVotesByDay();
		send(PREFIX + "Most votes in a day | All time");
		showBestDays(page, days, "allTime");
	}

	private void showBestDays(int page, Map<LocalDate, Integer> days, String type) {
		paginate(Utils.sortByValueReverse(days).keySet(), (date, index) -> {
			String color = date.equals(LocalDate.now()) ? "&6" : "&e";
			return json(index + " " + color + TimeUtils.shortishDateFormat(date) + " &7- " + days.get(date));
		}, "/votes bestDays " + type, page);
	}

	@Async
	@Permission(Group.STAFF)
	@Path("onlineCounts")
	@Description("View whether online players have voted")
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
	@Description("Toggle Discord voting reminders")
	void reminders(Boolean enable, @Arg(value = "self", permission = Group.STAFF) Voter voter) {
		if (enable == null)
			enable = !voter.isReminders();

		voter.setReminders(enable);
		send(PREFIX + "Discord voting reminders " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("points store [page]")
	@Description("Open the vote points store")
	void points_store(@Arg("1") int page) {
		if (worldGroup() == WorldGroup.MINIGAMES && Minigames.isInMinigameLobby(player()))
			MGMExchange.open(player());
		else
			VPS.open(player(), page);
	}

	@Path("points store buy plot")
	@Description("Buy a plot from the vote points store")
	void buyPlot() {
		if (LuckPermsUtils.hasPermission(uuid(), "plots.plot.6", ImmutableContextSet.of("world", "creative")))
			error("You have already purchased the maximum amount of plots");

		new VoterService().edit(player(), voter -> voter.takePoints(150));
		PermHelperCommand.add(NumericPermission.PLOTS, uuid(), 1);
		send(PREFIX + "Purchased &e1 creative plot &3for &e150 vote points");
	}

	@Path("points [player]")
	@Description("View your vote points")
	void points(@Arg("self") Voter voter) {
		if (!isSelf(voter)) {
			send("&e" + voter.getNickname() + " &3has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
		} else
			send("&3You have &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("points set <player> <number>")
	@Description("Modify a player's vote points")
	void setPoints(Voter voter, int number) {
		voter.setPoints(number);
		service.save(voter);
		send("&e" + voter.getNickname() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("points give <player> <number>")
	@Description("Modify a player's vote points")
	void givePoints(Voter voter, int number) {
		voter.givePoints(number);
		service.save(voter);
		send("&e" + voter.getNickname() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("points take <player> <number>")
	@Description("Modify a player's vote points")
	void takePoints(Voter voter, int number) {
		voter.takePoints(number);
		service.save(voter);
		send("&e" + voter.getNickname() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Async
	@Path("points top [page]")
	@Description("View the vote point leaderboard")
	void points_top(@Arg("1") int page) {
		send(PREFIX + "Top Vote Points");
		paginate(service.getTopPoints(), (user, index) -> json(index + " &e" + user.getNickname() + " &7- " + user.getPoints()), "/vote points top", page);
	}

	@Confirm
	@Path("endOfMonth [month]")
	@Permission(Group.ADMIN)
	@Description("Run the end of month task")
	void endOfMonth(@Arg("previous") YearMonth month) {
		EndOfMonth.run(month);
	}

	@Path("write")
	@Permission(Group.ADMIN)
	@Description("Write HTML vote files to disk for the website")
	void write() {
		Votes.write();
		send(PREFIX + "Done");
	}

	@Path("party target <target>")
	@Permission(Group.ADMIN)
	@Description("Set the current vote party target")
	void partyTarget(int amount) {
		VotePartyService vpService = new VotePartyService();
		vpService.edit0(vp -> vp.setCurrentTarget(amount));

		VoteParty.process();
		send(PREFIX + "Set the current target to &e" + amount);
	}

	@Path("party update")
	@Permission(Group.ADMIN)
	@Description("Updates the current vote party")
	void partyUpdate() {
		VoteParty.process();
		send(VoteParty.getPrefix() + "Updated");
	}

	@Path("party reward <player> <tier>")
	@Permission(Group.ADMIN)
	@Description("Gives a random vote party reward to the player")
	void partyReward(Nerd player, RewardTier tier) {
		tier.giveReward(player);
	}

	@Path("party complete")
	@Permission(Group.ADMIN)
	@Description("Simulates a completed vote party")
	void partyComplete() {
		VoteParty.complete();
	}

	@ConverterFor(YearMonth.class)
	YearMonth convertToYearMonth(String value) {
		if ("current".equals(value))
			return YearMonth.now();
		if ("previous".equals(value))
			return YearMonth.now().minusMonths(1);

		final String[] split = value.split("-");
		return YearMonth.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
	}

	@TabCompleterFor(YearMonth.class)
	List<String> tabCompleteYearMonth(String filter) {
		List<String> completions = new ArrayList<>();

		if (filter.matches("\\d{4}-.*")) {
			final String[] split = filter.split("-");
			for (int i = 1; i <= 12; i++)
				completions.add(split[0] + "-" + String.format("%02d", i));
		} else {
			Year year = Year.of(ServerAge.getEPOCH().getYear());
			final Year stop = Year.now().plusYears(2);
			while (year.isBefore(stop)) {
				completions.add(String.valueOf(year.getValue()));
				year = year.plusYears(1);
			}
		}

		completions.removeIf(completion -> !completion.toLowerCase().startsWith(filter.toLowerCase()));

		return completions;
	}

	@ConverterFor(Year.class)
	Year convertToYear(String value) {
		if ("current".equals(value))
			return Year.now();
		if ("previous".equals(value))
			return Year.now().minusYears(1);

		return Year.of(Integer.parseInt(value));
	}

	@TabCompleterFor(Year.class)
	List<String> tabCompleteYear(String filter) {
		List<String> completions = new ArrayList<>();

		Year year = Year.of(ServerAge.getEPOCH().getYear());
		final Year stop = Year.now().plusYears(2);
		while (year.isBefore(stop)) {
			completions.add(String.valueOf(year.getValue()));
			year = year.plusYears(1);
		}

		completions.removeIf(completion -> !completion.toLowerCase().startsWith(filter.toLowerCase()));

		return completions;
	}
}

