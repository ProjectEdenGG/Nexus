package gg.projecteden.nexus.models.imagestand;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.particles.effects.LineEffect;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.Tasks;
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
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
	private BoundingBox boundingBox;
	private Map<UUID, BoundingBox> boundingBoxes = new ConcurrentHashMap<>();
	private ImageSize size;

	private transient int drawTaskId;

	public ImageStand(@NonNull UUID uuid, UUID outline, String id, ImageSize size) {
		this.uuid = uuid;
		this.outline = outline;
		this.id = id;
		this.size = size;
		this.boundingBox = size.getBoundingBox(getImageStandRequired().getLocation());
	}

	public boolean isActive() {
		return !isNullOrEmpty(id);
	}

	public boolean hasOutline() {
		return outline != null;
	}

	public boolean matches(UUID uuid) {
		return (hasOutline() && outline.equals(uuid)) || boundingBoxes.containsKey(uuid);
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
	public ArmorStand getOutlineStandRequired() {
		final var stand = getOutlineStand();
		if (stand == null)
			throw new InvalidInputException("Outline Stand not found");
		return stand;
	}

	@NotNull
	public ArmorStand getImageStandRequired() {
		final var stand = getImageStand();
		if (stand == null)
			throw new InvalidInputException("Image Stand not found: " + getId());
		return stand;
	}

	@Nullable
	private ArmorStand getArmorStand(UUID uuid) {
		if (uuid == null)
			return null;
		final var outline = Bukkit.getEntity(uuid);
		if (!(outline instanceof ArmorStand outlineStand))
			return null;
		return outlineStand;
	}

	public void calculateBoundingBox() {
		boundingBox = size.getBoundingBox(getImageStandRequired().getEyeLocation());
	}

	public void updateBoundingBoxes() {
		if (boundingBoxes.isEmpty()) {
			updateBoundingBox(getImageStand(), boundingBox);
			updateBoundingBox(getOutlineStand(), boundingBox);
		} else {
			updateBoundingBox(getImageStand(), new BoundingBox());
			updateBoundingBox(getOutlineStand(), new BoundingBox());
			boundingBoxes.forEach((uuid, boundingBox) -> updateBoundingBox(getArmorStand(uuid), boundingBox));
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

	public List<ArmorStand> getArmorStands() {
		final List<ArmorStand> armorStands = new ArrayList<>() {{
			add(getImageStand());
			add(getOutlineStand());
			boundingBoxes.keySet().forEach(uuid -> add(getArmorStand(uuid)));
		}};

		armorStands.removeIf(Objects::isNull);
		return armorStands;
	}

	public List<BoundingBox> getBoundingBoxes() {
		return new ArrayList<>() {{
			add(boundingBox);
			addAll(boundingBoxes.values());
		}};
	}

	public void draw() {
		stopDrawing();
		if (boundingBox == null)
			return;

		drawTaskId = Tasks.repeat(0, 1, () -> {
			final Particle particle = Particle.SMALL_FLAME;
			final float dustSize = .5f;
			final double density = .1;

			if (boundingBoxes.isEmpty())
				draw(boundingBox, particle, dustSize, density);
			else
				boundingBoxes.forEach((uuid1, boundingBox1) -> draw(boundingBox1, particle, dustSize, density));
		});
	}

	public boolean isDrawing() {
		return drawTaskId > 0;
	}

	public void stopDrawing() {
		Tasks.cancel(drawTaskId);
		drawTaskId = 0;
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
	private enum CubeEdge {
		_01(CubeVertex._1, CubeVertex._2),
		_02(CubeVertex._2, CubeVertex._3),
		_03(CubeVertex._3, CubeVertex._4),
		_04(CubeVertex._4, CubeVertex._1),
		_05(CubeVertex._5, CubeVertex._6),
		_06(CubeVertex._6, CubeVertex._7),
		_07(CubeVertex._7, CubeVertex._8),
		_08(CubeVertex._8, CubeVertex._5),
		_09(CubeVertex._1, CubeVertex._5),
		_10(CubeVertex._2, CubeVertex._6),
		_11(CubeVertex._3, CubeVertex._7),
		_12(CubeVertex._4, CubeVertex._8),
		;

		private final CubeVertex start, end;
	}

	public List<Integer> draw(BoundingBox box, Particle particle, float dustSize, double density) {
		final List<Integer> taskIds = new ArrayList<>();
		ArmorStand armorStand = getImageStand();
		if (armorStand == null) {
			stopDrawing();
			return new ArrayList<>();
		}

		final World world = armorStand.getWorld();

		for (CubeEdge edge : CubeEdge.values()) {
			taskIds.add(LineEffect.builder()
				.startLoc(edge.getStart().toLocation(box, world))
				.endLoc(edge.getEnd().toLocation(box, world))
				.particle(particle)
				.dustSize(dustSize)
				.density(density)
				.ticks(1)
				.count(0)
				.speed(0)
				.start()
				.getTaskId());
		}

		return taskIds;
	}

	@Getter
	@AllArgsConstructor
	public enum ImageSize {
		// Height x Width
		_1x1(null),
		_1x2(null),
		_3x2(null),
		_4x3(CustomMaterial.IMAGES_OUTLINE_4x3),
		;

		private final CustomMaterial material;

		public ItemStack getOutlineItem() {
			return new ItemBuilder(material).build();
		}

		public BoundingBox getBoundingBox(Location location) {
			final BoundingBox box = new BoundingBox();
			box.expand(0, -16, 0, 1 / 13d);
			box.expand(32, 32, 0, 1 / 13d);
			box.shift(-16 / 13d, 8 / 13d, 0);
			box.shift(0, -25.3 / 13d, 0);
			box.shift(location);
			return box;
		}
	}

}
