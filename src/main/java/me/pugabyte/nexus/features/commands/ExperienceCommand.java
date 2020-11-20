package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases("exp")
@Permission("group.staff")
public class ExperienceCommand extends CustomCommand {

	public ExperienceCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("get <player>")
	void get(Player player) {
		send(PREFIX + player.getName() + " has &e" + player.getLevel() + plural(" level", player.getLevel()));
	}

	@Path("set <player> <level>")
	void set(Player player, int amount) {
		player.setLevel(amount);
		common(player);
	}

	@Path("give <player> <amount>")
	void give(Player player, int amount) {
		player.giveExpLevels(amount);
		common(player);
	}

	@Path("take <player> <amount>")
	void take(Player player, int amount) {
		player.giveExpLevels(-amount);
		common(player);
	}

	private void common(Player player) {
		player.setExp(0f);
		send(PREFIX + player.getName() + " now has &e" + player.getLevel() + plural(" level", player.getLevel()));
	}

}
