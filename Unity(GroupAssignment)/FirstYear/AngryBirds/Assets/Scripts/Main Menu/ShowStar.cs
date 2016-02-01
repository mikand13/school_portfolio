using UnityEngine;
using System.Collections;

/**
 * This class defines generation of stars over level select buttons.
 * 
 * @author Group 9
 * 
 * 
 * */

public class ShowStar : MonoBehaviour {
    public GameObject star1;
    public GameObject star2;
    public GameObject star3;

    int highScore = 0;
    int maxScore = 0;

	// Use this for initialization
	void Update () {        
        highScore = PlayerPrefs.GetInt(gameObject.name + "HighScore");
        maxScore = PlayerPrefs.GetInt(gameObject.name + "MaxScore");

        if (highScore != 0 && maxScore != 0) {
            if (highScore > (maxScore * 0.2)) {
                star1.SetActive(true);
            }

            if (highScore > (maxScore * 0.4)) {
                star2.SetActive(true);
            }

            if (highScore > (maxScore * 0.7)) {
                star3.SetActive(true);
            }
        } else {
            star1.SetActive(false);
            star2.SetActive(false);
            star3.SetActive(false);
        }
	}
}
