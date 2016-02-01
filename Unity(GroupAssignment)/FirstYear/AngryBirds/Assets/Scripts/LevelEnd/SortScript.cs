using UnityEngine;
using System.Collections;

/**
 * This class defines sorting for a few select Sprites.
 * 
 * @author Group 9
 * 
 * 
 * */

public class SortScript : MonoBehaviour {

	// Use this for initialization
	void Start () {
        renderer.sortingLayerName = "Default";
        renderer.sortingOrder = 4;
	}
}
