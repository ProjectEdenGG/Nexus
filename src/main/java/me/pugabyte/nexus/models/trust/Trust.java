package me.pugabyte.nexus.models.trust;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.EnumUtils.IteratableEnum;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("trust")
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
			default -> throw new UnsupportedOperationException();
		};
	}

	public Set<UUID> getAll() {
		return new HashSet<>() {{
			for (Trust.Type type : Trust.Type.values())
				addAll(get(type));
		}};
	}

	public boolean trusts(Type type, Player player) {
		return trusts(type, player.getUniqueId());
	}

	public boolean trusts(Type type, OfflinePlayer player) {
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
