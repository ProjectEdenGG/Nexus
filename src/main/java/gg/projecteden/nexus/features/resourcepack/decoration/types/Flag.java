package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class Flag extends WallThing {

	public Flag(boolean multiblock, String name, ItemModelType itemModelType) {
		super(multiblock, name, itemModelType);
	}

	public Flag(String name, PrideFlagType prideFlagType) {
		super(false, name, prideFlagType.getFlag());
	}

	@AllArgsConstructor
	public enum PrideFlagType {
		ACE(ItemModelType.FLAG_PRIDE_ACE, ItemModelType.BUNTING_PRIDE_ACE),
		AGENDER(ItemModelType.FLAG_PRIDE_AGENDER, ItemModelType.BUNTING_PRIDE_AGENDER),
		ARO(ItemModelType.FLAG_PRIDE_ARO, ItemModelType.BUNTING_PRIDE_ARO),
		BI(ItemModelType.FLAG_PRIDE_BI, ItemModelType.BUNTING_PRIDE_BI),
		DEMI(ItemModelType.FLAG_PRIDE_DEMI, ItemModelType.BUNTING_PRIDE_DEMI),
		DEMIBOY(ItemModelType.FLAG_PRIDE_DEMIBOY, ItemModelType.BUNTING_PRIDE_DEMIBOY),
		DEMIGIRL(ItemModelType.FLAG_PRIDE_DEMIGIRL, ItemModelType.BUNTING_PRIDE_DEMIGIRL),
		DEMIROMANTIC(ItemModelType.FLAG_PRIDE_DEMIROMANTIC, ItemModelType.BUNTING_PRIDE_DEMIROMANTIC),
		GAY(ItemModelType.FLAG_PRIDE_GAY, ItemModelType.BUNTING_PRIDE_GAY),
		GENDERFLUID(ItemModelType.FLAG_PRIDE_GENDERFLU, ItemModelType.BUNTING_PRIDE_GENDERFLU),
		GENDERFLUX(ItemModelType.FLAG_PRIDE_GENDERFLUX, ItemModelType.BUNTING_PRIDE_GENDERFLUX),
		GENDERQUEER(ItemModelType.FLAG_PRIDE_GENQUEER, ItemModelType.BUNTING_PRIDE_GENQUEER),
		GRAY_ACE(ItemModelType.FLAG_PRIDE_GRAYACE, ItemModelType.BUNTING_PRIDE_GRAYACE),
		GRAY_ARO(ItemModelType.FLAG_PRIDE_GRAYARO, ItemModelType.BUNTING_PRIDE_GRAYARO),
		INTERSEX(ItemModelType.FLAG_PRIDE_INTERSEX, ItemModelType.BUNTING_PRIDE_INTERSEX),
		LESBIAN(ItemModelType.FLAG_PRIDE_LESBIAN, ItemModelType.BUNTING_PRIDE_LESBIAN),
		NONBINARY(ItemModelType.FLAG_PRIDE_NONBINARY, ItemModelType.BUNTING_PRIDE_NONBINARY),
		PAN(ItemModelType.FLAG_PRIDE_PAN, ItemModelType.BUNTING_PRIDE_PAN),
		POLYAM(ItemModelType.FLAG_PRIDE_POLYAM, ItemModelType.BUNTING_PRIDE_POLYAM),
		POLYSEX(ItemModelType.FLAG_PRIDE_POLYSEX, ItemModelType.BUNTING_PRIDE_POLYSEX),
		TRANS(ItemModelType.FLAG_PRIDE_TRANS, ItemModelType.BUNTING_PRIDE_TRANS),
		TRANSFEM(ItemModelType.FLAG_PRIDE_TRANSFEM, ItemModelType.BUNTING_PRIDE_TRANSFEM),
		TRANSMASC(ItemModelType.FLAG_PRIDE_TRANSMASC, ItemModelType.BUNTING_PRIDE_TRANSMASC),
		QUEER(ItemModelType.FLAG_PRIDE_QUEER, ItemModelType.BUNTING_PRIDE_QUEER),
		PRIDE(ItemModelType.FLAG_PRIDE_PRIDE, ItemModelType.BUNTING_PRIDE_PRIDE),
		;

		@Getter
		final ItemModelType flag;
		@Getter
		final ItemModelType bunting;

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
