package gg.projecteden.nexus.models.witherarena;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "wither_arena_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class WitherArenaConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean beta;
	private boolean maintenance;
	public List<UUID> queue = new ArrayList<>();

	public static boolean isMaintenance() {
		return new WitherArenaConfigService().get0().maintenance;
	}

	public static boolean isBeta() {
		return new WitherArenaConfigService().get0().beta;
	}

}
