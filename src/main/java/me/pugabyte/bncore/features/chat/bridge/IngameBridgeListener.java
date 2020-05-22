package me.pugabyte.bncore.features.chat.bridge;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.events.PublicChatEvent;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.store.perks.joinquit.VanishEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import net.dv8tion.jda.api.entities.GuildChannel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.discord.Discord.discordize;

@NoArgsConstructor
public class IngameBridgeListener implements Listener {


	@EventHandler(priority = EventPriority.MONITOR)
	public void onChannelChat(PublicChatEvent event) {
		DiscordId.Channel discordChannel = event.getChannel().getDiscordChannel();
		if (discordChannel == null) return;

		Player player = event.getChatter().getPlayer();
		DiscordUser user = new DiscordService().get(player);
		RoleManager.update(user);

		String message = event.getMessage();
		message = discordize(message);
		message = parseMentions(message);
		Discord.send(user.getBridgeName() + message, discordChannel);
	}

	public static String parseMentions(String message) {
		if (message != null && message.contains("@")) {
			Matcher matcher = Pattern.compile("@[A-Za-z0-9_]+").matcher(message);
			while (matcher.find()) {
				String group = matcher.group();
				try {
					OfflinePlayer player = Utils.getPlayer(group.replace("@", ""));
					DiscordUser mentioned = new DiscordService().get(player);
					if (mentioned.getUserId() != null)
						message = message.replace(group, "<@" + mentioned.getUserId() + ">");
				} catch (PlayerNotFoundException ignore) {}
			}
		}
		return message;
	}

	// TODO needed
//	@EventHandler
//	public void onRankChange(PermissionEntityEvent event) {
//		if (event.getAction() != Action.RANK_CHANGED) return;
//
//		if (event.getEntity() instanceof PermissionUser) {
//			OfflinePlayer player = Utils.getPlayer(event.getEntity().getName());
//			RoleManager.update(new DiscordService().get(player));
//		}
//	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (WorldGroup.get(event.getEntity()) == WorldGroup.SURVIVAL)
			Chat.broadcastDiscord(discordize(event.getDeathMessage()));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.async(this::updateTopics);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Tasks.async(this::updateTopics);
	}

	@EventHandler
	public void onVanish(VanishEvent event) {
		Tasks.async(this::updateTopics);
	}

	private static String bridgeTopic = "";
	private static String staffBridgeTopic = "";

	private void updateTopics() {
		String newBridgeTopic = getBridgeTopic();
		String newStaffBridgeTopic = getStaffBridgeTopic();

		if (!bridgeTopic.equals(newBridgeTopic))
			updateBridgeTopic(newBridgeTopic);
		if (!staffBridgeTopic.equals(newStaffBridgeTopic))
			updateStaffBridgeTopic(newStaffBridgeTopic);
	}

	private String getBridgeTopic() {
		List<Player> players = Bukkit.getOnlinePlayers().stream()
				.filter(player -> !Utils.isVanished(player))
				.collect(Collectors.toList());
		return "Online nerds (" + players.size() + "): " + players.stream().map(Player::getName).collect(Collectors.joining(", "));
	}

	private void updateBridgeTopic(String newBridgeTopic) {
		bridgeTopic = newBridgeTopic;
		GuildChannel channel = Discord.getGuild().getGuildChannelById(Channel.BRIDGE.getId());
		if (channel != null)
			channel.getManager().setTopic(bridgeTopic).queue();
	}

	private String getStaffBridgeTopic() {
		List<Player> players = Bukkit.getOnlinePlayers().stream()
				.filter(player -> new Nerd(player).getRank().isStaff())
				.collect(Collectors.toList());
		return "Online staff (" + players.size() + "): " + players.stream().map(Player::getName).collect(Collectors.joining(", "));
	}

	private void updateStaffBridgeTopic(String newStaffBridgeTopic) {
		staffBridgeTopic = newStaffBridgeTopic;
		GuildChannel channel = Discord.getGuild().getGuildChannelById(Channel.STAFF_BRIDGE.getId());
		if (channel != null)
			channel.getManager().setTopic(staffBridgeTopic).queue();
	}

}