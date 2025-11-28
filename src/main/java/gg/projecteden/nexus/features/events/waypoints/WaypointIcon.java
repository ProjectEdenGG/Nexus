package gg.projecteden.nexus.features.events.waypoints;

import lombok.AllArgsConstructor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;

@AllArgsConstructor
public
enum WaypointIcon {
	DOT("default"),
	BOWTIE("bowtie"),
	X("x"),
	PRESENT("present"),
	;

	private final String location;

	public ResourceKey<WaypointStyleAsset> getAsset() {
		return ResourceKey.create(WaypointStyleAssets.ROOT_ID, ResourceLocation.withDefaultNamespace(location));
	}
}
