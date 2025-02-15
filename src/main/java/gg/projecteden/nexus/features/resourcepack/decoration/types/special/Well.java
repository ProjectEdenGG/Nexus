package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Well extends DyeableFloorThing {

	public Well(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, CustomHitbox hitbox) {
		super(multiblock, name, itemModelType, colorableType, hitbox);
	}

	static {
		Nexus.registerListener(new WellListener());
	}

	public static class WellListener implements Listener {

		@EventHandler
		public void on(DecorationInteractEvent event) {
			if (event.isCancelled())
				return;

			if (!event.getInteractType().equals(InteractType.RIGHT_CLICK))
				return;

			if (!(event.getDecoration().getConfig() instanceof Well))
				return;

			Player player = event.getPlayer();
			ItemStack tool = ItemUtils.getTool(player);
			if (Nullables.isNullOrAir(tool) || tool.getType() != Material.BUCKET)
				return;

			event.setCancelled(true);

			DecorationLang.debug(player, "filling empty container");

			ItemUtils.subtract(player, tool);
			new SoundBuilder(Sound.ITEM_BUCKET_FILL).location(event.getClickedBlock()).play();

			Tasks.wait(2, () -> PlayerUtils.giveItem(player, new ItemStack(Material.WATER_BUCKET)));
		}
	}
}
