package gg.projecteden.nexus.models.scoreboard;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.scoreboard.ScoreboardLine;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.parchment.sidebar.Sidebar;
import gg.projecteden.parchment.sidebar.SidebarLayout;
import gg.projecteden.parchment.sidebar.SidebarStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "scoreboard_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@Converters(UUIDConverter.class)
public class ScoreboardUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<ScoreboardLine, Boolean> lines = new ConcurrentHashMap<>();
	private List<ScoreboardLine> order = new ArrayList<>();
	private boolean active = true;

	private transient ListOrderedMap<ScoreboardLine, String> rendered = new ListOrderedMap<>();
	private transient int headerTaskId = -1;
	private transient Map<ScoreboardLine, Integer> taskIds = new ConcurrentHashMap<>();

	public static final int HEADER_UPDATE_INTERVAL = 2;
	public static final int UPDATE_INTERVAL = 40;

	private transient ScoreboardLayout layout;

	public ScoreboardUser(UUID uuid) {
		this.uuid = uuid;
		if (lines.isEmpty() && isOnline())
			lines = ScoreboardLine.getDefaultLines(getOnlinePlayer());
	}

	@PostLoad
	public void fixOrder() {
		if (isOnline())
			for (ScoreboardLine value : ScoreboardLine.values()) {
				if (!order.contains(value) && value.hasPermission(getPlayer()))
					setOrder(value, value.ordinal());
				if (order.contains(value) && !value.hasPermission(getPlayer()))
					order.remove(value);
			}
	}

	public void flushScoreboard() {
		if (this.layout != null)
			this.layout.flush();
	}

	public void on() {
		pause();
		this.layout = new ScoreboardLayout();
		active = true;

		Sidebar.get(getOnlinePlayer()).applyLayout(this.layout);

		this.layout.start();
	}

	public void off() {
		active = false;
		pause();
	}

	public void pause() {
		rendered = new ListOrderedMap<>();
		if (this.layout != null)
			this.layout.stop();
		Sidebar.get(getOnlinePlayer()).applyLayout(null);
	}

	public void setOrder(ScoreboardLine line, int index) {
		order.remove(line);
		if (index >= order.size())
			order.add(line);
		else
			order.add(index, line);
	}

	private int getScore(ScoreboardLine line) {
		List<ScoreboardLine> renderedOrder = new ArrayList<>();
		for (ScoreboardLine toRender : ScoreboardLine.values())
			if (lines.containsKey(toRender) && lines.get(toRender))
				renderedOrder.add(toRender);
		renderedOrder.sort(Comparator.comparingInt(orderedLine -> order.indexOf(orderedLine)));
		return renderedOrder.indexOf(line);
	}

	public class ScoreboardLayout extends SidebarLayout {

		private int taskId;

		private int index;
		private Iterator<String> headerFrames = ScoreboardLine.getHeaderFrames().iterator();
		private boolean flush;

		@Override
		protected void setup(SidebarStage stage) {
			renderHeader(stage);
			renderLines(stage);

			index++;
			if (index == 121)
				index = 0;
		}

		private void renderHeader(SidebarStage stage) {
			if (index % 2 == 0) return;
			if (headerFrames.hasNext())
				stage.setTitle(headerFrames.next());
			else
				headerFrames = ScoreboardLine.getHeaderFrames().iterator();
		}

		private void renderLines(SidebarStage stage) {
			Player player = getPlayer();
			if (player == null || !player.isOnline())
				return;

			for (ScoreboardLine line : ScoreboardLine.values()) {
				if (lines.getOrDefault(line, false)) {
					if (index % line.getInterval() == 0 || flush)
						if (getScore(line) < 15)
							stage.setLine(getScore(line), line.render(player));
				}
			}
			flush = false;
		}

		@Override
		protected void update(SidebarStage stage) {
			this.setup(stage);
		}

		public void stop() {
			Tasks.cancel(this.taskId);
		}

		public void start() {
			this.taskId = Tasks.repeatAsync(1, 1, this::refresh);
		}

		public void flush() {
			this.flush = true;
		}

	}

}
