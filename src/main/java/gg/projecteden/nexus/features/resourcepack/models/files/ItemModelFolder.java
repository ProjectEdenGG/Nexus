package gg.projecteden.nexus.features.resourcepack.models.files;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance.CustomItemModelMeta;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
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
public class ItemModelFolder implements Comparable<ItemModelFolder> {

	@NonNull
	private String path;
	private List<ItemModelFolder> folders = new ArrayList<>();
	private List<ItemModelInstance> models = new ArrayList<>();

	public ItemModelFolder(@NonNull String path) {
		this.path = path;
		this.path = this.path.replaceFirst("//", "/");
		ResourcePack.getFolders().add(this);
		findModels();
	}

	public ItemModelFolder getFolder(String path) {
		if (this.path.equals(path))
			return this;

		for (ItemModelFolder folder : folders) {
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

		folders.add(new ItemModelFolder(path + "/" + name));
	}

	public ItemModelInstance getIcon() {
		return getIcon(model -> true);
	}

	public ItemModelInstance getIcon(java.util.function.Predicate<ItemModelInstance> predicate) {
		if (!models.isEmpty()) {
			final ItemModelInstance icon = models.stream()
				.filter(model -> "icon".equals(model.getFileName()))
				.findFirst()
				.orElse(null);

			if (icon != null && predicate.test(icon))
				return icon;

			for (ItemModelInstance next : models)
				if (predicate.test(next))
					return next;
		}

		for (ItemModelFolder folder : folders) {
			ItemModelInstance icon = folder.getIcon(predicate);
			if (icon != null)
				return icon;
		}

		return null;
	}

	private String getFullPath() {
		if (path.endsWith("/"))
			return (ItemModelInstance.getItemsSubdirectory() + path.substring(0, path.length() - 1)).replace("//", "/");
		return (ItemModelInstance.getItemsSubdirectory() + path).replace("//", "/");
	}

	private void findModels() {
		try (Stream<Path> files = Files.walk(ResourcePack.getZipFile().getPath(getFullPath()), 1)) {
			files.forEach(path -> {
				Nexus.debug("Full path: " + getFullPath());
				if (path.toString().equals(getFullPath()))
					return;
				Nexus.debug("Find models path: " + path);

				if (path.toString().endsWith(".meta"))
					return;

				if (!path.toString().endsWith(".json")) {
					Nexus.debug("Adding folder");
					addFolder(path.toString().substring(path.toString().lastIndexOf("/") + 1));
					return;
				}

				if (this.path.equals("/"))
					return;

				try {
					Nexus.debug("Processing model file");
					ItemModelInstance model = Utils.getGson().fromJson(String.join("", Files.readAllLines(path)), ItemModelInstance.class);
					model.setFolder(this);

					String data = path.toString().split("assets/minecraft/items/")[1].replace(".json", "");
					model.setItemModel(data);

					ItemModelType itemModelType = ItemModelType.of(data);
					if (itemModelType == null)
						model.setMaterial(Material.PAPER);
					else
						model.setMaterial(itemModelType.getMaterial());

					model.setFileName(path.toString().substring(path.toString().lastIndexOf("/") + 1).replace(".json", ""));

					model.setMeta(getMeta(data));
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

		models.sort(ItemModelInstance::compareTo);
		models.forEach(model -> ResourcePack.getModels().put(model.getId(), model));
		folders.sort(ItemModelFolder::compareTo);
	}

	@SneakyThrows
	public CustomItemModelMeta getMeta(String model) {
		String metaUri = ItemModelInstance.getItemsSubdirectory() + "/" + model + ".meta";
		Path metaPath = ResourcePack.getZipFile().getPath(metaUri);

		if (Files.exists(metaPath))
			return Utils.getGson().fromJson(String.join("", Files.readAllLines(metaPath)), CustomItemModelMeta.class);

		return new CustomItemModelMeta();
	}

	@Override
	public int compareTo(@NotNull ItemModelFolder other) {
		return CharSequence.compare(path, other.getPath());
	}
}
