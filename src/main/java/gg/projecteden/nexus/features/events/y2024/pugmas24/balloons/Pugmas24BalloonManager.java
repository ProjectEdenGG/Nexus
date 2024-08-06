package gg.projecteden.nexus.features.events.y2024.pugmas24.balloons;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Pugmas24BalloonManager {
	protected static final String REGION_BASE = Pugmas24.get().getRegionName() + "_balloon_";
	protected static final String REGION_PLACEMENT_REGEX = REGION_BASE + "placement_[0-9]+";
	protected static final String DIRECTORY = "pugmas24/balloons/";
	protected static final String SCHEM_TEMPLATE = DIRECTORY + "template/";
	protected static final String SCHEM_USER = DIRECTORY + "user/";
	protected static final WorldEditUtils worldedit = Pugmas24.get().worldedit();
	protected static final WorldGuardUtils worldguard = Pugmas24.get().worldguard();
	//

	@Getter
	private static final Map<String, String> userPlacementRegions = new HashMap<>();

	public Pugmas24BalloonManager() {
		new Pugmas24BalloonEditor();

		// TODO: SOME SORT OF ANIMATION/PARTICLES?
		Tasks.repeat(TickTime.SECOND.x(2), TickTime.MINUTE, () -> {
			List<ProtectedRegion> regions = new ArrayList<>(getPlacementRegions());
			Collections.shuffle(regions);
			for (ProtectedRegion region : regions) {
				Location location = worldedit.toLocation(worldguard.convert(region).getMinimumPoint());
				String schematicPath = getSchematicPath(getRandomUserSchematic());

				String uuid = schematicPath.replaceAll(SCHEM_USER, "");
				Nerd player = Nerd.of(uuid);

				userPlacementRegions.put(region.getId(), player.getUniqueId().toString());
				pasteBalloonAsync(schematicPath, location);
			}
		});
	}

	public static List<File> getUserSchematics() {
		return getSchematicFiles(SCHEM_USER);
	}

	public static Set<ProtectedRegion> getPlacementRegions() {
		return worldguard.getRegionsLike(REGION_PLACEMENT_REGEX);
	}

	public static int getTotalTemplateSchematics() {
		return getSchematicFiles(SCHEM_TEMPLATE).size();
	}

	private static final Set<File> usedUserSchematics = new HashSet<>();

	private static File getRandomUserSchematic() {
		List<File> userSchematics = getUserSchematics();
		int userSchematicsSize = userSchematics.size();

		if (userSchematicsSize == 0)
			throw new InvalidInputException("Can't find any user schematics");

		if (usedUserSchematics.size() >= userSchematicsSize) {
			usedUserSchematics.clear();
		} else {
			usedUserSchematics.forEach(userSchematics::remove);
		}

		File schematic = RandomUtils.randomElement(userSchematics);
		if (!schematic.exists())
			throw new InvalidInputException("Schematic id '" + schematic.getName() + "' does not exist");

		usedUserSchematics.add(schematic);
		return schematic;
	}

	protected static void pasteBalloonAsync(String filePath, Location location) {
		worldedit.paster().file(filePath).at(location).pasteAsync();
	}

	private static @NonNull List<File> getSchematicFiles(String subDirectory) {
		String directory = WorldEditUtils.getSchematicsDirectory() + subDirectory;
		File dir = new File(directory);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0)
			return Collections.emptyList();

		return new ArrayList<>(List.of(files));
	}

	private static String getSchematicPath(File file) {
		String path = file.getPath().replace(WorldEditUtils.getSchematicsDirectory(), "");
		path = path.replaceAll(".schem", "");

		return path;
	}


}
