package gg.projecteden.nexus.features.events.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class Script {
	int delay;
	List<String> lines;

	public static Script wait(String... lines) {
		return wait(0, Arrays.asList(lines));
	}

	public static Script wait(int delay, String... lines) {
		return wait(delay, Arrays.asList(lines));
	}

	public static Script wait(int delay, List<String> lines) {
		return new Script(delay, lines);
	}
}
