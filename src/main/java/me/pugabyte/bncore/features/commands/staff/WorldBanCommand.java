package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.NerdService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// todo: if player is in the world they have been banned from, teleport them to spawn

@NoArgsConstructor
@Permission("group.moderator")
public class WorldBanCommand extends CustomCommand implements Listener {

	public static Map<String, List<WorldGroup>> banList = new HashMap<>();

	static {
		BNCore.registerListener(new WorldBanCommand());
	}

	public WorldBanCommand(CommandEvent event) {
		super(event);
	}

	@Path("list")
	void listAllBans() {
		if (banList.size() == 0)
			error("There are no world banned players");

		line();
		send(PREFIX + "World banned players:");
		banList.forEach((uuid, worldList) -> {
			if (worldList == null || worldList.size() == 0)
				return;
			String playerName = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
			send(" &e" + playerName + "&7 - &3" + worldList.stream().map(WorldGroup::toString).collect(Collectors.joining("&e, &3")));
		});
		line();
	}

	@Path("<player> [worldGroup]")
	void worldBan(@Arg Player player, @Arg WorldGroup worldGroup) {
		if (worldGroup == null) {
			List<WorldGroup> worldList = banList.get(player.getUniqueId().toString());
			if (worldList == null)
				error(player.getName() + " is not world banned");
			else
				send(PREFIX + "&e" + player.getName() + "&7 - &3" + worldList.stream().map(WorldGroup::toString).collect(Collectors.joining("&e, &3")));
		} else {
			if (worldGroup.equals(WorldGroup.SURVIVAL) || worldGroup.equals(WorldGroup.UNKNOWN))
				error("Cannot world ban from " + worldGroup.toString());

			String uuid = player.getUniqueId().toString();
			List<WorldGroup> worldList = banList.get(uuid);
			if (worldList == null)
				worldList = new ArrayList<>();

			if (worldList.contains(worldGroup))
				error(player.getName() + " is already banned from " + worldGroup.toString());

			worldList.add(worldGroup);
			banList.put(uuid, worldList);

			String message = "&a" + player().getName() + " &fhas world banned &a" + player.getName() + " &ffrom &a" + worldGroup.toString();
			new NerdService().getOnlineNerdsWith("group.moderator").forEach(staff -> staff.send(message));
			Discord.send(message, DiscordId.Channel.STAFF_BRIDGE, DiscordId.Channel.STAFF_LOG);
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		List<WorldGroup> bannedWorlds = banList.get(player.getUniqueId().toString());
		if (bannedWorlds == null)
			return;

		WorldGroup worldGroup = WorldGroup.get(player.getWorld());
		if (bannedWorlds.contains(worldGroup)) {
			runCommand(player, "warp spawn");
			Tasks.wait(10, () -> {
				send(player, "");
				send(player, "&cDue to your behavior, your access to " + worldGroup.toString() + " has been restricted.");
				send(player, "");
			});
		}
	}
}
