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

public class cSound_Manager {
	public cSound_Manager( )
	
	//virtual ~cSound_Manager( void );

	// Return the Sound from Path
	public final virtual cSound *Get_Pointer( const std::string &path )
	

	/* Add a Sound
	 * Should always have the path set
	 */
	public void Add( cSound *item );

	public final cSound *operator [] ( int identifier )
	{
		return cObject_Manager<cSound>::Get_Pointer( identifier );
	}
	
	public final cSound *operator [] ( final String &path )
	{
		return Get_Pointer( path );
	}

	// Delete all Sounds, but keep object vector entries
	public void Delete_Sounds( )
	

	// sounds loaded since initialization
	private int m_load_count;
	
	// Sound Manager
	public static cSound_Manager *pSound_Manager;
}
