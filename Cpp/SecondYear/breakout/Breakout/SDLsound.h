#pragma once
#ifndef BREAKOUT_SDL_SOUND_
#define BREAKOUT_SDL_SOUND_

// C libraries
#include <SDL_mixer.h>

/**
 * This header file declares the facade for SDL_mixer. In this class I declare all consts
 * for songs and sound have provided a simple enum interface for playing songs and sounds.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class SDLsound {
public:
	// enumerations
	enum ESong { INTRO_MUSIC, GAME_MUSIC, GAMEOVER_MUSIC };
	enum ESound { PLAYER_HIT_SOUND, BRICK_HIT_SOUND, BALL_DEATH_SOUND };

	SDLsound();
	~SDLsound();

	// config
	void Init();
	void Kill();

	// music operations
	bool IsMusicPlaying() { return Mix_PlayingMusic() == 1 ? true : false; }
	void PlaySong(ESong song);
	void SwapSong(ESong song) { Mix_HaltMusic(); PlaySong(song); }
	void KillSong();

	// sound operations
	void PlaySound(ESound sound);
private:
	// music
	Mix_Music *intro_music_;
	Mix_Music *game_music_;
	Mix_Music *game_over_music_;

	// sound
	Mix_Chunk *player_hit_;
	Mix_Chunk *brick_hit_;
	Mix_Chunk *ball_death_;

	// music strings
	const char* INTRO_SONG = "..//Resources//music//intro_music.wav";
	const char* GAME_SONG = "..//Resources//music//game_music.wav";
	const char* GAME_OVER_SONG = "..//Resources//music//game_over_music.wav";

	// sound strings
	const char* PLAYER_HIT = "..//Resources//sounds//player_hit.wav";
	const char* BRICK_HIT = "..//Resources//sounds//brick_hit.wav";
	const char* BALL_DEATH = "..//Resources//sounds//ball_death.wav";
};

#endif