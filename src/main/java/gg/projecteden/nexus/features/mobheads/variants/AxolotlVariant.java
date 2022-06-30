package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Axolotl.Variant;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AxolotlVariant implements MobHeadVariant {
	LUCY("41592", Variant.LUCY),
	WILD("41591", Variant.WILD),
	GOLD("41590", Variant.GOLD),
	CYAN("41589", Variant.CYAN),
	BLUE("42453", Variant.BLUE),
	;

	private final String headId;
	private final Variant bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.AXOLOTL;
	}

	public static AxolotlVariant of(Axolotl axolotl) {
		return Arrays.stream(values()).filter(entry -> axolotl.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}
