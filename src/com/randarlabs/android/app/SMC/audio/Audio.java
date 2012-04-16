package com.randarlabs.android.app.SMC.audio;

/***************************************************************************
 * audio.cpp  -  Audio Engine
 * Audio.java - Randall D. Rowland Jr.
 * Copyright (C) 2003 - 2009 Florian Richter
 ***************************************************************************/
/*
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

//#include "../audio/audio.h"
//#include "../core/game_core.h"
import com.randarlabs.android.app.SMC.core.*;
//#include "../level/level.h"
import com.randarlabs.android.app.SMC.level.*;
//#include "../overworld/overworld.h"
import com.randarlabs.android.app.SMC.overworld.*;
//#include "../user/preferences.h"
import com.randarlabs.android.app.SMC.user.*;
//#include "../core/i18n.h"
//#include "../core/filesystem/filesystem.h"
import com.randarlabs.android.app.SMC.core.filesystem.*;
//#include "../core/globals.h"
//#include "../audio/sound_manager.h"
import com.randarlabs.android.app.SMC.audio.*;

public class Audio {

	//namespace SMC //held over from C++
	//{

	/* *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** */

	/* *** *** *** *** *** *** *** Sound Resource ID's  *** *** *** *** *** *** *** *** *** *** */

	// sounds which shouldn't be played multiple times at the same time
	public enum AudioChannel
	{
		RID_MARYO_JUMP(1),
		RID_MARYO_WALL_HIT(2),
		RID_MARYO_POWERDOWN(3),
		RID_MARYO_DEATH(5),
		RID_MARYO_BALL(4),
		RID_MARYO_AU(8),
		RID_MARYO_STOP(9),

		RID_FIREPLANT(6),
		RID_MUSHROOM_BLUE(6),
		RID_MUSHROOM_GHOST(6),
		RID_MUSHROOM(6),
		RID_FEATHER(6),
		RID_1UP_MUSHROOM(7),
		RID_MOON(7);
	}

	/* *** *** *** *** *** *** *** Audio Sound object *** *** *** *** *** *** *** *** *** *** */
	public void Finished_Sound( final int channel )
	{
		// find the finished sound and free the data
		for( cAudio_Sound.iterator itr = cAudio.pAudio->m_active_sounds.begin(), itr_end = pAudio->m_active_sounds.end(); itr != itr_end; ++itr )
		{
			cAudio_Sound *obj = (*itr);

			if( obj->m_channel == channel )
			{
				obj->Finished();
			}
		}
	}

	/* *** *** *** *** *** *** *** *** Audio Sound *** *** *** *** *** *** *** *** *** */


	


	/* *** *** *** *** *** *** *** *** Audio *** *** *** *** *** *** *** *** *** */

	

	
	

	




	/* *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** */

	typedef vector<cAudio_Sound *> AudioSoundList;

	/* *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** */

	} // namespace SMC

}
