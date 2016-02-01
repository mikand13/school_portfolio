using UnityEngine;
using System.Collections;

/**
 * This class defines superclass for buttons.
 * 
 * @author Group 9
 * 
 * */

public class Button : MonoBehaviour {
    public GameObject continueButton;
    public GameObject quitButton;
    public GameObject backButton;

    public GameObject go;
    public Transform _Trans;

    void Awake() {
        _Trans = gameObject.GetComponent<Transform>();
        go = GameObject.Find("LevelSelectIcons");
    }

    virtual public void Update() {
    
    }

    virtual public void OnMouseOver() {
    
    }

    virtual public void OnMouseExit() {
        _Trans.localScale = new Vector3(1, 1, 0);
    }
}
