using UnityEngine;
using System.Collections;

/**
 * This class defines the quit button on main screen.
 * 
 * @author Group 9
 * 
 * 
 * */

public class QuitButton : Button {
    // Update is called once per frame
    override public void OnMouseOver() {
        _Trans.localScale = new Vector3(1.3f, 1.3f, 0);
        if (Input.GetButtonDown("Fire1")) {
            Application.Quit();
        }
    }
}
