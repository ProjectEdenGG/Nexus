package gg.projecteden.nexus.models.monthlypodium;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.listeners.Podiums.Podium;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "monthly_podium_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class MonthlyPodiumUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<MonthlyPodiumData> podiums = new ArrayList<>();

	@Data
	@AllArgsConstructor
	public static class MonthlyPodiumData {
		private MonthlyPodiumType type;
		private PodiumSpot spot;
		private String text;
	}

	@Getter
	@AllArgsConstructor
	public enum PodiumSpot {
		FIRST(Badge.MONTHLY_PODIUMS_FIRST),
		SECOND(Badge.MONTHLY_PODIUMS_SECOND),
		THIRD(Badge.MONTHLY_PODIUMS_THIRD),
		;

		private final Badge badge;
	}

	@Getter
	@AllArgsConstructor
	public enum MonthlyPodiumType {
		VOTES(Podium.VOTES),
		PLAYTIME(Podium.PLAYTIME_MONTHLY),
		CONTRIBUTORS(Podium.TOP_MONTHLY_CONTRIBUTORS),
		;

		private final Podium podium;
	}

}
