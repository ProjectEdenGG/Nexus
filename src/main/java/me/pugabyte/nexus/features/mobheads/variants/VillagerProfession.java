package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum VillagerProfession implements MobHeadVariant {
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

	private final Profession type;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.VILLAGER;
	}

	public static VillagerProfession of(Villager villager) {
		return Arrays.stream(values()).filter(entry -> villager.getProfession() == entry.getType()).findFirst().orElse(null);
	}
}
