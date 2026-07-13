package gg.projecteden.nexus.models.store;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "disguise_permission_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class DisguisePermissionConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, List<DisguiseType>> packages = new ConcurrentHashMap<>();
	private Map<String, List<String>> enabledWatchers = new ConcurrentHashMap<>();
}
