package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BoopCommand extends CustomCommand {

	public BoopCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send("&4Correct Usage: &c/boop <player> [-s]");
		send("&4-s&c: makes the boop anonymous");
	}

	@Path("<player> [flag]")
	@Cooldown(value = @Part(value = Time.SECOND, x = 5), bypass = "group.admin")
	void boop(Player playerArg, String flag) {
		boolean anon = false;
		if (flag != null)
			if (flag.equalsIgnoreCase("-s") || flag.equalsIgnoreCase("-a"))
				anon = true;
			else
				showUsage();

		if (isSelf(playerArg))
			error("You cannot boop yourself!");

		if (isPlayer() && Minigames.isMinigameWorld(player().getWorld()))
			error("You cannot boop in minigames!");

		if (Minigames.isMinigameWorld(playerArg.getWorld()))
			error("You cannot boop " + playerArg.getName() + " (in minigames)");

		if (anon) {
			send(PREFIX + "&3You anonymously booped &e" + playerArg.getName());
			send(playerArg, PREFIX + "&eSomebody &3booped you");
		} else {
			send(PREFIX + "&3You booped &e" + playerArg.getName());
			send(playerArg, PREFIX + "&e" + player().getName() + " &3booped you");
		}

		playerArg.playSound(playerArg.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10.0F, 0.1F);
	}
}
