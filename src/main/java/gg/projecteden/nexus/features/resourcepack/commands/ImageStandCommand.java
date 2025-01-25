package gg.projecteden.nexus.features.resourcepack.commands;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntityService;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@Aliases("imagestands")
@Permission(Group.ADMIN)
public class ImageStandCommand extends CustomCommand implements Listener {
	private static final ImageStandService service = new ImageStandService();
	private static final CustomBoundingBoxEntityService aabbService = new CustomBoundingBoxEntityService();

	private ImageStand imageStand;

	public ImageStandCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("list [page]")
	@Description("List image stands")
	void list(@Arg("1") int page) {
		final List<ImageStand> imageStands = service.getAll();
		if (imageStands.isEmpty())
			error("No image stands found");

		new Paginator<ImageStand>()
			.values(imageStands)
			.formatter((imageStand1, index) -> json("&3" + index + " &e" + imageStand1.getId())
				.hover("Click for more information")
				.command("/db debug ImageStandService " + imageStand1.getUuid())
			)
			.command("/imagestand list")
			.page(page)
			.send();
	}

	@Path("tp <id>")
	@Description("Teleport to an image stand")
	void info(ImageStand imageStand) {
		player().teleport(imageStand.getImageStandRequired().getLocation());
	}

	@Path("create <id> <size> [--outline]")
	@Description("Create an image stand")
	void create(String id, ImageSize size, @Switch boolean outline) {
		final ArmorStand imageArmorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);
		UUID outlineUuid = null;
		if (outline)
			outlineUuid = summonOutlineStand(imageArmorStand).getUniqueId();

		final ImageStand imageStand = new ImageStand(imageArmorStand.getUniqueId(), outlineUuid, id, size);
		service.save(imageStand);
		service.cache(imageStand);
		if (outlineUuid != null)
			aabbService.edit(outlineUuid, outlineBox -> outlineBox.setBoundingBox(imageStand.getBoundingBox()));
		send(PREFIX + "Created new stand");
	}

	@Path("id <id> [--id]")
	@Description("Update the ID of an image stand")
	void id(String newId, @Switch String id) {
		getImageStand(id);

		imageStand.setId(newId);
		service.save(imageStand);
		send(PREFIX + "Updated id to " + newId);
	}

	@Path("size <size> [--id]")
	@Description("Update the size an image stand")
	void size(ImageSize size, @Switch String id) {
		getImageStand(id);

		imageStand.setSize(size);
		service.save(imageStand);
		send(PREFIX + "Updated size to " + size.toString());
	}

	@Path("delete [--id]")
	@Description("Delete an image stand")
	void delete(@Switch String id) {
		getImageStand(id);

		imageStand.stopDrawing();
		service.delete(imageStand);
		send(PREFIX + "Deleted image stand " + imageStand.getId());
	}

	@Path("outline add [--id]")
	@Description("Add an outline stand")
	void outline_add(@Switch String id) {
		getImageStand(id);
		if (imageStand.getOutline() != null)
			error("Outline stand already exists");

		final ArmorStand armorStand = imageStand.getImageStandRequired();
		imageStand.setOutline(summonOutlineStand(armorStand).getUniqueId());
		service.save(imageStand);
		send(PREFIX + "Added outline to " + imageStand.getId());
	}

	@Path("outline remove [--id]")
	@Description("Remove an outline stand")
	void outline_remove(@Switch String id) {
		getImageStand(id);
		imageStand.getOutlineStandRequired().remove();
		imageStand.setOutline(null);
		aabbService.delete(aabbService.get(imageStand.getOutline()));
		service.save(imageStand);
		send(PREFIX + "Deleted outline from " + imageStand.getId());
	}

	@Path("outline update [--id]")
	@Description("Teleport an outline stand to the image stand")
	void outline_update(@Switch String id) {
		getImageStand(id);

		final ArmorStand image = imageStand.getImageStandRequired();
		final ArmorStand outline = imageStand.getOutlineStandRequired();
		outline.teleport(image);
		outline.setHeadPose(image.getHeadPose());
		imageStand.updateBoundingBoxes();
	}

	@Path("stands yaw <yaw> [--id]")
	@Description("Update the yaw of the image stand and outline stand")
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

	@Path("stands pitch <pitch> [--id]")
	@Description("Update the pitch of the image stand and outline stand")
	void stands_pitch(float pitch, @Switch String id) {
		getImageStand(id);

		final ArmorStand image = imageStand.getImageStandRequired();
		final ArmorStand outline = imageStand.getOutlineStand();

		final Location location = image.getLocation();
		location.setPitch(pitch);

		image.teleport(location);
		if (outline != null)
			outline.teleport(location);

		imageStand.updateBoundingBoxes();
	}

	private void getImageStand(String id) {
		if (Nullables.isNullOrEmpty(id))
			imageStand = getTargetImageStandRequired();
		else
			imageStand = service.getById(id);
	}

	private ImageStand getTargetImageStandRequired() {
		final ImageStand imageStand = service.getTargetStand(player());
		if (imageStand == null)
			throw new InvalidInputException("You must be looking at an image stand");
		return imageStand;
	}

	@NotNull
	private static ArmorStand summonOutlineStand(ArmorStand armorStand) {
		return ArmorStandEditorCommand.summon(armorStand.getLocation(), outlineStand -> {
			outlineStand.setInvisible(true);
			outlineStand.setHeadPose(armorStand.getHeadPose());
		});
	}

	static {
		try {
			for (World world : Bukkit.getWorlds())
				for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class))
					onLoad(armorStand.getUniqueId());
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

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
		@Getter
		private static final HandlerList handlerList = new HandlerList();
		private final Player player;
		private final ImageStand imageStand;

		@Override
		public @NotNull HandlerList getHandlers() {
			return handlerList;
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

	@TabCompleterFor(ImageSize.class)
	List<String> tabCompleteImageSize(String filter) {
		return tabCompleteEnum(filter, ImageSize.class, size -> size.name().replaceFirst("_", ""));
	}

	@ConverterFor(ImageSize.class)
	ImageSize convertToImageSize(String value) {
		return convertToEnum("_" + value, ImageSize.class);
	}

	@TabCompleterFor(ImageStand.class)
	List<String> tabCompleteImageStand(String filter) {
		return service.getAll().stream()
			.map(ImageStand::getId)
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	@ConverterFor(ImageStand.class)
	ImageStand convertToImageStand(String value) {
		return service.getById(value);
	}

}
