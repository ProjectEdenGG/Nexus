package me.pugabyte.bncore.models.trust;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils.IteratableEnum;
import org.bukkit.Material;

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
public class Trust extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<UUID> locks = new ArrayList<>();
	private List<UUID> homes = new ArrayList<>();

	public List<UUID> get(Type type) {
		switch (type) {
			case HOMES:
				return homes;
			case LOCKS:
				return locks;
			default:
				throw new UnsupportedOperationException();
		}
	}

	public Set<UUID> getAll() {
		return new HashSet<UUID>() {{
			for (Trust.Type type : Trust.Type.values())
				addAll(get(type));
		}};
	}

	public enum Type implements IteratableEnum {
		LOCKS(3, Material.CHEST),
		HOMES(5, Material.CYAN_BED);

		@Getter
		private int column;
		@Getter
		private Material material;

		Type(int column, Material material) {
			this.column = column;
			this.material = material;
		}

		public String camelCase() {
			return StringUtils.camelCase(name());
		}
	}
}
