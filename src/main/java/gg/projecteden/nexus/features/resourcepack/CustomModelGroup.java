package gg.projecteden.nexus.features.resourcepack;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import gg.projecteden.nexus.features.resourcepack.CustomModel.CustomModelMeta;
import gg.projecteden.nexus.utils.AudioUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
class CustomModelGroup {
	private Material material;
	private List<Override> overrides = new ArrayList<>();

	@Data
	public static class Override {
		private Predicate predicate;
		private String model;

		@Data
		public static class Predicate {
			@SerializedName("custom_model_data")
			private int customModelData;
		}

		public String getFolderPath() {
			String path = model.replaceFirst("projecteden", "");
			List<String> folders = new ArrayList<>(Arrays.asList(path.split("/")));
			folders.remove(folders.size() - 1); // remove file name
			return String.join("/", folders);
		}

		public String getFileName() {
			return StringUtils.listLast(model, "/");
		}

		@SneakyThrows
		public CustomModelMeta getMeta() {
			String metaUri = ResourcePack.getSubdirectory() + model.replaceFirst("projecteden", "") + ".meta";
			Path metaPath = ResourcePack.getZipFile().getPath(metaUri);
			if (Files.exists(metaPath))
				return new Gson().fromJson(String.join("", Files.readAllLines(metaPath)), CustomModelMeta.class);
			return new CustomModelMeta();
		}
	}

	private static final String MODEL_REGEX = ".*" + ResourcePack.getSubdirectory() + "/" + ResourcePack.getFileRegex() + "\\.json";

	private static void addCustomModel(Path path) {
		if (!path.toUri().toString().matches(MODEL_REGEX))
			return;

		CustomModelGroup group = read(path);
		if (group.getMaterial() != null && !group.getOverrides().isEmpty())
			ResourcePack.getModelGroups().add(group);
	}

	private static CustomModelGroup read(Path path) {
		CustomModelGroup model = getCustomModel(path);
		model.setMaterial(getMaterial(path));
		return model;
	}

	@SneakyThrows
	private static CustomModelGroup getCustomModel(Path path) {
		String json = String.join("", Files.readAllLines(path));
		return new Gson().fromJson(json, CustomModelGroup.class);
	}

	@Nullable
	private static Material getMaterial(Path path) {
		String materialName = path.getName(path.getNameCount() - 1).toString().split("\\.")[0];
		return Material.getMaterial(materialName.toUpperCase());
	}

	static void load() {
		try {
			for (Path root : ResourcePack.getZipFile().getRootDirectories()) {
				Files.walk(root).forEach(path -> {
					try {
						if (path.toUri().toString().contains(ResourcePack.getSubdirectory()))
							addCustomModel(path);
						if (path.toUri().toString().contains(".ogg"))
							addSoundFile(path);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// milliseconds
	private static final Map<String, Integer> soundDuration = new HashMap<>();

	@SneakyThrows
	private static void addSoundFile(Path path) {
		final int duration = (int) AudioUtils.getVorbisDuration(Files.readAllBytes(path));
		soundDuration.put(path.getFileName().toString().replace(".ogg", ""), duration);
	}

}
