package gg.projecteden.nexus.utils;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.bukkit.Material;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.nexus.utils.IOUtils.getPluginFile;
import static org.apache.commons.io.FileUtils.readFileToString;

public class ColorUtils {
	public static final Map<Material, Color> BLOCK_PALETTE = new HashMap<>();

	static {
		try {
			Map<String, Map<String, Double>> blockPalette = new Gson().fromJson(readFileToString(getPluginFile("minecraft_block_palette.json")), Map.class);;

			for (String material : blockPalette.keySet()) {
				Material match = Material.matchMaterial(material);
				if (match == null)
					continue;

				if (!match.isBlock() || !match.isSolid() || !match.isOccluding())
					continue;

				if (MaterialTag.SHULKER_BOXES.isTagged(match))
					continue;

				if (MaterialTag.TREE_LOGS.isTagged(match)) {
					Material wood = WoodType.of(match).getWood();
					if (wood != null)
						match = wood;
				}

				if (MaterialTag.STRIPPED_LOGS.isTagged(match)) {
					Material wood = WoodType.of(match).getStrippedWood();
					if (wood != null)
						match = wood;
				}

				var colors = blockPalette.get(material);
				BLOCK_PALETTE.put(match, new Color(colors.get("r").intValue(), colors.get("g").intValue(), colors.get("b").intValue()));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SneakyThrows
	public static Material closestBlock(Color target, MaterialTag exclude, MaterialTag include) {
		double[] targetLAB = toLAB(target);

		Set<Material> materials = new HashSet<>(BLOCK_PALETTE.keySet());

		if (include.size() > 0)
			materials.retainAll(include.getValues());
		materials.removeAll(exclude.getValues());

		return materials.stream()
			.min(Comparator.comparingDouble(material -> deltaE(targetLAB, toLAB(BLOCK_PALETTE.get(material)))))
			.orElseThrow();
	}

	// --- Conversion Helpers ---

	public static double[] toLAB(Color color) {
		// Convert sRGB to XYZ
		double r = pivotRGB(color.getRed() / 255.0);
		double g = pivotRGB(color.getGreen() / 255.0);
		double b = pivotRGB(color.getBlue() / 255.0);

		// Observer=2°, Illuminant=D65
		double x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.95047;
		double y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.00000;
		double z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.08883;

		x = pivotXYZ(x);
		y = pivotXYZ(y);
		z = pivotXYZ(z);

		double l = 116 * y - 16;
		double a = 500 * (x - y);
		double b2 = 200 * (y - z);

		return new double[]{l, a, b2};
	}

	public static double pivotRGB(double c) {
		return (c <= 0.04045) ? (c / 12.92) : Math.pow((c + 0.055) / 1.055, 2.4);
	}

	public static double pivotXYZ(double c) {
		return (c > 0.008856) ? Math.cbrt(c) : (7.787 * c) + (16.0 / 116.0);
	}

	// DeltaE (CIE76)
	public static double deltaE(double[] lab1, double[] lab2) {
		double dl = lab1[0] - lab2[0];
		double da = lab1[1] - lab2[1];
		double db = lab1[2] - lab2[2];
		return Math.sqrt(dl * dl + da * da + db * db);
	}

}
