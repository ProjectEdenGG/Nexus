package me.pugabyte.bncore.features.store;

import lombok.SneakyThrows;
import me.pugabyte.bncore.features.store.annotations.Commands.Command;
import me.pugabyte.bncore.features.store.annotations.ExpirationCommands.ExpirationCommand;
import me.pugabyte.bncore.features.store.annotations.ExpirationDays;
import me.pugabyte.bncore.features.store.annotations.Id;
import me.pugabyte.bncore.features.store.annotations.Permissions.Permission;
import me.pugabyte.bncore.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Package {

	@Id("2589641")
	CUSTOM_DONATION,

	@Id("1922887")
	@Permission("set.my.prefix")
	CUSTOM_PREFIX_LIFETIME,

	@Id("2730030")
	@Permission("set.my.prefix")
	@ExpirationDays(30)
	@ExpirationCommand("/prefix expire [player]")
	CUSTOM_PREFIX_ONE_MONTH,

	@Id("2019251")
	@Permission("automaticinventory.sortinventory")
	@Permission("automaticinventory.sortchests")
	@Permission("automaticinventory.quickdeposit")
	@Permission("automaticinventory.depositall")
	@Permission("automaticinventory.autocraft")
	AUTO_SORT_LIFETIME,

	@Id("2729981")
	@Permission("automaticinventory.sortinventory")
	@Permission("automaticinventory.sortchests")
	@Permission("automaticinventory.quickdeposit")
	@Permission("automaticinventory.depositall")
	@Permission("automaticinventory.autocraft")
	@ExpirationDays(30)
	AUTO_SORT_ONE_MONTH,

	@Id("2965488")
	@Permission("jq.custom")
	CUSTOM_JOIN_QUIT_MESSAGES_LIFETIME,

	@Id("2965489")
	@Permission("jq.custom")
	@ExpirationDays(30)
	CUSTOM_JOIN_QUIT_MESSAGES_ONE_MONTH,

	@Id("2982264")
	@Permission("durabilitywarning.use")
	DURABILITY_WARNING_LIFETIME,

	@Id("2982265")
	@Permission("durabilitywarning.use")
	@ExpirationDays(30)
	DURABILITY_WARNING_ONE_MONTH,

	@Id("3239567")
	@Permission("emoticons.use")
	EMOTES,

	@Id("3218615")
	@Permission("wings.use")
	@Permission("wings.style.*")
	PARTICLE_WINGS,

	@Id("2019259")
	@Command("/permhelper vaults add [player] 1")
	VAULTS,

	@Id("2019261")
	@Command("/permhelper homes add [player] 5")
	FIVE_SETHOMES,

	@Id("2559650")
	@Permission("citizens.help")
	@Permission("citizens.npc.create")
	@Permission("citizens.npc.create.*")
	@Permission("citizens.npc.profession")
	@Permission("citizens.npc.remove")
	@Permission("citizens.npc.rename")
	@Permission("citizens.npc.edit.equip")
	@Permission("citizens.npc.select")
	@Permission("citizens.npc.skin")
	@Permission("citizens.npc.lookclose")
	@Permission("citizens.npc.skeletontype")
	@Permission("citizens.npc.zombiemodifier")
	@Permission("citizens.npc.age")
	@Permission("citizens.npc.tphere")
	@Permission("citizens.npc.type")
	@Permission("citizens.npc.power")
	@Permission("citizens.npc.edit.path")
	@Permission("citizens.npc.edit.text")
	@Command("/permhelper npcs add [player] 1")
	NPC,

	@Id("2019264")
	@Permission("essentials.skull")
	DONOR_SKULL,

	@Id("2496109")
	@Permission("essentials.hat")
	HAT,

	@Id("2019265")
	@Permission("essentials.ptime")
	PTIME,

	@Id("2559439")
	@Permission("itemname.use")
	ITEM_NAME,

	@Id("2495885")
	@Permission("firework.launch")
	FIREWORKS,

	@Id("2678902")
	@Permission("fireworkbow.single")
	FIREWORK_BOW_SINGLE,

	@Id("2678893")
	@Permission("fireworkbow.infinite")
	FIREWORK_BOW_INFINITE,

	@Id("2495909")
	@Command("/permhelper plots add [player] 1")
	CREATIVE_PLOTS,

	@Id("2495900")
	@Permission("rainbowarmour.use")
	RAINBOW_ARMOUR,

	@Id("2886239")
	@Permission("invis.armour")
	INVISIBLE_ARMOUR,

	@Id("2856645")
	@Permission("rainbow.beacon")
	RAINBOW_BEACON,

	@Id("2495867")
	@Permission("pet.type.chicken")
	@Permission("pet.type.chicken.hat")
	@Permission("pet.type.chicken.data.*")
	@Permission("pet.type.cow")
	@Permission("pet.type.cow.hat")
	@Permission("pet.type.cow.data.*")
	@Permission("pet.type.mooshroom")
	@Permission("pet.type.mooshroom.hat")
	@Permission("pet.type.mooshroom.data.*")
	@Permission("pet.type.sheep")
	@Permission("pet.type.sheep.hat")
	@Permission("pet.type.sheep.data.*")
	@Permission("pet.type.pig")
	@Permission("pet.type.pig.hat")
	@Permission("pet.type.pig.data.*")
	@Permission("pet.type.rabbit")
	@Permission("pet.type.rabbit.hat")
	@Permission("pet.type.rabbit.data.*")
	@Permission("pet.type.bee")
	@Permission("pet.type.bee.hat")
	@Permission("pet.type.bee.data.*")
	PETS_FARM,

	@Id("2495869")
	@Permission("pet.type.fox")
	@Permission("pet.type.fox.hat")
	@Permission("pet.type.fox.data.*")
	@Permission("pet.type.ocelot")
	@Permission("pet.type.ocelot.hat")
	@Permission("pet.type.ocelot.data.*")
	@Permission("pet.type.wolf")
	@Permission("pet.type.wolf.hat")
	@Permission("pet.type.wolf.data.*")
	@Permission("pet.type.cat")
	@Permission("pet.type.cat.hat")
	@Permission("pet.type.cat.data.*")
	@Permission("pet.type.parrot")
	@Permission("pet.type.parrot.hat")
	@Permission("pet.type.parrot.data.*")
	@Permission("pet.type.bat")
	@Permission("pet.type.bat.hat")
	@Permission("pet.type.bat.data.*")
	@Permission("pet.type.panda")
	@Permission("pet.type.panda.hat")
	@Permission("pet.type.panda.data.*")
	PETS_CUTIES,

	@Id("2495876")
	@Permission("pet.type.wanderingtrader")
	@Permission("pet.type.wanderingtrader.hat")
	@Permission("pet.type.wanderingtrader.data.*")
	@Permission("pet.type.vex")
	@Permission("pet.type.vex.hat")
	@Permission("pet.type.vex.data.*")
	@Permission("pet.type.villager")
	@Permission("pet.type.villager.hat")
	@Permission("pet.type.villager.data.*")
	@Permission("pet.type.zombievillager")
	@Permission("pet.type.zombievillager.hat")
	@Permission("pet.type.zombievillager.data.*")
	@Permission("pet.type.evoker")
	@Permission("pet.type.evoker.hat")
	@Permission("pet.type.evoker.data.*")
	@Permission("pet.type.illusioner")
	@Permission("pet.type.illusioner.hat")
	@Permission("pet.type.illusioner.data.*")
	@Permission("pet.type.ravager")
	@Permission("pet.type.ravager.hat")
	@Permission("pet.type.ravager.data.*")
	@Permission("pet.type.vindicator")
	@Permission("pet.type.vindicator.hat")
	@Permission("pet.type.vindicator.data.*")
	@Permission("pet.type.pillager")
	@Permission("pet.type.pillager.hat")
	@Permission("pet.type.pillager.data.*")
	PETS_NATIVES,

	@Id("3919092")
	@Permission("pet.type.dolphin")
	@Permission("pet.type.dolphin.hat")
	@Permission("pet.type.dolphin.data.*")
	@Permission("pet.type.squid")
	@Permission("pet.type.squid.hat")
	@Permission("pet.type.squid.data.*")
	@Permission("pet.type.turtle")
	@Permission("pet.type.turtle.hat")
	@Permission("pet.type.turtle.data.*")
	@Permission("pet.type.drowned")
	@Permission("pet.type.drowned.hat")
	@Permission("pet.type.drowned.data.*")
	@Permission("pet.type.guardian")
	@Permission("pet.type.guardian.hat")
	@Permission("pet.type.guardian.data.*")
	@Permission("pet.type.polarbear")
	@Permission("pet.type.polarbear.hat")
	@Permission("pet.type.polarbear.data.*")
	@Permission("pet.type.pufferfish")
	@Permission("pet.type.pufferfish.hat")
	@Permission("pet.type.pufferfish.data.*")
	@Permission("pet.type.cod")
	@Permission("pet.type.cod.hat")
	@Permission("pet.type.cod.data.*")
	@Permission("pet.type.salmon")
	@Permission("pet.type.salmon.hat")
	@Permission("pet.type.salmon.data.*")
	PETS_AQUATIC,

	@Id("2495873")
	@Permission("pet.type.witherskeleton")
	@Permission("pet.type.witherskeleton.hat")
	@Permission("pet.type.witherskeleton.data.*")
	@Permission("pet.type.ghast")
	@Permission("pet.type.ghast.hat")
	@Permission("pet.type.ghast.data.*")
	@Permission("pet.type.blaze")
	@Permission("pet.type.blaze.hat")
	@Permission("pet.type.blaze.data.*")
	@Permission("pet.type.magmacube")
	@Permission("pet.type.magmacube.hat")
	@Permission("pet.type.magmacube.data.*")
	@Permission("pet.type.hoglin")
	@Permission("pet.type.hoglin.hat")
	@Permission("pet.type.hoglin.data.*")
	@Permission("pet.type.piglin")
	@Permission("pet.type.piglin.hat")
	@Permission("pet.type.piglin.data.*")
	@Permission("pet.type.zoglin")
	@Permission("pet.type.zoglin.hat")
	@Permission("pet.type.zoglin.data.*")
	@Permission("pet.type.strider")
	@Permission("pet.type.strider.hat")
	@Permission("pet.type.strider.data.*")
	@Permission("pet.type.pigzombie")
	@Permission("pet.type.pigzombie.hat")
	@Permission("pet.type.pigzombie.data.*")
	PETS_NETHER,

	@Id("2495872")
	@Permission("pet.type.cavespider")
	@Permission("pet.type.cavespider.hat")
	@Permission("pet.type.cavespider.data.*")
	@Permission("pet.type.creeper")
	@Permission("pet.type.creeper.hat")
	@Permission("pet.type.creeper.data.*")
	@Permission("pet.type.skeleton")
	@Permission("pet.type.skeleton.hat")
	@Permission("pet.type.skeleton.data.*")
	@Permission("pet.type.spider")
	@Permission("pet.type.spider.hat")
	@Permission("pet.type.spider.data.*")
	@Permission("pet.type.witch")
	@Permission("pet.type.witch.hat")
	@Permission("pet.type.witch.data.*")
	@Permission("pet.type.zombie")
	@Permission("pet.type.zombie.hat")
	@Permission("pet.type.zombie.data.*")
	@Permission("pet.type.husk")
	@Permission("pet.type.husk.hat")
	@Permission("pet.type.husk.data.*")
	@Permission("pet.type.stray")
	@Permission("pet.type.stray.hat")
	@Permission("pet.type.stray.data.*")
	PETS_MONSTERS,

	@Id("2495871")
	@Permission("pet.type.horse")
	@Permission("pet.type.horse.mount")
	@Permission("pet.type.horse.hat")
	@Permission("pet.type.horse.data.*")
	@Permission("pet.type.skeletonhorse")
	@Permission("pet.type.skeletonhorse.mount")
	@Permission("pet.type.skeletonhorse.hat")
	@Permission("pet.type.skeletonhorse.data.*")
	@Permission("pet.type.zombiehorse")
	@Permission("pet.type.zombiehorse.mount")
	@Permission("pet.type.zombiehorse.hat")
	@Permission("pet.type.zombiehorse.data.*")
	@Permission("pet.type.donkey")
	@Permission("pet.type.donkey.mount")
	@Permission("pet.type.donkey.hat")
	@Permission("pet.type.donkey.data.*")
	@Permission("pet.type.mule")
	@Permission("pet.type.mule.mount")
	@Permission("pet.type.mule.hat")
	@Permission("pet.type.mule.data.*")
	@Permission("pet.type.llama")
	@Permission("pet.type.llama.mount")
	@Permission("pet.type.llama.hat")
	@Permission("pet.type.llama.data.*")
	@Permission("pet.type.traderllama")
	@Permission("pet.type.traderllama.mount")
	@Permission("pet.type.traderllama.hat")
	@Permission("pet.type.traderllama.data.*")
	PETS_MOUNTS,

	@Id("2495870")
	@Permission("pet.type.enderman")
	@Permission("pet.type.enderman.hat")
	@Permission("pet.type.enderman.data.*")
	@Permission("pet.type.endermite")
	@Permission("pet.type.endermite.hat")
	@Permission("pet.type.endermite.data.*")
	@Permission("pet.type.shulker")
	@Permission("pet.type.shulker.hat")
	@Permission("pet.type.shulker.data.*")
	@Permission("pet.type.phantom")
	@Permission("pet.type.phantom.hat")
	@Permission("pet.type.phantom.data.*")
	@Permission("pet.type.silverfish")
	@Permission("pet.type.silverfish.hat")
	@Permission("pet.type.silverfish.data.*")
	@Permission("pet.type.slime")
	@Permission("pet.type.slime.hat")
	@Permission("pet.type.slime.data.*")
	@Permission("pet.type.snowman")
	@Permission("pet.type.snowman.hat")
	@Permission("pet.type.snowman.data.*")
	@Permission("pet.type.irongolem")
	@Permission("pet.type.irongolem.hat")
	@Permission("pet.type.irongolem.data.*")
	PETS_OTHER,

	@Id("2496219")
	@Permission("miniaturepets.pet.BB8")
	@Permission("miniaturepets.pet.Bee")
	@Permission("miniaturepets.pet.Boxer")
	@Permission("miniaturepets.pet.Camera")
	@Permission("miniaturepets.pet.Chimp")
	@Permission("miniaturepets.pet.Duck")
	@Permission("miniaturepets.pet.earth")
	@Permission("miniaturepets.pet.FacebookLogo")
	@Permission("miniaturepets.pet.Frog")
	@Permission("miniaturepets.pet.Giraffe")
	@Permission("miniaturepets.pet.Gorilla")
	@Permission("miniaturepets.pet.hamster")
	@Permission("miniaturepets.pet.InstagramLogo")
	@Permission("miniaturepets.pet.King")
	@Permission("miniaturepets.pet.Koala")
	@Permission("miniaturepets.pet.LionCub")
	@Permission("miniaturepets.pet.Lion")
	@Permission("miniaturepets.pet.Milker")
	@Permission("miniaturepets.pet.Milk")
	@Permission("miniaturepets.pet.MiniMe")
	@Permission("miniaturepets.pet.Moon")
	@Permission("miniaturepets.pet.Pug")
	@Permission("miniaturepets.pet.Panda")
	@Permission("miniaturepets.pet.Penguin")
	@Permission("miniaturepets.pet.PerryThePlatipus")
	@Permission("miniaturepets.pet.PolarBearCub")
	@Permission("miniaturepets.pet.Princess")
	@Permission("miniaturepets.pet.Pug")
	@Permission("miniaturepets.pet.Rapina")
	@Permission("miniaturepets.pet.Robo")
	@Permission("miniaturepets.pet.Snowglobe")
	@Permission("miniaturepets.pet.Soccer")
	@Permission("miniaturepets.pet.Summerglobe")
	@Permission("miniaturepets.pet.Sun")
	@Permission("miniaturepets.pet.TigerCub")
	@Permission("miniaturepets.pet.Tiger")
	@Permission("miniaturepets.pet.Turtle")
	@Permission("miniaturepets.pet.TwitchLogo")
	@Permission("miniaturepets.pet.TwitterLogo")
	@Permission("miniaturepets.pet.YouTubeLogo")
	@Permission("miniaturepets.pet.ZombieHead")
	@Permission("miniaturepets.pet.ZombieStatue")
	@Permission("miniaturepets.pet.ZombieStatueAir")
	@Permission("miniaturepets.pet.footballplayer")
	@Permission("miniaturepets.pet.footballplayermini")
	@Permission("pokeblocks.mob.magifish")
	@Permission("pokeblocks.mob.bulbasaur")
	@Permission("minime.edsheeran")
	@Permission("quickpets.ownerpet")
	@Permission("MinaturePets.Giraffe")
	@Permission("puffle.pet.blue")
	@Permission("minecraft.steve")
	@Permission("MinaturePets.Witherboss")
	@Permission("minaturepets.voltorb")
	@Permission("MiniaturePets.Koala")
	@Permission("pokeblocks.mob.pikachu")
	@Permission("miniaturepets.BearCub")
	@Permission("miniaturepets.Cat")
	@Permission("miniaturepets.Dipper")
	@Permission("miniaturepets.Tech")
	@Permission("miniaturepets.FrostySnowman")
	@Permission("miniaturepets.FrosytFriend")
	@Permission("miniaturepets.GrunkleStan")
	@Permission("miniaturepets.Jake")
	@Permission("miniaturepets.King")
	@Permission("miniaturepets.Mabel")
	@Permission("miniaturepets.Ocelot")
	@Permission("miniaturepets.Princess")
	@Permission("miniaturepets.Squirrel")
	MINIATURE_PETS,

	@Id("2495938")
	@Permission("libsdisguises.disguise.cow.setBaby.setBurning.setArrowsSticking.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.chicken.setArrowsSticking.setBurning.setBaby.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.pig.setSaddled.setArrowsSticking.setBurning.setBaby.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.sheep.setBaby.setColor.setSheared.setArrowsSticking.setBurning.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.mushroom_cow.setArrowsSticking.setBurning.setBaby.setViewSelfDisguise")
	DISGUISES_FARM_ANIMALS,

	@Id("2495940")
	@Permission("libsdisguises.disguise.ocelot.setArrowsSticking.setBurning.setBaby.setType.setSitting.setTamed.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.rabbit.setBaby.setType.setArrowsSticking.setBurning.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.wolf.setBaby.setSneaking.setTamed.setAngry.setBegging.setCollarColor.setArrowsSticking.setBurning.setSitting.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.parrot.setArrowsSticking.setBurning.setViewSelfDisguise.setBaby.setSitting.setTamed.setVariant")
	DISGUISES_DOMESTIC_ANIMALS,

	@Id("2495941")
	@Permission("libsdisguises.disguise.villager.setArrowsSticking.setBurning.setBaby.setProfession.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.iron_golem.setArrowsSticking.setBurning.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.snowman.setArrowsSticking.setBurning.setHat.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.squid.setArrowsSticking.setBurning.setViewSelfDisguise")
	DISGUISES_LEFTOVERS,

	@Id("2495942")
	@Permission("libsdisguises.disguise.mule.setArrowsSticking.setBurning.setBaby.setCarryingChest.setColor.setGrazing.setHorseArmor.setMouthOpen.setRearing.setSaddled.setStyle.setTamed.setVariant.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.horse.setArrowsSticking.setBurning.setBaby.setCarryingChest.setColor.setGrazing.setHorseArmor.setMouthOpen.setRearing.setSaddled.setStyle.setTamed.setVariant.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.donkey.setArrowsSticking.setBurning.setBaby.setCarryingChest.setColor.setGrazing.setHorseArmor.setMouthOpen.setRearing.setSaddled.setStyle.setTamed.setVariant.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.llama.setArrowsSticking.setBurning.setBaby.setCarryingChest.setColor.setGrazing.setCarpet.setMouthOpen.setRearing.setSaddled.setStyle.setTamed.setViewSelfDisguise")
	DISGUISES_RIDABLE_ANIMALS,

	@Id("2495944")
	@Permission("libsdisguises.disguise.zombie.setArrowsSticking.setBurning.setArmor.setItemInMainHand.setItemInOffHand.setAggressive.setBaby.setProfession.setShaking.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.skeleton.setArrowsSticking.setBurning.setArmor.setItemInMainHand.setItemInOffHand.setSwingArms.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.cave_spider.setArrowsSticking.setBurning.setClimbing.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.spider.setArrowsSticking.setBurning.setClimbing.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.creeper.setIgnited.setPowered.setBurning.setArrowsSticking.setBurning.setViewSelfDisguise")
	DISGUISES_MONSTERS,

	@Id("2495945")
	@Permission("libsdisguises.disguise.blaze.setArrowsSticking.setBurning.setBlazing.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.pigman.setArrowsSticking.setBurning.setItemInMainHand.setItemInOffHand.setAggressive.setBaby.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.ghast.setArrowsSticking.setBurning.setAggressive.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.magma_cube.setArrowsSticking.setBurning.setSize.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.wither_skeleton.setArrowsSticking.setBurning.setSwingArms.setItemInMainHand.setItemInOffHand.setArmor.setViewSelfDisguise")
	DISGUISES_NETHER,

	@Id("2495946")
	@Permission("libsdisguises.disguise.enderman.setArrowsSticking.setBurning.setAggressive.setItemInMainHand.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.endermite.setArrowsSticking.setBurning.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.shulker.setArrowsSticking.setBurning.setFacingDirection.setShieldHeight.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.silverfish.setArrowsSticking.setBurning.setViewSelfDisguise")
	DISGUISES_END_CREATURES,

	@Id("2495947")
	@Permission("libsdisguises.disguise.bat.setArrowsSticking.setBurning.setHanging.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.witch.setArrowsSticking.setBurning.setAggressive.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.slime.setArrowsSticking.setBurning.setSize.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.stray.setArrowsSticking.setBurning.setSwingArms.setArmor.setItemInMainHand.setItemInOffHand.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.husk.setArrowsSticking.setBurning.setBaby.setProfession.setShaking.setAggressive.setArmor.setViewSelfDisguise")
	DISGUISES_SPOOKIES,

	@Id("2495948")
	@Permission("libsdisguises.disguise.evoker.setArrowsSticking.setBurning.setSpellTicks.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.vex.setArrowsSticking.setBurning.setAngry.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.vindicator.setArrowsSticking.setBurning.setJohnny.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.illusioner.setArrowsSticking.setBurning.setSpellTicks.setViewSelfDisguise")
	DISGUISES_NEW_GENERATION_MONSTERS;

	@SneakyThrows
	public Field getField() {
		return getClass().getDeclaredField(name());
	}

	public String getId() {
		return getField().getAnnotation(Id.class).value();
	}

	public List<String> getPermissions() {
		return Arrays.stream(getField().getAnnotationsByType(Permission.class))
				.map(Permission::value)
				.collect(Collectors.toList());
	}

	public List<String> getCommands() {
		return Arrays.stream(getField().getAnnotationsByType(Command.class))
				.map(Command::value)
				.map(StringUtils::trimFirst)
				.collect(Collectors.toList());
	}

	public int getExpirationDays() {
		if (getField().getAnnotation(ExpirationDays.class) != null)
			return getField().getAnnotation(ExpirationDays.class).value();
		return -1;
	}

	public List<String> getExpirationCommands() {
		return Arrays.stream(getField().getAnnotationsByType(ExpirationCommand.class))
				.map(ExpirationCommand::value)
				.map(StringUtils::trimFirst)
				.collect(Collectors.toList());
	}

	public static Package getPackage(String id) {
		for (Package value : values())
			if (value.getId().equals(id))
				return value;
		return null;
	}

}
