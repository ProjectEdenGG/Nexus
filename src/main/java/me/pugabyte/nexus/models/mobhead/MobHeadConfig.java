package me.pugabyte.nexus.models.mobhead;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import dev.morphia.annotations.PrePersist;
import eden.mongodb.serializers.UUIDConverter;
import eden.utils.EnumUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.MobHeadType;
import me.pugabyte.nexus.features.mobheads.MobHeadType.MobHeadVariant;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("mob_head_config")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class MobHeadConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private Map<MobHeadType, MobHeadTypeConfig> types = new HashMap<>();

	public MobHeadTypeConfig get(MobHeadType type) {
		return types.computeIfAbsent(type, $ -> new MobHeadTypeConfig(type));
	}

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class MobHeadTypeConfig {
		@NonNull
		private MobHeadType mobHeadType;
		private ItemStack head;
		private double chance;
		private transient Map<MobHeadVariant, ItemStack> variantHeads = new HashMap<>();

		@Getter(AccessLevel.NONE)
		@Setter(AccessLevel.NONE)
		private Map<String, ItemStack> variantHeadsRaw = new HashMap<>();

		@PostLoad
		void postLoad() {
			variantHeadsRaw.forEach((variant, head) ->
				variantHeads.put(EnumUtils.valueOf(mobHeadType.getVariant(), variant), head));
		}

		@PrePersist
		void preSave() {
			variantHeads.forEach((variant, head) ->
				variantHeadsRaw.put(variant.name(), head));
		}

	}

}
