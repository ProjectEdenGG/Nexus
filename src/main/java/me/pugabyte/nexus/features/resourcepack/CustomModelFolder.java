package me.pugabyte.nexus.features.resourcepack;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.features.resourcepack.CustomModelGroup.Override;

import java.util.ArrayList;
import java.util.List;

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
				if (override.getModel().matches("item" + path + "/" + ResourcePack.getFileRegex()))
					models.add(new CustomModel(override, group.getMaterial()));

		models.sort(CustomModel::compareTo);
		ResourcePack.getModels().addAll(models);
	}

}
