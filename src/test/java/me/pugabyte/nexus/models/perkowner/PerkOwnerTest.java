package me.pugabyte.nexus.models.perkowner;

import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.PerkType;
import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class PerkOwnerTest extends PerkOwner {
	public PerkOwnerTest() {
		uuid = UUID.fromString("f84c6a79-0a4e-45e0-879b-cd49ebd4c4e2");
	}
	private boolean saveCalled = false;

	@Override
	protected void save() {saveCalled = true;}

	@Test
	public void rewardTest() {
		assertTrue(reward("Arena"));
		assertThat(getTokenDate(), not(LocalDate.of(1970, 1, 1)));
		assertThat(getTokens(), not(0));
		assertThat(getDailyTokens(), not(0)); // assert their daily tokens isn't 0
		assertTrue(saveCalled);
	}

	@Test
	public void successfulPurchaseTest() {
		setTokens(100);
		assertTrue(purchase(PerkType.UNICORN_HORN));
		assertThat(getPurchasedPerks().keySet(), hasItem(PerkType.UNICORN_HORN));
		assertEquals(getPurchasedPerks().get(PerkType.UNICORN_HORN), false);
		assertEquals(100-PerkType.UNICORN_HORN.getPrice(), getTokens());
		assertTrue(saveCalled);
	}

	@Test
	public void duplicatePurchaseTest() {
		successfulPurchaseTest();
		saveCalled = false;
		assertFalse(purchase(PerkType.UNICORN_HORN));
		assertEquals(100-PerkType.UNICORN_HORN.getPrice(), getTokens());
		assertFalse(saveCalled);
	}

	@Test
	public void insufficientFundsPurchaseTest() {
		assertFalse(purchase(PerkType.UNICORN_HORN));
		assertEquals(0, getTokens());
		assertFalse(getPurchasedPerks().containsKey(PerkType.UNICORN_HORN));
		assertFalse(saveCalled);
	}

	@Test
	public void equalsTest() {
		assertTrue(equals(new PerkOwnerTest()));
	}

	@Test
	public void unsuccessfulToggleTest() {
		assertFalse(toggle(PerkType.UNICORN_HORN));
		assertFalse(saveCalled);
	}

	@Test
	public void successfulToggleTest() {
		getPurchasedPerks().put(PerkType.UNICORN_HORN, false);
		assertTrue(toggle(PerkType.UNICORN_HORN));
		assertTrue(getPurchasedPerks().get(PerkType.UNICORN_HORN));
		assertTrue(saveCalled);
		saveCalled = false;
		assertTrue(toggle(PerkType.UNICORN_HORN));
		assertFalse(getPurchasedPerks().get(PerkType.UNICORN_HORN));
		assertTrue(saveCalled);
	}

	@Test
	public void successfulExclusiveToggleTest() {
		getPurchasedPerks().put(PerkType.UNICORN_HORN, false);
		getPurchasedPerks().put(PerkType.ICE, true);
		getPurchasedPerks().put(PerkType.MUSHROOM, true);
		getPurchasedPerks().put(PerkType.CLOUD, true);
		getPurchasedPerks().put(PerkType.SPRING, true);
		assertTrue(toggle(PerkType.UNICORN_HORN));
		assertTrue(saveCalled);
		assertTrue(getPurchasedPerks().get(PerkType.UNICORN_HORN));
		assertFalse(getPurchasedPerks().get(PerkType.ICE));
		assertFalse(getPurchasedPerks().get(PerkType.MUSHROOM));
		assertTrue(getPurchasedPerks().get(PerkType.CLOUD));
		assertTrue(getPurchasedPerks().get(PerkType.SPRING));
		saveCalled = false;
		assertTrue(toggle(PerkType.UNICORN_HORN));
		assertTrue(saveCalled);
		assertFalse(getPurchasedPerks().get(PerkType.UNICORN_HORN));
	}

	@Test
	public void enabledPerksTest() {
		getPurchasedPerks().put(PerkType.UNICORN_HORN, true);
		getPurchasedPerks().put(PerkType.ICE, false);
		assertTrue(getEnabledPerks().contains(PerkType.UNICORN_HORN));
		assertFalse(getEnabledPerks().contains(PerkType.ICE));
	}

	@Test
	public void enabledPerksByClassTest() {
		getPurchasedPerks().put(PerkType.UNICORN_HORN, true);
		getPurchasedPerks().put(PerkType.ICE, false);
		getPurchasedPerks().put(PerkType.SPRING, true);
		getPurchasedPerks().put(PerkType.CONCRETE_HAT, true);
		Set<LoadoutPerk> enabledPerks = getEnabledPerksByClass(LoadoutPerk.class);
		assertTrue(enabledPerks.contains(PerkType.UNICORN_HORN.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.ICE.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.SPRING.getPerk()));
		assertTrue(enabledPerks.contains(PerkType.CONCRETE_HAT.getPerk()));
	}

	@Test
	public void enabledPerksByCategoryTest() {
		getPurchasedPerks().put(PerkType.UNICORN_HORN, true);
		getPurchasedPerks().put(PerkType.ICE, false);
		getPurchasedPerks().put(PerkType.SPRING, true);
		getPurchasedPerks().put(PerkType.CONCRETE_HAT, true);
		Set<? extends Perk> enabledPerks = getEnabledPerksByCategory(PerkCategory.HAT);
		assertTrue(enabledPerks.contains(PerkType.UNICORN_HORN.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.ICE.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.SPRING.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.CONCRETE_HAT.getPerk()));
	}
}