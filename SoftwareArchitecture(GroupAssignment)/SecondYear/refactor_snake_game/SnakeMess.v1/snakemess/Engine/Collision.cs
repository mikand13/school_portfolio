using System;
using SnakeMess.Engine.Util;

namespace SnakeMess.Engine {
    public class Collision {

        public Collision(ICollidable collidable1, ICollidable collidable2) {
            Collidable1 = collidable1;
            Collidable2 = collidable2;
        }

        public ICollidable Collidable1 { get; set; }
        public ICollidable Collidable2 { get; set; }

        public new bool Equals(Object obj){
            if (!(obj is Collision)) return false;
            var temp = (Collision) obj;

            return (Collidable1 == temp.Collidable1 || Collidable1 == temp.Collidable2)
                   && (Collidable2 == temp.Collidable2 || Collidable2 == temp.Collidable1);
        }
    }
}
