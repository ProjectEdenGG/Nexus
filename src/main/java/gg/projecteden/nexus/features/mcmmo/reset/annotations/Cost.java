package gg.projecteden.nexus.features.mcmmo.reset.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cost {
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Acrobatics { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Alchemy { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Archery { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Axes { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Excavation { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Fishing { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Herbalism { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Mining { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Repair { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Swords { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Taming { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Unarmed { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Woodcutting { int value(); }

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Grandmaster { int value(); }
}


