package gg.projecteden.nexus.models.honeypot;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
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
@Entity(value = "honeypot_bans", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class HoneyPotBans implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<HoneyPot> honeyPots = new ArrayList<>();

	public HoneyPot get(String id) {
		return honeyPots.stream().filter(honeyPot -> honeyPot.getId().equalsIgnoreCase(id)).findFirst().orElseGet(() -> {
			HoneyPot honeyPot = new HoneyPot(id);
			honeyPots.add(honeyPot);
			return honeyPot;
		});
	}

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class HoneyPot {
		@NonNull
		private String id;
		private int bans;

		public void addBan() {
			++bans;
		}
	}

}
