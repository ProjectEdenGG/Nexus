package gg.projecteden.nexus.models.radio;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.radio.RadioConfig.Radio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(value = "radio_user", noClassnameStored = true)
@Converters({UUIDConverter.class})
public class RadioUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private int volume = 25;
	private boolean mute = false;

	private String serverRadioId;
	private String lastServerRadioId;

	@Embedded
	private Set<String> leftRadiusRadios = new HashSet<>();

	public void setVolume(byte volume) {
		this.volume = volume;
		NoteBlockAPI.setPlayerVolume(uuid, volume);
	}

	public void setServerRadioId(String serverRadioId) {
		this.lastServerRadioId = this.serverRadioId;
		this.serverRadioId = serverRadioId;
	}

	public Radio getServerRadio() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig config = configService.get0();
		return config.getById(serverRadioId);
	}

	public Radio getLastServerRadio() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig config = configService.get0();
		if (lastServerRadioId == null)
			return null;

		return config.getById(lastServerRadioId);
	}
}
