package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
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
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Aliases("welc")
@NoArgsConstructor
@Permission("group.staff")
public class WelcomeCommand extends CustomCommand {
	private static String lastMessage = null;

	List<String> messages = new ArrayList<>() {{
		add("Welcome to the server [player]! Make sure to read the /rules and feel free to ask questions.");
		add("Welcome to Project Eden [player]! Please take a moment to read the /rules and feel free to ask any questions you have.");
		add("Hi [player], welcome to Project Eden :) Please read the /rules and ask if you have any questions.");
		add("Hey [player]! Welcome to Project Eden. Be sure to read the /rules and don't be afraid to ask questions ^^");
		add("Hi there [player] :D Welcome to Project Eden. Make sure to read the /rules and feel free to ask questions.");
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

			if (!new CooldownService().check(StringUtils.getUUID0(), "bumpReminder", TickTime.DAY))
				return;

			String url = "https://docs.google.com/document/d/1MVFG2ipdpCY42cUzZyVsIbjVlPRCiN0gmYL89sJNRTw/edit?usp=sharing";
			Broadcast.staffIngame().message("").send();
			Broadcast.staffIngame().message("&eHi Staff. &3It looks like there's a few of you online. Time to &ebump the server!").send();
			Broadcast.staffIngame().message(new JsonBuilder("&eClick me").url(url).group().next(" &3for the instructions")).send();
			Broadcast.staffIngame().message("").send();
		});
	}

	@Path("[player]")
	void welcome(Player player) {
		if (player != null) {
			if (Rank.of(player) != Rank.GUEST)
				error("Prevented accidental welcome");
			if (new HoursService().get(player).has(TickTime.HOUR))
				error("Prevented accidental welcome");
		}

		if (new CooldownService().check(StringUtils.getUUID0(), "welc", TickTime.SECOND.x(20))) {
			String message = getMessage();
			if (player == null)
				message = message.replaceAll(" \\[player]", "");
			else
				message = message.replaceAll("\\[player]", player.getName());

			runCommand("ch qm g " + message);
		} else {
			if (player == null)
				runCommand("ch qm g Welcome to the server!");
			else
				runCommand("ch qm g Welcome to the server, " + player.getName());
		}
	}

	private String getMessage() {
		ArrayList<String> list = new ArrayList<>(messages);
		if (lastMessage != null)
			list.remove(lastMessage);
		String message = RandomUtils.randomElement(list);
		lastMessage = message;
		return message;
	}

}
