using UnityEngine;
using System.Collections;

/**
 * This class defines the resume button on the pause screen.
 * 
 * @author Group 9
 * 
 * 
 * */

public class ResumeButton : Button {
    public GameObject pauseScreen;

    void OnMouseDown() {
        Time.timeScale = 1;
        DestroyObject(pauseScreen);
    }
}
