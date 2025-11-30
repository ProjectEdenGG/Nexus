package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.parchment.sidebar.Sidebar;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pugmas25Sidebar {
	public static final int UPDATE_TICK_INTERVAL = 4;
	private final Map<Player, Pugmas25SidebarLayout> sidebars = new HashMap<>();

	public Pugmas25SidebarLayout createSidebar(Player player) {
		return new Pugmas25SidebarLayout(player);
	}


	public void update() {
		Pugmas25.get().getOnlinePlayers().forEach(player ->
			sidebars.computeIfAbsent(player, $ -> createSidebar(player)));

		sidebars.forEach(((player, layout) ->
			layout.refresh()));
	}

	public void handleJoin(Player player) {
		sidebars.put(player, createSidebar(player));

		Pugmas25SidebarLayout layout = sidebars.get(player);
		Sidebar.get(player).applyLayout(layout);
		layout.start();

		update();
	}

	public void handleQuit(Player player) {
		Pugmas25SidebarLayout layout = sidebars.remove(player);
		if (layout != null)
			layout.stop();

		Sidebar.get(player).applyLayout(null);
		update();
	}

	public void handleEnd() {
		sidebars.forEach((player, scoreboard) ->
			Sidebar.get(player).applyLayout(null));
		sidebars.clear();
	}

	public static final List<String> TITLE_FRAMES = Arrays.asList(
		"&f⛄ &3Pugmas 2025 &f⛄",
		"&f⛄ &3Pugmas 2025 &f⛄",
		"&f⛄ &3Pugmas 2025 &f⛄",
		"&f⛄ &3Pugmas 2025 &f⛄",
		//
		"&f⛄ &bP&3ugmas 2025 &f⛄",
		"&f⛄ &3P&bu&3gmas 2025 &f⛄",
		"&f⛄ &3Pu&bg&3mas 2025 &f⛄",
		"&f⛄ &3Pug&bm&3as 2025 &f⛄",
		"&f⛄ &3Pugm&ba&3s 2025 &f⛄",
		"&f⛄ &3Pugma&bs &32025 &f⛄",
		"&f⛄ &3Pugmas &b2&3025 &f⛄",
		"&f⛄ &3Pugmas 2&b0&325 &f⛄",
		"&f⛄ &3Pugmas 20&b2&35 &f⛄",
		"&f⛄ &3Pugmas 202&b5 &f⛄",
		"&f⛄ &3Pugmas 20&b2&35 &f⛄",
		"&f⛄ &3Pugmas 2&b0&325 &f⛄",
		"&f⛄ &3Pugmas &b2&3025 &f⛄",
		"&f⛄ &3Pugma&bs &32025 &f⛄",
		"&f⛄ &3Pugm&ba&3s 2025 &f⛄",
		"&f⛄ &3Pug&bm&3as 2025 &f⛄",
		"&f⛄ &3Pu&bg&3mas 2025 &f⛄",
		"&f⛄ &3P&bu&3gmas 2025 &f⛄",
		"&f⛄ &bP&3ugmas 2025 &f⛄"
	);
}
