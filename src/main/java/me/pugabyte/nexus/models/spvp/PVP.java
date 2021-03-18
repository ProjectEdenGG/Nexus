package me.pugabyte.nexus.models.spvp;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.*;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Builder
@Entity("pvp")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class PVP extends PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = false;
	private boolean keepInventory = true;

	@Override
	public UUID getUuid() {
		return uuid;
	}

}
