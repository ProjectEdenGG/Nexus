package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collection;

public class MgInviteCommand extends CustomCommand {
	static String command;

	public MgInviteCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void mginvite() {
		boolean isMinigameNight = false;
		LocalDateTime date = LocalDateTime.now();
		DayOfWeek dow = date.getDayOfWeek();

		if (dow.equals(DayOfWeek.SATURDAY)) {
			int hour = date.getHour();
			if (hour > 15 && hour < 18) {
				isMinigameNight = true;
			}
		}

		boolean canUse = false;
		if (!isMinigameNight)
			canUse = true;
		if (player().hasPermission("mginvite.use"))
			canUse = true;

		if (!canUse)
			error("You do not have permission to use this command!");

		WorldGuardUtils WGUtils = new WorldGuardUtils(player().getWorld());
		if (!WGUtils.isInRegion(player().getLocation(), "minigamelobby"))
			error("You must be in the Minigame Lobby to use this command");

		Collection<Player> players = WGUtils.getPlayersInRegion("minigamelobby");
		int count = players.size() - 1;
		if (count == 0)
			error("There is no one to invite!");

		String message;
		if (WGUtils.isInRegion(player().getLocation(), "screenshot")) {
			command = "warp screenshot";
			message = "take a screenshot";
		} else {
			// TODO: 1.13+ switch to getTargetBlockExact(6);
			Block targetBlock = player().getTargetBlock(null, 6);
//			float pitch = player().getLocation().getPitch();

			if (!Utils.isSign(targetBlock.getType()))
				error("Look at a sign!");

			Sign sign = (Sign) targetBlock.getState();
			String line2 = StringUtils.stripColor(sign.getLine(1)).toLowerCase();
			if (line2.contains("screenshot"))
				error("Stand in the screenshot area then run the command (sign not needed)");
			if (!line2.contains("join"))
				error("Cannot parse sign. If you believe this is an error, make a GitHub ticket with information and screenshots.");

			String prefix = "";
			String line1 = StringUtils.stripColor(sign.getLine(0)).toLowerCase();
			if (line1.contains("[minigame]"))
				prefix = "mgm";
			else if (line1.contains("< minigames >"))
				prefix = "newmgm";
			else
				error("Cannot parse sign. If you believe this is an error, make a GitHub ticket with information and screenshots.");

			String line3 = StringUtils.stripColor(sign.getLine(2)) + StringUtils.stripColor(sign.getLine(3));
			command = prefix + " join " + line3;
			message = line3;
		}

		String sender = player().getName();
		send("&3Invite sent to &e" + count + " &3players for &e" + message);
		for (Player player : players) {
			if (player.equals(player()))
				continue;

			send(player, json("")
					.newline()
					.next(" &e" + sender + " &3has invited you to &e" + message).group()
					.newline()
					.next("&e Click here to &a&laccept")
					.command("/mgaccept")
					.hover("&eClick &3to accept"));
		}


	}
}
