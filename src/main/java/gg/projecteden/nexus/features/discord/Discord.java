package gg.projecteden.nexus.features.discord;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.discord.DiscordId;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.api.discord.appcommands.AppCommandRegistry;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.commands.DiscordAppCommand;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.queup.QueUp;
import gg.projecteden.nexus.models.queup.QueUpService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
			appCommandRegistry = new AppCommandRegistry(Bot.KODA.jda(), DiscordAppCommand.class.getPackageName());
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
		var discordUser = new DiscordUserService().getFromUserId(user.getId());
		if (discordUser != null)
			return discordUser.getNickname();

		if (member != null)
			if (member.getNickname() != null)
				return member.getNickname();

		if (user != null)
			return user.getName();

		return null;
	}

	public static String replaceAll(String message, String find, String replacement) {
		return String.join(replacement, Arrays.asList(discordize(message + " ").split(find))).trim();
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
	public static CompletableFuture<Message> staffAlerts(String message) {
		// send(message, Channel.STAFF_ALERTS);
		return null;
	}

	public static CompletableFuture<Message> log(String message) {
		return send(message, TextChannel.STAFF_BRIDGE, TextChannel.STAFF_LOG);
	}

	public static CompletableFuture<Message> staffBridge(String message) {
		return send(message, TextChannel.STAFF_BRIDGE);
	}

	public static CompletableFuture<Message> staffLog(String message) {
		return send(message, TextChannel.STAFF_LOG);
	}

	public static CompletableFuture<Message> adminLog(String message) {
		return send(message, TextChannel.ADMIN_LOG);
	}

	public static CompletableFuture<Message> send(String message, TextChannel... targets) {
		return send(new MessageBuilder(stripColor(message).replace("<@role", "<@&")), targets);
	}

	public static CompletableFuture<Message> send(MessageBuilder message, TextChannel... targets) {
		return send(message, success -> {}, Throwable::printStackTrace, targets);
	}

	public static CompletableFuture<Message> send(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, TextChannel... targets) {
		return send(message, onSuccess, onError, Bot.RELAY, targets);
	}

	public static CompletableFuture<Message> koda(String message, TextChannel... targets) {
		return koda(new MessageBuilder(stripColor(message)), targets);
	}

	public static CompletableFuture<Message> koda(MessageBuilder message, TextChannel... targets) {
		return koda(message, success -> {}, Throwable::printStackTrace, targets);
	}

	public static CompletableFuture<Message> koda(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, TextChannel... targets) {
		return send(message, onSuccess, onError, Bot.KODA, targets);
	}

	private static CompletableFuture<Message> send(MessageBuilder message, Consumer<Message> onSuccess, Consumer<Throwable> onError, Bot bot, TextChannel... targets) {
		if (targets == null || targets.length == 0)
			targets = new TextChannel[]{ TextChannel.BRIDGE };

		for (TextChannel target : targets) {
			if (target == null || bot.jda() == null)
				continue;

			final MentionType[] mentionTypes = { MentionType.EVERYONE, MentionType.HERE };
			if (bot == Bot.RELAY && target == TextChannel.BRIDGE)
				message.denyMentions(mentionTypes);
			else
				message.allowMentions(mentionTypes);

			var textChannel = target.get(bot.jda());
			if (textChannel == null)
				continue;

			return textChannel.sendMessage(message.build()).submit().thenApply(success -> {
				onSuccess.accept(success);
				return success;
			}).exceptionally(ex -> {
				onError.accept(ex);
				return null;
			});
		}

		return CompletableFuture.completedFuture(null);
	}

	public static void addRole(String userId, DiscordId.Role role) {
		try {
			Role roleById = getGuild().getRoleById(role.getId());
			if (roleById == null)
				Nexus.log("Role from " + role.name() + " not found");
			else
				getGuild().addRoleToMember(UserSnowflake.fromId(userId), roleById).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void removeRole(String userId, DiscordId.Role role) {
		Role roleById = getGuild().getRoleById(role.getId());
		if (roleById == null)
			Nexus.log("Role from " + role.name() + " not found");
		else
			getGuild().removeRoleFromMember(UserSnowflake.fromId(userId), roleById).queue();
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
				.filter(player -> !Vanish.isVanished(player))
				.sorted(Comparator.comparing(player -> Nickname.of(player).toLowerCase()))
				.collect(Collectors.toList());

		String topic = "Online nerds (%d): %n%s".formatted(players.size(), getTopicPlayerList(players));

		QueUpService queupService = new QueUpService();
		QueUp queup = queupService.get0();
		if (!isNullOrEmpty(queup.getLastSong())) {
			final String song = System.lineSeparator() + System.lineSeparator() + "Now playing on " + EdenSocialMediaSite.QUEUP.getUrl() + ": " + stripColor(queup.getLastSong());
			if ((topic + song).length() <= (1024 - timestamp().length()))
				topic += song;
		}

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

	private static final OffsetDateTime BOT_GRANDFATHER_TIME = TimeUtil.getTimeCreated(Long.parseLong("352232748955729930"));

	public static void executeOnMessage(String channelId, String messageId, Consumer<Message> consumer) {
		OffsetDateTime timeCreated = TimeUtil.getTimeCreated(Long.parseLong(messageId));
		Bot botGuess = timeCreated.isAfter(BOT_GRANDFATHER_TIME) ? Bot.RELAY : Bot.KODA;
		Bot otherBot = botGuess == Bot.KODA ? Bot.RELAY : Bot.KODA;

		botGuess.jda().getTextChannelById(channelId).retrieveMessageById(messageId).queue(message -> {
			if (message.getAuthor().getId().equals(botGuess.getId()))
				consumer.accept(message);
			else
				otherBot.jda().getTextChannelById(channelId).retrieveMessageById(messageId).queue(consumer);
		});
	}

	public static void applyRoles(User user) {
		Discord.addRole(user.getId(), DiscordId.Role.NERD);
		DiscordUser discordUser = new DiscordUserService().getFromUserId(user.getId());
		if (discordUser != null) {
			Discord.addRole(user.getId(), DiscordId.Role.VERIFIED);

			if (discordUser.getRank() == Rank.VETERAN)
				Discord.addRole(user.getId(), DiscordId.Role.VETERAN);

			if (new BadgeUserService().get(discordUser).owns(Badge.SUPPORTER))
				Discord.addRole(user.getId(), DiscordId.Role.SUPPORTER);

			discordUser.updatePronouns(new NerdService().get(discordUser).getPronouns());
		}
	}

}
