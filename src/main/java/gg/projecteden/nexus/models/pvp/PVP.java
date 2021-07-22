package gg.projecteden.nexus.models.pvp;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Entity(value = "pvp", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class PVP implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = false;
	private boolean keepInventory = true;

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
