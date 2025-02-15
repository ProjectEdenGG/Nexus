package gg.projecteden.nexus.models.trust;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	private Map<Type, Set<UUID>> trusts = new HashMap<>();

	public Set<UUID> get(Type type) {
		return trusts.computeIfAbsent(type, $ -> new HashSet<>());
	}

	public Set<UUID> getAll() {
		return new HashSet<>() {{
			for (Type type : Type.values())
				addAll(get(type));
		}};
	}

	public boolean trusts(Type type, HasUniqueId player) {
		return trusts(type, player.getUniqueId());
	}

	public boolean trusts(Type type, UUID uuid) {
		return get(type).contains(uuid);
	}

	public void add(Type type, Trust trust) {
		add(type, trust.getUuid());
	}

	public void add(Type type, UUID uuid) {
		if (trusts(type, uuid))
			return;

		get(type).add(uuid);
	}

	public void addAllTypes(Trust trust) {
		addAllTypes(trust.getUuid());
	}

	public void addAllTypes(UUID uuid) {
		for (Type type : Type.values())
			add(type, uuid);
	}

	public void remove(Type type, Trust trust) {
		remove(type, trust.getUuid());
	}

	public void remove(Type type, UUID uuid) {
		if (!trusts(type, uuid))
			return;

		get(type).remove(uuid);
	}

	public void removeAllTypes(Trust trust) {
		removeAllTypes(trust.getUuid());
	}

	public void removeAllTypes(UUID uuid) {
		for (Type type : Type.values())
			remove(type, uuid);
	}

	public void clear(Type type) {
		get(type).clear();
	}

	public void clearAll() {
		for (Type type : Type.values())
			clear(type);
	}

	@Getter
	public enum Type implements IterableEnum {
		LOCKS(1, Material.CHEST),
		HOMES(3, Material.CYAN_BED),
		TELEPORTS(5, Material.COMPASS),
		DECORATIONS(7, ItemModelType.CHAIR_CLOTH);

		private final int column;
		private final Material material;
		private final String modelId;

		Type(int column, ItemModelType itemModelType) {
			this.column = column;
			this.material = itemModelType.getMaterial();
			this.modelId = itemModelType.getModel();
		}

		Type(int column, Material material) {
			this.column = column;
			this.material = material;
			this.modelId = null;
		}

		public String camelCase() {
			return StringUtils.camelCase(name());
		}

		public ItemStack getDisplayItem() {
			ItemBuilder itemBuilder = new ItemBuilder(this.material).model(this.modelId);

			if (itemBuilder.isDyeable())
				itemBuilder.dyeColor(ColorType.RED);

			return itemBuilder.build();
		}
	}
}
