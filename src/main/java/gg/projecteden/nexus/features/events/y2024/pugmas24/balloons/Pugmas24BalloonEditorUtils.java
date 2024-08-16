package gg.projecteden.nexus.features.events.y2024.pugmas24.balloons;

import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Pugmas24BalloonEditorUtils {

	static void send(String message) {
		Pugmas24.get().sendNoPrefix(Pugmas24BalloonEditor.getEditor().getPlayer(), Pugmas24BalloonEditor.PREFIX + message);
	}

	public static boolean isEditing(Player player) {
		if (!Pugmas24.get().isAtEvent(player))
			return false;

		if (!Pugmas24BalloonEditor.isBeingUsed())
			return false;

		if (Pugmas24BalloonEditor.getEditor() == null || player == null)
			return false;

		return player.getUniqueId().toString().equalsIgnoreCase(Pugmas24BalloonEditor.getEditor().getUniqueId().toString());
	}

	public static String getEditorName() {
		return Pugmas24BalloonEditor.getEditor().getNickname();
	}

	public static ItemStack getReplaceBrush() {
		return Pugmas24BlockReplaceBrushMenu.getBrushItem().dyeColor(Pugmas24BalloonEditor.getBrushColor()).build();
	}

	static void giveBrush() {
		Player player = Pugmas24BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		PlayerUtils.giveItem(player, getReplaceBrush());

	}

	static void removeBrush() {
		Player player = Pugmas24BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		PlayerUtils.removeItem(player, Pugmas24BlockReplaceBrushMenu.BRUSH_MATERIAL);
	}

	static void enableFlight() {
		Player player = Pugmas24BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		Pugmas24BalloonEditor.allowedFlight = player.getAllowFlight();
		Pugmas24BalloonEditor.flying = player.isFlying();

		player.setAllowFlight(true);
		player.setFlying(true);
	}

	static void resetFlight() {
		Player player = Pugmas24BalloonEditor.getEditor().getPlayer();
		if (player == null)
			return;

		player.setAllowFlight(Pugmas24BalloonEditor.allowedFlight);
		player.setFlying(Pugmas24BalloonEditor.flying);
	}

	static void removeBrush(Player player) {
		if (player == null)
			return;

		PlayerUtils.removeItem(player, Pugmas24BlockReplaceBrushMenu.BRUSH_MATERIAL);
	}

	static void disableFlight(Player player) {
		if (player == null)
			return;

		player.setAllowFlight(false);
		player.setFlying(false);
	}

	public static void nextTemplate() {
		int id = Pugmas24BalloonEditor.schemId;
		id++;
		if (id > Pugmas24BalloonEditor.TEMPLATE_SIZE)
			id = 1;

		Pugmas24BalloonEditor.selectTemplate(id);
	}

	public static void previousTemplate() {
		int id = Pugmas24BalloonEditor.schemId;
		id--;
		if (id < 1)
			id = Pugmas24BalloonEditor.TEMPLATE_SIZE;

		Pugmas24BalloonEditor.selectTemplate(id);
	}
}
