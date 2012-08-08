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

	/**
	 * A String that uniquely identifies this end point.
	 * 
	 * @return
	 */
	String getEndPointId();

}
