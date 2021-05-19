package me.pugabyte.nexus.features.store.annotations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

	}

}
