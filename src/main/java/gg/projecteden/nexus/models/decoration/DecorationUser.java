package gg.projecteden.nexus.models.decoration;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "decoration_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class DecorationUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	List<Catalog.Theme> ownedThemes = new ArrayList<>();
	boolean boughtMasterCatalog = false;

	public void addOwnedThemes(Catalog.Theme theme) {
		this.ownedThemes.add(theme);
	}
}
