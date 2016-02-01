using UnityEngine;
using System.Collections;

/**
 * This class defines super class for level cleared buttons.
 * 
 * @author Group 9
 * 
 * */

public class LevelButton : Button {
    bool availible;
	private Shader grayScaleShader;

    override public void Update() {
        availible = GameManager.Instance.GetLevelAvailible(int.Parse(gameObject.name.Substring(5, 1)));
        if (!availible) {
			grayScaleShader = Shader.Find ("Unlit/GreyScale");
            gameObject.GetComponentInChildren<SpriteRenderer>().material.shader = grayScaleShader;
        }
    }

    // Update is called once per frame
    override public void OnMouseOver() {
        if (availible) {
            _Trans.localScale = new Vector3(1.3f, 1.3f, 0);
            if (Input.GetButtonDown("Fire1")) {
                GameManager.Instance.SetLevel(gameObject.name);
            }
        }
	}

    override public void OnMouseExit() {
        if (availible) {
            _Trans.localScale = new Vector3(1, 1, 0);
        }
    }
}
