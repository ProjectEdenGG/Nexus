package gg.projecteden.nexus.models.imagestand;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.interfaces.DatabaseObject;
import gg.projecteden.mongodb.serializers.UUIDConverter;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

	public ImageStand(@NonNull UUID uuid, UUID outline, String id, ImageSize size) {
		this.uuid = uuid;
		this.outline = outline;
		this.id = id;
		this.size = size;
		this.boundingBox = size.getBoundingBox(getImageStandRequired().getLocation());
	}

	public boolean isActive() {
		return outline != null;
	}

	public boolean matches(UUID uuid) {
		return outline.equals(uuid) || boundingBoxes.containsKey(uuid);
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
	private ArmorStand getArmorStand(UUID outline1) {
		final var outline = Bukkit.getEntity(outline1);
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

	public List<Integer> drawBoundingBox(Particle particle, float dustSize) {
		List<Integer> taskIds = new ArrayList<>();
		if (boundingBoxes.isEmpty()) {
			taskIds.add(Tasks.repeatAsync(0, 1, BoundingBoxAPI.drawParticleOutline(boundingBox, getImageStand().getWorld(), particle, dustSize)));
		} else {
			boundingBoxes.forEach((uuid, boundingBox) ->
				taskIds.add(Tasks.repeatAsync(0, 1, BoundingBoxAPI.drawParticleOutline(boundingBox, getArmorStand(uuid).getWorld(), particle, dustSize))));
		}
		return taskIds;
	}

	@Getter
	@AllArgsConstructor
	public enum ImageSize {
		// Height x Width
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
