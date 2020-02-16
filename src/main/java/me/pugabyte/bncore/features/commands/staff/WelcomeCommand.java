package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Aliases("welc")
@Permission("group.staff")
public class WelcomeCommand extends CustomCommand {
	List<String> messages = new ArrayList<String>() {{
		add("Welcome to the server [player]! Make sure to read the /rules and feel free to ask questions.");
		add("Welcome to Bear Nation, [player]! Please take a moment to read the /rules and feel free to ask any questions you have.");
		add("Hi [player], welcome to Bear Nation :) Please read the /rules and ask if you have any questions.");
		add("Hey [player]! Welcome to Bear Nation. Be sure to read the /rules and don't be afraid to ask questions ^^");
		add("Hi there [player] :D Welcome to Bear Nation. Make sure to read the /rules and feel free to ask questions.");
	}};

	public WelcomeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void welcome(Player player) {
		if (!player.hasPermission("rank.guest"))
			error("Prevented accidental welcome");
		if (((Hours) new HoursService().get(player)).getTotal() < 60 * 60)
			error("Prevented accidental welcome");

		try {
			new CooldownService().check("staff", "welc-" + player.getUniqueId(), 30 * 20);

			String message = ((String) Utils.getRandomElement(messages)).replaceAll("\\[player]", player.getName());
			runCommand("ch qm g " + message);
		} catch (CooldownException ex) {
			runCommand("ch qm g Welcome to the server, " + player.getName());
		}
	}
}
