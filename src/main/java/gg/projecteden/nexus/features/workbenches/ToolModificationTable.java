package gg.projecteden.nexus.features.workbenches;

import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.equipment.skins.BackpackSkin;
import gg.projecteden.nexus.features.equipment.skins.EquipmentSkinType;
import gg.projecteden.nexus.features.equipment.skins.ToolSkin;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.InventoryManager;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class ToolModificationTable extends CustomBench implements ICraftableCustomBench{

	private static final ItemBuilder WORKBENCH = new ItemBuilder(ItemModelType.TOOL_MODIFICATION_TABLE).name("Tool Modification Table");

	public static ItemBuilder getWorkbench() {
		return WORKBENCH.clone();
	}

	@Override
	public CustomBenchType getBenchType() {
		return CustomBenchType.TOOL_MODIFICATION_TABLE;
	}

	@Override
	public RecipeBuilder<?> getBenchRecipe() {
		return RecipeBuilder.shaped("111", "232", "242")
			.add('1', Material.BRICKS)
			.add('2', Material.STONE)
			.add('3', Material.SMITHING_TABLE)
			.add('4', MaterialTag.ANVIL)
			.toMake(getWorkbench().build());
	}

	public static void open(Player player) {
		new ToolModificationTableMenu().open(player);
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemBreak(PlayerItemBreakEvent event) {
		if (!EquipmentSkinType.isApplicable(event.getBrokenItem()))
			return;

		EquipmentSkinType type = EquipmentSkinType.of(event.getBrokenItem());
		if (type == null)
			return;

		PlayerUtils.giveItem(event.getPlayer(), type.getTemplate());
	}

	@Rows(3)
	private static class ToolModificationTableMenu extends InventoryProvider {

		private static final Map<Integer, TMTSlotType> SLOTS = new HashMap<>() {{
			put(10, TMTSlotType.TOOL);

			put(3, TMTSlotType.SKIN);
			put(12, TMTSlotType.PARTICLE);
			put(21, TMTSlotType.STAT_TRACK);

			put(15, TMTSlotType.RENDER);
		}};

		@Override
		public String getTitle() {
			return InventoryTexture.GUI_TOOL_MODIFICATION_TABLE.getMenuTexture() + "&0Tool Modification Table";
		}

		private ItemStack tool;

		@Override
		public void init() {
			contents.clear();
			for (int i = 0; i < 27; i++)
				if (!SLOTS.containsKey(i))
					contents.set(i, ClickableItem.empty(ItemUtils.getEmptySlotItem()));
				else {
					ClickableItem item = SLOTS.get(i).getItem(this, this.tool);
					if (item != null)
						contents.set(i, item);
				}
		}

		@Override
		public void onClose(InventoryManager manager) {
			super.onClose(manager);
			if (!isNullOrAir(tool)) {
				this.getViewer().give(this.tool);
				Tasks.wait(1, () -> this.getViewer().updateInventory());
			}
		}

		private enum TMTSlotType {
			TOOL {
				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					if (tool != null)
						return ClickableItem.of(tool, e -> {
							ItemStack cursor = e.getPlayer().getItemOnCursor();

							if (!isNullOrAir(cursor) && !EquipmentSkinType.isApplicable(cursor))
								return;

							e.getPlayer().setItemOnCursor(tool);
							inv.tool = isNullOrAir(cursor) ? null : cursor;
							inv.init();
						});

					return ClickableItem.of(
						new ItemBuilder(ItemModelType.GUI_TOOL_OUTLINES).hideTooltip().build(),
						e -> {
							ItemStack item = e.getPlayer().getItemOnCursor();
							if (isNullOrAir(item))
								return;

							if (EquipmentSkinType.isApplicable(item)) {
								e.getPlayer().setItemOnCursor(null);
								inv.tool = item;
								inv.init();
							}
						}
					);
				}
			},
			SKIN {
				private void onClick(ToolModificationTableMenu inv, ItemStack tool, ItemClickData e) {
					ItemStack item = e.getPlayer().getItemOnCursor();
					EquipmentSkinType skinType = EquipmentSkinType.of(item);

					ItemStack clicked = e.getItem();
					EquipmentSkinType clickedType = EquipmentSkinType.of(clicked);

					if (!EquipmentSkinType.isTemplate(item) && !isNullOrAir(item))
						return;

					if (skinType != null) {
						if (!skinType.applies(tool))
							return;

						e.getPlayer().setItemOnCursor(clickedType == null ? null : clickedType.getTemplate());
						inv.tool = skinType.apply(tool);
						inv.init();
					}
					else if (clickedType != null) {
						e.getPlayer().setItemOnCursor(clickedType.getTemplate());

						if (ToolSkin.DEFAULT.applies(tool))
							inv.tool = ToolSkin.DEFAULT.apply(tool);

						if (ArmorSkin.DEFAULT.applies(tool))
							inv.tool = ArmorSkin.DEFAULT.apply(tool);

						if (BackpackSkin.DEFAULT.applies(tool))
							inv.tool = BackpackSkin.DEFAULT.apply(tool);

						inv.init();
					}
				}

				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					if (tool == null)
						return ClickableItem.empty(ItemUtils.getEmptySlotItem());

					EquipmentSkinType type = EquipmentSkinType.of(tool);
					if (type != null)
						return ClickableItem.of(type.getTemplate(), e -> onClick(inv, tool, e));
					return ClickableItem.of(ItemUtils.getEmptySlotItem(), e -> onClick(inv, tool, e)); // TODO - correct item
				}
			},
			PARTICLE {
				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					return null;
				}
			},
			STAT_TRACK {
				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					return null;
				}
			},
			RENDER {
				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					EquipmentSkinType skinType = EquipmentSkinType.of(tool);
					if (skinType != null)
						return ClickableItem.empty(skinType.getBig(tool));

					else {
						if (ToolSkin.DEFAULT.applies(tool))
							return ClickableItem.empty(ToolSkin.DEFAULT.getBig(tool));

						if (ArmorSkin.DEFAULT.applies(tool))
							return ClickableItem.empty(ArmorSkin.DEFAULT.getBig(tool));

						if (BackpackSkin.DEFAULT.applies(tool))
							return ClickableItem.empty(BackpackSkin.DEFAULT.getBig(tool));
					}
					return ClickableItem.empty(ItemUtils.getEmptySlotItem());
				}
			};

			abstract ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool);
		}

	}

}
