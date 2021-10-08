package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;

import java.util.List;

@Data
public class FontFile {

	private final List<CustomCharacter> providers;

	@Data
	public static class CustomCharacter {
		private final String type;
		private final String file;
		private final boolean purchasable;
		private final String discordId;
		private final int height;
		private final int ascent;
		private final List<String> chars;

		public String fileName() {
			return StringUtils.listLast(file, "/").split("\\.")[0];
		}

		public String getChar() {
			return chars.get(0);
		}
	}

}
