package gg.projecteden.nexus.features.commands;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockMarkersCommand extends CustomCommand {
	private final ParticleService service = new ParticleService();
	private final ParticleOwner user;

	public BlockMarkersCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	@Path("[state]")
	void toggle(Boolean state) {
		if (state == null)
			state = !user.isShowBlockMarkers();

		user.setShowBlockMarkers(state);
		service.save(user);
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled"));
	}

	static {
		Tasks.repeat(2, TickTime.SECOND.x(4), () -> {
			for (ParticleOwner user : new ParticleService().getOnline()) {
				if (!user.isShowBlockMarkers())
					continue;

				final Player player = user.getOnlinePlayer();

				for (Material material : List.of(Material.BARRIER, Material.LIGHT))
					for (int x = -3; x <= 3; x++)
						for (int y = -3; y <= 3; y++) {
							BlockUtils.getBlocksInChunk(player.getLocation().add(x * 16, 0, y * 16).getChunk(), material)
								.forEach(location -> new ParticleBuilder(Particle.BLOCK_MARKER)
									.receivers(player)
									.data(material.createBlockData())
									.location(location.toCenterLocation())
									.spawn());
						}
			}
		});
	}

}
