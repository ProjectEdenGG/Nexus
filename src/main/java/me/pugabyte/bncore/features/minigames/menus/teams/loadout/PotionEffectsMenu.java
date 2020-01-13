package me.pugabyte.bncore.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;
import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class PotionEffectsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;

	public PotionEffectsMenu(Arena arena, Team team) {
		this.arena = arena;
		this.team = team;
	}

	static void openAnvilMenu(Player player, Arena arena, Team team, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Utils.wait(1, () -> menus.getTeamMenus().openPotionEffectsMenu(player, arena, team)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> menus.getTeamMenus().openLoadoutMenu(player, arena, team));

		contents.set(0, 2, ClickableItem.from(nameItem(new ItemStack(Material.ANVIL), "&eCopy Potions", "&3This will copy all the||&3potion effects you have||&3into the team's loadout."), e -> {
			team.getLoadout().getEffects().addAll(player.getActivePotionEffects());
			arena.write();
			menus.getTeamMenus().openLoadoutMenu(player, arena, team);
		}));

		contents.set(0, 6, ClickableItem.from(nameItem(new ItemStack(Material.BOOK), "&eList Potion Effects", "&3Click me to get a list of||&3all valid potion effect||&3names that can be added."), e -> {
			StringBuilder potions = new StringBuilder();
			ArrayList<PotionEffectType> potionList = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
			potionList.remove(0);
			for (PotionEffectType potion : potionList)
				potions.append(potion.getName().substring(0, 1).toUpperCase()).append(potion.getName().substring(1).toLowerCase()).append(", ");

			potions = new StringBuilder(potions.substring(0, potions.lastIndexOf(", ")));
			player.sendMessage(Utils.colorize(PREFIX + "&3Available Potion Effect Types:"));
			player.sendMessage(Utils.colorize(PREFIX + "&e" + potions));
		}));

		contents.set(0, 4, ClickableItem.from(nameItem(
				Material.EMERALD_BLOCK,
				"&eAdd Potion Effect"
			),
			e -> openAnvilMenu(player, arena, team, "Potion Effect Name", (p, text) -> {
				try {
					PotionEffectType potion = PotionEffectType.getByName(text.toUpperCase());
					team.getLoadout().getEffects().add(new PotionEffect(potion, 0, 0));
					arena.write();
					menus.getTeamMenus().openPotionEffectsMenu(player, arena, team);
					return AnvilGUI.Response.text(text);
				} catch (Exception ex) {
					player.sendMessage(Utils.colorize(PREFIX + "Please use one of the valid potion types."));
					return AnvilGUI.Response.close();
				}
			})));

		ItemStack deleteItem = nameItem(Material.TNT, "&cDelete Item", "&7Click me to enter deletion mode.||&7Then, click a potion effect with ||&7me to delete it.");
		contents.set(0, 8, ClickableItem.from(deleteItem, e -> Utils.wait(2, () -> {
			if (player.getItemOnCursor().getType().equals(Material.TNT)) {
				player.setItemOnCursor(new ItemStack(Material.AIR));
			} else if (Utils.isNullOrAir(player.getItemOnCursor())) {
				player.setItemOnCursor(deleteItem);
			}
		})));

		if (team.getLoadout().getEffects() == null) return;

		int row = 1;
		int column = 0;
		for (PotionEffect potionEffect : team.getLoadout().getEffects()) {
			ItemStack item = new ItemStackBuilder(Material.POTION)
					.name("&e" + potionEffect.getType().getName())
					.lore("&3Duration:&e " + potionEffect.getDuration(), "&3Amplifier:&e " + potionEffect.getAmplifier(), " ", "&7Click me to edit.")
					.effect(potionEffect)
					.build();

			contents.set(row, column, ClickableItem.from(item, e -> {
				InventoryClickEvent event = (InventoryClickEvent) e.getEvent();
				if (event.getCursor().getType().equals(Material.TNT)) {
					Utils.wait(2, () -> {
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

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}
