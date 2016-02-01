using System;

namespace SnakeMess.Engine.Util.Gui {
    public abstract class GUI : IRenderable{
        public abstract void Render();
        public abstract void Render(int points);

        protected void PrintLine(String s, int i) {
            for (int j = 0; j < i; j++) {
                Console.Write(s);
            }
        }

        protected void NewLine(int offset, int width, int height) {
            Console.BackgroundColor = ConsoleColor.Gray;
            Console.Write(" ");
            Console.SetCursorPosition((Console.WindowWidth / 2) - (width / 2), (Console.WindowHeight / 2) - (height / 2) + offset);
            Console.Write(" ");
            Console.BackgroundColor = ConsoleColor.Black;
        }
    }
}
