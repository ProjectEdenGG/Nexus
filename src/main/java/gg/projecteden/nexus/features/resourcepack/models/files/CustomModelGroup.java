package gg.projecteden.nexus.features.resourcepack.models.files;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel.CustomModelMeta;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class CustomModelGroup {
	private Material material;
	private List<Override3> overrides = new ArrayList<>();

	@Data
	public static class Override3 {
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
			String metaUri = CustomModel.getModelsSubdirectory() + "/" + model + ".meta";
			Path metaPath = ResourcePack.getZipFile().getPath(metaUri);

			if (Files.exists(metaPath))
				return new Gson().fromJson(String.join("", Files.readAllLines(metaPath)), CustomModelMeta.class);

			return new CustomModelMeta();
		}
	}

	private static final String MODEL_REGEX = ".*" + CustomModel.getVanillaSubdirectory() + "/" + ResourcePack.getFileRegex() + "\\.json";

	public static void addCustomModel(Path path) {
		if (!path.toUri().toString().matches(MODEL_REGEX))
			return;

		CustomModelGroup group = of(path);
		if (group.getMaterial() != null && !group.getOverrides().isEmpty())
			ResourcePack.getModelGroups().add(group);
	}

	private static CustomModelGroup of(Path path) {
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

}
