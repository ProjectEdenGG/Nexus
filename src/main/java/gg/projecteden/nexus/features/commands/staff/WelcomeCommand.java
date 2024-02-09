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
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;

@Aliases("welc")
@NoArgsConstructor
@Permission(Group.STAFF)
public class WelcomeCommand extends CustomCommand {
	private static String lastUniqueMessage = null;
	private static String lastGenericMessage = null;
	private static final String genericWelcome = "Welcome to the server!";

	List<String> uniqueMessages = new ArrayList<>() {{
		add("Welcome to the server [player]! Make sure to read the /rules and feel free to ask questions.");
		add("Welcome to Project Eden [player]! Please take a moment to read the /rules and feel free to ask any questions you have.");
		add("Hi [player], welcome to Project Eden :) Please read the /rules and ask if you have any questions.");
		add("Hey [player]! Welcome to Project Eden. Be sure to read the /rules and don't be afraid to ask questions ^^");
		add("Hi there [player] :D Welcome to Project Eden. Make sure to read the /rules and feel free to ask questions.");
	}};

	List<String> genericMessages = new ArrayList<>() {{
		add("Welcome to Project Eden [player]!");
		add("Welcome to Project Eden!");
		add("Welcome to the server [player]!");
		add("Welcome to the server!");
		add("Welcome [player]!");
		add("Welcome!");
	}};

	public WelcomeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(TickTime.MINUTE, TickTime.MINUTE, () -> {
			if (OnlinePlayers.getAll().stream().filter(player ->
				Rank.of(player).isMod() &&
					Dev.KODA.isNot(player) &&
					AFK.get(player).isNotAfk()
			).count() < 4)
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

			Status rpStatus = player.getResourcePackStatus();
			if (rpStatus != Status.FAILED_DOWNLOAD && !ResourcePack.isEnabledFor(player))
				error("Their resource pack is not loaded yet (Status: " + StringUtils.camelCase(rpStatus) + ")");
		}

		if (new CooldownService().check(UUID0, "welc", TickTime.SECOND.x(20))) {
			runCommand("ch qm g " + getUniqueMessage(player));
		} else {
			String message = player == null ? genericWelcome : getGenericMessage(player);
			runCommand("ch qm g " + message);
		}
	}

	private String getGenericMessage(@Nullable Player player) {
		ArrayList<String> list = new ArrayList<>(genericMessages);
		if (lastGenericMessage != null)
			list.remove(lastGenericMessage);
		String message = RandomUtils.randomElement(list);
		lastGenericMessage = message;
		return replacePlayer(player, message);
	}

	private String getUniqueMessage(@Nullable Player player) {
		ArrayList<String> list = new ArrayList<>(uniqueMessages);
		if (lastUniqueMessage != null)
			list.remove(lastUniqueMessage);
		String message = RandomUtils.randomElement(list);
		lastUniqueMessage = message;
		return replacePlayer(player, message);
	}

	private String replacePlayer(@Nullable Player player, String message) {
		if (player == null)
			message = message.replaceAll(" \\[player]", "").trim();
		else
			message = message.replaceAll("\\[player]", player.getName()).trim();

		return message;
	}

}
