package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.api.common.utils.RandomUtils.randomElement;
import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.isPastResourcePackScreen;

@Aliases("welc")
@NoArgsConstructor
@Permission(Group.STAFF)
public class WelcomeCommand extends CustomCommand implements Listener {
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
			var activeStaff = OnlinePlayers.where()
				.rank(Rank::isMod)
				.afk(false)
				.filter(Dev.KODA::isNot);

			if (activeStaff.count() < 4)
				return;

			if (CooldownService.isOnCooldown(UUID0, "bumpReminder", TickTime.HOUR.x(20)))
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
	void run(Player player) {
		welcome(nerd(), player);
	}

	public static void welcome(Nerd welcomer, Player welcomee) {
		if (welcomee != null) {
			String nickname = Nickname.of(welcomee);
			if (Rank.of(welcomee) != Rank.GUEST)
				throw new InvalidInputException("Prevented accidental welcome: " + nickname + " is not a guest");

			if (new HoursService().get(welcomee).has(TickTime.HOUR))
				throw new InvalidInputException("Prevented accidental welcome: " + nickname + " has more than an hour of playtime");

			if (!isPastResourcePackScreen(welcomee)) {
				QUEUE.computeIfAbsent(welcomee.getUniqueId(), $ -> new LinkedHashSet<>()).add(welcomer.getUniqueId());
				String status = StringUtils.camelCase(welcomee.getResourcePackStatus());
				throw new InvalidInputException("Queued welcome, " + nickname + "'s resource pack is not loaded yet (Status: " + status + ")");
			}
		}

		sayWelcome(welcomer, welcomee);
	}

	@EventHandler
	public void on(PlayerResourcePackStatusEvent event) {
		final Player welcomee = event.getPlayer();
		if (!isPastResourcePackScreen(welcomee))
			return;

		if (!QUEUE.containsKey(welcomee.getUniqueId()))
			return;

		final List<Nerd> welcomers = QUEUE.remove(welcomee.getUniqueId())
			.stream()
			.map(Nerd::of)
			.filter(Nerd::isOnline)
			.toList();

		int wait = 10;
		for (Nerd welcomer : welcomers)
			Tasks.wait(wait += RandomUtils.randomInt(30, 50), () -> sayWelcome(welcomer, welcomee));
	}

	private static void sayWelcome(@NotNull Nerd welcomer, @Nullable Player welcomee) {
		String message;

		if (CooldownService.isNotOnCooldown(UUID0, "welc", TickTime.SECOND.x(20)))
			message = getLongMessage(welcomee);
		else
			message = welcomee == null ? genericMessage : getShortMessage(welcomee);

		Chatter.of(welcomer).say(StaticChannel.GLOBAL.getChannel(), message);
	}

	private static String getShortMessage(@Nullable Player welcomee) {
		lastShortMessage = randomElement(SHORT_MESSAGES.stream()
			.filter(message -> !message.equals(lastShortMessage))
			.toList());

		return interpolate(welcomee, lastShortMessage);
	}

	private static String getLongMessage(@Nullable Player welcomee) {
		lastLongMessage = randomElement(LONG_MESSAGES.stream()
			.filter(message -> !message.equals(lastLongMessage))
			.toList());

		return interpolate(welcomee, lastLongMessage);
	}

	private static String interpolate(@Nullable Player welcomee, String message) {
		if (welcomee == null)
			message = message.replaceAll(" \\[player]", "").trim();
		else
			message = message.replaceAll("\\[player]", Nickname.of(welcomee)).trim();

		return message;
	}

}
