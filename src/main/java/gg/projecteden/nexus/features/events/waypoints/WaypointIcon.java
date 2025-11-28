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
	FOOD("food"),
	HEART("heart"),
	HOME("home"),
	MOB("mob"),
	MOUNTAIN("mountain"),
	PICKAXE("pickaxe"),
	PIN("pin"),
	PORTAL("portal"),
	STRONGHOLD("stronghold"),
	SWORD("sword"),
	TREE("tree"),
	;

	private final String location;

	public ResourceKey<WaypointStyleAsset> getAsset() {
		return ResourceKey.create(WaypointStyleAssets.ROOT_ID, ResourceLocation.withDefaultNamespace(location));
	}
}
