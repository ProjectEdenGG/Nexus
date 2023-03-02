package gg.projecteden.nexus.features.survival.decorationstore;

import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DecorationStoreUtils {
	@Getter
	private static final String layoutDirectory = "survival/decor_store/";
	@Getter
	public static final String layoutDirectoryAbsolute = WorldEditUtils.getSchematicsDirectory() + layoutDirectory;
	@Getter
	static final String storeRegion = "spawn_decor_store";
	@Getter
	private static final String schematicStoreRegion = storeRegion + "_schem";

	public static @NonNull File[] getLayoutFiles() {
		File dir = new File(layoutDirectoryAbsolute);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0)
			throw new NullPointerException("Couldn't find any schematics under directory: " + getLayoutDirectory());

		return files;
	}

	public static String getLayoutPath(int id) {
		return getLayoutDirectory() + id;
	}

	public static int getRandomLayoutId() {
		return getRandomLayoutId(List.of(getLayoutFiles()));
	}

	public static int getRandomLayoutId(List<File> files) {
		return RandomUtils.randomInt(1, files.size());
	}

	public static String getRandomLayoutPath() {
		return getLayoutPath(getRandomLayoutId());
	}

	public static String getRandomLayoutPath(String currentSchematicPath) {
		List<File> layouts = new ArrayList<>();
		for (File layoutFile : getLayoutFiles()) {
			String schematicPath = getSchematicPath(layoutFile);
			Dev.WAKKA.send("schem file: " + schematicPath);

			if (schematicPath.equalsIgnoreCase(currentSchematicPath)) {
				Dev.WAKKA.send("skipping");
				continue;
			}

			layouts.add(layoutFile);
		}

		if (layouts.isEmpty())
			throw new NullPointerException("Couldn't find any schematics under directory: " + getLayoutDirectory());

		return getLayoutPath(getRandomLayoutId(layouts));
	}

	public static String getSchematicPath(File layoutFile) {
		String path;
		path = layoutFile.getPath().replace(WorldEditUtils.getSchematicsDirectory(), "");
		path = path.replaceAll(".schem", "");

		return path;
	}
}
