package gg.projecteden.nexus.models.fakenpcs.npcs.traits;

import gg.projecteden.nexus.models.fakenpcs.npcs.Trait;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LookCloseTrait extends Trait {
	//	net.citizensnpcs.trait.LookClose
	protected int radius = 10;
	protected LookCloseType lookCloseType = LookCloseType.CLIENTSIDE;

	public enum LookCloseType {
		CLIENTSIDE,
		GLOBAL,
	}

	@Override
	public List<String> getDebug() {
		return new ArrayList<>() {{
			add("radius=" + radius);
			add("type=" + lookCloseType);
		}};
	}
}
