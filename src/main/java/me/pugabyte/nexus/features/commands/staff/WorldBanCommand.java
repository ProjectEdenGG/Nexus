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
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

@NoArgsConstructor
@Permission("group.moderator")
public class WorldBanCommand extends CustomCommand implements Listener {
	public WorldBanService service = new WorldBanService();

	public WorldBanCommand(CommandEvent event) {
		super(event);
	}

	@Path("list")
	void listAllBans() {
		List<WorldBan> bans = service.getAll();

		if (bans.size() == 0)
			error("There are no world banned players");

		line();
		send(PREFIX + "World banned players:");
		bans.forEach(worldBan ->
				send(" &e" + worldBan.getOfflinePlayer().getName() + "&7 - &3" + String.join("&e, &3", worldBan.getBanNames())));
		line();
	}

	@Path("<player> [worldGroup]")
	void worldBan(OfflinePlayer player, WorldGroup worldGroup) {
		WorldBan worldBan = service.get(player);

		if (worldGroup == null) {
			if (worldBan.getBans().size() == 0)
				error(player.getName() + " is not world banned");
			else
				send(PREFIX + "&e" + player.getName() + "&7 - &3" + String.join("&e, &3", worldBan.getBanNames()));
		} else {
			if (worldGroup.equals(WorldGroup.SURVIVAL) || worldGroup.equals(WorldGroup.UNKNOWN))
				error("Cannot world ban from " + worldGroup.toString());

			List<WorldGroup> worldList = worldBan.getBans();

			if (worldList.contains(worldGroup))
				error(player.getName() + " is already banned from " + worldGroup.toString());

			worldList.add(worldGroup);
			service.save(worldBan);

			String message = "&a" + name() + " &fhas world banned &a" + player.getName() + " &ffrom &a" + worldGroup.toString();
			Chat.broadcast(message, StaticChannel.STAFF);
			Discord.send(message, TextChannel.STAFF_BRIDGE, TextChannel.STAFF_LOG);

			if (player.isOnline() && player.getPlayer() != null)
				if (WorldGroup.of(player.getPlayer()).equals(worldGroup)) {
					if (player.getPlayer().getVehicle() != null)
						player.getPlayer().getVehicle().removePassenger(player.getPlayer());

					removeFromBannedWorld(player.getPlayer(), worldGroup);
				}
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		WorldBan worldBan = new WorldBanService().get(player);

		WorldGroup worldGroup = WorldGroup.of(player);
		if (worldBan.getBans().contains(worldGroup))
			removeFromBannedWorld(player, worldGroup);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();

		WorldBan worldBan = new WorldBanService().get(player);
		if (worldBan.getBans().size() == 0)
			return;

		WorldGroup worldGroup = WorldGroup.of(player);
		if (worldBan.getBans().contains(worldGroup))
			Tasks.wait(5, () -> removeFromBannedWorld(player, worldGroup));
	}

	public void removeFromBannedWorld(Player player, WorldGroup worldGroup) {

		runCommand(player, "warp spawn");
		Tasks.wait(10, () -> {
			send(player, "");
			send(player, "&cDue to your behavior, your access to " + worldGroup.toString() + " has been restricted.");
			send(player, "");
		});
	}

}
