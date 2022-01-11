package gg.projecteden.nexus.features.discord;

import gg.projecteden.discord.appcommands.AppCommandRegistry;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.commands.DiscordAppCommand;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.queup.QueUp;
import gg.projecteden.nexus.models.queup.QueUpService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.DiscordId;
import gg.projecteden.utils.DiscordId.TextChannel;
import gg.projecteden.utils.Env;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class Discord extends Feature {
	@Getter
	private static final Map<String, DiscordUser> codes = new HashMap<>();
	private static AppCommandRegistry appCommandRegistry;

	@Override
	public void onStart() {
		if (Nexus.getEnv() != Env.PROD)
			return;

		Tasks.repeatAsync(0, TickTime.MINUTE, this::connect);
		Tasks.waitAsync(TickTime.SECOND.x(2), this::connect);
		Nexus.getCron().schedule("*/6 * * * *", Discord::updateTopics);
	}

	public void connect() {
		for (Bot bot : Bot.values()) {
			try {
				bot.connect();
			} catch (Exception ex) {
				Nexus.severe("An error occurred while trying to connect to Discord");
				ex.printStackTrace();
			}
		}
	}

	public static void registerAppCommands() {
		getAppCommandRegistry().registerAll();
	}

	public static void unregisterAppCommands() {
		getAppCommandRegistry().unregisterAll();
	}

	private static AppCommandRegistry getAppCommandRegistry() {
		if (appCommandRegistry == null)
			appCommandRegistry = new AppCommandRegistry(Bot.KODA.jda(), DiscordAppCommand.class.getPackage().getName());
		return appCommandRegistry;
	}

	@Override
	public void onStop() {
		try {
			for (Bot bot : Bot.values())
				bot.shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean isConnected() {
		return getGuild() != null;
	}

	public static String getName(String id) {
		String name = getName(Discord.getGuild().retrieveMemberById(id).complete());
		if (name == null) name = id;
		return name;
	}

	public static String getName(Member member) {
		User user = null;
		if (member != null)
			user = member.getUser();
		return getName(member, user);
	}

	public static String getName(User user) {
		return getName(null, user);
	}

	public static String getName(Member member, User user) {
		if (member != null)
			if (member.getNickname() != null)
				return member.getNickname();

		if (user != null)
			return user.getName();

		return null;
	}

	public static String discordize(String message) {
		if (message == null)
			return null;

		return message
			.replaceAll("\\\\([*_`~\\\\])", "$1")
			.replaceAll("([*_`~\\\\])", "\\\\$1");
	}

	public static String discordize(ComponentLike component) {
		return discordize(AdventureUtils.asPlainText(component));
	}

	@Nullable
	public static Guild getGuild() {
		if (Bot.KODA.jda() == null) return null;
		return Bot.KODA.jda().getGuildById(DiscordId.Guild.PROJECT_EDEN.getId());
	}

	@Deprecated
	public static void staffAlerts(String message) {
		// send(message, Channel.STAFF_ALERTS);
	}

	public static void log(String message) {
		send(message, TextChannel.STAFF_BRIDGE, TextChannel.STAFF_LOG);
	}

	public static void staffBridge(String message) {
		send(message, TextChannel.STAFF_BRIDGE);
	}

	public static void staffLog(String message) {
		send(message, TextChannel.STAFF_LOG);
	}

	public static void adminLog(String message) {
		send(message, TextChannel.ADMIN_LOG);
	}

	public static void send(String message, TextChannel... targets) {
		send(new MessageBuilder(stripColor(message).replace("<@role", "<@&")), targets);
	}

	public static void send(MessageBuilder message, TextChannel... targets) {
		send(message, success -> {}, error -> {}, targets);
	}

	public static void send(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, TextChannel... targets) {
		send(message, onSuccess, onError, Bot.RELAY, targets);
	}

	public static void koda(String message, TextChannel... targets) {
		koda(new MessageBuilder(stripColor(message)), targets);
	}
	public static void koda(MessageBuilder message, TextChannel... targets) {
		koda(message, success -> {}, error -> {}, targets);
	}

	public static void koda(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, TextChannel... targets) {
		send(message, onSuccess, onError, Bot.KODA, targets);
	}

	private static void send(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, Bot bot, TextChannel... targets) {
		if (targets == null || targets.length == 0)
			targets = new TextChannel[]{ TextChannel.BRIDGE };
		for (TextChannel target : targets) {
			if (target == null || bot.jda() == null)
				continue;
			if (bot == Bot.RELAY && target == TextChannel.BRIDGE)
				message.denyMentions(Message.MentionType.EVERYONE, Message.MentionType.HERE);
			else
				message.allowMentions(Message.MentionType.EVERYONE, Message.MentionType.HERE);
			var textChannel = target.get(bot.jda());
			if (textChannel != null)
				textChannel.sendMessage(message.build()).queue(onSuccess, onError);
		}
	}

	public static void addRole(String userId, DiscordId.Role role) {
		try {
			Role roleById = getGuild().getRoleById(role.getId());
			if (roleById == null)
				Nexus.log("Role from " + role.name() + " not found");
			else
				getGuild().addRoleToMember(userId, roleById).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void removeRole(String userId, DiscordId.Role role) {
		Role roleById = getGuild().getRoleById(role.getId());
		if (roleById == null)
			Nexus.log("Role from " + role.name() + " not found");
		else
			getGuild().removeRoleFromMember(userId, roleById).queue();
	}

	private static String bridgeTopic = "";
	private static String staffBridgeTopic = "";

	private static void updateTopics() {
		String newBridgeTopic = getBridgeTopic();
		String newStaffBridgeTopic = getStaffBridgeTopic();

		if (!bridgeTopic.equals(newBridgeTopic))
			updateBridgeTopic(newBridgeTopic);
		if (!staffBridgeTopic.equals(newStaffBridgeTopic))
			updateStaffBridgeTopic(newStaffBridgeTopic);
	}

	@NotNull
	private static String getTopicPlayerList(List<Player> players) {
		return players.stream()
			.map(player -> Presence.of(player).discord() + " " + Nickname.discordOf(player).trim())
			.collect(Collectors.joining(System.lineSeparator()));
	}

	private static String getBridgeTopic() {
		List<Player> players = OnlinePlayers.getAll().stream()
				.filter(player -> !PlayerUtils.isVanished(player))
				.sorted(Comparator.comparing(player -> Nickname.of(player).toLowerCase()))
				.collect(Collectors.toList());

		String topic = "Online nerds (%d): %n%s".formatted(players.size(), getTopicPlayerList(players));

		QueUpService queupService = new QueUpService();
		QueUp queup = queupService.get0();
		if (!isNullOrEmpty(queup.getLastSong()))
			topic += System.lineSeparator() + System.lineSeparator() + "Now playing on " + EdenSocialMediaSite.QUEUP.getUrl() + ": " + stripColor(queup.getLastSong());

		return topic;
	}

	private static String getStaffBridgeTopic() {
		List<Player> players = OnlinePlayers.getAll().stream()
			.filter(player -> Rank.of(player).isStaff())
			.sorted(Comparator.comparing(Nickname::of))
			.collect(Collectors.toList());

		return "Online staff (%d): %n%s".formatted(players.size(), getTopicPlayerList(players));
	}

	private static String timestamp() {
		return "%n%n%s".formatted("Last update: <t:" + System.currentTimeMillis() / 1000 + ">");
	}

	private static void updateBridgeTopic(String newBridgeTopic) {
		if (Discord.getGuild() == null) return;
		bridgeTopic = newBridgeTopic;
		var channel = TextChannel.BRIDGE.get(Bot.KODA.jda());
		if (channel == null)
			return;

		String topic = bridgeTopic + timestamp();

		try {
			channel.getManager().setTopic(topic).queue();
		} catch (Exception e) {
			Nexus.warn("Discord topic too long! (" + topic.length() + " /1024)");
			e.printStackTrace();
		}
	}

	private static void updateStaffBridgeTopic(String newStaffBridgeTopic) {
		if (Discord.getGuild() == null) return;
		staffBridgeTopic = newStaffBridgeTopic;
		var channel = TextChannel.STAFF_BRIDGE.get(Bot.KODA.jda());
		if (channel != null)
			channel.getManager().setTopic(staffBridgeTopic + timestamp()).queue();
	}

	@NotNull
	public static String getInvite() {
		Guild guild = getGuild();
		if (guild == null)
			throw new InvalidInputException("Discord bot is not connected");

		String url = getGuild().getVanityUrl();

		if (isNullOrEmpty(url)) {
			var textChannel = TextChannel.GENERAL.get(Bot.KODA.jda());
			if (textChannel == null)
				throw new InvalidInputException("General channel not found");

			url = textChannel.createInvite().complete().getUrl();
		}

		if (isNullOrEmpty(url))
			throw new InvalidInputException("Could not generate invite link");

		return url;
	}

}
