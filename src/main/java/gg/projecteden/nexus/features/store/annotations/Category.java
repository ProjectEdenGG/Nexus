package gg.projecteden.nexus.features.store.annotations;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.store.Package;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Category {
	StoreCategory value();

	@Getter
	@AllArgsConstructor
	@RequiredArgsConstructor
	enum StoreCategory {
		CHAT(Material.WRITABLE_BOOK),
		BOOSTS(Material.EXPERIENCE_BOTTLE),
		VISUALS(Material.NETHER_STAR),
		INVENTORY(Material.CHEST),
		MISC(Material.EMERALD),
		PETS(Material.BONE),
		DISGUISES(Material.CREEPER_HEAD);

		@NonNull
		private final Material material;
		private String modelId;

		StoreCategory(CustomMaterial material) {
			this.material = material.getMaterial();
			this.modelId = material.getModel();
		}

		public ItemBuilder getDisplayItem() {
			return new ItemBuilder(material).model(modelId).name(StringUtils.camelCase(name()));
		}

		public List<Package> getPackages() {
			List<Package> packages = new ArrayList<>();

			for (Package storePackage : Package.values())
				if (storePackage.isEnabled())
					if (storePackage.getCategory() == this)
						packages.add(storePackage);

			return packages;
		}

	}

}
