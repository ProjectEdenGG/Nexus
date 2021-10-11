package gg.projecteden.nexus.features.resourcepack.models.files;

import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.utils.AudioUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.SoundBuilder.SOUND_DURATIONS;

@Data
public class SoundsFile {
	@Getter
	private static final String path = "assets/minecraft/sounds.json";

	private final Map<String, SoundGroup> sounds;

	@Data
	public static class SoundGroup {
		private final List<String> sounds;
	}

	@SneakyThrows
	public static SoundsFile of(Path path) {
		return Utils.getGson().fromJson("{\"sounds\":" + String.join("", Files.readAllLines(path)) + "}", SoundsFile.class);
	}

	public static void addAudioFile(Path path) {
		final String filePath = path.toUri().toString().split("sounds/", 2)[1].replace(".ogg", "");
		ResourcePack.getSoundsFile().getSounds().forEach((sound, group) -> {
			if (!sound.contains(":"))
				sound = "minecraft:" + sound;

			for (String file : group.getSounds()) {
				try {
					if (!file.equals(filePath))
						continue;

					SOUND_DURATIONS.put(sound, (int) AudioUtils.getVorbisDuration(Files.readAllBytes(path)));
					return;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

}
