using UnityEngine;
using System.Collections;

/**
 * This class is for killing particle systems of materials.
 * 
 * @author Group 9
 * 
 * 
 * */

public class KillParticleSystem : MonoBehaviour {
    void Awake () {
        StartCoroutine(DestroySquirt());
    }

    IEnumerator DestroySquirt() {
        yield return new WaitForSeconds(1);
        DestroyObject(gameObject);
    }
}
