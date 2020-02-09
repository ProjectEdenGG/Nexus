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
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
@Permission("group.moderator")
public class WorldUnbanCommand extends CustomCommand {

	public WorldUnbanCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> [worldGroup]")
	void worldUnban(@Arg Player player, @Arg WorldGroup worldGroup) {
		String uuid = player.getUniqueId().toString();
		List<WorldGroup> worldList = WorldBanCommand.banList.get(uuid);
		if (worldList == null || worldList.size() == 0)
			return;

		String message = PREFIX + "&e" + player().getName() + " &chas unbanned &e" + player.getName() + " &cfrom &e";
		if (worldGroup != null) {
			message += worldGroup.toString();
			worldList.remove(worldGroup);
		} else {
			message += worldList.toString();
			worldList.clear();
		}

		WorldBanCommand.banList.put(uuid, worldList);

		String finalMessage = message;
		new NerdService().getOnlineNerdsWith("group.moderator").forEach(staff -> staff.send(finalMessage));
		Discord.send(message, DiscordId.Channel.STAFF_BRIDGE, DiscordId.Channel.STAFF_LOG);
	}

}
