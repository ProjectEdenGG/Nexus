package me.pugabyte.bncore.utils;

import lombok.Builder;
import me.pugabyte.bncore.BNCore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;

import java.util.List;
import java.util.function.Consumer;

public class Tasks {

	public static int wait(long delay, Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().runTaskLater(BNCore.getInstance(), runnable, delay).getTaskId();
	}

	public static int repeat(long startDelay, long interval, Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(BNCore.getInstance(), runnable, startDelay, interval);
	}

	public static int sync(Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().runTask(BNCore.getInstance(), runnable).getTaskId();
	}

	public static int waitAsync(long delay, Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().runTaskLater(BNCore.getInstance(), () -> async(runnable), delay).getTaskId();
	}

	public static int repeatAsync(long startDelay, long interval, Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(BNCore.getInstance(), runnable, startDelay, interval).getTaskId();
	}

	public static int async(Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().runTaskAsynchronously(BNCore.getInstance(), runnable).getTaskId();
	}

	public static void cancel(int taskId) {
		BNCore.getInstance().getServer().getScheduler().cancelTask(taskId);
	}

	public static class Countdown {
		private int duration;
		private boolean doZero;
		private Consumer<Integer> onTick;
		private Consumer<Integer> onSecond;
		private Runnable onComplete;

		@Builder(buildMethodName = "start")
		public Countdown(int duration, boolean doZero, Consumer<Integer> onTick, Consumer<Integer> onSecond, Runnable onComplete) {
			this.duration = duration;
			this.doZero = doZero;
			this.onTick = onTick;
			this.onSecond = onSecond;
			this.onComplete = onComplete;
			start();
		}

		private int taskId;
		private int ticks;
		private int seconds;

		public void start() {
			if (duration < 0) {
				stop();
				return;
			}

			taskId = repeat(1, 1, () -> {
				if (duration == ticks) {
					if (doZero)
						iteration();

					if (onComplete != null)
						try {
							onComplete.run();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					stop();
					return;
				}

				iteration();
			});
		}

		private void iteration() {
			if (ticks % 20 == 0)
				if (onSecond != null)
					onSecond.accept(((duration / 20) - seconds++));
				else
					++seconds;

			if (onTick != null)
				onTick.accept(duration - ticks++);
			else
				++ticks;
		}

		void stop() {
			cancel(taskId);
		}
	}

	public static class GlowTask {

		@Builder(buildMethodName = "start")
		public GlowTask(int duration, Entity entity, GlowAPI.Color color, Runnable onComplete, List<Player> viewers) {
			GlowAPI.setGlowing(entity, color, viewers);
			Tasks.wait(duration, () -> GlowAPI.setGlowing(entity, false, viewers));
			if (onComplete != null)
				Tasks.wait(duration + 1, onComplete);
		}

	}
}
