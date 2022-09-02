package gg.projecteden.nexus.models.fakenpcs.npcs.traits;

import gg.projecteden.nexus.features.fakenpc.FakeNPCUtils.SkinProperties;
import gg.projecteden.nexus.models.fakenpcs.npcs.Trait;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlternativeSkinsTrait extends Trait {
	boolean global;
	Map<String, SkinProperties> altSkins = new HashMap<>();


	@Override
	public List<String> getDebug() {
		return new ArrayList<>() {{
			add("global=" + global);
			add("skins=" + altSkins.keySet());
		}};
	}


}
