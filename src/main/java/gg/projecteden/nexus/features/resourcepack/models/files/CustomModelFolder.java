package gg.projecteden.nexus.features.resourcepack.models.files;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
public class CustomModelFolder implements Comparable<CustomModelFolder> {

	@NonNull
	private String path;
	private List<CustomModelFolder> folders = new ArrayList<>();
	private List<CustomModel> models = new ArrayList<>();

	public CustomModelFolder(@NonNull String path) {
		this.path = path;
		this.path = this.path.replaceFirst("//", "/");
		ResourcePack.getFolders().add(this);
		findModels();
	}

	public CustomModelFolder getFolder(String path) {
		if (this.path.equals(path))
			return this;

		for (CustomModelFolder folder : folders) {
			if (path.equals(folder.getPath()))
				return folder;
			else if (path.startsWith(folder.getPath()))
				return folder.getFolder(path);
		}

		return null;
	}

	public String getDisplayPath() {
		String path = this.path;
		if (path.startsWith("/"))
			path = path.replaceFirst("/", "");
		return path;
	}

	public void addFolder(String name) {
		if ("item".equals(name))
			return;

		folders.add(new CustomModelFolder(path + "/" + name));
	}

	public CustomModel getIcon() {
		return getIcon(model -> true);
	}

	public CustomModel getIcon(java.util.function.Predicate<CustomModel> predicate) {
		if (!models.isEmpty()) {
			final CustomModel icon = models.stream()
				.filter(model -> "icon".equals(model.getFileName()))
				.findFirst()
				.orElse(null);

			if (icon != null && predicate.test(icon))
				return icon;

			for (CustomModel next : models)
				if (predicate.test(next))
					return next;
		}

		for (CustomModelFolder folder : folders) {
			CustomModel icon = folder.getIcon(predicate);
			if (icon != null)
				return icon;
		}

		return null;
	}

	private String getFullPath() {
		if (path.endsWith("/"))
			return (CustomModel.getItemsSubdirectory() + path.substring(0, path.length() - 1)).replace("//", "/");
		return (CustomModel.getItemsSubdirectory() + path).replace("//", "/");
	}

	private void findModels() {
		try (Stream<Path> files = Files.walk(ResourcePack.getZipFile().getPath(getFullPath()), 1)) {
			files.forEach(path -> {
				Nexus.debug("Full path: " + getFullPath());
				if (path.toString().equals(getFullPath()))
					return;
				Nexus.debug("Find models path: " + path);

				if (!path.toString().endsWith(".json")) {
					Nexus.debug("Adding folder");
					addFolder(path.toString().substring(path.toString().lastIndexOf("/") + 1));
					return;
				}

				try {
					Nexus.debug("Processing model file");
					CustomModel model = Utils.getGson().fromJson(String.join("", Files.readAllLines(path)), CustomModel.class);
					model.setFolder(this);

					String data = path.toString().split("assets/minecraft/items/")[1].replace(".json", "");
					model.setData(data);

					CustomMaterial customMaterial = CustomMaterial.of(data);
					if (customMaterial == null)
						model.setMaterial(Material.PAPER);
					else
						model.setMaterial(customMaterial.getMaterial());

					model.setFileName(path.toString().substring(path.toString().lastIndexOf("/") + 1).replace(".json", ""));

					model.setMeta(getMeta(path.toString().replace(".json", ".meta")));
					Nexus.debug("Adding model: " + data);

					models.add(model);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			Nexus.log("Error while reading custom model folder");
			e.printStackTrace();
		}

		models.sort(CustomModel::compareTo);
		models.forEach(model -> ResourcePack.getModels().put(model.getId(), model));
		folders.sort(CustomModelFolder::compareTo);
	}

	@SneakyThrows
	public CustomModel.CustomModelMeta getMeta(String model) {
		String metaUri = CustomModel.getModelsSubdirectory() + "/" + model + ".meta";
		Path metaPath = ResourcePack.getZipFile().getPath(metaUri);

		if (Files.exists(metaPath))
			return Utils.getGson().fromJson(String.join("", Files.readAllLines(metaPath)), CustomModel.CustomModelMeta.class);

		return new CustomModel.CustomModelMeta();
	}

	@Override
	public int compareTo(@NotNull CustomModelFolder other) {
		return CharSequence.compare(path, other.getPath());
	}
}
