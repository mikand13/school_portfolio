using UnityEngine;
using System.Collections;

/**
 * This class defines the next level button on level cleared screen.
 * 
 * @author Group 9
 * 
 * */

public class NextSceneButton : LevelClearButton {
    override public void OnMouseDown() {
        if (!(Application.loadedLevelName == "Level3")) {
            GameManager.Instance.SetLevel("Level" + (int.Parse(Application.loadedLevelName.Substring(5, 1)) + 1));
        } else {
            GameManager.Instance.SetLevel("EndScene");        
        }
    }
}
