package gg.projecteden.nexus.models.decoration;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "decoration_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class DecorationUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	List<Catalog.Theme> ownedThemes = new ArrayList<>();
	List<Tickable> tickableDecorations = new ArrayList<>();
	boolean boughtMasterCatalog = false;

	public void addOwnedThemes(Catalog.Theme theme) {
		this.ownedThemes.add(theme);
	}

	public void removeTickable(UUID uuid) {
		tickableDecorations.remove(new Tickable(uuid, null, null));
	}

	public void addTickable(ItemFrame itemFrame, String configId) {
		tickableDecorations.add(new Tickable(itemFrame, configId));
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Tickable {
		UUID uuid;
		Location location;
		String configId;

		public Tickable(ItemFrame itemFrame, String configId) {
			this.uuid = itemFrame.getUniqueId();
			this.location = itemFrame.getLocation();
			this.configId = configId;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Tickable tickable))
				return false;

			return tickable.getUuid().equals(this.uuid);
		}
	}
}
