package me.pugabyte.nexus.features.resourcepack;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.features.resourcepack.CustomModelGroup.Override;
import me.pugabyte.nexus.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static me.pugabyte.nexus.features.resourcepack.ResourcePack.fileRegex;

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
		folders.add(new CustomModelFolder(path + "/" + name));
	}

	public CustomModel getFirstModel() {
		if (!models.isEmpty())
			return models.get(0);

		for (CustomModelFolder folder : folders) {
			CustomModel firstModel = folder.getFirstModel();
			if (firstModel != null)
				return firstModel;
		}

		return null;
	}

	private void findModels() {
		for (CustomModelGroup group : ResourcePack.getModelGroups())
			for (Override override : group.getOverrides())
				if (override.getModel().matches("item" + path + "/" + fileRegex))
					models.add(CustomModel.builder()
							.material(group.getMaterial())
							.data(override.getPredicate().getCustomModelData())
							.fileName(StringUtils.listLast(override.getModel(), "/"))
							.build());

		models.sort(CustomModel::compareTo);
	}

	static void load() {
		CustomModelGroup.load();

		Set<String> paths = new HashSet<>();

		for (CustomModelGroup group : ResourcePack.getModelGroups()) {
			for (Override override : group.getOverrides()) {
				String path = override.getModel().replaceFirst("item", "");
				List<String> folders = new ArrayList<>(Arrays.asList(path.split("/")));
				folders.remove(folders.size() - 1); // remove file name
				paths.add(String.join("/", folders));
			}
		}

		paths = new TreeSet<>(paths);

		for (String path : paths) {
			String[] folders = path.split("/");

			String walk = "";
			for (String folder : folders) {
				if (folder.isEmpty() || folder.equals("/"))
					continue;

				String parent = walk;
				walk += "/" + folder;
				CustomModelFolder existing = ResourcePack.getRootFolder().getFolder(walk);
				if (existing == null)
					if (parent.isEmpty())
						ResourcePack.getRootFolder().addFolder(folder);
					else
						ResourcePack.getRootFolder().getFolder(parent).addFolder(folder);
			}
		}

	}

}
