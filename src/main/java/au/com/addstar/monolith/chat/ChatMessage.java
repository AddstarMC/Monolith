package au.com.addstar.monolith.chat;

import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftStatistic;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ChatMessage
{
	private static final Map<ChatColor, EnumChatFormat> mFormats;

	static
	{
		Builder<ChatColor, EnumChatFormat> builder = ImmutableMap.builder();
		builder.put(ChatColor.BLACK, EnumChatFormat.BLACK);
		builder.put(ChatColor.DARK_BLUE, EnumChatFormat.DARK_BLUE);
		builder.put(ChatColor.DARK_GREEN, EnumChatFormat.DARK_GREEN);
		builder.put(ChatColor.DARK_AQUA, EnumChatFormat.DARK_AQUA);
		builder.put(ChatColor.DARK_RED, EnumChatFormat.DARK_RED);
		builder.put(ChatColor.DARK_PURPLE, EnumChatFormat.DARK_PURPLE);
		builder.put(ChatColor.GOLD, EnumChatFormat.GOLD);
		builder.put(ChatColor.GRAY, EnumChatFormat.GRAY);
		builder.put(ChatColor.DARK_GRAY, EnumChatFormat.DARK_GRAY);
		builder.put(ChatColor.BLUE, EnumChatFormat.BLUE);
		builder.put(ChatColor.AQUA, EnumChatFormat.AQUA);
		builder.put(ChatColor.GREEN, EnumChatFormat.GREEN);
		builder.put(ChatColor.RED, EnumChatFormat.RED);
		builder.put(ChatColor.LIGHT_PURPLE, EnumChatFormat.LIGHT_PURPLE);
		builder.put(ChatColor.YELLOW, EnumChatFormat.YELLOW);
		builder.put(ChatColor.WHITE, EnumChatFormat.WHITE);

		mFormats = builder.build();
	}

	public static ChatMessage begin()
	{
		return new ChatMessage();
	}

	public static ChatMessage begin( String message )
	{
		return new ChatMessage().then(message);
	}

	public static ChatMessage begin( ChatColor... color )
	{
		return new ChatMessage().then(color);
	}

	private static ChatMessage begin( ChatModifier modifier )
	{
		ChatMessage message = new ChatMessage();
		message.mCurrentModifier = modifier;
		return message;
	}
	
	private ArrayList<IChatBaseComponent> mComponents;
	private IChatBaseComponent mCurrent;
	private ChatModifier mCurrentModifier;

	private ChatMessage()
	{
		mComponents = new ArrayList<IChatBaseComponent>();
		mCurrentModifier = new ChatModifier();
	}

	public ChatMessage then( String message )
	{
		IChatBaseComponent[] components = Parser.fromString(message, mCurrentModifier);

		for (int i = 0; i < components.length; ++i)
		{
			if ( i == 0 )
			{
				if ( mCurrent == null )
				{
					mCurrent = components[i];
					mComponents.add(components[i]);
				}
				else
					mCurrent.addSibling(components[i]);
			}
			else
			{
				mComponents.add(components[i]);
				mCurrent = components[i];
			}
		}

		if ( components.length != 0 )
			mCurrentModifier = components[components.length - 1].getChatModifier().clone();

		return this;
	}
	
	protected ChatMessage thenRaw(String message)
	{
		IChatBaseComponent component = new ChatComponentText(message).setChatModifier(mCurrentModifier);
		
		if ( mCurrent == null )
		{
			mCurrent = component;
			mComponents.add(component);
		}
		else
			mCurrent.addSibling(component);
		
		mCurrentModifier = mCurrentModifier.clone();
		
		return this;
	}
	
	public ChatMessage thenNewline()
	{
		mCurrent = null;
		mCurrentModifier = new ChatModifier();
		return this;
	}

	private ChatModifier resetFormat()
	{
		ChatModifier old = mCurrentModifier;
		return mCurrentModifier = new ChatModifier().setChatClickable(old.h()).setChatHoverable(old.i()).setChatModifier(old.clone());
	}

	private void format(ChatColor color)
	{
		if(color == ChatColor.RESET)
			resetFormat();
		else if(color.isFormat())
		{
			switch(color)
			{
			case BOLD:
				mCurrentModifier.setBold(true);
				break;
			case ITALIC:
				mCurrentModifier.setItalic(true);
				break;
			case STRIKETHROUGH:
				mCurrentModifier.setStrikethrough(true);
				break;
			case UNDERLINE:
				mCurrentModifier.setUnderline(true);
				break;
			case MAGIC:
				mCurrentModifier.setRandom(true);
				break;
			default:
				throw new AssertionError("Unexpected message format");
			}
		}
		else
			resetFormat().setColor(mFormats.get(color));
	}

	public ChatMessage then( ChatColor... color )
	{
		for (ChatColor col : color)
			format(col);

		return this;
	}

	public ChatMessage click( String url )
	{
		mCurrentModifier.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, url));
		return this;
	}

	public ChatMessage click( String command, boolean suggest )
	{
		mCurrentModifier.setChatClickable(new ChatClickable((suggest ? EnumClickAction.SUGGEST_COMMAND : EnumClickAction.RUN_COMMAND), command));
		return this;
	}

	public ChatMessage clickOff()
	{
		mCurrentModifier.setChatClickable(null);
		return this;
	}

	public ChatMessage hover( String text )
	{
		mCurrentModifier.setChatHoverable(new ChatHoverable(EnumHoverAction.SHOW_TEXT, Parser.fromStringBasic(text)));
		return this;
	}

	public ChatMessage hover( Achievement achievement )
	{
		mCurrentModifier.setChatHoverable(new ChatHoverable(EnumHoverAction.SHOW_ACHIEVEMENT, new ChatComponentText(CraftStatistic.getNMSAchievement(achievement).name)));
		return this;
	}

	public ChatMessage hover( ItemStack item )
	{
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        Validate.notNull(nmsItem, "The item " + item.toString() + " cannot be used in a chat hover");
        NBTTagCompound tag = new NBTTagCompound();
        nmsItem.save(tag);
        mCurrentModifier.setChatHoverable(new ChatHoverable(EnumHoverAction.SHOW_ITEM, new ChatComponentText(tag.toString())));
		return this;
	}

	public ChatMessage hoverOff()
	{
		mCurrentModifier.setChatHoverable(null);
		return this;
	}

	public ChatMessage bothOff()
	{
		mCurrentModifier.setChatHoverable(null);
		mCurrentModifier.setChatClickable(null);
		return this;
	}
	
	public void send(CommandSender sender)
	{
		if(sender instanceof Player)
			send((Player)sender);
		else
			sender.sendMessage(toPlain());
	}
	public void send(Player player)
	{
		((CraftPlayer)player).getHandle().sendMessage(toComponents());
	}
	
	public void send(Player player, ChatMessageType type)
	{
		for (IChatBaseComponent component : toComponents())
		{
			// Action bar does not support full json correctly
			if (type == ChatMessageType.ActionBar)
			{
				StringBuilder builder = new StringBuilder();
				toPlain(component, builder);
				
				component = new ChatComponentText(builder.toString());
			}
			
			PacketPlayOutChat chat = new PacketPlayOutChat(component, (byte)type.ordinal());
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(chat);
		}
	}
	
	IChatBaseComponent[] toComponents()
	{
		return mComponents.toArray(new IChatBaseComponent[mComponents.size()]);
	}
	
	private String[] toPlain()
	{
		String[] lines = new String[mComponents.size()];
		int i = 0;
		for(IChatBaseComponent root : mComponents)
		{
			StringBuilder line = new StringBuilder();
			toPlain(root, line);
			lines[i++] = line.toString();
		}
		
		return lines;
	}
	
	private void toPlain(IChatBaseComponent root, StringBuilder builder)
	{
		for(IChatBaseComponent component : (Iterable<IChatBaseComponent>)root)
		{
			ChatModifier mod = component.getChatModifier();
			
			builder.append(mod.getColor().toString());
			
			if(mod.isBold())
				builder.append(ChatColor.BOLD);
			if(mod.isItalic())
				builder.append(ChatColor.ITALIC);
			if(mod.isStrikethrough())
				builder.append(ChatColor.STRIKETHROUGH);
			if(mod.isUnderlined())
				builder.append(ChatColor.UNDERLINE);
			if(mod.isRandom())
				builder.append(ChatColor.MAGIC);
			
			builder.append(component.getText());
		}
	}
	
	private static class Parser
	{
		private static final Pattern mPattern = Pattern.compile("(" + ChatColor.COLOR_CHAR + "[0-9a-fk-or])|(\\n)", Pattern.CASE_INSENSITIVE);
		private static final Pattern mPatternNoNewline = Pattern.compile("(" + ChatColor.COLOR_CHAR + "[0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
		
		public static IChatBaseComponent[] fromString(String text, boolean newline, ChatModifier current)
		{
			Matcher matcher = (newline ? mPattern : mPatternNoNewline).matcher(text);
			ChatMessage message = ChatMessage.begin(current);
			
			int lastPos = 0;
			while (matcher.find())
			{
				int pos = matcher.start();
				
				// Find the current matched part
				int groupId = 0;
				String match;
                while ((match = matcher.group(++groupId)) == null);
                
                // Append any text
                if(pos > lastPos)
                	message.thenRaw(text.substring(lastPos, pos));
                
                // Do formatting/newline stuff
                switch(groupId)
                {
                case 1: // Chat color
                	message.then(ChatColor.getByChar(match.charAt(1)));
                	break;
                case 2: // Newline
                	message.thenNewline();
                	break;
                }
                
                lastPos = matcher.end(groupId);
			}
			
			if(lastPos < text.length())
				message.thenRaw(text.substring(lastPos));
			
			return message.toComponents();
		}
		
		public static IChatBaseComponent[] fromString(String text, ChatModifier current)
		{
			return fromString(text, true, current);
		}
		
		public static IChatBaseComponent fromStringBasic(String text)
		{
			return fromString(text, false, new ChatModifier())[0];
		}
	}
}
