using SnakeMess.Engine;
using SnakeMess.Engine.Util;
using System;

namespace SnakeMess.Model {
    public sealed class SnakePit : LevelManager {
        public SnakePit() {
            Initialize();
            SetLists();
        }

        protected override void Initialize() {
            Width = Console.WindowWidth;
            Height = Console.WindowHeight;

            var playerCount = 1;

            var random = new Random();
            var snake = new Snake(new Vector2D(10, 10), playerCount++);
            GameObjects.Add(snake);

            // hack for giving apple a random location not equal to snake
            var temp = new Apple(new Vector2D(random.Next(0, Width), random.Next(0, Height)));
            while (temp.Body[0] == snake.Body[0]) {
                temp = new Apple(new Vector2D(random.Next(0, Width), random.Next(0, Height)));
            }
            GameObjects.Add(temp);
            GameObjects.Add(new Level(Width, Height));
        }
    }
}
