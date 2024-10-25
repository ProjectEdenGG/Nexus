package gg.projecteden.nexus.models.imagestand;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntityService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.phys.AABB;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Data
@Entity(value = "image_stand", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class ImageStand implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private UUID outline;
	private String id;
	private List<UUID> boundingBoxes = new ArrayList<>();
	private ImageSize size;

	private transient int drawTaskId;

	private static final CustomBoundingBoxEntityService aabbService = new CustomBoundingBoxEntityService();

	public ImageStand(@NonNull UUID uuid, UUID outline, String id, ImageSize size) {
		this.uuid = uuid;
		this.outline = outline;
		this.id = id;
		this.size = size;
	}

	@NotNull
	private static CustomBoundingBoxEntity getCustomBoundingBox(UUID uuid) {
		return aabbService.get(uuid);
	}

	public BoundingBox getBoundingBox() {
		return getBoundingBox(uuid);
	}

	private static BoundingBox getBoundingBox(UUID uuid) {
		return getCustomBoundingBox(uuid).getBoundingBox();
	}

	public boolean isActive() {
		return !isNullOrEmpty(id);
	}

	public boolean hasOutline() {
		return outline != null;
	}

	public boolean matches(@NotNull UUID uuid) {
		return uuid.equals(outline) || boundingBoxes.contains(uuid);
	}

	public void outlineFor(Player player) {
		final ImageStandService service = new ImageStandService();
		service.removeOutlineFor(player);
		sendOutlineItem(player, size.getOutlineItem());
		service.getOutlineCache().put(player.getUniqueId(), uuid);
	}

	public void removeOutlineFor(Player player) {
		sendOutlineItem(player, new ItemStack(Material.AIR));
		new ImageStandService().getOutlineCache().remove(player.getUniqueId());
	}

	private void sendOutlineItem(Player player, ItemStack item) {
		ArmorStand outlineStand = getOutlineStand();
		if (outlineStand == null)
			return;

		PacketUtils.sendFakeItem(outlineStand, player, item, EquipmentSlot.HEAD);
	}

	@Nullable
	public ArmorStand getOutlineStand() {
		return getArmorStand(this.outline);
	}

	@Nullable
	public ArmorStand getImageStand() {
		return getArmorStand(this.uuid);
	}

	@NotNull
	public ArmorStand getImageStandRequired() {
		final var stand = getImageStand();
		if (stand == null)
			throw new InvalidInputException("ImageStand " + getId() + " not found (chunk unloaded?)");
		return stand;
	}

	@NotNull
	public ArmorStand getOutlineStandRequired() {
		final var stand = getOutlineStand();
		if (stand == null)
			throw new InvalidInputException("ImageStand outline " + getId() + " not found (chunk unloaded?)");
		return stand;
	}

	@Nullable
	private ArmorStand getArmorStand(UUID uuid) {
		if (uuid == null)
			return null;
		final var entity = Bukkit.getEntity(uuid);
		if (!(entity instanceof ArmorStand armorStand))
			return null;
		return armorStand;
	}

	public void updateBoundingBoxes() {
		if (boundingBoxes.isEmpty()) {
			updateBoundingBox(getImageStand(), getBoundingBox(uuid));
			updateBoundingBox(getOutlineStand(), getBoundingBox(uuid));
		} else {
			updateBoundingBox(getImageStand(), new BoundingBox());
			updateBoundingBox(getOutlineStand(), new BoundingBox());
			boundingBoxes.forEach(uuid -> updateBoundingBox(getArmorStand(uuid), getBoundingBox(uuid)));
		}
	}

	public void updateBoundingBox(ArmorStand armorStand, BoundingBox box) {
		if (armorStand == null || box == null)
			return;

		updateBoundingBox((CraftArmorStand) armorStand, box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
	}

	public void updateBoundingBox(CraftArmorStand armorStand, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		armorStand.getHandle().setBoundingBox(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
	}

	public List<UUID> getUUIDs() {
		final ArrayList<UUID> uuids = new ArrayList<>() {{
			add(uuid);
			add(outline);
			addAll(ImageStand.this.boundingBoxes);
		}};

		uuids.removeIf(Objects::isNull);
		return uuids;
	}

	public List<ArmorStand> getArmorStands() {
		final List<ArmorStand> armorStands = new ArrayList<>() {{
			getUUIDs().forEach(uuid -> add(getArmorStand(uuid)));
		}};

		armorStands.removeIf(Objects::isNull);
		return armorStands;
	}

	public List<BoundingBox> getBoundingBoxes() {
		final ArrayList<BoundingBox> boundingBoxes = new ArrayList<>() {{
			getUUIDs().forEach(uuid -> add(getBoundingBox(uuid)));
		}};

		boundingBoxes.removeIf(Objects::isNull);
		return boundingBoxes;
	}

	public void draw() {
		if (boundingBoxes.isEmpty())
			getCustomBoundingBox(uuid).draw();
		else
			boundingBoxes.forEach(uuid -> getCustomBoundingBox(uuid).draw());
	}

	public boolean isDrawing() {
		if (getCustomBoundingBox(uuid).isDrawing())
			return true;

		for (UUID uuid : boundingBoxes)
			if (getCustomBoundingBox(uuid).isDrawing())
				return true;

		return false;
	}

	public void stopDrawing() {
		getCustomBoundingBox(uuid).stopDrawing();

		for (UUID uuid : boundingBoxes)
			getCustomBoundingBox(uuid).stopDrawing();
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		if (!boundingBoxes.isEmpty())
			throw new InvalidInputException("Image stand has multiple bounding boxes, use /customboundingbox to modify bounding boxes");

		getCustomBoundingBox(uuid).setBoundingBox(boundingBox);
		getCustomBoundingBox(outline).setBoundingBox(boundingBox);
	}

	@Getter
	@AllArgsConstructor
	private enum CubeVertex {
		_1(BoundingBox::getMaxX, BoundingBox::getMinY, BoundingBox::getMinZ),
		_2(BoundingBox::getMaxX, BoundingBox::getMinY, BoundingBox::getMaxZ),
		_3(BoundingBox::getMinX, BoundingBox::getMinY, BoundingBox::getMaxZ),
		_4(BoundingBox::getMinX, BoundingBox::getMinY, BoundingBox::getMinZ),
		_5(BoundingBox::getMaxX, BoundingBox::getMaxY, BoundingBox::getMinZ),
		_6(BoundingBox::getMaxX, BoundingBox::getMaxY, BoundingBox::getMaxZ),
		_7(BoundingBox::getMinX, BoundingBox::getMaxY, BoundingBox::getMaxZ),
		_8(BoundingBox::getMinX, BoundingBox::getMaxY, BoundingBox::getMinZ),
		;

		private final Function<BoundingBox, Double> x, y, z;

		public Location toLocation(BoundingBox box, World world) {
			return new Location(world, x.apply(box), y.apply(box), z.apply(box));
		}
	}

	@Getter
	@AllArgsConstructor
	public enum ImageSize {
		// Height x Width
		_1x1(null),
		_1x2(CustomMaterial.IMAGES_OUTLINE_1x2),
		_3x2(CustomMaterial.IMAGES_OUTLINE_3x2),
		_4x3(CustomMaterial.IMAGES_OUTLINE_4x3),
		;

		private final CustomMaterial material;

		public ItemStack getOutlineItem() {
			final ItemBuilder itemBuilder = new ItemBuilder(material);
			if (material.getMaterial() == Material.LEATHER_HORSE_ARMOR)
				itemBuilder.dyeColor("#FD6A02");
			return itemBuilder.build();
		}
	}

}
