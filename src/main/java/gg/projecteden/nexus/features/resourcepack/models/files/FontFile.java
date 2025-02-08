package gg.projecteden.nexus.features.resourcepack.models.files;

import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Data
public class FontFile {
	@Getter
	private static final String path = "assets/minecraft/font/default.json";

	private final List<CustomCharacter> providers;

	public CustomCharacter get(String fileName) {
		return providers.stream()
			.filter(character -> fileName.equalsIgnoreCase(character.fileName()))
			.findFirst()
			.orElse(null);
	}

	@Data
	public static class CustomCharacter {
		private final String type;
		private final String file;
		private final boolean purchasable;
		private final String name;
		private final String discordId;
		private final String modelId;
		private final int height;
		private final int ascent;
		private final List<String> chars;

		public String fileName() {
			if (file == null)
				return null;

			return StringUtils.listLast(file, "/").split("\\.")[0];
		}

		public String getChar() {
			return chars.get(0);
		}
	}

	@SneakyThrows
	public static FontFile of(Path path) {
		return Utils.getGson().fromJson(String.join("", Files.readAllLines(path)), FontFile.class);
	}

}
