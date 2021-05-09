package me.pugabyte.nexus.models.killermoney;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity("killer_money")
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class KillerMoney implements PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private boolean muted;
	private double boost;
}
