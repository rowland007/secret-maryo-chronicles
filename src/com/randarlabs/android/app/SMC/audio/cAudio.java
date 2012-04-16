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

	//namespace SMC
	//{
public class cAudio {
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
		

	//typedef vector<cAudio_Sound *> AudioSoundList;

	/* *** *** *** *** *** *** *** Audio class *** *** *** *** *** *** *** *** *** *** */

	public cAudio( )
	{
		m_initialised = 0;
		m_sound_enabled = 0;
		m_music_enabled = 0;

		m_debug = 0;

		m_sound_volume = cPreferences::m_sound_volume_default;
		m_music_volume = cPreferences::m_music_volume_default;

		m_music = NULL;
		m_music_old = NULL;

		m_max_sounds = 0;

		m_audio_buffer = 4096; // below 2048 can be choppy
		m_audio_channels = MIX_DEFAULT_CHANNELS; // 1 = Mono, 2 = Stereo
	}
	
		//~cAudio( void )
		//{
			//Close();
		//}

		// Initialize Audio Engine
		public boolean Init( )
		{
			// Get current device parameters
			int dev_frequency = 0;
			Uint16 dev_format = 0;
			int dev_channels = 0;
			int numtimesopened = Mix_QuerySpec( &dev_frequency, &dev_format, &dev_channels );

			boolean sound = pPreferences->m_audio_sound;
			boolean music = pPreferences->m_audio_music;

			// if no change
			if( numtimesopened && m_music_enabled == music && m_sound_enabled == sound && dev_frequency == pPreferences->m_audio_hz )
			{
				return 1;
			}

			Close();

			// if no audio
			if( !music && !sound )
			{
				return 1;
			}

			// if audio system is not initialized
			if( !m_initialised )
			{
				if( m_debug )
				{
					printf( "Initializing Audio System - Buffer %i, Frequency %i, Speaker Channels %i\n", m_audio_buffer, pPreferences->m_audio_hz, m_audio_channels );
				}

				/*	Initializing preferred Audio System specs with Mixer Standard format (Stereo)
				*
				*	frequency	: Output sampling frequency in samples per second (Hz).
				*	format		: Output sample format.
				*	channels	: Number of sound channels in output. 2 for stereo and 1 for mono.
				*	chunk size	: Bytes used per output sample.
				*/

				if( Mix_OpenAudio( pPreferences->m_audio_hz, MIX_DEFAULT_FORMAT, m_audio_channels, m_audio_buffer ) < 0 ) 
				{
					printf( "Warning : Could not init 16-bit Audio\n- Reason : %s\n", SDL_GetError() );
					return 0;
				}

				numtimesopened = Mix_QuerySpec( &dev_frequency, &dev_format, &dev_channels );

				if( !numtimesopened )
				{
					printf( "Mix_QuerySpec failed: %s\n", Mix_GetError() );
				}
				else
				{
					// different frequency
					if( pPreferences->m_audio_hz != dev_frequency )
					{
						printf( "Warning : different frequency got %d but requested %d\n", dev_frequency, pPreferences->m_audio_hz );
					}

					// different format
					if( dev_format != MIX_DEFAULT_FORMAT )
					{
						char *format_str;

						switch( dev_format )
						{
							case AUDIO_U8:
								format_str = "U8";
								break;
							case AUDIO_S8:
								format_str = "S8";
								break;
							case AUDIO_U16LSB:
								format_str = "U16LSB";
								break;
							case AUDIO_S16LSB:
								format_str = "S16LSB";
								break;
							case AUDIO_U16MSB:
								format_str = "U16MSB";
								break;
							case AUDIO_S16MSB:
								format_str = "S16MSB";
								break;
							default:
								format_str = "Unknown";
								break;
						}

						printf( "Warning : got different format %s\n", format_str );
					}

					// different amount of channels
					if( m_audio_channels != dev_channels )
					{
						printf( "Warning : different channels got %d but requested %d\n", dev_channels, m_audio_channels );
					}
				}

				m_initialised = 1;
			}


			if( m_debug )
			{
				printf( "Audio Sound Channels available : %d\n", Mix_AllocateChannels( -1 ) );
			}

			// music initialization
			if( music && !m_music_enabled )
			{
				m_music_enabled = 1;

				// set music volume
				Set_Music_Volume( m_music_volume );
			}
			// music de-initialization
			else if( !music && m_music_enabled )
			{
				Halt_Music();

				m_music_enabled = 0;
			}

			// sound initialization
			if( sound && !m_sound_enabled )
			{
				m_sound_enabled = 1;

				// create sound array
				Set_Max_Sounds();
				// set sound volume
				Set_Sound_Volume( m_sound_volume );
			}
			// sound de-initialization
			else if( !sound && m_sound_enabled )
			{
				Stop_Sounds();

				m_sound_enabled = 0;
			}

			return 1;
		}
		
		// De-initializes Audio Engine
		public void Close( )
		{
			if( m_initialised )
			{
				if( m_debug )
				{
					printf( "Closing Audio System\n" );
				}

				if( m_sound_enabled )
				{
					Stop_Sounds();

					// clear sounds
					for( AudioSoundList::iterator itr = m_active_sounds.begin(), itr_end = m_active_sounds.end(); itr != itr_end; ++itr )
					{
						delete *itr;
					}

					m_active_sounds.clear();

					Mix_AllocateChannels( 0 );
					m_max_sounds = 0;
					m_sound_enabled = 0;
				}

				if( m_music_enabled )
				{
					Halt_Music();

					if( m_music )
					{
						Mix_FreeMusic( m_music );
						m_music = NULL;
					}

					if( m_music_old )
					{
						Mix_FreeMusic( m_music_old );
						m_music_old = NULL;
					}

					m_music_enabled = 0;
				}

				Mix_CloseAudio();

				m_initialised = 0;
			}
		}

		// Set the maximum number of sounds playable at once
		public void Set_Max_Sounds( unsigned int limit = 10 )
		{
			if( !m_initialised || !m_sound_enabled )
			{
				return;
			}

			// if limit is too small set it to the minimum
			if( limit < 5 )
			{
				limit = 5;
			}

			m_max_sounds = limit;

			// remove exceeding sounds
			AudioSoundList::iterator last_itr;

			while( m_active_sounds.size() > m_max_sounds )
			{
				last_itr = (m_active_sounds.end() - 1);

				// delete data
				delete *(last_itr);
				// erase from list
				m_active_sounds.erase( last_itr );
			}

			// change channels managed by the mixer
			Mix_AllocateChannels( m_max_sounds );

			if( m_debug )
			{
				printf( "Audio Sound Channels changed : %d\n", Mix_AllocateChannels( -1 ) );
			}
		}

		/* Check if the sound was already loaded and returns a pointer to it else it will be loaded.
		 * The returned sound should not be deleted or modified.
		 */
		public cSound *Get_Sound_File( String filename ) const;
		{
			if( !m_initialised || !m_sound_enabled )
			{
				return NULL;
			}

			// not available
			if( !File_Exists( filename ) )
			{
				// add sound directory
				if( filename.find( DATA_DIR "/" GAME_SOUNDS_DIR "/" ) == std::string::npos )
				{
					filename.insert( 0, DATA_DIR "/" GAME_SOUNDS_DIR "/" );
				}
			}

			cSound *sound = pSound_Manager->Get_Pointer( filename );

			// if not already cached
			if( !sound )
			{
				sound = new cSound();

				// loaded sound
				if( sound->Load( filename ) )
				{
					pSound_Manager->Add( sound );

					if( m_debug )
					{
						printf( "Loaded sound file : %s\n", filename.c_str() );
					}
				}
				// failed loading
				else
				{
					printf( "Could not load sound file : %s \nReason : %s\n", filename.c_str(), SDL_GetError() );
					
					delete sound;
					return NULL;
				}
			}

			return sound;
		}
		
		// Play the given sound
		public boolean Play_Sound( String filename, int res_id = -1, int volume = -1, int loops = 0 )
		{
			if( !m_initialised || !m_sound_enabled )
			{
				return 0;
			}

			// not available
			if( !File_Exists( filename ) )
			{
				// add sound directory
				if( filename.find( DATA_DIR "/" GAME_SOUNDS_DIR "/" ) == std::string::npos )
				{
					filename.insert( 0, DATA_DIR "/" GAME_SOUNDS_DIR "/" );
				}

				// not found
				if( !File_Exists( filename ) )
				{
					printf( "Could not find sound file : %s\n", filename.c_str() );
					return 0;
				}
			}

			cSound *sound_data = Get_Sound_File( filename );

			// failed loading
			if( !sound_data )
			{
				printf( "Warning : Could not load sound file : %s\n", filename.c_str() );
				return 0;
			}

			// create channel
			cAudio_Sound *sound = Create_Sound_Channel();

			if( !sound )
			{
				// no free channel available
				return 0;
			}

			// load data
			sound->Load( sound_data );
			// play
			sound->Play( res_id, loops );

			// failed to play
			if( sound->m_channel < 0 )
			{
				if( m_debug )
				{
					printf( "Could not play sound file : %s\n", filename.c_str() );
				}

				return 0;
			}
			// playing successfully
			else
			{
				// volume is out of range
				if( volume > MIX_MAX_VOLUME )
				{
					printf( "PlaySound Volume is out of range : %d\n", volume );
					volume = m_sound_volume;
				}
				// no volume is given
				else if( volume < 0 )
				{
					volume = m_sound_volume;
				}

				// set volume
				Mix_Volume( sound->m_channel, volume );
			}

			return 1;
		}
		// If no forcing it will be played after the current Music file
		boolean Play_Music( String filename, int loops = 0, boolean force = 1, unsigned int fadein_ms = 0 ); 
		{
			if( !m_music_enabled || !m_initialised )
			{
				return 0;
			}

			if( filename.find( DATA_DIR "/" GAME_MUSIC_DIR "/" ) == std::string::npos )
			{
				filename.insert( 0, DATA_DIR "/" GAME_MUSIC_DIR "/" );
			}

			// no valid file
			if( !File_Exists( filename ) )
			{
				printf( "Couldn't find music file : %s\n", filename.c_str() );
				return 0;
			}

			// if music is stopped resume it
			Resume_Music();

			// if no music is playing or force to play the given music
			if( !Is_Music_Playing() || force ) 
			{
				// stop and free current music
				if( m_music )
				{
					Halt_Music();
					Mix_FreeMusic( m_music );
				}
				// free old music
				if( m_music_old )
				{
					Mix_FreeMusic( m_music_old );
					m_music_old = NULL;
				}

				// load the given music
				m_music = Mix_LoadMUS( filename.c_str() );

				// loaded
				if( m_music )
				{
					// no fade in
					if( !fadein_ms )
					{
						Mix_PlayMusic( m_music, loops );
					}
					// fade in
					else
					{
						Mix_FadeInMusic( m_music, loops, fadein_ms );
					}
				}
				// not loaded
				else 
				{
					if( m_debug )
					{
						printf( "Couldn't load music file : %s\n", filename.c_str() );
					}

					// failed to play
					return 0;
				}
			}
			// music is playing and is not forced
			else
			{
				// if music is loaded
				if( m_music )
				{
					// if old music is loaded free the wanted next playing music data
					if( m_music_old )
					{
						Mix_FreeMusic( m_music );
						m_music = NULL;
					}
					// if no old music move current to old music
					else
					{
						m_music_old = m_music;
						m_music = NULL;
					}
				}

				// load the wanted next playing music
				m_music = Mix_LoadMUS( filename.c_str() );
			}
		
			return 1;
		}
		
		/* Returns a pointer to the sound if it is active.
		 * The returned sound should not be deleted or modified.
		 */
		public cAudio_Sound *Get_Playing_Sound( String filename )
		{
			if( !m_sound_enabled || !m_initialised )
			{
				return NULL;
			}

			// add sound directory
			if( filename.find( DATA_DIR "/" GAME_SOUNDS_DIR "/" ) == String::npos )
			{
				filename.insert( 0, DATA_DIR "/" GAME_SOUNDS_DIR "/" );
			}

			// get all sounds
			for( AudioSoundList::iterator itr = m_active_sounds.begin(), itr_end = m_active_sounds.end(); itr != itr_end; ++itr )
			{
				// get object pointer
				cAudio_Sound *obj = (*itr);

				// if not playing
				if( obj->m_channel < 0 )
				{
					continue;
				}

				// found it
				if( obj->m_data->m_filename.compare( filename ) == 0 )
				{
					// return first found
					return obj;
				}
			}

			// not found
			return NULL;
		}

		/* Returns true if a free channel for the sound is available
		*/
		public cAudio_Sound *Create_Sound_Channel( )
		{
			// get all sounds
			for( AudioSoundList::iterator itr = m_active_sounds.begin(), itr_end = m_active_sounds.end(); itr != itr_end; ++itr )
			{
				// get object pointer
				cAudio_Sound *obj = (*itr);

				// if not playing
				if( obj->m_channel < 0 )
				{
					// found a free channel
					obj->Free();
					return obj;
				}
			}

			// if not maximum sounds
			if( m_active_sounds.size() < m_max_sounds )
			{
				cAudio_Sound *sound = new cAudio_Sound();
				m_active_sounds.push_back( sound );
				return sound;
			}

			// none found
			return NULL;
		}
		
		// Toggle Music on/off
		public void Toggle_Music( )
		{
			pPreferences->m_audio_music = !pPreferences->m_audio_music;
			Init();

			// play music
			if( m_music_enabled )
			{
				// valid level music available
				if( pActive_Level->m_valid_music )
				{
					Play_Music( pActive_Level->m_musicfile, -1, 1, 2000 );
				}
				// in overworld
				else if( Game_Mode == MODE_OVERWORLD )
				{
					Play_Music( pActive_Overworld->m_musicfile, -1, 1, 2000 );
				}
				// in menu
				else
				{
					Play_Music( "game/menu.ogg", -1, 1, 2000 );
				}

				// Warning if no music pack is installed and music got enabled
				if( !File_Exists( std::string(DATA_DIR "/" GAME_MUSIC_DIR "/game/menu.ogg") ) && !File_Exists( std::string(DATA_DIR "/" GAME_MUSIC_DIR "/land/land_1.ogg") ) )
				{
					Draw_Static_Text( _("Music addon not detected.\nYou can download it from the Website."), &orange );
				}
			}
		}
		
		// Toggle Sounds on/off
		public void Toggle_Sounds( )
		{
			pPreferences->m_audio_sound = !pPreferences->m_audio_sound;
			Init();

			// play a test sound
			if( m_sound_enabled )
			{
				Play_Sound( "audio_on.ogg" );
			}
		}
		
		// Pause Music
		public final void Pause_Music( )
		{
			if( !m_music_enabled || !m_initialised )
			{
				return;
			}

			// Check if music is currently playing
			if( Mix_PlayingMusic() )
			{
				Mix_PauseMusic();
			}
		}
		
		/* Resume halted sound
		 * if channel is -1 all halted sounds will be resumed
		*/
		public final void Resume_Sound( int channel = -1 )
		{
			if( !m_sound_enabled || !m_initialised )
			{
				return;
			}

			// resume playback on all previously active channels
			Mix_Resume( channel );
		}
		// Resume Music
		public final void Resume_Music( ) 
		{
			if( !m_music_enabled || !m_initialised )
			{
				return;
			}

			// Check if music is currently paused
			if( Mix_PausedMusic() )
			{
				Mix_ResumeMusic();
			}
		}

		/* Fade out Sound(s)
		 * ms : the time to fade out
		 * channel : if set only fade this channel out or if -1 all channels
		 * overwrite_fading : overwrite an already existing fade out
		*/
		public final void Fadeout_Sounds( unsigned int ms = 200, int channel = -1, boolean overwrite_fading = 0 ) 
		{
			if( !m_sound_enabled || !m_initialised )
			{
				return;
			}

			// Check the Channels
			if( Mix_Playing( channel ) )
			{
				// Do not fade out the sound again
				if( !overwrite_fading && Is_Sound_Fading( -1 ) == MIX_FADING_OUT )
				{
					return;
				}

				Mix_FadeOutChannel( channel, ms );
			}
		}
		
		/* Fade out Sound(s)
		 * ms : the time to fade out
		 * filename : fade all sounds with this filename out
		 * overwrite_fading : overwrite an already existing fade out
		*/
		public void Fadeout_Sounds( int ms, String filename, boolean overwrite_fading = 0 )
		{
			if( !m_sound_enabled || !m_initialised )
			{
				return;
			}

			// add sound directory
			if( filename.find( DATA_DIR "/" GAME_SOUNDS_DIR "/" ) == std::string::npos )
			{
				filename.insert( 0, DATA_DIR "/" GAME_SOUNDS_DIR "/" );
			}

			// get all sounds
			for( AudioSoundList::iterator itr = m_active_sounds.begin(), itr_end = m_active_sounds.end(); itr != itr_end; ++itr )
			{
				// get object pointer
				cAudio_Sound *obj = (*itr);

				// filename does not match
				if( obj->m_data->m_filename.compare( filename ) != 0 )
				{
					continue;
				}

				// Do not fade out the sound again
				if( !overwrite_fading && Is_Sound_Fading( obj->m_channel ) == MIX_FADING_OUT )
				{
					continue;
				}

				Mix_FadeOutChannel( obj->m_channel, ms );
			}
		}
		
		/* Fade out Music
		 * ms : the time to fade out
		 * overwrite_fading : overwrite an already existing fade out
		*/
		public final void Fadeout_Music( unsigned int ms = 500, boolean overwrite_fading = 0 )
		{
			if( !m_music_enabled || !m_initialised )
			{
				return;
			}

			// if music is currently playing
			if( Mix_PlayingMusic() )
			{
				Mix_Fading status = Is_Music_Fading();

				// if already fading out
				if( status == MIX_FADING_OUT )
				{
					// don't fade the music out again
					if( !overwrite_fading )
					{
						return;
					}
				} 
				// if fading in
				else if( status == MIX_FADING_IN )
				{
					// Can t stop fade in with SDL_Mixer and fade out is ignored when fading in
					Halt_Music();
				}

				// if it failed
				if( !Mix_FadeOutMusic( ms ) )
				{
					// stop music
					Halt_Music();
				}
			}
		}

		// Set the Music position ( if .ogg in seconds )
		public final void Set_Music_Position( float position )
		{
			if( !m_music_enabled || !m_initialised || Is_Music_Fading() == MIX_FADING_OUT )
			{
				return;
			}

			Mix_SetMusicPosition( position );
		}

		// Returns 1 if the Music is currently fading in and 2 if it's fading out else 0
		public final Mix_Fading Is_Music_Fading( )
		{
			if( !m_music_enabled || !m_initialised )
			{
				return MIX_NO_FADING;
			}

			return Mix_FadingMusic();
		}
		
		// Returns 1 if the Sound is currently fading in and 2 if it's fading out else 0
		public final Mix_Fading Is_Sound_Fading( int sound_channel )
		{
			if( !m_sound_enabled || !m_initialised )
			{
				return MIX_NO_FADING;
			}

			 return Mix_FadingChannel( sound_channel );
		}
		
		// Returns true if the Music is paused
		public final boolean Is_Music_Paused( )
		{
			if( !m_music_enabled || !m_initialised )
			{
				return 0;
			}
			
			if( Mix_PausedMusic() )
			{
				return 1;
			}
			
			return 0;
		}
		
		// Returns true if the Music is playing
		public final boolean Is_Music_Playing( )
		{
			if( !m_music_enabled || !m_initialised )
			{
				return 0;
			}
			
			if( Mix_PlayingMusic() )
			{
				return 1;
			}
			
			return 0;
		}
		
		// Halt the given sounds
		public final void Halt_Sounds( int channel = -1 )
		{
			if( !m_sound_enabled || !m_initialised )
			{
				return;
			}

			// Check all Channels
			if( Mix_Playing( channel ) )
			{
				Mix_HaltChannel( channel );
			}
		}
		
		// Halt the Music
		public final void Halt_Music( )
		{
			if( !m_initialised )
			{
				return;
			}

			// Checks if music is playing
			if( Mix_PlayingMusic() )
			{
				Mix_HaltMusic();
			}
		}
		
		// Stop all sounds
		public final void Stop_Sounds( )
		{
			if( !m_initialised )
			{
				return;
			}

			// Stop all channels
			if( Mix_Playing( -1 ) )
			{
				Mix_HaltChannel( -1 );
			}
		}
		
		// Set the Sound Volume
		public final void Set_Sound_Volume( Uint8 volume, int channel = -1 )
		{
			// not active
			if( !m_initialised )
			{
				return;
			}

			// out of range
			if( volume > MIX_MAX_VOLUME )
			{
				volume = MIX_MAX_VOLUME;
			}

			Mix_Volume( channel, volume );
		}
		
		// Set the Music Volume
		public final void Set_Music_Volume( Uint8 volume )
		{
			// not active
			if( !m_initialised )
			{
				return;
			}

			// out of range
			if( volume > MIX_MAX_VOLUME )
			{
				volume = MIX_MAX_VOLUME;
			}

			Mix_VolumeMusic( volume );
		}
		
		// Update the Audio Engine
		public void Update( )
		{
			if( !m_initialised )
			{
				return;
			}

			// if music is enabled
			if( m_music_enabled )
			{
				// if no music is playing
				if( !Mix_PlayingMusic() && m_music ) 
				{
					Mix_PlayMusic( m_music, 0 );

					// delete old music if available
					if( m_music_old )
					{
						Mix_FreeMusic( m_music_old );
						m_music_old = NULL;
					}
				}
			}
		}
		
		// is the audio engine initialized
		private boolean m_initialised;
		// is sound enabled
		private boolean m_sound_enabled;
		// is music enabled
		private boolean m_music_enabled;
		// is the debug mode enabled
		private boolean m_debug;

		// current music and sound volume
		private Uint8 m_sound_volume, m_music_volume;

		// current playing Music pointer
		private Mix_Music *m_music;
		// if new music should play after the current this is the old data
		private Mix_Music *m_music_old;

		// The current sounds pointer array
		private AudioSoundList m_active_sounds;

		// maximum sounds allowed at once
		private /*unsigned*/ int m_max_sounds;

		// initialization information
		private int m_audio_buffer, m_audio_channels;

	/* *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** */

	// Audio Handler
	public static cAudio *pAudio;
	pAudio = NULL;
	/* *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** */

	} // namespace SMC

