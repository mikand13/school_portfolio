using UnityEngine;
using System.Collections;

public class MainMenu : MonoBehaviour {
	public Texture backgroundTexture;
    private GameManager gameManager;

    void Awake() {
            gameManager  = (GameManager) (GameObject.Find("GameManager").GetComponent<GameManager>());
    }

	void OnGUI() {
		GUI.DrawTexture(new Rect(0, 0, Screen.width, Screen.height), backgroundTexture);
		if (GUI.Button(new Rect((Screen.width / 2) - 50, (Screen.height / 2) - 12, 150, 25), "Start Game")) {
            gameManager.StartNewGame();
		}

        if (GUI.Button(new Rect((Screen.width / 2) - 50, (Screen.height / 2) - 12 + 30, 150, 25), "Quit")) {
			Application.Quit();
		}
	}
}
