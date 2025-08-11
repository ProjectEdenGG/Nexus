package gg.projecteden.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets the permission node required to run a command
 * <p>
 * View the bottom of the source code to this annotation for a common permission node cheatsheet
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	/**
	 * The permission node required to run this command
	 * <p>
	 * View the bottom of the source code to this annotation for a common permission node cheatsheet
	 * @return a permission node
	 */
	String value();

	class Group {
		/**
		 * Admins and Owner
		 */
		public static final String ADMIN = "group.admin";
		/**
		 * Operator and above
		 */
		public static final String SENIOR_STAFF = "group.seniorstaff";
		/**
		 * Mod and above
		 */
		public static final String MODERATOR = "group.moderator";
		/**
		 * Builders & Architects
		 */
		public static final String BUILDER = "group.builder";
		/**
		 * Builder and above
		 */
		public static final String STAFF = "group.staff";
		/**
		 * Guest -> Veteran
		 */
		public static final String NON_STAFF = "group.nonstaff";
	}

	/**
	 * Test if someone has a rank or greater, according to the below hierarchy
	 *
	 *          ladder.owner
	 *          ladder.admin
	 *          ladder.operator
	 *           |         \
	 * ladder.moderator   ladder.architect
	 *          \         ladder.builder
	 *           \         |
	 *          ladder.veteran
	 *          ladder.elite
	 *          ladder.trusted
	 *          ladder.member
	 *          ladder.guest
	 *
	 * Ex:
	 *   Ladder.TRUSTED = trusted and above
	 *   Ladder.BUILDER = builder, architect, operator, admin, owner
	 */
	class Ladder {
		public static final String OWNER = "ladder.owner";
		public static final String ADMIN = "ladder.admin";
		public static final String OPERATOR = "ladder.operator";
		public static final String MODERATOR = "ladder.moderator";
		public static final String ARCHITECT = "ladder.architect";
		public static final String BUILDER = "ladder.builder";
		public static final String VETERAN = "ladder.veteran";
		public static final String ELITE = "ladder.elite";
		public static final String TRUSTED = "ladder.trusted";
		public static final String MEMBER = "ladder.member";
		public static final String GUEST = "ladder.guest";
	}

}
