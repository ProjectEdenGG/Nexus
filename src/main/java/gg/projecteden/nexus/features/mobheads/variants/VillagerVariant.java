package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum VillagerVariant implements MobHeadVariant {
	NONE("12199", Profession.NONE),
	ARMORER("23911", Profession.ARMORER),
	BUTCHER("30561", Profession.BUTCHER),
	CARTOGRAPHER("23913", Profession.CARTOGRAPHER),
	CLERIC("23914", Profession.CLERIC),
	FARMER("23757", Profession.FARMER),
	FISHERMAN("23756", Profession.FISHERMAN),
	FLETCHER("30556", Profession.FLETCHER),
	LEATHERWORKER("32900", Profession.LEATHERWORKER),
	LIBRARIAN("30555", Profession.LIBRARIAN),
	MASON("32914", Profession.MASON),
	NITWIT("23089", Profession.NITWIT),
	SHEPHERD("30554", Profession.SHEPHERD),
	TOOLSMITH("32937", Profession.TOOLSMITH),
	WEAPONSMITH("23910", Profession.WEAPONSMITH),
	;

	private final String headId;
	private final Profession bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.VILLAGER;
	}

	public static VillagerVariant of(Villager villager) {
		return Arrays.stream(values()).filter(entry -> villager.getProfession() == entry.getBukkitType()).findFirst().orElse(null);
	}
}
