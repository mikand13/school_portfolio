using System;
using SnakeMess.Engine.Util.Gui;

namespace SnakeMess.Model {
    class SnakeStart : GUI {
        public override void Render() {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.White;
            Console.SetCursorPosition((Console.WindowWidth / 2) - 8, (Console.WindowTop) + 5);
            Console.WriteLine("[P]lay Game");
            Console.SetCursorPosition((Console.WindowWidth / 2) - 8, (Console.WindowTop) + 7);
            Console.WriteLine("[Q]uit Game");
            Console.ForegroundColor = ConsoleColor.Green;
            Console.ForegroundColor = ConsoleColor.White;
            Console.SetCursorPosition((Console.WindowWidth / 2) - 25, (Console.WindowTop) + 3);
            Console.WriteLine("Welcome to NITHSnake. What do you want to do?");
            Console.SetCursorPosition((Console.WindowWidth / 2) - 5, (Console.WindowTop) + 11);
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("0000@");
        }

        public override void Render(int points) {
            
        }
    }
}
