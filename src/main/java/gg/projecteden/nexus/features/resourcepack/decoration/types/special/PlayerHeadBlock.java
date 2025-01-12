package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSpawnEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerHeadBlock extends FloorThing {

	public PlayerHeadBlock(boolean multiblock, String name, CustomMaterial material, CustomHitbox hitbox) {
		super(multiblock, name, material, hitbox);
	}

	static {
		Nexus.registerListener(new PlayerHeadListener());
	}

	public static class PlayerHeadListener implements Listener {

		@EventHandler
		public void on(DecorationSpawnEvent event) {
			if (event.isCancelled())
				return;

			if (!(event.getDecoration().getConfig() instanceof PlayerHeadBlock))
				return;

			ItemBuilder skullBuilder = new ItemBuilder(event.getItemStack()).skullOwner(event.getPlayer().getUniqueId());
			event.setItemStack(skullBuilder.build());
		}
	}

}
