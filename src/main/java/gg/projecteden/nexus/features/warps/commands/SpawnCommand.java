package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.WarpsService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
public class SpawnCommand extends CustomCommand implements Listener {
	private final WarpsService service = new WarpsService();

	public SpawnCommand(CommandEvent event) {
		super(event);
	}

	public enum SpawnType {
		HUB,
		SURVIVAL,
		MINIGAMES,
		CREATIVE
	}

	@Path("[world]")
	void run(@Arg("survival") SpawnType spawnType) {
		WarpType.NORMAL.get(spawnType.name()).teleportAsync(player());
	}

	@Path("force [player]")
	@Permission(Group.STAFF)
	void sudo(Player player) {
		runCommand(player, "spawn");
		runCommand(player, "spawn");
	}

	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPlayedBefore())
			return;

		Tasks.wait(1, () -> WarpType.NORMAL.get("spawn").teleportAsync(event.getPlayer()));
	}

}
