using UnityEngine;
using System.Collections;

/**
 * This class defines the parralaxx behaviour of the second cam.
 * 
 * @author Group 9
 * 
 * 
 * */

public class ScrollScript : MonoBehaviour {
	public float parallaxSpeed;
	private float _offset;


	// Use this for initialization
	void Start () {
	}
	
	// Update is called once per frame
	void Update () {
        _offset = transform.position.x * parallaxSpeed;
        // Debug.Log("Offset: " + _offset);

        renderer.material.SetTextureOffset("_MainTex", new Vector2(_offset, 0f));
        // Debug.Log(renderer.material.GetTextureOffset("_MainTex"));
	}
}
