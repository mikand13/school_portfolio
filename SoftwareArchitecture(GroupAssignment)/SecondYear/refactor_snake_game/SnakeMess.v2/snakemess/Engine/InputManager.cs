using System;
using SnakeMess.Engine.Util;

namespace SnakeMess.Engine {
    public abstract class InputManager {
        // general input for the engine
        // reads key and returns object based on key, object is handled by game manager
        internal Object PollKey(EDirection currentDirection, EGameState gameState) {
            if (!Console.KeyAvailable) return null;
            var cki = Console.ReadKey(true);
            return SpecialInputs(cki.Key, currentDirection, gameState);
        }
        protected abstract Object SpecialInputs(ConsoleKey cki, EDirection currentDirection, EGameState gameState) ; 
    }
}
