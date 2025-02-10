package gg.projecteden.nexus.features.resourcepack.models.files;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
public class CustomModelFolder {

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

	private void findModels() {
		try (Stream<Path> files = Files.list(Path.of(this.path))) {
			files.forEach(path -> {
				if (!path.endsWith(".json"))
					return;

				File file = path.toFile();
				if (file.isDirectory())
					return;

				try {
					CustomModel model = Utils.getGson().fromJson(String.join("", Files.readAllLines(path)), CustomModel.class);
					model.setFolder(this);
					if (model.getOldMaterial() == null)
						model.setMaterial(Material.PAPER);
					else
						model.setMaterial(model.getOldMaterial());

					model.setData(path.toString().replace("assets/minecraft/items", "").replace(".json", ""));

					model.setMeta(getMeta(path.toString().replace(".json", ".meta")));

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
	}

	@SneakyThrows
	public CustomModel.CustomModelMeta getMeta(String model) {
		String metaUri = CustomModel.getModelsSubdirectory() + "/" + model + ".meta";
		Path metaPath = ResourcePack.getZipFile().getPath(metaUri);

		if (Files.exists(metaPath))
			return Utils.getGson().fromJson(String.join("", Files.readAllLines(metaPath)), CustomModel.CustomModelMeta.class);

		return new CustomModel.CustomModelMeta();
	}

}
