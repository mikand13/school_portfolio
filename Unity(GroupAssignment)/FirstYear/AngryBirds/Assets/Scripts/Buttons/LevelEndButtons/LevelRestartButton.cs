using UnityEngine;
using System.Collections;

/**
 * This class defines the level restart button on cleared screen.
 * 
 * @author Group 9
 * 
 * */

public class LevelRestartButton : LevelClearButton {
    override public void OnMouseDown() {
		GameManager.Instance.SetLevel(Application.loadedLevelName);
	}
}

