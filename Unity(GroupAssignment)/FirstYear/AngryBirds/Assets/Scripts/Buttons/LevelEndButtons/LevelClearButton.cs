using UnityEngine;
using System.Collections;

/**
 * This class defines level clear button behaviour.
 * 
 * @author Group 9
 * 
 * */

public class LevelClearButton : Button {
    override public void OnMouseOver() {
        _Trans.localScale = new Vector3(1.2f, 1.2f, 1f);
    }

    override public void OnMouseExit() {
        _Trans.localScale = new Vector3(1f, 1f, 1f);
    }

    virtual public void OnMouseDown() {
    
    }
}
