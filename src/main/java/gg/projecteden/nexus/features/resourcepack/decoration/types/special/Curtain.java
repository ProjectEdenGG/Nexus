package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxWall;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

@MultiBlock
public class Curtain extends DyeableWallThing {
	@Getter
	private final CurtainType curtainType;

	public Curtain(CurtainType curtainType) {
		super(curtainType.getName(), curtainType.getMaterial(), ColorableType.DYE, curtainType.getHitbox());
		this.curtainType = curtainType;
		this.rotatable = false;
	}

	@AllArgsConstructor
	public enum CurtainType {
		_1x2_OPEN(true, "Window Curtains 1x2", CustomMaterial.CURTAINS_1x2_OPEN, CustomMaterial.CURTAINS_1x2_CLOSED, HitboxWall._1x2V_LIGHT),
		_2x2_OPEN(true, "Window Curtains 2x2", CustomMaterial.CURTAINS_2x2_OPEN, CustomMaterial.CURTAINS_2x2_CLOSED, HitboxWall._2x2_LIGHT),
		_2x3H_OPEN(true, "Window Curtains 2x3H", CustomMaterial.CURTAINS_2x3H_OPEN, CustomMaterial.CURTAINS_2x3H_CLOSED, HitboxWall._2x3H_LIGHT),
		_2x3V_OPEN(true, "Window Curtains 2x3V", CustomMaterial.CURTAINS_2x3V_OPEN, CustomMaterial.CURTAINS_2x3V_CLOSED, HitboxWall._2x3V_LIGHT),
		_3x3_OPEN(true, "Window Curtains 3x3", CustomMaterial.CURTAINS_3x3_OPEN, CustomMaterial.CURTAINS_3x3_CLOSED, HitboxWall._3x3_LIGHT),

		// Internal Only
		_1x2_CLOSED(false, "Window Curtains 1x2", CustomMaterial.CURTAINS_1x2_CLOSED, CustomMaterial.CURTAINS_1x2_OPEN, HitboxWall._1x2V_LIGHT),
		_2x2_CLOSED(false, "Window Curtains 2x2", CustomMaterial.CURTAINS_2x2_CLOSED, CustomMaterial.CURTAINS_2x2_OPEN, HitboxWall._2x2_LIGHT),
		_2x3H_CLOSED(false, "Window Curtains 2x3H", CustomMaterial.CURTAINS_2x3H_CLOSED, CustomMaterial.CURTAINS_2x3H_OPEN, HitboxWall._2x3H_LIGHT),
		_2x3V_CLOSED(false, "Window Curtains 2x3V", CustomMaterial.CURTAINS_2x3V_CLOSED, CustomMaterial.CURTAINS_2x3V_OPEN, HitboxWall._2x3V_LIGHT),
		_3x3_CLOSED(false, "Window Curtains 3x3", CustomMaterial.CURTAINS_3x3_CLOSED, CustomMaterial.CURTAINS_3x3_OPEN, HitboxWall._3x3_LIGHT),
		;

		@Getter
		private final boolean opened;
		@Getter
		private final String name;
		@Getter
		private final CustomMaterial material;
		@Getter
		private final CustomMaterial oppositeMaterial;
		@Getter
		private final CustomHitbox hitbox;

	}

	static {
		Nexus.registerListener(new CurtainListener());
	}

	private static class CurtainListener implements Listener {

		@EventHandler
		public void on(DecorationInteractEvent event) {
			if (event.isCancelled())
				return;

			Decoration decoration = event.getDecoration();
			DecorationConfig config = decoration.getConfig();
			if (!(config instanceof Curtain curtain))
				return;

			Player player = event.getPlayer();
			if (!Nullables.isNullOrAir(ItemUtils.getTool(player)))
				return;

			CurtainType type = curtain.getCurtainType();
			ItemBuilder itemBuilder = new ItemBuilder(event.getDecoration().getItem(player));
			itemBuilder.material(type.getOppositeMaterial());
			itemBuilder.resetName();

			decoration.getItemFrame().setItem(itemBuilder.build(), false);
			// TODO: SOUND
		}

		@EventHandler
		public void on(ItemSpawnEvent event) {
			final ItemStack item = event.getEntity().getItemStack();
			if (!(DecorationConfig.of(item) instanceof Curtain curtain))
				return;

			CustomMaterial openedMaterial = curtain.getCurtainType().getMaterial();
			if (!curtain.getCurtainType().isOpened())
				openedMaterial = curtain.getCurtainType().getOppositeMaterial();

			ItemStack converted = new ItemBuilder(item)
					.modelId(openedMaterial.getModelId())
					.build();

			event.getEntity().setItemStack(converted);
		}
	}

}
