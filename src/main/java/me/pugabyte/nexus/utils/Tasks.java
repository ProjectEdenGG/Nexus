package me.pugabyte.nexus.utils;

import lombok.Builder;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.inventivetalent.glow.GlowAPI;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Tasks {
	private static final BukkitScheduler scheduler = Nexus.getInstance().getServer().getScheduler();
	private static final Nexus instance = Nexus.getInstance();

	public static int wait(Time delay, Runnable runnable) {
		return wait(delay.get(), runnable);
	}

	public static int wait(long delay, Runnable runnable) {
		if (instance.isEnabled())
			return scheduler.runTaskLater(instance, runnable, delay).getTaskId();
		Nexus.log("Attempted to register wait task while disabled");
		return -1;
	}

	public static int repeat(Time startDelay, long interval, Runnable runnable) {
		return repeat(startDelay.get(), interval, runnable);
	}

	public static int repeat(long startDelay, Time interval, Runnable runnable) {
		return repeat(startDelay, interval.get(), runnable);
	}

	public static int repeat(Time startDelay, Time interval, Runnable runnable) {
		return repeat(startDelay.get(), interval.get(), runnable);
	}

	public static int repeat(long startDelay, long interval, Runnable runnable) {
		if (instance.isEnabled())
			return scheduler.scheduleSyncRepeatingTask(instance, runnable, startDelay, interval);
//		try {
//			throw new InvalidInputException("");
//		} catch (Exception ex) {
//			String fileName = ex.getStackTrace()[1].getFileName();
//			int lineNumber = ex.getStackTrace()[1].getLineNumber();
//			String key = fileName + ":" + lineNumber;
//		}
		Nexus.log("Attempted to register repeat task while disabled");
		return -1;
	}

	public static int sync(Runnable runnable) {
		if (instance.isEnabled())
			return scheduler.runTask(instance, runnable).getTaskId();
		Nexus.log("Attempted to register sync task while disabled");
		return -1;
	}

	public static int waitAsync(Time delay, Runnable runnable) {
		return waitAsync(delay.get(), runnable);
	}

	public static int waitAsync(long delay, Runnable runnable) {
		if (instance.isEnabled())
			return scheduler.runTaskLaterAsynchronously(instance, runnable, delay).getTaskId();
		Nexus.log("Attempted to register waitAsync task while disabled");
		return -1;
	}

	public static int repeatAsync(long startDelay, Time interval, Runnable runnable) {
		return repeatAsync(startDelay, interval.get(), runnable);
	}

	public static int repeatAsync(Time startDelay, long interval, Runnable runnable) {
		return repeatAsync(startDelay.get(), interval, runnable);
	}

	public static int repeatAsync(Time startDelay, Time interval, Runnable runnable) {
		return repeatAsync(startDelay.get(), interval.get(), runnable);
	}

	public static int repeatAsync(long startDelay, long interval, Runnable runnable) {
		if (instance.isEnabled())
			return scheduler.runTaskTimerAsynchronously(instance, runnable, startDelay, interval).getTaskId();
		Nexus.log("Attempted to register repeatAsync task while disabled");
		return -1;
	}

	public static int async(Runnable runnable) {
		if (instance.isEnabled())
			return scheduler.runTaskAsynchronously(instance, runnable).getTaskId();
		Nexus.log("Attempted to register async task while disabled");
		return -1;
	}

	public static void cancel(int taskId) {
		scheduler.cancelTask(taskId);
	}

	public static void cancel(Collection<Integer> taskIds) {
		taskIds.forEach(Tasks::cancel);
	}

	public static class Countdown {
		private final int duration;
		private final boolean doZero;
		private final Consumer<Integer> onTick;
		private final Consumer<Integer> onSecond;
		private final Runnable onStart;
		private final Runnable onComplete;

		@Builder(buildMethodName = "start")
		public Countdown(int duration, boolean doZero, Consumer<Integer> onTick, Consumer<Integer> onSecond, Runnable onStart, Runnable onComplete) {
			this.duration = duration;
			this.doZero = doZero;
			this.onTick = onTick;
			this.onSecond = onSecond;
			this.onStart = onStart;
			this.onComplete = onComplete;
			start();
		}

		@Getter
		private int taskId = -1;
		private int ticks;
		private int seconds;

		public void start() {
			if (duration < 0) {
				stop();
				return;
			}

			if (onStart != null)
				onStart.run();

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

	public static class ExpBarCountdown {
		private final AtomicReference<Countdown> countdown = new AtomicReference<>();

		public int getTaskId() {
			return countdown.get().getTaskId();
		}

		@Builder(buildMethodName = "start")
		public ExpBarCountdown(Player player, int duration, boolean restoreExp) {
			final int level = player.getLevel();
			final float exp = player.getExp();

			countdown.set(Tasks.Countdown.builder()
					.duration(duration)
					.onTick(ticks -> {
						if (!player.isOnline())
							countdown.get().stop();

						int seconds = (ticks / 20) + 1;
						player.setLevel(seconds > 59 ? seconds / 60 : seconds);
						player.setExp((float) ticks / duration);
					})
					.onComplete(() -> {
						if (!player.isOnline())
							countdown.get().stop();

						if (restoreExp) {
							player.setLevel(level);
							player.setExp(exp);
						}
					})
					.start());
		}

	}
}
