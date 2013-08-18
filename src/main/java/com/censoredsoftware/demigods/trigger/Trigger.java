package com.censoredsoftware.demigods.trigger;

import com.censoredsoftware.demigods.trigger.fair.DivinityUnbalanced;
import com.censoredsoftware.demigods.trigger.fair.NewPlayerNeedsHelp;

public interface Trigger
{
	public boolean evaluate();

	public interface Process
	{
		public void sync();

		public void async();
	}

	public Process process();

	public static class Util
	{
		/**
		 * List of all triggers.
		 */
		public static Trigger[] getAll()
		{
			return new Trigger[] { DivinityUnbalanced.trigger, NewPlayerNeedsHelp.trigger };
		}
	}
}
