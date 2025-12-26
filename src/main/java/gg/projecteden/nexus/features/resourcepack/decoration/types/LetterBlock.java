package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableBlockThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class LetterBlock extends DyeableBlockThing {
	private final LetterBlockType blockType;

	public LetterBlock(LetterBlockType blockType) {
		super(blockType.getName(), blockType.getModel(), ColorableType.DYE, RotationSnap.BOTH);
		this.blockType = blockType;
	}

	@Getter
	@AllArgsConstructor
	public enum LetterBlockType {
		A(ItemModelType.LETTER_BLOCK_A),
		B(ItemModelType.LETTER_BLOCK_B),
		C(ItemModelType.LETTER_BLOCK_C),
		P(ItemModelType.LETTER_BLOCK_P),
		;

		private final ItemModelType model;

		public String getName() {
			return "Letter Block " + this.name();
		}
	}
}
