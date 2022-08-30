package gg.projecteden.nexus.models.fakenpcs.npcs.traits;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MirrorTrait extends Trait {
	protected boolean skin = false;
	protected boolean equipment = false;
	protected boolean name = false;

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


}
