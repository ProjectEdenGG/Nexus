package gg.projecteden.nexus.features.workbenches;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.equipment.skins.BackpackSkin;
import gg.projecteden.nexus.features.equipment.skins.EquipmentSkinType;
import gg.projecteden.nexus.features.equipment.skins.ToolSkin;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrack;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
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
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
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
	private static class ToolModificationTableMenu extends InventoryProvider implements TemporaryListener {

		@Override
		public String getTitle() {
			return InventoryTexture.GUI_TOOL_MODIFICATION_TABLE.getMenuTexture() + "&0Tool Modification Table";
		}

		private ItemStack tool;

		@Override
		public void open(Player viewer) {
			super.open(viewer);
			Nexus.registerTemporaryListener(this);
		}

		@Override
		public void init() {
			contents.clear();

			Map<Integer, TMTSlotType> SLOTS = new HashMap<>() {{
				put(10, TMTSlotType.TOOL);

				if (tool != null) {
					if (EquipmentSkinType.isApplicable(tool)) {
						put(3, TMTSlotType.SKIN);
						put(12, TMTSlotType.STAT_TRACK);
						put(21, TMTSlotType.PARTICLE);
					}
					else {
						put(3, TMTSlotType.STAT_TRACK);
						put(12, TMTSlotType.PARTICLE);
					}
				}

				put(15, TMTSlotType.RENDER);

				put(8, TMTSlotType.STAT_TRACK_CONFIG);
			}};

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
			Nexus.unregisterTemporaryListener(this);
		}

		@Override
		public Player getPlayer() {
			return getViewer();
		}

		@EventHandler
		public void onShiftClick(InventoryClickEvent event) {
			if (!event.isShiftClick()) return;
			if (!event.getWhoClicked().equals(getViewer())) return;
			if (event.getClickedInventory() == null) return;
			if (event.getClickedInventory().equals(getBukkitInventory())) return;

			ItemStack item = event.getClickedInventory().getItem(event.getSlot());
			if (isNullOrAir(item)) return;

			if (EquipmentSkinType.isApplicable(item) || StatTrack.isApplicableItem(item)) {
				tool = item.clone();
				item.subtract();
				init();
				getViewer().updateInventory();
				return;
			}

			if (this.tool != null && EquipmentSkinType.isTemplate(item) && EquipmentSkinType.isApplicable(tool)) {
				if (EquipmentSkinType.of(tool) != null) return;

				EquipmentSkinType type = EquipmentSkinType.of(item);
				if (type == null) return;
				tool = type.apply(tool);
				item.subtract();
				init();
				getViewer().updateInventory();
			}

			if (this.tool != null && StatTrack.isTemplate(item)) {
				if (StatTrack.isEnabledOn(tool)) return;
				if (!StatTrack.isApplicableItem(tool)) return;

				tool = StatTrack.enableFor(tool);
				item.subtract();
				init();
				getViewer().updateInventory();
			}
		}

		private enum TMTSlotType {
			TOOL {
				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					if (tool != null)
						return ClickableItem.of(tool, e -> {
							ItemStack cursor = e.getPlayer().getItemOnCursor();

							if (!isNullOrAir(cursor) && !EquipmentSkinType.isApplicable(cursor) && !StatTrack.isApplicableItem(cursor))
								return;

							if (e.isShiftClick() && PlayerUtils.hasRoomFor(e.getPlayer(), tool)) {
								((InventoryClickEvent) e.getEvent()).setCancelled(false);
								Tasks.wait(1, () -> {
									inv.tool = null;
									inv.init();
									e.getPlayer().updateInventory();
								});
								return;
							}

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

							if (EquipmentSkinType.isApplicable(item) || StatTrack.isApplicableItem(item)) {
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
					Player player = e.getPlayer();
					ItemStack cursor = player.getItemOnCursor();
					EquipmentSkinType cursorType = EquipmentSkinType.of(cursor);

					ItemStack clicked = e.getItem();
					EquipmentSkinType clickedType = EquipmentSkinType.of(clicked);

					if (e.isShiftClick() && clickedType != null) {
						((InventoryClickEvent) e.getEvent()).setCancelled(false);
						Tasks.wait(1, () -> {
							reset(inv, tool);
							inv.init();
							player.updateInventory();
						});
						return;
					}

					if (!EquipmentSkinType.isTemplate(cursor) && !isNullOrAir(cursor))
						return;

					if (cursorType != null) {
						if (!cursorType.applies(tool))
							return;

						if (isNullOrAir(cursor)) {
							player.setItemOnCursor(clickedType == null ? null : clickedType.getTemplate());
						} else {
							if (clickedType != null && clickedType != cursorType) {
								if (cursor.getAmount() == 1 && clicked.getAmount() == 1) {
									player.setItemOnCursor(clicked);
								} else {
									return;
								}
							} else {
								if (clickedType == cursorType) {
									player.getItemOnCursor().add();
									reset(inv, tool);
									inv.init();
									return;
								} else {
									player.getItemOnCursor().subtract();
								}
							}
						}
						inv.tool = cursorType.apply(tool);
						inv.init();
					} else if (clickedType != null) {
						player.setItemOnCursor(clickedType.getTemplate());
						reset(inv, tool);
						inv.init();
					}
				}

				private void reset(ToolModificationTableMenu inv, ItemStack tool) {
					if (ToolSkin.DEFAULT.applies(tool))
						inv.tool = ToolSkin.DEFAULT.apply(tool);

					if (ArmorSkin.DEFAULT.applies(tool))
						inv.tool = ArmorSkin.DEFAULT.apply(tool);

					if (BackpackSkin.DEFAULT.applies(tool))
						inv.tool = BackpackSkin.DEFAULT.apply(tool);
				}

				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					if (tool == null)
						return ClickableItem.empty(ItemUtils.getEmptySlotItem());

					EquipmentSkinType type = EquipmentSkinType.of(tool);
					if (type != null)
						return ClickableItem.of(type.getTemplate(), e -> onClick(inv, tool, e));
					return ClickableItem.of(new ItemBuilder(ItemModelType.GUI_TOOL_SKIN_OUTLINE).hideTooltip().build(), e -> onClick(inv, tool, e));
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
					if (tool == null)
						return ClickableItem.empty(ItemUtils.getEmptySlotItem());

					if (StatTrack.isEnabledOn(tool))
						return ClickableItem.empty(StatTrack.getTemplate());
					return ClickableItem.of(new ItemBuilder(ItemModelType.GUI_TOOL_STATTRACK_OUTLINE).hideTooltip().build(), e -> onClick(inv, tool, e));
				}

				private void onClick(ToolModificationTableMenu inv, ItemStack tool, ItemClickData e) {
					Player player = e.getPlayer();
					ItemStack cursor = player.getItemOnCursor();

					if (!StatTrack.isTemplate(cursor))
						return;

					player.getItemOnCursor().subtract();
					inv.tool = StatTrack.enableFor(tool);
					inv.init();
				}
			},
			STAT_TRACK_CONFIG {
				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					if (!StatTrack.isEnabledOn(tool))
						return ClickableItem.empty(ItemUtils.getEmptySlotItem());

					ItemBuilder builder = new ItemBuilder(Material.PAPER)
						.name("&eStatTrack Config")
						.model(ItemModelType.GUI_GEAR)
						.lore("&eLeft Click &3to change")
						.lore("&3displayed statistic")
						.lore("&3 ");

					List<StatTrackStatistic> validStats = StatTrack.getValidStatsFor(tool);

					int currentStatIndex = 0;
					for (int i = 0; i < validStats.size(); i++) {
						StatTrackStatistic stat = validStats.get(i);
						if (StatTrack.getDisplayedStat(tool).equals(stat)) {
							builder.lore("&e- " + stat.getDisplayName());
							currentStatIndex = i;
						}
						else
							builder.lore("&3- " + stat.getDisplayName());
					}

					builder.lore("&3 ");
					builder.lore("&eShift-Right &3Click to Eject");
					builder.lore("&3StatTrack from this tool");
					builder.lore("&4&oThis will permanently delete all");
					builder.lore("&4&otracked statistics for this tool");

					int nextStatIndex = currentStatIndex == (validStats.size() - 1) ? 0 : currentStatIndex + 1;
					return ClickableItem.of(builder.build(), e -> {
						if (e.isAnyLeftClick()) {
							inv.tool = StatTrack.setDisplayedStat(tool, validStats.get(nextStatIndex));
							inv.init();
						}
						else if (e.isShiftRightClick()) {
							PlayerUtils.giveItemAndMailExcess(e.getPlayer(), StatTrack.getTemplate(), WorldGroup.of(e.getPlayer()));
							inv.tool = StatTrack.disableFor(tool);
							inv.init();
						}
					});
				}
			},
			RENDER {
				@Override
				ClickableItem getItem(ToolModificationTableMenu inv, ItemStack tool) {
					if (tool == null)
						return ClickableItem.empty(ItemUtils.getEmptySlotItem());

					EquipmentSkinType skinType = EquipmentSkinType.of(tool);
					if (skinType != null)
						return ClickableItem.empty(skinType.getBig(tool));

					else {
						if (ToolSkin.DEFAULT.applies(tool) || tool.getType() == Material.ELYTRA)
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
