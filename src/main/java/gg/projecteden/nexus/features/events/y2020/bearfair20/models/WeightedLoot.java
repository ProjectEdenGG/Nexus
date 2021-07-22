package gg.projecteden.nexus.features.events.y2020.bearfair20.models;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

@Getter
public class WeightedLoot {
	@NonNull
	ItemStack itemStack;
	@NonNull
	int weight;
	String regionCheck;
	// True = Day, False = Night, Null = Ignored
	Boolean dayTimeCheck;

	public WeightedLoot(ItemStack itemStack, int weight, String regionCheck, Boolean dayTimeCheck) {
		this.itemStack = itemStack;
		this.weight = weight;
		this.regionCheck = regionCheck;
		this.dayTimeCheck = dayTimeCheck;
	}

	public WeightedLoot(ItemStack itemStack, int weight) {
		this.itemStack = itemStack;
		this.weight = weight;
		this.regionCheck = null;
		this.dayTimeCheck = null;
	}

	public WeightedLoot(ItemStack itemStack, int weight, String regionCheck) {
		this.itemStack = itemStack;
		this.weight = weight;
		this.regionCheck = regionCheck;
		this.dayTimeCheck = null;
	}

	public WeightedLoot(ItemStack itemStack, int weight, Boolean dayTimeCheck) {
		this.itemStack = itemStack;
		this.weight = weight;
		this.regionCheck = null;
		this.dayTimeCheck = dayTimeCheck;
	}

}
