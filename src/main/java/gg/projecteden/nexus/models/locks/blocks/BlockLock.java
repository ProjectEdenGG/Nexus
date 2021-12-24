package gg.projecteden.nexus.models.locks.blocks;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.utils.IndexType;
import gg.projecteden.interfaces.DatabaseObject;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.locks.common.LockFlag;
import gg.projecteden.nexus.models.locks.common.LockPermission;
import gg.projecteden.nexus.models.locks.common.LockType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "lock_block", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, LocalDateTimeConverter.class})
@Indexes({@Index(
	fields = @Field(value = "location", type = IndexType.HASHED),
	options = @IndexOptions(unique = true, background = true)
)})
public class BlockLock implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private UUID owner;

	private Location location;
	private Material material;
	private LockType lockType;

	private LocalDateTime created;
	private LocalDateTime lastAccessed;

	private String password;

	private final Map<UUID, LockPermission> permissions = new ConcurrentHashMap<>();

	private final List<LockFlag> flags = new ArrayList<>();

	public BlockLock(UUID owner, Location location, LockType lockType) {
		this.uuid = UUID.nameUUIDFromBytes(StringUtils.getShortLocationString(location).getBytes());
		this.owner = owner;
		this.location = location;
		this.material = location.getBlock().getType();

		this.lockType = lockType;
		this.created = LocalDateTime.now();
	}

	public void trust(UUID uuid, LockPermission permission) {
		permissions.put(uuid, permission);
	}

	public void untrust(UUID uuid) {
		permissions.remove(uuid);
	}

	public boolean canBeOpenedBy(UUID uuid) {
		return permissions.containsKey(uuid) || lockType.canOpen();
	}

	public boolean canBeEditedBy(UUID uuid) {
		return permissions.containsKey(uuid) && permissions.get(uuid).canEdit(lockType);
	}

	public boolean hasFlag(LockFlag flag) {
		return flags.contains(flag);
	}

	public void addFlag(LockFlag flag) {
		flags.add(flag);
	}

	public void removeFlag(LockFlag flag) {
		flags.remove(flag);
	}

}
