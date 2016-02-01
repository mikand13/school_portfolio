using UnityEngine;
using System.Collections;

/**
 * This class defines the play button on main screen.
 * 
 * @author Group 9
 * 
 * 
 * */

public class PlayButton : Button {
	// Update is called once per frame
    override public void OnMouseOver() {
		_Trans.localScale = new Vector3(1.3f, 1.3f, 0);
        if (Input.GetButtonDown("Fire1")) {
            GameManager.Instance.ResetGame();
            go.SetActive(true);
            backButton.SetActive(true);
            continueButton.SetActive(false);
            quitButton.SetActive(false);
            _Trans.localScale = new Vector3(1, 1, 0);
            gameObject.SetActive(false);
        }
    }
}
