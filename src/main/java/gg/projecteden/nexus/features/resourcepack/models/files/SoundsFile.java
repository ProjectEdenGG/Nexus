package gg.projecteden.nexus.features.resourcepack.models.files;

import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.utils.AudioUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Data
public class SoundsFile {
	@Getter
	private static final String path = "assets/minecraft/sounds.json";

	private final Map<String, SoundGroup> sounds;

	@Data
	public static class SoundGroup {
		private final List<Sound> sounds;
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

			for (Sound file : group.getSounds()) {
				try {
					if (!file.getName().equals(filePath))
						continue;

					SoundBuilder.SOUND_DURATIONS.put(sound, (int) AudioUtils.getVorbisDuration(Files.readAllBytes(path)));
					return;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Data
	@RequiredArgsConstructor
	public static class Sound {
		@NonNull String name;
		double volume = 1.0;
		double pitch = 1.0;
		int weight = 1;
		boolean stream = false;
		int attenuation_distance = 16;
		boolean preload = false;
		@NonNull String type = "sound";
	}

}
