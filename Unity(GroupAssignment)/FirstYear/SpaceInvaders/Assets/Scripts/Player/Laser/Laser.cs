using UnityEngine;
using System.Collections;

/**
 * This is the parent class for all lasers.
 * 
 * @author Anders Mikkelsen
 * */
public class Laser : MonoBehaviour {
    private float moveSpeed;

    void Start() {
        moveSpeed = 2.0f;
        GameManager.onStateChangedListener += onStateChangedListener;
    }

    void onStateChangedListener(GameState gameState) {
        if (gameState == GameState.GAME_OVER) {
            GameManager.onStateChangedListener -= onStateChangedListener;
        }
    }

    public float MoveSpeed {
        get { return moveSpeed; }
    }

    void OnTriggerEnter2D(Collider2D collider) {
        DestroyObject(gameObject);
    }
}
