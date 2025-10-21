package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.sudoku.SudokuConfig;
import gg.projecteden.nexus.models.sudoku.SudokuConfigService;
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
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;

import static gg.projecteden.nexus.models.sudoku.SudokuUser.get3x3ItemFrameGrid;
import static gg.projecteden.nexus.utils.MapUtils.getMapPixelHit;

@NoArgsConstructor
@Environments({Env.TEST, Env.UPDATE})
public class SudokuCommand extends CustomCommand implements Listener {
	private static final SudokuUserService userService = new SudokuUserService();
	private static final SudokuUser user = userService.get(Dev.GRIFFIN);;
	private static final SudokuConfigService configService = new SudokuConfigService();
	private static final SudokuConfig config = configService.get0();

	public SudokuCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("config create <id>")
	void config_create(String id) {
		if (config.getBoards().containsKey(id))
			error("A board with ID &e" + id + " &calready exists");

		var frames = get3x3ItemFrameGrid(player());
		config.create(id, new ArrayList<>(frames.keySet()));

		send(PREFIX + "Created board " + id);
	}

	@Path("config setMapIds")
	void config_setMapIds() {
		var frames = get3x3ItemFrameGrid(player());
		config.setMapIds(new ArrayList<>(frames.values()));
		configService.save(config);
		send(PREFIX + "Updated map IDs: " + config.getMapIds());
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

		if (!config.isBoardFrame(itemFrame))
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
		if (!config.isBoardFrame(itemFrame))
			return;

		event.setCancelled(true);
		handleInteract(player, itemFrame, result);
	}

	public void handleInteract(Player player, ItemFrame itemFrame, RayTraceResult result) {
		if (!(itemFrame.getItem().getItemMeta() instanceof MapMeta mapMeta))
			throw new InvalidInputException("Item frame item is not a map");

		var map = config.getMapIds().indexOf(mapMeta.getMapId());
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
		} else {
			if (player.isSneaking())
				user.setCellAnswer(selected, 0);
			else
				player.getInventory().setHeldItemSlot(answer - 1);
			user.setSelected(selected);
		}

		user.render().sendMapImagePackets();
		userService.save(user);
	}

}
