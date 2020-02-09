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
		line();
		send(PREFIX + "WorldBanned Players:");
		banList.forEach((uuid, worldList) -> {
			String playerName = Bukkit.getPlayer(uuid).getName();
			send("&e" + playerName + ": &e" + worldList.toString());
		});
		line();
	}

	@Path("<player>")
	void playerBannedWorlds(@Arg Player player) {
		line();
		List<WorldGroup> worldList = banList.get(player.getUniqueId().toString());
		send(PREFIX + "&e" + player.getName() + "&3: &e" + worldList.toString());
		line();
	}

	@Path("<worldGroup> <player>")
	void worldBan(@Arg WorldGroup worldGroup, @Arg Player player) {
		if (worldGroup.equals(WorldGroup.SURVIVAL))
			error("Cannot worldban from Survival");

		String uuid = player.getUniqueId().toString();
		List<WorldGroup> worldList = banList.get(uuid);
		if (worldList == null)
			worldList = new ArrayList<>();

		if (worldList.contains(worldGroup))
			error("Player is already banned from " + worldGroup.toString());

		worldList.add(worldGroup);
		banList.put(uuid, worldList);

		String message = PREFIX + "&e" + player().getName() + " &chas banned &e" + player.getName() + " &cfrom &e" + worldGroup.toString();
		new NerdService().getOnlineNerdsWith("group.moderator").forEach(staff -> staff.send(message));
		Discord.send(message, DiscordId.Channel.STAFF_BRIDGE, DiscordId.Channel.STAFF_LOG);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		List<WorldGroup> bannedWorlds = banList.get(player.getUniqueId().toString());
		if (bannedWorlds.contains(WorldGroup.get(player.getWorld()))) {
			runCommand(player, "warp spawn");
			send(player, "&cDue to your behavior, your access to this world has been restricted.");
		}
	}

}
