package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.clientsideentities.ClientSideEntitiesConfig;
import gg.projecteden.nexus.models.clientsideentities.ClientSideEntitiesConfigService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

@Permission(Group.STAFF)
public class ClientSideEntitiesCommand extends CustomCommand {
	private final ClientSideEntitiesConfigService service = new ClientSideEntitiesConfigService();
	private ClientSideEntitiesConfig config;

	public ClientSideEntitiesCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			config = service.get(player());
	}

	public static final Set<EntityType> types = Set.of(EntityType.ITEM_FRAME, EntityType.ARMOR_STAND, EntityType.PAINTING);

	static {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.MINUTE, () -> {
			final World survival = Bukkit.getWorld("survival");
			if (survival == null)
				return;

//			new WorldGuardUtils(survival).getEntitiesInRegion("spawn").forEach(entity -> {
//				if (entity instanceof HangingEntity hanging)
//					hanging.setCanTick(false);
//			});
		});

		Tasks.repeat(TickTime.SECOND, TickTime.SECOND, () -> {
			final ClientSideEntitiesConfigService service = new ClientSideEntitiesConfigService();

			for (Player player : OnlinePlayers.where().world(Bukkit.getWorld("survival")).region("spawn").get()) {
				final ClientSideEntitiesConfig config = service.get(player);
				if (!config.isEnabled())
					continue;

				for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
					if (!types.contains(entity.getType()))
						continue;

					if (entity.getLocation().distance(player.getLocation()) <= config.getRadius())
						player.showEntity(Nexus.getInstance(), entity);
					else
						player.hideEntity(Nexus.getInstance(), entity);
				}
			}
		});
	}

	@SneakyThrows
	@Path("[enabled]")
	void toggle(Boolean enabled) {
		if (enabled == null)
			enabled = !config.isEnabled();

		config.setEnabled(enabled);
		service.save(config);

		// TODO
//		if (!enabled)
//			player().getHiddenEntities(Nexus.getInstance()).forEach(uuid -> player().showEntity(Nexus.getInstance(), uuid));

		send(PREFIX + (enabled ? "&aEnabled" : "&cDisabled"));
	}

	@Path("radius <radius>")
	void toggle(@Arg(min = 15, max = 50) int radius) {
		config.setRadius(radius);
		service.save(config);

		send(PREFIX + "Set entity render radius to &e" + radius + " blocks");
	}

}
