#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include <ncurses.h>
#include <SDL/SDL_mixer.h>

/*
  This source file defines all my globals.
 */

/*
  THESE DEFINE THE SIZE OF THE TETRISBUCKET

  The game supports any size that will fit on your screen.
 */

const int width = 10;
const int height = 20;

/*
  THESE DEFINE THE SIZE OF THE TETRISBUCKET
 */

// bucket structs
typedef struct BucketContainer {
  char value;
  chtype color;
} BucketContainer;

typedef struct TetrisBucket {
  BucketContainer **bucket;
  size_t height;
  size_t width;
} TetrisBucket;

// brick structs
typedef struct Coordinate {
  int value;
  int x;
  int y;
} Coordinate;

typedef struct Brick {
  int brickID;
  Coordinate visualRepresentation[5][5];
  int size;
  chtype color;
} Brick;

typedef struct BrickPack {
  Brick* bricks[7];
  int size;
} BrickPack;


// game loop structs and variables
typedef enum BrickDirection {
  Right,
  Left,
  Normal
} BrickDirection;

typedef enum GameState {
  GameInit,
  GameRunning,
  GameOver
} GameState;

/*
  I chose to make the following variables global as they are intrisic to the
  game and wont change, if they do they dont break the game.

  I also had level and score as globals as they are accesed many places, but i
  decided to make them locals to show you i can handle passing pointers around
  and i didnt want them to be public.
*/

// Bucket
TetrisBucket tetrisBucket;

// bricks
Brick *currentbrick;
Brick *nextBrick;

// brick pack
BrickPack brickPack;

// windows for ncurses
WINDOW *bucketWin = NULL;
WINDOW *scoreWin = NULL;
WINDOW *nextBrickWin = NULL;

// music
Mix_Music *korobeiniki = NULL;
Mix_Music *introMusic = NULL;
Mix_Music *gameOverMusic = NULL;

// sounds
Mix_Chunk *moveSound = NULL;
Mix_Chunk *rotateSound = NULL;
Mix_Chunk *landSound = NULL;
Mix_Chunk *levelUpSound = NULL;
Mix_Chunk *clearLineSound = NULL;
Mix_Chunk *tetrisSound = NULL;