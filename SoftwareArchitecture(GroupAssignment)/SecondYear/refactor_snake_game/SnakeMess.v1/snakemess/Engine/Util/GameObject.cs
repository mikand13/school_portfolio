using System.Collections.Generic;

namespace SnakeMess.Engine.Util {
    public abstract class GameObject{
        public delegate void OnDeath(GameObject go);
        public static event OnDeath OnDeathListener;

        public List<Vector2D> Body { get; set; }        

        protected GameObject() {
            Body = new List<Vector2D>();
            GameManager.OnGameStateChangedListener += StateListener;

            if (this is IMoveable)
                GameManager.OnDirectionChangedListener += DirectionListener;
        }

        public abstract void StateListener(EGameState state);

        public virtual void DirectionListener(EDirection cardinalDirection){
        
        }

        protected void SendDeath(GameObject go){
            OnDeathListener(go);
        }
    }
}
