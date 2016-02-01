using SnakeMess.Engine;
using SnakeMess.Model;

namespace SnakeMess {
    public class StartupManager {
        static int Main() {
            new GameManager(new SnakePit());
            return 0;
        }
    }
}
