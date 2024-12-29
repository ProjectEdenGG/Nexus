package gg.projecteden.nexus.features.minigames.menus.teams.loadout;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

@Rows(3)
@Title("Potion Effects Menu")
@RequiredArgsConstructor
public class PotionEffectsMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;

	static void openAnvilMenu(Player player, Arena arena, Team team, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> new PotionEffectsMenu(arena, team).open(player)));
	}

	@Override
	public void init() {
		addBackItem(e -> new LoadoutMenu(arena, team).open(viewer));

		contents.set(0, 2, ClickableItem.of(new ItemBuilder(Material.ANVIL).name("&eCopy Potions").lore("&3This will copy all the", "&3potion effects you have", "&3into the team's loadout."), e2 -> {
			team.getLoadout().getEffects().addAll(viewer.getActivePotionEffects());
			arena.write();
			new PotionEffectsMenu(arena, team).open(viewer);

		}));

		contents.set(0, 6, ClickableItem.of(new ItemBuilder(Material.BOOK).name("&eList Potion Effects").lore("&3Click me to get a list of", "&3all valid potion effect", "&3names that can be added."), e1 -> {
			StringBuilder potions = new StringBuilder();
			ArrayList<PotionEffectType> potionList = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
			potionList.remove(0);
			for (PotionEffectType potion : potionList)
				potions.append(potion.getName().substring(0, 1).toUpperCase()).append(potion.getName().substring(1).toLowerCase()).append(", ");

			potions = new StringBuilder(potions.substring(0, potions.lastIndexOf(", ")));
			PlayerUtils.send(viewer, Minigames.PREFIX + "&3Available Potion Effect Types:");
			PlayerUtils.send(viewer, Minigames.PREFIX + "&e" + potions);
		}));

		contents.set(0, 4, ClickableItem.of(Material.EMERALD_BLOCK, "&eAdd Potion Effect",
			e -> {
					PotionEffect potionEffect = new PotionEffectBuilder(PotionEffectType.SPEED).duration(5).amplifier(5).ambient(true).build();
					team.getLoadout().getEffects().add(potionEffect);
				arena.write();
				new PotionEffectEditorMenu(arena, team, potionEffect).open(viewer);

			}));

		ItemBuilder deleteItem = new ItemBuilder(Material.TNT)
			.name("&cDelete Item")
			.lore("&7Click me to enter deletion mode.", "&7Then, click a potion effect with ", "&7me to delete it.");
		contents.set(0, 8, ClickableItem.of(deleteItem, e -> Tasks.wait(2, () -> {
			if (viewer.getItemOnCursor().getType().equals(Material.TNT)) {
				viewer.setItemOnCursor(new ItemStack(Material.AIR));
			} else if (Nullables.isNullOrAir(viewer.getItemOnCursor())) {
				viewer.setItemOnCursor(deleteItem.build());
			}
		})));

		if (team.getLoadout().getEffects() == null) return;

		int row = 1;
		int column = 0;
		for (PotionEffect potionEffect : team.getLoadout().getEffects()) {
			ItemStack item = new ItemBuilder(Material.POTION)
					.name("&e" + potionEffect.getType().getName())
					.lore("&3Duration:&e " + potionEffect.getDuration(), "&3Amplifier:&e " + (potionEffect.getAmplifier() + 1), " ", "&7Click me to edit.")
					.potionEffect(potionEffect)
					.build();

			contents.set(row, column, ClickableItem.of(item, e -> {
				if (viewer.getItemOnCursor().getType().equals(Material.TNT)) {
					Tasks.wait(2, () -> {
						team.getLoadout().getEffects().remove(potionEffect);
						arena.write();
						viewer.setItemOnCursor(new ItemStack(Material.AIR));
						new PotionEffectsMenu(arena, team).open(viewer);

					});
				} else {
					new PotionEffectEditorMenu(arena, team, potionEffect).open(viewer);

				}
			}));

			if (column != 8) {
				column++;
			} else {
				column = 0;
				row++;
			}
		}
	}

}
