package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.worldban.WorldBan;
import gg.projecteden.nexus.models.worldban.WorldBanService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.NoArgsConstructor;
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
				send(" &e" + worldBan.getName() + "&7 - &3" + String.join("&e, &3", worldBan.getBanNames())));
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
				error("Cannot world ban from " + worldGroup);

			List<WorldGroup> worldList = worldBan.getBans();

			if (worldList.contains(worldGroup))
				error(player.getName() + " is already banned from " + worldGroup);

			worldList.add(worldGroup);
			service.save(worldBan);

			String message = "&a" + name() + " &fhas world banned &a" + player.getName() + " &ffrom &a" + worldGroup;
			Broadcast.log().prefix("Justice").message(message).send();

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
