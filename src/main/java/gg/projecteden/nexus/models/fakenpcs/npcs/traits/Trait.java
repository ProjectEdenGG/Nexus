package gg.projecteden.nexus.models.fakenpcs.npcs.traits;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Trait {
	private boolean enabled = false;
	protected FakeNPC npc;
	private boolean defaultTrait = false;

	public void linkTo(FakeNPC fakeNPC) {
		if (this.npc != null)
			throw new InvalidInputException("FakeNPC may only be set once!");

		this.npc = fakeNPC;
		onAttach();
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
		if (enable)
			onEnable();
		else
			onDisable();
	}

	public void onAttach() {}

	public void onEnable() {}

	public void onDisable() {}

	public void onPreSpawn() {}

	public void onSpawn() {}

	public void onDespawn() {}

	public void onDelete() {}

}
