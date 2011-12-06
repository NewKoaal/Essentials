package com.earth2me.essentials.storage;

import com.earth2me.essentials.IEssentials;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;


public abstract class AbstractDelayedYamlFileReader<T extends StorageObject> implements Runnable
{
	private final transient File file;
	private final transient Class<T> clazz;
	private final transient Plugin plugin;

	public AbstractDelayedYamlFileReader(final IEssentials ess, final File file, final Class<T> clazz)
	{
		this.file = file;
		this.clazz = clazz;
		this.plugin = ess;
		ess.scheduleAsyncDelayedTask(this);
	}

	public abstract void onStart();

	@Override
	public void run()
	{
		FileReader reader = null;
		try
		{
			onStart();
			reader = new FileReader(file);
			final T object = new YamlStorageReader(reader, plugin).load(clazz);
			onFinish(object);
		}
		catch (FileNotFoundException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, file.toString(), ex);
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException ex)
			{
				Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

	public abstract void onFinish(T object);
}
