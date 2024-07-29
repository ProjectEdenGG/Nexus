package gg.projecteden.nexus.features.virtualinventories.models.inventories.impl;

import dev.morphia.annotations.Converters;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceCookEvent;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceEndEvent;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceFuelBurnEvent;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceStartEvent;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceTickEvent;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventory;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.features.virtualinventories.models.properties.impl.FurnaceProperties;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

@Data
@NoArgsConstructor
@Converters(ItemStackConverter.class)
public class VirtualFurnace extends VirtualInventory<FurnaceProperties> {
	private ItemStack fuel = null;
	private ItemStack input = null;
	private ItemStack output = null;
	private int cookTime = 0;
	private int cookTimeTotal = 0;
	private int fuelTime = 0;
	private int fuelTimeTotal = 0;

	private float experience = 0f;
	private boolean isLit = false;

	public VirtualFurnace(VirtualInventoryType type) {
		this.type = type;
		this.updateInventory();
	}

	public float extractExperience() {
		float exp = this.experience;
		this.experience = 0.0f;
		return exp;
	}

	@Override
	public void updateInventory() {
		Inventory inv = getInventory();

		inv.setItem(0, this.input);
		inv.setItem(1, this.fuel);
		inv.setItem(2, this.output);
	}

	private void updateInventoryView() {
		if (!this.isOpened())
			return;

		Inventory inv = getInventory();

		input = inv.getItem(0);
		fuel = inv.getItem(1);
		output = inv.getItem(2);

		for (HumanEntity entity : inv.getViewers()) {
			InventoryView view = entity.getOpenInventory();
			view.setProperty(InventoryView.Property.COOK_TIME, cookTime);
			view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_SMELTING, cookTimeTotal);
			view.setProperty(InventoryView.Property.BURN_TIME, fuelTime);
			view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_FUEL, fuelTimeTotal);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.fuelTime > 0) {
			this.fuelTime--;

			if (canCook()) {
				if (!this.isLit)
					new VirtualFurnaceStartEvent(this).callEvent();

				this.isLit = true;
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
		} else {
			this.isLit = false;
			new VirtualFurnaceEndEvent(this).callEvent();
		}

		new VirtualFurnaceTickEvent(this).callEvent();

		if (this.isOpened())
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
		if (!burnEvent.callEvent())
			return;

		this.fuel.subtract();

		int burn = (int) (burnEvent.getBurnTime() / properties().fuelMultiplier());
		this.fuelTime = burn;
		this.fuelTimeTotal = burn;

		updateInventory();
	}

	private boolean canCook() {
		if (Nullables.isNullOrAir(this.input)) {
			return false;
		}

		var recipe = VirtualInventoryUtils.getCookingRecipes(this.input, properties().recipeClass());
		if (recipe == null)
			return false;

		this.cookTimeTotal = (int) (recipe.getCookingTime() / properties().cookMultiplier());
		if (this.output == null)
			return true;

		Material type = this.output.getType();
		if (type == recipe.getResult().getType())
			return this.output.getAmount() < type.getMaxStackSize();

		return false;
	}

	private void processCook() {
		CookingRecipe<?> recipe = VirtualInventoryUtils.getCookingRecipes(this.input, properties().recipeClass());
		if (recipe == null)
			return;

		ItemStack out;
		if (this.output == null) {
			out = recipe.getResult().clone();
		} else {
			out = this.output.clone();
			out.add();
		}

		this.experience += recipe.getExperience();

		VirtualFurnaceCookEvent cookEvent = new VirtualFurnaceCookEvent(this, this.input, out);
		if (!cookEvent.callEvent())
			return;

		this.output = cookEvent.getResult();
		this.input.subtract();

		updateInventory();
	}

}
