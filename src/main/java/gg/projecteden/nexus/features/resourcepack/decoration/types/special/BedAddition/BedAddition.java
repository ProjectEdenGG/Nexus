package gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Addition;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.BedAdditionUtils.BedInteractionData;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.ArrayList;
import java.util.List;

@Addition
@MultiBlock
public class BedAddition extends DyeableFloorThing {
	@Getter
	private boolean wide;
	@Getter
	private AdditionType additionType;

	public BedAddition(String name, CustomMaterial material, AdditionType additionType, ColorableType colorableType) {
		this(name, material, additionType, false, colorableType);
	}

	public BedAddition(String name, CustomMaterial material, AdditionType additionType, boolean wide, ColorableType colorableType) {
		super(name, material, colorableType);

		this.rotatable = false;
		this.rotationSnap = RotationSnap.DEGREE_90;

		this.wide = wide;
		this.additionType = additionType;

		this.lore = new ArrayList<>(List.of("&3Can only be placed on a bed", decorLore));
	}

	@Override
	public boolean isMultiBlock() {
		return true;
	}

	@AllArgsConstructor
	public enum AdditionType {
		FRAME(0),
		CANOPY(1),
		;

		@Getter
		private final int modY;
	}


	static {
		Nexus.registerListener(new BedAdditionListener());
	}

	public static class BedAdditionListener implements Listener {

		@EventHandler
		public void on(BlockBreakEvent event) {
			Player player = event.getPlayer();
			Block block = event.getBlock();

			if (MaterialTag.BEDS.isNotTagged(block))
				return;

			DecorationUtils.debug(player, "BedAddition - BedBreakEvent");

			BedInteractionData data = new BedInteractionData.BedInteractionDataBuilder()
					.player(player)
					.origin(block)
					.adjustBeds(false)
					.build();

			var additions = data.getAdditionsRight();
			if (Nullables.isNullOrEmpty(additions)) {
				DecorationUtils.debug(player, "No bed additions to destroy");
				return;
			}

			for (ItemFrame itemFrame : additions.keySet()) {
				DecorationConfig config = additions.get(itemFrame);
				if (config == null)
					continue;

				Decoration decoration = new Decoration(config, itemFrame);
				boolean destroyed = decoration.destroy(player, BlockFace.UP, player);
				if (!destroyed) {
					DecorationUtils.debug(player, "not destroyed");
					event.setCancelled(true);
					return;
				}
			}
		}

		@EventHandler
		public void on(PlayerBedEnterEvent event) {
			Player player = event.getPlayer();
			Block block = event.getBed();
			Material material = block.getType();

			if (MaterialTag.BEDS.isNotTagged(material))
				return;

			DecorationUtils.debug(player, "BedAddition - BedEnterEvent");

			BedInteractionData data = new BedInteractionData.BedInteractionDataBuilder()
					.player(player)
					.origin(block)
					.tool(ItemUtils.getTool(player))
					.adjustBeds(true)
					.build();

			if (data.isToolUnrelated()) {
				DecorationUtils.debug(player, "tool is unrelated");
				return;
			}

			// TODO: Check to see if works with canopy

			DecorationUtils.debug(player, "try paint");
			if (data.tryPaint(event)) {
				// TODO: PLAY SOUND
				return;
			}

			DecorationUtils.debug(player, "try swap");
			if (data.trySwap(event)) {
				// TODO: PLAY SOUND
				return;
			}

			DecorationUtils.debug(player, "try place");
			if (data.tryPlace(event))
				return;

			DecorationUtils.debug(player, "unknown interaction");
		}
	}
}