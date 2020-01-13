package me.pugabyte.bncore.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.menus.teams.TeamMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.PotionEffectEditor;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.function.BiFunction;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;
import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class PotionEffectEditorMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;
	PotionEffect potionEffect;
	TeamMenus teamMenus = new TeamMenus();

	public PotionEffectEditorMenu(Arena arena, Team team, PotionEffect potionEffect) {
		this.arena = arena;
		this.team = team;
		this.potionEffect = potionEffect;
	}

	static void openAnvilMenu(Player player, Arena arena, Team team, PotionEffect potionEffect, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Utils.wait(1, () -> menus.getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> teamMenus.openPotionEffectsMenu(player, arena, team));

		//Potion Item
		contents.set(0, 4, ClickableItem.empty(nameItem(Material.DIAMOND_BLOCK, "&e" + potionEffect.getType().getName(),
				"&3Duration:&e " + potionEffect.getDuration() + "||&3Amplifier: &e" + potionEffect.getAmplifier())));

		contents.set(1, 3, ClickableItem.from(nameItem(Material.REDSTONE, "&eDuration"),
				e -> openAnvilMenu(player, arena, team, potionEffect, String.valueOf(potionEffect.getDuration()), (p, text) -> {
					if (Utils.isInt(text)) {
						potionEffect = new PotionEffectEditor(potionEffect).withDuration(Integer.parseInt(text));
						arena.write();
						teamMenus.openLoadoutMenu(player, arena, team);
						return AnvilGUI.Response.text(text);
					} else {
						player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for the duration."));
						return AnvilGUI.Response.close();
					}
				})));

		contents.set(1, 5, ClickableItem.from(nameItem(Material.GLOWSTONE_DUST, "&eAmplifier"),
				e -> openAnvilMenu(player, arena, team, potionEffect, String.valueOf(potionEffect.getAmplifier()), (p, text) -> {
					if (Utils.isInt(text)) {
						potionEffect = new PotionEffectEditor(potionEffect).withAmplifier(Integer.parseInt(text));
						arena.write();
						teamMenus.openLoadoutMenu(player, arena, team);
						return AnvilGUI.Response.text(text);
					} else {
						player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for the amplifier."));
						return AnvilGUI.Response.close();
					}
				})));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
