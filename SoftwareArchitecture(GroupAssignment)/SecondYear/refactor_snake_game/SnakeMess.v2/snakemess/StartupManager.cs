using SnakeMess.Engine;
using SnakeMess.Model;

namespace SnakeMess {
    public class StartupManager {
        private static GameManager Gm { get; set; }
        static int Main() {            
            Gm = new GameManager(new SnakePit());
            return 0;
        }

        public static void Reset() {
            Gm = new GameManager(new SnakePit());
        }
    }
}
