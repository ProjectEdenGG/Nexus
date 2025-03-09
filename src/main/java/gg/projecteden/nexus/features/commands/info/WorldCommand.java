package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

	@Path("uuid [world]")
	void uuid(@Arg("current") World world) {
		String uuid = world.getUID().toString();
		send(json(PREFIX + "&e" + uuid).copy(uuid).hover("Click to copy"));
	}

	@Path("[player]")
	@Description("View what world you or another player are currently in")
	void run(@Arg("self") Player target) {
		String render = ScoreboardLine.WORLD.render(target).split(":")[1].trim();
		WorldGroup worldGroup = WorldGroup.of(target);

		boolean isSelf = isSelf(target);
		boolean isSelfStaff = isStaff();
		boolean isTargetStaff = isStaff(target);
		boolean isTargetInHiddenWorld = isInHiddenWorld(target);
		String targetNick = Nickname.of(target);

		if (!isSelfStaff && isTargetStaff && isTargetInHiddenWorld)
			send("&3" + (isSelf ? "You are" : targetNick + " is") + " in a staff world");
		else
			send("&3" + (isSelf ? "You are" : targetNick + " is") + " in world &e" + render + " &3in group &f" + worldGroup.getIcon() + " &e" + camelCase(worldGroup));
	}

	private boolean isInHiddenWorld(Player target) {
		WorldGroup worldGroup = WorldGroup.of(target);
		if (worldGroup == WorldGroup.STAFF || worldGroup == WorldGroup.UNKNOWN)
			return true;

		EdenEvent edenEvent = EdenEvent.of(target);
		if (edenEvent != null && edenEvent.isBeforeEvent())
			return true;

		return false;
	}

	@Path("list")
	@Description("List all worlds")
	@Permission(Group.STAFF)
	void list() {
		String list = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.joining(", "));
		send(json(PREFIX + "Loaded worlds: ").next(list).copy(list).hover("&fClick to copy"));
	}

	@Path("(groups|icons)")
	@Description("Lists all worlds that have a custom icon")
	void groups() {
		send(PREFIX + "Groups");
		for (WorldGroup worldGroup : WorldGroup.values())
			send(" " + worldGroup.getIcon() + " &e" + camelCase(worldGroup));
	}

}
