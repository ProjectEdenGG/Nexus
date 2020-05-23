package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.features.scoreboard.ScoreboardLine;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.entity.Player;

@Aliases("whatworld")
public class WorldCommand extends CustomCommand {

	public WorldCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void world(@Arg("self") Player player) {
		String render = ScoreboardLine.WORLD.render(player).split(":")[1].trim();
		send("&3" + (isSelf(player) ? "You are" : player.getName() + " is") + " in world &e" + render + " &3in group &e" + WorldGroup.get(player));
	}

}
