#pragma once
#ifndef BREAKOUT_SDL_EXCEPTIONS_INITIALIZATION_
#define BREAKOUT_SDL_EXCEPTIONS_INITIALIZATION_

// C++ libraries
#include <system_error>

/**
 * This header file declares the Exception class i made for showing that i understand how they
 * work. I have restricted its use for initialization so it will not impact performance in any
 * way.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class SDLInitializationException :
	public std::runtime_error {
public:
	SDLInitializationException() : runtime_error("SDL failed to initialize!") {}
	~SDLInitializationException();
};

#endif

