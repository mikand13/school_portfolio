using UnityEngine;
using System.Collections;

/**
 * This class defines the ammunition for the Shooter in a convienient way for the Game Designer in the Editor.
 * 
 * @author Group 9
 * 
 * 
 * */

public class ShooterAmmo : MonoBehaviour {
    public GameObject[] birds;

    public GameObject[] Birds {
        get { return birds; }
    }
}
