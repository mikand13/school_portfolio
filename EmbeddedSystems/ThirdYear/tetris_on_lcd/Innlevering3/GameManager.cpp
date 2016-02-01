//
// Created by Anders on 13.12.2015.
//

#include "GameManager.h"
const unsigned char FILLED_VALUE = '#';
const unsigned char UNFILLED_VALUE = '.';
const unsigned char EMPTY_VALUE = ' ';

GameManager::GameManager() :
        gameState(GameInit),
        level(1),
        score(0),
        standardSpeed(1100 - (100 * level)),
        gameSpeed(standardSpeed),
        before(millis()),
        mayMoveSideWays(true) {}

void GameManager::setManagers(LevelManager& levelManager, Screen& screen,
                              Accelerometer& accelerometer,
                              SDCardManager& sdCardManager, InputManager& inputManager) {
    this->levelManager = &levelManager;
    this->screen = &screen;
    screen.startScreen();
    this->accelerometer = &accelerometer;
    this->sdCardManager = &sdCardManager;
    this->inputManager = &inputManager;
}

void GameManager::setMusicPlayer(MusicPlayer& musicPlayer) {
    this->musicPlayer = &musicPlayer;
}

void GameManager::initializeNewGameState() {
    levelManager->initializeNewGame();

    gameState = GameInit;

    char highScore[6];
    screen->printGameMenu(sdCardManager->getHighScore(highScore, 5));

    screen->dimUp();
}

void GameManager::startGame() {
    screen->clearScreen();
    gameState = GameRunning;
    resetGameParams();
    showGame();
    setMusicTimer();
}

void GameManager::resetGameParams() {
    score = 0;
    level = 1;
    standardSpeed = 1100 - (100 * level);
    gameSpeed = standardSpeed;
}

void GameManager::showGame() {
    screen->printGame(*levelManager, score, level);
    screen->dimUp();
}

void GameManager::setMusicTimer() {
    noInterrupts();

    TCCR1A = 0;
    TCCR1B = 0;

    OCR1A = 4000;

    TCCR1B |= (1 << WGM12);

    TCCR1B |= (1 << CS10);
    TCCR1B |= (1 << CS12);

    TIMSK1 |= (1 << OCIE1A);

    interrupts();
}

void GameManager::gameOver() {
    gameState = GameOver;
    screen->dimDown();
    screen->clearScreen();
    screen->showGameOver(score, level, MAX_LEVEL);
    screen->dimUp();

    noInterrupts();
    TCCR1A = 0;
    TCCR1B = 0;
    interrupts();

    musicPlayer->stopMusic();

    char highScore[6] = {};
    if (atoi(sdCardManager->getHighScore(highScore, 5)) < score) {
        sdCardManager->setHighScore(score);
    }
}

void GameManager::resetToNewGame() {
    screen->clearScreen();
    initializeNewGameState();
}

void GameManager::pauseGame() {
    noInterrupts();
    screen->pauseGame();
    while (!accelerometer->checkForKnock()) {}
    screen->unPauseGame(*levelManager);
    interrupts();
}

void GameManager::doGameLoop() {
    unsigned long current;
    unsigned long moveCounter;
    int respawn = 0;

    while (gameState == GameRunning) {
        if (checkUserInput()) {
            screen->updateBrick(*levelManager, *levelManager->getBrick());
        } else if (accelerometer->checkForKnock()) {
            pauseGame();
        }

        current = millis();
        if (current - moveCounter > MOVE_MAX) {
            mayMoveSideWays = true;
            moveCounter = current;
        }

        // updates "fps" based on level, starts at 900ms.
        if (current - before > gameSpeed) {
            respawn = levelManager->moveBrick(Normal, score);

            if (respawn != 0) {
                screen->updateScoreWindow(score, level);
                // handle collisionEvent
                checkRespawn(respawn);

                if (gameState != GameOver) {
                    checkScore();
                    screen->updateBucketWindow(*levelManager);
                }
            } else {
                screen->updateBrick(*levelManager, *levelManager->getBrick());
            }

            if (level > MAX_LEVEL) {
                gameOver();
            }

            // reset updateTimer
            before = current;

            // reset in case of DOWN speed
            gameSpeed = standardSpeed;
        }
    }
}

void GameManager::updateMusic() {
    musicPlayer->doTone();
}

void GameManager::checkRespawn(byte respawn) {
    if (respawn == 1) {
        levelManager->spawnNewBrick();
        screen->updateNextBrickWindow(*levelManager->getNextBrick());

        if (checkForFullRows()) screen->updateScoreWindow(score, level);
    } else if (respawn == 2 || respawn == 11) {
        screen->updateScoreWindow(score, level);
        gameOver();
    }
}

void GameManager::checkScore() {
    if (score >= (level * 1000)) {
        level += 1;
        if (standardSpeed > 200) standardSpeed = 1000 - (100 * (level));

        screen->updateScoreWindow(score, level);
    }
}

// checks row by row if full, adds points and rebuilds bucket on full
byte GameManager::checkForFullRows() {
    byte scoreRows = 0, scoreAccumulated = 0;
    boolean fullRow = true;
    TetrisBucket *tetrisBucket = levelManager->getTetrisBucket();

    for (byte i = 1; i < levelManager->getHeight(); i++) {
        for (byte j = 0; j < levelManager->getWidth(); j++) {
            if (tetrisBucket->bucket[i][j].value != FILLED_VALUE) fullRow = false;
        }

        if (fullRow) {
            scoreRows++;
            score += 100 * scoreRows;
            scoreAccumulated = 1;

            boolean brickRemnantOnLine = false;
            for (byte k = i; k > 0; k--) {
                for (byte l = 0; l < levelManager->getWidth(); l++) {
                    tetrisBucket->bucket[k][l] = tetrisBucket->bucket[k - 1][l];
                    if (tetrisBucket->bucket[k][l].value == FILLED_VALUE) brickRemnantOnLine = true;
                }

                if (!brickRemnantOnLine) break;
            }
        } else {
            fullRow = true;
        }
    }

    if (scoreRows > 0) levelManager->resetHighestFilledRow();

    if (scoreRows > 0 && scoreRows < 4) {
        screen->updateBucketWindow(*levelManager, true);
    } else if (scoreRows == 4) {
        screen->invert(true);
        screen->updateBucketWindow(*levelManager, true);
    }

    screen->invert(false);

    return scoreAccumulated;
}

boolean GameManager::checkUserInput() {
    byte respawn = 0;

    switch (inputManager->checkUserInput()) {
        case LEFT_PS_STICK:
            if (mayMoveSideWays) {
                checkRespawn(levelManager->moveBrick(Left, score));
                mayMoveSideWays = false;
            }

            return true;
        case RIGHT_PS_STICK:
            if (mayMoveSideWays) {
                checkRespawn(levelManager->moveBrick(Right, score));
                mayMoveSideWays = false;
            }

            return true;
        case DOWN_PS_STICK:
            gameSpeed = 70;
            mayMoveSideWays = true;
            return false;
        case UP_PS_STICK:
            levelManager->rotateBrick();
            screen->updateBrick(*levelManager, *levelManager->getBrick(), true);
            mayMoveSideWays = true;
            return true;
        case PUSH_PS_STICK:
            screen->removeBrick(*levelManager, *levelManager->getBrick());
            while (respawn == 0) { respawn = levelManager->moveBrick(Normal, score); }
            screen->updateBucketWindow(*levelManager);
            screen->updateScoreWindow(score, level);

            checkRespawn(respawn);
            checkScore();

            before = millis();

            return false;
        default:
            return false;
    }
}

boolean GameManager::checkPushInput() {
    if (inputManager->checkForPush()) {
        screen->dimDown();
        return true;
    }

    return false;
}
