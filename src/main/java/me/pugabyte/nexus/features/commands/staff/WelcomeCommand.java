package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Aliases("welc")
@NoArgsConstructor
@Permission("group.staff")
public class WelcomeCommand extends CustomCommand {
	private static String lastMessage = null;

	List<String> messages = new ArrayList<String>() {{
		add("Welcome to the server [player]! Make sure to read the /rules and feel free to ask questions.");
		add("Welcome to Bear Nation [player]! Please take a moment to read the /rules and feel free to ask any questions you have.");
		add("Hi [player], welcome to Bear Nation :) Please read the /rules and ask if you have any questions.");
		add("Hey [player]! Welcome to Bear Nation. Be sure to read the /rules and don't be afraid to ask questions ^^");
		add("Hi there [player] :D Welcome to Bear Nation. Make sure to read the /rules and feel free to ask questions.");
	}};

	public WelcomeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(Time.MINUTE, Time.MINUTE, () -> {
			if (Bukkit.getOnlinePlayers().stream().filter(player ->
					PlayerUtils.isModeratorGroup(player) &&
							!player.getName().equals("KodaBear") &&
							!AFK.get(player).isAfk()
			).count() < 4)
				return;

			if (!new CooldownService().check(Nexus.getUUID0(), "bumpReminder", Time.DAY))
				return;

			String url = "https://docs.google.com/document/d/1MVFG2ipdpCY42cUzZyVsIbjVlPRCiN0gmYL89sJNRTw/edit?usp=sharing";
			Chat.broadcastIngame("", StaticChannel.STAFF);
			Chat.broadcastIngame("&eHi Staff. &3It looks like there's a few of you online. Time to &ebump the server!", StaticChannel.STAFF);
			Chat.broadcastIngame(new JsonBuilder("&eClick me").url(url).group().next(" &3for the instructions"), StaticChannel.STAFF);
			Chat.broadcastIngame("", StaticChannel.STAFF);
		});
	}

	@Path("[player]")
	void welcome(Player player) {
		if (player != null) {
			if (Rank.of(player) != Rank.GUEST)
				error("Prevented accidental welcome");
			if (((Hours) new HoursService().get(player)).getTotal() > (60 * 60))
				error("Prevented accidental welcome");
		}

		if (new CooldownService().check(Nexus.getUUID0(), "welc", Time.SECOND.x(20))) {
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
