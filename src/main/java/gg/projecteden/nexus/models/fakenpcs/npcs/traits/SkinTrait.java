package gg.projecteden.nexus.models.fakenpcs.npcs.traits;

import gg.projecteden.nexus.features.fakenpc.DefaultTrait;
import gg.projecteden.nexus.models.fakenpcs.npcs.Trait;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@DefaultTrait
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SkinTrait extends Trait {
//	net.citizensnpcs.trait.SkinTrait

	@Override
	public List<String> getDebug() {
		return new ArrayList<>() {{
			add("todo=true");
		}};
	}
}
