package me.pugabyte.nexus.utils;

import eden.utils.TimeUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.server.v1_16_R3.MCUtil.MAIN_EXECUTOR;
import static net.minecraft.server.v1_16_R3.MCUtil.asyncExecutor;

/**
 * Chains together a series of {@link CompletableFuture}s and {@link CompletionStage}s using either
 * Minecraft's main thread or Paper's pool of async threads
 */
@RequiredArgsConstructor
public class CompletableTask<T> {
	protected @NotNull @Getter final CompletableFuture<T> future;

	/**
	 * Returns the result value when complete, or throws an
	 * (unchecked) exception if completed exceptionally. To better
	 * conform with the use of common functional forms, if a
	 * computation involved in the completion of this
	 * CompletableFuture threw an exception, this method throws an
	 * (unchecked) {@link CompletionException} with the underlying
	 * exception as its cause.
	 *
	 * @return the result value
	 * @throws CancellationException if the computation was cancelled
	 * @throws CompletionException if this future completed
	 * exceptionally or a completion computation threw an exception
	 */
	public T join() throws CancellationException, CompletionException {
		return future.join();
	}

	protected <R> CompletableTask<R> newFuture(CompletableFuture<R> future) {
		return new CompletableTask<>(future);
	}

	protected CompletableTask<Void> thenAccept(Consumer<T> consumer, Executor executor) {
		return newFuture(future.thenAcceptAsync(consumer, executor));
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, is executed with this stage's result as the argument
	 * to the supplied action.
	 *
	 * <p>Executes on the main thread.
	 *
	 * <p>See the {@link CompletionStage} documentation for rules
	 * covering exceptional completion.
	 *
	 * @param consumer the action to perform before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final CompletableTask<Void> thenAcceptSync(Consumer<T> consumer) {
		return thenAccept(consumer, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, is executed with this stage's result as the argument
	 * to the supplied action.
	 *
	 * <p>Executes on an async thread.
	 *
	 * <p>See the {@link CompletionStage} documentation for rules
	 * covering exceptional completion.
	 *
	 * @param consumer the action to perform before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final CompletableTask<Void> thenAcceptAsync(Consumer<T> consumer) {
		return thenAccept(consumer, asyncExecutor);
	}

	protected CompletableTask<Void> thenAcceptEither(CompletionStage<T> other, Consumer<T> consumer, Executor executor) {
		return newFuture(future.acceptEitherAsync(other, consumer, executor));
	}

	/**
	 * Returns a new CompletableTask that, when either this or the
	 * other given stage complete normally, is executed with the
	 * corresponding result as argument to the supplied action.
	 *
	 * <p>Executes on the main thread.
	 *
	 * <p>See the {@link CompletionStage} documentation for rules
	 * covering exceptional completion.
	 *
	 * @param other the other CompletionStage
	 * @param consumer the action to perform before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final CompletableTask<Void> thenAcceptEitherSync(CompletionStage<T> other, Consumer<T> consumer) {
		return thenAcceptEither(other, consumer, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask that, when either this or the
	 * other given stage complete normally, is executed with the
	 * corresponding result as argument to the supplied action.
	 *
	 * <p>Executes on an async thread.
	 *
	 * <p>See the {@link CompletionStage} documentation for rules
	 * covering exceptional completion.
	 *
	 * @param other the other CompletionStage
	 * @param consumer the action to perform before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final CompletableTask<Void> thenAcceptEitherAsync(CompletionStage<T> other, Consumer<T> consumer) {
		return thenAcceptEither(other, consumer, asyncExecutor);
	}

	protected <R> CompletableTask<R> thenApply(Function<T, R> function, Executor executor) {
		return newFuture(future.thenApplyAsync(function, executor));
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, is executed with this stage's result as the argument
	 * to the supplied function.
	 *
	 * <p>Executes on the main thread.
	 *
	 * <p>This method is analogous to
	 * {@link java.util.Optional#map Optional.map} and
	 * {@link java.util.stream.Stream#map Stream.map}.
	 *
	 * <p>See the {@link CompletionStage} documentation for rules
	 * covering exceptional completion.
	 *
	 * @param function the function to use to compute the value of the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final <R> CompletableTask<R> thenApplySync(Function<T, R> function) {
		return thenApply(function, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, is executed with this stage's result as the argument
	 * to the supplied function.
	 *
	 * <p>Executes on an async thread.
	 *
	 * <p>This method is analogous to
	 * {@link java.util.Optional#map Optional.map} and
	 * {@link java.util.stream.Stream#map Stream.map}.
	 *
	 * <p>See the {@link CompletionStage} documentation for rules
	 * covering exceptional completion.
	 *
	 * @param function the function to use to compute the value of the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final <R> CompletableTask<R> thenApplyAsync(Function<T, R> function) {
		return thenApply(function, asyncExecutor);
	}

	protected <R> CompletableTask<R> thenHandle(BiFunction<T, Throwable, R> biFunction, Executor executor) {
		return newFuture(future.handleAsync(biFunction, executor));
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * either normally or exceptionally, is executed with this stage's
	 * result and exception as arguments to the supplied function.
	 *
	 * <p>When this stage is complete, the given function is invoked
	 * with the result (or {@code null} if none) and the exception (or
	 * {@code null} if none) of this stage as arguments, and the
	 * function's result is used to complete the returned stage.
	 *
	 * <p>Executes on the main thread.
	 *
	 * @param biFunction the function to use to compute the value of the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final <R> CompletableTask<R> thenHandleSync(BiFunction<T, Throwable, R> biFunction) {
		return thenHandle(biFunction, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * either normally or exceptionally, is executed with this stage's
	 * result and exception as arguments to the supplied function.
	 *
	 * <p>When this stage is complete, the given function is invoked
	 * with the result (or {@code null} if none) and the exception (or
	 * {@code null} if none) of this stage as arguments, and the
	 * function's result is used to complete the returned stage.
	 *
	 * <p>Executes on an async thread.
	 *
	 * @param biFunction the function to use to compute the value of the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final <R> CompletableTask<R> thenHandleAsync(BiFunction<T, Throwable, R> biFunction) {
		return thenHandle(biFunction, asyncExecutor);
	}

	protected CompletableTask<T> exceptionally(Function<Throwable, T> handler, Executor executor) {
		return newFuture(future.exceptionallyAsync(handler, executor));
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * exceptionally, is executed with this stage's exception as the
	 * argument to the supplied function. Otherwise, if this stage
	 * completes normally, then the returned stage also completes
	 * normally with the same value.
	 *
	 * <p>Executes on the main thread.
	 *
	 * @param handler the function to use to compute the value of the
	 * returned CompletableTask if this CompletableTask completed
	 * exceptionally
	 * @return the new CompletableTask
	 */
	public final CompletableTask<T> exceptionallySync(Function<Throwable, T> handler) {
		return exceptionally(handler, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * exceptionally, is executed with this stage's exception as the
	 * argument to the supplied function. Otherwise, if this stage
	 * completes normally, then the returned stage also completes
	 * normally with the same value.
	 *
	 * <p>Executes on an async thread.
	 *
	 * @param handler the function to use to compute the value of the
	 * returned CompletableTask if this CompletableTask completed
	 * exceptionally
	 * @return the new CompletableTask
	 */
	public final CompletableTask<T> exceptionallyAsync(Function<Throwable, T> handler) {
		return exceptionally(handler, asyncExecutor);
	}

	protected CompletableTask<T> complete(Supplier<T> supplier, Executor executor) {
		future.completeAsync(supplier, executor);
		return this;
	}

	/**
	 * Completes this CompletableTask with the result of
	 * the given Supplier function.
	 *
	 * <p>Executes on the main thread.
	 *
	 * @param supplier a function returning the value to be used
	 * to complete this CompletableTask
	 * @return this CompletableTask
	 */
	public final CompletableTask<T> completeSync(Supplier<T> supplier) {
		return complete(supplier, MAIN_EXECUTOR);
	}

	/**
	 * Completes this CompletableTask with the result of
	 * the given Supplier function.
	 *
	 * <p>Executes on an async thread.
	 *
	 * @param supplier a function returning the value to be used
	 * to complete this CompletableTask
	 * @return this CompletableTask
	 */
	public final CompletableTask<T> completeAsync(Supplier<T> supplier) {
		return complete(supplier, asyncExecutor);
	}

	/**
	 * If not already completed, sets the value returned by {@link
	 * #join()} and related methods to the given value.
	 *
	 * <p>If you need to know if the future was not already completed,
	 * use {@link CompletableFuture#complete(Object)
	 * <code>getFuture().complete(T)</code>}
	 *
	 * @param value the result value
	 * @return this CompletableTask
	 */
	public CompletableTask<T> complete(T value) {
		future.complete(value);
		return this;
	}

	protected CompletableTask<T> whenComplete(BiConsumer<T, Throwable> biConsumer, Executor executor) {
		return newFuture(future.whenCompleteAsync(biConsumer, executor));
	}

	/**
	 * Returns a new CompletableTask with the same result or exception as
	 * this stage, that executes the given action when this stage completes.
	 *
	 * <p>When this stage is complete, the given action is invoked
	 * with the result (or {@code null} if none) and the exception (or
	 * {@code null} if none) of this stage as arguments. The returned
	 * stage is completed when the action returns.
	 *
	 * <p>Unlike method {@link #thenHandleSync(BiFunction) #handle},
	 * this method is not designed to translate completion outcomes,
	 * so the supplied action should not throw an exception. However,
	 * if it does, the following rules apply: if this stage completed
	 * normally but the supplied action throws an exception, then the
	 * returned stage completes exceptionally with the supplied
	 * action's exception. Or, if this stage completed exceptionally
	 * and the supplied action throws an exception, then the returned
	 * stage completes exceptionally with this stage's exception.
	 *
	 * <p>Executes on the main thread.
	 *
	 * @param biConsumer the action to perform
	 * @return the new CompletableTask
	 */
	public final CompletableTask<T> whenCompleteSync(BiConsumer<T, Throwable> biConsumer) {
		return whenComplete(biConsumer, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask with the same result or exception as
	 * this stage, that executes the given action when this stage completes.
	 *
	 * <p>When this stage is complete, the given action is invoked
	 * with the result (or {@code null} if none) and the exception (or
	 * {@code null} if none) of this stage as arguments. The returned
	 * stage is completed when the action returns.
	 *
	 * <p>Unlike method {@link #thenHandleAsync(BiFunction) #handle},
	 * this method is not designed to translate completion outcomes,
	 * so the supplied action should not throw an exception. However,
	 * if it does, the following rules apply: if this stage completed
	 * normally but the supplied action throws an exception, then the
	 * returned stage completes exceptionally with the supplied
	 * action's exception. Or, if this stage completed exceptionally
	 * and the supplied action throws an exception, then the returned
	 * stage completes exceptionally with this stage's exception.
	 *
	 * <p>Executes on an async thread.
	 *
	 * @param biConsumer the action to perform
	 * @return the new CompletableTask
	 */
	public final CompletableTask<T> whenCompleteAsync(BiConsumer<T, Throwable> biConsumer) {
		return whenComplete(biConsumer, asyncExecutor);
	}

	protected CompletableTask<Void> runnable(Runnable runnable, Executor executor) {
		return newFuture(future.thenRunAsync(runnable, executor));
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, executes the given action.
	 *
	 * <p>Executes on the main thread.
	 *
	 * <p>See the {@link CompletionStage} documentation for rules
	 * covering exceptional completion.
	 *
	 * @param runnable the action to perform before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final CompletableTask<Void> thenSync(Runnable runnable) {
		return runnable(runnable, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, executes the given action.
	 *
	 * <p>Executes on an async thread.
	 *
	 * <p>See the {@link CompletionStage} documentation for rules
	 * covering exceptional completion.
	 *
	 * @param runnable the action to perform before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public final CompletableTask<Void> thenAsync(Runnable runnable) {
		return runnable(runnable, asyncExecutor);
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, asynchronously waits for the provided duration.
	 *
	 * @param duration how long to wait
	 * @return the new CompletableTask
	 */
	public final CompletableTask<T> delay(Duration duration) {
		return thenApplyAsync(t -> {
			try {
				Thread.sleep(duration.toMillis());
			} catch (InterruptedException ignored) {}
			return t;
		});
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, asynchronously waits for the provided ticks.
	 *
	 * @param ticks how long to wait
	 * @return the new CompletableTask
	 */
	public final CompletableTask<T> delay(long ticks) {
		return delay(ChronoUnit.SECONDS.getDuration().dividedBy(20).multipliedBy(ticks));
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, asynchronously waits for the provided duration.
	 *
	 * @param amount amount of time
	 * @param unit unit of time
	 * @return the new CompletableTask
	 */
	public final CompletableTask<T> delay(long amount, TemporalUnit unit) {
		return delay(unit.getDuration().multipliedBy(amount));
	}

	/**
	 * Returns a new CompletableTask that, when this stage completes
	 * normally, asynchronously waits for the provided duration.
	 *
	 * @param amount amount of time
	 * @param unit unit of time
	 * @return the new CompletableTask
	 */
	public final CompletableTask<T> delay(long amount, TimeUtils.Time unit) {
		return delay(unit.duration(amount));
	}

	// static creators

	protected static @NotNull <T> CompletableTask<T> supply(Supplier<T> supplier, Executor executor) {
		return new CompletableTask<>(CompletableFuture.supplyAsync(supplier, executor));
	}

	/**
	 * Returns a new CompletableTask that is completed on the main
	 * thread with the value obtained by calling the given Supplier.
	 *
	 * @param supplier a function returning the value to be used
	 * to complete the returned CompletableTask
	 * @return the new CompletableTask
	 */
	public static @NotNull <T> CompletableTask<T> supplySync(Supplier<T> supplier) {
		return supply(supplier, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask that is completed on an async
	 * thread with the value obtained by calling the given Supplier.
	 *
	 * @param supplier a function returning the value to be used
	 * to complete the returned CompletableTask
	 * @return the new CompletableTask
	 */
	public static @NotNull <T> CompletableTask<T> supplyAsync(Supplier<T> supplier) {
		return supply(supplier, asyncExecutor);
	}

	protected static @NotNull CompletableTask<Void> run(Runnable runnable, Executor executor) {
		return new CompletableTask<>(CompletableFuture.runAsync(runnable, executor));
	}

	/**
	 * Returns a new CompletableTask that is completed by a task
	 * running in the main thread after it runs the given action.
	 *
	 * @param runnable the action to run before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public static @NotNull CompletableTask<Void> runSync(Runnable runnable) {
		return run(runnable, MAIN_EXECUTOR);
	}

	/**
	 * Returns a new CompletableTask that is completed by a task
	 * running in an async thread after it runs the given action.
	 *
	 * @param runnable the action to run before completing the
	 * returned CompletableTask
	 * @return the new CompletableTask
	 */
	public static @NotNull CompletableTask<Void> runAsync(Runnable runnable) {
		return run(runnable, asyncExecutor);
	}

	/**
	 * Returns a new CompletableTask that is completed with the
	 * provided object.
	 *
	 * @param object object to complete
	 * @return the new CompletableTask
	 */
	public static @NotNull <T> CompletableTask<T> fromObject(T object) {
		CompletableFuture<T> future = new CompletableFuture<>();
		future.complete(object);
		return new CompletableTask<>(future);
	}
}
