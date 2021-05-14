package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.utils.CitizensUtils;
import net.citizensnpcs.npc.CitizensNPC;

@AllArgsConstructor
public enum BearFair21NPC {
	// Merchants
	ARTIST("Sage", 2657),
	BAKER("Rye", 2659),
	BARTENDER("Cosmo", 2655),
	BLACKSMITH("Alvor", 2656),
	BOTANIST("Fern", 2661),
	BREWER("Charlie", 2662),
	COLLECTOR("Pluto", 2750),
	FISHERMAN1("Gage", 2653),
	INVENTOR("Joshua", 2660),
	PASTRY_CHEF("Maple", 2654),
	SORCERER("Lucian", 2658),
	// Quest Givers
	MAYOR("John", 3838),
	LUMBERJACK("Flint", 3845),
	BEEKEEPER("Harold", 3844),
	FISHERMAN2("Nate", 3841),
	// Misc
	CAPTAIN("Captain", 3839),
	WAKKAFLOCKA("WakkaFlocka", 3798),
	;

	@Getter
	private final String name;
	@Getter
	private final int id;

	public CitizensNPC getNPC() {
		return (CitizensNPC) CitizensUtils.getNPC(this.id);
	}

}
