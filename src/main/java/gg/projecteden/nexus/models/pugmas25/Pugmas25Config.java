package gg.projecteden.nexus.models.pugmas25;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25AnglerLoot;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "pugmas25_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Pugmas25Config implements DatabaseObject {

	@Id
	@NonNull
	private UUID uuid;
	private Pugmas25AnglerLoot anglerQuestFish;
	private String anglerQuestFishHint;
	private LocalDateTime anglerQuestResetDateTime = LocalDateTime.now();
	private Set<Location> nutCrackerLocations = new HashSet<>();

	public static Pugmas25Config get() {
		return new Pugmas25ConfigService().get0();
	}

	public void setAnglerQuestFish(Pugmas25AnglerLoot anglerQuestFish) {
		this.anglerQuestFish = anglerQuestFish;
		this.anglerQuestFishHint = anglerQuestFish.getRandomHint();
	}
}
