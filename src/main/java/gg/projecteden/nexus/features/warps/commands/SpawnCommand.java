package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.WarpsService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;

@NoArgsConstructor
public class SpawnCommand extends CustomCommand implements Listener {
	private final WarpsService service = new WarpsService();

	public SpawnCommand(CommandEvent event) {
		super(event);
	}

	@AllArgsConstructor
	public enum SpawnType {
		HUB(WorldGroup.SERVER),
		SURVIVAL(WorldGroup.SURVIVAL),
		MINIGAMES(WorldGroup.MINIGAMES),
		CREATIVE(WorldGroup.CREATIVE),
		;

		@Getter
		final WorldGroup worldGroup;

		public static SpawnType of(WorldGroup worldGroup) {
			return Arrays.stream(SpawnType.values())
				.filter(spawnType -> spawnType.getWorldGroup() == worldGroup)
				.findFirst()
				.orElse(HUB);
		}
	}

	@Path("[world]")
	void run(SpawnType spawnType) {
		if (spawnType == null)
			spawnType = SpawnType.of(worldGroup());

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

		Tasks.wait(1, () -> Warps.hub(event.getPlayer()));
	}

}
