using UnityEngine;
using System.Collections;

/**
 * This class defines the GameManager. The GameManager runs the game based on states and collects information to display. F.ex when the game is over.
 * 
 * In general i think GUI is pretty shit to work with in Unity, at least this kind, but im doing it to show i understand how to use it.
 * I use maxHeight, maxWidth and baseAmount to scale the GUI. In addition i check the width and adjust max and min movement for the aliens accordingly.
 * 
 * @author Anders Mikkelsen
 * */

public class GameManager : MonoBehaviour {
    private static GameManager instance;
    public GUIStyle gs;

    public delegate void OnStateChanged(GameState gameState);
    public static event OnStateChanged onStateChangedListener;

    public float xMaxDistance;
    public float xMinDistance;
    private float maxHeight;
    private float maxWidth;
    private float baseAmount;

    private bool gameOver;
    private bool win;
    private bool highScore;
    private bool blink;
    private bool blinkStarted;

    void Awake() { 
        if (instance != null && instance != this) {
            Destroy(this.gameObject);
            return;
        }
        instance = this;
        DontDestroyOnLoad(this.gameObject);

        maxHeight = Screen.height;
        maxWidth = Screen.width;
        baseAmount = maxHeight / 100;

        if (GameManager.Instance.MaxWidth < 1400) {
            xMinDistance = -0.7f;
            xMaxDistance = 3.7f;
        }

        blink = true;
    }

    public static GameManager Instance { // Singleton
        get { return instance; }
    }

    void OnGUI() {
        if (gameOver) {
            GameObject[] g = GameObject.FindGameObjectsWithTag("Alien");
            GameObject[] l = GameObject.FindGameObjectsWithTag("Laser");
            foreach (GameObject go in g) { // destroys all aliens
                DestroyObject(go);
            }

            foreach (GameObject go in l) { // destroys all lasers
                DestroyObject(go);
            }
            gs.fontSize = 72;
            GUI.Label(new Rect(maxWidth / 2 - 100, baseAmount * 25f, 200, baseAmount * 7.5f), "Game over!", gs);
            if (win) {
                gs.fontSize = 40;
                if (highScore) {
                    GUI.Label(new Rect(maxWidth / 2 - 100, baseAmount * 40f, 200, baseAmount * 7.5f), "New Highscore!", gs);
                    PlayerPrefs.SetInt("HighScore", ScoreCounter.Instance.TotalPoints);
                } else {
                    GUI.Label(new Rect(maxWidth / 2 - 100, baseAmount * 40f, 200, baseAmount * 7.5f), "Current Highscore:  " + PlayerPrefs.GetInt("HighScore"), gs);
                }
            } else {
                GUI.Label(new Rect(maxWidth / 2 - 100, baseAmount * 40f, 200, baseAmount * 7.5f), "You died!", gs);
            }

            gs.fontSize = 24;

            // This is the code for a button, just to show i know how its made, but i prefered the old school arcade version.

            //if (GUI.Button(new Rect(maxWidth / 2 - 200, baseAmount * 75, 400, baseAmount * 15), "PUSH TO RETURN TO MAIN MENU", gs)) {
            //  startGame();
            //}

            if (blink) {
                GUI.Label(new Rect(maxWidth / 2 - 200, baseAmount * 75, 400, baseAmount * 15), "PUSH SPACE TO RETURN TO MAIN MENU", gs);
                switchBlink();
            } else {
                switchBlink();
            }

            if (Input.GetKeyUp(KeyCode.Space)) {
                onStateChangedListener(GameState.GAME_INIT);
                Application.LoadLevel("Intro");
                gameOver = false;
            }         
        }
    }

    private void switchBlink() {
        if (!blinkStarted) {
            blinkStarted = true;
            StartCoroutine(BlinkButton());
        }
    }

    IEnumerator BlinkButton() {
        yield return new WaitForSeconds(0.5f);
        blinkStarted = false;
        blink = !blink;
    }

    public void StartGame() {
        Application.LoadLevel("Game");
        StartCoroutine(Delay());
    }

    IEnumerator Delay() {
        yield return new WaitForSeconds(0.05f);
        onStateChangedListener(GameState.LEVEL_INIT);
    }

    public void ScoreCounterReady() {
        onStateChangedListener(GameState.LEVEL_RUNNING);
    }

    public void endGame(bool win) { // This method is called when a dieing alien recognizes no other living aliens, or if the player dies.
        if (!gameOver) {
            gameOver = true;
            onStateChangedListener(GameState.GAME_OVER);
            this.win = win;
            if (win) {
                highScore = ScoreCounter.Instance.TotalPoints > PlayerPrefs.GetInt("HighScore");
            }
        }
    }

    public bool GameOver {
        get { return gameOver; }
    }

    public bool Win {
        get { return win; }
    }

    public float XMaxDistance {
        get { return xMaxDistance; }
    }

    public float XMinDistance {
        get { return xMinDistance; }
    }

    public float MaxHeight {
        get { return maxHeight; }
    }

    public float MaxWidth {
        get { return maxWidth; }
    }
}
