package gg.projecteden.nexus.features.resourcepack.commands;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.UUID;

import static gg.projecteden.utils.Nullables.isNullOrEmpty;

@NoArgsConstructor
@Aliases("imagestands")
@Permission(Group.ADMIN)
public class ImageStandCommand extends CustomCommand implements Listener {
	private static final ImageStandService service = new ImageStandService();
	private ImageStand imageStand;

	public ImageStandCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("create <id> <size> [--outline]")
	void create(String id, ImageSize size, @Switch boolean outline) {
		final ArmorStand imageArmorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);
		UUID outlineUuid = null;
		if (outline)
			outlineUuid = ArmorStandEditorCommand.summon(imageArmorStand.getLocation(), armorStand ->
				armorStand.setHeadPose(imageArmorStand.getHeadPose())).getUniqueId();

		final ImageStand imageStand = new ImageStand(imageArmorStand.getUniqueId(), outlineUuid, id, size);
		service.save(imageStand);
		service.cache(imageStand);
		send(PREFIX + "Created new stand");
	}

	@Path("delete [--id]")
	void delete(@Switch String id) {
		getImageStand(id);
		imageStand.stopDrawing();
		service.delete(imageStand);
		send(PREFIX + "Deleted image stand " + imageStand.getId());
	}

	@Path("shift [--id] [--x] [--y] [--z]")
	void shift(@Switch String id, @Switch double x, @Switch double y, @Switch double z) {
		getImageStand(id);

		for (ArmorStand armorStand : imageStand.getArmorStands())
			armorStand.teleport(armorStand.getLocation().add(x, y, z));

		for (BoundingBox boundingBox : imageStand.getBoundingBoxes())
			boundingBox.shift(x, y, z);

		service.save(imageStand);
		imageStand.updateBoundingBoxes();
		send(PREFIX + "Shifted image stand");
	}

	@Path("boundingBox modify [--id] [--x] [--y] [--z] [--posX] [--posY] [--posZ] [--negX] [--negY] [--negZ] [--all]")
	void boundingBox_modify(
		@Switch String id,
		@Switch double x, @Switch double y, @Switch double z,
		@Switch double negX, @Switch double negY, @Switch double negZ,
		@Switch double posX, @Switch double posY, @Switch double posZ,
		@Switch double all
	) {
		getImageStand(id);
		BoundingBox box = imageStand.getBoundingBox();
		if (box == null)
			box = new BoundingBox().shift(imageStand.getImageStandRequired().getEyeLocation());

		if (all != 0) {
			negX += all; negY += all; negZ += all;
			posX += all; posY += all; posZ += all;
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

	@Path("boundingBox draw [--id] [--stop]")
	void boundingBox_draw(@Switch String id, @Switch boolean stop) {
		getImageStand(id);

		if (stop) {
			if (!imageStand.isDrawing())
				error("No particle task for that image stand running");

			imageStand.stopDrawing();
			send(PREFIX + "Particle task cancelled");
			return;
		}

		imageStand.draw();
	}

	private void getImageStand(String id) {
		if (isNullOrEmpty(id))
			imageStand = getTargetImageStandRequired();
		else
			imageStand = service.getById(id);
	}

	static {
		Bukkit.getMessenger().registerOutgoingPluginChannel(Nexus.getInstance(), "worldedit:cui");
	}

	@Path("boundingBox update [--id]")
	void boundingBox_update(@Switch String id) {
		getImageStand(id);
		imageStand.updateBoundingBoxes();
	}

	@Path("outline update [--id]")
	void outline_update(@Switch String id) {
		getImageStand(id);

		final ArmorStand image = imageStand.getImageStandRequired();
		final ArmorStand outline = imageStand.getOutlineStandRequired();
		outline.teleport(image);
		outline.setHeadPose(image.getHeadPose());
	}

	@Path("stands yaw <yaw> [--id]")
	void stands_yaw(float yaw, @Switch String id) {
		getImageStand(id);

		final ArmorStand image = imageStand.getImageStandRequired();
		final ArmorStand outline = imageStand.getOutlineStand();

		final Location location = image.getLocation();
		location.setYaw(yaw);

		image.teleport(location);
		if (outline != null)
			outline.teleport(location);

		imageStand.updateBoundingBoxes();
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
		if (event.getEntity() instanceof ArmorStand armorStand)
			onLoad(armorStand.getUniqueId());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ImageStandService service = new ImageStandService();
		final ImageStand imageStand = service.getTargetStand(player);

		if (imageStand == null)
			return;

		new ImageStandInteractEvent(player, imageStand).callEvent();
	}

	@EventHandler
	public void on(PlayerInteractAtEntityEvent event) {
		final Player player = event.getPlayer();
		final ImageStandService service = new ImageStandService();
		final ImageStand imageStand = service.getTargetStand(player);

		if (imageStand == null)
			return;

		new ImageStandInteractEvent(player, imageStand).callEvent();
	}

	@Getter
	@AllArgsConstructor
	public static class ImageStandInteractEvent extends Event {
		private static final HandlerList handlers = new HandlerList();
		private final Player player;
		private final ImageStand imageStand;

		public static HandlerList getHandlerList() {
			return handlers;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}

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
		Tasks.repeat(0, 1, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				final ImageStandService service = new ImageStandService();
				final ImageStand imageStand = service.getTargetStand(player);
				if (imageStand != null && imageStand.hasOutline())
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
