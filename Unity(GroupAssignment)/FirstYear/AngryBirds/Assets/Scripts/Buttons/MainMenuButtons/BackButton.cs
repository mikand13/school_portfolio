using UnityEngine;
using System.Collections;

/**
 * This class defines the back button on main screen.
 * 
 * @author Group 9
 * 
 * */

public class BackButton : Button {
    public GameObject playButton;
    // Update is called once per frame
    override public void OnMouseOver() {
        _Trans.localScale = new Vector3(1.3f, 1.3f, 0);
        if (Input.GetButtonDown("Fire1")) {
            go.SetActive(false);
            playButton.SetActive(true);
            continueButton.SetActive(true);
            quitButton.SetActive(true);
            _Trans.localScale = new Vector3(1, 1, 0);
            gameObject.SetActive(false);
        }
    }
}
