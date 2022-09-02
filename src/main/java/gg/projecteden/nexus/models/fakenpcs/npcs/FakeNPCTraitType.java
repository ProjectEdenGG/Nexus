package gg.projecteden.nexus.models.fakenpcs.npcs;

import gg.projecteden.nexus.features.fakenpc.DefaultTrait;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.AlternativeSkinsTrait;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.HologramTrait;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.LookCloseTrait;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.MirrorTrait;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.SkinTrait;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FakeNPCTraitType {
	HOLOGRAM(HologramTrait.class),
	SKIN(SkinTrait.class),
	MIRROR(MirrorTrait.class),
	LOOK_CLOSE(LookCloseTrait.class),
	ALTERNATIVE_SKINS(AlternativeSkinsTrait.class),
	;

	private final Class<? extends Trait> clazz;

	public <T extends Trait> T create() {
		try {
			return (T) clazz.newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InvalidInputException("Error creating Trait: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}

	public static FakeNPCTraitType of(Class<? extends Trait> clazz) {
		for (FakeNPCTraitType traitType : values()) {
			if (traitType.clazz.equals(clazz))
				return traitType;
		}
		return null;
	}

	public boolean isDefault() {
		return clazz.getAnnotation(DefaultTrait.class) != null;
	}
}
