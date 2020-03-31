package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

@Permission("group.moderator")
public class MgSign extends CustomCommand {

	public MgSign(CommandEvent event) {
		super(event);
	}

	@Path("join <arena>")
	void join(@Arg Arena arena) {
		Sign sign = getTargetSign(player());
		sign.setLine(0, StringUtils.colorize("&0&l< &1Minigames &0&l>"));
		sign.setLine(1, StringUtils.colorize("&aJoin"));
		String arenaName = arena.getName();
		if (arenaName.length() > 15) {
			sign.setLine(2, arenaName.substring(0, 15));
			sign.setLine(3, arenaName.substring(15));
		} else {
			sign.setLine(2, arena.getName());
			sign.setLine(3, "");
		}

		sign.update();
	}

	@Path("quit")
	void quit() {
		Sign sign = getTargetSign(player());
		sign.setLine(0, StringUtils.colorize("&0&l< &1Minigames &0&l>"));
		sign.setLine(1, StringUtils.colorize("&aQuit"));
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}

	private Sign getTargetSign(Player player) {
		Block targetBlock = player.getTargetBlock(null, 5);
		Material material = targetBlock.getType();
		if (Utils.isNullOrAir(material) && !Utils.isSign(material))
			error(player, "Look at a sign!");
		return (Sign) targetBlock.getState();
	}
}
