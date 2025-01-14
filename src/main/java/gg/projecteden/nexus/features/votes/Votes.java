package gg.projecteden.nexus.features.votes;

import com.vexsoftware.votifier.model.VotifierEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.votes.party.VoteParty;
import gg.projecteden.nexus.features.votes.vps.VPS;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteRewardService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.voter.TopVoter;
import gg.projecteden.nexus.models.voter.VotePartyData;
import gg.projecteden.nexus.models.voter.VotePartyService;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class Votes extends Feature implements Listener {
	static final int GOAL = 1750;

	@Override
	public void onStart() {
		scheduler();
	}

	private void scheduler() {
		Tasks.repeatAsync(TickTime.SECOND.x(5), TickTime.SECOND.x(10), () -> {
			// Don't try to process votes if discord is offline, reminders will break
			if (Discord.getGuild() == null)
				return;

			VoterService service = new VoterService();
			service.getActiveVotes().forEach(vote -> {
				LocalDateTime expiration = vote.getTimestamp().plusHours(vote.getSite().getExpirationHours());
				if (!expiration.isBefore(LocalDateTime.now())) return;

				vote.setActive(false);
				service.save(service.get(vote.getUuid()));
				write();

				sendVoteReminder(vote);
			});
		});
	}

	private static MessageEmbed createEmbed(String username) {
		EmbedBuilder builder = new EmbedBuilder().setTitle("Voting Links").setDescription("");
		builder.appendDescription(EdenSocialMediaSite.WEBSITE.getUrl() + "/vote" + System.lineSeparator());
		for (VoteSite value : VoteSite.getActiveSites())
			builder.appendDescription(System.lineSeparator() + "**" + value.name().toUpperCase() + "**: [Click to vote!](" + value.getUrl(username) + ")");
		return builder.build();
	}

	private void sendVoteReminder(Vote vote) {
		DiscordUser discordUser = new DiscordUserService().get(vote.getUuid());
		if (discordUser.getUserId() == null)
			return;

		if (!vote.getVoter().isReminders())
			return;

		if (!new CooldownService().check(vote.getUuid(), "vote-reminder", TickTime.MINUTE.x(10)))
			return;

		User user = Bot.KODA.jda().retrieveUserById(discordUser.getUserId()).complete();
		if (user != null && user.getMutualGuilds().size() > 0) {
			String username = Nerd.of(vote.getUuid()).getName();
			Nexus.log("[Votes] Sending vote reminder to " + username);
			MessageBuilder messageBuilder = new MessageBuilder().append("Boop! It's votin' time!").setEmbeds(createEmbed(username));
			user.openPrivateChannel().complete().sendMessage(messageBuilder.build()).queue();
		}
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		String username = event.getVote().getUsername().replaceAll(" ", "");
		OfflinePlayer player = null;
		try { player = PlayerUtils.getPlayer(username); } catch (PlayerNotFoundException ignore) {}
		String name = player != null ? Nickname.of(player) : "Unknown";
		UUID uuid = player != null ? player.getUniqueId() : UUIDUtils.UUID0;
		VoteSite site = VoteSite.getFromId(event.getVote().getServiceName());

		boolean accepted = true;
		if (site == null || !site.isActive())
			accepted = false;

		Nexus.log("[Votes] Vote %s from %s: %s (%s | %s)".formatted(accepted ? "accepted" : "rejected", event.getVote().getServiceName(), username, name, uuid));

		LocalDateTime timestamp = Utils.epochSecond(event.getVote().getTimeStamp());
		if (site == VoteSite.MCBIZ)
			timestamp = LocalDateTime.now(); // cant trust mcbiz timestamp

		if (!accepted)
			return;

		final VoterService voterService = new VoterService();
		final Voter voter = voterService.get(uuid);
		Vote vote = new Vote(uuid, site, extraVotePoints(voter), timestamp);
		voter.vote(vote);

		final VotePartyService votePartyService = new VotePartyService();
		final VotePartyData voteParty = votePartyService.get0();

		int sum = voteParty.getCurrentAmount();
		int goal = voteParty.getCurrentTarget();

		int left = 0;
		if (goal > sum)
			left = goal - sum;

		int points = vote.getExtra() + BASE_POINTS;
		voter.givePoints(points);
		voterService.save(voter);

		if (new CooldownService().check(uuid, "vote-announcement", TickTime.HOUR)) {
			String message = " &3for the server and received &b" + BASE_POINTS + StringUtils.plural(" &3vote point", BASE_POINTS) + " per site!";
			if (left > 0)
				message += " &e" + left + " &3more votes needed to trigger a &eVote Party&3!";

			Broadcast.ingame().message("&a[✔] &e" + name + " &bvoted" + message).send();
			Broadcast.discord().message(":white_check_mark: **" + Discord.discordize(name) + " voted**" + message).send();
		}

		PlayerUtils.send(player, VPS.PREFIX + "You have received " + points + StringUtils.plural(" point", points));
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(voter).pitchStep(6).play();

		if (vote.getExtra() > 0) {
			Broadcast.ingame().message("&3[✦] &e" + name + " &3received &e" + vote.getExtra() + " extra &3vote points!").send();
			Broadcast.discord().message(":star: **" + Discord.discordize(name) + "** received **" + vote.getExtra() + "** extra vote points!").send();
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(voter).pitchStep(10).play();
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BELL).receiver(voter).pitchStep(13).play();
		}

		final int allVotes = voter.getVotes().size();
		final int todaysVotes = voter.getTodaysVotes().size();

		Nexus.log("[VoteStreak] " + name + " - Total votes: " + allVotes + " / Today's votes: " + todaysVotes);
		if (todaysVotes >= 2) {
			final DailyVoteRewardService dailyVoteRewardService = new DailyVoteRewardService();
			final DailyVoteReward dailyVoteReward = dailyVoteRewardService.get(player);
			if (!dailyVoteReward.getCurrentStreak().isEarnedToday()) {
				dailyVoteReward.getCurrentStreak().incrementStreak();
				dailyVoteRewardService.save(dailyVoteReward);
			}
		}

		Tasks.async(Votes::write);
		VoteParty.process();
	}

	public static final int BASE_POINTS = 1;
	public static final Map<Integer, Integer> EXTRA_CHANCES = new HashMap<>() {{
		final double div = .66;
		put((int) (1500 * div), 50);
		put((int) (500 * div), 25);
		put((int) (200 * div), 15);
		put((int) (100 * div), 10);
		put((int) (50 * div), 5);
	}};

	@NotNull
	protected static Map<Integer, Integer> getExtraChances(HasUniqueId player) {
		double multiplier = Booster.getTotalBoost(player, Boostable.VOTE_POINTS);

		return new HashMap<>() {{
			EXTRA_CHANCES.forEach((chance, amount) -> put((int) (chance / multiplier), amount));
		}};
	}

	private int extraVotePoints(HasUniqueId player) {
		for (var chances : getExtraChances(player).entrySet())
			if (RandomUtils.randomInt(chances.getKey()) == 1)
				return chances.getValue();
		return 0;
	}

	protected static void write() {
		Tasks.async(() -> {
			final LocalDateTime now = LocalDateTime.now();
			List<TopVoter> topVoters = new VoterService().getTopVoters(now.getMonth());

			votes_sites();
			votes_voted();
			votes_monthly_total(topVoters);
			votes_monthly_top(topVoters);
			votes_monthly(topVoters);
			votes_alltime();

			List<TopVoter> lastMonthTopVoters = new VoterService().getTopVoters(now.minusMonths(1).getMonth());
			lastmonth_votes_monthly_total(lastMonthTopVoters);
			lastmonth_votes_monthly(lastMonthTopVoters);
		});
	}

	private static void votes_sites() {
		IOUtils.fileWrite("plugins/website/votes_sites.html", (writer, outputs) -> {
			for (VoteSite voteSite : VoteSite.getActiveSites()) {
				outputs.add(String.format("""
					<div class="btn-group">
						<a href="%s" target="_blank" role="button" <?php echo getVoteButtonHtml($%s == 1); ?></a>
						<button class="btn btn-default active votebtn"><strong>%s</strong></button>
					</div>
					<br/><br/>
				""", voteSite.getPhpUrl(), voteSite.name().toLowerCase(), voteSite.getName()));
			}
		});
	}

	private static void votes_monthly_top(List<TopVoter> topVoters) {
		final String center = "style=\"text-align: center;\"";

		IOUtils.fileWrite("plugins/website/votes_monthly_top.html", (writer, outputs) -> {
			int index = 0;
			for (TopVoter topVoter : topVoters) {
				if (++index > 3)
					break;

				outputs.add(String.format("""
					<div class="col-sm-4">
						<h3 %s>#%d</h3>
						<img class="center" style="border-radius: 12px; width: 75%%" src="https://crafatar.com/avatars/%s?overlay">
						<h3 %s>%s</h3>
						<h4 %s>%d</p>
					</div>
				""", center, index, topVoter.getVoter().getUuid(), center, topVoter.getNickname(), center, topVoter.getCount()));
			}
		});
	}

	private static void votes_monthly(List<TopVoter> topVoters) {
		IOUtils.fileWrite("plugins/website/votes_monthly.html", (writer, outputs) -> {
			int index = 0;
			for (TopVoter topVoter : topVoters) {
				if (++index <= 3)
					continue;
				if (index > 53)
					break;

				outputs.add(String.format("""
					<tr>
						<th>%d</th>
						<th>%s</th>
						<th>%d</th>
					</tr>
				""", index, topVoter.getNickname(), topVoter.getCount()));
			}
		});
	}

	private static void votes_alltime() {
		IOUtils.fileWrite("plugins/website/votes.html", (writer, outputs) -> {
			List<TopVoter> allTimeTopVoters = new VoterService().getTopVoters();

			int index = 0;
			for (TopVoter topVoter : allTimeTopVoters) {
				if (++index > 50)
					break;

				outputs.add(String.format("""
					<tr>
						<th>%d</th>
						<th>%s</th>
						<th>%d</th>
					</tr>
				""", index, topVoter.getNickname(), topVoter.getCount()));
			}
		});
	}

	private static void votes_monthly_total(List<TopVoter> topVoters) {
		int sum = topVoters.stream().mapToInt(topVoter -> Long.valueOf(topVoter.getCount()).intValue()).sum();
		IOUtils.fileWrite("plugins/website/votes_monthly_total.html", (writer, outputs) -> outputs.add(String.valueOf(sum)));
	}

	private static void votes_voted() {
		try {
			List<Vote> activeVotes = new VoterService().getActiveVotes();
			File file = Paths.get("plugins/website/votes_voted.yml").toFile();
			if (!file.exists()) file.createNewFile();
			YamlConfiguration config = new YamlConfiguration();
			activeVotes.forEach(vote -> {
				OfflinePlayer player = PlayerUtils.getPlayer(vote.getUuid());
				String name = Name.of(player);
				if (name == null) return;
				if (!config.isConfigurationSection(name))
					config.createSection(name);
				config.getConfigurationSection(name).set(vote.getSite().name().toLowerCase(), true);
			});
			config.save(file);
		} catch (PlayerNotFoundException ignore) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void lastmonth_votes_monthly_total(List<TopVoter> topVoters) {
		int sum = topVoters.stream().mapToInt(topVoter -> Long.valueOf(topVoter.getCount()).intValue()).sum();
		IOUtils.fileWrite("plugins/website/lastmonth_votes_monthly_total.html", (writer, outputs) -> outputs.add(String.valueOf(sum)));
	}

	private static void lastmonth_votes_monthly(List<TopVoter> topVoters) {
		IOUtils.fileWrite("plugins/website/lastmonth_votes_monthly.html", (writer, outputs) -> {
			int index = 0;
			for (TopVoter topVoter : topVoters) {
				if (++index > 50)
					break;

				outputs.add(String.format("""
					<tr>
						<th>%d</th>
						<th>%s</th>
						<th>%d</th>
					</tr>
				""", index, topVoter.getNickname(), topVoter.getCount()));
			}
		});
	}

}
