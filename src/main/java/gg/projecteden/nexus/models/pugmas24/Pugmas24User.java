package gg.projecteden.nexus.models.pugmas24;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestLine;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "pugmas24_user", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Pugmas24User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private Pugmas24QuestLine questLine;
	private boolean firstVisit = false;

	@Getter(AccessLevel.PRIVATE)
	private Advent24User advent;

	public Advent24User advent() {
		if (advent == null)
			advent = new Advent24User(uuid);

		return advent;
	}

}
