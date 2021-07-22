package gg.projecteden.nexus.models.killermoney;

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

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(value = "killer_money", noClassnameStored = true)
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class KillerMoney implements PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private boolean muted;
}
