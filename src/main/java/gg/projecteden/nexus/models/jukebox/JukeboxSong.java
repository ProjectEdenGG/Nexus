package gg.projecteden.nexus.models.jukebox;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static gg.projecteden.nexus.utils.IOUtils.getPluginFolder;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

@Data
@RequiredArgsConstructor
public class JukeboxSong {
	private final Song song;

	public static List<JukeboxSong> SONGS = new ArrayList<>();

	public static JukeboxSong of(String name) {
		return SONGS.stream()
			.filter(song -> song.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null);
	}

	public String getName() {
		if (!isNullOrEmpty(getAuthor()))
			return getAuthor() + " - " + getTitle();
		return getTitle();
	}

	public String getTitle() {
		return song.getTitle();
	}

	public String getAuthor() {
		return song.getAuthor();
	}

	public static CompletableFuture<Void> reload() {
		final CompletableFuture<Void> future = new CompletableFuture<>();
		Tasks.async(() -> {
			SONGS.clear();
			try (Stream<Path> paths = Files.walk(getPluginFolder("jukebox").toPath())) {
				paths.forEach(path -> {
					final String fileName = path.getFileName().toString();
					if (!fileName.contains(".nbs"))
						return;

					SONGS.add(new JukeboxSong(NBSDecoder.parse(path.toFile())));
				});
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			future.complete(null);
		});
		return future;
	}

}
