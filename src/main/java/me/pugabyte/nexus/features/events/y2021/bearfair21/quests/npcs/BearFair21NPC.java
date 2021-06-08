package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.StringUtils;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public enum BearFair21NPC {
	// Merchants
	ARCHITECT("Zach", 4109),
	ARTIST("Sage", 2657),
	BAKER("Rye", 2659),
	BARTENDER("Cosmo", 2655),
	BLACKSMITH("Alvor", 2656),
	BOTANIST("Fern", 2661),
	CARPENTER("Ron", 4110),
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
	AERONAUT("Skye", 4111),
	// Misc
	ADMIRAL("Phoenix", 3839),
	ORGANIZER("Wakka", 3798),

	// MGN
	AXEL("Axel", 4126),
	XAVIER("Xavier", 4127),
	RYAN("Ryan", 4128),
	HEATHER("Heather", 4134),
	TRENT("Trent", 4135),
	GARY("Gary", 4136),
	// PUGMAS
	MAYOR_PUGMAS("Mayor", 4130),
	GRINCH("Grinch", 4129),
	// HALLOWEEN
	JOSE("Jose", 4131),
	SANTIAGO("Santiago", 4132),
	ANA("Ana", 4133),
	// SDU
	;

	@Getter
	private final String name;
	@Getter
	private final int id;

	public static BearFair21NPC from(int id) {
		for (BearFair21NPC bearFair21NPC : values()) {
			if (bearFair21NPC.getId() == id)
				return bearFair21NPC;
		}
		return null;
	}

	public static BearFair21NPC of(Integer id) {
		return Arrays.stream(values()).filter(bearFair21NPC -> bearFair21NPC.getId() == id).findFirst().orElse(null);
	}

	public @Nullable NPC getNPC() {
		return CitizensUtils.getNPC(this.id);
	}

	public List<EntityArmorStand> showName(Player player) {
		NPC npc = getNPC();
		if (npc == null) return null;

		String npcJob = StringUtils.camelCase(this).replaceAll("[0-9]+", "");
		if (npcJob.equalsIgnoreCase(name))
			return Collections.singletonList(PacketUtils.entityNameFake(player, npc.getEntity(), name));
		else
			return PacketUtils.entityNameFake(player, npc.getEntity(), npcJob, name);
	}
}
