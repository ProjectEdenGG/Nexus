package gg.projecteden.nexus.models.fakenpcs.npcs;

import gg.projecteden.nexus.features.fakenpc.DefaultTrait;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public abstract class Trait {
	private boolean enabled = false;
	protected UUID npcUUID;

	public void linkTo(FakeNPC npc) {
		if (this.npcUUID != null)
			throw new InvalidInputException("FakeNPC may only be set once!");

		this.npcUUID = npc.getUuid();
		onAttach();
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
		if (enable)
			onEnable();
		else
			onDisable();
	}

	public boolean isDefault() {
		return getClass().getAnnotation(DefaultTrait.class) != null;
	}

	public FakeNPCTraitType getType() {
		return FakeNPCTraitType.of(getClass());
	}

	public void onAttach() {}

	public void onEnable() {}

	public void onDisable() {}

	public void onSpawn() {}

	public void onDespawn() {}

	public void onDelete() {}

	public List<String> getDebug() {
		return new ArrayList<>();
	}

	public String toDebug(boolean expand) {
		List<String> debug = new ArrayList<>() {{
			add("enabled=" + enabled);
			add("default=" + isDefault());
		}};

		debug.addAll(getDebug());

		if (expand) {
			String space = "\n  &e  &e";
			List<String> expanded = new ArrayList<>();
			for (String line : debug) {
				expanded.add(" &3- &e" + line.replace("=", " = "));
			}

			return space + String.join(space, expanded);
		}

		return debug.toString();
	}

	public void update(UpdateType type) {
		switch (type) {
			case ATTACH -> onAttach();
			case ENABLE -> onEnable();
			case DISABLE -> onDisable();
			case SPAWN -> onSpawn();
			case DESPAWN -> onDespawn();
			case DELETE -> onDelete();
		}
	}

	public enum UpdateType {
		ATTACH,
		ENABLE,
		DISABLE,
		SPAWN,
		DESPAWN,
		DELETE,
		;
	}

}
