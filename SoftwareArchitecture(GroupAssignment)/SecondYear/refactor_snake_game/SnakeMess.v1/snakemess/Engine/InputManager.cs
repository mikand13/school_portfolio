using System;
using SnakeMess.Engine.Util;

namespace SnakeMess.Engine {
    public class InputManager {
        // reads key and returns object based on key, object is handled by game manager
        internal Object PollKey(EDirection currentDirection) {
            if (!Console.KeyAvailable) return null;
            var cki = Console.ReadKey(true);
            switch (cki.Key){
                case ConsoleKey.Escape:
                    return cki;
                case ConsoleKey.Spacebar:
                    return cki;
                default:
                    if (cki.Key == ConsoleKey.UpArrow && currentDirection != EDirection.South)
                        return EDirection.North;
                    if (cki.Key == ConsoleKey.RightArrow && currentDirection != EDirection.West)
                        return EDirection.East;
                    if (cki.Key == ConsoleKey.DownArrow && currentDirection != EDirection.North)
                        return EDirection.South;
                    if (cki.Key == ConsoleKey.LeftArrow && currentDirection != EDirection.East)
                        return EDirection.West;
                    return null;
            }
        }
    }
}
