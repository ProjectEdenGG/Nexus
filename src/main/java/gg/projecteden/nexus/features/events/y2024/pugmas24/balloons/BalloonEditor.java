package gg.projecteden.nexus.features.events.y2024.pugmas24.balloons;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class BalloonEditor implements Listener {
	private static final Location WARP = Pugmas24.get().location(-614.5, 156.0, -3216.5, 0, 0);
	protected static final String REGION_EDIT = BalloonManager.REGION_BASE + "edit";  // TODO FINAL: DEFINE REGION
	protected static final String REGION_SCHEM = BalloonManager.REGION_BASE + "schem";  // TODO FINAL: DEFINE REGION
	protected static final Location SCHEM_PASTE = Pugmas24.get().location(-620, 162, -3214); // TODO FINAL: LOCATION
	protected static final int TEMPLATE_SIZE = BalloonManager.getTotalTemplateSchematics();
	protected static final ColorType defaultBrushColor = ColorType.RED;

	public static final String PREFIX = StringUtils.getPrefix("BalloonEditor");

	@Getter
	private static boolean beingUsed = false;
	@Getter
	private static boolean stopping = false;
	protected static int schemId = 1;
	@Getter
	private static Nerd editor;
	protected static boolean flying = false;
	protected static boolean allowedFlight = false;
	protected static boolean lostEditor = false;
	protected static int lostEditorTask;
	@Getter
	protected static boolean savingSchem = false;

	@Getter
	@Setter
	private static ColorType brushColor = defaultBrushColor;

	public BalloonEditor() {
		Nexus.registerListener(this);

		Pugmas24.get().forceLoadRegions(REGION_EDIT);
	}

	public static void shutdown() {
		if (!isBeingUsed())
			return;

		saveBalloon();
	}

	public static boolean reload() {
		if (!isBeingUsed())
			return false;

		if (isStopping())
			return true;

		stopping = true;
		saveBalloon();
		return true;
	}

	public static void editBalloon(Nerd nerd) {
		beingUsed = true;
		editor = nerd;

		flying = editor.getPlayer().isFlying();
		allowedFlight = editor.getPlayer().getAllowFlight();

		if (hasSchematic())
			pasteBalloon(getSchematicPath());
		else
			resetBalloon();

		BalloonEditorUtils.giveBrush();
		BalloonEditorUtils.enableFlight();
	}

	public static boolean hasSchematic() {
		File file = BalloonManager.worldedit.getSchematicFile(getSchematicPath(), true);
		return file.exists();
	}

	public static void saveBalloon() {
		Player player = editor.getPlayer();
		if (player == null)
			throw new InvalidInputException(PREFIX + "player cannot be null");

		if (savingSchem)
			throw new InvalidInputException(PREFIX + "balloon is already saving");

		BalloonEditorUtils.removeBrush();

		String filePath = getSchematicPath();
		Region region = BalloonManager.worldguard.convert(BalloonManager.worldguard.getProtectedRegion(REGION_SCHEM));

		String selectCommand = "rg select " + REGION_SCHEM;
		String copyCommand = "/copy";
		String saveCommand = "/schem save " + filePath + " -f";
		String deselectCommand = "/deselect";

		GameMode originalGameMode = player.getGameMode();
		Location location = BalloonManager.worldedit.toLocation(region.getMinimumPoint());

		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(location);

		Tasks.wait(1, () -> {
			if (!player.isOnline()) {
				return;
			}

			savingSchem = true;

			PlayerUtils.runCommandAsOp(player, selectCommand);
			Tasks.wait(10, () -> {
				if (!player.isOnline()) {
					savingSchem = false;
					return;
				}

				PlayerUtils.runCommandAsOp(player, copyCommand);

				Tasks.wait(10, () -> {
					if (!player.isOnline()) {
						savingSchem = false;
						return;
					}

					PlayerUtils.runCommandAsOp(player, saveCommand);

					Tasks.wait(10, () -> {
						savingSchem = false;

						if (!player.isOnline()) {
							reset();
							return;
						}

						PlayerUtils.runCommandAsOp(player, deselectCommand);

						player.teleport(WARP);
						player.setGameMode(originalGameMode);

						Tasks.wait(1, () -> {
							BalloonEditorUtils.send("Balloon saved");
							reset();
						});
					});
				});
			});
		});
	}

	public static void reset() {
		Player player = editor.getPlayer();
		if (player != null && player.isOnline()) {
			BalloonEditorUtils.resetFlight();
			BalloonEditorUtils.removeBrush();
		}

		brushColor = defaultBrushColor;
		stopping = false;
		lostEditor = false;
		if (lostEditorTask != -1)
			Tasks.cancel(lostEditorTask);
		lostEditorTask = -1;

		editor = null;
		beingUsed = false;
	}

	public static void resetBalloon() {
		selectTemplate(schemId);
	}

	public static void selectTemplate(int id) {
		schemId = id;
		pasteBalloon(BalloonManager.SCHEM_TEMPLATE + schemId);
	}

	//

	protected static void pasteBalloon(String filePath) {
		BalloonManager.pasteBalloonAsync(filePath, SCHEM_PASTE);

	}

	protected static String getSchematicPath() {
		return BalloonManager.SCHEM_USER + editor.getUniqueId();
	}

	//

	@EventHandler
	public void onSavingSchem(PlayerMoveEvent event) {
		if (!BalloonEditorUtils.isEditing(event.getPlayer()))
			return;

		if (!savingSchem)
			return;

		event.setCancelled(true);
		BalloonEditorUtils.sendCooldown("&cPlease wait while your balloon is saving", "pugmas24_balloon_editor-schem");
	}

	@EventHandler
	public void onSavingSchem(PlayerTeleportEvent event) {
		if (!BalloonEditorUtils.isEditing(event.getPlayer()))
			return;

		if (!savingSchem)
			return;

		event.setCancelled(true);
		BalloonEditorUtils.sendCooldown("&cPlease wait while your balloon is saving", "pugmas24_balloon_editor-schem");
	}

	@EventHandler
	public void onUseBrush(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!BalloonEditorUtils.isEditing(player))
			return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock))
			return;

		if (!BlockReplaceBrushMenu.isBrushItem(item))
			return;

		event.setCancelled(true);

		Action action = event.getAction();
		if (action.isLeftClick()) {
			new BlockReplaceBrushMenu(item).open(player);
			return;
		}

		if (!action.isRightClick() || !canEditBlock(clickedBlock))
			return;

		clickedBlock.setType(brushColor.getWool());
		new SoundBuilder(Sound.BLOCK_WOOL_PLACE).receiver(player).location(clickedBlock).volume(0.5).play();
		player.swingMainHand();
	}

	private boolean canEditBlock(Block block) {
		if (!BalloonManager.worldguard.isInRegion(block, REGION_SCHEM))
			return false;

		if (block.getType() == brushColor.getWool())
			return false;

		return MaterialTag.WOOL.isTagged(block);
	}

	@EventHandler
	public void whenEditing(PlayerLeavingRegionEvent event) {
		if (!BalloonEditorUtils.isEditing(event.getPlayer()))
			return;

		if (!event.getRegion().getId().equalsIgnoreCase(REGION_EDIT) || savingSchem)
			return;

		event.setCancelled(true);
		BalloonEditorUtils.sendCooldown("&cYou're still editing your balloon", "pugmas24_balloon_editor-editing");
	}

	@EventHandler
	public void whenEditing(PlayerTeleportEvent event) {
		if (!BalloonEditorUtils.isEditing(event.getPlayer()))
			return;

		if (BalloonManager.worldguard.isInRegion(event.getTo(), REGION_EDIT) || savingSchem)
			return;

		event.setCancelled(true);
		BalloonEditorUtils.sendCooldown("&cYou're still editing your balloon", "pugmas24_balloon_editor-editing");
	}

	@EventHandler
	public void whenEditing(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!BalloonEditorUtils.isEditing(player))
			return;

		lostEditor = true;
		lostEditorTask = Tasks.wait(TickTime.SECOND.x(30), () -> {
			if (!lostEditor)
				return;

			reset();
		});
	}

	@EventHandler
	public void whenEditing(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!BalloonEditorUtils.isEditing(player)) {
			if (BalloonManager.worldguard.isInRegion(player, REGION_EDIT)) {
				BalloonEditorUtils.removeBrush(player);
				player.setGameMode(GameMode.SURVIVAL);
				BalloonEditorUtils.disableFlight(player);
				PlayerUtils.runCommandAsOp(player, "/deselect");
			}
			return;
		}

		lostEditor = false;
		Tasks.cancel(lostEditorTask);
		lostEditorTask = -1;
	}
}
