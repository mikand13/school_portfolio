using SnakeMess.Engine.Util;
using System.Collections.Generic;
using SnakeMess.Engine.Util.Gui;

namespace SnakeMess.Engine {
    public abstract class LevelManager{
        public int Width { get; protected set; }
        public int Height { get; protected set; }

        internal GUI StartMenu { get; set; }
        internal GUI GameOver { get; set; }
        internal InputManager InputManager { get; set; }

        internal List<GameObject> GameObjects { get; private set; }
        internal List<ICollidable> Collidables { get; private set; }
        internal List<IMoveable> Moveables { get; private set; }
        internal List<IRenderable> Renderables { get; private set; }

        protected LevelManager() {
            GameObjects = new List<GameObject>();
        }

        // removes GameObject from all lists depending on type
        public void Remove(GameObject go){
            GameObjects.Remove(go);

            var item = go as ICollidable;
            if (item != null)
                Collidables.Remove(item);

            var moveable = go as IMoveable;
            if (moveable != null)
                Moveables.Remove(moveable);

            var renderable = go as IRenderable;
            if (renderable != null)
                Renderables.Remove(renderable);
        }

        // sets the various lists for easy access later
        protected virtual void SetLists() {
            Collidables = new List<ICollidable>();
            Moveables = new List<IMoveable>();
            Renderables = new List<IRenderable>();

            foreach (var t in GameObjects){
                AddToList(t);
            }
        }

        public void AddGameObject(GameObject newGo){
            GameObjects.Add(newGo);
            AddToList(newGo);
        }

        private void AddToList(GameObject go) {
            var item = go as ICollidable;
            if (item != null) {
                Collidables.Add(item);
            }

            var moveable = go as IMoveable;
            if (moveable != null) {
                Moveables.Add(moveable);
            }

            var renderable = go as IRenderable;
            if (renderable != null) {
                Renderables.Add(renderable);
            }
        }

        protected abstract void Initialize();
    }
}
