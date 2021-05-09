package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.worldban.WorldBan;
import me.pugabyte.nexus.models.worldban.WorldBanService;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.OfflinePlayer;

import java.util.List;

@NoArgsConstructor
@Permission("group.moderator")
public class WorldUnbanCommand extends CustomCommand {
	public WorldBanService service = new WorldBanService();

	public WorldUnbanCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("WorldBan");
	}

	@Path("<player> [worldGroup]")
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

		Chat.broadcast(message, StaticChannel.STAFF);
		Discord.send(message, TextChannel.STAFF_BRIDGE, TextChannel.STAFF_LOG);
	}

}
