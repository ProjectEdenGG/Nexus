package gg.projecteden.nexus.models.customboundingbox;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.interfaces.EntityOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.BoundingBoxUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.nms.NMSUtils.fromNMS;
import static gg.projecteden.nexus.utils.nms.NMSUtils.toNMS;

@Data
@dev.morphia.annotations.Entity(value = "custom_bounding_box_entity", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class CustomBoundingBoxEntity implements EntityOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String id;
	private BoundingBox boundingBox;
	private Map<String, UUID> associated = new HashMap<>();

	private transient int drawTaskId;

	public void updateBoundingBox() {
		if (!isLoaded())
			return;

		getNMSEntity().setBoundingBox(toNMS(boundingBox));
	}

	public void modifyBoundingBox(Consumer<BoundingBox> consumer) {
		consumer.accept(boundingBox);
		updateBoundingBox();
	}

	public boolean hasCustomBoundingBox() {
		return boundingBox != null;
	}

	public BoundingBox createBoundingBox() {
		boundingBox = fromNMS(getNMSEntity().getBoundingBox());
		return boundingBox;
	}

	public <T extends Entity> T getAssociatedEntity(String id) {
		final UUID uuid = associated.get(id);
		if (uuid == null)
			return null;

		if (!isLoaded())
			return null;

		final Entity entity = getWorld().getEntity(uuid);
		if (entity == null)
			return null;

		return (T) entity;
	}

	public <T extends Entity> T getLoadedAssociatedEntity(String id) {
		return (T) getAssociatedEntity(id);
	}

	public void draw() {
		stopDrawing();
		if (!hasCustomBoundingBox()) {
			Nexus.log("Tried to draw custom bounding box of entity with no bounding box" + (id == null ? "" : " (" + id + ")"));
			return;
		}

		drawTaskId = Tasks.repeat(0, 1, () -> {
			if (!isLoaded()) {
				stopDrawing();
				return;
			}

			BoundingBoxUtils.draw(getLoadedEntity().getWorld(), boundingBox);
		});
	}

	public boolean isDrawing() {
		return drawTaskId > 0;
	}

	public void stopDrawing() {
		Tasks.cancel(drawTaskId);
		drawTaskId = 0;
	}
}
