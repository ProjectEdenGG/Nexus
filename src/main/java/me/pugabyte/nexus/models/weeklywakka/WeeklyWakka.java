package me.pugabyte.nexus.models.weeklywakka;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.*;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity("weekly_wakka")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class WeeklyWakka extends PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private String currentTip;
	private String currentLocation;
	@Embedded
	private List<UUID> foundPlayers = new ArrayList<>();
}
