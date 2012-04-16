package com.randarlabs.android.app.SMC.audio;

/***************************************************************************
 * audio.h  -  header for the corresponding cpp file
 *
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

//#ifndef SMC_AUDIO_H
//#define SMC_AUDIO_H

//#include "../core/globals.h"
import com.randarlabs.android.app.SMC.core.*;
//#include "../audio/sound_manager.h"
import com.randarlabs.android.app.SMC.audio.*;

public class cAudio_Sound {


	//namespace SMC
	//{

	/* *** *** *** *** *** *** *** Sound Resource ID's  *** *** *** *** *** *** *** *** *** *** */

	// sounds which shouldn't be played multiple times at the same time
	enum AudioChannel
	{
		RID_MARYO_JUMP		= 1,
		RID_MARYO_WALL_HIT	= 2,
		RID_MARYO_POWERDOWN = 3,
		RID_MARYO_DEATH		= 5,
		RID_MARYO_BALL		= 4,
		RID_MARYO_AU		= 8,
		RID_MARYO_STOP		= 9,

		RID_FIREPLANT		= 6,
		RID_MUSHROOM_BLUE	= 6,
		RID_MUSHROOM_GHOST	= 6,
		RID_MUSHROOM		= 6,
		RID_FEATHER			= 6,
		RID_1UP_MUSHROOM	= 7,
		RID_MOON			= 7
	};

	/* *** *** *** *** *** *** *** Audio Sound object *** *** *** *** *** *** *** *** *** *** */
		
	public:
		public cAudio_Sound( )
		{
			m_data = NULL;
			m_channel = -1;
			m_resource_id = -1;
		}
		//virtual ~cAudio_Sound( void )
		//{
			//Free();
		//}
		
		// Load the data
		public void Load( cSound *data )
		{
			Free();
			m_data = data;
		}
		
		// Free the data
		public void Free( )
		{
			Stop();
			
			if(m_data)
			{
				m_data = NULL;
			}
			
			m_channel = -1;
			m_resource_id = -1;
		}
		
		// Finished playing
		public void Finished( )
		{
			m_channel = -1;
		}

		/* Play the Sound
		 * use_res_id: if set stops all sounds using the same resource id.
		 * loops : if set to -1 loops indefinitely or if greater than zero, loop the sound that many times.
		*/
		public int Play( int use_res_id = -1, int loops = 0 )
		{
			if( !m_data || !m_data->m_chunk )
			{
				return 0;
			}

			if( use_res_id >= 0 )
			{
				for( AudioSoundList::iterator itr = pAudio->m_active_sounds.begin(), itr_end = pAudio->m_active_sounds.end(); itr != itr_end; ++itr )
				{
					// get object pointer
					cAudio_Sound *obj = (*itr);

					// skip self
					if( !obj || obj->m_channel == m_channel )
					{
						continue;
					}

					// stop Sounds using the given resource id
					if( obj->m_resource_id == use_res_id )
					{
						obj->Stop();
					}
				}
			}

			m_resource_id = use_res_id;
			// play sound
			m_channel = Mix_PlayChannel( -1, m_data->m_chunk, loops );
			// add callback if sound finished playing
			Mix_ChannelFinished( &Finished_Sound );

			return m_channel;
		}
		
		// Stop the Sound if playing
		public void Stop( )
		{
			// if not loaded or not playing
			if( !m_data || m_channel < 0 )
			{
				return;
			}
			
			Mix_HaltChannel( m_channel );
			m_channel = -1;
		}

		// sound object
		private cSound m_data;

		// channel if playing else -1
		private int m_channel;
		// the last used resource id
		private int m_resource_id;
	

	/* *** *** *** *** *** *** *** Audio class *** *** *** *** *** *** *** *** *** *** */

	
} // namespace SMC

	//#endif


