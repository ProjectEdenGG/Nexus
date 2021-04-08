package me.pugabyte.nexus.features.chat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.events.ChatEvent;
import me.pugabyte.nexus.features.commands.AgeCommand.ServerAge;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.minigames.utils.MinigameNight.NextMGN;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class Koda {
	@Getter
	private static final String PREFIX = StringUtils.getPrefix("KodaBear");
	@Getter
	private static final String nameFormat = "&5KodaBear";
	@Getter
	private static final String globalFormat = "&2[G] " + nameFormat + " &2&l> &f";
	@Getter
	private static final String localFormat = "&e[L] " + nameFormat + " &e&l> &f";
	@Getter
	private static final String dmFormat = "&3&l[&bPM&3&l] &eFrom &3KodaBear &b&l> &e";
	@Getter
	private static final String discordFormat = "<@&&f" + Role.KODA.getId() + "> **>** ";
	@Getter
	private static final OfflinePlayer player = Bukkit.getOfflinePlayer("KodaBear");
	@Getter
	private static final Chatter chatter = new ChatService().get(player);

	public static void reply(String message) {
		Tasks.wait(10, () -> say(message));
	}

	public static void replyIngame(String message) {
		Tasks.wait(10, () -> sayIngame(message));
	}

	public static void replyDiscord(String message) {
		Tasks.wait(10, () -> sayDiscord(message));
	}

	public static void say(String message) {
		sayIngame(message);
		sayDiscord(message);
	}

	public static void sayIngame(String message) {
		Chat.broadcastIngame(AdventureUtils.fromLegacyAmpersandText(globalFormat + message));
	}

	public static void sayDiscord(String message) {
		Chat.broadcastDiscord(discordFormat + message);
	}

	public static void announce(String message) {
		Discord.koda(message, TextChannel.ANNOUNCEMENTS);
	}

	public static void console(String message) {
		Bukkit.getConsoleSender().sendMessage("[KodaBear] " + stripColor(message));
	}

	public static void dm(Player player, String message) {
		PlayerUtils.send(player, dmFormat + message);
	}

	@Getter
	private static final List<Trigger> triggers = new ArrayList<>();

	static {
		reloadConfig();
	}

	public static void reloadConfig() {
		triggers.clear();
		ConfigurationSection config = Nexus.getConfig("koda.yml").getConfigurationSection("triggers");
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
							.build());
			}
		}
	}

	@Data
	@Builder
	private static class Trigger {
		private final String name;
		private final List<String> contains;
		private final List<String> responses;
		private final String routine;

		String getResponse() {
			return RandomUtils.randomElement(responses);
		}
	}

	public static void process(ChatEvent event) {
		responses: for (Trigger trigger : triggers) {
			for (String contains : trigger.getContains())
				if (!(" " + event.getMessage() + " ").matches("(?i).*(" + contains + ").*"))
					continue responses;

			if (!isNullOrEmpty(trigger.getRoutine()))
				routine(event, trigger.getRoutine());

			String response = trigger.getResponse();
			if (!isNullOrEmpty(response))
				respond(event, response);

			break;
		}
	}

	public static void respond(ChatEvent event, String response) {
		Tasks.waitAsync(Time.SECOND, () -> {
			final String finalResponse = response.replaceAll("\\[player]", event.getOrigin());
			PublicChannel channel = (PublicChannel) event.getChannel();
			event.getRecipients().forEach(recipient -> recipient.send(channel.getChatterFormat(chatter) + finalResponse));
			channel.broadcastDiscord(discordFormat + finalResponse);
		});
	}

	private static void routine(ChatEvent event, String id) {
		switch (id) {
			case "mgn":
				NextMGN mgn = new NextMGN();
				if (mgn.isNow())
					respond(event, "Minigame night is happening right now! Join with /gl");
				else
					respond(event, "The next Minigame Night will be hosted on " + mgn.getDateFormatted() + " at "
							+ mgn.getTimeFormatted() + ". That is in " + mgn.getUntil());
				break;
			case "canihaveop":
				if (event.getChatter() != null && event.getChatter().getOfflinePlayer().isOnline()) {
					Player player = event.getChatter().getPlayer();
					double health = player.getHealth();
					player.setHealth(20);
					player.getWorld().strikeLightning(player.getLocation());
					Tasks.wait(10, () -> {
						player.setHealth(20);
						player.getWorld().strikeLightning(player.getLocation());
						Tasks.wait(10, () -> {
							respond(event, "Does that answer your question?");
							player.setHealth(health);
						});
					});
				}
				break;
			case "canibestaff":
				if (event.getChatter() != null && event.getChatter().getOfflinePlayer().isOnline()) {
					Player player = event.getChatter().getPlayer();
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
				if ("Pugabyte".equals(event.getOrigin()))
					respond(event, "You're the one who decided to make a potato do important things.");
				else
					respond(event, "Puga is the one who decided to make a potato do important things.");
				break;
			case "griefing":
				if (event.getChatter() != null)
					if (!(event.getMessage().contains("not allowed") || event.getMessage().contains("isn't") || event.getMessage().contains("isnt")))
						if (Rank.of(event.getChatter()) == Rank.GUEST)
							respond(event, "[player], griefing is not allowed. Please take a look at the /rules for more information.");
				break;
			case "lag":
				int ping = event.getChatter().getPlayer().spigot().getPing();
				double tps = Bukkit.getTPS()[1];

				if (ping > 200 && tps > 16)
					respond(event, "[player], you are lagging (" + ping + "ms), not the server. Try relogging or rebooting your router.");
				break;
		}
	}

}
