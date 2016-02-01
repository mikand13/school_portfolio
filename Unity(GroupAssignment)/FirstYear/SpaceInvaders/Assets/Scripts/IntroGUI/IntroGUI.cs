using UnityEngine;
using System.Collections;
/**
 * This class generates the GUI for the intro scene.
 * 
 * In general i think GUI is pretty shit to work with in Unity, at least this kind, but im doing it to show i understand how to use it.
 * I use maxHeight, maxWidth and baseAmount to scale the GUI.
 * 
 * @author Anders Mikkelsen
 * */
public class IntroGUI : MonoBehaviour {
    private GUIStyle gs;
    public Texture2D alien1;
    public Texture2D alien2;
    public Texture2D alien3;
    public Texture2D mothership;

    private float maxHeight;
    private float maxWidth;
    private float baseAmount;

    private bool gameStarted;
    private bool blink;
    private bool blinkStarted;

    void Start() {
        gs = GameManager.Instance.gs;
        maxHeight = GameManager.Instance.MaxHeight;
        maxWidth = GameManager.Instance.MaxWidth;
        baseAmount = maxHeight / 100;

        gameStarted = false;
        blink = true;
    }

    void OnGUI() {
        gs.fontSize = 140;
        GUI.Label(new Rect(maxWidth / 2 - 100, baseAmount * 12.5f, 200, baseAmount * 7.5f), "SPACE", gs);
        gs.fontSize = 64;
        GUI.Label(new Rect(maxWidth / 2 - 100, baseAmount * 25f, 200, baseAmount * 15), "INVADERS!", gs);

        if (PlayerPrefs.GetInt("HighScore") > 0) {
            gs.fontSize = 40;
            GUI.Label(new Rect(maxWidth / 2 - 100, baseAmount * 35, 200, baseAmount * 15), "Current Highscore: " + PlayerPrefs.GetInt("HighScore"), gs);
        } else {
            gs.fontSize = 40;
            GUI.Label(new Rect(maxWidth / 2 - 100, baseAmount * 35, 200, baseAmount * 15), "No Highscore registered!", gs);
        }

        GUI.DrawTexture(new Rect(maxWidth / 2 - 250, baseAmount * 51, 20, 20), alien3);
        GUI.Label(new Rect(maxWidth / 2 - 50, baseAmount * 45, 200, baseAmount * 15), "           =                 10", gs);
        GUI.DrawTexture(new Rect(maxWidth / 2 - 250, baseAmount * 58, 27, 20), alien2);
        GUI.Label(new Rect(maxWidth / 2 - 50, baseAmount * 52, 200, baseAmount * 15), "           =                20", gs);
        GUI.DrawTexture(new Rect(maxWidth / 2 - 250, baseAmount * 66, 30, 20), alien3);
        GUI.Label(new Rect(maxWidth / 2 - 50, baseAmount * 59, 200, baseAmount * 15), "           =                40", gs);
        GUI.DrawTexture(new Rect(maxWidth / 2 - 275, baseAmount * 72, 100, 33), mothership);
        GUI.Label(new Rect(maxWidth / 2 - 50, baseAmount * 66, 200, baseAmount * 15), "            =              ???", gs);
        gs.fontSize = 24;

        // This is the code for a button, just to show i know how its made, but i prefered the old school arcade version.

        //if (GUI.Button(new Rect(maxWidth / 2 - 200, baseAmount * 75, 400, baseAmount * 15), "PUSH TO START", gs)) {
          //  startGame();
        //}

        if (blink) {
            GUI.Label(new Rect(maxWidth / 2 - 200, baseAmount * 75, 400, baseAmount * 15), "PUSH SPACE TO START", gs);
            switchBlink();
        } else {
            switchBlink();
        }
            
        if (Input.GetKeyUp(KeyCode.Space)){
            startGame();
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

    private void startGame() {
        if (!gameStarted) {
            GameManager.Instance.StartGame();
            gameStarted = true;
        }
    }
}
