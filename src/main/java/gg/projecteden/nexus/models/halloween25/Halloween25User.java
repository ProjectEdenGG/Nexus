package gg.projecteden.nexus.models.halloween25;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "halloween25_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Halloween25User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int candyBaskets;

	public boolean hasAcquiredCandyBasket() {
		return candyBaskets > 0;
	}

	public void incrementCandyBaskets() {
		this.candyBaskets++;
	}

}
