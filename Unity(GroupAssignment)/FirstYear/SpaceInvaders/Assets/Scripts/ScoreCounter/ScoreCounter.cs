using UnityEngine;
using System.Collections;

/**
 * This class defines the Score Counter. It receives information from all game objects about points and tallies them.
 * 
 * If this game was to have the same kind of level structure as the orginal Space Invaders, i would just stay in the scene and instantiate a new AlienLevel prefab.
 * 
 * @author Anders Mikkelsen
 * 
 * */
public class ScoreCounter : MonoBehaviour {
    private static ScoreCounter instance;
    private int totalPoints;

    void Awake() { // making a singleton, for convinience and assurance of no object-collisions
        if (instance != null && instance != this) {
            Destroy(this.gameObject);
            return;
        }
        instance = this;
        DontDestroyOnLoad(this.gameObject);
    }

    public static ScoreCounter Instance { // Singleton
        get { return instance; }
    }

    void Start() {
        Alien.onPointsListener += pointsListener; //Alien is the script attached to the Alien1, Alien2, Alien3 and superclasses Mothership, sends points.
        GameManager.onStateChangedListener += stateChangedListener; //Alien is the script attached to the Alien1, Alien2, Alien3 and superclasses Mothership, sends points.
    }

    public void pointsListener(int points) { // MoveAliens
        totalPoints += points;
        gameObject.GetComponent<TextMesh>().text = ToString();
    }

    public void stateChangedListener(GameState gamestate) {
        if (gamestate == GameState.LEVEL_INIT) {
            totalPoints = 0;
            gameObject.GetComponent<TextMesh>().text = ToString();
            GameManager.Instance.ScoreCounterReady();
        } else if(gamestate == GameState.GAME_INIT) {
            GameManager.onStateChangedListener -= stateChangedListener;
            DestroyObject(gameObject);
        } else if (gamestate == GameState.GAME_OVER) {
            Alien.onPointsListener -= pointsListener;
        }
    }

    public int TotalPoints {
        get { return totalPoints; }
    }

    override public string ToString() {
        return ("Score    " + TotalPoints);
    }
}
