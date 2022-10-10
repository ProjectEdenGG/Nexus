package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events.VirtualFurnaceCookEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events.VirtualFurnaceFuelBurnEvent;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.UUID;

@Getter
public class VirtualFurnace extends VirtualInventory {
	private final FurnaceProperties furnaceProperties;
	private ItemStack fuel = null;
	private ItemStack input = null;
	private ItemStack output = null;
	private int cookTime = 0;
	private int cookTimeTotal = 0;
	private int fuelTime = 0;
	private int fuelTimeTotal = 0;
	private final Inventory inventory;
	private float experience = 0f;

	public VirtualFurnace(String title, FurnaceProperties properties) {
		super(VirtualInventoryType.FURNACE, title, UUID.randomUUID());
		this.furnaceProperties = properties;
		this.inventory = Bukkit.createInventory(null, InventoryType.FURNACE, StringUtils.colorize(title));
		this.updateInventory();
	}

	public float extractExperience() {
		float exp = this.experience;
		this.experience = 0.0f;
		return exp;
	}

	@Override
	public void openInventory(Player player) {
		updateInventory();
		player.openInventory(this.inventory);
	}

	private void updateInventory() {
		this.inventory.setItem(0, this.input);
		this.inventory.setItem(1, this.fuel);
		this.inventory.setItem(2, this.output);
	}

	private void updateInventoryView() {
		ItemStack input = this.inventory.getItem(0);
		if (this.input != input)
			this.input = input;

		ItemStack fuel = this.inventory.getItem(1);
		if (this.fuel != fuel)
			this.fuel = fuel;

		ItemStack output = this.inventory.getItem(2);
		if (this.output != output)
			this.output = output;

		for (HumanEntity entity : this.inventory.getViewers()) {
			InventoryView view = entity.getOpenInventory();
			view.setProperty(InventoryView.Property.COOK_TIME, this.cookTime);
			view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_SMELTING, this.cookTimeTotal);
			view.setProperty(InventoryView.Property.BURN_TIME, this.fuelTime);
			view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_FUEL, this.fuelTimeTotal);
		}
	}

	@Override
	public void tick() {
		if (this.fuelTime > 0) {
			this.fuelTime--;

			if (canCook()) {
				this.cookTime++;
				if (this.cookTime >= this.cookTimeTotal) {
					this.cookTime = 0;
					processCook();
				}
			} else
				this.cookTime = 0;

		} else if (canBurn() && canCook()) {
			processBurn();

		} else if (this.cookTime > 0) {
			if (canCook())
				this.cookTime -= 5;
			else
				this.cookTime = 0;
		}

		updateInventoryView();
	}

	private boolean canBurn() {
		if (this.fuel == null) {
			return false;
		}

		return ItemUtils.getBurnTime(fuel) > 0;
	}

	private void processBurn() {
		VirtualFurnaceFuelBurnEvent burnEvent = new VirtualFurnaceFuelBurnEvent(this, this.fuel);
		if (!burnEvent.callEvent()) {
			return;
		}

		this.fuel.subtract();

		int burn = (int) (burnEvent.getBurnTime() / furnaceProperties.getFuelMultiplier());
		this.fuelTime = burn;
		this.fuelTimeTotal = burn;

		updateInventory();
	}

	private @Nullable FurnaceRecipe getFurnaceRecipe(ItemStack ingredient) {
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		while (iter.hasNext()) {
			Recipe recipe = iter.next();
			if (recipe instanceof FurnaceRecipe furnaceRecipe) {
				if (ItemUtils.isFuzzyMatch(furnaceRecipe.getInput(), ingredient))
					return furnaceRecipe;
			}
		}

		return null;
	}

	private boolean canCook() {
		if (Nullables.isNullOrAir(this.input)) {
			return false;
		}

		FurnaceRecipe furnaceRecipe = getFurnaceRecipe(this.input);
		if (furnaceRecipe == null) {
			return false;
		}

		this.cookTimeTotal = (int) (furnaceRecipe.getCookingTime() / furnaceProperties.getCookMultiplier());
		if (this.output == null) {
			return true;
		}

		Material type = this.output.getType();
		if (type == furnaceRecipe.getResult().getType()) {
			return this.output.getAmount() < type.getMaxStackSize();
		}

		return false;
	}

	private void processCook() {
		FurnaceRecipe furnaceRecipe = getFurnaceRecipe(this.input);
		if (furnaceRecipe == null)
			return;

		ItemStack out;
		if (this.output == null) {
			out = furnaceRecipe.getResult().clone();
		} else {
			out = this.output.clone();
			out.add();
		}

		this.experience += furnaceRecipe.getExperience();

		VirtualFurnaceCookEvent cookEvent = new VirtualFurnaceCookEvent(this, this.input, out);
		if (!cookEvent.callEvent())
			return;

		this.output = cookEvent.getResult();
		this.input.subtract();

		updateInventory();
	}

	@Override
	public String toString() {
		return "Furnace{" +
			"name=" + getTitle() +
			", uuid=" + getUuid().toString() +
			", properties=" + furnaceProperties.toString() +
			", fuel=" + (fuel == null ? "null" : fuel) +
			", input=" + (input == null ? "null" : input) +
			", output=" + (output == null ? "null" : output) +
			", cookTime=" + cookTime +
			", fuelTime=" + fuelTime +
			'}';
	}


}
