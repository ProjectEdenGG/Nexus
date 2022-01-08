package gg.projecteden.nexus.features.resourcepack.commands;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.imagestand.ImageStand;
import gg.projecteden.nexus.models.imagestand.ImageStand.ImageSize;
import gg.projecteden.nexus.models.imagestand.ImageStandService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@Aliases("imagestands")
@Permission(Group.ADMIN)
public class ImageStandCommand extends CustomCommand implements Listener {
	private static final ImageStandService service = new ImageStandService();
	private ImageStand imageStand;

	public ImageStandCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("create <id> <size>")
	void create(String id, ImageSize size) {
		final ArmorStand image = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);
		final ArmorStand outline = ArmorStandEditorCommand.summon(image.getLocation(), armorStand -> {
			armorStand.setHeadPose(image.getHeadPose());
		});

		final ImageStand imageStand = new ImageStand(image.getUniqueId(), outline.getUniqueId(), id, size);
		service.save(imageStand);
		service.cache(imageStand);
		send(PREFIX + "Created new stand");
	}

	@Path("boundingBox modify [--x] [--y] [--z] [--posX] [--posY] [--posZ] [--negX] [--negY] [--negZ] [--all]")
	void boundingBox_modify(
		@Switch double x, @Switch double y, @Switch double z,
		@Switch double negX, @Switch double negY, @Switch double negZ,
		@Switch double posX, @Switch double posY, @Switch double posZ,
		@Switch double all
	) {
		imageStand = getTargetImageStandRequired();
		BoundingBox box = imageStand.getBoundingBox();
		if (box == null)
			box = new BoundingBox().shift(imageStand.getImageStandRequired().getEyeLocation());

		if (all != 0) {
			negX += all;
			negY += all;
			negZ += all;
			posX += all;
			posY += all;
			posZ += all;
		}

		if (x != 0) {
			negX += x;
			posX += x;
		}

		if (y != 0) {
			negY += y;
			posY += y;
		}

		if (z != 0) {
			negZ += z;
			posZ += z;
		}

		box.expand(negX, negY, negZ, posX, posY, posZ);
		imageStand.updateBoundingBoxes();
		service.save(imageStand);
		send(PREFIX + "Modified bounding box");
	}

	private static final Map<UUID, List<Integer>> particleTaskIds = new HashMap<>();

	@Path("boundingBox draw particles [--particle] [--dustSize] [--stop]")
	void boundingBox_draw_particles(
		@Switch @Arg("villager_happy") Particle particle,
		@Switch @Arg(".5") float dustSize,
		@Switch boolean stop
	) {
		imageStand = getTargetImageStandRequired();
		if (stop) {
			if (!particleTaskIds.containsKey(imageStand.getUuid()))
				error("No particle task for that image stand running");

			particleTaskIds.get(imageStand.getUuid()).forEach(Tasks::cancel);
			particleTaskIds.remove(imageStand.getUuid());
			send(PREFIX + "Particle task cancelled");
		} else {
			particleTaskIds.put(imageStand.getUuid(), imageStand.drawBoundingBox(particle, dustSize));
		}
	}

	static {
		Bukkit.getMessenger().registerOutgoingPluginChannel(Nexus.getInstance(), "worldedit:cui");
	}

	@Path("boundingBox draw wecui")
	void boundingBox_draw_wecui() {
		imageStand = getTargetImageStandRequired();
		final BoundingBox box = imageStand.getBoundingBox();
		final String message0 = "p|0|" + box.getMinX() + "|" + box.getMinY() + "|" + box.getMinZ() + "|-1";
		final String message1 = "p|1|" + (box.getMaxX() - 1) + "|" + (box.getMaxY() - 1) + "|" + (box.getMaxZ() - 1) + "|" + Math.ceil(box.getVolume());
		player().sendPluginMessage(Nexus.getInstance(), "worldedit:cui", message0.getBytes());
		player().sendPluginMessage(Nexus.getInstance(), "worldedit:cui", message1.getBytes());
	}

	@Path("boundingBox update")
	void boundingBox_update() {
		imageStand = getTargetImageStandRequired();
		imageStand.updateBoundingBoxes();
	}

	@Path("outline update")
	void outline_update() {
		imageStand = getTargetImageStandRequired();
		final ArmorStand image = imageStand.getImageStandRequired();
		final ArmorStand outline = imageStand.getOutlineStandRequired();
		outline.teleport(image);
		outline.setHeadPose(image.getHeadPose());
	}

	@Path("stands yaw <yaw>")
	void stands_yaw(float yaw) {
		imageStand = getTargetImageStandRequired();
		final ArmorStand image = imageStand.getImageStandRequired();
		final ArmorStand outline = imageStand.getOutlineStandRequired();

		final Location location = image.getLocation();
		location.setYaw(yaw);

		image.teleport(location);
		outline.teleport(location);
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

	@TabCompleterFor(ImageSize.class)
	List<String> tabCompleteImageSize(String filter) {
		return tabCompleteEnum(filter, ImageSize.class, size -> size.name().replaceFirst("_", ""));
	}

	@ConverterFor(ImageSize.class)
	ImageSize convertToImageSize(String value) {
		return convertToEnum("_" + value, ImageSize.class);
	}

}
