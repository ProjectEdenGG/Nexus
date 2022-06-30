package gg.projecteden.nexus.models.imagestand;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.interfaces.DatabaseObject;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.particles.effects.LineEffect;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.inventivetalent.boundingbox.BoundingBoxAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static gg.projecteden.utils.Nullables.isNullOrEmpty;

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
			throw new InvalidInputException("Image Stand not found");
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

	public void updateBoundingBox(ArmorStand armorStand, BoundingBox boundingBox) {
		if (armorStand == null || boundingBox == null)
			return;

		BoundingBoxAPI.setBoundingBox(armorStand, boundingBox);
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
		final World world = getImageStandRequired().getWorld();

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
		_1x1(Material.PAPER, 1296),
		_1x2(Material.PAPER, 1297),
		_3x2(Material.PAPER, 1298),
		_4x3(Material.PAPER, 1299),
		;

		private final Material material;
		private final int customModelData;

		public ItemStack getOutlineItem() {
			return new ItemBuilder(material).customModelData(customModelData).build();
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
