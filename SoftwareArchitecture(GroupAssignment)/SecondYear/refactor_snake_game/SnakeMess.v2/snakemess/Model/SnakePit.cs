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

            const int playerOffset = 10;
            var playerCount = 1;

            var random = new Random();
            GameObjects.Add(new Snake(new Vector2D(random.Next(0, Width - playerOffset), random.Next(0, Height - playerOffset)), playerCount++));
            GameObjects.Add(new Apple(new Vector2D(random.Next(0, Width), random.Next(0, Height)), 10));
            GameObjects.Add(new Level(Width, Height));

            StartMenu = new SnakeStart();
            GameOver = new SnakeEnd();
            InputManager = new SnakeInput();
        }
    }
}
