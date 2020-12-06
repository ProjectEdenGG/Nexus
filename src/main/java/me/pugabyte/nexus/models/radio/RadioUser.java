package me.pugabyte.nexus.models.radio;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.radio.RadioConfig.Radio;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity("radio_user")
@Converters({UUIDConverter.class})
public class RadioUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	String radioId;
	String lastRadioId;

	public Radio getRadio() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig config = configService.get(Nexus.getUUID0());
		return config.getById(radioId);
	}

	public Radio getLastRadio() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig config = configService.get(Nexus.getUUID0());
		return config.getById(lastRadioId);
	}
}
