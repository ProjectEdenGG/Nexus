package gg.projecteden.nexus.features.chat;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.commands.AgeCommand.ServerAge;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.minigames.utils.MinigameNight;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.audience.MessageType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Koda {
	@Getter @NotNull
	private static final String PREFIX = StringUtils.getPrefix("KodaBear");
	@Getter @NotNull
	private static final String name = "KodaBear";
	@Getter @NotNull
	private static final ChatColor chatColor = ChatColor.DARK_PURPLE;
	@Getter @NotNull
	private static final Color color = chatColor.getColor();
	@Getter @NotNull
	private static final String coloredName = chatColor + name;
	@Getter @NotNull
	private static final String globalFormat = "&2[G] " + coloredName + " &2&l> &f";
	@Getter @NotNull
	private static final String localFormat = "&e[L] " + coloredName + " &e&l> &f";
	@Getter @NotNull
	private static final String dmFormat = StringUtils.colorize("&3&l[&bPM&3&l] &eFrom &3KodaBear &b&l> &e");
	@Getter @NotNull
	private static final String discordFormat = "<@role" + Role.KODA.getId() + "> **>** ";
	@Getter @NotNull
	private static final UUID uuid = Dev.KODA.getUuid();
	@Getter @NotNull
	private static final Chatter chatter = new ChatterService().get(uuid);

	public static void reply(@NotNull String message) {
		Tasks.wait(10, () -> say(message));
	}

	public static void replyIngame(@NotNull String message) {
		Tasks.wait(10, () -> sayIngame(message));
	}

	public static void replyDiscord(@NotNull String message) {
		Tasks.wait(10, () -> sayDiscord(message));
	}

	public static void say(@NotNull String message) {
		sayIngame(message);
		sayDiscord(message);
	}

	public static void sayIngame(@NotNull String message) {
		PublicChannel channel = StaticChannel.GLOBAL.getChannel();
		Broadcast.ingame()
			.channel(channel)
			.sender(chatter)
			.messageFunction(viewer -> new JsonBuilder("")
				.next(channel.getChatterFormat(chatter, viewer == null ? null : new ChatterService().get(viewer), false))
				.group()
				.next(message))
			.messageType(MessageType.CHAT)
			.send();
	}

	public static void sayDiscord(@NotNull String message) {
		Broadcast.discord().message(discordFormat + message).send();
	}

	public static void announce(@NotNull String message) {
		Discord.koda(message, TextChannel.ANNOUNCEMENTS);
	}

	public static void console(@NotNull String message) {
		Bukkit.getConsoleSender().sendMessage("[KodaBear] " + StringUtils.stripColor(message));
	}

	public static void dm(@Nullable Player player, @NotNull String message) {
		PlayerUtils.send(player, dmFormat + message);
	}

	@Getter
	private static final List<Trigger> triggers = new ArrayList<>();

	static {
		reloadConfig();
	}

	public static void reloadConfig() {
		triggers.clear();
		ConfigurationSection config = IOUtils.getNexusConfig("koda.yml").getConfigurationSection("triggers");
		if (config != null) {
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				if (!config.isConfigurationSection(key) || section == null)
					Nexus.warn(PREFIX + "Configuration section " + key + " misconfigured");
				else
					triggers.add(Trigger.builder()
							.name(key)
							.contains(section.getStringList("contains"))
							.responses(section.getStringList("responses"))
							.routine(section.getString("routine"))
							.cooldown(section.getInt("cooldown"))
							.build());
			}
		}
	}

	public static boolean is(HasUniqueId hasUniqueId) {
		return Dev.KODA.is(hasUniqueId);
	}

	@Data
	@Builder
	private static class Trigger {
		private final String name;
		private final List<String> contains;
		private final List<String> responses;
		private final String routine;
		private final Integer cooldown;

		String getResponse() {
			return RandomUtils.randomElement(responses);
		}
	}

	public static void process(ChatEvent event) {
		responses: for (Trigger trigger : triggers) {
			for (String contains : trigger.getContains())
				if (!(" " + event.getMessage() + " ").matches("(?i).*(" + contains + ").*"))
					continue responses;

			if (trigger.getCooldown() != null && trigger.getCooldown() > 0)
				if (!new CooldownService().check(UUIDUtils.UUID0, "koda_" + trigger.getName(), TickTime.SECOND.x(trigger.getCooldown())))
					continue;

			if (!Nullables.isNullOrEmpty(trigger.getRoutine()))
				routine(event, trigger.getRoutine());

			String response = trigger.getResponse();
			if (!Nullables.isNullOrEmpty(response))
				respond(event, response);

			break;
		}
	}

	public static void respond(ChatEvent event, PublicChannel channel, String response) {
		if (event.isCancelled())
			return;

		Tasks.waitAsync(TickTime.SECOND, () -> {
			final String finalResponse = response.replaceAll("\\[player]", event.getOrigin());

			Broadcast.ingame()
				.channel(channel)
				.sender(event.getChatter()) // Set sender to the sender of the trigger, so that the mute carries over
				.message(viewer -> channel.getChatterFormat(chatter, Chatter.of(viewer), false).next(finalResponse))
				.messageType(MessageType.CHAT)
				.send();

			if (StaticChannel.GLOBAL.getChannel().equals(channel))
				Broadcast.discord()
					.channel(channel)
					.message(discordFormat + finalResponse)
					.send();
		});
	}

	public static void respond(ChatEvent event, String response) {
		respond(event, (PublicChannel) event.getChannel(), response);
	}

	private static void routine(ChatEvent event, String id) {
		switch (id) {
			case "mgn":
				MinigameNight mgn = new MinigameNight();
				if (mgn.isNow())
					respond(event, "Minigame night is happening right now! Join with /gl");
				else
					respond(event, "The next Minigame Night will be hosted on " + mgn.getDateFormatted() + " at "
						+ mgn.getTimeFormatted() + ". That is in " + mgn.getUntil());
				break;
			case "canihaveop":
				if (event.getChatter() != null && event.getChatter().isOnline()) {
					Player player = event.getChatter().getOnlinePlayer();
					double health = player.getHealth();
					player.setHealth(20);
					player.getWorld().strikeLightningEffect(player.getLocation());
					player.damage(8);
					Tasks.wait(10, () -> {
						player.setHealth(20);
						player.getWorld().strikeLightningEffect(player.getLocation());
						player.damage(8);
						Tasks.wait(10, () -> {
							respond(event, "Does that answer your question?");
							player.setHealth(health);
						});
					});
				}
				break;
			case "canibestaff":
				if (event.getChatter() != null && event.getChatter().isOnline()) {
					Player player = event.getChatter().getOnlinePlayer();
					if (Rank.of(player) == Rank.GUEST) {
						String command = "staff";
						if (event.getMessage().contains("mod")) command = "moderator";
						if (event.getMessage().contains(" op")) command = "operator";
						if (event.getMessage().contains("admin")) command = "admin";
						if (event.getMessage().contains("builder")) command = "builder";

						if ("staff".equals(command)) {
							respond(event, "Sorry [player], but you don't meet the requirements for staff. Type /moderator for more info about what's required.");
						} else {
							respond(event, "Sorry [player], but you don't meet the requirements for " + command + ". Type /" + command + " for more info about what's required.");
						}
					}
				}
				break;
			case "serverage":
				ServerAge serverAge = new ServerAge();
				String days = ServerAge.format(serverAge.getDays());
				String years = ServerAge.format(serverAge.getYears());
				String dogYears = ServerAge.format(serverAge.getDogYears());
				respond(event, "The server is " + days + " days old! That's " + years + " years, or " + dogYears + " dog years!");
				break;
			case "useless":
				if ("GriffinCodes".equals(event.getOrigin()) || "Griffin".equals(event.getOrigin()))
					respond(event, "You're the one who decided to make a potato do important things");
				else
					respond(event, "Griffin is the one who decided to make a potato do important things");
				break;
			case "griefing":
				if (event.getChatter() != null)
					if (!(event.getMessage().contains("not allowed") || event.getMessage().contains("isn't") || event.getMessage().contains("isnt")))
						if (Rank.of(event.getChatter()) == Rank.GUEST)
							respond(event, "[player], griefing is not allowed. Please take a look at the /rules for more information.");
				break;
			case "lag":
				if (!event.getChatter().isOnline())
					break;

				if (event.getChatter().getRank().isStaff())
					break;

				int ping = event.getChatter().getOnlinePlayer().getPing();
				double tps = Bukkit.getTPS()[1];

				if (ping > 200 && tps > 16) {
					event.setCancelled(true);
					respond(event, StaticChannel.LOCAL.getChannel(), "[player], you are lagging (" + ping + "ms), not the server. Try relogging or rebooting your router.");
				}
				break;
			case "jokes":
				if (sentJokes.size() == JOKES.size())
					sentJokes.clear();

				Map<String, Integer> joke = RandomUtils.randomElement(JOKES);
				String firstLine = joke.keySet().stream().toList().getFirst();
				int SAFETY = 0;
				while (sentJokes.contains(firstLine)) {
					joke = RandomUtils.randomElement(JOKES);
					firstLine = joke.keySet().stream().toList().getFirst();

					if (SAFETY++ > 100)
						sentJokes.clear();
				}

				sentJokes.add(firstLine);

				for (String line : joke.keySet()) {
					int waitSeconds = joke.get(line);
					if (waitSeconds == 0 && line.equalsIgnoreCase("MOCK")) {
						respond(event, StringUtils.randomizeCase(event.getMessage()));
						Tasks.wait(TickTime.SECOND.x(2), () -> respond(event, "I'm not your personal jester"));
						return;
					}

					Tasks.wait(TickTime.SECOND.x(waitSeconds), () -> respond(event, line));
				}
		}
	}

	private static final List<String> sentJokes = new ArrayList<>();

	private static final List<Map<String, Integer>> JOKES = new ArrayList<>() {{
		add(Map.of("MOCK", 0));
		add(Map.of("Your face", 0));
		add(Map.of("Your life", 0));
		add(Map.of("Your existence", 0));
		add(Map.of("Look in the mirror", 0));
		add(Map.of("Today at the bank, an old lady asked me to help check her balance.", 0, "So I pushed her over", 2));
		add(Map.of("Don’t trust anything atoms say", 0, "They make up everything", 2));
		add(Map.of("Why do dolphins sing off-key?", 0, "Because you can't tuna fish", 4));
		add(Map.of("I once met a giant. I didn't know what to say.", 0, "So I just used big words", 4));
		add(Map.of("What do bees use to fix their hair?", 0, "Honeycombs!", 4));
		add(Map.of("If there was ever a Minecraft movie, then it would be a blockbuster", 0));
		add(Map.of("What is a witch's favorite subject at school?", 0, "Spelling!", 4));
		add(Map.of("What do you get when you drop a piano into a mine shaft?", 0, "A Flat Miner", 4));
	}};

}
