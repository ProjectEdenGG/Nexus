package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@Aliases("whatworld")
public class WorldCommand extends CustomCommand {

	public WorldCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("View what world you or another player are currently in")
	void run(@Optional("self") Player player) {
		String render = ScoreboardLine.WORLD.render(player).split(":")[1].trim();
		WorldGroup worldGroup = WorldGroup.of(player);
		if (!isStaff(player()) && isStaff(player) && worldGroup == WorldGroup.STAFF)
			send("&3" + (isSelf(player) ? "You are" : Nickname.of(player) + " is") + " in a staff world");
		else
			send("&3" + (isSelf(player) ? "You are" : Nickname.of(player) + " is") + " in world &e" + render + " &3in group &f" + worldGroup.getIcon() + " &e" + camelCase(worldGroup));
	}

	@Description("List all worlds")
	@Permission(Group.STAFF)
	void list() {
		String list = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.joining(", "));
		send(json(PREFIX + "Loaded worlds: ").next(list).copy(list).hover("&fClick to copy"));
	}

	@Description("Lists all world groups and view their custom icon")
	void groups() {
		send(PREFIX + "Groups");
		for (WorldGroup worldGroup : WorldGroup.values())
			send(" " + worldGroup.getIcon() + " &e" + camelCase(worldGroup));
	}

}
