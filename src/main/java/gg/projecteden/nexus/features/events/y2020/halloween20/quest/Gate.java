package gg.projecteden.nexus.features.events.y2020.halloween20.quest;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2020.halloween20.Halloween20;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class Gate {

	public Player player;
	public int taskId;

	public Gate(Player player) {
		this.player = player;
	}

	public void open() {
		AtomicInteger xOff = new AtomicInteger(0);
		taskId = Tasks.repeat(0, TickTime.SECOND, () -> {
			for (int y = 0; y < 5; y++) {
				Location original = new Location(Halloween20.getWorld(), 307 - xOff.get(), 59 + y, -1992);
				player.sendBlockChange(original, Material.AIR.createBlockData());
				//player.sendBlockChange(original.add(-5, 0, 0), original.getBlock().getBlockData());
			}
			player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1f, 1f);
			xOff.getAndIncrement();
			if (xOff.get() == 4) {
				cancel();
			}
		});
	}

	public void close() {
		AtomicInteger xOff = new AtomicInteger(0);
		taskId = Tasks.repeat(0, TickTime.SECOND, () -> {
			for (int y = 0; y < 6; y++) {
				Location original = new Location(Halloween20.getWorld(), 298 + xOff.get(), 59 + y, -1992);
				//player.sendBlockChange(original, Material.AIR.createBlockData());
				player.sendBlockChange(original.add(5, 0, 0), original.getBlock().getBlockData());
			}
			player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1f, 1f);
			xOff.getAndIncrement();
			if (xOff.get() == 5)
				cancel();
		});
	}

	public void teleportOut() {
		player.teleportAsync(new Location(Halloween20.getWorld(), 305, 58, -1995));
	}

	public void teleportIn() {
		player.teleportAsync(new Location(Halloween20.getWorld(), 305, 59, -1990));
	}

	public void cancel() {
		Tasks.cancel(taskId);
	}

}
