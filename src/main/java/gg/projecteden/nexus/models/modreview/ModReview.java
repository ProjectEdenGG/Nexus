package gg.projecteden.nexus.models.modreview;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "mod_review", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class ModReview implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	@Embedded
	private List<Mod> mods = new ArrayList<>();

	@Embedded
	private List<ModReviewRequest> requests = new ArrayList<>();

	public void add(Mod mod) {
		Optional<Mod> match = findMatch(mod.getName());
		if (match.isPresent())
			throw new InvalidInputException("Entry for &e" + mod.getName() + " &calready exists under " + match.get().getName());

		findRequestMatch(mod.getName()).ifPresent(modReviewRequest -> requests.remove(modReviewRequest));

		mods.add(mod);
		mods.sort(Comparator.comparing(Mod::getName));
	}

	public void request(ModReviewRequest request) {
		Optional<Mod> match = findMatch(request.getName());
		if (match.isPresent())
			throw new InvalidInputException("Mod &e" + request.getName() + " &calready requested for review");

		requests.add(request);
		requests.sort(Comparator.comparing(ModReviewRequest::getName));
	}

	public Optional<Mod> findMatch(String name) {
		return mods.stream()
				.filter(_mod -> _mod.getName().equalsIgnoreCase(name) || _mod.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(name)))
				.findFirst();
	}

	public Optional<ModReviewRequest> findRequestMatch(String name) {
		return requests.stream()
				.filter(_request -> _request.getName().equalsIgnoreCase(name))
				.findFirst();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class Mod {
		@NonNull
		private String name;
		private Set<String> aliases = new HashSet<>();
		@NonNull
		private ModVerdict verdict;
		private String notes;

		public Mod(@NonNull String name, @NonNull ModVerdict verdict, String notes) {
			this.name = name;
			this.verdict = verdict;
			this.notes = notes;
		}

		@Getter
		@AllArgsConstructor
		public enum ModVerdict {
			ALLOWED(ChatColor.GREEN),
			BANNED(ChatColor.RED),
			PARTIAL(ChatColor.GOLD);

			private final ChatColor color;
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class ModReviewRequest {
		@NonNull
		private UUID requester;
		@NonNull
		private String name;
		private String notes;
	}

}
