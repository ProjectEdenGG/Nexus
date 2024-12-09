package gg.projecteden.nexus.features.events.y2025.pugmas25.balloons;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Pugmas25BalloonEditorUtils {

	static void send(String message) {
		Pugmas25.get().sendNoPrefix(Pugmas25BalloonEditor.getEditor().getPlayer(), Pugmas25BalloonEditor.PREFIX + message);
	}

	public static boolean isEditing(Player player) {
		if (!Pugmas25.get().isAtEvent(player))
			return false;

		if (!Pugmas25BalloonEditor.isBeingUsed())
			return false;

		if (Pugmas25BalloonEditor.getEditor() == null || player == null)
			return false;

		return player.getUniqueId().toString().equalsIgnoreCase(Pugmas25BalloonEditor.getEditor().getUniqueId().toString());
	}

	public static String getEditorName() {
		return Pugmas25BalloonEditor.getEditor().getNickname();
	}

	public static ItemStack getReplaceBrush() {
		return Pugmas25BlockReplaceBrushMenu.getBrushItem().dyeColor(Pugmas25BalloonEditor.getBrushColor()).build();
	}

	static void giveBrush() {
		Player player = Pugmas25BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		PlayerUtils.giveItem(player, getReplaceBrush());

	}

	static void removeBrush() {
		Player player = Pugmas25BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		PlayerUtils.removeItem(player, Pugmas25BlockReplaceBrushMenu.BRUSH_MATERIAL);
	}

	static void enableFlight() {
		Player player = Pugmas25BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		Pugmas25BalloonEditor.allowedFlight = player.getAllowFlight();
		Pugmas25BalloonEditor.flying = player.isFlying();

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	static void resetFlight() {
		Player player = Pugmas25BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		player.setAllowFlight(Pugmas25BalloonEditor.allowedFlight);
		player.setFlying(Pugmas25BalloonEditor.flying);
	}

	static void removeBrush(Player player) {
		if (player == null)
			return;

		PlayerUtils.removeItem(player, Pugmas25BlockReplaceBrushMenu.BRUSH_MATERIAL);
	}

	static void disableFlight(Player player) {
		if (player == null)
			return;

		player.setAllowFlight(false);
		player.setFlying(false);
	}

	public static void nextTemplate() {
		int id = Pugmas25BalloonEditor.schemId;
		id++;
		if (id > Pugmas25BalloonEditor.TEMPLATE_SIZE)
			id = 1;

		Pugmas25BalloonEditor.selectTemplate(id);
	}

	public static void previousTemplate() {
		int id = Pugmas25BalloonEditor.schemId;
		id--;
		if (id < 1)
			id = Pugmas25BalloonEditor.TEMPLATE_SIZE;

		Pugmas25BalloonEditor.selectTemplate(id);
	}
}