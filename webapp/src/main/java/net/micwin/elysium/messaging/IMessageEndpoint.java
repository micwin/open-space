package net.micwin.elysium.messaging;

public interface IMessageEndpoint {

	/**
	 * The message end point type identifier for an avatar.
	 */
	public static final String TYPE_AVATAR = "AVT#";

	/**
	 * The message end point type of a user.
	 */
	public static final String TYPE_USER = "USR#";

	public static final String TYPE_NANITE_GROUP = "NAG#";

	public static final String TYPE_SYSTEM = "SYS#";

	/**
	 * A String that uniquely identifies this end point.
	 * 
	 * @return
	 */
	String getEndPointId();

}