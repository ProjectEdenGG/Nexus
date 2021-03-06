package me.pugabyte.nexus.features.votes;

import lombok.NonNull;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.vote.TopVoter;
import me.pugabyte.nexus.models.vote.Vote;
import me.pugabyte.nexus.models.vote.VoteService;
import me.pugabyte.nexus.models.vote.VoteSite;
import me.pugabyte.nexus.models.vote.Voter;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.ProgressBarStyle.NONE;
import static me.pugabyte.nexus.utils.StringUtils.progressBar;

@Aliases("votes")
public class VoteCommand extends CustomCommand {
	private final String PLUS = "&e[+] &3";
	private Voter voter;

	public VoteCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			voter = new VoteService().get(player());
	}

	@Path
	@Async
	void run() {
		line(3);
		send(json("&3Support the server by voting daily! Each vote gives you &e1 Vote point &3to spend in the " +
				"&eVote Point Store, &3and the top voters of each month receive a special reward!"));
		line();
		JsonBuilder builder = json("&3 Links");
		for (VoteSite site : VoteSite.values())
			builder.next(" &3|| &e").next("&e" + site.name()).url(site.getUrl(new Nerd(player()).getName())).group();
		send(builder);
		int sum = new VoteService().getTopVoters(LocalDateTime.now().getMonth()).stream()
				.mapToInt(topVoter -> Long.valueOf(topVoter.getCount()).intValue()).sum();
		line();
		send(json("&3Server goal: " + progressBar(sum, 4000, NONE, 75) + " &e" + sum + "&3/&e4000")
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
				send(json("&e" + site.name() + " &7- &3Click here to vote").url(site.getUrl(new Nerd(player()).getName())));
			}
		}
	}

	@Path("points [player]")
	void points(@Arg("self") OfflinePlayer player) {
		if (!isSelf(player)) {
			voter = new Voter(player);
			send("&e" + player.getName() + " &3has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
		} else
			send("&3You have &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission("group.seniorstaff")
	@Path("points set <player> <number>")
	void setPoints(OfflinePlayer player, int number) {
		Voter voter = new Voter(player);
		voter.setPoints(number);
		send("&e" + player.getName() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission("group.seniorstaff")
	@Path("points give <player> <number>")
	void givePoints(OfflinePlayer player, int number) {
		Voter voter = new Voter(player);
		voter.givePoints(number);
		send("&e" + player.getName() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
	}

	@Permission("group.seniorstaff")
	@Path("points take <player> <number>")
	void takePoints(OfflinePlayer player, int number) {
		Voter voter = new Voter(player);
		voter.takePoints(number);
		send("&e" + player.getName() + " &3now has &e" + voter.getPoints() + plural(" &3vote point", voter.getPoints()));
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

	@Path("calcFebKeys [dryRun]")
	@Permission("group.admin")
	void calcFebKeys(@Arg("true") boolean dryRun) {
		Map<UUID, Integer> voteKey = new HashMap<>();
		List<TopVoter> topVoters = new VoteService().getTopVoters(Month.FEBRUARY);
		for (TopVoter topVoter : topVoters) {
			if (topVoter.getCount() < 50) continue;
			voteKey.put(UUID.fromString(topVoter.getUuid()), 0);
		}
		for (TopVoter topVoter : topVoters) {
			if (topVoters.indexOf(topVoter) >= (((double) voteKey.size()) * .33)) continue;
			voteKey.put(UUID.fromString(topVoter.getUuid()), 1);
		}
		for (TopVoter topVoter : topVoters) {
			if (topVoters.indexOf(topVoter) >= (((double) voteKey.size()) * .1)) continue;
			voteKey.put(UUID.fromString(topVoter.getUuid()), 2);
		}

		StringBuilder paste = new StringBuilder();

		Utils.sortByValue(voteKey).forEach((uuid, amount) -> {
			if (amount < 1)
				return;

			OfflinePlayer player = PlayerUtils.getPlayer(uuid);
			paste.append(player.getName()).append(": ").append(amount).append(System.lineSeparator());

			ItemStack key = CrateType.FEB_VOTE_REWARD.getKey();
			key.setAmount(amount);
			if (!dryRun) {
				PlayerUtils.giveItemAndDeliverExcess(player, key, WorldGroup.SURVIVAL);
				if (player.getPlayer() != null)
					send(player.getPlayer(), PREFIX + "You have been given &e" + amount + " February Vote Keys &3for the server monthly reward");
			} else {
				ItemBuilder.addLore(key, player.getName() + "'s keys");
				PlayerUtils.giveItem(player(), key);
			}
		});

		send(json(PREFIX + "February Vote Keys were given. &eClick here to view the amounts.").url(StringUtils.paste(paste.toString())).hover("&eOpens paste link"));
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
