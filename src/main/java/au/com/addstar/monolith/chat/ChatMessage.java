package au.com.addstar.monolith.chat;

import net.minecraft.server.v1_7_R4.ChatClickable;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.ChatHoverable;
import net.minecraft.server.v1_7_R4.ChatModifier;
import net.minecraft.server.v1_7_R4.EnumChatFormat;
import net.minecraft.server.v1_7_R4.EnumClickAction;
import net.minecraft.server.v1_7_R4.EnumHoverAction;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.NBTTagCompound;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftStatistic;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ChatMessage
{
	private static final Map<Character, EnumChatFormat> mFormats;

	static
	{
		Builder<Character, EnumChatFormat> builder = ImmutableMap.builder();
		for (EnumChatFormat format : EnumChatFormat.values())
			builder.put(Character.toLowerCase(format.getChar()), format);

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
		return mCurrentModifier = new ChatModifier().setChatClickable(old.h()).a(old.i()).a(old.m());
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
			resetFormat().setColor(mFormats.get(color.getChar()));
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
		mCurrentModifier.a(new ChatHoverable(EnumHoverAction.SHOW_TEXT, Parser.fromStringBasic(text)));
		return this;
	}

	public ChatMessage hover( Achievement achievement )
	{
		mCurrentModifier.a(new ChatHoverable(EnumHoverAction.SHOW_ACHIEVEMENT, new ChatComponentText(CraftStatistic.getNMSAchievement(achievement).name)));
		return this;
	}

	public ChatMessage hover( ItemStack item )
	{
		net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        Validate.notNull(nmsItem, "The item " + item.toString() + " cannot be used in a chat hover");
        NBTTagCompound tag = new NBTTagCompound();
        nmsItem.save(tag);
        mCurrentModifier.a(new ChatHoverable(EnumHoverAction.SHOW_ITEM, new ChatComponentText(tag.toString())));
		return this;
	}

	public ChatMessage hoverOff()
	{
		mCurrentModifier.a((ChatHoverable)null);
		return this;
	}

	public ChatMessage bothOff()
	{
		mCurrentModifier.a((ChatHoverable)null);
		mCurrentModifier.a((ChatHoverable)null);
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
	
	private IChatBaseComponent[] toComponents()
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
	
	@SuppressWarnings( "unchecked" )
	private void toPlain(IChatBaseComponent root, StringBuilder builder)
	{
		for(IChatBaseComponent component : (Iterable<IChatBaseComponent>)root)
		{
			ChatModifier mod = component.getChatModifier();
			
			builder.append(ChatColor.COLOR_CHAR);
			builder.append(mod.a().getChar());
			
			if(mod.b()) // bold
				builder.append(ChatColor.BOLD);
			if(mod.c()) // Italic
				builder.append(ChatColor.ITALIC);
			if(mod.d()) // Strikethrough
				builder.append(ChatColor.STRIKETHROUGH);
			if(mod.e()) // Underline
				builder.append(ChatColor.UNDERLINE);
			if(mod.f()) // Magic
				builder.append(ChatColor.MAGIC);
			
			builder.append(component.c());
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
                case 0: // Chat color
                	message.then(ChatColor.getByChar(match.charAt(1)));
                	break;
                case 1: // Newline
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
