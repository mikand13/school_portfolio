#include "SDLsound.h"

// C libraries
#include <SDL.h>

// local headers
#include "Debug.h"
#include "SDLInitializationException.h"

SDLsound::SDLsound() :
	intro_music_(nullptr),
	game_music_(nullptr),
	game_over_music_(nullptr),
	player_hit_(nullptr),
	brick_hit_(nullptr),
	ball_death_(nullptr) {}

SDLsound::~SDLsound() {
	DEBUG_DESTRUCTOR("SDLsound has been destroyed...");
}

void SDLsound::Init() {
	try {
		// init sdl audio
		if (SDL_Init(SDL_INIT_AUDIO) < 0) {
			DEBUG("Could not initialize SDL Audio! SDL_Error: " << SDL_GetError());
			
			throw SDLInitializationException();
		}

		// init sdl mixer
		if (Mix_OpenAudio(44100, MIX_DEFAULT_FORMAT, 2, 2048) < 0) {
			DEBUG(("SDL_mixer could not initialize! SDL_mixer Error: %s\n", Mix_GetError()));

			throw SDLInitializationException();
		}

		// load songs
		intro_music_ = Mix_LoadMUS(INTRO_SONG);
		game_music_ = Mix_LoadMUS(GAME_SONG);
		game_over_music_ = Mix_LoadMUS(GAME_OVER_SONG);

		if (intro_music_ == nullptr) {
			DEBUG("Could not load INTRO_SONG: " << INTRO_SONG);
		}

		if (game_music_ == nullptr) {
			DEBUG("Could not load GAME_SONG: " << GAME_SONG);
		}

		if (game_over_music_ == nullptr) {
			DEBUG("Could not load GAME_OVER_SONG: " << GAME_OVER_SONG);
		}

		// load sounds
		player_hit_ = Mix_LoadWAV(PLAYER_HIT);
		brick_hit_ = Mix_LoadWAV(BRICK_HIT);
		ball_death_ = Mix_LoadWAV(BALL_DEATH);

		if (player_hit_ == nullptr) {
			DEBUG("Could not load PLAYER_HIT: " << PLAYER_HIT);
		}

		if (brick_hit_ == nullptr) {
			DEBUG("Could not load BRICK_HIT: " << BRICK_HIT);
		}

		if (ball_death_ == nullptr) {
			DEBUG("Could not load BALL_DEATH: " << BALL_DEATH);
		}
	} catch (SDLInitializationException) {
		throw;
	}
}

void SDLsound::Kill() {
	DEBUG("Killing SDLsound...");

	Mix_FreeMusic(intro_music_);
	Mix_FreeMusic(game_music_);
	Mix_FreeMusic(game_over_music_);

	Mix_FreeChunk(player_hit_);
	Mix_FreeChunk(brick_hit_);
	Mix_FreeChunk(ball_death_);

	Mix_Quit();

	DEBUG("SDLsound dead...");
}

void SDLsound::PlaySong(ESong song) {
	switch (song) {
		case INTRO_MUSIC:
		Mix_PlayMusic(intro_music_, -1);
		break;
		case GAME_MUSIC:
		Mix_PlayMusic(game_music_, -1);
		break;
		case GAMEOVER_MUSIC:
		Mix_PlayMusic(game_over_music_, -1);
		break;
	}
}

void SDLsound::KillSong() {
	Mix_HaltMusic();
}

void SDLsound::PlaySound(ESound sound) {
	switch (sound) {
	case ESound::PLAYER_HIT_SOUND:
		Mix_PlayChannel(-1, player_hit_, 0);
		break;
	case ESound::BRICK_HIT_SOUND:
		Mix_PlayChannel(-1, brick_hit_, 0);
		break;
	case ESound::BALL_DEATH_SOUND:
		Mix_PlayChannel(-1, ball_death_, 0);
		break;
	}
}