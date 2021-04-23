package me.pugabyte.nexus.models.autotrash;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("autotrash")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class AutoTrash implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = true;
	private Behavior behavior = Behavior.TRASH;
	private Set<Material> materials = new HashSet<>();

	public enum Behavior {
		NO_PICKUP,
		TRASH
	}

}
