package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.TrashCommand.TrashMenu;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TrashCan extends DyeableFloorThing implements Interactable {

	public TrashCan(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		super(false, name, material, colorableType, hexOverride, hitbox);
	}

	static {
		Nexus.registerListener(new TrashCanListener());
	}

	private static class TrashCanListener implements Listener {

		@EventHandler
		public void on(DecorationInteractEvent event) {
			if (event.isCancelled())
				return;

			if (event.getInteractType() != InteractType.RIGHT_CLICK)
				return;

			if (!(event.getDecoration().getConfig() instanceof TrashCan))
				return;

			if (DyeStation.isPaintbrush(ItemUtils.getTool(event.getPlayer())))
				return;

			event.setCancelled(true);
			DecorationUtils.getSoundBuilder(CustomSound.DECOR_TRASH_CAN_OPEN).volume(0.25).receiver(event.getPlayer()).play();
			new TrashMenu(event.getPlayer(), player ->
					DecorationUtils.getSoundBuilder(CustomSound.DECOR_TRASH_CAN_CLOSE).volume(0.25).receiver(event.getPlayer()).play()
			);
		}
	}
}
