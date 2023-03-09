package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.justice.misc._JusticeCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.worldban.WorldBan;
import gg.projecteden.nexus.models.worldban.WorldBanService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.OfflinePlayer;

import java.util.List;

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class WorldUnbanCommand extends _JusticeCommand {
	public WorldBanService service = new WorldBanService();

	public WorldUnbanCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("WorldBan");
	}

	@Path("<player> [worldGroup]")
	@Description("Unban a player from a world group")
	void worldUnban(OfflinePlayer player, WorldGroup worldGroup) {
		WorldBan worldBan = service.get(player);
		List<WorldGroup> worldList = worldBan.getBans();

		if (worldList.size() == 0)
			error(player.getName() + " is not world banned");

		if (worldGroup != null && !worldList.contains(worldGroup))
			error(player.getName() + " is not world banned from " + worldGroup.toString());

		String message = "&a" + name() + " &fhas world unbanned &a" + player.getName() + " &ffrom &a";
		if (worldGroup != null) {
			message += worldGroup.toString();
			worldList.remove(worldGroup);
		} else {
			message += String.join("&f, &a", worldBan.getBanNames());
			worldList.clear();
		}

		service.save(worldBan);

		Broadcast.log().prefix("Justice").message(message).send();
	}

}
