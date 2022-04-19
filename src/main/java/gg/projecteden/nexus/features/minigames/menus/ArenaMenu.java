package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.menus.teams.TeamMenus;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.NonNull;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.minigames.Minigames.menus;

public class ArenaMenu extends MenuUtils implements InventoryProvider {
	private final Arena arena;

	public ArenaMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> menus.openArenaMenu(player, arena)));
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
		MECHANIC_TYPE(1, 4, Material.REDSTONE) {
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
		CUSTOM_MECHANIC_SETTINGS(1, 5, Material.WRITABLE_BOOK) {
			@Override
			void onClick(Player player, Arena arena) {
				menus.openCustomSettingsMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				return null;
			}
		},
		TEST_MODE(1, 7, Material.LEVER) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.setTestMode(!arena.isTestMode());
				arena.write();
				menus.openArenaMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				return "||&eCurrent value: &3" + (arena.isTestMode() ? "&cEnabled" : "&aDisabled");
			}
		},
		TEAMS(2, 1, Material.WHITE_WOOL) {
			@Override
			void onClick(Player player, Arena arena) {
				new TeamMenus().openTeamsMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				return "||&eCurrent Teams: " + arena.getTeams().stream().map(Team::getColoredName).collect(Collectors.joining("&f, "));
			}
		},
		LOBBY(2, 2, Material.OAK_DOOR) {
			@Override
			void onClick(Player player, Arena arena) {
				menus.openLobbyMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				return null;
			}
		},
		FLAGS(2, 3, new ItemStack(Material.CYAN_BANNER, 1)) {
			@Override
			void onClick(Player player, Arena arena) {
				menus.openFlagsMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				return null;
			}
		},
		LIVES(3, 1, new ItemBuilder(Material.PLAYER_HEAD).build()),
		SECONDS(3, 2, Material.CLOCK) {
			@Override
			public String getTitle() {
				return "Game Time (Seconds)";
			}
		},
		BEGIN_DELAY(3, 3, Material.REPEATER),
		WINNING_SCORE(3, 4, Material.GOLD_INGOT),
		MIN_WINNING_SCORE(3, 5, Material.BRICK),
		MAX_WINNING_SCORE(3, 6, Material.IRON_INGOT),
		MIN_PLAYERS(4, 1, Material.LEATHER_CHESTPLATE),
		MAX_PLAYERS(4, 2, Material.DIAMOND_CHESTPLATE),
		SPECTATE_LOCATION(5, 1, Material.COMPASS) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.setSpectateLocation(player.getLocation());
				arena.write();
				menus.openArenaMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena){
				if(arena.getSpectateLocation() == null) return "null";
				return getLocationLore(arena.getSpectateLocation());
			}

		},
		RESPAWN_LOCATION(5, 2, Material.RED_BED) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.setRespawnLocation(player.getLocation());
				arena.write();
				menus.openArenaMenu(player, arena);
			}

			@Override
			String getLore(Player player, Arena arena) {
				if(arena.getRespawnLocation() == null) return "null";
				return getLocationLore(arena.getRespawnLocation());
			}
		},
		RESPAWN_SECONDS(5, 3, Material.TOTEM_OF_UNDYING),
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
			return StringUtils.camelCase(name());
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
			return new PropertyDescriptor(Character.toLowerCase(name().charAt(0)) + StringUtils.camelCase(name()).substring(1).replaceAll(" ", ""), Arena.class);
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
					ClickableItem.of(
							nameItem(menuItem.getItem(), "&e" + menuItem.getTitle(), menuItem.getLore(player, arena)),
							e -> menuItem.onClick(player, arena)
					)
			)
		);
	}

}
