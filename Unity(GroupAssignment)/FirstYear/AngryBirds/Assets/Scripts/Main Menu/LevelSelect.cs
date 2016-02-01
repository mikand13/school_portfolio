using UnityEngine;
using System.Collections;

/**
 * This class ensures Level Select Buttons are invisible on Start but accesible for others upon Awake.
 * 
 * @author Group 9
 * 
 * 
 * */

public class LevelSelect : MonoBehaviour {
    // Use this for initialization
    void Start() {
        GameObject.Find("LevelSelectIcons").SetActive(false);
    }
}
