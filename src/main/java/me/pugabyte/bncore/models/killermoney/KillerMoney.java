package me.pugabyte.bncore.models.killermoney;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.*;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity("killer_money")
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class KillerMoney extends PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private boolean muted;
	private double boost;
}
