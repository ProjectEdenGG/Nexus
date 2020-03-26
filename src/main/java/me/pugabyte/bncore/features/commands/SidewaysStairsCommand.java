package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Camaros
 */

@Aliases("sws")
@NoArgsConstructor
public class SidewaysStairsCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("SidewaysStairs");
	static Map<Player, SidewaysStairsPlayer> playerData = new HashMap<>();

	private SidewaysStairsPlayer swsPlayer;

	SidewaysStairsCommand(CommandEvent event) {
		super(event);
		if (!playerData.containsKey(player()))
			playerData.put(player(), new SidewaysStairsPlayer(player()));

		swsPlayer = playerData.get(player());
	}

	@Path
	void help() {
		send(PREFIX + "Commands:");
		send("&c/sws toggle &7- Turn SWS on or off");
		send("&c/sws angle <number> &7- Set the angle of the stairs to be placed (0-7)");
		send("&c/sws rotate &7- Rotate the stair angle");
		send("&c/sws copy &7- Copy the angle of an existing stair");
		send("&c/sws upsidedown [true|false] &7- Toggle upsidedown stairs");
	}

	@Path("toggle")
	void toggle() {
		toggle(!swsPlayer.isEnabled());
	}

	@Path("<true|false>")
	void toggle(boolean enable) {
		swsPlayer.setEnabled(enable);
		send(PREFIX + (swsPlayer.isEnabled() ? "Enabled" : "Disabled"));
	}

	@Path("(set|angle|setangle)")
	void setAngle() {
		send(PREFIX + "/sws angle <number> &7- Set the angle of the stairs to be placed (0-7)");
	}

	@Path("(set|angle|setangle) <angle>")
	void setAngle(byte angle) {
		swsPlayer.trySetAngle(angle);
		swsPlayer.setAction("set_angle");
		swsPlayer.setEnabled(true);
		send(PREFIX + "Angle successfully set to " + angle);
	}

	@Path("copy")
	void copy() {
		swsPlayer.setAction("copy");
		send(PREFIX + "Right click a stair block to copy its angle.");
	}

	@Path("rotate")
	void rotate() {
		swsPlayer.setEnabled(true);
		send(PREFIX + "Angle changed to " + swsPlayer.rotate());
	}

	@Path("upsidedown")
	void upsideDown() {
		upsideDown(swsPlayer.getAction().equals("disable_upsidedown_placement"));
	}

	@Path("upsidedown <true|false>")
	void upsideDown(boolean allow) {
		swsPlayer.setAction(allow ? "" : "disable_upsidedown_placement");
		swsPlayer.setEnabled(!allow);
		send(PREFIX + "Upsidedown stair placement " + (allow ? "enabled" : "disabled."));
	}

	@EventHandler
	public void onStairInteract(PlayerInteractEvent event) {

		if (event.getAction() == null || event.getHand() == null) return;
		if (event.getHand().equals(EquipmentSlot.HAND)) return;
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

			Block block = event.getClickedBlock();
			String blockName = block.getType().toString();
			Player player = event.getPlayer();
			SidewaysStairsPlayer swsPlayer = playerData.get(player);

			if (swsPlayer != null && swsPlayer.isEnabled())
				if (swsPlayer.getAction().equals("copy"))
					if (blockName.toLowerCase().endsWith("stairs")) {
						swsPlayer.setAction("set_angle");
						swsPlayer.setAngle(block.getData());
						swsPlayer.setEnabled(true);
						player.sendMessage(PREFIX + "Angle succesfully copied (" + block.getData() + ")");
					} else
						player.sendMessage(PREFIX + "Can only copy angle of a stair block.");
		}
	}

	@EventHandler
	public void onStairPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		String blockName = block.getType().toString();
		SidewaysStairsPlayer swsPlayer = playerData.get(player);

		if (swsPlayer != null && swsPlayer.isEnabled())
			if (swsPlayer.getAction().equals("set_angle"))
				if (blockName.toLowerCase().endsWith("stairs"))
					block.setData(swsPlayer.getAngle());
			else if (swsPlayer.getAction().equals("disable_upsidedown_placement"))
				if ((int) (block.getData()) > 3)
					block.setData((byte) (block.getData() - 4));
	}

	public class SidewaysStairsPlayer {
		private final Player player;
		private boolean enabled = false;
		private String action = "";
		private byte angle = 0;

		public SidewaysStairsPlayer(Player player) {
			this.player = player;
		}

		public Player getPlayer() {
			return player;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public byte getAngle() {
			return angle;
		}

		public void setAngle(byte angle) {
			this.angle = angle;
		}

		public void trySetAngle(byte angle) {
			if (angle > 7 || angle < 0)
				throw new InvalidInputException("Invalid angle (Must be a number between 0-7)");

			this.angle = angle;
		}

		public int rotate() {
			this.angle = (byte) ((angle + 1) % 8);
			return angle;
		}

	}

}
