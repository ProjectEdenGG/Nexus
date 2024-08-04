package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;
import static gg.projecteden.nexus.features.chat.Chat.StaticChannel.GLOBAL;

@Aliases("welc")
@NoArgsConstructor
@Permission(Group.STAFF)
public class WelcomeCommand extends CustomCommand {
	private static String lastLongMessage = null;
	private static String lastShortMessage = null;

	private static final String genericMessage = "Welcome to the server!";

	private static final List<String> LONG_MESSAGES = List.of(
		"Welcome to the server [player]! Make sure to read the /rules and feel free to ask questions.",
		"Welcome to Project Eden [player]! Please take a moment to read the /rules and feel free to ask any questions you have.",
		"Hi [player], welcome to Project Eden :) Please read the /rules and ask if you have any questions.",
		"Hey [player]! Welcome to Project Eden. Be sure to read the /rules and don't be afraid to ask questions ^^",
		"Hi there [player] :D Welcome to Project Eden. Make sure to read the /rules and feel free to ask questions."
	);

	private static final List<String> SHORT_MESSAGES = List.of(
		"Welcome to Project Eden [player]!",
		"Welcome to Project Eden!",
		"Welcome to the server [player]!",
		"Welcome to the server!",
		"Welcome [player]!",
		"Welcome!"
	);

	private static final Map<UUID, Set<UUID>> QUEUE = new HashMap<>();

	public WelcomeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(TickTime.MINUTE, TickTime.MINUTE, () -> {
			if (OnlinePlayers.where(player -> Rank.of(player).isMod() && Dev.KODA.isNot(player) && AFK.get(player).isNotAfk()).count() < 4)
				return;

			if (!new CooldownService().check(UUID0, "bumpReminder", TickTime.DAY))
				return;

			String url = "https://docs.google.com/document/d/1MVFG2ipdpCY42cUzZyVsIbjVlPRCiN0gmYL89sJNRTw/edit?usp=sharing";
			Broadcast.staffIngame().message("").send();
			Broadcast.staffIngame().message("&eHi Staff. &3It looks like there's a few of you online. Time to &ebump the server!").send();
			Broadcast.staffIngame().message(new JsonBuilder("&eClick me").url(url).group().next(" &3for the instructions")).send();
			Broadcast.staffIngame().message("").send();
		});
	}

	@Path("[player]")
	@Description("Welcome a player")
	void welcome(Player player) {
		if (player != null) {
			if (Rank.of(player) != Rank.GUEST)
				error("Prevented accidental welcome: this player is not a guest");

			if (new HoursService().get(player).has(TickTime.HOUR))
				error("Prevented accidental welcome: this player has more than an hour of playtime");

			if (!isPastResourcePackScreen(player)) {
				QUEUE.computeIfAbsent(player.getUniqueId(), $ -> new LinkedHashSet<>()).add(uuid());
				error("Queued welcome, their resource pack is not loaded yet (Status: " + camelCase(player.getResourcePackStatus()) + ")");
			}
		}

		sayWelcome(player(), player);
	}

	@EventHandler
	public void on(PlayerResourcePackStatusEvent event) {
		final Player welcomee = event.getPlayer();
		if (!isPastResourcePackScreen(welcomee))
			return;

		if (!QUEUE.containsKey(welcomee.getUniqueId()))
			return;

		final List<Player> welcomers = QUEUE.remove(welcomee.getUniqueId())
			.stream()
			.map(Nerd::of)
			.filter(Nerd::isOnline)
			.map(Nerd::getOnlinePlayer)
			.toList();

		int wait = 10;
		for (Player welcomer : welcomers)
			Tasks.wait(wait += RandomUtils.randomInt(30, 50), () -> sayWelcome(welcomer, welcomee));
	}

	private void sayWelcome(@NotNull Player welcomer, @Nullable Player welcomee) {
		if (new CooldownService().check(UUID0, "welc", TickTime.SECOND.x(20)))
			Chatter.of(welcomer).say(GLOBAL.getChannel(), getLongMessage(welcomee));
		else
			Chatter.of(welcomer).say(GLOBAL.getChannel(), welcomee == null ? genericMessage : getShortMessage(welcomee));
	}

	private static boolean isPastResourcePackScreen(Player player) {
		return player.getResourcePackStatus() == Status.FAILED_DOWNLOAD || ResourcePack.isEnabledFor(player);
	}

	private String getShortMessage(@Nullable Player player) {
		lastShortMessage = RandomUtils.randomElement(SHORT_MESSAGES.stream().filter(message -> !message.equals(lastShortMessage)).toList());
		return interpolate(player, lastShortMessage);
	}

	private String getLongMessage(@Nullable Player player) {
		lastLongMessage = RandomUtils.randomElement(LONG_MESSAGES.stream().filter(message -> !message.equals(lastLongMessage)).toList());
		return interpolate(player, lastLongMessage);
	}

	private String interpolate(@Nullable Player player, String message) {
		if (player == null)
			message = message.replaceAll(" \\[player]", "").trim();
		else
			message = message.replaceAll("\\[player]", player.getName()).trim();

		return message;
	}

}
