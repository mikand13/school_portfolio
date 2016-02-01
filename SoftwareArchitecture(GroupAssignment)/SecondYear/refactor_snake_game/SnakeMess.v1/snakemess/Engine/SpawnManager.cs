using System;
using SnakeMess.Engine.Util;

namespace SnakeMess.Engine {
    public class SpawnManager {
        internal void Spawn(GameObject newGo, int width, int height){
            var r = new Random();
            for (int i = 0; i < newGo.Body.Count; i++){
                newGo.Body[i] = new Vector2D(r.Next(0, width), r.Next(0, height));
            }            
        }
    }
}
