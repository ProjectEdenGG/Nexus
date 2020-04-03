package me.pugabyte.bncore.features.votes;

import com.vexsoftware.votifier.model.VotifierEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.votes.vps.VPS;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.vote.Vote;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.VoteSite;
import me.pugabyte.bncore.models.vote.Voter;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.Utils.epochSecond;
import static me.pugabyte.bncore.utils.Utils.randomInt;

public class Votes implements Listener {

	public Votes() {
		BNCore.registerListener(this);
		scheduler();
		new VPS();

//		BNCore.getCron().schedule("00 00 1 * *", () -> EndOfMonth.run(month));
	}

	private void scheduler() {
		Tasks.repeatAsync(Time.SECOND.x(5), Time.SECOND.x(10), () -> {
			VoteService service = new VoteService();
			service.getActiveVotes().forEach(vote -> {
				LocalDateTime expiration = vote.getTimestamp().plusHours(vote.getSite().getExpirationHours());
				if (!expiration.isBefore(LocalDateTime.now())) return;

				vote.setExpired(true);
				BNCore.log("Vote expired: " + vote);
				service.save(vote);

				sendVoteReminder(vote);
			});
		});
	}

	private static MessageEmbed voteLinksEmbed;

	static {
		EmbedBuilder builder = new EmbedBuilder().setTitle("https://bnn.gg/vote").setDescription("");
		for (VoteSite value : VoteSite.values())
			builder.appendDescription(System.lineSeparator() + "**" + value.name().toUpperCase() + "**: [Click to vote!](" + value.getLink() + ")");
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
				BNCore.log("[Votes] Sending vote reminder to " + Utils.getPlayer(vote.getUuid()));
				MessageBuilder messageBuilder = new MessageBuilder().append("Boop! It's votin' time!").setEmbed(voteLinksEmbed);
				user.openPrivateChannel().complete().sendMessage(messageBuilder.build()).queue();
			}
		} catch (CooldownException ignore) {}
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getVote().getUsername());
		String name = player != null ? player.getName() : "null";
		String uuid = player != null ? player.getName() : "00000000-0000-0000-0000-000000000000";
		VoteSite site = VoteSite.getFromId(event.getVote().getServiceName());

		BNCore.log("[Votes] Vote received from " + event.getVote().getServiceName() + ": " + event.getVote().getUsername() + " (" + name + " | " + uuid + ")");

		Vote vote = new Vote(uuid, site, extraVotePoints(), epochSecond(event.getVote().getTimeStamp()));
		new VoteService().save(vote);

		try {
			new CooldownService().check(uuid, "vote-announcement", Time.HOUR.x(12));
			if (site == VoteSite.PMC) {
				Chat.broadcastIngame("&a[✔] &3" + name + " &bvoted &3for the server and received &b1 &3vote point per site!");
				Discord.send(":white_check_mark: **" + name + " voted** for the server and received **1 vote point** per site!");
			}
		} catch (CooldownException ignore) {}

		if (vote.getExtra() > 0) {
			Chat.broadcastIngame("&3[✦] &e" + name + " &3received &e" + vote.getExtra() + " extra &3vote points!");
			Discord.send(":star: **" + name + "** received **" + vote.getExtra() + "** extra vote points!");
		}

		if (player != null && player.hasPlayedBefore()) {
			Voter voter = new VoteService().get(player);
			int points = vote.getExtra() + 1;
			voter.addPoints(points);
			if (player.isOnline())
				player.getPlayer().sendMessage(colorize(VPS.PREFIX + "You have received " + points + " point" + (points == 1 ? "" : "s")));
		}
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

}
