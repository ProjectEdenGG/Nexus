package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.NerdService;
import me.pugabyte.bncore.models.worldban.WorldBan;
import me.pugabyte.bncore.models.worldban.WorldBanService;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.OfflinePlayer;

import java.util.List;

@NoArgsConstructor
@Permission("group.moderator")
public class WorldUnbanCommand extends CustomCommand {
	public WorldBanService service = new WorldBanService();

	public WorldUnbanCommand(CommandEvent event) {
		super(event);
		PREFIX = Utils.getPrefix("WorldBan");
	}

	@Path("<player> [worldGroup]")
	void worldUnban(@Arg OfflinePlayer player, @Arg WorldGroup worldGroup) {
		WorldBan worldBan = service.get(player);
		List<WorldGroup> worldList = worldBan.getBans();

		if (worldList.size() == 0)
			error(player.getName() + " is not world banned");

		if (worldGroup != null && !worldList.contains(worldGroup))
			error(player.getName() + " is not world banned from " + worldGroup.toString());

		String message = "&a" + player().getName() + " &fhas world unbanned &a" + player.getName() + " &ffrom &a";
		if (worldGroup != null) {
			message += worldGroup.toString();
			worldList.remove(worldGroup);
		} else {
			message += String.join("&f, &a", worldBan.getBanNames());
			worldList.clear();
		}

		service.save(worldBan);

		String finalMessage = message;
		new NerdService().getOnlineNerdsWith("group.moderator").forEach(staff -> staff.send(finalMessage));
		Discord.send(message, DiscordId.Channel.STAFF_BRIDGE, DiscordId.Channel.STAFF_LOG);
	}

}
