package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.ZombieVillager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ZombieVillagerVariant implements MobHeadVariant {
	NONE("27600", Profession.NONE),
	ARMORER("31544", Profession.ARMORER),
	BUTCHER("27598", Profession.BUTCHER),
	CARTOGRAPHER("27597", Profession.CARTOGRAPHER),
	CLERIC("27596", Profession.CLERIC),
	FARMER("31525", Profession.FARMER),
	FISHERMAN("31526", Profession.FISHERMAN),
	FLETCHER("27593", Profession.FLETCHER),
	LEATHERWORKER("39094", Profession.LEATHERWORKER),
	LIBRARIAN("27592", Profession.LIBRARIAN),
	MASON("27600", Profession.MASON),
	NITWIT("30552", Profession.NITWIT),
	SHEPHERD("27591", Profession.SHEPHERD),
	TOOLSMITH("27600", Profession.TOOLSMITH),
	WEAPONSMITH("31521", Profession.WEAPONSMITH),
	;

	private final String headId;
	private final Profession bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.ZOMBIE_VILLAGER;
	}

	public static ZombieVillagerVariant of(ZombieVillager zombieVillager) {
		return Arrays.stream(values()).filter(entry -> zombieVillager.getVillagerProfession() == entry.getBukkitType()).findFirst().orElse(null);
	}
}
