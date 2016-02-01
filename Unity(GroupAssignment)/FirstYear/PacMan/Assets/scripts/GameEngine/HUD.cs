using UnityEngine;
using System.Collections;

public class HUD : MonoBehaviour {
	public Texture lifeTexture;
	private Texture2D backgroundTexture;
    private GameManager gameManager;

	private const ushort HEADER_HEIGHT = 50;
	private const ushort FOOTER_HEIGHT = 40;

    void Awake() {
        gameManager = (GameManager)(GameObject.Find("GameManager").GetComponent<GameManager>());
    }

	void Start () {
		backgroundTexture = new Texture2D(1, 1);
		backgroundTexture.SetPixel(0, 0, Color.black);
		backgroundTexture.Apply();
	}

	void OnGUI() {
		// Draw header
		GUI.DrawTexture(new Rect(0, 0, Screen.width, HEADER_HEIGHT), backgroundTexture);
		GUI.skin.label.alignment = TextAnchor.UpperRight;
		GUI.Label(new Rect(10, 5, 50, 18), "SCORE:");
		GUI.Label(new Rect(10, 25, 50, 18), "" + gameManager.Score);
		GUI.Label(new Rect(100, 5, 100, 18), "TOTAL SCORE:");
		GUI.Label(new Rect(100, 25, 100, 18), "" + gameManager.TotalScore);

		// Draw footer
		GUI.DrawTexture(new Rect(0, Screen.height - FOOTER_HEIGHT, Screen.width, FOOTER_HEIGHT), backgroundTexture);
		int x = lifeTexture.width / 2;

		for (int i = 0; i < gameManager.Lives; i++) {
			GUI.DrawTexture(new Rect(x, Screen.height - (FOOTER_HEIGHT / 2) -  (lifeTexture.height / 2), lifeTexture.width, lifeTexture.height), lifeTexture);
			x += lifeTexture.width;
		}
	}
}
