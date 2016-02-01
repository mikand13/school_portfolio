using UnityEngine;
using System.Collections;

/**
 * This class defines the button for main menu / level select on the level cleared screen.
 * 
 * @author Group 9
 * 
 * */

public class LevelSelectButton : LevelClearButton {
    override public void OnMouseDown() {		
		GameManager.Instance.SetLevel("StartScene");
	}
}

