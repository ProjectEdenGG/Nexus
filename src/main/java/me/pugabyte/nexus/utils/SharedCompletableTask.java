package me.pugabyte.nexus.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * An expansion of {@link CompletableTask} which allows sharing builders to easily create tasks
 * that can wait for certain actions in other parts of code.
 */
public class SharedCompletableTask<T> extends CompletableTask<T> {
	protected final @NotNull String name;
	public SharedCompletableTask(@NotNull CompletableFuture<T> future, @NotNull String name) {
		this(future, name, true);
	}

	private SharedCompletableTask(@NotNull CompletableFuture<T> future, @NotNull String name, boolean saveOnInstantiation) {
		super(future);
		Validate.notNull(name, "name cannot be null");
		this.name = name;
		if (saveOnInstantiation)
			save(this);
	}

	private static final Map<String, SharedCompletableTask<?>> TASKS = new HashMap<>();

	public static CompletableTask<?> getTask(String name) {
		synchronized (TASKS) {
			TASKS.computeIfAbsent(name, $ -> new SharedCompletableTask<>(CompletableFuture.runAsync(()->{}), name, false));
			return TASKS.get(name);
		}
	}

	@Override
	protected <R> CompletableTask<R> newFuture(CompletableFuture<R> future) {
		return new SharedCompletableTask<>(future, name);
	}

	protected static <T> CompletableTask<T> save(SharedCompletableTask<T> task) {
		synchronized (TASKS) {
			TASKS.put(task.name, task);
			return task;
		}
	}

	// static creators

	protected static @NotNull <T> CompletableTask<T> supply(String name, Supplier<T> supplier, Executor executor) {
		return new SharedCompletableTask<>(CompletableFuture.supplyAsync(supplier, executor), name);
	}

	/**
	 * Returns a new CompletableTask that is completed on the main
	 * thread with the value obtained by calling the given Supplier.
	 *
	 * @param supplier a function returning the value to be used
	 * to complete the returned CompletableTask
	 * @return the new CompletableTask
	 */
	public static @NotNull <T> CompletableTask<T> supplySync(String name, Supplier<T> supplier) {
		return supply(name, supplier, Bukkit.getServer().getMainExecutor());
	}

	/**
	 * Returns a new CompletableTask that is completed on an async
	 * thread with the value obtained by calling the given Supplier.
	 *
	 * @param supplier a function returning the value to be used
	 * to complete the returned CompletableTask
	 * @return the new CompletableTask
	 */
	public static @NotNull <T> CompletableTask<T> supplyAsync(String name, Supplier<T> supplier) {
		return supply(name, supplier, Bukkit.getServer().getAsyncExecutor());
	}

	protected static @NotNull CompletableTask<Void> run(String name, Runnable runnable, Executor executor) {
		return new SharedCompletableTask<>(CompletableFuture.runAsync(runnable, executor), name);
	}

	/**
	 * Returns a new CompletableTask that is completed by a task
	 * running in the main thread after it runs the given action.
	 *
	 * @param runnable the action to run before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public static @NotNull CompletableTask<Void> runSync(String name, Runnable runnable) {
		return run(name, runnable, Bukkit.getServer().getMainExecutor());
	}

	/**
	 * Returns a new CompletableTask that is completed by a task
	 * running in an async thread after it runs the given action.
	 *
	 * @param runnable the action to run before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public static @NotNull CompletableTask<Void> runAsync(String name, Runnable runnable) {
		return run(name, runnable, Bukkit.getServer().getAsyncExecutor());
	}

	/**
	 * Returns a new CompletableTask that is completed with the
	 * provided object.
	 *
	 * @param object object to complete
	 * @return the new CompletableTask
	 */
	public static @NotNull <T> CompletableTask<T> fromObject(String name, T object) {
		CompletableFuture<T> future = new CompletableFuture<>();
		future.complete(object);
		return new SharedCompletableTask<>(future, name);
	}
}
