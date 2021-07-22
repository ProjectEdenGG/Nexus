package gg.projecteden.nexus.models.mobheads;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.MobHeadConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity(value = "mob_head_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class MobHeadUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<MobHeadData> data = new ArrayList<>();

	private final transient Map<MobHead, MobHeadData> map = new HashMap<>();

	@PostLoad
	void postLoad() {
		for (MobHeadData data : data)
			map.put(data.getMobHead(), data);
	}

	public @NotNull MobHeadData get(MobHead mobHead) {
		return map.computeIfAbsent(mobHead, $ -> {
			MobHeadData mobHeadData = new MobHeadData(mobHead);
			data.add(mobHeadData);
			return mobHeadData;
		});
	}

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	@Converters(MobHeadConverter.class)
	public static class MobHeadData {
		@NonNull
		private MobHead mobHead;
		private int kills;
		private int heads;

		public void kill() {
			++kills;
		}

		public void head() {
			++heads;
		}

	}

}
