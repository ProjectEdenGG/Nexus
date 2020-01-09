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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;

public class PotionEffectsMenu extends MenuUtils implements InventoryProvider {

    Arena arena;
    Team team;
    TeamMenus teamMenus = new TeamMenus();
    public PotionEffectsMenu(Arena arena, Team team) {
        this.arena = arena;
        this.team = team;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        //Back Item
        contents.set(0, 0, ClickableItem.of(backItem(), e->teamMenus.openLoadoutMenu(player, arena, team)));

        //Copy Potions Item
        contents.set(0, 8, ClickableItem.of(nameItem(new ItemStack(Material.ANVIL), "&eCopy Potions", "&3This will copy all the||&3potion effects you have||&3into the team's loadout."), e->{
            team.getLoadout().getPotionEffects().addAll(e.getWhoClicked().getActivePotionEffects());
            ArenaManager.write(arena);
            ArenaManager.add(arena);
            teamMenus.openLoadoutMenu(player, arena, team);
        }));

        //List Potion Effects
        contents.set(0, 6, ClickableItem.of(nameItem(new ItemStack(Material.BOOK), "&eList Potion Effects", "&3Click me to get a list of||&3all valid potion effect||&3names that can be added."), e->{
            e.getWhoClicked().closeInventory();
            StringBuilder potions = new StringBuilder();
            ArrayList<PotionEffectType> potionList = new ArrayList<PotionEffectType>(Arrays.asList(PotionEffectType.values()));
            potionList.remove(0);
            for(PotionEffectType potion : potionList) {
                potions.append(potion.getName().substring(0, 1).toUpperCase()).append(potion.getName().substring(1).toLowerCase()).append(", ");
            }
            potions = new StringBuilder(potions.substring(0, potions.lastIndexOf(", ")));
            player.sendMessage(Utils.colorize(PREFIX + "&3Available Potion Effect Types:"));
            player.sendMessage(Utils.colorize(PREFIX + "&e" + potions));
        }));

        //Add Effect Item
        contents.set(0, 4, ClickableItem.of(nameItem(new ItemStack(Material.EMERALD_BLOCK),"&eAdd Potion Effect"), e->{
            new AnvilGUI.Builder()
                    .onClose(p -> { teamMenus.openPotionEffectsMenu(player, arena, team); })
                    .onComplete((p, text) -> {
                        try {
                            PotionEffectType potion = PotionEffectType.getByName(text.toUpperCase());
                            team.getLoadout().getPotionEffects().add(new PotionEffect(potion, 5, 0));
                            ArenaManager.write(arena);
                            ArenaManager.add(arena);
                            teamMenus.openPotionEffectsMenu(player, arena, team);
                            return AnvilGUI.Response.text(text);
                        } catch (Exception ex) {
                            player.sendMessage(Utils.colorize(PREFIX + "Please use one of the valid potion types."));
                            teamMenus.openPotionEffectsMenu(player, arena, team);
                            return AnvilGUI.Response.close();
                        }
                    })
                    .text("Potion Effect Name")
                    .plugin(BNCore.getInstance())
                    .open(player);
        }));

        //Potion Effects
        int row = 1;
        int column = 0;
        for(PotionEffect potion : team.getLoadout().getPotionEffects()){
            ItemStack item = nameItem(new ItemStack(Material.POTION), "&e" + potion.getType().getName(), "&3Duration:&e " + potion.getDuration() + "||&3Amplifier:&e " + potion.getAmplifier() + "|| ||&7Click me to edit.");
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(potion, true);
            item.setItemMeta(meta);
            contents.set(row, column, ClickableItem.of(item, e -> teamMenus.openPotionEffectEditorMenu(player, arena, team, potion)));
            if(column != 8){
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
