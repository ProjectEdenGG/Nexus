package me.pugabyte.bncore.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.menus.teams.TeamMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class ArenaMenu extends MenuUtils implements InventoryProvider {

	Arena arena;
	public ArenaMenu(Arena arena) {
		this.arena = arena;
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Utils.wait(1, () -> menus.openArenaMenu(player, arena)));
	}

	private enum ArenaMenuItem {
		DELETE_ARENA(1, 9, Material.TNT) {
			@Override
			void onClick(Player player, Arena arena) {
				menus.openDeleteMenu(player, arena);
			}

			@Override
			public String getTitle() {
				return "&c&lDelete Arena";
			}

			@Override
			String getLore(Player player, Arena arena) {
				return "&7You will need to confirm||&7deleting an arena.|| ||&7&lTHIS CANNOT BE UNDONE.";
			}
		},
		NAME(1, 1, Material.NAME_TAG),
		DISPLAY_NAME(1, 2, Material.PAPER),
		MECHANIC_TYPE(1, 5, Material.REDSTONE) {
			@Override
			void onClick(Player player, Arena arena) {
				menus.openMechanicsMenu(player, arena);
			}

			@Override
			String getter(Player player, Arena arena) {
				if (arena.getMechanic() != null)
					return arena.getMechanic().getName();
				return "";
			}
		},
		CUSTOM_MECHANIC_SETTINGS(1, 6, Material.BOOK_AND_QUILL) {
			@Override
			void onClick(Player player, Arena arena) {
				menus.openCustomSettingsMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				return null;
			}
		},
		TEAMS(2, 1, Material.WOOL) {
			@Override
			void onClick(Player player, Arena arena) {
				new TeamMenus().openTeamsMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				return "||&eCurrent Teams: " + arena.getTeams().stream().map(Team::getColoredName).collect(Collectors.joining("&f, "));
			}
		},
		LOBBY(2, 2, Material.WOOD_DOOR) {
			@Override
			void onClick(Player player, Arena arena) {
				menus.openLobbyMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				return null;
			}
		},
		FLAGS(2, 3, new ItemStack(Material.BANNER, 1, ColorType.CYAN.getDyeColor().getDyeData())) {
			@Override
			void onClick(Player player, Arena arena) {

			}

			@Override
			String getLore(Player player, Arena arena) {
				return null;
			}
		},
		LIVES(3, 1, new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal())),
		SECONDS(3, 2, Material.WATCH) {
			@Override
			public String getTitle() {
				return "Game Time (Seconds)";
			}
		},
		WINNING_SCORE(3, 3, Material.GOLD_INGOT),
		MIN_WINNING_SCORE(3, 4, Material.CLAY_BRICK),
		MAX_WINNING_SCORE(3, 5, Material.IRON_INGOT),
		MIN_PLAYERS(4, 1, Material.LEATHER_CHESTPLATE),
		MAX_PLAYERS(4, 2, Material.DIAMOND_CHESTPLATE),
		SPECTATE_LOCATION(5, 1, Material.COMPASS) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.setSpectateLocation(player.getLocation());
				arena.write();
			}
		},
		RESPAWN_LOCATION(5, 2, Material.BED) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.setRespawnLocation(player.getLocation());
				arena.write();
			}
		},
		RESPAWN_SECONDS(5, 3, Material.TOTEM),
		SAVE(5, 9, Material.END_CRYSTAL) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.write();
			}

			@Override
			String getLore(Player player, Arena arena) {
				return null;
			}
		};

		@Getter
		private int row;
		@Getter
		private int column;
		@Getter
		private ItemStack item;

		ArenaMenuItem(int row, int column, Material material) {
			this(row, column, new ItemStack(material));
		}

		ArenaMenuItem(int row, int column, ItemStack item) {
			this.row = row;
			this.column = column;
			this.item = item;
		}

		public String getTitle() {
			return Utils.camelCase(name());
		}

		String getLore(Player player, Arena arena) {
			return "||&eCurrent value: &3" + getter(player, arena);
		}

		void onClick(Player player, Arena arena) {
			openAnvilMenu(player, arena, getter(player, arena), (p, text) -> {
				setter(player, arena, text);
				return AnvilGUI.Response.close();
			});
		}

		private PropertyDescriptor getPropertyDescriptor() throws IntrospectionException {
			return new PropertyDescriptor(Character.toLowerCase(name().charAt(0)) + Utils.camelCase(name()).substring(1).replaceAll(" ", ""), Arena.class);
		}

		String getter(Player player, Arena arena) {
			try {
				PropertyDescriptor propertyDescriptor = getPropertyDescriptor();
				return String.valueOf(propertyDescriptor.getReadMethod().invoke(arena));
			} catch (Exception ignore) {}
			return "";
		}

		void setter(Player player, Arena arena, String text) {
			try {
				PropertyDescriptor propertyDescriptor = getPropertyDescriptor();
				Object value = text;
				if (propertyDescriptor.getPropertyType() == Integer.TYPE)
					value = Integer.valueOf(text);
				propertyDescriptor.getWriteMethod().invoke(arena, value);
			} catch (Exception ignore) {}

			arena.write();
		}
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Arrays.asList(ArenaMenuItem.values()).forEach(menuItem ->
			contents.set(
					(menuItem.getRow() - 1),
					(menuItem.getColumn() - 1),
					ClickableItem.from(
							nameItem(menuItem.getItem(), "&e" + menuItem.getTitle(), menuItem.getLore(player, arena)),
							e -> menuItem.onClick(player, arena)
					)
			)
		);
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
