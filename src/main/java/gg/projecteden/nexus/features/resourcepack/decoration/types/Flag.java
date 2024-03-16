package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class Flag extends WallThing {

	public Flag(boolean multiblock, String name, CustomMaterial material) {
		super(multiblock, name, material);
	}

	public Flag(String name, PrideFlagType prideFlagType) {
		super(false, name, prideFlagType.getFlag());
	}

	@AllArgsConstructor
	public enum PrideFlagType {
		ACE(CustomMaterial.FLAG_PRIDE_ACE, CustomMaterial.BUNTING_PRIDE_ACE),
		AGENDER(CustomMaterial.FLAG_PRIDE_AGENDER, CustomMaterial.BUNTING_PRIDE_AGENDER),
		ARO(CustomMaterial.FLAG_PRIDE_ARO, CustomMaterial.BUNTING_PRIDE_ARO),
		BI(CustomMaterial.FLAG_PRIDE_BI, CustomMaterial.BUNTING_PRIDE_BI),
		DEMI(CustomMaterial.FLAG_PRIDE_DEMI, CustomMaterial.BUNTING_PRIDE_DEMI),
		DEMIBOY(CustomMaterial.FLAG_PRIDE_DEMIBOY, CustomMaterial.BUNTING_PRIDE_DEMIBOY),
		DEMIGIRL(CustomMaterial.FLAG_PRIDE_DEMIGIRL, CustomMaterial.BUNTING_PRIDE_DEMIGIRL),
		DEMIROMANTIC(CustomMaterial.FLAG_PRIDE_DEMIROMANTIC, CustomMaterial.BUNTING_PRIDE_DEMIROMANTIC),
		GAY(CustomMaterial.FLAG_PRIDE_GAY, CustomMaterial.BUNTING_PRIDE_GAY),
		GENDERFLUID(CustomMaterial.FLAG_PRIDE_GENDERFLU, CustomMaterial.BUNTING_PRIDE_GENDERFLU),
		GENDERFLUX(CustomMaterial.FLAG_PRIDE_GENDERFLUX, CustomMaterial.BUNTING_PRIDE_GENDERFLUX),
		GENDERQUEER(CustomMaterial.FLAG_PRIDE_GENQUEER, CustomMaterial.BUNTING_PRIDE_GENQUEER),
		GRAY_ACE(CustomMaterial.FLAG_PRIDE_GRAYACE, CustomMaterial.BUNTING_PRIDE_GRAYACE),
		GRAY_ARO(CustomMaterial.FLAG_PRIDE_GRAYARO, CustomMaterial.BUNTING_PRIDE_GRAYARO),
		INTERSEX(CustomMaterial.FLAG_PRIDE_INTERSEX, CustomMaterial.BUNTING_PRIDE_INTERSEX),
		LESBIAN(CustomMaterial.FLAG_PRIDE_LESBIAN, CustomMaterial.BUNTING_PRIDE_LESBIAN),
		NONBINARY(CustomMaterial.FLAG_PRIDE_NONBINARY, CustomMaterial.BUNTING_PRIDE_NONBINARY),
		PAN(CustomMaterial.FLAG_PRIDE_PAN, CustomMaterial.BUNTING_PRIDE_PAN),
		POLYAM(CustomMaterial.FLAG_PRIDE_POLYAM, CustomMaterial.BUNTING_PRIDE_POLYAM),
		POLYSEX(CustomMaterial.FLAG_PRIDE_POLYSEX, CustomMaterial.BUNTING_PRIDE_POLYSEX),
		TRANS(CustomMaterial.FLAG_PRIDE_TRANS, CustomMaterial.BUNTING_PRIDE_TRANS),
		TRANSFEM(CustomMaterial.FLAG_PRIDE_TRANSFEM, CustomMaterial.BUNTING_PRIDE_TRANSFEM),
		TRANSMASC(CustomMaterial.FLAG_PRIDE_TRANSMASC, CustomMaterial.BUNTING_PRIDE_TRANSMASC),
		QUEER(CustomMaterial.FLAG_PRIDE_QUEER, CustomMaterial.BUNTING_PRIDE_QUEER),
		PRIDE(CustomMaterial.FLAG_PRIDE_PRIDE, CustomMaterial.BUNTING_PRIDE_PRIDE),
		;

		@Getter
		final CustomMaterial flag;
		@Getter
		final CustomMaterial bunting;

		@Override
		public String toString() {
			return StringUtils.camelCase(this);
		}

		public ItemStack getFlagItem() {
			return new ItemBuilder(flag).name("&e" + this + " Flag").build();
		}

		public ItemStack getBuntingItem() {
			return new ItemBuilder(bunting).name("&e" + this + " Bunting").build();
		}
	}
}
