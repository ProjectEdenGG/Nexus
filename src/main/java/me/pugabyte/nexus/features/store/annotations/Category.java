package me.pugabyte.nexus.features.store.annotations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.store.Package;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import static eden.utils.StringUtils.camelCase;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Category {
	StoreCategory value();

	@Getter
	@AllArgsConstructor
	@RequiredArgsConstructor
	enum StoreCategory {
		CHAT(Material.WRITABLE_BOOK),
		VISUALS(Material.NETHER_STAR),
		INVENTORY(Material.CHEST),
		MISC(Material.EMERALD),
		PETS(Material.BONE),
		DISGUISES(Material.CREEPER_HEAD);

		@NonNull
		private final Material material;
		private int customModelData;

		public ItemBuilder getDisplayItem() {
			return new ItemBuilder(material).customModelData(customModelData).name(camelCase(name()));
		}

		public List<Package> getPackages() {
			List<Package> packages = new ArrayList<>();

			for (Package storePackage : Package.values())
				if (storePackage.getCategory() == this)
					packages.add(storePackage);

			return packages;
		}

	}

}
