package gg.projecteden.nexus.models.push;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import lombok.*;

import java.util.UUID;

@Data
@Entity(value = "push_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class PushUser implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;

	public boolean isDisabled() {
		return !enabled;
	}
}
