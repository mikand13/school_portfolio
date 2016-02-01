using UnityEngine;
using System.Collections;

public class FollowCam : MonoBehaviour {

    public long transitionTime;
    private Vector3 defaultPosition;
    private float time;
    private float tiltAroundY;

    private bool inPosition;

	void Start () {
        defaultPosition = transform.position;
        time = Time.time;
        inPosition = false;
	}

    public float TiltAroundY {
        get { return this.tiltAroundY; }
        set { this.tiltAroundY = value; }
    }

    public bool InPosition {
        get { return this.inPosition; }
        set { this.inPosition = value; }
    }

	void Update () {

        PacManMove pacMan = GameObject.Find("PacMan").GetComponent<PacManMove>();
        Vector3 position = pacMan.transform.position;
        float delta = (Time.time - time) * 0.75f;
        position.y += 1;
        transform.position = Vector3.Lerp(defaultPosition, position, delta);
        
        if (transform.position.Equals(position)) {
            inPosition = true;
            Quaternion.Euler(0, tiltAroundY, 0);
            Quaternion target = Quaternion.Euler(0, tiltAroundY, 0);
            transform.rotation = Quaternion.Lerp(transform.rotation, target, Time.deltaTime * tiltAroundY);
        }

        if (inPosition == true) {
         if (tiltAroundY <= 0) {
           tiltAroundY = 359;
         } else if (tiltAroundY > 359) {
            tiltAroundY = 0;
         }

         if (Input.GetButton("Horizontal")) {
            tiltAroundY += Input.GetAxis("Horizontal") * pacMan.MoveSpeed;
            Quaternion.Euler(0, tiltAroundY, 0);
            Quaternion target = Quaternion.Euler(0, tiltAroundY, 0);
            transform.rotation = Quaternion.Lerp(transform.rotation, target, Time.deltaTime * tiltAroundY);
         }
        }
    }
}
