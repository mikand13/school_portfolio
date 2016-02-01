using SnakeMess.Engine.Util;

namespace SnakeMess.Engine {
    public class ScoreManager {
		private int TotalScore { get; set; }

		public virtual void AddScore(IPointsValue points) {
		    TotalScore += points.Points;
		}

		public virtual int GetScore() {
		    return TotalScore;
		}
	}
}
