package gg.projecteden.nexus.utils;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.parchment.HasPlayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Tasks {
	private static final BukkitScheduler scheduler = Nexus.getInstance().getServer().getScheduler();
	private static final Nexus instance = Nexus.getInstance();

	public static int wait(TickTime delay, Runnable runnable) {
		return wait(delay.get(), runnable);
	}

	public static int wait(long delay, Runnable runnable) {
		if (instance.isEnabled())
			return scheduler.runTaskLater(instance, runnable, delay).getTaskId();
		Nexus.log("Attempted to register wait task while disabled");
		return -1;
	}

	public static int repeat(TickTime startDelay, long interval, Runnable runnable) {
		return repeat(startDelay.get(), interval, runnable);
	}

	public static int repeat(long startDelay, TickTime interval, Runnable runnable) {
		return repeat(startDelay, interval.get(), runnable);
	}

	public static int repeat(TickTime startDelay, TickTime interval, Runnable runnable) {
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

	public static int waitAsync(TickTime delay, Runnable runnable) {
		return waitAsync(delay.get(), runnable);
	}

	public static int waitAsync(long delay, Runnable runnable) {
		if (instance.isEnabled())
			return scheduler.runTaskLaterAsynchronously(instance, runnable, delay).getTaskId();
		Nexus.log("Attempted to register waitAsync task while disabled");
		return -1;
	}

	public static int repeatAsync(long startDelay, TickTime interval, Runnable runnable) {
		return repeatAsync(startDelay, interval.get(), runnable);
	}

	public static int repeatAsync(TickTime startDelay, long interval, Runnable runnable) {
		return repeatAsync(startDelay.get(), interval, runnable);
	}

	public static int repeatAsync(TickTime startDelay, TickTime interval, Runnable runnable) {
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

	public static void cancel(Integer taskId) {
		if (taskId == null)
			return;

		scheduler.cancelTask(taskId);
	}

	public static void cancel(int taskId) {
		scheduler.cancelTask(taskId);
	}

	public static void cancel(Collection<Integer> taskIds) {
		taskIds.forEach(Tasks::cancel);
	}

	public static boolean isRunning(int id) {
		return scheduler.isCurrentlyRunning(id);
	}

	public static boolean isQueued(int id) {
		return scheduler.isQueued(id);
	}

	public static @NotNull List<BukkitTask> getPending() {
		return scheduler.getPendingTasks();
	}

	public static @NotNull List<BukkitWorker> getActive() {
		return scheduler.getActiveWorkers();
	}

	public static AtomicInteger selfRepeating(TickTime interval, Runnable runnable) {
		return selfRepeating(interval.get(), runnable);
	}

	public static AtomicInteger selfRepeating(long interval, Runnable runnable) {
		final AtomicInteger taskId = new AtomicInteger();

		new AtomicReference<Runnable>() {{
			set(() -> taskId.set(Tasks.wait(interval, () -> {
				runnable.run();
				get().run();
			})));
		}}.get().run();

		return taskId;
	}

	public static AtomicInteger selfRepeatingAsync(TickTime interval, Runnable runnable) {
		return selfRepeatingAsync(interval.get(), runnable);
	}

	public static AtomicInteger selfRepeatingAsync(long interval, Runnable runnable) {
		final AtomicInteger taskId = new AtomicInteger();

		new AtomicReference<Runnable>() {{
			set(() -> taskId.set(Tasks.waitAsync(interval, () -> {
				runnable.run();
				get().run();
			})));
		}}.get().run();

		return taskId;
	}

	public static class Countdown {
		private final long duration;
		private final boolean doZero;
		private final Consumer<Long> onTick;
		private final Consumer<Long> onSecond;
		private final Runnable onStart;
		private final Runnable onComplete;

		@Builder(buildMethodName = "start")
		public Countdown(long duration, boolean doZero, Consumer<Long> onTick, Consumer<Long> onSecond, Runnable onStart, Runnable onComplete) {
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

	public static class ExpBarCountdown {
		private final AtomicReference<Countdown> countdown = new AtomicReference<>();

		public int getTaskId() {
			return countdown.get().getTaskId();
		}

		@Builder(buildMethodName = "start")
		public ExpBarCountdown(HasPlayer player, int duration, boolean restoreExp) {
			Player _player = player.getPlayer();
			final int level = _player.getLevel();
			final float exp = _player.getExp();

			countdown.set(Tasks.Countdown.builder()
					.duration(duration)
					.onTick(ticks -> {
						if (!_player.isOnline())
							countdown.get().stop();

						long seconds = (ticks / 20) + 1;
						_player.setLevel((int) (seconds > 59 ? seconds / 60 : seconds));
						_player.setExp((float) ticks / duration);
					})
					.onComplete(() -> {
						if (!_player.isOnline())
							countdown.get().stop();

						if (restoreExp) {
							_player.setLevel(level);
							_player.setExp(exp);
						}
					})
					.start());
		}
	}

	@Data
	@AllArgsConstructor
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	public static class QueuedTask {
		@EqualsAndHashCode.Include
		private @NonNull UUID uuid;
		@EqualsAndHashCode.Include
		private @NonNull String type;
		private @NonNull Runnable task;
		private boolean completeBeforeShutdown;

		private AtomicInteger taskId;

		@Builder(buildMethodName = "_build")
		public QueuedTask(@NonNull UUID uuid, @NonNull String type, @NonNull Runnable task, boolean completeBeforeShutdown) {
			this(uuid, type, task, completeBeforeShutdown, new AtomicInteger(-1));
		}

		public static class QueuedTaskBuilder {

			public void queue(TickTime delay) {
				queue(delay.get());
			}

			public void queue(long delayTicks) {
				_build().queue(delayTicks);
			}

		}

		public static final Map<QueuedTask, Integer> QUEUE = new ConcurrentHashMap<>();

		public void queue(long delayTicks) {
			Runnable task = () -> {
				synchronized (QUEUE) {
					final Integer expectedTaskId = QUEUE.get(this);
					if (expectedTaskId == null || !expectedTaskId.equals(taskId.get()))
						return;

					QUEUE.remove(this);
					this.task.run();
				}
			};

			if (Bukkit.isPrimaryThread())
				taskId.set(Tasks.wait(delayTicks, task));
			else
				taskId.set(Tasks.waitAsync(delayTicks, task));

			synchronized (QUEUE) {
				final Integer taskId = QUEUE.get(this);
				if (taskId != null)
					Tasks.cancel(taskId);

				QUEUE.put(this, this.taskId.get());
			}
		}

	}

}
