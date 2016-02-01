using SnakeMess.Engine;
using SnakeMess.Engine.Util;
using System;

namespace SnakeMess.Model {
    public class Apple : GameObject, ICollidable, IRenderable, IRespawnable {
        public Apple() {
            Body.Add(new Vector2D(0, 0));
        }

        public Apple(Vector2D startPosition) {
            Body.Add(new Vector2D(startPosition));
        }

        // handles specific collisonbehaviour
        public void OnCollision(ICollidable collidable) {
            SendDeath(this);
        }

        // renders graphics
        public void Render() {
            Console.CursorVisible = false;
            Console.ForegroundColor = ConsoleColor.Red;
            foreach (Vector2D v in Body) {
                Console.SetCursorPosition(v.X, v.Y);
                Console.Write("$");
            }
        }

        public override void StateListener(EGameState state){
            
        }
    }
}
