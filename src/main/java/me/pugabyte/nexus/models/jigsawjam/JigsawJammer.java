package me.pugabyte.nexus.models.jigsawjam;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Builder
@Entity("jigsaw_jam")
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
