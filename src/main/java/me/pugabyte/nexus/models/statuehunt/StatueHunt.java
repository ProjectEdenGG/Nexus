package me.pugabyte.nexus.models.statuehunt;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity("statue_hunt")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class StatueHunt implements PlayerOwnedObject {

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
