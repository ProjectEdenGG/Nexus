package gg.projecteden.nexus.models.fakenpcs.npcs.traits;

import gg.projecteden.nexus.features.fakenpc.DefaultTrait;
import gg.projecteden.nexus.models.fakenpcs.npcs.Trait;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.ArrayList;
import java.util.List;

@Data
@DefaultTrait
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HologramTrait extends Trait {
//	net.citizensnpcs.trait.HologramTrait

	protected boolean spawned;

	protected String name;
	boolean visibleName;

	protected List<ArmorStand> lineHolograms = new ArrayList<>();
	protected List<String> lines = new ArrayList<>();
	protected HologramDirection direction = HologramDirection.BOTTOM_UP;

	protected HologramVisibilityType visibilityType = HologramVisibilityType.ALWAYS;
	protected Integer visibilityRadius;


	public enum HologramDirection {
		BOTTOM_UP,
		TOP_DOWN,
		;
	}

	@AllArgsConstructor
	public enum HologramVisibilityType {
		HIDDEN(false, null),
		ALWAYS(true, 0),
		ALWAYS_AFTER_INTRODUCTION(true, 10), // TODO
		WITHIN_RADIUS(true, 10),
		WITHIN_RADIUS_AFTER_INTRODUCTION(true, 10),  // TODO
		;

		@Getter
		private final boolean visible;
		@Getter
		private final Integer defaultRadius;
	}

	public static List<String> setupLines(String line1, String line2, String line3, String line4) {
		List<String> lines = new ArrayList<>(List.of(trimOrEmpty(line1), trimOrEmpty(line2), trimOrEmpty(line3), trimOrEmpty(line4)));
		return lines;
//		return truncateLast(lines);
	}

	private static String trimOrEmpty(String value) {
		return Nullables.isNullOrEmpty(value) ? "" : value.trim();
	}

//	private static List<String> truncateLast(List<String> lines){
//		if(lines == null)
//			return new ArrayList<>();
//		if(lines.isEmpty())
//			return lines;
//
//		int lastIndex = lines.size() - 1;
//		if(!lines.get(lastIndex).isBlank())
//			return lines;
//		else {
//			lines.remove(lastIndex);
//			return truncateLast(lines);
//		}
//	}

	@Override
	public List<String> getDebug() {
		return new ArrayList<>() {{
			add("spawned=" + spawned);
			add("name=" + name);
			add("nameVisible=" + visibleName);
			add("lines=" + lines);
			add("direction=" + direction);
			add("type=" + visibilityType);
			add("radius=" + visibilityRadius);
		}};
	}

}
