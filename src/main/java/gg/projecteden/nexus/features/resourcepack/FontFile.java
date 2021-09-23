package gg.projecteden.nexus.features.resourcepack;

import lombok.Data;

import java.util.List;

@Data
public class FontFile {

	private final List<CustomCharacter> providers;

	@Data
	public static class CustomCharacter {
		private final String type;
		private final String file;
		private final int height;
		private final int ascent;
		private final List<String> chars;
	}

}
