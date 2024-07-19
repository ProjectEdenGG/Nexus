package gg.projecteden.nexus.features.events.y2024.pugmas24.ballooneditor;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BalloonEditorUtils {

	static void sendCooldown(String message, String key) {
		if (!new CooldownService().check(BalloonEditor.getEditor().getUuid(), key, TickTime.SECOND))
			return;

		send(message);
	}

	static void send(String message) {
		BalloonEditor.getEditor().sendMessage(BalloonEditor.PREFIX + message);
	}

	public static boolean isEditing(Player player) {
		if (!Pugmas24.get().isAtEvent(player))
			return false;

		if (!BalloonEditor.isBeingUsed())
			return false;

		if (BalloonEditor.getEditor() == null || player == null)
			return false;

		return player.getUniqueId().toString().equalsIgnoreCase(BalloonEditor.getEditor().getUniqueId().toString());
	}

	public static String getEditorName() {
		return BalloonEditor.getEditor().getNickname();
	}

	public static ItemStack getReplaceBrush() {
		return BlockReplaceBrushMenu.getBrushItem().dyeColor(BalloonEditor.getBrushColor()).build();
	}

	static void giveBrush() {
		Player player = BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		PlayerUtils.giveItem(player, getReplaceBrush());

	}

	static void removeBrush() {
		Player player = BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		PlayerUtils.removeItem(player, BlockReplaceBrushMenu.BRUSH_MATERIAL);
	}

	static void enableFlight() {
		Player player = BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		BalloonEditor.allowedFlight = player.getAllowFlight();
		BalloonEditor.flying = player.isFlying();

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	static void resetFlight() {
		Player player = BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		player.setAllowFlight(BalloonEditor.allowedFlight);
		player.setFlying(BalloonEditor.flying);
	}

	static void removeBrush(Player player) {
		if (player == null)
			return;

		PlayerUtils.removeItem(player, BlockReplaceBrushMenu.BRUSH_MATERIAL);
	}

	static void disableFlight(Player player) {
		if (player == null)
			return;

		player.setAllowFlight(false);
		player.setFlying(false);
	}

	public static void nextTemplate() {
		int id = BalloonEditor.schemId;
		id++;
		if (id > BalloonEditor.TEMPLATE_SIZE)
			id = 1;

		BalloonEditor.selectTemplate(id);
	}

	public static void previousTemplate() {
		int id = BalloonEditor.schemId;
		id--;
		if (id < 1)
			id = BalloonEditor.TEMPLATE_SIZE;

		BalloonEditor.selectTemplate(id);
	}
}
