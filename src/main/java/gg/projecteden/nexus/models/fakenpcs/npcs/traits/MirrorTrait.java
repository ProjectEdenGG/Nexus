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
public class MirrorTrait extends Trait {
	protected boolean skin = false;
	protected boolean name = false;
	protected boolean equipment = false;

	@Override
	public void onAttach() {
		if (isEnabled())
			mirrorOn();
	}

	@Override
	public void onSpawn() {
		if (isEnabled())
			mirrorOn();
	}

	@Override
	public void onDelete() {
		if (isEnabled())
			mirrorOff();
	}

	@Override
	public void onEnable() {
		mirrorOn();
	}

	@Override
	public void onDisable() {
		mirrorOff();
	}

	public void mirrorOn() {
		// TODO
	}

	public void mirrorOff() {
		// TODO
	}

	@Override
	public List<String> getDebug() {
		return new ArrayList<>() {{
			add("skin=" + skin);
			add("name=" + name);
			add("equipment=" + equipment);
		}};
	}
}
