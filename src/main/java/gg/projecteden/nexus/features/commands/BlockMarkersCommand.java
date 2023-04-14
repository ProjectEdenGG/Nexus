package gg.projecteden.nexus.features.commands;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Location;
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

	@NoLiterals
	@Description("View block marker particles (i.e. barriers, light)")
	void toggle(@Optional Boolean state) {
		if (state == null)
			state = !user.isShowBlockMarkers();

		user.setShowBlockMarkers(state);
		service.save(user);
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled"));
	}

	private static final int MAX_MARKERS = 1000;

	static {
		Tasks.repeat(2, TickTime.SECOND.x(4), () -> {
			for (ParticleOwner user : new ParticleService().getOnline()) {
				int count = 0;
				if (!user.isShowBlockMarkers())
					continue;

				final Player player = user.getOnlinePlayer();

				for (Material material : List.of(Material.BARRIER, Material.LIGHT))
					for (int x = -3; x <= 3; x++)
						for (int y = -3; y <= 3; y++) {
							final List<Location> blocks = BlockUtils.getBlocksInChunk(player.getLocation().add(x * 16, 0, y * 16).getChunk(), material, MAX_MARKERS);
							count += blocks.size();

							if (count >= MAX_MARKERS)
								return;

							blocks.forEach(location -> new ParticleBuilder(Particle.BLOCK_MARKER)
								.receivers(player)
								.data(material.createBlockData())
								.location(location.toCenterLocation())
								.spawn());
						}
			}
		});
	}

}
