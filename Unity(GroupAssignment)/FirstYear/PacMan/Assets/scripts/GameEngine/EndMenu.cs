using UnityEngine;
using System.Collections;

public class EndMenu : MonoBehaviour {
    public Texture wonBackground;
    public Texture lostBackground;
<<<<<<< Updated upstream
    private GameManager gameManager;
    private bool isWon;

    void Awake() {
        gameManager = (GameManager)(GameObject.Find("GameManager").GetComponent<GameManager>());
    }

	void Start () {
=======
    public GameManager gameManager;

    private bool isWon;

	void Start ()
    {
>>>>>>> Stashed changes
        if (gameManager.Lives > 0) {
            isWon = true;
        } else {
            isWon = false;
        }
	}

    void OnGUI() {
        if (isWon) {
            DrawWonScreen();
        } else {
            DrawLostScreen();
        }

		GUI.Label (new Rect ((Screen.width / 2) - 50, (Screen.height / 2) - 82, 150, 25), "Your total score is: " + gameManager.TotalScore);

        if (GUI.Button(new Rect((Screen.width / 2) - 50, (Screen.height / 2) - 12, 150, 25), "Restart Game")) {
            Application.LoadLevel("mainMenu");
        } if (GUI.Button(new Rect((Screen.width / 2) - 50, (Screen.height / 2) - 12 + 30, 150, 25), "Quit")) {
            Application.Quit();
        }
    }

    private void DrawWonScreen() {
        GUI.DrawTexture(new Rect(0, 0, Screen.width, Screen.height), wonBackground);
    }

    private void DrawLostScreen() {
        GUI.DrawTexture(new Rect(0, 0, Screen.width, Screen.height), lostBackground);
    }
}
