package me.pugabyte.nexus.models.newrankcolors;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nerd.Rank;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("new_rank_colors")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class NewRankColors extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<Rank, String> colors = new HashMap<>();

}
