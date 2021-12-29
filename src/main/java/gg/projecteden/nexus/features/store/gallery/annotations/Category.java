package gg.projecteden.nexus.features.store.gallery.annotations;

import gg.projecteden.nexus.features.store.gallery.GalleryPackage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Category {
	GalleryCategory value();

	@Getter
	@AllArgsConstructor
	@RequiredArgsConstructor
	enum GalleryCategory {
		CHAT,
		VISUALS,
		INVENTORY,
		MISC,
		PETS_DISGUISES,
		;

		public List<GalleryPackage> getPackages() {
			List<GalleryPackage> packages = new ArrayList<>();

			for (GalleryPackage storePackage : GalleryPackage.values())
				if (storePackage.getCategory() == this)
					packages.add(storePackage);

			return packages;
		}

	}

}
