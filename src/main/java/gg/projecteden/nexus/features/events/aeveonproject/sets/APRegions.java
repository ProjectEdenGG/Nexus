package gg.projecteden.nexus.features.events.aeveonproject.sets;

public class APRegions {

	public APRegions() {

	}

	// Lobby
	private static final String lobby = APSetType.LOBBY.get().getRegion();
	public static String lobby_shipColor = lobby + "_shipcolor";

	// Sialia
	private static final String sialia = APSetType.SIALIA.get().getRegion();
	public static String sialia_shipColor = sialia + "_shipcolor";
	public static String sialia_dockingport_1 = sialia + "_dockingport_1";
	public static String sialia_dockingport_2 = sialia + "_dockingport_2";

	// Sialia Crashing
	private static final String sialiaCrashing = APSetType.SIALIA_CRASHING.get().getRegion();
	public static String sialiaCrashing_shipColor = sialiaCrashing + "_shipcolor";
	public static String sialiaCrashing_dockingport_1 = sialiaCrashing + "_dockingport_1";
	public static String sialiaCrashing_dockingport_2 = sialiaCrashing + "_dockingport_2";
	public static String sialiaCrashing_vent_door = sialiaCrashing + "_vent_door";

	// Sialia Wreckage
	private static final String sialiaWreckage = APSetType.SIALIA_WRECKAGE.get().getRegion();
	public static String sialiaWreckage_shipColor = sialiaWreckage + "_shipcolor";

	// Vespyr
	private static final String vespyr = APSetType.VESPYR.get().getRegion();
	public static String vespyr_shipColor = vespyr + "_shipcolor";


}
