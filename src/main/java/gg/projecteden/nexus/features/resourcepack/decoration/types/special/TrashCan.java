package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.TrashCommand.TrashMenu;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TrashCan extends DyeableFloorThing {

	public TrashCan(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		super(name, material, colorableType, hexOverride, hitbox);
	}

	static {
		Nexus.registerListener(new TrashCanListener());
	}

	private static class TrashCanListener implements Listener {

		@EventHandler
		public void on(DecorationInteractEvent event) {
			new TrashMenu(event.getPlayer());
		}
	}
}
