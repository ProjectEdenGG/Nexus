package me.pugabyte.nexus.features.menus.sabotage;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

// this should be called SabotageMenu but there's already a SabotageMenu so oh well
@Getter
public class ImpostorMenu extends MenuUtils implements InventoryProvider {
    private final SmartInventory inventory = SmartInventory.builder()
            .title(colorize("&4Sabotage"))
            .size(3, 9)
            .provider(this)
            .build();

    @Override
    public void open(Player viewer, int page) {
        getInventory().open(viewer, page);
    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Minigamer minigamer = PlayerManager.get(player);
        Match match = minigamer.getMatch();
        SabotageMatchData matchData = match.getMatchData();
        match.getTasks().repeat(1, 2, () -> {

        });
    }
}
