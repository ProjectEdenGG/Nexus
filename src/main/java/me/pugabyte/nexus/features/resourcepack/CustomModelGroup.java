package me.pugabyte.nexus.features.resourcepack;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.nexus.features.resourcepack.ResourcePack.fileRegex;

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
	}

	private static void addCustomModel(Path path) {
		if (!path.toUri().toString().matches(".*" + subdirectory + "/" + fileRegex + "\\.json"))
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

	static final String subdirectory = "assets/minecraft/models/item";
	static final URI fileUri = URI.create("jar:" + ResourcePack.getFile().toURI());

	static void load() {
		try (FileSystem fileSystem = FileSystems.newFileSystem(fileUri, Collections.emptyMap())) {
			for (Path root : fileSystem.getRootDirectories()) {
				Files.walk(root).forEach(path -> {
					if (path.toUri().toString().contains(subdirectory))
						addCustomModel(path);
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
