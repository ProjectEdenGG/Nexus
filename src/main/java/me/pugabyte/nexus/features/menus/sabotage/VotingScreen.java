package me.pugabyte.nexus.features.menus.sabotage;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageColor;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageTeam;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class VotingScreen extends MenuUtils implements InventoryProvider {
	private final Minigamer reporter;

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title("Who is the Impostor?")
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents inventoryContents) {
		Minigamer voter = PlayerManager.get(player);
		Match match = voter.getMatch();
		SabotageMatchData matchData = match.getMatchData();

		// TODO: skip button

		inventoryContents.set(0, 2, ClickableItem.from(new ItemBuilder(Material.BARRIER).name("&eSkip Vote").build(), $ -> matchData.vote(voter, null)));
		if (!reporter.getUniqueId().equals(voter.getUniqueId()))
			inventoryContents.set(0, 4, votingItem(voter, reporter, matchData));
		inventoryContents.set(0, 6, votingItem(voter, voter, matchData));

		int row = 1;
		int col = 0;
		for (Minigamer target : match.getAllMinigamers()) {
			if (target.equals(voter) || target.equals(reporter))
				continue;
			inventoryContents.set(row, col, votingItem(voter, target, matchData));
			col += 1;
			if (col == 9) {
				col = 0;
				row += 1;
			}
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {
		init(player, contents);
	}

	private ClickableItem votingItem(Minigamer voter, Minigamer target, SabotageMatchData matchData) {
		return votingItem(votingItemBuilder(voter, target, matchData).build(), voter, target, matchData);
	}

	private ClickableItem votingItem(ItemStack item, Minigamer voter, Minigamer target, SabotageMatchData matchData) {
		return ClickableItem.from(item, $ -> matchData.vote(voter, target));
	}

	private ItemBuilder votingItemBuilder(Minigamer voter, Minigamer target, SabotageMatchData matchData) {
		SabotageColor targetColor = matchData.getColor(target);
		ItemBuilder builder = new ItemBuilder(targetColor.getHead()).name(new JsonBuilder(target.getNickname(), targetColor));
		List<ComponentLike> components = new ArrayList<>();
		components.add(SabotageTeam.render(voter, target).asComponent());
		components.add(new JsonBuilder(target.isAlive() ? "&fAlive" : "&cDead"));
		if (target.getUniqueId().equals(reporter.getUniqueId()))
			components.add(new JsonBuilder("&eReporter"));
		if (matchData.hasVoted(target))
			components.add(new JsonBuilder("&cVoted"));
		return builder.componentLore(components);
	}
}
