package gg.projecteden.nexus.utils;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import lombok.AllArgsConstructor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public enum CopperState implements IterableEnum {
	NORMAL,
	EXPOSED,
	WEATHERED,
	OXIDIZED,
	;

	private static final List<CopperState> list = Arrays.asList(values());

	static {
		Collections.reverse(list);
	}

	public static CopperState of(Material material) {
		for (CopperState state : list)
			if (material.name().contains(state.name()))
				return state;

		return NORMAL;
	}

	public Material get(CopperBlockType blockType) {
		return blockType.of(this);
	}

	@AllArgsConstructor
	public enum CopperBlockType {
		BLOCK(state -> state == NORMAL ? Material.COPPER_BLOCK : Material.getMaterial(state.name() + "_COPPER")),
		CUT_BLOCK(state -> Material.getMaterial((state == NORMAL ? "" : state.name() + "_") + "CUT_COPPER")),
		CUT_STAIR(state -> Material.getMaterial((state == NORMAL ? "" : state.name() + "_") + "CUT_COPPER_STAIRS")),
		CUT_SLAB(state -> Material.getMaterial((state == NORMAL ? "" : state.name() + "_") + "CUT_COPPER_SLAB")),
		;

		private final Function<CopperState, Material> getter;

		public Material of(CopperState state) {
			return getter.apply(state);
		}
	}

}
