namespace SnakeMess.Engine.Util {
    public interface IMoveable {
        EDirection CardinalDirection { get; set; }

        void UpdatePosition();
    }
}
