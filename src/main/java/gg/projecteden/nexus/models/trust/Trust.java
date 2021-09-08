package gg.projecteden.nexus.models.trust;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.EnumUtils.IteratableEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasUniqueId;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "trust", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Trust implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<UUID> locks = new ArrayList<>();
	private List<UUID> homes = new ArrayList<>();
	private List<UUID> teleports = new ArrayList<>();

	public List<UUID> get(Type type) {
		return switch (type) {
			case HOMES -> homes;
			case LOCKS -> locks;
			case TELEPORTS -> teleports;
		};
	}

	public Set<UUID> getAll() {
		return new HashSet<>() {{
			for (Trust.Type type : Trust.Type.values())
				addAll(get(type));
		}};
	}

	public boolean trusts(Type type, HasUniqueId player) {
		return trusts(type, player.getUniqueId());
	}

	public boolean trusts(Type type, UUID uuid) {
		return get(type).contains(uuid);
	}

	@Getter
	@AllArgsConstructor
	public enum Type implements IteratableEnum {
		LOCKS(2, Material.CHEST),
		HOMES(4, Material.CYAN_BED),
		TELEPORTS(6, Material.COMPASS);

		private final int column;
		private final Material material;

		public String camelCase() {
			return StringUtils.camelCase(name());
		}
	}
}
