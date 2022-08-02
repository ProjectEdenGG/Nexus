package gg.projecteden.nexus.models.customhitbox;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.EntityOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.BoundingBoxUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.util.BoundingBox;

import java.util.UUID;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.NMSUtils.fromNMS;
import static gg.projecteden.nexus.utils.NMSUtils.toNMS;

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
	private BoundingBox boundingBox;

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

	public boolean hasCustomHitbox() {
		return boundingBox != null;
	}

	public BoundingBox createBoundingBox() {
		boundingBox = fromNMS(getNMSEntity().getBoundingBox());
		return boundingBox;
	}

	public void draw() {
		stopDrawing();
		drawTaskId = Tasks.repeat(0, 1, () -> BoundingBoxUtils.draw(getLoadedEntity().getWorld(), boundingBox));
	}

	public boolean isDrawing() {
		return drawTaskId > 0;
	}

	public void stopDrawing() {
		Tasks.cancel(drawTaskId);
		drawTaskId = 0;
	}
}
