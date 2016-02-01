/*
  This source file defines all my utility functions for the tetrisgame.
  Initilizations, cleanups, threads and verifiers.
 */

void initializeMusic();
void initializeNcurses();
void cleanUp(pthread_t musicThread);
void resizeTerminal(int lines, int col);
void (*playMusic(void *arg));
void (*playSound(void *arg));
void soundPlayer(Mix_Chunk* soundName);
void verifyWMove(WINDOW* win, int a, int b);
void verifyWRefresh(WINDOW* win);
void verifyRefresh();
void verifyClear();

void initializeMusic() {
  Mix_Init(MIX_INIT_MP3);
  Mix_OpenAudio(44100, AUDIO_S16SYS, 2, 1024);

  // music
  const char* themeSong = "music/Tetris.mp3";
  const char* intro = "music/intro.mp3";
  const char* gameOver = "music/gameOver.mp3";

  // sounds
  const char* moveBrickSound = "sounds/move.wav";
  const char* rotateBrickSound = "sounds/rotate.wav";
  const char* landBrickSound = "sounds/land.wav";
  const char* levelUpBrickSound = "sounds/levelUp.wav";
  const char* clearLineBrickSound = "sounds/clearLine.wav";
  const char* tetrisBrickSound = "sounds/tetris.wav";

  // music
  introMusic = Mix_LoadMUS(intro);
  korobeiniki = Mix_LoadMUS(themeSong);
  gameOverMusic = Mix_LoadMUS(gameOver);

  // sounds
  moveSound = Mix_LoadWAV(moveBrickSound);
  rotateSound = Mix_LoadWAV(rotateBrickSound);
  landSound = Mix_LoadWAV(landBrickSound);
  levelUpSound = Mix_LoadWAV(levelUpBrickSound);
  clearLineSound = Mix_LoadWAV(clearLineBrickSound);
  tetrisSound = Mix_LoadWAV(tetrisBrickSound);
}

// initializes ncurses and sets the color pairs
// also sets standard color pair for ncurses ui
void initializeNcurses() {
  initscr();
  raw();
  noecho();
  keypad(stdscr, true);
  curs_set(0);
  start_color();

  // UI
  init_pair(1, COLOR_GREEN, COLOR_BLACK);

  // Brick 1 through 7
  init_pair(2, COLOR_CYAN, COLOR_BLACK);
  init_pair(3, COLOR_BLUE, COLOR_BLACK);
  init_pair(4, COLOR_BLUE, COLOR_BLACK);
  init_pair(5, COLOR_YELLOW, COLOR_BLACK);
  init_pair(6, COLOR_WHITE, COLOR_BLACK);
  init_pair(7, COLOR_MAGENTA, COLOR_BLACK);
  init_pair(8, COLOR_RED, COLOR_BLACK);

  // all standard ui screens will be green with black background
  attron(COLOR_PAIR(1));
}

// restes terminal to standard, frees resources and checks threadexecution
// then quits
void cleanUp(pthread_t musicThread) {
  resizeTerminal(24, 80);
  verifyClear();
  verifyRefresh();
  attroff(COLOR_PAIR(1));

  delwin(scoreWin);
  delwin(bucketWin);
  delwin(nextBrickWin);

  endwin();

  int i;
  for (i = 0; i < tetrisBucket.height; i++) {
    free(tetrisBucket.bucket[i]);
  }

  free(tetrisBucket.bucket);

  for (i = 7 - brickPack.size; i < 7; i++) {
    free(brickPack.bricks[i]);
  }

  // free music
  Mix_FreeMusic(korobeiniki);
  Mix_FreeMusic(introMusic);
  Mix_FreeMusic(gameOverMusic);

  // free chunks
  Mix_FreeChunk(moveSound);
  Mix_FreeChunk(rotateSound);
  Mix_FreeChunk(landSound);
  Mix_FreeChunk(levelUpSound);
  Mix_FreeChunk(clearLineSound);
  Mix_FreeChunk(tetrisSound);

  // don't exit before musicThread is done executing
  pthread_cancel(musicThread);
  pthread_join(musicThread, NULL);

  Mix_CloseAudio();
}

void resizeTerminal(int lines, int col) {
  char temp[50];
  sprintf(temp, "resize -s %d %d", lines, col);

  if ((system(temp)) == -1) {
    endwin();
    printf("\n\nCould not resize terminal window\n\n");
  }

  verifyRefresh();
}

void soundPlayer(Mix_Chunk* soundName) {
  void* sound = soundName;
  pthread_t moveSoundThread;
  pthread_create(&moveSoundThread, NULL, playSound, sound);
  pthread_detach(moveSoundThread);
}

void musicPlayer(pthread_t* musicThread, Mix_Music* musicName) {
  void* music = musicName;

  pthread_cancel(*musicThread);
  pthread_join(*musicThread, NULL);
  pthread_create(musicThread, NULL, playMusic, music);
}

void verifyWMove(WINDOW* win, int a, int b) {
  if ((wmove(win, a, b)) == ERR) {
    endwin();
    printf("\n\nUnable to move cursor. Exiting...\n\n");
    exit(2);
  }
}

void verifyWRefresh(WINDOW* win) {
  if ((wrefresh(win)) == ERR) {
    endwin();
    printf("\n\nUnable to refresh window. Exiting...\n\n");
    exit(2);
  }
}

void verifyWClear(WINDOW* win) {
  if ((wclear(win)) == ERR) {
    endwin();
    printf("\n\nUnable to print to window. Exiting...\n\n");
    exit(2);

    log:d("ALKsjf ! ");

    exit(2);

    endwin();
  }
}

void verifyRefresh() {
  if ((refresh()) == ERR) {
    endwin();
    printf("\n\nUnable to refresh ncurses. Exiting...\n\n");
    exit(2);
  }
}

void verifyClear() {
  if ((clear()) == ERR) {
    endwin();
    printf("\n\nUnable to clear ncurses. Exiting...\n\n");
    exit(2);
  }
}