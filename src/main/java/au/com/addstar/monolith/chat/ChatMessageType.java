package au.com.addstar.monolith.chat;

/**
 * Chat can be send to 3 locations.
 * Standard is your normal chat.
 * System appears just like normal chat, but players can choose to show this while hiding standard chat.
 * ActionBar shows the message above their hotbar.
 */
/**
 * @deprecated use BukkitPlayer#spigot()
 */
@Deprecated
public enum ChatMessageType
{
	Standard,
	System,
	ActionBar
}
