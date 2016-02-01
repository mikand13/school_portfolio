using System.Collections;
using System.Collections.Generic;
using SnakeMess.Engine.Util;

namespace SnakeMess.Engine {
    public class CollisionManager {
        private List<Collision> Collisions { get; set; }
        private Hashtable CollisionMap { get; set; }

        internal Collision[] CheckCollision(List<ICollidable> collidables){
            Collisions = new List<Collision>();
            CollisionMap = new Hashtable();

            foreach (ICollidable t in collidables){
                var temp = (GameObject) t;
                foreach (var t1 in temp.Body){
                    if (CollisionMap.ContainsKey(t1)){
                        // create collison
                        var proposedCollision = new Collision(t,
                            (ICollidable) CollisionMap[t1]);
                        // add collision if not already existing in array
                        if (!Collisions.Contains(proposedCollision)){
                            Collisions.Add(proposedCollision);
                        }
                    } else{
                        CollisionMap.Add(t1, temp);
                    }
                }
            }
            return Collisions.ToArray();
        }

        internal bool CheckRespawnCollision(GameObject newGo, List<ICollidable> collidables){
            var posList = new List<Vector2D>();

            foreach (var c in collidables) {
                if (c == newGo) continue;
                foreach (var v in ((GameObject) c).Body){
                    posList.Add(v);                    
                }
            }

            foreach (var v in newGo.Body){
                if (posList.Contains(v)){
                    return false;
                }
            }
            return true;
        }
    }
}
