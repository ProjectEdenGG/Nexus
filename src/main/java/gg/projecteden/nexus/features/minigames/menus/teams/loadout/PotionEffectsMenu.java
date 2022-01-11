package gg.projecteden.nexus.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.features.minigames.Minigames.PREFIX;
import static gg.projecteden.nexus.features.minigames.Minigames.menus;

public class PotionEffectsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;

	public PotionEffectsMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	static void openAnvilMenu(Player player, Arena arena, Team team, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> menus.getTeamMenus().openPotionEffectsMenu(player, arena, team)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> menus.getTeamMenus().openLoadoutMenu(player, arena, team));

		contents.set(0, 2, ClickableItem.from(nameItem(Material.ANVIL, "&eCopy Potions", "&3This will copy all the||&3potion effects you have||&3into the team's loadout."), e -> {
			team.getLoadout().getEffects().addAll(player.getActivePotionEffects());
			arena.write();
			menus.getTeamMenus().openPotionEffectsMenu(player, arena, team);
		}));

		contents.set(0, 6, ClickableItem.from(nameItem(Material.BOOK, "&eList Potion Effects", "&3Click me to get a list of||&3all valid potion effect||&3names that can be added."), e -> {
			StringBuilder potions = new StringBuilder();
			ArrayList<PotionEffectType> potionList = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
			potionList.remove(0);
			for (PotionEffectType potion : potionList)
				potions.append(potion.getName().substring(0, 1).toUpperCase()).append(potion.getName().substring(1).toLowerCase()).append(", ");

			potions = new StringBuilder(potions.substring(0, potions.lastIndexOf(", ")));
			PlayerUtils.send(player, PREFIX + "&3Available Potion Effect Types:");
			PlayerUtils.send(player, PREFIX + "&e" + potions);
		}));

		contents.set(0, 4, ClickableItem.from(nameItem(Material.EMERALD_BLOCK, "&eAdd Potion Effect"),
			e -> {
					PotionEffect potionEffect = new PotionEffectBuilder(PotionEffectType.SPEED).duration(5).amplifier(5).ambient(true).build();
					team.getLoadout().getEffects().add(potionEffect);
					arena.write();
					menus.getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect);
			}));

		ItemStack deleteItem = nameItem(Material.TNT, "&cDelete Item", "&7Click me to enter deletion mode.||&7Then, click a potion effect with ||&7me to delete it.");
		contents.set(0, 8, ClickableItem.from(deleteItem, e -> Tasks.wait(2, () -> {
			if (player.getItemOnCursor().getType().equals(Material.TNT)) {
				player.setItemOnCursor(new ItemStack(Material.AIR));
			} else if (Nullables.isNullOrAir(player.getItemOnCursor())) {
				player.setItemOnCursor(deleteItem);
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

			contents.set(row, column, ClickableItem.from(item, e -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					Tasks.wait(2, () -> {
						team.getLoadout().getEffects().remove(potionEffect);
						arena.write();
						player.setItemOnCursor(new ItemStack(Material.AIR));
						menus.getTeamMenus().openPotionEffectsMenu(player, arena, team);
					});
				} else {
					menus.getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect);
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
