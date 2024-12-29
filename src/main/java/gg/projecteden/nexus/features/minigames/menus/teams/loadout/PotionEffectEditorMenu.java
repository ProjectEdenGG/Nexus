package gg.projecteden.nexus.features.minigames.menus.teams.loadout;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.BiFunction;

@AllArgsConstructor
@Title("Potion Effect Editor Menu")
public class PotionEffectEditorMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;
	private PotionEffect potionEffect;

	static void openAnvilMenu(Player player, Arena arena, Team team, PotionEffect potionEffect, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(player, text, onComplete, p -> Tasks.wait(1, () -> new PotionEffectEditorMenu(arena, team, potionEffect).open(player)));
	}

	@Override
	public void init() {
		addBackItem(e -> new PotionEffectsMenu(arena, team).open(viewer));

		contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.REDSTONE)
				.name("&eDuration")
				.lore("", "&eCurrent value: &3" + potionEffect.getDuration()),
			e -> openAnvilMenu(viewer, arena, team, potionEffect, String.valueOf(potionEffect.getDuration()), (p, text) -> {
				if (Utils.isInt(text)) {
					team.getLoadout().getEffects().remove(potionEffect);
					potionEffect = new PotionEffectBuilder(potionEffect).duration(Integer.parseInt(text)).build();
					team.getLoadout().getEffects().add(potionEffect);
					arena.write();
					Tasks.wait(1, () -> {
						// Since potion effects don't have setters, pass-by-reference is broken, so we
						// have to do some hacky waits to get the menu to open with the correct object
						viewer.closeInventory();
						Tasks.wait(1, () -> new PotionEffectEditorMenu(arena, team, potionEffect).open(viewer));
					});
					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(viewer, Minigames.PREFIX + "You must use an integer for the duration.");
					return AnvilGUI.Response.close();
				}
			})));

		contents.set(0, 5, ClickableItem.of(new ItemBuilder(Material.GLOWSTONE_DUST)
				.name("&eAmplifier")
				.lore("", "&eCurrent value: &3" + (potionEffect.getAmplifier() + 1)),
			e -> openAnvilMenu(viewer, arena, team, potionEffect, String.valueOf(potionEffect.getAmplifier()), (p, text) -> {
				if (Utils.isInt(text)) {
					team.getLoadout().getEffects().remove(potionEffect);
					potionEffect = new PotionEffectBuilder(potionEffect).amplifier(Integer.parseInt(text) - 1).build();
					team.getLoadout().getEffects().add(potionEffect);
					arena.write();
					Tasks.wait(1, () -> {
						viewer.closeInventory();
						Tasks.wait(1, () -> new PotionEffectEditorMenu(arena, team, potionEffect).open(viewer));
					});
					return AnvilGUI.Response.text(text);
				} else {
					PlayerUtils.send(viewer, Minigames.PREFIX + "You must use an integer for the amplifier.");
					return AnvilGUI.Response.close();
				}
			})));

		contents.set(0, 8, ClickableItem.of(Material.END_CRYSTAL, "&eSave", e -> arena.write()));

		int row = 2;
		int column = 0;
		for (PotionEffectType effect : PotionEffectType.values()) {
			if (effect == null) continue;

			ItemStack potionItem = new ItemBuilder(Material.POTION)
				.name("&e" + StringUtils.camelCase(effect.getName().replace("_", " ")))
				.potionEffect(new PotionEffectBuilder(effect).duration(5).amplifier(0))
				.potionEffectColor(effect.getColor())
				.build();

			if (effect == potionEffect.getType()) potionItem.setType(Material.SPLASH_POTION);

			contents.set(row, column, ClickableItem.of(potionItem,
				e -> {
					team.getLoadout().getEffects().remove(potionEffect);
					potionEffect = new PotionEffectBuilder(potionEffect).type(effect).build();
					team.getLoadout().getEffects().add(potionEffect);
					arena.write();
					Tasks.wait(1, () -> {
						viewer.closeInventory();
						Tasks.wait(1, () -> new PotionEffectEditorMenu(arena, team, potionEffect).open(viewer));
					});
				}));

			if (column == 8) {
				column = 0;
				row++;
			} else {
				column++;
			}
		}

	}

}
