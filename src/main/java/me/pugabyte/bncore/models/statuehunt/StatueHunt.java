package me.pugabyte.bncore.models.statuehunt;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.*;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity("statue_hunt")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class StatueHunt extends PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	List<String> found = new ArrayList<>();
	boolean claimed;

	@Override
	public UUID getUuid() {
		return uuid;
	}
}
