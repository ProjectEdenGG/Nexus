package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@Aliases("whatworld")
public class WorldCommand extends CustomCommand {

	public WorldCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Player player) {
		String render = ScoreboardLine.WORLD.render(player).split(":")[1].trim();
		WorldGroup worldGroup = WorldGroup.of(player);
		if (!isStaff(player()) && isStaff(player) && worldGroup == WorldGroup.STAFF)
			send("&3" + (isSelf(player) ? "You are" : player.getName() + " is") + " in a staff world");
		else
			send("&3" + (isSelf(player) ? "You are" : player.getName() + " is") + " in world &e" + render + " &3in group &e" + camelCase(worldGroup));
	}

	@Path("list")
	@Permission("group.staff")
	void list() {
		String list = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.joining(", "));
		send(json(PREFIX + "Loaded worlds: ").next(list).copy(list).hover("&fClick to copy"));
	}

}
