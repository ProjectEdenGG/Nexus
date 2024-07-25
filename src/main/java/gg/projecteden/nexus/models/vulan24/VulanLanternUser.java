package gg.projecteden.nexus.models.vulan24;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.*;

import java.util.UUID;


@Data
@Entity(value = "vulan24lantern", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class VulanLanternUser implements PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private int lanterns;

}
