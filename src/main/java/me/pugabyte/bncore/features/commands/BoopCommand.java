package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

//TODO: cooldown

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
	void boop(@Arg Player playerArg, @Arg String flag) {
		boolean anon = false;
		if (flag != null && flag.equalsIgnoreCase("-s"))
			anon = true;

		if (playerArg.equals(player()))
			error("You cannot boop yourself!");

		if (player().getWorld().equals(Minigames.getGameworld()))
			error("You cannot boop in gameworld!");

		if (playerArg.getWorld().equals(Minigames.getGameworld()))
			error("You cannot boop " + playerArg.getName() + " (in gameworld)");

		if (anon) {
			send("You anonymously boop'd " + playerArg.getName());
			send(playerArg, "Somebody boop'd you");
		} else {
			send("You boop'd " + playerArg.getName());
			send(playerArg, player().getName() + " boop'd you");
		}

		playerArg.playSound(playerArg.getLocation(), Sound.BLOCK_NOTE_XYLOPHONE, 10.0F, 0.1F);
	}
}
