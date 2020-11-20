package me.pugabyte.nexus.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.managers.ArenaManager;
import me.pugabyte.nexus.features.minigames.mechanics.UncivilEngineers;
import me.pugabyte.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.arenas.UncivilEngineersArena;
import me.pugabyte.nexus.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.nexus.features.minigames.Minigames.menus;

@CustomMechanicSettings(UncivilEngineers.class)
public class UncivilEngineersMenu extends MenuUtils implements InventoryProvider {

	UncivilEngineersArena arena;

	public UncivilEngineersMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, UncivilEngineersArena.class);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));

		contents.set(1, 4, ClickableItem.from(nameItem(new ItemStack(Material.ZOMBIE_SPAWN_EGG), "&eMob Points"), e -> openMobPointsMenu(player)));

		contents.set(1, 6, ClickableItem.from(nameItem(new ItemStack(Material.COMPASS), "&eOrigins"), e -> openOriginsMenu(player)));

		contents.set(1, 2, ClickableItem.from(nameItem(new ItemStack(Material.WOODEN_AXE), "&eSetup Region", "&7You must set all the origins first, and then run this after setting the first region."), e -> UncivilEngineers.setupArena(arena, player)));
	}

	private void openOriginsMenu(Player player) {
		SmartInventory INV = SmartInventory.builder()
				.provider(new UncivilEngineersOriginsMenu())
				.title("Mob Points")
				.size(6, 9)
				.build();
		INV.open(player);
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

	public void openMobPointsMenu(Player player) {
		SmartInventory INV = SmartInventory.builder()
				.provider(new UncivilEngineersMobPointsMenu())
				.title("Mob Points")
				.size(6, 9)
				.build();
		INV.open(player);
	}

	public class UncivilEngineersMobPointsMenu extends MenuUtils implements InventoryProvider {

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openCustomSettingsMenu(player, arena)));

			contents.set(0, 4, ClickableItem.from(nameItem(Material.EMERALD_BLOCK, "&aAdd Mob Point"), e -> {
				SmartInventory INV = SmartInventory.builder()
						.provider(new UncivilEngineersMobHeadMenu())
						.title("Mob Points")
						.size(6, 9)
						.build();
				INV.open(player);
			}));

			int row = 1;
			int column = 0;

			for (UncivilEngineers.MobPoint mobPoint : arena.getMobPoints()) {
				for (MobHead head : MobHead.values()) {
					if (mobPoint.getType() == head.getType()) {
						ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(head.getMHFName()).build();
						contents.set(row, column, ClickableItem.from(nameItem(skull, "&3" + head.getTitle(),
								getLocationLore(mobPoint.getLocation()) +
										"|| ||&7Click me to remove this Mob"),
								e -> {
									arena.getMobPoints().remove(mobPoint);
									arena.write();
									openMobPointsMenu(player);
								}));
					}
				}
				if (column != 8) {
					column++;
				} else {
					column = 0;
					row++;
				}
			}

		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {

		}
	}

	public enum MobHead {

		BLAZE(EntityType.BLAZE),
		CAVE_SPIDER(EntityType.CAVE_SPIDER),
		CHICKEN(EntityType.CHICKEN),
		COW(EntityType.COW),
		CREEPER(EntityType.CREEPER),
		GOLEM(EntityType.IRON_GOLEM),
		LAVA_SLIME(EntityType.MAGMA_CUBE),
		MUSHROOM_COW(EntityType.MUSHROOM_COW),
		OCELOT(EntityType.OCELOT),
		PIG(EntityType.PIG),
		PIG_ZOMBIE(EntityType.ZOMBIFIED_PIGLIN),
		SHEEP(EntityType.SHEEP),
		SKELETON(EntityType.SKELETON),
		SHULKER(EntityType.SHULKER),
		SLIME(EntityType.SLIME),
		SPIDER(EntityType.SPIDER),
		SQUID(EntityType.SQUID),
		VILLAGER(EntityType.VILLAGER),
		W_SKELETON(EntityType.WITHER_SKELETON),
		WITCH(EntityType.WITCH),
		ZOMBIE(EntityType.ZOMBIE);

		EntityType type;

		MobHead(EntityType type) {
			this.type = type;
		}

		public EntityType getType() {
			return type;
		}

		public String getMHFName() {
			return "MHF_" + name().toUpperCase().replace("_", "");
		}

		public String getTitle() {
			return StringUtils.camelCase(type.name().replace("_", " "));
		}

	}

	public class UncivilEngineersMobHeadMenu extends MenuUtils implements InventoryProvider {

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openCustomSettingsMenu(player, arena)));

			int row = 1;
			int column = 0;
			for (MobHead head : MobHead.values()) {
				ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD).name("&e" + head.getTitle()).skullOwner(head.getMHFName()).build();
				contents.set(row, column, ClickableItem.from(skull, e -> {
					arena.getMobPoints().add(new UncivilEngineers.MobPoint(player.getLocation(), head.getType()));
					arena.write();
					openMobPointsMenu(player);
				}));
				if (column != 8) {
					column++;
				} else {
					column = 0;
					row++;
				}
			}
		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {
		}
	}

	public class UncivilEngineersOriginsMenu extends MenuUtils implements InventoryProvider {

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openCustomSettingsMenu(player, arena)));

			ItemStack deleteItem = nameItem(Material.TNT, "&cDelete Item", "&7Click me to enter deletion mode.||&7Then, click a spawnpoint with me to||&7delete the spawnpoint.");
			contents.set(0, 8, ClickableItem.from(deleteItem, e -> Tasks.wait(2, () -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (ItemUtils.isNullOrAir(player.getItemOnCursor())) {
					player.setItemOnCursor(deleteItem);
				}
			})));

			contents.set(0, 4, ClickableItem.from(nameItem(Material.EMERALD_BLOCK, "&aSet New Origin"),
					e -> {
						Location loc = player.getLocation();
						AtomicInteger originID = new AtomicInteger(0);
						player.closeInventory();
						Nexus.getSignMenuFactory()
								.lines("", "^ ^ ^ ^ ^ ^", "UE Line", "Number")
								.prefix(MechanicType.UNCIVIL_ENGINEERS.get().getPrefix())
								.response(lines -> {
									try {
										originID.set(Integer.parseInt(lines[0]));
										if (originID.get() == 0) {
											Utils.send(player, "&cYou must use an integer greater than 0.");
											player.closeInventory();
										}
										arena.getOrigins().put(originID.get(), loc.getBlock().getLocation());
										arena.write();
										openOriginsMenu(player);
									} catch (Exception ignore) {
										Utils.send(player, "&cYou must use an integer greater than 0.");
										player.closeInventory();
									}
								})
								.open(player);
					}));

			int row = 1;
			int column = 0;

			List<Integer> sorted = new ArrayList<>(arena.getOrigins().keySet());
			sorted.sort(Comparator.naturalOrder());
			for (int origin : sorted) {
				contents.set(row, column, ClickableItem.from(nameItem(Material.COMPASS,
						"&eOrigin: " + origin, getLocationLore(arena.getOrigins().get(origin)) +
								"|| ||&7Click to teleport."),
						e -> {
							if (player.getItemOnCursor().getType().equals(Material.TNT)) {
								player.setItemOnCursor(new ItemStack(Material.AIR));
								String originID = StringUtils.right(e.getItem().getItemMeta().getDisplayName(), 1);
								arena.getOrigins().remove(Integer.parseInt(originID));
								openOriginsMenu(player);
								return;
							}
							player.teleport(arena.getOrigins().get(origin));
						}));
				if (column != 8) {
					column++;
				} else {
					column = 0;
					row++;
				}
			}
		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {
		}
	}


}
