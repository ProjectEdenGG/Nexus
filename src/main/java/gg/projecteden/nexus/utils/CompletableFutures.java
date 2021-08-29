package gg.projecteden.nexus.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

// https://github.com/spotify/completable-futures
public class CompletableFutures {

	@NotNull
	public static <T> CompletableFuture<? extends T>[] toArray(Collection<? extends CompletionStage<? extends T>> stages) {
		return stages.stream().map(CompletionStage::toCompletableFuture).toArray(CompletableFuture[]::new);
	}

	@NotNull
	public static <T> CompletableFuture<Void> joinAll(Stream<? extends CompletionStage<? extends T>> stages) {
		return joinAll(stages.toList());
	}

	@NotNull
	public static <T> CompletableFuture<Void> joinAll(Collection<? extends CompletionStage<? extends T>> stages) {
		return CompletableFuture.allOf(toArray(stages));
	}

	@NotNull
	public static <T> CompletableFuture<List<T>> allOf(Collection<? extends CompletionStage<? extends T>> stages) {
		final CompletableFuture<? extends T>[] all = toArray(stages);

		CompletableFuture<Void> allOf = CompletableFuture.allOf(all);

		for (CompletableFuture<? extends T> future : all)
			future.exceptionally(throwable -> {
				if (!allOf.isDone())
					allOf.completeExceptionally(throwable);
				return null;
			});

		return allOf.thenApply($ -> {
			final List<T> result = new ArrayList<>(all.length);
			for (CompletableFuture<? extends T> completableFuture : all)
				result.add(completableFuture.join());
			return result;
		});
	}

	@NotNull
	public static <U, T> CompletableFuture<Map<U, T>> allOf(Map<U, ? extends CompletionStage<? extends T>> map) {
		final List<U> keys = new ArrayList<>(map.keySet());
		final CompletableFuture<? extends T>[] values = new CompletableFuture[keys.size()];
		for (int i = 0; i < keys.size(); i++)
			values[i] = map.get(keys.get(i)).toCompletableFuture();

		return CompletableFuture.allOf(values).thenApply($ -> {
			final Map<U, T> result = new HashMap<>(values.length);
			for (int i = 0; i < values.length; i++)
				result.put(keys.get(i), values[i].join());
			return result;
		});
	}

}
