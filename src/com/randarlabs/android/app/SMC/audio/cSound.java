package com.randarlabs.android.app.SMC.audio;

/***************************************************************************
 * sound_manager.h  -  header for the corresponding cpp file
 *
 * Copyright (C) 2006 - 2009 Florian Richter
 ***************************************************************************/
/*
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

//#ifndef SMC_SOUND_MANAGER_H
//#define SMC_SOUND_MANAGER_H
//typedef vector<cSound *> SoundList;
//#include "../core/globals.h"
import com.randarlabs.android.app.SMC.core.*;
//#include "../core/obj_manager.h"
// SDL
// also includes needed SDL headers
//#include "SDL_mixer.h"

public class cSound {
	public cSound( )
	{
		m_chunk = NULL;
	}
	
	//virtual ~cSound( void )
	//{
		//Free();
	//}
	
	// Load the data
	public boolean Load( final String &filename )
	{
		Free();
		
		m_chunk = Mix_LoadWAV( filename.c_str() );

		if( m_chunk )
		{
			m_filename = filename;
			return 1;
		}
		
		return 0;
	}
	
	// Free the data
	public void Free( )
	{
		if( m_chunk )
		{
			Mix_FreeChunk( m_chunk );
			m_chunk = NULL;
		}
		
		m_filename.clear();
	}
	
	// filename
	public String m_filename;
	
	// data if loaded else null
	public Mix_Chunk m_chunk;
	
}
