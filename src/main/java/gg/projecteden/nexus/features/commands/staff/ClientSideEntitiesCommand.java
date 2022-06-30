package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.clientsideentities.ClientSideEntitiesConfig;
import gg.projecteden.nexus.models.clientsideentities.ClientSideEntitiesConfigService;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

@Permission(Group.STAFF)
public class ClientSideEntitiesCommand extends CustomCommand {
	private final ClientSideEntitiesConfigService service = new ClientSideEntitiesConfigService();
	private ClientSideEntitiesConfig config;
	private static boolean ENABLED = true;

	public ClientSideEntitiesCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			config = service.get(player());
	}

	public static final Set<EntityType> types = Set.of(EntityType.ITEM_FRAME, EntityType.ARMOR_STAND, EntityType.PAINTING);

	static {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(5), () -> {
			if (!ENABLED)
				return;

			final ClientSideEntitiesConfigService service = new ClientSideEntitiesConfigService();

			for (Player player : OnlinePlayers.where().world(Bukkit.getWorld("buildadmin")).get()) {
				final ClientSideEntitiesConfig config = service.get(player);
				if (!config.isEnabled())
					continue;

				for (Entity entity : player.getNearbyEntities(150, 150, 150)) {
					if (!types.contains(entity.getType()))
						continue;

					if (entity.getLocation().distance(player.getLocation()) >= 20)
						PacketUtils.entityDestroy(player, entity.getEntityId());
				}
			}
		});
	}

	@Path("toggle [enabled]")
	void toggle(Boolean enabled) {
		if (enabled == null)
			enabled = !config.isEnabled();

		config.setEnabled(enabled);
		service.save(config);

		send(PREFIX + (enabled ? "&aEnabled" : "&cDisabled"));
	}

	@Path("view")
	void view() {
		Location location = location();
		player().teleportAsync(new Location(world(), 0, 4, 0)).thenRun(() -> Tasks.wait(10, () -> player().teleportAsync(location)));
	}

	@Path("serverToggle [enabled]")
	void serverToggle(Boolean enabled) {
		if (enabled == null)
			enabled = !ENABLED;

		ENABLED = enabled;

		send(PREFIX + (enabled ? "&aEnabled" : "&cDisabled"));
	}

}
