package gg.projecteden.nexus.features.votes;

import com.vexsoftware.votifier.model.VotifierEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.votes.vps.VPS;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteReward;
import gg.projecteden.nexus.models.dailyvotereward.DailyVoteRewardService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.voter.TopVoter;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;
import static gg.projecteden.nexus.utils.StringUtils.plural;
import static gg.projecteden.nexus.utils.Utils.epochSecond;

@NoArgsConstructor
public class Votes extends Feature implements Listener {
	static final int GOAL = 6000;

	@Override
	public void onStart() {
		scheduler();
	}

	private void scheduler() {
		Tasks.repeatAsync(Time.SECOND.x(5), Time.SECOND.x(10), () -> {
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
		EmbedBuilder builder = new EmbedBuilder().setTitle(EdenSocialMediaSite.WEBSITE.getUrl() + "/vote").setDescription("");
		for (VoteSite value : VoteSite.getValues())
			builder.appendDescription(System.lineSeparator() + "**" + value.name().toUpperCase() + "**: [Click to vote!](" + value.getUrl(username) + ")");
		return builder.build();
	}

	private void sendVoteReminder(Vote vote) {
		DiscordUser discordUser = new DiscordUserService().get(vote.getUuid());
		if (discordUser.getUserId() == null)
			return;

		if (!vote.getVoter().isReminders())
			return;

		if (!new CooldownService().check(vote.getUuid(), "vote-reminder", Time.MINUTE.x(10)))
			return;

		User user = Bot.KODA.jda().retrieveUserById(discordUser.getUserId()).complete();
		if (user != null && user.getMutualGuilds().size() > 0) {
			String username = Nerd.of(vote.getUuid()).getName();
			Nexus.log("[Votes] Sending vote reminder to " + username);
			MessageBuilder messageBuilder = new MessageBuilder().append("Boop! It's votin' time!").setEmbed(createEmbed(username));
			user.openPrivateChannel().complete().sendMessage(messageBuilder.build()).queue();
		}
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		String username = event.getVote().getUsername().replaceAll(" ", "");
		OfflinePlayer player = null;
		try { player = PlayerUtils.getPlayer(username); } catch (PlayerNotFoundException ignore) {}
		String name = player != null ? Nickname.of(player) : "Unknown";
		UUID uuid = player != null ? player.getUniqueId() : StringUtils.getUUID0();
		VoteSite site = VoteSite.getFromId(event.getVote().getServiceName());

		Nexus.log("[Votes] Vote received from " + event.getVote().getServiceName() + ": " + username + " (" + name + " | " + uuid + ")");

		// MCBIZ is sending votes with a timestamp of an hour ago ??
		LocalDateTime timestamp = epochSecond(event.getVote().getTimeStamp());
		if (site == VoteSite.MCBIZ) {
			long minutes = timestamp.until(LocalDateTime.now(), ChronoUnit.MINUTES);
			if (minutes > 55 && minutes < 65)
				timestamp = timestamp.plusHours(1);
		}

		if (site == null)
			return;

		final VoterService voterService = new VoterService();
		final Voter voter = voterService.get(uuid);
		Vote vote = new Vote(uuid, site, extraVotePoints(), timestamp);
		voter.vote(vote);

		int sum = voterService.getTopVoters(LocalDateTime.now().getMonth()).stream()
			.mapToInt(topVoter -> Long.valueOf(topVoter.getCount()).intValue()).sum();
		int left = 0;
		if (GOAL > sum)
			left = GOAL - sum;

		if (new CooldownService().check(uuid, "vote-announcement", Time.HOUR)) {
			String message = " &3for the server and received &b" + basePoints + plural(" &3vote point", basePoints) + " per site!";
			if (left > 0)
				message += " &e" + left + " &3more votes needed to hit the goal";

			Broadcast.ingame().message("&a[✔] &3" + name + " &bvoted" + message).send();
			Broadcast.discord().message(":white_check_mark: **" + name + " voted**" + message).send();
		}

		if (vote.getExtra() > 0) {
			Broadcast.ingame().message("&3[✦] &e" + name + " &3received &e" + vote.getExtra() + " extra &3vote points!").send();
			Broadcast.discord().message(":star: **" + name + "** received **" + vote.getExtra() + "** extra vote points!").send();
		}

		if (player != null) {
			int points = vote.getExtra() + basePoints;
			voter.givePoints(points);
			PlayerUtils.send(player, VPS.PREFIX + "You have received " + points + plural(" point", points));
		}

		voterService.save(voter);

		final int allVotes = voter.getVotes().size();
		final int todaysVotes = voter.getTodaysVotes().size();

		Nexus.log("[VoteStreak] " + name + " - Total votes: " + allVotes + " / Today's votes: " + todaysVotes);
		if (todaysVotes >= 5) {
			final DailyVoteRewardService dailyVoteRewardService = new DailyVoteRewardService();
			final DailyVoteReward dailyVoteReward = dailyVoteRewardService.get(player);
			if (!dailyVoteReward.getCurrentStreak().isEarnedToday()) {
				dailyVoteReward.getCurrentStreak().incrementStreak();
				dailyVoteRewardService.save(dailyVoteReward);
			}
		}

		Tasks.async(Votes::write);
	}

	private static final int basePoints = 1;
	private static final Map<Integer, Integer> extras = new HashMap<>() {{
		put(1500, 50);
		put(500, 25);
		put(200, 15);
		put(100, 10);
		put(50, 5);
	}};

	@NotNull
	protected static Map<Integer, Integer> getExtras() {
		double multiplier = BoostConfig.multiplierOf(Boostable.VOTE_POINTS);

		return new HashMap<>() {{
			extras.forEach((chance, amount) -> put((int) (chance / multiplier), amount));
		}};
	}

	private int extraVotePoints() {
		for (Map.Entry<Integer, Integer> pair : getExtras().entrySet())
			if (randomInt(pair.getKey()) == 1)
				return pair.getValue();
		return 0;
	}

	protected static void write() {
		Tasks.async(() -> {
			List<TopVoter> topVoters = new VoterService().getTopVoters(LocalDateTime.now().getMonth());

			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("plugins/website/votes_monthly_top.html"), StandardCharsets.UTF_8)) {
				int index = 0;
				for (TopVoter topVoter : topVoters) {
					if (++index <= 3) {
						String name = "Unknown";
						try { name = topVoter.getVoter().getNickname(); } catch (PlayerNotFoundException ignore) {}

						writer.write("<div class=\"col-sm-4\">" + System.lineSeparator());
						writer.write("  <h3 style=\"text-align: center;\">#" + index + "</h3>" + System.lineSeparator());
						writer.write("  <img class=\"center\" style=\"border-radius: 12px; width: 75%%\" src=\"https://crafatar.com/avatars/" + topVoter.getVoter().getUuid() + "?overlay\">" + System.lineSeparator());
						writer.write("  <h3 style=\"text-align: center;\">" + name + "</h3>" + System.lineSeparator());
						writer.write("  <h4 style=\"text-align: center;\">" + topVoter.getCount() + "</p>" + System.lineSeparator());
						writer.write("</div>" + System.lineSeparator());
					} else
						break;
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}

			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("plugins/website/votes_monthly.html"), StandardCharsets.UTF_8)) {
				int index = 0;
				for (TopVoter topVoter : topVoters) {
					if (++index > 3) {
						if (index < 54) {
							String name = "Unknown";
							try { name = topVoter.getVoter().getNickname(); } catch (PlayerNotFoundException ignore) {}

							writer.write("  <tr>" + System.lineSeparator());
							writer.write("    <th>" + index + "</th>" + System.lineSeparator());
							writer.write("    <th>" + name + "</th>" + System.lineSeparator());
							writer.write("    <th>" + topVoter.getCount() + "</th>" + System.lineSeparator());
							writer.write("  </tr>" + System.lineSeparator());
						} else
							break;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}


			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("plugins/website/votes.html"), StandardCharsets.UTF_8)) {
				List<TopVoter> allTimeTopVoters = new VoterService().getTopVoters();

				int index = 0;
				for (TopVoter topVoter : allTimeTopVoters) {
					if (++index <= 50) {
						String name = "Unknown";
						try { name = topVoter.getVoter().getNickname(); } catch (PlayerNotFoundException ignore) {}

						writer.write("  <tr>" + System.lineSeparator());
						writer.write("	<th>" + index + "</th>" + System.lineSeparator());
						writer.write("	<th>" + name + "</th>" + System.lineSeparator());
						writer.write("	<th>" + topVoter.getCount() + "</th>" + System.lineSeparator());
						writer.write("  </tr>" + System.lineSeparator());
					} else
						break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			int sum = topVoters.stream().mapToInt(topVoter -> Long.valueOf(topVoter.getCount()).intValue()).sum();
			try {
				Files.write(Paths.get("plugins/website/votes_monthly_total.html"), String.valueOf(sum).getBytes());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

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
		});
	}

}
