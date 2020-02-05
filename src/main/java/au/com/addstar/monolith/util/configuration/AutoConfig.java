/*
 * Copyright (c) 2020. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package au.com.addstar.monolith.util.configuration;

import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

/**
 * AutoConfiguration System
 * 
 * Allows for simple config file creation and handling.
 * Any class that extends this class can specify its fields to be config values using {@link ConfigField}
 * 
 * You should specify a default value for each field
 * 
 * Valid primitive field types are:
 * <ul>
 *  <li>Short</li>
 *  <li>Integer</li>
 *  <li>Long</li>
 *  <li>Float</li>
 *  <li>Double</li>
 *  <li>Boolean</li>
 *  <li>String</li>
 * </ul>
 * 
 * Valid Complex field types are:
 * <ul>
 *  <li>Array of any primitive type</li>
 *  <li>List of any primitive type</li>
 *  <li>Set of any primitive type</li>
 * </ul>
 * 
 * NOTE: You cannot use an abstract type for the type of a config field. The loader needs to know what class to instantiate.
 * 
 * @author Schmoller, Narimm
 * @version 1.6
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AutoConfig
{
    /**
     * The file to save too and load from
     */
	private File mFile;
	private HashMap<String, String> mCategoryComments;

    public static String getVERSION() {
        return VERSION;
    }

    private static String VERSION = "1.5";
    /**
     * The name of the plugin
     */
	private String pluginName;
    private List<String> description = null;

	protected AutoConfig(File file, String pluginName)
	{
		mFile = file;
		mCategoryComments = new HashMap<>();
		this.pluginName = pluginName;
	}

    public void setDescription(List<String> description) {
        this.description = description;
    }
	
	protected void setCategoryComment(String category, String comment)
	{
		mCategoryComments.put(category, comment);
	}
	
	/**
	 * This should be used to process any data loaded from the config including
	 * performing validation and translation
	 * @throws InvalidConfigurationException Thrown if a field has an invalid value
	 */
	@SuppressWarnings("RedundantThrows")
    protected void onPostLoad() throws InvalidConfigurationException {}
	
	/**
	 * This should be used to update/convert any data in fields for saving
	 */
	protected void onPreSave(){}
	
	private <T> Set<T> newSet(Class<? extends Set<T>> setClass, Collection<T> data) throws InvalidConfigurationException
	{
		Validate.isTrue(!Modifier.isAbstract(setClass.getModifiers()), "You cannot use an abstract type for AutoConfiguration");
		
		Constructor<? extends Set<T>> constructor;
		
		try
		{
			constructor = setClass.getConstructor(Collection.class);
			
			return constructor.newInstance(data);
		}
		catch(Exception e)
		{
			throw new InvalidConfigurationException(e);
		}
	}
	
	private <T> List<T> newList(Class<? extends List<T>> listClass, Collection<T> data) throws InvalidConfigurationException
	{
		Validate.isTrue(!Modifier.isAbstract(listClass.getModifiers()), "You cannot use an abstract type for AutoConfiguration");
		
		Constructor<? extends List<T>> constructor;
		
		try
		{
			constructor = listClass.getConstructor(Collection.class);
			
			return constructor.newInstance(data);
		}
		catch(Exception e)
		{
			throw new InvalidConfigurationException(e);
		}
	}
	
	@SuppressWarnings( "unchecked" )
	public boolean load()
	{
		FileConfiguration yml = new YamlConfiguration();
		try
		{
			// Make sure the file exists
			if(!mFile.exists())
			{
                if(!mFile.getParentFile().mkdirs() || !mFile.createNewFile()){
                        Logger.getLogger(pluginName).warning(mFile.toString() + " could not be"
                              +" created" );
                }
			}
			
			// Parse the config
			yml.load(mFile);
			for(Field field : getClass().getDeclaredFields())
			{
                ConfigField configField = field.getAnnotation(ConfigField.class);
                if(configField == null)
                    continue;
                String path = setupField(configField, field);
				if(!yml.contains(path))
				{
					if(field.get(this) == null)
						throw new InvalidConfigurationException(path + " is required to be set! Info:\n" + configField.comment());  
				}
				else
				{
					// Parse the value
					
					if(field.getType().isArray())
					{
						// Integer
						if(field.getType().getComponentType().equals(Integer.TYPE))
							field.set(this, yml.getIntegerList(path).toArray(new Integer[0]));
						
						// Float
						else if(field.getType().getComponentType().equals(Float.TYPE))
							field.set(this, yml.getFloatList(path).toArray(new Float[0]));
						
						// Double
						else if(field.getType().getComponentType().equals(Double.TYPE))
							field.set(this, yml.getDoubleList(path).toArray(new Double[0]));
						
						// Long
						else if(field.getType().getComponentType().equals(Long.TYPE))
							field.set(this, yml.getLongList(path).toArray(new Long[0]));
						
						// Short
						else if(field.getType().getComponentType().equals(Short.TYPE))
							field.set(this, yml.getShortList(path).toArray(new Short[0]));
						
						// Boolean
						else if(field.getType().getComponentType().equals(Boolean.TYPE))
							field.set(this, yml.getBooleanList(path).toArray(new Boolean[0]));
						
						// String
						else if(field.getType().getComponentType().equals(String.class))
						{
							field.set(this, yml.getStringList(path).toArray(new String[0]));
						}
						else
							throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + " for AutoConfiguration");   //$NON-NLS-2$
					}
					else if(List.class.isAssignableFrom(field.getType()))
					{
						if(field.getGenericType() == null)
							throw new IllegalArgumentException("Cannot use type List without specifying generic type for AutoConfiguration");  
						
						Type type = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
						
						if(type.equals(Integer.class))
							field.set(this, newList((Class<? extends List<Integer>>)field.getType(), yml.getIntegerList(path)));
						else if(type.equals(Float.class))
							field.set(this, newList((Class<? extends List<Float>>)field.getType(), yml.getFloatList(path)));
						else if(type.equals(Double.class))
							field.set(this, newList((Class<? extends List<Double>>)field.getType(), yml.getDoubleList(path)));
						else if(type.equals(Long.class))
							field.set(this, newList((Class<? extends List<Long>>)field.getType(), yml.getLongList(path)));
						else if(type.equals(Short.class))
							field.set(this, newList((Class<? extends List<Short>>)field.getType(), yml.getShortList(path)));
						else if(type.equals(Boolean.class))
							field.set(this, newList((Class<? extends List<Boolean>>)field.getType(), yml.getBooleanList(path)));
						else if(type.equals(String.class))
							field.set(this, newList((Class<? extends List<String>>)field.getType(), yml.getStringList(path)));
						else
							throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + "<" + type.toString() + "> for AutoConfiguration");   //$NON-NLS-2$ //$NON-NLS-2$
					}
					else if(Set.class.isAssignableFrom(field.getType()))
					{
						if(field.getGenericType() == null)
							throw new IllegalArgumentException("Cannot use type set without specifying generic type for AytoConfiguration");
						
						Type type = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
						
						if(type.equals(Integer.class))
							field.set(this, newSet((Class<? extends Set<Integer>>)field.getType(), yml.getIntegerList(path)));
						else if(type.equals(Float.class))
							field.set(this, newSet((Class<? extends Set<Float>>)field.getType(), yml.getFloatList(path)));
						else if(type.equals(Double.class))
							field.set(this, newSet((Class<? extends Set<Double>>)field.getType(), yml.getDoubleList(path)));
						else if(type.equals(Long.class))
							field.set(this, newSet((Class<? extends Set<Long>>)field.getType(), yml.getLongList(path)));
						else if(type.equals(Short.class))
							field.set(this, newSet((Class<? extends Set<Short>>)field.getType(), yml.getShortList(path)));
						else if(type.equals(Boolean.class))
							field.set(this, newSet((Class<? extends Set<Boolean>>)field.getType(), yml.getBooleanList(path)));
						else if(type.equals(String.class))
							field.set(this, newSet((Class<? extends Set<String>>)field.getType(), yml.getStringList(path)));
						else
							throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + "<" + type.toString() + "> for AutoConfiguration");   //$NON-NLS-2$ //$NON-NLS-2$
					}
					else
					{
						// Integer
						if(field.getType().equals(Integer.TYPE))
							field.setInt(this, yml.getInt(path));
						
						// Float
						else if(field.getType().equals(Float.TYPE))
							field.setFloat(this, (float)yml.getDouble(path));
						
						// Double
						else if(field.getType().equals(Double.TYPE))
							field.setDouble(this, yml.getDouble(path));
						
						// Long
						else if(field.getType().equals(Long.TYPE))
							field.setLong(this, yml.getLong(path));
						
						// Short
						else if(field.getType().equals(Short.TYPE))
							field.setShort(this, (short)yml.getInt(path));
						
						// Boolean
						else if(field.getType().equals(Boolean.TYPE))
							field.setBoolean(this, yml.getBoolean(path));
						
						// ItemStack
						else if(field.getType().equals(ItemStack.class))
							field.set(this, yml.getItemStack(path));
						
						// String
						else if(field.getType().equals(String.class))
							field.set(this, yml.getString(path));
						else
							throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + " for AutoConfiguration");   //$NON-NLS-2$
					}
				}
			}
			
			onPostLoad();
			
			return true;
		}
		catch( IOException | IllegalAccessException | IllegalArgumentException | InvalidConfigurationException e )
		{
			e.printStackTrace();
			return false;
		}
	}
	private String setupField(ConfigField configField, Field field){


        String optionName = configField.name();
        if(optionName.isEmpty())
            optionName = field.getName();

        field.setAccessible(true);

        return (configField.category().isEmpty() ? "" : configField.category() + ".") + optionName;
    }
	public boolean save()
	{
		try
		{
			onPreSave();
			
			YamlConfiguration config = new YamlConfiguration();
			// Add all the category comments
			Map<String, String> comments = new HashMap<>(mCategoryComments);
			
			// Add all the values
			for(Field field : getClass().getDeclaredFields())
			{

				ConfigField configField = field.getAnnotation(ConfigField.class);
				if(configField == null)
					continue;
				String path = setupField(configField, field);
				// Ensure the secion exists
				if(!configField.category().isEmpty() && !config.contains(configField.category()))
					config.createSection(configField.category());
				
				if(field.getType().isArray())
				{
					// Integer
					if(field.getType().getComponentType().equals(Integer.TYPE))
						config.set(path, Arrays.asList((Integer[])field.get(this)));
					
					// Float
					else if(field.getType().getComponentType().equals(Float.TYPE))
						config.set(path, Arrays.asList((Float[])field.get(this)));
					
					// Double
					else if(field.getType().getComponentType().equals(Double.TYPE))
						config.set(path, Arrays.asList((Double[])field.get(this)));
					
					// Long
					else if(field.getType().getComponentType().equals(Long.TYPE))
						config.set(path, Arrays.asList((Long[])field.get(this)));
					
					// Short
					else if(field.getType().getComponentType().equals(Short.TYPE))
						config.set(path, Arrays.asList((Short[])field.get(this)));
					
					// Boolean
					else if(field.getType().getComponentType().equals(Boolean.TYPE))
						config.set(path, Arrays.asList((Boolean[])field.get(this)));
					
					// String
					else if(field.getType().getComponentType().equals(String.class))
						config.set(path, Arrays.asList((String[])field.get(this)));
					else
						throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + " for AutoConfiguration");   //$NON-NLS-2$
				}
				else if(List.class.isAssignableFrom(field.getType()))
				{
					if(field.getGenericType() == null)
						throw new IllegalArgumentException("Cannot use type List without specifying generic type for AutoConfiguration");  
					
					Type type = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
					
					if(type.equals(Integer.class) || type.equals(Float.class)
						|| type.equals(Double.class) || type.equals(Long.class)
						|| type.equals(Short.class) || type.equals(Boolean.class)
						|| type.equals(String.class))
					{
						config.set(path, field.get(this));
					}
					else
						throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + "<" + type.toString() + "> for AutoConfiguration");   //$NON-NLS-2$ //$NON-NLS-3$
				}
				else if(Set.class.isAssignableFrom(field.getType()))
				{
					if(field.getGenericType() == null)
						throw new IllegalArgumentException("Cannot use type Set without specifying generic type for AutoConfiguration");  
					
					Type type = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
					
					if(type.equals(Integer.class) || type.equals(Float.class)
						|| type.equals(Double.class) || type.equals(Long.class)
						|| type.equals(Short.class) || type.equals(Boolean.class)
						|| type.equals(String.class))
					{
						config.set(path, new ArrayList<Object>((Set<?>)field.get(this)));
					}
					else
						throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + "<" + type.toString() + "> for AutoConfiguration");   //$NON-NLS-2$ //$NON-NLS-3$
				}
				else
				{
					// Integer
					if(field.getType().equals(Integer.TYPE))
						config.set(path, field.get(this));
					
					// Float
					else if(field.getType().equals(Float.TYPE))
						config.set(path, field.get(this));
					
					// Double
					else if(field.getType().equals(Double.TYPE))
						config.set(path, field.get(this));
					
					// Long
					else if(field.getType().equals(Long.TYPE))
						config.set(path, field.get(this));
					
					// Short
					else if(field.getType().equals(Short.TYPE))
						config.set(path, field.get(this));
					
					// Boolean
					else if(field.getType().equals(Boolean.TYPE))
						config.set(path, field.get(this));
					
					// ItemStack
					else if(field.getType().equals(ItemStack.class))
						config.set(path, field.get(this));
					
					// String
					else if(field.getType().equals(String.class))
						config.set(path, field.get(this));
					else
						throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + " for AutoConfiguration");   //$NON-NLS-2$
				}
				
				// Record the comment
				if(!configField.comment().isEmpty())
					comments.put(path,configField.comment());
			}
			
			StringBuilder output = new StringBuilder(config.saveToString());
			//add a header and description
            String header = "AutoConfig v"+VERSION+" : " + pluginName;
            List<String> reverse = Lists.reverse(description);
            for (String desc:reverse){
                output.insert(0,"# "+ desc);
            }
            output.insert(0,"# " +header);
			// Apply comments
			String category = "";  
			List<String> lines = new ArrayList<>(Arrays.asList(output.toString().split("\n")));
			for(int l = 0; l < lines.size(); l++)
			{
				String line = lines.get(l);
				
				if(line.startsWith("#"))  
					continue;
				
				if(line.trim().startsWith("-"))  
					continue;
				
				if(!line.contains(":"))  
					continue;
				
				String path;  
				line = line.substring(0, line.indexOf(":"));  
				
				if(line.startsWith("  "))  
					path = category + "." + line.substring(2).trim();  
				else
				{
					category = line.trim();
					path = line.trim();
				}
				
				if(comments.containsKey(path))
				{
					String indent = "";  
					for(int i = 0; i < line.length(); i++)
					{
						if(line.charAt(i) == ' ')
							indent += " ";  
						else
							break;
					}
					
					// Add in the comment lines
					String[] commentLines = comments.get(path).split("\n");  
					lines.add(l++, "");  
					for(int i = 0; i < commentLines.length; i++)
					{
						commentLines[i] = indent + "# " + commentLines[i];  
						lines.add(l++,commentLines[i]);
					}
				}
			}
			output = new StringBuilder();
			for(String line : lines)
				output.append(line).append("\n");
			
			FileWriter writer = new FileWriter(mFile);
			writer.write(output.toString());
			writer.close();
			return true;
		}
		catch ( IllegalArgumentException | IOException | IllegalAccessException e )
		{
			e.printStackTrace();
		}
		return false;
	}
}
