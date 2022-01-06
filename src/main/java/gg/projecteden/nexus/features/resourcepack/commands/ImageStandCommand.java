package gg.projecteden.nexus.features.resourcepack.commands;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.imagestand.ImageStand;
import gg.projecteden.nexus.models.imagestand.ImageStandService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;

import java.util.UUID;

@NoArgsConstructor
@Aliases("imagestands")
@Permission(Group.ADMIN)
public class ImageStandCommand extends CustomCommand implements Listener {
	private static final ImageStandService service = new ImageStandService();
	private ImageStand imageStand;

	public ImageStandCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			imageStand = getTargetImageStandRequired();
	}

	@Path("boundingBox draw particles [--particle] [--dustSize]")
	void boundingBox_draw_particles(
		@Switch @Arg("villager_happy") Particle particle,
		@Switch @Arg(".5") float dustSize
	) {
		imageStand.drawBoundingBox(particle, dustSize);
	}

	static {
		Bukkit.getMessenger().registerOutgoingPluginChannel(Nexus.getInstance(), "worldedit:cui");
	}

	@Path("boundingBox draw wecui")
	void boundingBox_draw_wecui() {
		final BoundingBox box = imageStand.getBoundingBox();
		final String message0 = "p|0|" + box.getMinX() + "|" + box.getMinY() + "|" + box.getMinZ() + "|-1";
		final String message1 = "p|1|" + (box.getMaxX() - 1) + "|" + (box.getMaxY() - 1) + "|" + (box.getMaxZ() - 1) + "|" + Math.ceil(box.getVolume());
		player().sendPluginMessage(Nexus.getInstance(), "worldedit:cui", message0.getBytes());
		player().sendPluginMessage(Nexus.getInstance(), "worldedit:cui", message1.getBytes());
	}

	private ImageStand getTargetImageStandRequired() {
		final ImageStand imageStand = service.getTargetStand(player());
		if (imageStand == null)
			throw new InvalidInputException("You must be looking at an image stand");
		return imageStand;
	}

	static {
		for (World world : Bukkit.getWorlds())
			for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class))
				onLoad(armorStand.getUniqueId());
	}

	@EventHandler
	public void onEntityAddToWorld(EntityAddToWorldEvent event) {
		onLoad(event.getEntity().getUniqueId());
	}

	private static void onLoad(UUID uuid) {
		final ImageStand imageStand = service.get(uuid);
		if (!imageStand.isActive()) {
			service.getCache().remove(uuid);
			return;
		}

		imageStand.updateBoundingBoxes();
	}

	static {
		Tasks.repeat(0, 2, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				final ImageStandService service = new ImageStandService();
				final ImageStand imageStand = service.getTargetStand(player);
				if (imageStand != null)
					imageStand.outlineFor(player);
				else
					service.removeOutlineFor(player);
			}
		});
	}

}
