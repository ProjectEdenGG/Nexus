package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.sudoku.SudokuUser;
import gg.projecteden.nexus.models.sudoku.SudokuUser.Difficulty;
import gg.projecteden.nexus.models.sudoku.SudokuUserService;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;

import static gg.projecteden.nexus.utils.MapUtils.getMapPixelHit;

@NoArgsConstructor
@Environments({Env.TEST, Env.UPDATE})
public class SudokuCommand extends CustomCommand implements Listener {
	private static final SudokuUserService userService = new SudokuUserService();
	private static final SudokuUser user = userService.get(Dev.GRIFFIN);;

	public SudokuCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("start <difficulty>")
	void setup(Difficulty difficulty) {
		user.newGame(difficulty).sendMapImagePackets();
		userService.save(user);
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (user == null)
			return;

		if (!(event.getEntity() instanceof ItemFrame itemFrame))
			return;

		if (!user.getItemFrames().containsKey(itemFrame.getUniqueId()))
			return;

		event.setCancelled(true);

		if (!(event.getDamager() instanceof Player player))
			return;

		var result = player.rayTraceEntities(15);
		if (result == null || result.getHitEntity() == null)
			return;

		if (result.getHitEntity() != itemFrame)
			return;

		event.setCancelled(true);
		handleInteract(player, itemFrame, result);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		var player = event.getPlayer();
		if (!ActionGroup.CLICK.applies(event))
			return;

		if (user == null)
			return;

		var result = player.rayTraceBlocks(15);
		if (result == null || result.getHitBlock() == null)
			return;

		var entities = result.getHitBlock().getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, .6);
		if (entities.isEmpty())
			return;

		var itemFrame = entities.iterator().next();
		if (!user.getItemFrames().containsKey(itemFrame.getUniqueId()))
			return;

		event.setCancelled(true);
		handleInteract(player, itemFrame, result);
	}

	public void handleInteract(Player player, ItemFrame itemFrame, RayTraceResult result) {
		var map = new ArrayList<>(user.getItemFrames().keySet()).indexOf(itemFrame.getUniqueId());
		var pixelCoords = getMapPixelHit(itemFrame, result.getHitPosition());
		var selected = user.clickedCoordinateToGameCoordinate(map, pixelCoords);

		int answer = user.getCellAnswer(selected);
		if (answer == 0) {
			var slot = player.getInventory().getHeldItemSlot() + 1;
			if (player.isSneaking())
				if (user.getCandidates(selected).contains(slot))
					user.removeCandidate(selected, slot);
				else
					user.addCandidate(selected, slot);
			else
				user.setCellAnswer(selected, slot);

			user.render().sendMapImagePackets();
		} else {
			if (player.isSneaking())
				user.setCellAnswer(selected, 0);
			else
				player.getInventory().setHeldItemSlot(answer - 1);
			user.setSelected(selected).render().sendMapImagePackets();
		}

		userService.save(user);
	}

}
