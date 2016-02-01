/*
  This source file defines all the functions i use to create bricks in the game
  My main initilization function for a new game is also located here.
 */

void initializeNewGame(int width, int height, int* level, int* score);
TetrisBucket makeTetrisBucket(size_t width, size_t height);
void spawnNewBrick();
Brick* newRandomBrick();
void buildBrickPack();
int brickAlreadyExists(int brickID, int currentLength);
Brick* createBrick(int currentLength);
void setVisualRepresentation(Brick *brickToCreate, chtype color, int width,
        int argc, ...);

// builds bucket and two first bricks, sets windows and gets the game ready
void initializeNewGame(int width, int height, int* level, int* score) {
  clear();
  // set seed for bricks
  srand((unsigned int) time(NULL));

  tetrisBucket = makeTetrisBucket((size_t) width, (size_t) height);
  brickPack.size = 0;
  buildBrickPack();

  currentbrick = newRandomBrick();
  nextBrick = newRandomBrick();

  *score = 0;
  *level = 1;

  // ensure large enough terminal
  resizeTerminal(height + 4, 52 + width);

  nodelay(stdscr, true);

  scoreWin = newwin(16, 22, 4, 5);
  bucketWin = newwin(height + 6, width + 5, 0, 28);
  nextBrickWin =
          newwin(10, 15, 2, 36 + width);

  wattr_on(scoreWin, COLOR_PAIR(1), NULL);
  wattr_on(bucketWin, COLOR_PAIR(1), NULL);
  wattr_on(nextBrickWin, COLOR_PAIR(1), NULL);

  showGame(score, level);
  refresh();
}

// builds a bucket and fills with values
TetrisBucket makeTetrisBucket(size_t width, size_t height) {
  int i, j;
  TetrisBucket tetrisBucket;

  tetrisBucket.height = height;
  tetrisBucket.width = width;
  if ((tetrisBucket.bucket = calloc(height, sizeof(struct BucketContainer*))) ==
          NULL) {
    endwin();
    printf("\n\nUnable to calloc required memory. Exiting...\n\n");
    exit(1);
  }

  // makes and fills bucket with space and dots
  for (i = 0; i < height; i++) {
    if ((tetrisBucket.bucket[i] = calloc(width, sizeof(struct BucketContainer)))
            == NULL) {
      endwin();
      printf("\n\nUnable to calloc required memory. Exiting...\n\n");
      exit(1);
    }
    for (j = 0; j < width; j++) {
      if (j % 2 == 0) {
        tetrisBucket.bucket[i][j].value = '.';
        tetrisBucket.bucket[i][j].color = COLOR_PAIR(1);
      } else {
        tetrisBucket.bucket[i][j].value = ' ';
        tetrisBucket.bucket[i][j].color = COLOR_PAIR(1);
      }
    }
  }

  return tetrisBucket;
}

void spawnNewBrick() {
  free(currentbrick);
  currentbrick = nextBrick;

  nextBrick = NULL;
  nextBrick = newRandomBrick();
}

// generates a random brick from availible bricks
Brick* newRandomBrick() {
  if (brickPack.size == 0) {
    buildBrickPack();
  }

  return brickPack.bricks[(sizeof(brickPack.bricks) /
          sizeof(brickPack.bricks[0])) - brickPack.size--];
}

void buildBrickPack() {
  int i;
  for (i = 0; i < (sizeof(brickPack.bricks) /
          sizeof(brickPack.bricks[0])); i++) {
    brickPack.bricks[i] = createBrick(i);
  }

  brickPack.size = (sizeof(brickPack.bricks) / sizeof(brickPack.bricks[0]));
}

int brickAlreadyExists(int brickID, int currentLength) {
  int i;
  for (i = 0; i < currentLength; i++) {
    if (brickPack.bricks[i]->brickID == brickID) { return 1; }
  }
  return 0;
}

Brick* createBrick(int currentLength) {
  Brick* brickToCreate;
  if ((brickToCreate = malloc(sizeof(Brick))) == NULL) {
    endwin();
    printf("\n\nUnable to malloc required memory. Exiting...\n\n");
    exit(1);
  }

  const int totalNumberOfAvailbleBricks = 7;
  int brickSelection = (rand() % totalNumberOfAvailbleBricks) + 1;
  int width = (int) tetrisBucket.width / 2;

  // not a very efficient algorithm but it'll do for the sizes itll encounter
  // here
  while (brickAlreadyExists(brickSelection, currentLength)) {
    brickSelection = (rand() % totalNumberOfAvailbleBricks) + 1;
  }
  brickToCreate->brickID = brickSelection;

  // NOTICE: Pivotpoint is center, apply bricks accordingly if you
  // want them to pivot around their own center. They will always pivot on
  // rotate.
  switch (brickSelection) {
    case 1:
      setVisualRepresentation(brickToCreate, COLOR_PAIR(2), width, 12,
              0, 0, 0, 0, 0,
              0, 0, 0, 0, 0,
              0, 1, 1, 1, 1,
              0, 0, 0, 0, 0,
              0, 0, 0, 0, 0);
      break;
    case 2:
      setVisualRepresentation(brickToCreate, COLOR_PAIR(3), width, 12,
              0, 0, 0, 0, 0,
              0, 0, 0, 0, 0,
              0, 1, 1, 1, 0,
              0, 0, 0, 1, 0,
              0, 0, 0, 0, 0);
      break;
    case 3:
      setVisualRepresentation(brickToCreate, COLOR_PAIR(4), width, 12,
              0, 0, 0, 0, 0,
              0, 0, 0, 0, 0,
              0, 1, 1, 1, 0,
              0, 1, 0, 0, 0,
              0, 0, 0, 0, 0);
      break;
    case 4:
      setVisualRepresentation(brickToCreate, COLOR_PAIR(5), width, 12,
              0, 0, 0, 0, 0,
              0, 0, 0, 0, 0,
              0, 1, 1, 0, 0,
              0, 1, 1, 0, 0,
              0, 0, 0, 0, 0);
      break;
    case 5:
      setVisualRepresentation(brickToCreate, COLOR_PAIR(6), width, 12,
              0, 0, 0, 0, 0,
              0, 0, 0, 0, 0,
              0, 0, 1, 1, 0,
              0, 1, 1, 0, 0,
              0, 0, 0, 0, 0);
      break;
    case 6:
      setVisualRepresentation(brickToCreate, COLOR_PAIR(7), width, 12,
              0, 0, 0, 0, 0,
              0, 0, 0, 0, 0,
              0, 1, 1, 0, 0,
              0, 0, 1, 1, 0,
              0, 0, 0, 0, 0);
      break;
    case 7:
      setVisualRepresentation(brickToCreate, COLOR_PAIR(8), width, 12,
              0, 0, 0, 0, 0,
              0, 0, 0, 0, 0,
              0, 1, 1, 1, 0,
              0, 0, 1, 0, 0,
              0, 0, 0, 0, 0);
      break;
    default:
      break;
  }
  return brickToCreate;
}

// builds visual array for brick and sets color
void setVisualRepresentation(Brick *brickToCreate, chtype color,  int width,
        int argc, ...) {
  int i, j;
  brickToCreate->color = color;
  brickToCreate->size = sizeof(brickToCreate->visualRepresentation) /
          sizeof(brickToCreate->visualRepresentation[0]);
  va_list lst;
  va_start(lst, argc);

  for (i = 0; i < brickToCreate->size; i++) {
    for (j = 0; j < brickToCreate->size; j++) {
      brickToCreate->visualRepresentation[i][j].value = va_arg(lst, int);
      // center brick
      brickToCreate->visualRepresentation[i][j].x =
              ((width - brickToCreate->size / 2) + j);
      // set y-pos to directly above bucket
      brickToCreate->visualRepresentation[i][j].y = i - brickToCreate->size + 1;
    }
  }
  va_end(lst);
}