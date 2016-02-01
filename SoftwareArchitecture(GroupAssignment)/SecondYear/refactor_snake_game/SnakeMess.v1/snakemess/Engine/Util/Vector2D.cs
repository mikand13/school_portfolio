namespace SnakeMess.Engine.Util {
    public struct Vector2D {

        public int X { get; set; }
        public int Y { get; set; }

        public Vector2D(Vector2D vector2D) : this() {
            X = vector2D.X;
            Y = vector2D.Y;
        }

        public Vector2D(int x, int y) : this() {
            X = x;
            Y = y;
        }

        public static Vector2D operator +(Vector2D v1, Vector2D v2) {
            return new Vector2D(v1.X + v2.X, v1.Y + v2.Y);
        }

        public static Vector2D operator -(Vector2D v1, Vector2D v2) {
            return new Vector2D(v1.X - v2.X, v1.Y - v2.Y);
        }

        public static Vector2D operator /(Vector2D v1, Vector2D v2) {
            return new Vector2D(v1.X / v2.X, v1.Y / v2.Y);
        }

        public static Vector2D operator *(Vector2D v1, Vector2D v2) {
            return new Vector2D(v1.X * v2.X, v1.Y * v2.Y);
        }

        public static bool operator ==(Vector2D v1, Vector2D v2) {
            return v1.X == v2.X && v1.Y == v2.Y;
        }

        public static bool operator !=(Vector2D v1, Vector2D v2) {
            return v1.X != v2.X || v1.Y != v2.Y;
        }

        public override bool Equals(object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.GetType() != typeof(Vector2D)) {
                return false;
            }
            var v = (Vector2D) obj;
            return this == v;
        }

        // Implemented to make Visual Studio quit whining.
        public override int GetHashCode() {
            return base.GetHashCode();
        }

        public override string ToString() {
            return "[" + X + ", " + Y + "]";
        }
    }
}
