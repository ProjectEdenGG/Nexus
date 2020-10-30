package me.pugabyte.bncore.features.holidays.halloween20;

import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
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
		taskId = Tasks.repeat(0, Time.SECOND, () -> {
			for (int x = 0; x < 4; x++) {
				for (int y = 0; y < 5; y++) {
					Location original = new Location(Halloween20.getWorld(), 307 - xOff.get(), 59 + y, -1992);
					player.sendBlockChange(original, Material.AIR.createBlockData());
					player.sendBlockChange(original.add(-5, 0, 0), original.getBlock().getBlockData());
				}
			}
			xOff.getAndIncrement();
			if (xOff.get() == 4)
				cancel();
		});
	}

	public void close() {
		AtomicInteger xOff = new AtomicInteger(0);
		taskId = Tasks.repeat(0, Time.SECOND, () -> {
			for (int y = 0; y < 6; y++) {
				Location original = new Location(Halloween20.getWorld(), 298 + xOff.get(), 59 + y, -1992);
				player.sendBlockChange(original, Material.AIR.createBlockData());
				player.sendBlockChange(original.add(5, 0, 0), original.getBlock().getBlockData());
			}
			xOff.getAndIncrement();
			if (xOff.get() == 4)
				cancel();
		});
	}

	public void cancel() {
		Tasks.cancel(taskId);
	}


}
