package gg.projecteden.nexus.features.menus.sabotage;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.SabotageArena;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Tasks;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

// this should be called SabotageMenu but there's already a SabotageMenu so oh well
@Getter
public class ImpostorMenu extends MenuUtils implements InventoryProvider {
    private final SabotageArena arena;
    private final Set<Tasks> sabotages;
    private final SmartInventory inventory;

    public ImpostorMenu(SabotageArena arena) {
        this.arena = arena;
        sabotages = arena.getTasks().stream().filter(task -> task.getTaskType() == Tasks.TaskType.SABOTAGE).collect(Collectors.toCollection(LinkedHashSet::new));
        inventory = SmartInventory.builder()
                .title(colorize("&4Sabotage"))
                .size(getRows(sabotages.size(), 0), 9)
                .provider(this)
                .build();
    }

    @Override
    public void open(Player player, int page) {
        getInventory().open(player, page);
    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Minigamer minigamer = PlayerManager.get(player);
        Match match = minigamer.getMatch();
        SabotageMatchData matchData = match.getMatchData();
        match.getTasks().repeat(1, 2, () -> {
            int row = 0;
            int col = 0;
            // TODO: block sabotages/doors if one of the other was just called
            boolean canSabotage = matchData.getSabotage() == null;
            ItemBuilder builder = new ItemBuilder(canSabotage ? Material.WHITE_CONCRETE : Material.BLACK_CONCRETE);
            for (Tasks tasks : sabotages) {
                inventoryContents.set(row, col, ClickableItem.from(builder.clone().name(camelCase(tasks.name())).build(), $ -> sabotage(minigamer, tasks)));
                row += 1;
                if (row == 9) {
                    row = 0;
                    col += 1;
                }
            }
        });
    }

    private void sabotage(Minigamer player, Tasks task) {
        SabotageMatchData matchData = player.getMatch().getMatchData();
        if (matchData.getSabotage() == null)
            matchData.sabotage(task);
        else
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).category(SoundCategory.VOICE).volume(.8f).play();
    }
}
