using UnityEngine;
using System.Collections;

/**
 * This class defines the level cleared screen, and amount of stars to display and so on.
 * 
 * @author Group 9
 * 
 * 
 * */

public class LevelCleared : MonoBehaviour {
    public GameObject firstStar;
    public GameObject secondStar;
    public GameObject thirdStar;
    public GameObject points;
    public GameObject highScore;
    public GameObject counterObj;
    public Font angryFont;
    private int score;
    private int maxScore;
    private int pointCounter;

    public AudioClip pointSound;
    public AudioClip starSound;
    private AudioSource _Audio;
    private AudioSource _Counter;
    private bool firstSoundPlayed = false;
    private bool secondSoundPlayed = false;
    private bool thirdSoundPlayed = false;
    private bool highScorePosted = false;

    void Awake() {
        maxScore = GameManager.Instance.GetMaxScore();
        pointCounter = 0;
        score = GameManager.Instance.GetScore(Application.loadedLevelName);
        gameObject.AddComponent<AudioSource>();
        counterObj.AddComponent<AudioSource>();
        _Counter = GetComponent<AudioSource>();
        _Audio = GetComponent<AudioSource>();
    }

    void Update() {
        if (pointCounter < score) {
            StartCoroutine(CountPoints());
        }

        if (pointCounter >= maxScore * 0.2) {
            firstStar.SetActive(true);
            if (!firstSoundPlayed) {
                SpawnStarSquirt(firstStar);
                _Audio.PlayOneShot(starSound, 1);
                firstSoundPlayed = true;
            }
        }

        if (pointCounter >= maxScore * 0.4) {
            secondStar.SetActive(true);
            if (!secondSoundPlayed) {
                SpawnStarSquirt(secondStar);
                _Audio.PlayOneShot(starSound, 1);
                secondSoundPlayed = true;
            }
        }

        if (pointCounter >= maxScore * 0.7) {
            thirdStar.SetActive(true);
            if (!thirdSoundPlayed) {
                SpawnStarSquirt(thirdStar);
                _Audio.PlayOneShot(starSound, 1);
                thirdSoundPlayed = true;
            }
        }

        if (pointCounter >= score && _Counter.isPlaying) {
            _Counter.Stop();
        }

        if (pointCounter == score && highScorePosted == false) {
            highScorePosted = true;
            highScore.renderer.sortingLayerName = "Default";
            highScore.renderer.sortingOrder = 6;

            if (PlayerPrefs.GetInt(Application.loadedLevelName + "NewHighScore") != 0) {
                highScore.GetComponent<TextMesh>().text = "New HighScore!";
                PlayerPrefs.SetInt(Application.loadedLevelName + "NewHighScore", 0);
            } else {
                highScore.GetComponent<TextMesh>().text = "Current Highscore: " + PlayerPrefs.GetInt(Application.loadedLevelName + "HighScore").ToString();
            }
        }
    }

    private void SpawnStarSquirt(GameObject go) {
        Object o = Instantiate(Resources.Load("ParticleSystem/StarSystem"), new Vector3(go.transform.position.x, go.transform.position.y, -3) , new Quaternion(0, 0, 0, 0));
        StartCoroutine(KillSquirt(o));
    }

    IEnumerator KillSquirt(Object o) {
        yield return new WaitForSeconds(2);
        DestroyObject(o);
    }

    IEnumerator CountPoints() {
        if (!_Counter.isPlaying) {
            _Counter.clip = pointSound;
            _Counter.volume = 0.2f;
            _Counter.Play();
        }

        pointCounter += 5;
        //Debug.Log(pointCounter);
        points.renderer.sortingLayerName = "Default";
        points.renderer.sortingOrder = 6;
        points.GetComponent<TextMesh>().text = pointCounter.ToString();
        yield return new WaitForSeconds(1);
    }
}
