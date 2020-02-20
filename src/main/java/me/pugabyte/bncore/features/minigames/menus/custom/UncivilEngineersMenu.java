package me.pugabyte.bncore.features.minigames.menus.custom;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.menus.SignMenuFactory;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.mechanics.UncivilEngineers;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.UncivilEngineersArena;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

@CustomMechanicSettings(UncivilEngineers.class)
public class UncivilEngineersMenu extends MenuUtils implements InventoryProvider {

	UncivilEngineersArena arena;

	public UncivilEngineersMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, UncivilEngineersArena.class);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));

		contents.set(1, 3, ClickableItem.from(nameItem(Material.MONSTER_EGG, "Mob Points"), e -> openMobPointsMenu(player)));

		contents.set(1, 5, ClickableItem.from(nameItem(Material.COMPASS, "Origins"), e -> openOriginsMenu(player)));
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
			contents.set(0, 0, ClickableItem.from(backItem(), e -> openMobPointsMenu(player)));

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
						ItemStack skull = new ItemStack(Material.SKULL, 1, (byte) 3);
						SkullMeta meta = (SkullMeta) skull.getItemMeta();
						meta.setOwner(head.getMHFName());
						skull.setItemMeta(meta);
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
		PIG_ZOMBIE(EntityType.PIG_ZOMBIE),
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
			return Utils.camelCase(type.name().replace("_", " "));
		}

	}

	public class UncivilEngineersMobHeadMenu extends MenuUtils implements InventoryProvider {

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openCustomSettingsMenu(player, arena)));

			int row = 1;
			int column = 0;
			for (MobHead head : MobHead.values()) {
				ItemStack skull = new ItemStack(Material.SKULL, 1, (byte) 3);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				meta.setOwner(head.getMHFName());
				skull.setItemMeta(meta);
				contents.set(row, column, ClickableItem.from(nameItem(skull, "&e" + head.getTitle()), e -> {
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

			contents.set(0, 4, ClickableItem.from(nameItem(Material.EMERALD_BLOCK, "&aSet New Origin"),
					e -> {
						WorldEditUtils worldEditUtils = new WorldEditUtils(player.getWorld());
						LocalSession localSession = worldEditUtils.getPlayer(player).getSession();
						try {
							Location loc = worldEditUtils.toLocation(localSession.getSelection(new BukkitWorld(player.getWorld())).getMinimumPoint());
							AtomicInteger originID = new AtomicInteger(0);
							new SignMenuFactory(BNCore.getInstance())
									.lines("", "^^^^^^^^", "UE Line", "Number")
									.response((player1, response) -> {
										try {
											originID.set(Integer.parseInt(response[0]));
										} catch (Exception ignore) {
											player.sendMessage("&cYou must use an integer greater than 0.");
											player.closeInventory();
										}
									})
									.open(player);
							if (originID.get() == 0) {
								player.sendMessage("&cYou must use an integer greater than 0.");
								player.closeInventory();
							}
							arena.getOrigins().put(originID.get(), player.getLocation());
							arena.write();
							openOriginsMenu(player);
						} catch (IncompleteRegionException ex) {
							player.closeInventory();
							player.sendMessage("&cYour World Edit region is not set. " +
									"Please set your region to the lowest block in one corner of the map.");
						}
					}));

			int row = 1;
			int column = 0;

			List<Integer> sorted = new ArrayList<>(arena.getOrigins().keySet());
			sorted.sort(Comparator.naturalOrder());
			for (int origin : sorted) {
				contents.set(row, column, ClickableItem.from(nameItem(Material.COMPASS,
						"&eOrigin: " + origin, getLocationLore(arena.getOrigins().get(origin)) +
								"|| ||&7Click to teleport."),
						e -> player.teleport(arena.getOrigins().get(origin))));
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
