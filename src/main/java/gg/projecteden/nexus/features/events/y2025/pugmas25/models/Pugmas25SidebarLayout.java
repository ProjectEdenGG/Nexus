package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.parchment.sidebar.SidebarLayout;
import gg.projecteden.parchment.sidebar.SidebarStage;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Pugmas25SidebarLayout extends SidebarLayout {
	private final Player player;
	private int taskId;
	private Iterator<String> headerFrames = Pugmas25Sidebar.TITLE_FRAMES.iterator();

	public Pugmas25SidebarLayout(Player player) {
		this.player = player;
	}

	@Override
	protected void setup(SidebarStage stage) {
		renderHeader(stage);
		renderLines(stage);
	}

	private void renderHeader(SidebarStage stage) {
		if (headerFrames.hasNext())
			stage.setTitle(headerFrames.next());
		else
			headerFrames = Pugmas25Sidebar.TITLE_FRAMES.iterator();
	}

	private void renderLines(SidebarStage stage) {
		if (player == null || !player.isOnline())
			return;

		// Clear lines
		for (int i = 0; i < 15; i++) {
			stage.setLine(i, null);
		}

		// Setup lines
		int ndx = 1;
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		for (Pugmas25SidebarLine line : Pugmas25SidebarLine.getGenericLines()) {
			lines.put(line.render(player), ndx++);
		}

		List<Pugmas25SidebarLine> toolLines = Pugmas25SidebarLine.getToolLines().stream()
			.filter(line -> {
				try {
					if (line.canRender(player))
						return true;
				} catch (Exception e) {
					Nexus.log("[Pugmas25] Error when testing sidebar line " + line.name() + " for " + player.getName(), e);
				}
				return false;
			})
			.toList();

		if (!toolLines.isEmpty()) {
			lines.put("&f", ndx++);
			for (Pugmas25SidebarLine line : toolLines) {
				lines.put(line.render(player), ndx++);
			}
		}

		// Set lines
		AtomicInteger lineNum = new AtomicInteger();
		lines.forEach((line, score) -> {
			if (lineNum.get() >= 15)
				return;

			stage.setLine(lineNum.getAndIncrement(), line);
		});
	}

	@Override
	protected void update(SidebarStage stage) {
		setup(stage);
	}

	public void stop() {
		Tasks.cancel(this.taskId);
	}

	public void start() {
		this.taskId = Tasks.repeatAsync(1, Pugmas25Sidebar.UPDATE_TICK_INTERVAL, this::refresh);
	}
}
