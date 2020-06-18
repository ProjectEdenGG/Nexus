package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import lombok.SneakyThrows;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

public interface Island {
	default String getRegion() {
		return BearFair20.getRegion() + "_" + getClass().getAnnotation(Region.class).value();
	}

	@SneakyThrows
	default TalkingNPC getNPC(int id) {
		Class<? extends Enum<? extends TalkingNPC>> npcGroup = getNpcGroup();
		if (npcGroup != null) {
			Method getFromId = npcGroup.getDeclaredMethod("getFromId", Integer.class);
			return (TalkingNPC) getFromId.invoke(null, id);
		}

		return null;
	}

	default Class<? extends Enum<? extends TalkingNPC>> getNpcGroup() {
		return getClass().getAnnotation(NPCClass.class).value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@interface NPCClass {
		Class<? extends Enum<? extends TalkingNPC>> value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Region {
		String value();
	}

}
