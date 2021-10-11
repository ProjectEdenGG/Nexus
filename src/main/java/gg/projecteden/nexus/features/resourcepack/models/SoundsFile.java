package gg.projecteden.nexus.features.resourcepack.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SoundsFile {

	private final Map<String, SoundGroup> sounds;

	@Data
	public static class SoundGroup {
		private final List<String> sounds;
	}

}
