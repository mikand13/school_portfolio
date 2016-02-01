using System.Diagnostics;
using SnakeMess.Engine;
using SnakeMess.Engine.Util;
using System;

namespace SnakeMess.Model {
    public class Snake : GameObject, ICollidable, IMoveable, IRenderable, IPlayer {
        public EDirection CardinalDirection { get; set; }
        public int Id { get; set; }

        // hack to make the collisonmanager ignore first 200 milliseconds of game to allow the snake to unfurl.
        private readonly Stopwatch _t2;

        public Snake(Vector2D startPosition, int playerCount){
            Id = playerCount;
            _t2 = new Stopwatch();
            for (int i = 0; i < 4; i++) {
                Body.Add(new Vector2D(startPosition));
            }
            CardinalDirection = EDirection.South;
        }

        // increments movement toward direction
        public void UpdatePosition(){
            // moves head
            var head = Body[0];
            switch (CardinalDirection){
                case EDirection.North:
                    Body[0] -= new Vector2D(0, 1);
                    break;
                case EDirection.South:
                    Body[0] += new Vector2D(0, 1);
                    break;
                case EDirection.East:
                    Body[0] += new Vector2D(1, 0);
                    break;
                case EDirection.West:
                    Body[0] -= new Vector2D(1, 0);
                    break;
            }

            // moves body after head, caterpillar style
            for (int i = 1; i < Body.Count; i++){
                Vector2D oldPos = Body[i];
                Body[i] = head;
                head = oldPos;
            }
        }

        // handles specific collisonbehaviour
        public void OnCollision(ICollidable collidable) {
            if (_t2.ElapsedMilliseconds <= 300) return;
            if (!(collidable is Apple)){
                SendDeath(this);
            }
            Body.Add(new Vector2D(Body[Body.Count - 1].X, Body[Body.Count - 1].Y));
        }

        // renders graphics
        public void Render() {
            int i = 0;
            Console.CursorVisible = false;
            Console.ForegroundColor = ConsoleColor.Green;
            foreach (Vector2D v in Body) {
                Console.SetCursorPosition(v.X, v.Y);
                Console.Write(i == 0 ? "@" : "O");
                i++;
            }
        }

        public override void StateListener(EGameState state){
            if (state == EGameState.Running) {
                _t2.Start();
            } else if (state == EGameState.Paused && _t2.ElapsedMilliseconds <= 300) {
                _t2.Stop();
            }
        }

        public override void DirectionListener(EDirection cardinalDirection){
            CardinalDirection = cardinalDirection;
        }
    }
}
