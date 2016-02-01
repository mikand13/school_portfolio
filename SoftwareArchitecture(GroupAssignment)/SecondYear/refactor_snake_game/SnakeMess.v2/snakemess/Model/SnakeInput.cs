using System;
using SnakeMess.Engine;
using SnakeMess.Engine.Util;

namespace SnakeMess.Model {
    // special input for the snake game
    class SnakeInput : InputManager{
        protected override Object SpecialInputs(ConsoleKey cki, EDirection currentDirection, EGameState gameState) {
            switch (cki) {
                case ConsoleKey.Escape:
                    if (gameState == EGameState.Running)
                        return gameState == EGameState.Running ? EGameState.Over : EGameState.Running;
                    if (gameState == EGameState.Over || gameState == EGameState.Init)
                        Environment.Exit(0);
                    break;
                case ConsoleKey.Spacebar:
                    return gameState == EGameState.Running ? EGameState.Paused : EGameState.Running;
                case ConsoleKey.Q:
                    if (gameState == EGameState.Init)
                        Environment.Exit(0);
                    if (gameState == EGameState.Over)
                        StartupManager.Reset();
                    break;
                case ConsoleKey.P:
                    if (gameState == EGameState.Init)
                        return EGameState.Running;
                    break;
            }

            if (cki == ConsoleKey.UpArrow && currentDirection != EDirection.South)
                return EDirection.North;
            if (cki == ConsoleKey.RightArrow && currentDirection != EDirection.West)
                return EDirection.East;
            if (cki == ConsoleKey.DownArrow && currentDirection != EDirection.North)
                return EDirection.South;
            if (cki == ConsoleKey.LeftArrow && currentDirection != EDirection.East)
                return EDirection.West;
            return cki;
        }
    }
}
