package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ZombieVillagerVariant implements MobHeadVariant {
	ARMORER(Profession.ARMORER),
	BUTCHER(Profession.BUTCHER),
	CARTOGRAPHER(Profession.CARTOGRAPHER),
	CLERIC(Profession.CLERIC),
	FARMER(Profession.FARMER),
	FISHERMAN(Profession.FISHERMAN),
	FLETCHER(Profession.FLETCHER),
	LEATHERWORKER(Profession.LEATHERWORKER),
	LIBRARIAN(Profession.LIBRARIAN),
	MASON(Profession.MASON),
	NITWIT(Profession.NITWIT),
	SHEPHERD(Profession.SHEPHERD),
	TOOLSMITH(Profession.TOOLSMITH),
	WEAPONSMITH(Profession.WEAPONSMITH),
	;

	private final Profession bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.ZOMBIE_VILLAGER;
	}

	public static ZombieVillagerVariant of(ZombieVillager zombieVillager) {
		return Arrays.stream(values()).filter(entry -> zombieVillager.getVillagerProfession() == entry.getBukkitType()).findFirst().orElse(null);
	}
}
