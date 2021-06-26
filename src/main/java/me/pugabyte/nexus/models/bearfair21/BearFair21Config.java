package me.pugabyte.nexus.models.bearfair21;

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
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Builder
@Entity("bearfair21_config")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BearFair21Config implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	//
	private boolean enableWarp = false;
	private boolean enableEdit = false;
	private boolean enableRides = false;
	private boolean enableQuests = false;
	private boolean giveDailyPoints = false;
	private boolean skipWaits = false;

}
