using System;
using SnakeMess.Engine.Util.Gui;

namespace SnakeMess.Model {
    // generates the snake specifics for Game Over screen
    class SnakeEnd : GUI {
        protected const int Width = 38;
        protected const int Height = 10;
        protected int ScoreHeight;
        protected int ScoreWidth;

        public override void Render() {
            
        }

        public override void Render(int points) {
            ScoreHeight = (Height / 2) - 1;
            ScoreWidth = (Width / 2) - 31;

            var offset = 0;
            Console.ForegroundColor = ConsoleColor.White;
            Console.BackgroundColor = ConsoleColor.Gray;
            Console.SetCursorPosition((Console.WindowWidth / 2) - (Width / 2), (Console.WindowHeight / 2) - (Height / 2) + offset - 1);
            Console.Write(" ");
            PrintLine(" ", Width - 1);
            NewLine(offset++, Width, Height);
            PrintLine(" ", Width - 1);
            Console.BackgroundColor = ConsoleColor.Gray;
            NewLine(offset++, Width, Height);
            PrintLine(" ", 5);
            Console.Write("Game Over!   Your Score: ");
            PrintLine(" ", 7);
            NewLine(offset++, Width, Height);
            PrintLine(" ", Width - 1);
            NewLine(offset++, Width, Height);
            PrintLine(" ", 6);
            Console.Write("<Press 'Q' for main menu>");
            PrintLine(" ", 6);
            NewLine(offset++, Width, Height);
            PrintLine(" ", Width - 1);
            NewLine(offset, Width, Height);
            Console.BackgroundColor = ConsoleColor.Gray;
            PrintLine(" ", Width);
            Console.ForegroundColor = ConsoleColor.Green;
            Console.BackgroundColor = ConsoleColor.Black;
            Console.SetCursorPosition((Console.WindowWidth / 2) - ScoreWidth, (Console.WindowHeight / 2 - ScoreHeight));
            Console.ForegroundColor = ConsoleColor.Blue;
            Console.Write(points);
            Console.ForegroundColor = ConsoleColor.Green; 
        }
    }
}
