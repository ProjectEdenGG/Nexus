package gg.projecteden.nexus.models.jigsawjam;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Entity(value = "jigsaw_jam", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class JigsawJammer implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Accessors(fluent = true)
	private boolean hasPlayed;

	private boolean playing;
	private int time;

	public void incrementTime(int amount) {
		this.time += amount;
	}

}
