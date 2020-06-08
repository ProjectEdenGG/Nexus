package me.pugabyte.bncore.features.votes;

import com.vexsoftware.votifier.model.VotifierEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.votes.vps.VPS;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.vote.TopVoter;
import me.pugabyte.bncore.models.vote.Vote;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.VoteSite;
import me.pugabyte.bncore.models.vote.Voter;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.Utils.epochSecond;
import static me.pugabyte.bncore.utils.Utils.randomInt;

public class Votes implements Listener {

	public Votes() {
		BNCore.registerListener(this);
		scheduler();

		new Timer("    EndOfMonth", () -> BNCore.getCron().schedule("00 00 1 * *", EndOfMonth::run));
	}

	private void scheduler() {
		Tasks.repeatAsync(Time.SECOND.x(5), Time.SECOND.x(10), () -> {
			VoteService service = new VoteService();
			service.getActiveVotes().forEach(vote -> {
				LocalDateTime expiration = vote.getTimestamp().plusHours(vote.getSite().getExpirationHours());
				if (!expiration.isBefore(LocalDateTime.now())) return;

				vote.setExpired(true);
				service.save(vote);
				write();

				sendVoteReminder(vote);
			});
		});
	}

	private static final MessageEmbed voteLinksEmbed;

	static {
		EmbedBuilder builder = new EmbedBuilder().setTitle("https://bnn.gg/vote").setDescription("");
		for (VoteSite value : VoteSite.values())
			builder.appendDescription(System.lineSeparator() + "**" + value.name().toUpperCase() + "**: [Click to vote!](" + value.getUrl() + ")");
		voteLinksEmbed = builder.build();
	}

	private void sendVoteReminder(Vote vote) {
		DiscordUser discordUser = new DiscordService().get(vote.getUuid());
		if (discordUser.getUserId() == null)
			return;

		Setting reminders = new SettingService().get(vote.getUuid(), "vote-reminders");
		if (reminders.getValue() != null && reminders.getBoolean())
			return;

		try {
			new CooldownService().check(vote.getUuid(), "vote-reminder", Time.MINUTE.x(10));
			User user = Bot.KODA.jda().getUserById(discordUser.getUserId());
			if (user != null && user.getMutualGuilds().size() > 0) {
				BNCore.log("[Votes] Sending vote reminder to " + Utils.getPlayer(vote.getUuid()).getName());
				MessageBuilder messageBuilder = new MessageBuilder().append("Boop! It's votin' time!").setEmbed(voteLinksEmbed);
				user.openPrivateChannel().complete().sendMessage(messageBuilder.build()).queue();
			}
		} catch (CooldownException ignore) {}
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		String username = event.getVote().getUsername().replaceAll(" ", "");
		OfflinePlayer player = null;
		try { player = Utils.getPlayer(username); } catch (PlayerNotFoundException ignore) {}
		String name = player != null ? player.getName() : "Unknown";
		String uuid = player != null ? player.getUniqueId().toString() : "00000000-0000-0000-0000-000000000000";
		VoteSite site = VoteSite.getFromId(event.getVote().getServiceName());

		BNCore.log("[Votes] Vote received from " + event.getVote().getServiceName() + ": " + username + " (" + name + " | " + uuid + ")");

		Vote vote = new Vote(uuid, site, extraVotePoints(), epochSecond(event.getVote().getTimeStamp()));
		new VoteService().save(vote);

		try {
			new CooldownService().check(uuid, "vote-announcement", Time.HOUR);
			Chat.broadcastIngame("&a[✔] &3" + name + " &bvoted &3for the server and received &b1 &3vote point per site!");
			Chat.broadcastDiscord(":white_check_mark: **" + name + " voted** for the server and received **1 vote point** per site!");
		} catch (CooldownException ignore) {}

		if (vote.getExtra() > 0) {
			Chat.broadcastIngame("&3[✦] &e" + name + " &3received &e" + vote.getExtra() + " extra &3vote points!");
			Chat.broadcastDiscord(":star: **" + name + "** received **" + vote.getExtra() + "** extra vote points!");
		}

		if (player != null && player.hasPlayedBefore()) {
			Voter voter = new VoteService().get(player);
			int points = vote.getExtra() + 1;
			voter.addPoints(points);
			if (player.isOnline())
				player.getPlayer().sendMessage(colorize(VPS.PREFIX + "You have received " + points + " point" + (points == 1 ? "" : "s")));
		}

		Tasks.async(Votes::write);
	}

	Map<Integer, Integer> extras = new HashMap<Integer, Integer>() {{
		put(1500, 50);
		put(500, 25);
		put(200, 15);
		put(100, 10);
		put(50, 5);
	}};

	private int extraVotePoints() {
		for (Map.Entry<Integer, Integer> pair : extras.entrySet())
			if (randomInt(pair.getKey()) == 1)
				return pair.getValue();
		return 0;
	}

	protected static void write() {
		Tasks.async(() -> {
			List<TopVoter> topVoters = new VoteService().getTopVoters(LocalDateTime.now().getMonth());

			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("plugins/website/votes_monthly_top.html"), StandardCharsets.UTF_8)) {
				int index = 0;
				for (TopVoter topVoter : topVoters) {
					if (++index <= 3) {
						String name = "Unknown";
						try { name = Utils.getPlayer(topVoter.getUuid()).getName(); } catch (PlayerNotFoundException ignore) {}

						writer.write("<div class=\"col-sm-4\">" + System.lineSeparator());
						writer.write("  <h3 style=\"text-align: center;\">#" + index + "</h3>" + System.lineSeparator());
						writer.write("  <img class=\"center\" style=\"border-radius: 12px; width: 75%%\" src=\"https://crafatar.com/avatars/" + topVoter.getUuid() + "?overlay\">" + System.lineSeparator());
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
							try {
								name = Utils.getPlayer(topVoter.getUuid()).getName();
							} catch (PlayerNotFoundException ignore) {}

							writer.write("  <tr>" + System.lineSeparator());
							writer.write("    <th>" + index + "</th>" + System.lineSeparator());
							writer.write("    <th>" + name + "</th>" + System.lineSeparator());
							writer.write("    <th>" + topVoter.getCount() + "</th>" + System.lineSeparator());
							writer.write("  </tr>" + System.lineSeparator());
						} else
							break;
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}


			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("plugins/website/votes.html"), StandardCharsets.UTF_8)) {
				List<TopVoter> allTimeTopVoters = new VoteService().getTopVoters();

				int index = 0;
				for (TopVoter topVoter : allTimeTopVoters) {
					if (++index <= 50) {
						String name = "Unknown";
						try {
							name = Utils.getPlayer(topVoter.getUuid()).getName();
						} catch (PlayerNotFoundException ignore) {}

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
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			try {
				List<Vote> activeVotes = new VoteService().getActiveVotes();
				File file = Paths.get("plugins/website/votes_voted.yml").toFile();
				if (!file.exists()) file.createNewFile();
				YamlConfiguration config = new YamlConfiguration();
				activeVotes.forEach(vote -> {
					OfflinePlayer player = Utils.getPlayer(vote.getUuid());
					if (player.getName() == null) return;
					if (!config.isConfigurationSection(player.getName()))
						config.createSection(player.getName());
					config.getConfigurationSection(player.getName()).set(vote.getSite().name().toLowerCase(), true);
				});
				config.save(file);
			} catch (PlayerNotFoundException ignore) {
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		});
	}

}
