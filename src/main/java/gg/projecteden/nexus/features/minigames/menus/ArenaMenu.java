package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.menus.flags.FlagsMenu;
import gg.projecteden.nexus.features.minigames.menus.teams.TeamsMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Rows(5)
public class ArenaMenu extends InventoryProvider {
	private final Arena arena;

	public ArenaMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public String getTitle() {
		return arena.getDisplayName();
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> new ArenaMenu(arena).open(player)));
	}

	@Getter
	@AllArgsConstructor
	private enum ArenaMenuItem {
		DELETE_ARENA(1, 9, Material.TNT) {
			@Override
			void onClick(Player player, Arena arena) {
				ConfirmationMenu.builder()
					.onCancel(e -> new ArenaMenu(arena).open(player))
					.onConfirm(e -> {
						arena.delete();
						PlayerUtils.send(player, Minigames.PREFIX + "Arena &e" + arena.getName() + " &3deleted");
					})
					.open(player);
			}

			@Override
			public String getTitle() {
				return "&c&lDelete Arena";
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				return List.of("&7You will need to confirm", "&7deleting an arena.", "", "&7&lTHIS CANNOT BE UNDONE.");
			}
		},
		NAME(1, 1, Material.NAME_TAG),
		DISPLAY_NAME(1, 2, Material.PAPER),
		MECHANIC_TYPE(1, 4, Material.REDSTONE) {
			@Override
			void onClick(Player player, Arena arena) {
				new MechanicsMenu(arena).open(player);

			}

			@Override
			String getter(Player player, Arena arena) {
				return arena.getMechanic().getName();
			}
		},
		CUSTOM_MECHANIC_SETTINGS(1, 5, Material.WRITABLE_BOOK) {
			@Override
			void onClick(Player player, Arena arena) {
				MechanicsMenu.openCustomSettingsMenu(player, arena);
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				return null;
			}
		},
		TEST_MODE(1, 7, Material.LEVER) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.setTestMode(!arena.isTestMode());
				arena.write();
				new ArenaMenu(arena).open(player);
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				return List.of("", "&eCurrent value: &3" + (arena.isTestMode() ? "&cEnabled" : "&aDisabled"));
			}
		},
		TEAMS(2, 1, Material.WHITE_WOOL) {
			@Override
			void onClick(Player player, Arena arena) {
				new TeamsMenu(arena).open(player);
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				return List.of("", "&eCurrent Teams: " + arena.getTeams().stream().map(Team::getColoredName).collect(Collectors.joining("&f, ")));
			}
		},
		LOBBY(2, 2, Material.OAK_DOOR) {
			@Override
			void onClick(Player player, Arena arena) {
				new LobbyMenu(arena).open(player);
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				return null;
			}
		},
		FLAGS(2, 3, new ItemBuilder(Material.CYAN_BANNER)) {
			@Override
			void onClick(Player player, Arena arena) {
				new FlagsMenu(arena).open(player);
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				return null;
			}
		},
		LIVES(3, 1, new ItemBuilder(Material.PLAYER_HEAD)),
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
				new ArenaMenu(arena).open(player);
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				if (arena.getSpectateLocation() == null)
					return Collections.singletonList("null");
				return MenuUtils.getLocationLore(arena.getSpectateLocation());
			}

		},
		RESPAWN_LOCATION(5, 2, Material.RED_BED) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.setRespawnLocation(player.getLocation());
				arena.write();
				new ArenaMenu(arena).open(player);
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				if (arena.getRespawnLocation() == null)
					return Collections.singletonList("null");
				return MenuUtils.getLocationLore(arena.getRespawnLocation());
			}
		},
		RESPAWN_SECONDS(5, 3, Material.TOTEM_OF_UNDYING),
		SAVE(5, 9, Material.END_CRYSTAL) {
			@Override
			void onClick(Player player, Arena arena) {
				arena.write();
			}

			@Override
			List<String> getLore(Player player, Arena arena) {
				return null;
			}
		};

		private final int row;
		private final int column;
		private final ItemBuilder item;

		ArenaMenuItem(int row, int column, Material material) {
			this(row, column, new ItemBuilder(material));
		}

		public ItemBuilder getItem() {
			return item.clone();
		}

		public String getTitle() {
			return StringUtils.camelCase(name());
		}

		List<String> getLore(Player player, Arena arena) {
			return List.of("", "&eCurrent value: &3" + getter(player, arena));
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
			} catch (Exception ignore) {
			}
			return "";
		}

		void setter(Player player, Arena arena, String text) {
			try {
				PropertyDescriptor propertyDescriptor = getPropertyDescriptor();
				Object value = text;
				if (propertyDescriptor.getPropertyType() == Integer.TYPE)
					value = Integer.valueOf(text);
				propertyDescriptor.getWriteMethod().invoke(arena, value);
			} catch (Exception ignore) {
			}

			arena.write();
		}
	}

	@Override
	public void init() {
		Arrays.asList(ArenaMenuItem.values()).forEach(menuItem -> {
			final ItemBuilder item = menuItem.getItem()
				.name("&e" + menuItem.getTitle())
				.lore(menuItem.getLore(viewer, arena));

			contents.set(
				(menuItem.getRow() - 1),
				(menuItem.getColumn() - 1),
				ClickableItem.of(item, e -> menuItem.onClick(viewer, arena))
			);
		});
	}

}
