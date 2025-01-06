package gg.projecteden.nexus.models.jukebox;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

@Data
@RequiredArgsConstructor
public class JukeboxSong {
	private final String author;
	private final String title;
	private final Song song;

	public static List<JukeboxSong> SONGS = new ArrayList<>();

	public static JukeboxSong of(String name) {
		return SONGS.stream()
			.filter(song -> song.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null);
	}

	public String getName() {
		if (!Nullables.isNullOrEmpty(author))
			return author + " - " + title;
		return title;
	}

	public static CompletableFuture<Void> reload() {
		final CompletableFuture<Void> future = new CompletableFuture<>();
		Tasks.async(() -> {
			SONGS.clear();
			try (Stream<Path> paths = Files.walk(IOUtils.getPluginFolder("jukebox").toPath())) {
				paths.forEach(path -> {
					String fileName = path.getFileName().toString();
					if (!fileName.contains(".nbs"))
						return;

					fileName = fileName.replace(".nbs", "");

					Function<String, String> formatter = string -> string.replaceAll("_", " ").trim();
					final String[] split = fileName.split("=", 2);
					final String author = split.length > 1 ? formatter.apply(split[0]) : null;
					final String title = split.length > 1 ? formatter.apply(split[1]) : formatter.apply(split[0]);
					final Song song = NBSDecoder.parse(path.toFile());
					SONGS.add(new JukeboxSong(author, title, song));
				});
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			SONGS.sort(Comparator.comparing(JukeboxSong::getName));
			future.complete(null);
		});
		return future;
	}

}
