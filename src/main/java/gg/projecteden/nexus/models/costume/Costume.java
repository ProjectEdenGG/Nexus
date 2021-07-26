package gg.projecteden.nexus.models.costume;

import gg.projecteden.nexus.features.resourcepack.CustomModel;
import gg.projecteden.nexus.features.resourcepack.CustomModelFolder;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.store.StoreCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Costume {
	private final String id;
	private final CustomModel model;
	private final CostumeType type;

	public Costume(CustomModel model, CostumeType type) {
		this.id = model.getFolder().getDisplayPath().replace("costumes/", "") + "/" + model.getFileName();
		this.model = model;
		this.type = type;
	}

	@Getter
	@AllArgsConstructor
	public enum CostumeType {
		HAT(EquipmentSlot.HEAD),
		HAND(EquipmentSlot.HAND),
		;

		private final EquipmentSlot slot;

		public CustomModelFolder getFolder() {
			return ROOT_FOLDER.getFolder("/costumes/" + name().toLowerCase());
		}
	}

	public static Costume of(String id) {
		for (Costume costume : costumes)
			if (costume.getId().equalsIgnoreCase(id))
				return costume;
		return null;
	}

	public static List<Costume> values() {
		return costumes;
	}

	private static final List<Costume> costumes = new ArrayList<>();
	public static final CustomModelFolder ROOT_FOLDER = ResourcePack.getRootFolder().getFolder("/costumes");
	public static final String EXCLUSIVE = "exclusive";
	public static final String STORE_URL = StoreCommand.URL + "/category/costumes";

	static {
		for (CostumeType type : CostumeType.values())
			load(type, type.getFolder());
	}

	private static void load(CostumeType type, CustomModelFolder folder) {
		if (folder == null)
			return;

		for (CustomModelFolder subfolder : folder.getFolders())
			load(type, subfolder);

		for (CustomModel model : folder.getModels())
			costumes.add(new Costume(model, type));
	}

}
