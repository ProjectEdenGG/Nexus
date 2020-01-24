package me.pugabyte.bncore.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import me.pugabyte.bncore.utils.PotionEffectEditor;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.BiFunction;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;
import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class PotionEffectEditorMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;
	PotionEffect potionEffect;

	public PotionEffectEditorMenu(@NonNull Arena arena, @NonNull Team team, @NonNull PotionEffect potionEffect) {
		this.arena = arena;
		this.team = team;
		this.potionEffect = potionEffect;
	}

	static void openAnvilMenu(Player player, Arena arena, Team team, PotionEffect potionEffect, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> menus.getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> menus.getTeamMenus().openPotionEffectsMenu(player, arena, team));

		contents.set(0, 3, ClickableItem.from(nameItem(
					Material.REDSTONE,
					"&eDuration",
					"||&eCurrent value: &3" + potionEffect.getDuration()
				),
				e -> openAnvilMenu(player, arena, team, potionEffect, String.valueOf(potionEffect.getDuration()), (p, text) -> {
					if (Utils.isInt(text)) {
						team.getLoadout().getEffects().remove(potionEffect);
						potionEffect = new PotionEffectEditor(potionEffect).withDuration(Integer.parseInt(text) - 1);
						team.getLoadout().getEffects().add(potionEffect);
						arena.write();
						Tasks.wait(1, () -> {
							// Since potion effects don't have setters, pass-by-reference is broken, so we
							// have to do some hacky waits to get the menu to open with the correct object
							player.closeInventory();
							Tasks.wait(1, () -> menus.getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect));
						});
						return AnvilGUI.Response.text(text);
					} else {
						player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for the duration."));
						return AnvilGUI.Response.close();
					}
				})));

		contents.set(0, 5, ClickableItem.from(nameItem(
				Material.GLOWSTONE_DUST,
				"&eAmplifier",
				"||&eCurrent value: &3" + (potionEffect.getAmplifier() + 1)
				),
				e -> openAnvilMenu(player, arena, team, potionEffect, String.valueOf(potionEffect.getAmplifier()), (p, text) -> {
					if (Utils.isInt(text)) {
						team.getLoadout().getEffects().remove(potionEffect);
						potionEffect = new PotionEffectEditor(potionEffect).withAmplifier(Integer.parseInt(text));
						team.getLoadout().getEffects().add(potionEffect);
						arena.write();
						Tasks.wait(1, () -> {
							player.closeInventory();
							Tasks.wait(1, () -> menus.getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect));
						});
						return AnvilGUI.Response.text(text);
					} else {
						player.sendMessage(Utils.colorize(PREFIX + "You must use an integer for the amplifier."));
						return AnvilGUI.Response.close();
					}
				})));

		contents.set(0, 8, ClickableItem.from(nameItem(Material.END_CRYSTAL, "&eSave"), e-> arena.write()));

		int row = 2;
		int column = 0;
		for(PotionEffectType effect : PotionEffectType.values()){
			if(effect == null) continue;

			ItemStack potionItem = new ItemStackBuilder(Material.POTION)
					.name("&e" + Utils.camelCase(effect.getName().replace("_", " ")))
					.effect(new PotionEffect(effect, 5 ,0))
					.effectColor(effect.getColor())
					.build();

			if(effect == potionEffect.getType()) potionItem.setType(Material.SPLASH_POTION);

			contents.set(row, column, ClickableItem.from(potionItem,
					e-> {
				team.getLoadout().getEffects().remove(potionEffect);
				potionEffect = new PotionEffectEditor(potionEffect).withType(effect);
				team.getLoadout().getEffects().add(potionEffect);
				arena.write();
						Tasks.wait(1, () -> {
							player.closeInventory();
							Tasks.wait(1, () -> menus.getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect));
						});
					}));

			if(column == 8){
				column = 0;
				row++;
			} else {
				column++;
			}
		}

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
