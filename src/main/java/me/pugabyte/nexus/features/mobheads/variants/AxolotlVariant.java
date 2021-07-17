package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AxolotlVariant implements MobHeadVariant {
	LUCY(Axolotl.Variant.LUCY),
	WILD(Axolotl.Variant.WILD),
	GOLD(Axolotl.Variant.GOLD),
	CYAN(Axolotl.Variant.CYAN),
	BLUE(Axolotl.Variant.BLUE),
	;

	private final Axolotl.Variant type;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.AXOLOTL;
	}

	public static AxolotlVariant of(Axolotl axolotl) {
		return Arrays.stream(values()).filter(entry -> axolotl.getVariant() == entry.getType()).findFirst().orElse(null);
	}
}
