package gg.projecteden.nexus.models.costume;

import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance;
import gg.projecteden.nexus.features.resourcepack.models.files.ItemModelFolder;
import gg.projecteden.nexus.features.store.StoreCommand;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Costume {
	private final String id;
	private final CostumeType type;
	private final ItemStack item;

	public Costume(ItemModelInstance model, CostumeType type) {
		this.id = getId(model);
		this.type = model.getItem().getType() == Material.PLAYER_HEAD ? CostumeType.HAND : type;
		this.item = new ItemBuilder(model.getItem())
			.undroppable()
			.unframeable()
			.unplaceable()
			.unstorable()
			.untradeable()
			.build();
	}

	public static String getId(ItemModelInstance model) {
		return model.getId().replaceFirst("costumes/", "");
	}

	public ItemModelInstance getModel() {
		return ItemModelInstance.of("costumes/" + id);
	}

	public boolean isDyeable() {
		return MaterialTag.DYEABLE.isTagged(item);
	}

	@Getter
	@AllArgsConstructor
	public enum CostumeType {
		HAT(EquipmentSlot.HEAD, 2),
		HAND(EquipmentSlot.OFF_HAND, 5),
		BACK(EquipmentSlot.HEAD, 6),
		;

		private final EquipmentSlot slot;
		private final int menuHeaderSlot;

		public ItemModelFolder getFolder() {
			return getRootFolder().getFolder(ROOT_FOLDER + "/" + name().toLowerCase());
		}

		public static Set<EquipmentSlot> getSlots() {
			return Arrays.stream(values()).map(CostumeType::getSlot).collect(Collectors.toSet());
		}
	}

	public static Costume of(ItemModelInstance model) {
		return of(getId(model));
	}

	public static Costume of(String id) {
		if (id == null)
			return null;

		for (Costume costume : costumes)
			if (costume.getId().equalsIgnoreCase(id))
				return costume;
		return null;
	}

	public static List<Costume> values() {
		return costumes;
	}

	private static final List<Costume> costumes = new ArrayList<>();
	public static final String ROOT_FOLDER = "/costumes";
	public static final String EXCLUSIVE = "exclusive";
	public static final String ARMOR = "hat/armor";
	public static final String BACK = "back";
	public static final String STORE_URL_VISUALS = StoreCommand.URL + "/category/visuals";
	public static final String STORE_URL_BOOSTS = StoreCommand.URL + "/category/boosts";

	public static ItemModelFolder getRootFolder() {
		return ResourcePack.getRootFolder().getFolder(ROOT_FOLDER);
	}

	public static void loadAll() {
		costumes.clear();
		for (CostumeType type : CostumeType.values())
			load(type, type.getFolder());
	}

	private static void load(CostumeType type, ItemModelFolder folder) {
		if (folder == null)
			return;

		for (ItemModelFolder subfolder : folder.getFolders())
			load(type, subfolder);

		for (ItemModelInstance model : folder.getModels())
			if (model.getMaterial() != Material.CYAN_STAINED_GLASS_PANE) // legacy gg hat
				costumes.add(new Costume(model, type));
	}

}
