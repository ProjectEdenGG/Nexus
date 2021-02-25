package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.features.scoreboard.ScoreboardLine;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;

@Aliases("whatworld")
public class WorldCommand extends CustomCommand {

	public WorldCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Player player) {
		String render = ScoreboardLine.WORLD.render(player).split(":")[1].trim();
		send("&3" + (isSelf(player) ? "You are" : name() + " is") + " in world &e" + render + " &3in group &e" + WorldGroup.get(player));
	}

}
