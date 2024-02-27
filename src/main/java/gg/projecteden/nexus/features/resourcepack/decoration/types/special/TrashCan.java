package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.TrashCommand.TrashMenu;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Sound;
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
			if (event.isCancelled())
				return;

			if (!(event.getDecoration().getConfig() instanceof TrashCan))
				return;

			new SoundBuilder(Sound.BLOCK_IRON_TRAPDOOR_OPEN).volume(0.5).receiver(event.getPlayer()).play();
			new TrashMenu(event.getPlayer(), player ->
					new SoundBuilder(Sound.BLOCK_IRON_TRAPDOOR_CLOSE).volume(0.5).receiver(player).play());
		}
	}
}
