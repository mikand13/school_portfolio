using SnakeMess.Engine;
using SnakeMess.Engine.Util;

namespace SnakeMess.Model {
    class Level : GameObject, ICollidable {
        public override void StateListener(EGameState state){
            // extendability
        }

        public Level(int width, int height){
            for (var i = 0; i <= width; i++) {
                Body.Add(new Vector2D(i, height));
                Body.Add(new Vector2D(i, -1));
            }

            for (var i = 0; i <= height - 1; i++) {
                Body.Add(new Vector2D(-1, i));
                Body.Add(new Vector2D(width, i));
            }
        }

        public void OnCollision(ICollidable collidable){
            // No functionality yet, could call render for explosion etc.
        }
    }
}
