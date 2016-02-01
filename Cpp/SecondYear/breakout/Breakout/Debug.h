#pragma once
#ifndef BREAKOUT_DEBUG_LOG_
#define BREAKOUT_DEBUG_LOG_

/**
 * This header file declares my debug options. I have been disabling and enabling them here for
 * ease of development, they should be input into the preprocessor settings in the project when 
 * they are needed when the application is deployed.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */

// NOTE: GAME WILL SEEMINGLY BE SLOW, 
// IF DEBUG_LOG IS ACTIVATE AT THE SAME TIME AS DEBUG_CAP_EXCEEDED.
// RUN ALL FPS TESTS WITH DEBUG_LOG TURNED OFF

// ALL MUST BE DISABLED FOR RELEASE BUILD

//#define DEBUG_LOG
//#define DEBUG_DESTRUCTOR_LOG
//#define DEBUG_FPS_LOG 
//#define DEBUG_CAP_EXCEEDED

#if defined(DEBUG_LOG) || defined(DEBUG_DESTRUCTOR_LOG) || defined(DEBUG_FPS_LOG) || defined(DEBUG_CAP_EXCEEDED)
#include <iostream>
#endif

// verbose debug info about game mechanics and creations
#ifdef DEBUG_LOG
#define DEBUG(str) do { std::cout << str << std::endl; } while( false )
#else
#define DEBUG(str) do { } while ( false )
#endif

// debug info showing relevant destructors (heap) and select others
#ifdef DEBUG_DESTRUCTOR_LOG
#define DEBUG_DESTRUCTOR(str) do { std::cout << str << std::endl; } while( false )
#else
#define DEBUG_DESTRUCTOR(str) do { } while ( false )
#endif

// debug info showing frame time and wait time
#ifdef DEBUG_FPS_LOG
#define DEBUG_FPS(str) do { std::cout << str << std::endl; } while( false )
#else
#define DEBUG_FPS(str) do { } while ( false )
#endif

// debug info showing any point where game exceeds fps cap
#ifdef DEBUG_CAP_EXCEEDED
#define DEBUG_CAP(str) do { std::cout << str << std::endl; } while( false )
#else
#define DEBUG_CAP(str) do { } while ( false )
#endif

#endif
