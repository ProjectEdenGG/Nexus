package me.pugabyte.bncore.features.minigames.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.teams.TeamMenus;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class PotionEffectEditorMenu extends MenuUtils implements InventoryProvider {

    Arena arena;
    Team team;
    PotionEffect potionEffect;
    TeamMenus teamMenus = new TeamMenus();
    public PotionEffectEditorMenu(Arena arena, Team team, PotionEffect potionEffect){
        this.arena = arena;
        this.team = team;
        this.potionEffect = potionEffect;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        //Back Item
        contents.set(0, 0, ClickableItem.of(backItem(), e -> teamMenus.openPotionEffectsMenu(player, arena, team)));

        //Potion Item
        contents.set(0, 4, ClickableItem.empty(nameItem(new ItemStack(Material.DIAMOND_BLOCK),
                "&3" + potionEffect.getType().getName(), "&3Duration:&e " + potionEffect.getDuration() + "||&3Amplifier: &e" + potionEffect.getAmplifier())));

        //Duration Item
        contents.set(1, 3, ClickableItem.of(nameItem(new ItemStack(Material.REDSTONE), "&3Duration"), e ->{
            new AnvilGUI.Builder()
                    .onClose(p -> { teamMenus.openPotionEffectEditorMenu(player, arena, team, potionEffect); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>(team.getLoadout().getPotionEffects());
                            potions.remove(potionEffect);
                            potions.add(new PotionEffect(potionEffect.getType(), Integer.parseInt(text), potionEffect.getAmplifier()));
                            team.getLoadout().setPotionEffects(potions);
                            ArenaManager.write(arena);
                            ArenaManager.add(arena);
                            teamMenus.openPotionEffectsMenu(player, arena, team);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(Utils.getPrefix("Minigames") + "You must use an integer for the duration."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Duration")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));

        //Amplifier Item
        contents.set(1, 5, ClickableItem.of(nameItem(new ItemStack(Material.GLOWSTONE_DUST), "&3Amplifier"), e ->{
            new AnvilGUI.Builder()
                    .onClose(p -> { teamMenus.openPotionEffectEditorMenu(player, arena, team, potionEffect); })
                    .onComplete((p, text) -> {
                        if(Utils.isInt(text)){
                            ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>(team.getLoadout().getPotionEffects());
                            potions.remove(potionEffect);
                            potions.add(new PotionEffect(potionEffect.getType(), potionEffect.getDuration(), Integer.parseInt(text)));
                            team.getLoadout().setPotionEffects(potions);
                            ArenaManager.write(arena);
                            ArenaManager.add(arena);
                            teamMenus.openPotionEffectsMenu(player, arena, team);
                            return AnvilGUI.Response.text(text);
                        }
                        else {
                            player.sendMessage(Utils.colorize(Utils.getPrefix("Minigames") + "You must use an integer for the amplifier."));
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Amplifier")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
